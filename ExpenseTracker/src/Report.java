import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

public class Report {
    private static int counter = 1;
    private final int reportId;
    private String month = "";
    private double totalExpenses;
    private double remainingBalance;
    private double budgetLimit;
    private final Map<String, Double> byCategory = new LinkedHashMap<>();

    public Report() { this.reportId = counter++; }

    public void generateReport(User user, YearMonth ym) {
        month = ym.toString();
        totalExpenses = 0;
        byCategory.clear();
        for (Transaction t : user.getTransactions()) {
            if (t instanceof Expense e && YearMonth.from(e.getDate()).equals(ym)) {
                totalExpenses += e.getAmount();
                byCategory.merge(e.getCategory().getName(), e.getAmount(), Double::sum);
            }
        }
        Budget b = user.getBudget(ym);
        budgetLimit = b.getLimitAmount();
        remainingBalance = b.getRemainingBalance(totalExpenses);
    }

    public String viewSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("Report #").append(reportId).append(" - ").append(month).append("\n\n");
        sb.append(String.format("Budget limit:      %.2f%n", budgetLimit));
        sb.append(String.format("Total expenses:    %.2f%n", totalExpenses));
        sb.append(String.format("Remaining balance: %.2f%n", remainingBalance));
        if (remainingBalance < 0) sb.append("\nWARNING: over budget!\n");
        sb.append("\nExpenses by category:\n");
        if (byCategory.isEmpty()) sb.append("  (none)\n");
        for (Map.Entry<String, Double> e : byCategory.entrySet()) {
            sb.append(String.format("  %-12s %.2f%n", e.getKey(), e.getValue()));
        }
        return sb.toString();
    }

    public int getReportId() { return reportId; }
    public String getMonth() { return month; }
    public double getTotalExpenses() { return totalExpenses; }
    public double getRemainingBalance() { return remainingBalance; }
}