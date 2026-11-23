import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TransactionPanel extends JPanel {
    private final User user;
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Expense", "Income"});
    private final JTextField nameField = new JTextField(12);
    private final JTextField amountField = new JTextField(8);
    private final JComboBox<Category> categoryBox = new JComboBox<>();
    private final JTextField dateField = new JTextField(LocalDate.now().toString(), 9);
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Type", "Name / Source", "Amount", "Category", "Date"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel incomeLabel = new JLabel();
    private final JLabel expenseLabel = new JLabel();
    private final JLabel balanceLabel = new JLabel();

    public TransactionPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formFields = new JPanel(new FlowLayout(FlowLayout.LEFT));
        formFields.setBorder(BorderFactory.createTitledBorder("Transaction"));
        formFields.add(new JLabel("Type:"));     formFields.add(typeBox);
        formFields.add(new JLabel("Name/Source:")); formFields.add(nameField);
        formFields.add(new JLabel("Amount:"));   formFields.add(amountField);
        formFields.add(new JLabel("Category:")); formFields.add(categoryBox);
        formFields.add(new JLabel("Date (YYYY-MM-DD):")); formFields.add(dateField);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add");
        JButton editBtn = new JButton("Update Selected");
        JButton deleteBtn = new JButton("Delete Selected");
        actionPanel.add(addBtn); actionPanel.add(editBtn); actionPanel.add(deleteBtn);

        JPanel form = new JPanel(new BorderLayout());
        form.add(formFields, BorderLayout.NORTH);
        form.add(actionPanel, BorderLayout.CENTER);
        add(form, BorderLayout.NORTH);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel summary = new JPanel(new FlowLayout(FlowLayout.LEFT, 25, 5));
        summary.setBorder(BorderFactory.createTitledBorder("Summary (all time)"));
        summary.add(incomeLabel); summary.add(expenseLabel); summary.add(balanceLabel);
        add(summary, BorderLayout.SOUTH);

        typeBox.addActionListener(e -> categoryBox.setEnabled(typeBox.getSelectedIndex() == 0));
        addBtn.addActionListener(e -> add());
        editBtn.addActionListener(e -> edit());
        deleteBtn.addActionListener(e -> delete());
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelected();
        });
        refresh();
    }

    private void add() {
        String name = nameField.getText().trim();
        Double amount = readAmount();
        LocalDate date = readDate();
        if (name.isEmpty()) { error("Name / source is required."); return; }
        if (amount == null || date == null) return;

        if (typeBox.getSelectedIndex() == 0) {
            Category c = (Category) categoryBox.getSelectedItem();
            if (c == null) { error("No category available. Ask the administrator to add one."); return; }
            new Expense(name, amount, c, date).addExpense(user);
        } else {
            new Income(name, amount, date).addIncome(user);
        }
        clearForm();
        refresh();
    }

    private void edit() {
        int row = table.getSelectedRow();
        if (row < 0) { error("Select a transaction to update."); return; }
        String name = nameField.getText().trim();
        Double amount = readAmount();
        LocalDate date = readDate();
        if (name.isEmpty()) { error("Name / source is required."); return; }
        if (amount == null || date == null) return;

        Transaction t = user.getTransactions().get(row);
        if (t instanceof Expense e) {
            Category c = (Category) categoryBox.getSelectedItem();
            if (c == null) { error("Select a category."); return; }
            e.editExpense(name, amount, c, date);
        } else if (t instanceof Income i) {
            i.editIncome(name, amount, date);
        }
        clearForm();
        refresh();
    }

    private void delete() {
        int row = table.getSelectedRow();
        if (row < 0) { error("Select a transaction to delete."); return; }
        int ok = JOptionPane.showConfirmDialog(this, "Delete this transaction permanently?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        Transaction t = user.getTransactions().get(row);
        if (t instanceof Expense e) e.deleteExpense(user);
        else user.getTransactions().remove(t);
        clearForm();
        refresh();
    }

    private void loadSelected() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Transaction t = user.getTransactions().get(row);
        amountField.setText(String.valueOf(t.getAmount()));
        dateField.setText(t.getDate().toString());
        nameField.setText(t.getDescription());
        if (t instanceof Expense e) {
            typeBox.setSelectedIndex(0);
            categoryBox.setSelectedItem(e.getCategory());
        } else {
            typeBox.setSelectedIndex(1);
        }
    }

    private Double readAmount() {
        try {
            double a = Double.parseDouble(amountField.getText().trim());
            if (a <= 0) throw new NumberFormatException();
            return a;
        } catch (NumberFormatException ex) {
            error("Enter a valid amount greater than 0.");
            return null;
        }
    }

    private LocalDate readDate() {
        try {
            return LocalDate.parse(dateField.getText().trim());
        } catch (DateTimeParseException ex) {
            error("Date must be in YYYY-MM-DD format.");
            return null;
        }
    }

    private void clearForm() {
        nameField.setText("");
        amountField.setText("");
        dateField.setText(LocalDate.now().toString());
        table.clearSelection();
    }

    private void refresh() {
        Object selected = categoryBox.getSelectedItem();
        categoryBox.removeAllItems();
        for (Category c : DataStore.categories) categoryBox.addItem(c);
        if (selected != null) categoryBox.setSelectedItem(selected);

        model.setRowCount(0);
        double income = 0, expenses = 0;
        for (Transaction t : user.getTransactions()) {
            String type = "Income", cat = "-";
            if (t instanceof Expense e) {
                type = "Expense";
                cat = e.getCategory().getName();
                expenses += t.getAmount();
            } else {
                income += t.getAmount();
            }
            model.addRow(new Object[]{t.getId(), type, t.getDescription(),
                    String.format("%.2f", t.getAmount()), cat, t.getDate()});
        }
        incomeLabel.setText(String.format("Total Income: %.2f", income));
        expenseLabel.setText(String.format("Total Expenses: %.2f", expenses));
        double balance = income - expenses;
        balanceLabel.setText(String.format("Balance: %.2f", balance));
        balanceLabel.setForeground(balance < 0 ? Color.RED : new Color(0, 120, 0));
    }

    private void error(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }
}