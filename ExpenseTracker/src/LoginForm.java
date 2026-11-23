import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class LoginForm extends JFrame {
    // In-memory accounts: username -> hashed password (lost when the app closes)
    private static final Map<String, String> accounts = new HashMap<>();

    static {
        accounts.put("123", hash("123")); // default test account
    }

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginForm() {
        super("Personal Expense Tracker - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 22f));
        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        panel.add(title, c);

        c.gridwidth = 1;
        c.gridx = 0; c.gridy = 1; panel.add(new JLabel("Username:"), c);
        c.gridx = 1;              panel.add(usernameField, c);
        c.gridx = 0; c.gridy = 2; panel.add(new JLabel("Password:"), c);
        c.gridx = 1;              panel.add(passwordField, c);

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttons.add(loginBtn);
        buttons.add(registerBtn);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        panel.add(buttons, c);

        add(panel);
        getRootPane().setDefaultButton(loginBtn); // Enter key = Login

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void login() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            message("Please enter your username and password.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String stored = accounts.get(user);
        if (stored != null && stored.equals(hash(pass))) {
            dispose();
            new ExpenseTrackerApp().setVisible(true); // open the main app
        } else {
            message("Invalid username or password.", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void register() {
        String user = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());

        if (user.isEmpty() || pass.length() < 4) {
            message("Enter a username and a password of at least 4 characters, then click Register.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (accounts.containsKey(user)) {
            message("That username is already taken.", JOptionPane.ERROR_MESSAGE);
            return;
        }
        accounts.put(user, hash(pass));
        passwordField.setText("");
        message("Account created. You can now log in.", JOptionPane.INFORMATION_MESSAGE);
    }

    private void message(String text, int type) {
        JOptionPane.showMessageDialog(this, text, "Login", type);
    }

    private static String hash(String text) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}