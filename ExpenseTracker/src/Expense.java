import java.time.LocalDate;

public class Expense extends Transaction {
    private String expenseName;
    private Category category;

    Expense(String name, double amount, Category category, LocalDate date) {
        super(amount, date, name);
        this.expenseName = name;
        this.category = category;
    }
    public String getExpenseName() { return expenseName; }
    public Category getCategory() { return category; }

    public void addExpense(User user) { user.getTransactions().add(this); }

    public void editExpense(String name, double amount, Category category, LocalDate date) {
        this.expenseName = name;
        setDescription(name);
        setAmount(amount);
        this.category = category;
        setDate(date);
    }

    public void deleteExpense(User user) { user.getTransactions().remove(this); }

    @Override public String getDetails() { return expenseName + " (" + category + ") - " + getAmount(); }
}