package server;

import model.Category;

public record AddCategoryResult(Category category, String message) {
}
