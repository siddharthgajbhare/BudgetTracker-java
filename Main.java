//done
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;

/** Console menu for BudgetTracker. All input validation lives here. */
public class Main {

    private static final Path DATA_FILE = Path.of("budget_data.csv");
    private static final Scanner in = new Scanner(System.in);
    private static final BudgetTracker tracker = new BudgetTracker();

    public static void main(String[] args) {
        System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8));

        System.out.println("=== PERSONAL BUDGET TRACKER ===");
        loadData();

        try {
            boolean running = true;
            while (running) {
                printMenu();
                switch (prompt("Choose an option: ")) {
                    case "1" -> addTransaction(Transaction.Type.INCOME);
                    case "2" -> addTransaction(Transaction.Type.EXPENSE);
                    case "3" -> viewTransactions();
                    case "4" -> showSummary();
                    case "5" -> showCategoryBreakdown();
                    case "6" -> setBudgetLimit();
                    case "7" -> deleteTransaction();
                    case "8" -> saveData();
                    case "0" -> running = false;
                    default -> System.out.println("Invalid option, please try again.");
                }
            }
        } catch (NoSuchElementException endOfInput) {
            System.out.println();   // Ctrl+D / closed input
        }

        saveData();
        System.out.println("Goodbye!");
    }

    // ====================== MENU ACTIONS ======================

    private static void printMenu() {
        System.out.println("""

                ---------------- MENU ----------------
                 1. Add income        5. Spending by category
                 2. Add expense       6. Set monthly budget limit
                 3. View transactions 7. Delete a transaction
                 4. Summary           8. Save now
                 0. Save & exit
                --------------------------------------""");
    }

    private static void addTransaction(Transaction.Type type) {
        boolean isIncome = type == Transaction.Type.INCOME;
        BigDecimal amount = readAmount();
        Transaction.Category category = readCategory();
        String description = prompt("Description (optional): ");
        LocalDate date = readDate();

        try {
            Transaction t = isIncome
                    ? tracker.addIncome(amount, category, description, date)
                    : tracker.addExpense(amount, category, description, date);
            System.out.printf("Added %s of %s (ID %d). Balance: %s%n",
                    isIncome ? "income" : "expense",
                    BudgetTracker.formatMoney(t.amount()), t.id(),
                    BudgetTracker.formatMoney(tracker.getBalance()));

            if (!isIncome) {
                tracker.getBudgetWarning(category, YearMonth.from(date))
                        .ifPresent(w -> System.out.println("WARNING: " + w));
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void viewTransactions() {
        System.out.println("1. All   2. Income only   3. Expenses only");
        Transaction.Type type = switch (prompt("Show (blank = all): ")) {
            case "2" -> Transaction.Type.INCOME;
            case "3" -> Transaction.Type.EXPENSE;
            default -> null;
        };
        YearMonth month = readMonth("Month yyyy-MM (blank = all time): ", false);

        List<Transaction> list = tracker.getTransactions(type, month);
        if (list.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }
        System.out.printf("%n%-4s %-11s %-8s %-14s %15s  %s%n",
                "ID", "Date", "Type", "Category", "Amount", "Description");
        System.out.println("-".repeat(75));
        for (Transaction t : list) {
            System.out.printf("%-4d %-11s %-8s %-14s %15s  %s%n",
                    t.id(), t.date(), t.type(), t.category().label(),
                    BudgetTracker.formatMoney(t.amount()), t.description());
        }
        System.out.println("-".repeat(75));
        System.out.printf("%d transaction(s)%n", list.size());
    }

    private static void showSummary() {
        YearMonth thisMonth = YearMonth.now();
        System.out.println("\n----- ALL TIME -----");
        System.out.println("Total income   : " + BudgetTracker.formatMoney(tracker.getTotalIncome()));
        System.out.println("Total expenses : " + BudgetTracker.formatMoney(tracker.getTotalExpenses()));
        System.out.println("Balance        : " + BudgetTracker.formatMoney(tracker.getBalance()));

        System.out.println("\n----- THIS MONTH (" + thisMonth + ") -----");
        System.out.println("Income         : " + BudgetTracker.formatMoney(
                tracker.getTotal(Transaction.Type.INCOME, thisMonth)));
        System.out.println("Expenses       : " + BudgetTracker.formatMoney(
                tracker.getTotal(Transaction.Type.EXPENSE, thisMonth)));
        System.out.println("Net savings    : " + BudgetTracker.formatMoney(
                tracker.getMonthlyBalance(thisMonth)));
        Optional<BigDecimal> rate = tracker.getSavingsRate(thisMonth);
        System.out.println("Savings rate   : " + rate.map(r -> r + "%").orElse("n/a (no income this month)"));

        if (tracker.getBalance().signum() < 0) {
            System.out.println("\nWARNING: your overall balance is negative.");
        }
    }

    private static void showCategoryBreakdown() {
        YearMonth month = readMonth("Month yyyy-MM (blank = this month): ", true);
        Map<Transaction.Category, BigDecimal> breakdown = tracker.getExpensesByCategory(month);
        if (breakdown.isEmpty()) {
            System.out.println("No expenses recorded for " + month + ".");
            return;
        }
        BigDecimal total = breakdown.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        System.out.println("\nSpending for " + month);
        System.out.println("-".repeat(70));
        breakdown.forEach((category, amount) -> {
            BigDecimal pct = amount.multiply(BigDecimal.valueOf(100))
                    .divide(total, 1, RoundingMode.HALF_UP);
            String bar = "#".repeat(Math.max(1, pct.intValue() / 5));
            String flag = tracker.getBudgetWarning(category, month).isPresent() ? "  <-- check budget" : "";
            System.out.printf("%-14s %15s %5s%%  %s%s%n", category.label(),
                    BudgetTracker.formatMoney(amount), pct, bar, flag);
        });
        System.out.println("-".repeat(70));
        System.out.printf("%-14s %15s%n", "TOTAL", BudgetTracker.formatMoney(total));
    }

    private static void setBudgetLimit() {
        if (!tracker.getMonthlyLimits().isEmpty()) {
            System.out.println("Current limits:");
            tracker.getMonthlyLimits().forEach((c, v) ->
                    System.out.println("  " + c.label() + ": " + BudgetTracker.formatMoney(v)));
        }
        Transaction.Category category = readCategory();
        String raw = prompt("Monthly limit (₹), or 0 to remove: ").replace(",", "");
        try {
            BigDecimal limit = new BigDecimal(raw);
            if (limit.signum() == 0) {
                tracker.removeMonthlyLimit(category);
                System.out.println("Limit removed for " + category.label() + ".");
            } else {
                tracker.setMonthlyLimit(category, limit);
                System.out.println("Limit set for " + category.label() + ".");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid positive number.");
        }
    }

    private static void deleteTransaction() {
        String raw = prompt("Transaction ID to delete: ");
        try {
            int id = Integer.parseInt(raw);
            if (!prompt("Delete transaction " + id + "? (y/n): ").equalsIgnoreCase("y")) {
                System.out.println("Cancelled.");
                return;
            }
            System.out.println(tracker.removeTransaction(id) ? "Deleted." : "No transaction with that ID.");
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid ID number.");
        }
    }

    // ====================== FILE ======================

    private static void loadData() {
        if (!Files.exists(DATA_FILE)) {
            return;
        }
        try {
            int count = tracker.load(DATA_FILE);
            System.out.println("Loaded " + count + " transaction(s) from " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Could not load saved data: " + e.getMessage());
        }
    }

    private static void saveData() {
        try {
            tracker.save(DATA_FILE);
            System.out.println("Data saved to " + DATA_FILE + ".");
        } catch (IOException e) {
            System.out.println("Could not save data: " + e.getMessage());
        }
    }

    // ====================== INPUT HELPERS ======================

    private static String prompt(String message) {
        System.out.print(message);
        return in.nextLine().trim();
    }

    private static BigDecimal readAmount() {
        while (true) {
            String raw = prompt("Amount (₹): ").replace(",", "");
            try {
                BigDecimal value = new BigDecimal(raw);
                if (value.signum() > 0) {
                    return value;
                }
                System.out.println("Amount must be greater than 0.");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static Transaction.Category readCategory() {
        Transaction.Category[] all = Transaction.Category.values();
        for (int i = 0; i < all.length; i++) {
            System.out.printf("  %2d. %s%n", i + 1, all[i].label());
        }
        while (true) {
            String raw = prompt("Category number (blank = Other): ");
            if (raw.isEmpty()) {
                return Transaction.Category.OTHER;
            }
            try {
                int n = Integer.parseInt(raw);
                if (n >= 1 && n <= all.length) {
                    return all[n - 1];
                }
            } catch (NumberFormatException ignored) {
                // fall through to the message below
            }
            System.out.println("Enter a number from 1 to " + all.length + ".");
        }
    }

    private static LocalDate readDate() {
        while (true) {
            String raw = prompt("Date yyyy-MM-dd (blank = today): ");
            if (raw.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Use the format yyyy-MM-dd, e.g. 2026-10-06.");
            }
        }
    }

    /** Blank returns the current month if defaultToNow, otherwise null (meaning "all time"). */
    private static YearMonth readMonth(String message, boolean defaultToNow) {
        while (true) {
            String raw = prompt(message);
            if (raw.isEmpty()) {
                return defaultToNow ? YearMonth.now() : null;
            }
            try {
                return YearMonth.parse(raw);
            } catch (DateTimeParseException e) {
                System.out.println("Use the format yyyy-MM, e.g. 2026-10.");
            }
        }
    }
}
