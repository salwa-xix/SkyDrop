package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;
import skydrop.app.SkyDropClient;
import skydrop.app.User;

import javax.swing.*;
import java.awt.*;

import static skydrop.GUI.components.Label.createLabel;

public class OrderStatusScreen extends JFrame {

    private User currentUser;
    private int orderId;

    private static final int W = 375, H = 812;

    private static final Color STAR_OFF = new Color(170, 170, 170);
    private static final Color STAR_ON = Color.decode("#FFD36E");

    private static final Color BTN_NORMAL = Color.WHITE;
    private static final Color BTN_HOVER = Color.decode("#0092D9");

    private JLabel status, helper, stars, rejMsg;
    private JPanel rateP, rejP;
    private RoundedButton send;
    private int rating = 0;

    public OrderStatusScreen(int orderId, String type, String place, String item, User user) {

        this.currentUser = user;
        this.orderId = orderId;

        // Setup the frame
        setTitle("SkyDrop - Order Status");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        int cw = 320, ch = 470, x = (W - cw) / 2, y = 240;

        InfoCard card = new InfoCard(22);
        card.setBounds(x, y, cw, ch);
        card.setLayout(null);
        root.add(card);

        // Show order basic info
        card.add(lbl("Hi", 0, 18, cw, 28, 26, Color.BLACK));
        card.add(lbl("Order ID (" + orderId + ")", 0, 50, cw, 20, 14, aB(170)));
        card.add(lbl(type + " • " + place + " • " + item, 16, 72, cw - 32, 18, 12, aB(140)));
        card.add(lbl("Status", 0, 105, cw, 34, 34, Color.BLACK));

        status = lbl("Accepted", 0, 150, cw, 32, 28, Color.decode("#18A85B"));
        helper = lbl("Preparing your order...", 0, 186, cw, 20, 14, aB(160));
        card.add(status);
        card.add(helper);

        // Panels for rating and rejected order
        rateP = panel(cw);
        rejP = panel(cw);
        rateP.setVisible(false);
        rejP.setVisible(false);
        card.add(rateP);
        card.add(rejP);

        buildRatingPanel(cw);
        buildRejectedPanel(cw);

        // Ask the backend for order status every second
        Timer timer = new Timer(1000, e -> {

            // GUI talks only to SkyDropClient
            String response = SkyDropClient.sendRequest("GET_STATUS|" + orderId);

            if (response == null || !response.startsWith("STATUS|")) {
                return;
            }

            String currentStatus = response.split("\\|", -1)[1];

            switch (currentStatus) {
                case "Waiting":
                    setStatus("Waiting", "#888888", "Waiting for available drone...");
                    break;

                case "Accepted":
                    setStatus("Accepted", "#18A85B", "Preparing your order...");
                    break;

                case "On the way":
                    setStatus("On the way", "#D38B00", "Drone is on the way...");
                    break;

                case "Delivered":
                    setStatus("Delivered", "#18A85B", "Delivered successfully!");
                    showRate();
                    ((Timer) e.getSource()).stop();
                    break;

                case "Rejected":
                    showRejected("Bad weather conditions.");
                    ((Timer) e.getSource()).stop();
                    break;

                default:
                    setStatus(currentStatus, "#888888", "Checking order status...");
                    break;
            }
        });

        timer.start();

        updateStars();
        setVisible(true);
    }

    private void buildRatingPanel(int cw) {

        // Rating title
        rateP.add(lbl("Please rate order", 0, 0, cw, 20, 14, aB(160)));

        int starY = 35, s = 42, g = 10;
        int sx = (cw - ((s * 5) + (g * 4))) / 2;

        // Create 5 star buttons
        for (int i = 1; i <= 5; i++) {
            JButton b = star(i);
            b.setBounds(sx + (i - 1) * (s + g), starY, s, s);
            rateP.add(b);
        }

        stars = lbl("", 0, 90, cw, 18, 12, aB(140));
        rateP.add(stars);

        send = btn("Submit", (cw - 160) / 2, 120, 160, 52);
        send.setEnabled(false);

        // Hover effect for submit button
        send.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!send.isEnabled()) return;
                send.setBackground(BTN_HOVER);
                send.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!send.isEnabled()) return;
                send.setBackground(BTN_NORMAL);
                send.setForeground(Color.BLACK);
            }
        });

        // Save rating through the server
        send.addActionListener(e -> {

            if (rating == 0) return;

            // Send rating to backend using SkyDropClient only
            String response = SkyDropClient.sendRequest(
                    "SAVE_RATING|" + orderId + "|" + rating
            );

            if (response != null && response.equals("RATING_SAVED")) {
                JOptionPane.showMessageDialog(this,
                        "Thanks! Rating sent: " + rating + " stars");

                OrderTestScreen screen = new OrderTestScreen(currentUser);
                screen.setLocation(this.getLocation());
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not save rating.\nPlease make sure the server is running.",
                        "Rating Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        rateP.add(send);
    }

    private void buildRejectedPanel(int cw) {

        // Message shown when order is rejected
        rejMsg = lbl("", 16, 10, cw - 32, 60, 14, aB(170));
        rejP.add(rejMsg);

        RoundedButton newOrder = btn("New Order", (cw - 170) / 2, 95, 170, 55);
        newOrder.enableHover(Color.WHITE, Color.decode("#0092D9"));

        newOrder.addActionListener(e -> {
            OrderTestScreen screen = new OrderTestScreen(currentUser);
            screen.setLocation(this.getLocation());
            dispose();
        });

        rejP.add(newOrder);
    }

    private JLabel lbl(String t, int x, int y, int w, int h, int s, Color c) {
        return createLabel(t, x, y, w, h,
                new Font("SansSerif", Font.BOLD, s),
                c, SwingConstants.CENTER);
    }

    private Color aB(int a) {
        return new Color(0, 0, 0, a);
    }

    private JPanel panel(int w) {
        JPanel p = new JPanel(null);
        p.setOpaque(false);
        p.setBounds(0, 225, w, 210);
        return p;
    }

    private RoundedButton btn(String t, int x, int y, int w, int h) {
        RoundedButton b = new RoundedButton(t, 18);
        b.setBounds(x, y, w, h);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(BTN_NORMAL);
        b.setForeground(Color.BLACK);
        return b;
    }

    private JButton star(int v) {
        JButton b = new JButton("★");
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(STAR_OFF);
        b.setFont(new Font("SansSerif", Font.BOLD, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Save selected rating
        b.addActionListener(e -> {
            rating = v;
            updateStars();
        });

        return b;
    }

    private void setStatus(String s, String color, String h) {
        status.setText(s);
        status.setForeground(Color.decode(color));
        helper.setText(h);
        rateP.setVisible(false);
        rejP.setVisible(false);
    }

    private void showRate() {
        rating = 0;
        rejP.setVisible(false);
        rateP.setVisible(true);

        send.setBackground(BTN_NORMAL);
        send.setForeground(Color.BLACK);

        updateStars();
    }

    private void showRejected(String reason) {
        setStatus("Rejected", "#FF4B4B", "Sorry, your order was rejected.");
        rateP.setVisible(false);
        rejP.setVisible(true);

        rejMsg.setText(
                "<html><div style='text-align:center;'>Order rejected.<br/>" +
                        reason +
                        "</div></html>"
        );
    }

    private void updateStars() {
        int i = 0;

        // Update star colors based on selected rating
        for (Component c : rateP.getComponents()) {
            if (c instanceof JButton) {
                i++;
                ((JButton) c).setForeground(i <= rating ? STAR_ON : STAR_OFF);
            }
        }

        stars.setText("Selected: " + rating + " / 5");

        send.setEnabled(rating != 0);
        send.setText(rating == 0 ? "Submit" : "Submit (" + rating + "/5)");

        if (rating == 0) {
            send.setBackground(BTN_NORMAL);
            send.setForeground(Color.BLACK);
        }
    }
}