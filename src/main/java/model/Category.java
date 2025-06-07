package model;

import java.util.ArrayList;
import java.util.Collection;

public class Category {
    private String name;
    private double max;
    private double balance;
    private double spent = 0;
    private double received = 0;
    private final Collection<Expense> expenses;
    private final Collection<Income> income;

    public double getPercentRemaining() {
        return percentRemaining;
    }

    private double percentRemaining;

    public Category(String name) {
        this.name = name;
        max = 0;
        balance = max;
        expenses = new ArrayList<>();
        income = new ArrayList<>();
        percentRemaining = 1;
    }

    public Category(String name, double max) {
        this.name = name;
        this.max = max;
        balance = max;
        expenses = new ArrayList<>();
        income = new ArrayList<>();
        percentRemaining = 1;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
        setBalance();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addIncome(Income i) {
        income.add(i);
        received += i.getAmount();
        setBalance();
    }

    public Collection<Income> listIncome() {
        return income;
    }

    public void addExpense(Expense e) {
        expenses.add(e);
        spent += e.getAmount();
        setBalance();
    }

    public Collection<Expense> listExpenses() {
        return expenses;
    }

    private void setBalance() {
        balance = round(max + received - spent);
        if (max > 0) {
            percentRemaining = balance / max;
        } else {
            percentRemaining = 0;
        }
        if (percentRemaining < 0) {
            percentRemaining = 0;
        }
    }

    private double round(double value) {
        return Double.parseDouble(String.format("%.2f", value));
    }

    public double getBalance() {
        return balance;
    }

    public void removeExpense(Expense expenseToRemove) {
        expenses.remove(expenseToRemove);
        spent -= expenseToRemove.getAmount();
        setBalance();
    }

    @Override
    public String toString() {
        return "Category{" +
                "name='" + name + '\'' +
                ", max=" + max +
                ", balance=" + balance +
                ", expenses=" + expenses +
                ", income=" + income +
                '}';
    }
}
