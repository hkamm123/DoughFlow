package server.dataaccess;

import com.google.gson.Gson;
import model.Budget;
import model.Category;
import model.Expense;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import static server.dataaccess.DatabaseManager.*;

public class MySQLBudgetDao {
    private Gson gson;

    public MySQLBudgetDao() throws DataAccessException {
        configureDatabase();
        gson = new Gson();
    }

    public Collection<Budget> listBudgets() throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "SELECT (budgetJson) FROM budgets";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var resultSet = preparedStatement.executeQuery();
                Collection<Budget> budgets = new ArrayList<>();
                while (resultSet.next()) {
                    Budget nextReceivedBudget = gson.fromJson(resultSet.getString("budgetJson"), Budget.class);
                    budgets.add(nextReceivedBudget);
                }
                return budgets;
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void addBudget(Budget budget) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "INSERT INTO budgets (budgetTitle, budgetJson) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, budget.getTitle());
                preparedStatement.setString(2, gson.toJson(budget));
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public Budget getBudget(String title) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "SELECT * FROM budgets WHERE budgetTitle = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, title);
                var resultSet = preparedStatement.executeQuery();
                resultSet.next();
                return gson.fromJson(resultSet.getString("budgetJson"), Budget.class);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public Category addCategory(String budgetTitle, String name, Double max) throws DataAccessException {
        Budget budget = getBudget(budgetTitle);
        Category cat = new Category(name, max);
        budget.addCategory(cat);
        updateBudget(budget);
        return cat;
    }

    public void updateCategory(String budgetTitle, String oldName, String newName, double newMax) throws DataAccessException {
        Budget budget = getBudget(budgetTitle);
        for (Category c : budget.getCategories()) {
            if (c.getName().equals(oldName)) {
                c.setName(newName);
                c.setMax(newMax);
            }
        }
        updateBudget(budget);
    }

    public void addExpense(String budgetTitle, String categoryName, String description, double amount) throws DataAccessException {
        Budget budget = getBudget(budgetTitle);
        budget.addExpense(new Expense(amount, categoryName, description));
        updateBudget(budget);
    }

    private void updateBudget(Budget newBudget) throws DataAccessException {
        try (Connection conn = getConnection()) {
            var statement = "UPDATE budgets SET budgetJson = ? WHERE budgetTitle = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, gson.toJson(newBudget));
                preparedStatement.setString(2, newBudget.getTitle());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void deleteCategory(String title, String categoryName) throws DataAccessException {
        Budget budget = getBudget(title);
        budget.removeCategory(categoryName);
        updateBudget(budget);
    }

    public void deleteExpense(String title, String categoryName, int expenseIndex) throws DataAccessException {
        Budget budget = getBudget(title);
        budget.removeExpense(categoryName, expenseIndex);
        updateBudget(budget);
    }

    public void deleteBudget(String title) throws DataAccessException {
        try (var conn = getConnection()) {
            var statement = "DELETE FROM budgets WHERE budgetTitle = ?";
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1, title);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }
}
