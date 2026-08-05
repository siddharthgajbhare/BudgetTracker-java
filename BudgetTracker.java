import java.util.ArrayList;

public class BudgetTracker {

    private ArrayList<Double> expenses;
    private ArrayList<Double> incomes;
    private double balance;

    public BudgetTracker() {
        expenses = new ArrayList<>();
        incomes = new ArrayList<>();
        balance = 0.0;
    }

    // Add Expense
    public void addExpense(double expense) {
        if (expense > 0) {
            expenses.add(expense);
            balance -= expense;
        } else {
            System.out.println("Expense must be greater than 0.");
        }
    }

    // Add Income
    public void addIncome(double income) {
        if (income > 0) {
            incomes.add(income);
            balance += income;
        } else {
            System.out.println("Income must be greater than 0.");
        }
    }

    // Get Current Balance
    public double getBalance() {
        return balance;
    }

    // Print All Expenses
    public void printExpenses() {
        System.out.println("\nExpenses:");
        if (expenses.isEmpty()) {
            System.out.println("No expenses recorded.");
        } else {
            for (double expense : expenses) {
                System.out.println("₹" + expense);
            }
        }
    }

    // Print All Incomes
    public void printIncomes() {
        System.out.println("\nIncomes:");
        if (incomes.isEmpty()) {
            System.out.println("No incomes recorded.");
        } else {
            for (double income : incomes) {
                System.out.println("₹" + income);
            }
        }
    }

    // Total Expenses
    public double getTotalExpenses() {
        double total = 0;
        for (double expense : expenses) {
            total += expense;
        }
        return total;
    }

    // Total Income
    public double getTotalIncome() {
        double total = 0;
        for (double income : incomes) {
            total += income;
        }
        return total;
    }
}