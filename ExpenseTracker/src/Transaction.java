import java.time.LocalDate;

public abstract class Transaction {
    private static int counter = 1;
    private int transactionId;
    private double amount;
    private LocalDate date;
    private String description;

    Transaction(double amount, LocalDate date, String description) {
        this.transactionId = counter++;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }
    public int getId() { return transactionId; }
    public double getAmount() { return amount; }
    public LocalDate getDate() { return date; }
    public String getDescription() { return description; }
    public void setAmount(double a) { this.amount = a; }
    public void setDate(LocalDate d) { this.date = d; }
    public void setDescription(String d) { this.description = d; }
    public abstract String getDetails();
}