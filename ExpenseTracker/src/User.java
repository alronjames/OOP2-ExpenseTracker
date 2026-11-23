import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class User {
    private static int counter = 1;
    private final int userId;
    private final String name;
    private final String email;
    private final String passwordHash;
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Budget> budgets = new ArrayList<>();
    private final List<Recommendation> recommendations = new ArrayList<>();

    public User(String name, String email, String password) {
        this.userId = counter++;
        this.name = name;
        this.email = email;
        this.passwordHash = hash(password);
    }

    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return "User"; }
    public List<Transaction> getTransactions() { return transactions; }
    public List<Recommendation> getRecommendations() { return recommendations; }

    public Budget getBudget(YearMonth month) {
        for (Budget b : budgets) {
            if (b.getMonth().equals(month.toString())) return b;
        }
        Budget b = new Budget(month.toString());
        budgets.add(b);
        return b;
    }

    public boolean register() {
        if (DataStore.findByName(name) != null) return false;
        DataStore.users.add(this);
        return true;
    }

    public boolean login(String password) { return passwordHash.equals(hash(password)); }

    public void logout() { /* nothing to clear: the session ends when the window closes */ }

    public static String hash(String text) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override public String toString() { return name + " (" + getRole() + ")"; }
}