package system;

import javax.swing.*;
import java.awt.*;

public class NotificationPanel extends JPanel {
    public NotificationPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(255, 255, 200));
        setBorder(BorderFactory.createLineBorder(Color.ORANGE, 2));

        JLabel notificationLabel = new JLabel(" New notifications available! ");
        notificationLabel.setFont(new Font("Arial", Font.BOLD, 12));

        add(notificationLabel, BorderLayout.CENTER);

        JButton closeBtn = new JButton("×");
        closeBtn.setBorder(BorderFactory.createEmptyBorder());
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(e -> setVisible(false));

        add(closeBtn, BorderLayout.EAST);
    }
}