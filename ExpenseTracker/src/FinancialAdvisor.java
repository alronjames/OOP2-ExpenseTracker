import java.time.YearMonth;

public class FinancialAdvisor extends User {
    private static int counter = 1;
    private final int advisorId;
    private final String specialization;

    public FinancialAdvisor(String name, String email, String password, String specialization) {
        super(name, email, password);
        this.advisorId = counter++;
        this.specialization = specialization;
    }
    public int getAdvisorId() { return advisorId; }
    public String getSpecialization() { return specialization; }
    @Override public String getRole() { return "Financial Advisor"; }

    public String reviewSpending(User client) {
        Report report = new Report();
        report.generateReport(client, YearMonth.now());
        return report.viewSummary();
    }

    public void giveRecommendation(User client, String message) {
        new Recommendation(this, client, message).send();
    }
}