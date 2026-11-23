import java.time.LocalDate;

public class Income extends Transaction {
    private String source;

    Income(String source, double amount, LocalDate date) {
        super(amount, date, source);
        this.source = source;
    }
    public String getSource() { return source; }

    public void addIncome(User user) { user.getTransactions().add(this); }

    public void editIncome(String source, double amount, LocalDate date) {
        this.source = source;
        setDescription(source);
        setAmount(amount);
        setDate(date);
    }

    @Override public String getDetails() { return source + " - " + getAmount(); }
}