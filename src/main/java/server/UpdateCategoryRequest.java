package server;

public record UpdateCategoryRequest(String budgetTitle, String oldName, String newName, Double newMax) {
}
