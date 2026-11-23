import javax.swing.*;
import java.awt.*;

public class AdvisorPanel extends JPanel {
    private final FinancialAdvisor advisor;
    private final JComboBox<User> clientBox = new JComboBox<>();
    private final JTextArea output = new JTextArea();
    private final JTextField messageField = new JTextField(30);

    public AdvisorPanel(FinancialAdvisor advisor) {
        this.advisor = advisor;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createTitledBorder("Client"));
        JButton reloadBtn = new JButton("Reload Clients");
        JButton reviewBtn = new JButton("Review Spending (this month)");
        top.add(new JLabel("Client:")); top.add(clientBox);
        top.add(reloadBtn); top.add(reviewBtn);
        add(top, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.setBorder(BorderFactory.createTitledBorder("Budget Recommendation"));
        JButton sendBtn = new JButton("Send");
        bottom.add(new JLabel("Message:")); bottom.add(messageField); bottom.add(sendBtn);
        add(bottom, BorderLayout.SOUTH);

        reloadBtn.addActionListener(e -> loadClients());
        reviewBtn.addActionListener(e -> review());
        sendBtn.addActionListener(e -> send());
        loadClients();
    }

    private void loadClients() {
        clientBox.removeAllItems();
        for (User u : DataStore.users) {
            if (u.getClass() == User.class) clientBox.addItem(u); // regular users only
        }
    }

    private void review() {
        User client = (User) clientBox.getSelectedItem();
        if (client == null) { JOptionPane.showMessageDialog(this, "No client selected."); return; }
        output.setText("Client: " + client.getName() + "\n\n" + advisor.reviewSpending(client));
    }

    private void send() {
        User client = (User) clientBox.getSelectedItem();
        String msg = messageField.getText().trim();
        if (client == null || msg.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select a client and type a message.");
            return;
        }
        advisor.giveRecommendation(client, msg);
        messageField.setText("");
        JOptionPane.showMessageDialog(this, "Recommendation sent to " + client.getName() + ".");
    }
}