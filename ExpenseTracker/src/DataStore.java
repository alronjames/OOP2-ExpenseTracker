import java.util.ArrayList;
import java.util.List;

// Shared in-memory data (lost when the app closes)
public class DataStore {
    public static final List<User> users = new ArrayList<>();
    public static final List<Category> categories = new ArrayList<>();

    static {
        for (String n : new String[]{"Food", "Transport", "Bills", "Shopping", "Health", "Other"}) {
            categories.add(new Category(n, true));
        }
        users.add(new Administrator("admin", "admin@tracker.com", "admin123"));
        users.add(new FinancialAdvisor("advisor", "advisor@tracker.com", "advisor123", "Budget Planning"));
        users.add(new User("user", "user@tracker.com", "user123"));
        users.add(new User("123", "123@tracker.com", "123"));
    }

    public static User findByName(String name) {
        for (User u : users) {
            if (u.getName().equalsIgnoreCase(name)) return u;
        }
        return null;
    }
}