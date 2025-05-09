package model;

import java.util.ArrayList;
import java.util.Collection;

public class Budget {
    private String title;
    private final Collection<Category> categories;

    public Budget(String title) {
        this.title = title;
        this.categories = new ArrayList<>();
        String[] standardCategories = {
                "Groceries",
                "Eating Out",
                "Gas",
                "Desi Fun",
                "Hyrum Fun",
                "Phone Bill",
                "Car Insurance",
                "Rent",
                "Internet"
        };
        Double[] standardMaxAmounts = {
                350D,
                80D,
                120D,
                40D,
                40D,
                60D,
                170D,
                900D,
                35D
        };
        for (int i = 0; i < standardCategories.length; i++) {
            addCategory(new Category(standardCategories[i], standardMaxAmounts[i]));
        }
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addCategory(Category categoryToAdd) {
        for (Category cat : categories) {
            if (cat.getName().equals(categoryToAdd.getName())) {
                throw new RuntimeException("Cannot add a category with the same name as an existing one");
            }
        }
        categories.add(categoryToAdd);
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void addExpense(Expense e) {
        String categoryName = e.getCategory();
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                c.addExpense(e);
                return;
            }
        }
        Category newCategory = new Category(categoryName);
        newCategory.addExpense(e);
        addCategory(newCategory);
    }

    public void addIncome(Income i) {
        String categoryName = i.getCategory();
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                c.addIncome(i);
                return;
            }
        }
        Category newCategory = new Category(categoryName);
        newCategory.addIncome(i);
        addCategory(newCategory);
    }

    public void removeCategory(String categoryName) {
        Category categoryToRemove = null;
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                categoryToRemove = c;
            }
        }
        if (categoryToRemove != null) {
            categories.remove(categoryToRemove);
        }
    }

    public void removeExpense(String categoryName, int expenseIndex) {
        Category category = null;
        for (Category c : categories) {
            if (c.getName().equals(categoryName)) {
                category = c;
            }
        }
        if (category != null) {
            Expense expenseToRemove = new ArrayList<>(category.listExpenses()).get(expenseIndex);
            if (expenseToRemove != null) {
                category.removeExpense(expenseToRemove);
            }
        }
    }

    @Override
    public String toString() {
        return "Budget{" +
                "title='" + title + '\'' +
                ", categories=" + categories +
                '}';
    }
}
