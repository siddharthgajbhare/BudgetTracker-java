//done
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

/**
 * One income or expense entry. Immutable, and validated on creation.
 */
public record Transaction(
        int id,
        Type type,
        Category category,
        BigDecimal amount,
        String description,
        LocalDate date) {

    public enum Type { INCOME, EXPENSE }

    public enum Category {
        SALARY("Salary"),
        FOOD("Food"),
        TRANSPORT("Transport"),
        RENT("Rent"),
        BILLS("Bills"),
        SHOPPING("Shopping"),
        HEALTH("Health"),
        ENTERTAINMENT("Entertainment"),
        EDUCATION("Education"),
        OTHER("Other");

        private final String label;

        Category(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    public Transaction {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(amount, "amount");
        Objects.requireNonNull(date, "date");

        amount = amount.setScale(2, RoundingMode.HALF_UP);
        if (amount.signum() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0.");
        }
        description = (description == null) ? "" : description.replaceAll("[\\r\\n]+", " ").trim();
    }
}
