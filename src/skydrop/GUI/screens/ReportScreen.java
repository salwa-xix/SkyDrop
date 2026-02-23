package skydrop.GUI.screens;

import skydrop.GUI.components.RoundedButton;

import javax.swing.*;
import java.awt.Image;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ReportScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private final Color bgFallback = Color.decode("#262525");
    private final Color activeBg = Color.decode("#0092D9");

    private Image bgImage;
    private Image logoImage;

    public ReportScreen() {

        setTitle("SkyDrop - Report (Employee)");
        setSize(W, H);
        setLocationRelativeTo(null);

        // مهم: لا تقفلي كل البرنامج إذا قفلتي الريبورت
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
        int logoSize = 140;
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds((W - logoSize) / 2, 18, logoSize, logoSize);
        if (logoImage != null) {
            logoLabel.setIcon(new ImageIcon(logoImage.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH)));
        }
        root.add(logoLabel);

        // ===== Title =====
        JLabel title = new JLabel("Report", SwingConstants.CENTER);
        title.setBounds(0, 170, W, 28);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        root.add(title);

        // ===== Main card (centered) =====
        RoundedCard card = new RoundedCard(18);
        int cardW = 320;
        int cardX = (W - cardW) / 2;

        card.setBounds(cardX, 220, cardW, 420);
        card.setLayout(null);
        root.add(card);

        // Totals (Phase 2 placeholders)
        addRow(card, "Total Orders", "0", 20);
        addRow(card, "Accepted Orders", "0", 70);
        addRow(card, "Rejected Orders", "0", 120);

        JLabel sub = new JLabel("Drones");
        sub.setBounds(18, 175, card.getWidth() - 36, 18);
        sub.setFont(new Font("SansSerif", Font.BOLD, 13));
        sub.setForeground(Color.BLACK);
        card.add(sub);

        JTextArea droneArea = new JTextArea();
        droneArea.setEditable(false);
        droneArea.setOpaque(false);
        droneArea.setFont(new Font("SansSerif", Font.PLAIN, 13));
        droneArea.setForeground(Color.DARK_GRAY);
        droneArea.setBounds(18, 200, card.getWidth() - 36, 200);
        card.add(droneArea);

        // Static placeholder blocks
        droneArea.setText(
                "DR-01\n" +
                        "  Al Rawdah\n" +
                        "  Total Delivered: 0\n\n" +
                        "DR-02\n" +
                        "  Al Safa\n" +
                        "  Total Delivered: 0\n\n" +
                        "DR-03\n" +
                        "  Al Hamra\n" +
                        "  Total Delivered: 0"
        );

        // ===== Back button =====
        RoundedButton backBtn = new RoundedButton("Back", 18);
        backBtn.setBounds((W - 160) / 2, H - 95, 160, 48);
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        backBtn.setBackground(Color.WHITE);
        backBtn.setForeground(Color.BLACK);

        backBtn.enableHover(Color.WHITE, activeBg);
        backBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { backBtn.setForeground(Color.WHITE); }
            @Override public void mouseExited (java.awt.event.MouseEvent e) { backBtn.setForeground(Color.BLACK); }
        });

        backBtn.addActionListener(e -> {
            new DashboardScreen();
            dispose();
        });

        root.add(backBtn);

        setVisible(true);
    }

    private void addRow(JPanel card, String key, String value, int y) {
        JLabel k = new JLabel(key);
        k.setBounds(18, y, 170, 26);
        k.setFont(new Font("SansSerif", Font.BOLD, 14));
        k.setForeground(Color.BLACK);
        card.add(k);

        JLabel v = new JLabel(value);
        v.setBounds(190, y, card.getWidth() - 208, 26);
        v.setFont(new Font("SansSerif", Font.PLAIN, 14));
        v.setForeground(Color.BLACK);
        card.add(v);
    }

    private Image loadImageOrNull(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.out.println(" Resource not found: " + path);
            return null;
        }
        return new ImageIcon(url).getImage();
    }

    static class RoundedCard extends JPanel {
        private final int radius;
        RoundedCard(int radius) { this.radius = radius; setOpaque(false); }

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