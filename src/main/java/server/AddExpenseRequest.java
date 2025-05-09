package server;

public record AddExpenseRequest(String budgetTitle, String categoryName, String description, double amount) {
}
