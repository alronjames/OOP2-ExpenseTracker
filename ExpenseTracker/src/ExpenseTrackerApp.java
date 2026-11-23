import javax.swing.*;
import java.awt.*;

public class ExpenseTrackerApp extends JFrame {
    public ExpenseTrackerApp(User user) {
        super("Personal Expense Tracker - " + user.getName());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        if (user instanceof Administrator admin) {
            tabs.addTab("Manage Accounts & Categories", new AdminPanel(admin));
        } else if (user instanceof FinancialAdvisor advisor) {
            tabs.addTab("Advisor", new AdvisorPanel(advisor));
        } else {
            tabs.addTab("Transactions", new TransactionPanel(user));
            tabs.addTab("Budget & Report", new BudgetReportPanel(user));
            tabs.addTab("Recommendations", new RecommendationPanel(user));
        }

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            user.logout();
            dispose();
            new LoginForm().setVisible(true);
        });
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(8, 10, 0, 10));
        top.add(new JLabel("Logged in as: " + user.getName() + " (" + user.getRole() + ")"), BorderLayout.WEST);
        top.add(logoutBtn, BorderLayout.EAST);

        add(top, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setSize(950, 560);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}