import java.time.LocalDate;

public class Expense extends Transaction {
    private String expenseName;
    private String category;

    Expense(String name, double amount, String category, LocalDate date) {
        super(amount, date, name);
        this.expenseName = name;
        this.category = category;
    }
    public String getExpenseName() { return expenseName; }
    public String getCategory() { return category; }
    public void setExpenseName(String n) { this.expenseName = n; setDescription(n); }
    public void setCategory(String c) { this.category = c; }
    @Override public String getDetails() { return expenseName + " (" + category + ") - " + getAmount(); }
}