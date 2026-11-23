public class Budget {
    private final String month;
    private double limitAmount;

    public Budget(String month) {
        this.month = month;
    }

    public String getMonth() { return month; }
    public void setBudget(double limit) { this.limitAmount = limit; }
    public double getLimitAmount() { return limitAmount; }
    public double getRemainingBalance(double totalExpenses) { return limitAmount - totalExpenses; }
}