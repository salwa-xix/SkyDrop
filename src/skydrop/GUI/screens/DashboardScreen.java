package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import skydrop.app.SkyDropClient;

import static skydrop.GUI.components.Label.createLabel;

import javax.swing.*;
import java.awt.*;

public class DashboardScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private BaseScreen root;

    public DashboardScreen() {

        setTitle("SkyDrop - Dashboard (Employee)");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        root = new BaseScreen(getClass());
        setContentPane(root);

        refreshDashboard();

        Timer timer = new Timer(1000, e -> refreshDashboard());
        timer.start();

        setVisible(true);
    }

    private void refreshDashboard() {

        root.removeAll();

        JLabel title = createLabel("Drone Dashboard", 0, 175, W, 28,
                new Font("SansSerif", Font.BOLD, 18),
                Color.WHITE,
                SwingConstants.CENTER);
        root.add(title);

        loadDroneCards();

        addReportButton();

        root.revalidate();
        root.repaint();
    }

    private void loadDroneCards() {

        String response = SkyDropClient.sendRequest("GET_DRONES");

        if (response == null || !response.startsWith("DRONES|")) {
            return;
        }

        String data = response.substring("DRONES|".length());

        if (data.isEmpty()) {
            return;
        }

        String[] drones = data.split(";");

        int cardW = 320;
        int cardH = 135;
        int cardX = (W - cardW) / 2;
        int startY = 220;
        int gap = 16;

        for (int i = 0; i < drones.length; i++) {

            String[] parts = drones[i].split(",");

            int droneId = Integer.parseInt(parts[0]);
            String district = parts[1];
            String status = parts[2];
            String assigned = parts[3];
            String queue = parts[4];

            InfoCard card = new InfoCard(18);
            card.setBounds(cardX, startY + i * (cardH + gap), cardW, cardH);

            String droneName = String.format("DR-%02d", droneId);

            card.addTitle(droneName, 14, 10, 200, 22);
            card.addSubtitle("District: " + district, 14, 32, 250, 18);

            card.addInfoRow("Assigned:", assigned, 14, 90, 100, 240, 60);
            card.addInfoRow("Status:", status, 14, 90, 100, 240, 83);
            card.addInfoRow("Queue:", queue, 14, 90, 100, 240, 106);

            root.add(card);
        }
    }

    private void addReportButton() {

        RoundedButton reportButton = new RoundedButton("Report", 18);
        reportButton.setBounds((W - 160) / 2, H - 95, 160, 48);

        reportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        reportButton.setBackground(Color.WHITE);
        reportButton.setForeground(Color.BLACK);

        reportButton.enableHover(Color.WHITE, Color.decode("#0092D9"));

        reportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                reportButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                reportButton.setForeground(Color.BLACK);
            }
        });

        reportButton.addActionListener(e -> {
            ReportScreen screen = new ReportScreen();
            screen.setLocation(this.getLocation());
            dispose();
        });

        root.add(reportButton);
    }
}