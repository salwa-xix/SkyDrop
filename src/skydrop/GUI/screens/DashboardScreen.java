package skydrop.GUI.screens;

import skydrop.GUI.components.RoundedButton;

import javax.swing.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

public class DashboardScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private final Color bgFallback = Color.decode("#262525");
    private final Color activeBg = Color.decode("#0092D9");

    private Image bgImage;
    private Image logoImage;

    private final List<DroneCard> cards = new ArrayList<>();

    public DashboardScreen() {

        setTitle("SkyDrop - Dashboard (Employee)");
        setSize(W, H);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setResizable(false);

        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (bgImage != null) g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                else {
                    g2.setColor(bgFallback);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };

        root.setBackground(bgFallback);
        setContentPane(root);

        bgImage = loadImageOrNull("/Images/wallpaper.png");
        logoImage = loadImageOrNull("/Images/skydrop logo.png");

        // ===== Logo =====
        int logoSize = 150;
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds((W - logoSize) / 2, 18, logoSize, logoSize);
        if (logoImage != null) {
            logoLabel.setIcon(new ImageIcon(logoImage.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH)));
        }
        root.add(logoLabel);

        // ===== Title =====
        JLabel title = new JLabel("Drone Dashboard", SwingConstants.CENTER);
        title.setBounds(0, 175, W, 28);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title);

        // ===== Static data (Phase 2 placeholders) =====
        List<DroneData> drones = Arrays.asList(
                new DroneData("DR-01", "Al Rawdah"),
                new DroneData("DR-02", "Al Safa"),
                new DroneData("DR-03", "Al Hamra")
        );

        // ===== Cards layout (centered) =====
        int cardW = 320;
        int cardX = (W - cardW) / 2;
        int cardH = 135;
        int startY = 220;
        int gap = 16;

        for (int i = 0; i < drones.size(); i++) {
            DroneCard c = new DroneCard(drones.get(i), 18);
            c.setBounds(cardX, startY + i * (cardH + gap), cardW, cardH);
            root.add(c);
            cards.add(c);
        }

        // ===== Report button =====
        RoundedButton reportBtn = new RoundedButton("Report", 18);
        reportBtn.setBounds((W - 160) / 2, H - 95, 160, 48);
        reportBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        reportBtn.setBackground(Color.WHITE);
        reportBtn.setForeground(Color.BLACK);

        reportBtn.enableHover(Color.WHITE, activeBg);
        reportBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { reportBtn.setForeground(Color.WHITE); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { reportBtn.setForeground(Color.BLACK); }
        });

        reportBtn.addActionListener(e -> {
            new ReportScreen(); // Phase 2 report (static)
            dispose();
        });

        root.add(reportBtn);

        setVisible(true);
    }

    private Image loadImageOrNull(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println(" Resource not found: " + path);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    // =========================
    // Drone placeholder data
    // =========================
    static class DroneData {
        final String droneId;
        final String district;

        // Phase 2 defaults (static)
        final String assigned = "None";
        final String status = "Idle";
        final int queue = 0;

        DroneData(String droneId, String district) {
            this.droneId = droneId;
            this.district = district;
        }
    }

    // =========================
    // Drone card UI
    // =========================
    static class DroneCard extends RoundedCard {

        final DroneData data;

        private final JLabel idLabel = new JLabel();
        private final JLabel districtLabel = new JLabel();
        private final JLabel assignedValue = new JLabel();
        private final JLabel statusValue = new JLabel();
        private final JLabel queueValue = new JLabel();

        DroneCard(DroneData data, int radius) {
            super(radius);
            this.data = data;

            setLayout(null);
            setOpaque(false);

            idLabel.setBounds(14, 10, 200, 22);
            idLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
            idLabel.setForeground(Color.BLACK);
            add(idLabel);

            districtLabel.setBounds(14, 32, 250, 18);
            districtLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            districtLabel.setForeground(Color.DARK_GRAY);
            add(districtLabel);

            add(key("Assigned:", 14, 60));
            add(key("Status:", 14, 83));
            add(key("Queue:", 14, 106));

            assignedValue.setBounds(100, 60, 240, 18);
            assignedValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
            add(assignedValue);

            statusValue.setBounds(100, 83, 240, 18);
            statusValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
            add(statusValue);

            queueValue.setBounds(100, 106, 240, 18);
            queueValue.setFont(new Font("SansSerif", Font.PLAIN, 13));
            add(queueValue);

            refreshUI();
        }

        void refreshUI() {
            idLabel.setText(data.droneId);
            districtLabel.setText("District: " + data.district);

            assignedValue.setText(data.assigned);
            statusValue.setText(data.status);
            queueValue.setText(String.valueOf(data.queue));

            statusValue.setForeground(Color.BLACK);
            repaint();
        }

        private JLabel key(String t, int x, int y) {
            JLabel l = new JLabel(t);
            l.setBounds(x, y, 90, 18);
            l.setFont(new Font("SansSerif", Font.BOLD, 13));
            l.setForeground(Color.BLACK);
            return l;
        }
    }

    // Rounded card background
    static class RoundedCard extends JPanel {
        private final int radius;
        RoundedCard(int radius) { this.radius = radius; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(255, 255, 255, 235));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}