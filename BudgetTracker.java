import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Core budget logic: no printing and no user input in here, so it is easy to
 * test and to reuse from a console, Swing or web front end.
 *
 * Money is stored as BigDecimal (never double) so totals are always exact.
 * The balance is calculated from the transactions, so it can never drift
 * out of sync with the lists.
 */
public class BudgetTracker {

    private static final BigDecimal WARNING_THRESHOLD = new BigDecimal("0.80");

    private final List<Transaction> transactions = new ArrayList<>();
    private final Map<Transaction.Category, BigDecimal> monthlyLimits =
            new EnumMap<>(Transaction.Category.class);
    private int nextId = 1;

    // ====================== ADD / REMOVE ======================

    public Transaction addIncome(BigDecimal amount, Transaction.Category category,
                                 String description, LocalDate date) {
        return add(Transaction.Type.INCOME, amount, category, description, date);
    }

    public Transaction addExpense(BigDecimal amount, Transaction.Category category,
                                  String description, LocalDate date) {
        return add(Transaction.Type.EXPENSE, amount, category, description, date);
    }

    /** Backwards-compatible shortcuts that match the original API. */
    public Transaction addIncome(double amount) {
        return addIncome(BigDecimal.valueOf(amount), Transaction.Category.SALARY, "", LocalDate.now());
    }

    public Transaction addExpense(double amount) {
        return addExpense(BigDecimal.valueOf(amount), Transaction.Category.OTHER, "", LocalDate.now());
    }

    private Transaction add(Transaction.Type type, BigDecimal amount, Transaction.Category category,
                            String description, LocalDate date) {
        // Throws IllegalArgumentException on bad data before nextId is consumed.
        Transaction t = new Transaction(nextId, type, category, amount, description, date);
        transactions.add(t);
        nextId++;
        return t;
    }

    public boolean removeTransaction(int id) {
        return transactions.removeIf(t -> t.id() == id);
    }

    // ====================== QUERIES ======================

    /** Read-only view of every transaction, oldest first. */
    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    /** Filter by type and/or month. Pass null for "any". Newest first. */
    public List<Transaction> getTransactions(Transaction.Type type, YearMonth month) {
        return transactions.stream()
                .filter(t -> type == null || t.type() == type)
                .filter(t -> month == null || YearMonth.from(t.date()).equals(month))
                .sorted((a, b) -> {
                    int byDate = b.date().compareTo(a.date());
                    return byDate != 0 ? byDate : Integer.compare(b.id(), a.id());
                })
                .toList();
    }

    public BigDecimal getTotal(Transaction.Type type, YearMonth month) {
        return getTransactions(type, month).stream()
                .map(Transaction::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalIncome() {
        return getTotal(Transaction.Type.INCOME, null);
    }

    public BigDecimal getTotalExpenses() {
        return getTotal(Transaction.Type.EXPENSE, null);
    }

    public BigDecimal getBalance() {
        return getTotalIncome().subtract(getTotalExpenses());
    }

    public BigDecimal getMonthlyBalance(YearMonth month) {
        return getTotal(Transaction.Type.INCOME, month)
                .subtract(getTotal(Transaction.Type.EXPENSE, month));
    }

    /** Percentage of income saved in the month, or empty if there was no income. */
    public Optional<BigDecimal> getSavingsRate(YearMonth month) {
        BigDecimal income = getTotal(Transaction.Type.INCOME, month);
        if (income.signum() == 0) {
            return Optional.empty();
        }
        return Optional.of(getMonthlyBalance(month)
                .multiply(BigDecimal.valueOf(100))
                .divide(income, 1, RoundingMode.HALF_UP));
    }

    /** Expenses per category for a month (null = all time), biggest first. */
    public Map<Transaction.Category, BigDecimal> getExpensesByCategory(YearMonth month) {
        Map<Transaction.Category, BigDecimal> totals = new EnumMap<>(Transaction.Category.class);
        for (Transaction t : getTransactions(Transaction.Type.EXPENSE, month)) {
            totals.merge(t.category(), t.amount(), BigDecimal::add);
        }
        Map<Transaction.Category, BigDecimal> sorted = new LinkedHashMap<>();
        totals.entrySet().stream()
                .sorted(Map.Entry.<Transaction.Category, BigDecimal>comparingByValue().reversed())
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));
        return sorted;
    }

    // ====================== BUDGET LIMITS ======================

    public void setMonthlyLimit(Transaction.Category category, BigDecimal limit) {
        if (limit == null || limit.signum() <= 0) {
            throw new IllegalArgumentException("Limit must be greater than 0.");
        }
        monthlyLimits.put(category, limit.setScale(2, RoundingMode.HALF_UP));
    }

    public void removeMonthlyLimit(Transaction.Category category) {
        monthlyLimits.remove(category);
    }

    public Map<Transaction.Category, BigDecimal> getMonthlyLimits() {
        return Collections.unmodifiableMap(monthlyLimits);
    }

    /** A human-readable warning if the category is near or over its monthly limit. */
    public Optional<String> getBudgetWarning(Transaction.Category category, YearMonth month) {
        BigDecimal limit = monthlyLimits.get(category);
        if (limit == null) {
            return Optional.empty();
        }
        BigDecimal spent = getExpensesByCategory(month).getOrDefault(category, BigDecimal.ZERO);
        if (spent.compareTo(limit) > 0) {
            return Optional.of("Over %s budget by %s (spent %s of %s)".formatted(
                    category.label(), formatMoney(spent.subtract(limit)),
                    formatMoney(spent), formatMoney(limit)));
        }
        if (spent.compareTo(limit.multiply(WARNING_THRESHOLD)) >= 0) {
            return Optional.of("%s budget is %s%% used (%s of %s)".formatted(
                    category.label(),
                    spent.multiply(BigDecimal.valueOf(100)).divide(limit, 0, RoundingMode.HALF_UP),
                    formatMoney(spent), formatMoney(limit)));
        }
        return Optional.empty();
    }

    // ====================== SAVE / LOAD (CSV-style text) ======================
    //   T,id,TYPE,CATEGORY,amount,yyyy-MM-dd,description
    //   L,CATEGORY,limit

    public void save(Path file) throws IOException {
        List<String> lines = new ArrayList<>();
        for (Transaction t : transactions) {
            lines.add("T,%d,%s,%s,%s,%s,%s".formatted(t.id(), t.type(), t.category(),
                    t.amount().toPlainString(), t.date(), t.description()));
        }
        monthlyLimits.forEach((c, v) -> lines.add("L,%s,%s".formatted(c, v.toPlainString())));
        Files.write(file, lines, StandardCharsets.UTF_8);
    }

    /** Replaces the current data with the file's contents. Returns the transaction count. */
    public int load(Path file) throws IOException {
        List<Transaction> loaded = new ArrayList<>();
        Map<Transaction.Category, BigDecimal> limits = new EnumMap<>(Transaction.Category.class);
        int maxId = 0;
        int lineNo = 0;

        for (String line : Files.readAllLines(file, StandardCharsets.UTF_8)) {
            lineNo++;
            if (line.isBlank()) {
                continue;
            }
            try {
                String[] p = line.split(",", 7);   // description is last, so it may contain commas
                switch (p[0]) {
                    case "T" -> {
                        int id = Integer.parseInt(p[1]);
                        loaded.add(new Transaction(id,
                                Transaction.Type.valueOf(p[2]),
                                Transaction.Category.valueOf(p[3]),
                                new BigDecimal(p[4]),
                                p.length > 6 ? p[6] : "",
                                LocalDate.parse(p[5])));
                        maxId = Math.max(maxId, id);
                    }
                    case "L" -> limits.put(Transaction.Category.valueOf(p[1]), new BigDecimal(p[2]));
                    default -> throw new IllegalArgumentException("Unknown record type '" + p[0] + "'");
                }
            } catch (RuntimeException e) {
                throw new IOException("Bad data on line " + lineNo + ": " + e.getMessage(), e);
            }
        }

        transactions.clear();
        transactions.addAll(loaded);
        monthlyLimits.clear();
        monthlyLimits.putAll(limits);
        nextId = maxId + 1;
        return loaded.size();
    }

    // ====================== FORMATTING ======================

    public static String formatMoney(BigDecimal amount) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")).format(amount);
    }
}
