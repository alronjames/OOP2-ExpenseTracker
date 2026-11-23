import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {
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

        JLabel hint = new JLabel("<html><center>Test accounts: 123/123, advisor/advisor123, user/user123</center></html>",
                SwingConstants.CENTER);
        hint.setFont(hint.getFont().deriveFont(10f));
        c.gridy = 4;
        panel.add(hint, c);

        add(panel);
        getRootPane().setDefaultButton(loginBtn);
        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> register());

        pack();
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void login() {
        String name = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (name.isEmpty() || pass.isEmpty()) {
            message("Please enter your username and password.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = DataStore.findByName(name);
        if (user != null && user.login(pass)) {
            dispose();
            new ExpenseTrackerApp(user).setVisible(true);
        } else {
            message("Invalid username or password.", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void register() {
        String name = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        if (name.isEmpty() || pass.length() < 4) {
            message("Enter a username and a password of at least 4 characters, then click Register.",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField emailField = new JTextField();
        JComboBox<String> roleBox = new JComboBox<>(new String[]{"User", "Financial Advisor"});
        Object[] form = {"Email:", emailField, "Account type:", roleBox};
        int ok = JOptionPane.showConfirmDialog(this, form, "Register", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String email = emailField.getText().trim();
        if (!email.contains("@")) {
            message("Please enter a valid email.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        User user = roleBox.getSelectedIndex() == 1
                ? new FinancialAdvisor(name, email, pass, "General")
                : new User(name, email, pass);
        if (user.register()) {
            passwordField.setText("");
            message("Account created. You can now log in.", JOptionPane.INFORMATION_MESSAGE);
        } else {
            message("That username is already taken.", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void message(String text, int type) {
        JOptionPane.showMessageDialog(this, text, "Login", type);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}