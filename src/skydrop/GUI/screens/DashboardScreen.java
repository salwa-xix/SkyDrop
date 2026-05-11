package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;
import skydrop.app.SkyDropClient;

import javax.swing.*;
import java.awt.*;

import static skydrop.GUI.components.Label.createLabel;

public class DashboardScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private BaseScreen root;
    private Timer timer;

    public DashboardScreen() {

        // Setup the frame
        setTitle("SkyDrop - Dashboard");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background
        root = new BaseScreen(getClass());
        setContentPane(root);

        // Load dashboard for the first time
        refreshDashboard();

        // Refresh dashboard every second
        timer = new Timer(1000, e -> refreshDashboard());
        timer.start();

        setVisible(true);
    }

    private void refreshDashboard() {

        // Clear old cards before drawing updated data
        root.removeAll();

        root.addLogo();

        JLabel title = createLabel("Drone Dashboard", 0, 175, W, 28,
                new Font("SansSerif", Font.BOLD, 18),
                Color.WHITE,
                SwingConstants.CENTER);
        root.add(title);

        // Load drones from backend
        loadDroneCards();

        // Add report button at the bottom
        addReportButton();

        root.revalidate();
        root.repaint();
    }

    private void loadDroneCards() {

        // Ask server for drones data
        String response = SkyDropClient.sendRequest("GET_DRONES");

        if (response == null) {
            showMessage("Cannot connect to server.");
            return;
        }

        if (!response.startsWith("DRONES|")) {
            showMessage("Invalid server response.");
            return;
        }

        String data = response.substring("DRONES|".length());

        if (data.trim().isEmpty()) {
            showMessage("No drones available.");
            return;
        }

        String[] drones = data.split(";");

        int cardW = 320;
        int cardH = 135;
        int cardX = (W - cardW) / 2;
        int startY = 220;
        int gap = 16;

        for (int i = 0; i < drones.length; i++) {

            String[] parts = drones[i].split(",", -1);

            // Skip invalid drone records
            if (parts.length < 5) {
                continue;
            }

            String droneId = parts[0];
            String district = parts[1];
            String status = parts[2];
            String assignedOrder = parts[3];
            String queueCount = parts[4];

            InfoCard card = new InfoCard(18);
            card.setBounds(cardX, startY + i * (cardH + gap), cardW, cardH);
            card.setLayout(null);

            String droneName = "DR-" + droneId;

            card.addTitle(droneName, 14, 10, 200, 22);
            card.addSubtitle("District: " + district, 14, 32, 250, 18);

            card.addInfoRow("Assigned:", assignedOrder,
                    14, 90, 100, 200, 60);

            card.addInfoRow("Status:", status,
                    14, 90, 100, 200, 83);

            card.addInfoRow("Queue:", queueCount,
                    14, 90, 100, 200, 106);

            root.add(card);
        }
    }

    private void addReportButton() {

        RoundedButton reportButton = new RoundedButton("Report", 18);
        reportButton.setBounds((W - 160) / 2, H - 95, 160, 48);

        // Style the button
        reportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        reportButton.setBackground(Color.WHITE);
        reportButton.setForeground(Color.BLACK);
        reportButton.enableHover(Color.WHITE, Color.decode("#0092D9"));

        // Open report screen
        reportButton.addActionListener(e -> {

            // Stop timer before leaving this screen
            if (timer != null) {
                timer.stop();
            }

            ReportScreen screen = new ReportScreen();
            screen.setLocation(this.getLocation());
            dispose();
        });

        root.add(reportButton);
    }

    private void showMessage(String message) {

        // Show simple message when dashboard data cannot be loaded
        JLabel label = createLabel(message, 0, 330, W, 25,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE,
                SwingConstants.CENTER);

        root.add(label);
    }
}