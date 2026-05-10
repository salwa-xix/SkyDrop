package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;
import skydrop.app.SkyDropClient;

import javax.swing.*;
import java.awt.*;

import static skydrop.GUI.components.Label.createLabel;

public class ReportScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    public ReportScreen() {

        // Setup the frame
        setTitle("SkyDrop - Report");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background
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
        card.setLayout(null);
        root.add(card);

        // Load report from the backend
        loadReport(card, cardW);

        RoundedButton saveButton = new RoundedButton("Save", 18);
        saveButton.setBounds((W - 250) / 2, H - 95, 115, 48);
        styleButton(saveButton);

        // Save report using SkyDropClient
        saveButton.addActionListener(e -> saveReport());
        root.add(saveButton);

        RoundedButton backButton = new RoundedButton("Back", 18);
        backButton.setBounds((W - 250) / 2 + 135, H - 95, 115, 48);
        styleButton(backButton);

        // Return to dashboard
        backButton.addActionListener(e -> {
            DashboardScreen screen = new DashboardScreen();
            screen.setLocation(this.getLocation());
            dispose();
        });
        root.add(backButton);

        setVisible(true);
    }

    private void loadReport(InfoCard card, int cardW) {

        // Request report data from the server
        String response = SkyDropClient.sendRequest("GET_REPORT");

        if (response == null) {
            showMessage(card, cardW,
                    "Could not load report.",
                    "Please make sure SkyDropServer is running.");
            return;
        }

        if (!response.startsWith("REPORT|")) {
            showMessage(card, cardW,
                    "Invalid report response.",
                    response);
            return;
        }

        /*
         Expected format:
         REPORT|totalOrders|acceptedOrders|rejectedOrders|deliveredOrders|droneData

         droneData format:
         droneId,district,deliveredCount;droneId,district,deliveredCount
        */
        String[] parts = response.split("\\|", -1);

        if (parts.length < 6) {
            showMessage(card, cardW,
                    "Invalid report format.",
                    "Missing report data.");
            return;
        }

        // Display report summary
        card.addInfoRow("Total Orders", parts[1],
                18, 170, 190, cardW - 208, 20);

        card.addInfoRow("Accepted Orders", parts[2],
                18, 170, 190, cardW - 208, 70);

        card.addInfoRow("Rejected Orders", parts[3],
                18, 170, 190, cardW - 208, 120);

        card.addInfoRow("Delivered Orders", parts[4],
                18, 170, 190, cardW - 208, 170);

        card.addTitle("Drones", 18, 225, cardW - 36, 18);

        String droneData = parts[5];

        if (droneData == null || droneData.trim().isEmpty()) {
            card.addLines(new String[]{"No drone data available."},
                    18, 255, cardW - 36, 18,
                    new Font("SansSerif", Font.PLAIN, 13),
                    Color.DARK_GRAY);
            return;
        }

        String[] drones = droneData.split(";");

        String[] lines = new String[drones.length * 4];
        int idx = 0;

        // Read each drone record and show it in the card
        for (String drone : drones) {

            String[] d = drone.split(",", -1);

            if (d.length >= 3) {
                lines[idx++] = "Drone ID: " + d[0];
                lines[idx++] = "District: " + d[1];
                lines[idx++] = "Delivered: " + d[2];
                lines[idx++] = "";
            }
        }

        if (idx == 0) {
            card.addLines(new String[]{"Invalid drone data."},
                    18, 255, cardW - 36, 18,
                    new Font("SansSerif", Font.PLAIN, 13),
                    Color.DARK_GRAY);
            return;
        }

        // Remove empty unused lines
        String[] finalLines = new String[idx];
        System.arraycopy(lines, 0, finalLines, 0, idx);

        card.addLines(finalLines,
                18, 255, cardW - 36, 18,
                new Font("SansSerif", Font.PLAIN, 12),
                Color.DARK_GRAY);
    }

    private void saveReport() {

        // Ask the server to save the report to file
        String response = SkyDropClient.sendRequest("SAVE_REPORT");

        if (response != null && response.equals("REPORT_SAVED")) {
            JOptionPane.showMessageDialog(this,
                    "Report saved successfully.");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Could not save report.\nPlease make sure SkyDropServer is running.",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showMessage(InfoCard card, int cardW, String line1, String line2) {

        // Show error messages inside the card
        card.addLines(new String[]{line1, line2},
                18, 40, cardW - 36, 22,
                new Font("SansSerif", Font.PLAIN, 13),
                Color.DARK_GRAY);
    }

    private void styleButton(RoundedButton button) {

        // Basic button styling
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.enableHover(Color.WHITE, Color.decode("#0092D9"));
    }
}