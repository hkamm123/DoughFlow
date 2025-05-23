package model;

public class BudgetItem {
    private double amount;
    private String category;
    private String description;

    public BudgetItem(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "{amount=" + amount +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
