package model;

public class Expense extends BudgetItem {
    public Expense(double amount, String category, String description) {
        super(amount, category, description);
    }

    public String toString() {
        return "Expense: " + super.toString();
    }
}
