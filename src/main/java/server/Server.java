package server;

import com.google.gson.Gson;
import model.Budget;
import model.Category;
import org.eclipse.jetty.websocket.api.RemoteEndpoint;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.FileTemplateResolver;
import org.thymeleaf.context.Context;
import server.dataaccess.DataAccessException;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Server {
    private final Gson gson = new Gson();
    private final BudgetService budgetService = new BudgetService();
    private final TemplateEngine templateEngine = new TemplateEngine();

    public Server() throws DataAccessException {
    }

    /**
     * Runs the Spark server on the given port.
     * @param desiredPort: port on which to run the server
     * @return the port on which the server is running
     */
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("/public");

        // Set up Thymeleaf Template Engine
        FileTemplateResolver templateResolver = new FileTemplateResolver();
        templateResolver.setPrefix("src/main/resources/templates/");
        templateResolver.setSuffix(".html");
        templateEngine.setTemplateResolver(templateResolver);

        Spark.post("/api/budgets", this::addBudget);

        Spark.get("/budgets", this::listBudgets);
        Spark.get("/budget/:title", this::getBudgetDetails);
        Spark.get("/budget/:title/delete", this::deleteBudget);
        Spark.post("/budgets", this::addBudget);
        Spark.post("/budget/:title/categories", this::addCategory);
        Spark.get("/budget/:title/categories/:name/edit", this::showCategoryDetails);
        Spark.post("/budget/:title/categories/:name/edit", this::updateCategory);
        Spark.post("/budget/:title/expenses/add", this::addExpense);
        Spark.get("/budget/:title/categories/:name/edit/delete", this::deleteCategory);
        Spark.get("/budget/:title/categories/:name/expenses/:index/delete", this::deleteExpense);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object listBudgets(Request request, Response response) {
        try {
            Collection<Budget> budgets = budgetService.listBudgets();
            Collections.reverse((List<?>) budgets);
            Context context = new Context();
            context.setVariable("budgets", budgets);

            List<String> budgetLinks = budgets.stream()
                    .map(budget -> "/budget/" + budget.getTitle())
                    .collect(Collectors.toList());
            context.setVariable("budgetLinks", budgetLinks);

            return templateEngine.process("budgets", context);
        } catch (DataAccessException ex) {
            return "Error loading budgets" + ex.getMessage();
        }
    }

    private Object deleteBudget(Request request, Response response) {
        String title = request.params(":title");
        DeleteResult result = budgetService.deleteBudget(title);
        if (result.message() != null) {
            response.status(400);
            return result.message();
        }
        response.redirect("/budgets");
        return "";
    }

    private Object getBudgetDetails(Request request, Response response) {
        String title = request.params("title");
        GetBudgetResult result = budgetService.getBudget(title);

        if (result.message() == null) {
            Context context = new Context();
            String addCategoryLink = "/budget/" + title + "/categories";

            List<String> editCategoryLinks = result.budget().getCategories().stream()
                    .map(category ->
                            "/budget/" + result.budget().getTitle() + "/categories/" + category.getName() + "/edit")
                    .toList();
            context.setVariable("editCategoryLinks", editCategoryLinks);

            context.setVariable("addCategoryLink", addCategoryLink);
            Budget budget = result.budget();
            context.setVariable("budget", budget);
            String addExpenseLink = "/budget/" + title + "/expenses/add";
            context.setVariable("addExpenseLink", addExpenseLink);
            return templateEngine.process("budget", context);
        }
        return "Error loading budget: " + result.message();
    }

    private Object deleteCategory(Request request, Response response) {
        String title = request.params("title");
        String categoryName = request.params("name");
        DeleteResult result = budgetService.deleteCategory(title, categoryName);
        if (result.message() != null) {
            response.status(400);
            return result.message();
        }
        response.redirect("/budget/" + title);
        return "";
    }

    private Object deleteExpense(Request request, Response response) {
        String title = request.params("title");
        String categoryName = request.params("name");
        String expenseIndexStr = request.params("index");
        int expenseIndex = Integer.parseInt(expenseIndexStr);
        DeleteResult result = budgetService.deleteExpense(title, categoryName, expenseIndex);
        if (result.message() != null) {
            response.status(400);
            return result.message();
        }
        response.redirect("/budget/" + title);
        return "";
    }

    private Object addExpense(Request request, Response response) {
        String title = request.params("title");
        // get the category and expense info
        String categoryName = request.queryParams("categoryName");
        String description = request.queryParams("description");
        String amountStr = request.queryParams("amount");
        if (categoryName == null || description == null || amountStr == null) {
            response.status(400);
            return "Missing input from one or more fields";
        }
        double amount = Double.parseDouble(amountStr);
        // update the category in the db
        AddResult result = budgetService.addExpense(new AddExpenseRequest(title, categoryName, description, amount));
        // redirect to budget details page
        if (result.message() != null) {
            response.status(400);
            return result.message();
        }
        response.redirect("/budget/" + title);
        return "";
    }

    private Object addBudget(Request request, Response response) {
        String budgetTitle = request.queryParams("budgetTitle");
        AddBudgetRequest addReq = new AddBudgetRequest(budgetTitle);

        AddResult result = budgetService.addBudget(addReq);
        if (result.message() != null) {
            response.status(400);
            return "Error: " + result.message();
        }
        response.redirect("/budgets");
        return "";
    }

    private Object addCategory(Request request, Response response) {
        String title = request.params("title");
        String name = request.queryParams("name");
        String max = request.queryParams("max");
        AddCategoryRequest addReq = new AddCategoryRequest(name, Double.parseDouble(max));
        AddCategoryResult result = budgetService.addCategory(title, addReq);

        if (result.message() != null) {
            return "Error adding cateogory: " + result.message();
        }
        response.redirect("/budget/" + title);
        return "";
    }

    private Object showCategoryDetails(Request request, Response response) {
        String title = request.params("title");
        String name = request.params("name");

        Context context = new Context();
        context.setVariable("budgetTitle", title);
        String budgetLink = "/budget/" + title;
        context.setVariable("budgetLink", budgetLink);
        Budget budget = budgetService.getBudget(title).budget();
        Category category = null;
        for (Category c : budget.getCategories()) {
            if (c.getName().equals(name)) {
                category = c;
            }
        }
        if (category == null) {
            return "Error: cannot find category.";
        }
        context.setVariable("category", category);
        String editCategoryLink = "/budget/" + title + "/categories/" + name + "/edit";
        context.setVariable("editCategoryLink", editCategoryLink);

        return templateEngine.process("edit-category", context);
    }

    private Object updateCategory(Request request, Response response) {
        String title = request.params("title");
        String oldName = request.params("name");

        String newName = request.queryParams("name");
        String newMaxStr = request.queryParams("max");

        if (newName == null || newMaxStr == null) {
            response.status(400);
            return "Missing input from name or max field";
        }

        double max = Double.parseDouble(newMaxStr);
        UpdateCategoryResult result = budgetService.updateCategory(new UpdateCategoryRequest(title, oldName, newName, max));
        if (result.message() != null) {
            response.status(400);
            return result.message();
        }
        response.redirect("/budget/" + title);
        return "";
    }
}
