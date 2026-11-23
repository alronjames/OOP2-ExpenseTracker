import javax.swing.*;
import java.awt.*;
import java.time.YearMonth;
import java.time.format.DateTimeParseException;

public class BudgetReportPanel extends JPanel {
    private final User user;
    private final JTextField monthField = new JTextField(YearMonth.now().toString(), 8);
    private final JTextField budgetField = new JTextField(8);
    private final JTextArea output = new JTextArea();

    public BudgetReportPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Monthly Budget"));
        JButton setBtn = new JButton("Set Budget");
        JButton reportBtn = new JButton("Generate Report");
        top.add(new JLabel("Month (YYYY-MM):")); top.add(monthField);
        top.add(new JLabel("Limit:"));           top.add(budgetField);
        top.add(setBtn); top.add(reportBtn);
        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(output), BorderLayout.CENTER);

        setBtn.addActionListener(e -> setBudget());
        reportBtn.addActionListener(e -> generate());
    }

    private YearMonth readMonth() {
        try {
            return YearMonth.parse(monthField.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Month must be in YYYY-MM format.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private void setBudget() {
        YearMonth ym = readMonth();
        if (ym == null) return;
        try {
            double v = Double.parseDouble(budgetField.getText().trim());
            if (v < 0) throw new NumberFormatException();
            user.getBudget(ym).setBudget(v);
            generate();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Enter a valid budget amount.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generate() {
        YearMonth ym = readMonth();
        if (ym == null) return;
        Report report = new Report();
        report.generateReport(user, ym);
        output.setText(report.viewSummary());
    }
}