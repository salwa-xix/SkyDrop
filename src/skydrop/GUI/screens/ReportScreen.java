package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import static skydrop.GUI.components.Label.createLabel;
import javax.swing.*;
import java.awt.*;

public class ReportScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    public ReportScreen() {

        // Window settings
        setTitle("SkyDrop - Report (Employee)"); // window bar
        setSize(W, H); // window size
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // dispose only this window when closed
        setResizable(false); // disable resizing

        // Create the main container panel for this screen
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        // Create and positioning the title "Report"
        JLabel title = createLabel("Report", 0, 170, W, 28,
                new Font("SansSerif", Font.BOLD, 18), Color.WHITE, SwingConstants.CENTER);
        root.add(title);

        // Create the main report card
        InfoCard card = new InfoCard(18);
        int cardW = 320;
        int cardX = (W - cardW) / 2;

        card.setBounds(cardX, 220, cardW, 420); // position & size
        root.add(card);

    // Add totals
        card.addInfoRow("Total Orders",    "0", 18, 170, 190, card.getWidth() - 208, 20);
        card.addInfoRow("Accepted Orders", "0", 18, 170, 190, card.getWidth() - 208, 70);
        card.addInfoRow("Rejected Orders", "0", 18, 170, 190, card.getWidth() - 208, 120);

    // Section label
        card.addTitle("Drones", 18, 175, cardW - 36, 18);

        card.addLines(new String[]{
                "DR-01", "  Al Rawdah", "  Total Delivered: 0", "",
                "DR-02", "  Al Safa",   "  Total Delivered: 0", "",
                "DR-03", "  Al Hamra",  "  Total Delivered: 0"
        }, 18, 200, cardW - 36, 18, new Font("SansSerif", Font.PLAIN, 13), Color.DARK_GRAY);

    // Create the "Back" action button
        RoundedButton backButton = new RoundedButton("Back", 18);
        backButton.setBounds((W - 160) / 2, H - 95, 160, 48); // positioning

        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);

    // Hover effect
        backButton.enableHover(Color.WHITE, Color.decode("#0092D9"));
        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                backButton.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                backButton.setForeground(Color.BLACK);
            }
        });

    // Run this when "Back" button is clicked
        backButton.addActionListener(e -> {
            new DashboardScreen();  // open dashboard window
            dispose();              // close current report window
        });

        root.add(backButton);
        // Display the UI after all components are added
        setVisible(true);
    }
}