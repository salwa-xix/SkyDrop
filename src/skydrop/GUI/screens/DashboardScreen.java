package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import static skydrop.GUI.components.Label.createLabel;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Arrays;

public class DashboardScreen extends JFrame {

    // Fixed window width & height
    private static final int W = 375;
    private static final int H = 812;

    public DashboardScreen() {

        // Window settings
        setTitle("SkyDrop - Dashboard (Employee)"); // window bar
        setSize(W, H); // window size
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // dispose only this window when closed
        setResizable(false); // disable resizing

        // Create the main container panel for this screen
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        // Create and positioning the title "Drone Dashboard"
        JLabel title = createLabel("Drone Dashboard", 0, 175, W, 28,
                new Font("SansSerif", Font.BOLD, 18), Color.WHITE, SwingConstants.CENTER);
        root.add(title);

        // Create static mock data
        List<DroneData> drones = Arrays.asList(
                new DroneData("DR-01", "Al Rawdah"),
                new DroneData("DR-02", "Al Safa"),
                new DroneData("DR-03", "Al Hamra")
        );

        // Card settings
        int cardW = 320;
        int cardH = 135;
        int cardX = (W - cardW) / 2; // center cards
        int startY = 220; // first card Y position
        int gap = 16; // gap between cards

        // Loop through all drones and create a card for each
        for (int i = 0; i < drones.size(); i++) {

            DroneData d = drones.get(i);

            // Create card with information
            InfoCard card = new InfoCard(18);

            // position & size
            card.setBounds(cardX, startY + i * (cardH + gap), cardW, cardH);

            // Card content
            card.addTitle(d.droneId, 14, 10, 200, 22);
            card.addSubtitle("District: " + d.district, 14, 32, 250, 18);

            card.addInfoRow("Assigned:", d.assigned, 14, 90, 100, 240, 60);
            card.addInfoRow("Status:", d.status, 14, 90, 100, 240, 83);
            card.addInfoRow("Queue:", String.valueOf(d.queue), 14, 90, 100, 240, 106);

            root.add(card);
        }

// Create the "Report" action button
        RoundedButton reportButton = new RoundedButton("Report", 18);
        reportButton.setBounds((W - 160) / 2, H - 95, 160, 48); // positioning

        reportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        reportButton.setBackground(Color.WHITE);
        reportButton.setForeground(Color.BLACK);

// Hover effect
        reportButton.enableHover(Color.WHITE, Color.decode("#0092D9"));
        reportButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                reportButton.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                reportButton.setForeground(Color.BLACK);
            }
        });

// Run this when "Report" button is clicked
        reportButton.addActionListener(e -> {
            new ReportScreen();     // open report window
            dispose();              // close current dashboard window
        });

        root.add(reportButton);

        // Display the UI after all components are added
        setVisible(true);
    }

    // Mock data class
    static class DroneData {
        final String droneId;
        final String district;
        final String assigned = "None";
        final String status = "Idle";
        final int queue = 0;

        DroneData(String droneId, String district) {
            this.droneId = droneId;
            this.district = district;
        }
    }
}