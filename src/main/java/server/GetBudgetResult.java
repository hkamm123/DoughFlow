package server;

import model.Budget;

public record GetBudgetResult(Budget budget, String message) {
}
