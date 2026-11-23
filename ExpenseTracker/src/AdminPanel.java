import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    private final Administrator admin;
    private final DefaultListModel<User> userModel = new DefaultListModel<>();
    private final DefaultListModel<Category> catModel = new DefaultListModel<>();
    private final JList<User> userList = new JList<>(userModel);
    private final JList<Category> catList = new JList<>(catModel);

    public AdminPanel(Administrator admin) {
        this.admin = admin;
        setLayout(new GridLayout(1, 2, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel users = new JPanel(new BorderLayout(5, 5));
        users.setBorder(BorderFactory.createTitledBorder("Manage Accounts"));
        users.add(new JScrollPane(userList), BorderLayout.CENTER);
        JButton deleteUserBtn = new JButton("Delete Account");
        users.add(deleteUserBtn, BorderLayout.SOUTH);

        JPanel cats = new JPanel(new BorderLayout(5, 5));
        cats.setBorder(BorderFactory.createTitledBorder("Manage Categories"));
        cats.add(new JScrollPane(catList), BorderLayout.CENTER);
        JPanel btns = new JPanel(new GridLayout(1, 3, 5, 5));
        JButton addBtn = new JButton("Add");
        JButton renameBtn = new JButton("Rename");
        JButton deleteBtn = new JButton("Delete");
        btns.add(addBtn); btns.add(renameBtn); btns.add(deleteBtn);
        cats.add(btns, BorderLayout.SOUTH);

        add(users);
        add(cats);

        deleteUserBtn.addActionListener(e -> deleteUser());
        addBtn.addActionListener(e -> addCategory());
        renameBtn.addActionListener(e -> renameCategory());
        deleteBtn.addActionListener(e -> deleteCategory());
        refresh();
    }

    private void deleteUser() {
        User u = userList.getSelectedValue();
        if (u == null) { info("Select an account first."); return; }
        if (!admin.manageUsers(u)) { info("You cannot delete your own account."); return; }
        refresh();
    }

    private void addCategory() {
        String name = JOptionPane.showInputDialog(this, "New category name:");
        if (name == null) return;
        if (!admin.manageCategories(null, name)) info("Invalid or duplicate category name.");
        refresh();
    }

    private void renameCategory() {
        Category c = catList.getSelectedValue();
        if (c == null) { info("Select a category first."); return; }
        String name = JOptionPane.showInputDialog(this, "New name:", c.getName());
        if (name == null) return;
        if (!admin.manageCategories(c, name)) info("Invalid or duplicate category name.");
        refresh();
    }

    private void deleteCategory() {
        Category c = catList.getSelectedValue();
        if (c == null) { info("Select a category first."); return; }
        if (!admin.manageCategories(c, null)) info("Default categories cannot be deleted.");
        refresh();
    }

    private void refresh() {
        userModel.clear();
        for (User u : DataStore.users) userModel.addElement(u);
        catModel.clear();
        for (Category c : DataStore.categories) catModel.addElement(c);
    }

    private void info(String msg) { JOptionPane.showMessageDialog(this, msg); }
}