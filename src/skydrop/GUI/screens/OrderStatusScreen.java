package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;

import javax.swing.*;
import java.awt.*;

import static skydrop.GUI.components.Label.createLabel;

public class OrderStatusScreen extends JFrame {

    // Screen size
    private static final int W = 375, H = 812;

    // Star colors
    private static final Color STAR_OFF = new Color(170, 170, 170); // gray
    private static final Color STAR_ON  = Color.decode("#FFD36E");  // yellow

    // Hover colors for Submit
    private static final Color BTN_NORMAL = Color.WHITE;
    private static final Color BTN_HOVER  = Color.decode("#0092D9");

    // Main UI elements
    private JLabel status, helper, stars, rejMsg;
    private JPanel rateP, rejP;
    private RoundedButton send;
    private int rating = 0; // selected stars (0–5)

    public OrderStatusScreen(int orderId, String type, String place, String item) {

        // Frame setup
        setTitle("SkyDrop - Order Status");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Root screen (background + logo)
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        // Main info card
        int cw = 320, ch = 470, x = (W - cw) / 2, y = 240;
        InfoCard card = new InfoCard(22);
        card.setBounds(x, y, cw, ch);
        card.setLayout(null);
        root.add(card);

        // Header texts
        card.add(lbl("Hi", 0, 18, cw, 28, 26, Color.BLACK));
        card.add(lbl("Order ID (" + orderId + ")", 0, 50, cw, 20, 14, aB(170)));
        card.add(lbl(type + " • " + place + " • " + item, 16, 72, cw - 32, 18, 12, aB(140)));
        card.add(lbl("Status", 0, 105, cw, 34, 34, Color.BLACK));

        // Status labels (change during flow)
        status = lbl("Accepted", 0, 150, cw, 32, 28, Color.decode("#18A85B"));
        helper = lbl("Preparing your order...", 0, 186, cw, 20, 14, aB(160));
        card.add(status);
        card.add(helper);

        // Panels (rating / rejected)
        rateP = panel(cw);  rateP.setVisible(false);
        rejP  = panel(cw);  rejP.setVisible(false);
        card.add(rateP);
        card.add(rejP);

        // Rating UI
        rateP.add(lbl("Please rate order", 0, 0, cw, 20, 14, aB(160)));

        int starY = 35, s = 42, g = 10;
        int sx = (cw - ((s * 5) + (g * 4))) / 2;

        // Create 5 star buttons
        for (int i = 1; i <= 5; i++) {
            JButton b = star(i);
            b.setBounds(sx + (i - 1) * (s + g), starY, s, s);
            rateP.add(b);
        }

        // Rating text
        stars = lbl("", 0, 90, cw, 18, 12, aB(140));
        rateP.add(stars);

        // Submit rating button (hover only when enabled)
        send = btn("Submit", (cw - 160) / 2, 120, 160, 52);
        send.setEnabled(false);

        // Manual hover (only works after rating is chosen)
        send.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!send.isEnabled()) return;
                send.setBackground(BTN_HOVER);
                send.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!send.isEnabled()) return;
                send.setBackground(BTN_NORMAL);
                send.setForeground(Color.BLACK);
            }
        });

        send.addActionListener(e -> {
            if (rating == 0) return;
            JOptionPane.showMessageDialog(this, "Thanks! Rating sent: " + rating + " stars");
            new OrderTestScreen();
            dispose();
        });
        rateP.add(send);

        // Rejected UI
        rejMsg = lbl("", 16, 10, cw - 32, 60, 14, aB(170));
        rejP.add(rejMsg);

        RoundedButton newOrder = btn("New Order", (cw - 170) / 2, 95, 170, 55);
        newOrder.enableHover(Color.WHITE, Color.decode("#0092D9"));
        newOrder.addActionListener(e -> {
            new OrderTestScreen();
            dispose();
        });
        rejP.add(newOrder);

        // Status flow simulation
        boolean reject = false; // change to true to test reject flow

        later(1200, () -> {
            if (reject) rejected("Busy now — order rejected.");
            else set("Accepted", "#18A85B", "Preparing your order...");
        });

        later(3200, () -> {
            if (!reject) set("On the way", "#D38B00", "Drone is on the way...");
        });

        later(5200, () -> {
            if (!reject) {
                set("Delivered", "#18A85B", "Delivered successfully!");
                showRate();
            }
        });

        updateStars();
        setVisible(true);
    }

    // Small helper methods

    // Create label using project helper
    private JLabel lbl(String t, int x, int y, int w, int h, int s, Color c) {
        return createLabel(t, x, y, w, h,
                new Font("SansSerif", Font.BOLD, s),
                c, SwingConstants.CENTER);
    }

    // Semitransparent black color
    private Color aB(int a) {
        return new Color(0, 0, 0, a);
    }

    // Transparent panel used inside the card
    private JPanel panel(int w) {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 225, w, 210);
        return p;
    }

    // Rounded primary button (base style)
    private RoundedButton btn(String t, int x, int y, int w, int h) {
        RoundedButton b = new RoundedButton(t, 18);
        b.setBounds(x, y, w, h);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(BTN_NORMAL);
        b.setForeground(Color.BLACK);
        return b;
    }

    // Single star button (gray by default)
    private JButton star(int v) {
        JButton b = new JButton("★");
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(STAR_OFF);
        b.setFont(new Font("SansSerif", Font.BOLD, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            rating = v;
            updateStars();
        });
        return b;
    }

    // Run action after delay
    private void later(int ms, Runnable r) {
        Timer t = new Timer(ms, e -> r.run());
        t.setRepeats(false);
        t.start();
    }

    // Update status text and hide panels
    private void set(String s, String color, String h) {
        status.setText(s);
        status.setForeground(Color.decode(color));
        helper.setText(h);
        rateP.setVisible(false);
        rejP.setVisible(false);
    }

    // Show rating panel
    private void showRate() {
        rating = 0;
        rejP.setVisible(false);
        rateP.setVisible(true);

        // Reset submit button look
        send.setBackground(BTN_NORMAL);
        send.setForeground(Color.BLACK);

        updateStars();
    }

    // Show rejected panel
    private void rejected(String reason) {
        set("Rejected", "#FF4B4B", "Sorry, your order was rejected.");
        rateP.setVisible(false);
        rejP.setVisible(true);
        rejMsg.setText(
                "<html><div style='text-align:center;'>Order rejected.<br/>" +
                        "<span style='opacity:0.85;'>" + reason + "</span></div></html>"
        );
    }

    // Update stars colors and submit button state
    private void updateStars() {
        int i = 0;
        for (Component c : rateP.getComponents()) {
            if (c instanceof JButton) {
                i++;
                ((JButton) c).setForeground(i <= rating ? STAR_ON : STAR_OFF);
            }
        }

        stars.setText("Selected: " + rating + " / 5");

        // Enable submit only after choosing rating
        send.setEnabled(rating != 0);
        send.setText(rating == 0 ? "Submit" : "Submit (" + rating + "/5)");

        // Keep normal style when disabled
        if (rating == 0) {
            send.setBackground(BTN_NORMAL);
            send.setForeground(Color.BLACK);
        }
    }
}
