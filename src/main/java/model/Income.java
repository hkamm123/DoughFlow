package model;

public class Income extends BudgetItem {
    public Income(double amount, String category, String description) {
        super(amount, category, description);
    }

    public String toString() {
        return "Income: " + super.toString();
    }
}
