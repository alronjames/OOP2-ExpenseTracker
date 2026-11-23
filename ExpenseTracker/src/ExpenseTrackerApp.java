import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class ExpenseTrackerApp extends JFrame {
    private final List<Expense> expenses = new ArrayList<>();
    private final Budget budget = new Budget();

    private final JTextField nameField = new JTextField(12);
    private final JTextField amountField = new JTextField(8);
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 9);
    private final JComboBox<String> categoryBox = new JComboBox<>(
            new String[]{"Food", "Transport", "Bills", "Shopping", "Health", "Other"});
    private final JTextField budgetField = new JTextField(8);

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Name", "Amount", "Category", "Date"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel budgetLabel = new JLabel("Budget: 0.00");
    private final JLabel totalLabel = new JLabel("Total Expenses: 0.00");
    private final JLabel remainingLabel = new JLabel("Remaining: 0.00");

    public ExpenseTrackerApp() {
        super("Personal Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Budget panel (top)
        JPanel budgetPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        budgetPanel.setBorder(BorderFactory.createTitledBorder("Monthly Budget"));
        JButton setBudgetBtn = new JButton("Set Budget");
        budgetPanel.add(new JLabel("Limit:"));
        budgetPanel.add(budgetField);
        budgetPanel.add(setBudgetBtn);

        // Expense form
        JPanel formFields = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formFields.setBorder(BorderFactory.createTitledBorder("Expense"));
        formFields.add(new JLabel("Name:"));     formFields.add(nameField);
        formFields.add(new JLabel("Amount:"));   formFields.add(amountField);
        formFields.add(new JLabel("Category:")); formFields.add(categoryBox);
        formFields.add(new JLabel("Date (YYYY-MM-DD):")); formFields.add(dateField);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        actionPanel.add(addBtn); actionPanel.add(editBtn); actionPanel.add(deleteBtn);

        JPanel form = new JPanel(new BorderLayout());
        form.add(formFields, BorderLayout.NORTH);
        form.add(actionPanel, BorderLayout.CENTER);

        JPanel north = new JPanel(new GridLayout(2, 1, 5, 5));
        north.add(budgetPanel);
        north.add(form);
        add(north, BorderLayout.NORTH);

        // Table (center)
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Summary (bottom)
        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 5));
        summary.setBorder(BorderFactory.createTitledBorder("Summary"));
        summary.add(budgetLabel); summary.add(totalLabel); summary.add(remainingLabel);
        add(summary, BorderLayout.SOUTH);

        // Events
        setBudgetBtn.addActionListener(e -> setBudget());
        addBtn.addActionListener(e -> addExpense());
        editBtn.addActionListener(e -> editExpense());
        deleteBtn.addActionListener(e -> deleteExpense());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedToForm();
        });

        setSize(900, 520);
        setLocationRelativeTo(null);
    }

    private void setBudget() {
        try {
            double v = Double.parseDouble(budgetField.getText().trim());
            if (v < 0) throw new NumberFormatException();
            budget.setBudget(v);
            refresh();
        } catch (NumberFormatException ex) {
            error("Enter a valid budget amount.");
        }
    }

    private Expense readForm(Expense target) {
        String name = nameField.getText().trim();
        if (name.isEmpty()) { error("Expense name is required."); return null; }
        double amount;
        try {
            amount = Double.parseDouble(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) { error("Enter a valid amount greater than 0."); return null; }
        LocalDate date;
        try { date = LocalDate.parse(dateField.getText().trim()); }
        catch (DateTimeParseException ex) { error("Date must be in YYYY-MM-DD format."); return null; }
        String category = (String) categoryBox.getSelectedItem();

        if (target == null) return new Expense(name, amount, category, date);
        target.setExpenseName(name); target.setAmount(amount);
        target.setCategory(category); target.setDate(date);
        return target;
    }

    private void addExpense() {
        Expense e = readForm(null);
        if (e == null) return;
        expenses.add(e);
        clearForm();
        refresh();
    }

    private void editExpense() {
        int row = table.getSelectedRow();
        if (row < 0) { error("Select an expense to update."); return; }
        if (readForm(expenses.get(row)) != null) { clearForm(); refresh(); }
    }

    private void deleteExpense() {
        int row = table.getSelectedRow();
        if (row < 0) { error("Select an expense to delete."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Delete this expense permanently?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok == JOptionPane.YES_OPTION) {
            expenses.remove(row);
            clearForm();
            refresh();
        }
    }

    private void loadSelectedToForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Expense e = expenses.get(row);
        nameField.setText(e.getExpenseName());
        amountField.setText(String.valueOf(e.getAmount()));
        categoryBox.setSelectedItem(e.getCategory());
        dateField.setText(e.getDate().toString());
    }

    private void clearForm() {
        nameField.setText(""); amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        table.clearSelection();
    }

    private void refresh() {
        model.setRowCount(0);
        double total = 0;
        for (Expense e : expenses) {
            model.addRow(new Object[]{e.getId(), e.getExpenseName(),
                    String.format("%.2f", e.getAmount()), e.getCategory(), e.getDate()});
            total += e.getAmount();
        }
        double remaining = budget.getRemainingBalance(total);
        budgetLabel.setText(String.format("Budget: %.2f", budget.getLimitAmount()));
        totalLabel.setText(String.format("Total Expenses: %.2f", total));
        remainingLabel.setText(String.format("Remaining: %.2f", remaining));
        remainingLabel.setForeground(remaining < 0 ? Color.RED : new Color(0, 120, 0));
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ExpenseTrackerApp().setVisible(true));
    }
}