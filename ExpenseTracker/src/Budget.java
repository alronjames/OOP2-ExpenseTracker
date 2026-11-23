public class Budget {
    private double limitAmount;
    public void setBudget(double limit) { this.limitAmount = limit; }
    public double getLimitAmount() { return limitAmount; }
    public double getRemainingBalance(double totalExpenses) { return limitAmount - totalExpenses; }
}