package server;

import model.Budget;
import model.Category;
import server.dataaccess.DataAccessException;
import server.dataaccess.MySQLBudgetDao;

import java.util.Collection;

public class BudgetService {
    private final MySQLBudgetDao budgetDao = new MySQLBudgetDao();

    public BudgetService() throws DataAccessException {
    }

    public Collection<Budget> listBudgets() throws DataAccessException {
        return budgetDao.listBudgets();
    }

    public AddResult addBudget(AddBudgetRequest addReq) {
        Budget requestedBudget = new Budget(addReq.budgetTitle());
        try {
            Budget budgetWithSameTitle = getBudget(requestedBudget.getTitle()).budget();
            if (budgetWithSameTitle != null) {
                return new AddResult("Budget with same name already exists!");
            }
            budgetDao.addBudget(requestedBudget);
            return new AddResult(null);
        } catch (DataAccessException ex) {
            return new AddResult(ex.getMessage());
        }
    }

    public GetBudgetResult getBudget(String title) {
        try {
            return new GetBudgetResult(budgetDao.getBudget(title), null);
        } catch (DataAccessException ex) {
            return new GetBudgetResult(null, ex.getMessage());
        }
    }

    public AddCategoryResult addCategory(String budgetTitle, AddCategoryRequest addReq) {
        try {
            Category newCat = budgetDao.addCategory(budgetTitle, addReq.name(), addReq.max());
            return new AddCategoryResult(newCat, null);
        } catch (DataAccessException ex) {
            return new AddCategoryResult(null, ex.getMessage());
        }
    }

    public UpdateCategoryResult updateCategory(UpdateCategoryRequest req) {
        try {
            budgetDao.updateCategory(req.budgetTitle(), req.oldName(), req.newName(), req.newMax());
            return new UpdateCategoryResult(null);
        } catch (DataAccessException ex) {
            return new UpdateCategoryResult("Error: " + ex.getMessage());
        }
    }

    public AddResult addExpense(AddExpenseRequest req) {
        try {
            budgetDao.addExpense(req.budgetTitle(), req.categoryName(), req.description(), req.amount());
            return new AddResult(null);
        } catch (DataAccessException ex) {
            return new AddResult("Error: " + ex.getMessage());
        }
    }

    public DeleteResult deleteCategory(String title, String categoryName) {
        try {
            budgetDao.deleteCategory(title, categoryName);
            return new DeleteResult(null);
        } catch (DataAccessException ex) {
            return new DeleteResult(ex.getMessage());
        }
    }

    public DeleteResult deleteExpense(String title, String categoryName, int expenseIndex) {
        try {
            budgetDao.deleteExpense(title, categoryName, expenseIndex);
            return new DeleteResult(null);
        } catch (DataAccessException ex) {
            return new DeleteResult(ex.getMessage());
        }
    }

    public DeleteResult deleteBudget(String title) {
        try {
            budgetDao.deleteBudget(title);
            return new DeleteResult(null);
        } catch (DataAccessException ex) {
            return new DeleteResult(ex.getMessage());
        }
    }
}
