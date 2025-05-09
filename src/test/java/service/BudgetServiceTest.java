package service;

import model.Budget;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;

public class BudgetServiceTest {

    @Test
    public void listBudgetsTest() {
        Collection<Budget> expectedBudgets = new ArrayList<Budget>();
        expectedBudgets.add(new Budget("Feb 2025"));


    }
}
