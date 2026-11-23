import java.time.LocalDate;

public class Recommendation {
    private static int counter = 1;
    private final int recommendationId;
    private final String message;
    private final LocalDate dateGiven;
    private final FinancialAdvisor advisor;
    private final User recipient;

    public Recommendation(FinancialAdvisor advisor, User recipient, String message) {
        this.recommendationId = counter++;
        this.advisor = advisor;
        this.recipient = recipient;
        this.message = message;
        this.dateGiven = LocalDate.now();
    }
    public void send() { recipient.getRecommendations().add(this); }
    public int getRecommendationId() { return recommendationId; }
    public String getMessage() { return message; }
    public LocalDate getDateGiven() { return dateGiven; }

    @Override public String toString() {
        return "[" + dateGiven + "] " + advisor.getName() + ": " + message;
    }
}