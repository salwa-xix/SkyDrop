package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import skydrop.app.Main;

import static skydrop.GUI.components.Label.createLabel;

import javax.swing.*;
import java.awt.*;

public class ReportScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    public ReportScreen() {

        setTitle("SkyDrop - Report (Employee)");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        JLabel title = createLabel("Report", 0, 170, W, 28,
                new Font("SansSerif", Font.BOLD, 18),
                Color.WHITE,
                SwingConstants.CENTER);
        root.add(title);

        InfoCard card = new InfoCard(18);
        int cardW = 320;
        int cardX = (W - cardW) / 2;

        card.setBounds(cardX, 220, cardW, 420);
        root.add(card);

        card.addInfoRow("Total Orders",
                String.valueOf(Main.db.getTotalOrders()),
                18, 170, 190, card.getWidth() - 208, 20);

        card.addInfoRow("Accepted Orders",
                String.valueOf(Main.db.getAcceptedOrdersCount()),
                18, 170, 190, card.getWidth() - 208, 70);

        card.addInfoRow("Rejected Orders",
                String.valueOf(Main.db.getRejectedOrdersCount()),
                18, 170, 190, card.getWidth() - 208, 120);

        card.addTitle("Drones", 18, 175, cardW - 36, 18);

        card.addLines(new String[]{
                        "DR-01", "  Al Rawdah",
                        "  Total Delivered: " + Main.db.getDeliveredCountForDrone(1), "",

                        "DR-02", "  Al Hamra",
                        "  Total Delivered: " + Main.db.getDeliveredCountForDrone(2), "",

                        "DR-03", "  Al Naeem",
                        "  Total Delivered: " + Main.db.getDeliveredCountForDrone(3)
                },
                18, 200, cardW - 36, 18,
                new Font("SansSerif", Font.PLAIN, 13),
                Color.DARK_GRAY);

        RoundedButton backButton = new RoundedButton("Back", 18);
        backButton.setBounds((W - 160) / 2, H - 95, 160, 48);

        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);

        backButton.enableHover(Color.WHITE, Color.decode("#0092D9"));

        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setForeground(Color.BLACK);
            }
        });

        backButton.addActionListener(e -> {
            DashboardScreen screen = new DashboardScreen();
            screen.setLocation(this.getLocation());
            dispose();
        });

        root.add(backButton);

        setVisible(true);
    }
}