import javax.swing.*;
import java.awt.*;

public class RecommendationPanel extends JPanel {
    private final User user;
    private final JTextArea area = new JTextArea();

    public RecommendationPanel(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refresh());
        add(refreshBtn, BorderLayout.NORTH);
        add(new JScrollPane(area), BorderLayout.CENTER);
        refresh();
    }

    private void refresh() {
        StringBuilder sb = new StringBuilder();
        for (Recommendation r : user.getRecommendations()) sb.append(r).append("\n\n");
        area.setText(sb.length() == 0 ? "No recommendations yet." : sb.toString());
    }
}