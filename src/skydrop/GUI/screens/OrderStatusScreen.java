package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.net.URL;

import skydrop.GUI.components.RoundedButton;

public class OrderStatusScreen extends JFrame {

    // screen size like mobile
    private static final int W = 375;
    private static final int H = 812;

    // images
    private Image bgImage;
    private Image originalLogo;

    // fallback background color if image not found
    private final Color bgFallback = Color.decode("#262525");

    // UI components
    private JLabel statusLabel;
    private JLabel helperLabel;

    // rating UI
    private JPanel ratingPanel;
    private JLabel starsLabel;
    private RoundedButton sendRatingBtn;

    // rejected UI
    private JPanel rejectedPanel;
    private JLabel rejectedMsgLabel;
    private RoundedButton newOrderBtn;

    // selected rating from 0 to 5
    private int rating = 0;

    public OrderStatusScreen(int orderId, String placeType, String placeName, String itemName) {

        // basic frame setup
        setTitle("SkyDrop - Order Status");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // root panel that draws background image
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(bgFallback);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };
        root.setBackground(bgFallback);
        setContentPane(root);

        // load background and logo from resources
        bgImage = loadImage("/Images/wallpaper.png");
        originalLogo = loadImage("/Images/skydrop logo.png");

        // logo label at the top
        int logoSize = 160;
        int logoYTop = 30;
        JLabel logoLabel = new JLabel();
        logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);
        if (originalLogo != null) {
            Image scaled = originalLogo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        }
        root.add(logoLabel);

        // glass card container for all texts and panels
        int cardX = 28;
        int cardY = logoYTop + logoSize + 45;
        int cardW = W - (cardX * 2);
        int cardH = 470;

        JPanel card = new GlassCardPanel(22, new Color(0, 0, 0, 135));
        card.setLayout(null);
        card.setBounds(cardX, cardY, cardW, cardH);
        root.add(card);

        // greeting title
        JLabel hi = new JLabel("Hi", SwingConstants.CENTER);
        hi.setBounds(0, 18, cardW, 28);
        hi.setForeground(Color.WHITE);
        hi.setFont(new Font("SansSerif", Font.BOLD, 26));
        card.add(hi);

        // show order id
        JLabel orderIdLabel = new JLabel("Order ID (" + orderId + ")", SwingConstants.CENTER);
        orderIdLabel.setBounds(0, 50, cardW, 20);
        orderIdLabel.setForeground(new Color(255, 255, 255, 220));
        orderIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        card.add(orderIdLabel);

        // show summary info
        JLabel summary = new JLabel(placeType + " • " + placeName + " • " + itemName, SwingConstants.CENTER);
        summary.setBounds(16, 72, cardW - 32, 18);
        summary.setForeground(new Color(255, 255, 255, 180));
        summary.setFont(new Font("SansSerif", Font.PLAIN, 12));
        card.add(summary);

        // status title
        JLabel statusTitle = new JLabel("Status", SwingConstants.CENTER);
        statusTitle.setBounds(0, 105, cardW, 34);
        statusTitle.setForeground(Color.WHITE);
        statusTitle.setFont(new Font("SansSerif", Font.BOLD, 34));
        card.add(statusTitle);

        // main status label
        statusLabel = new JLabel("Accepted", SwingConstants.CENTER);
        statusLabel.setBounds(0, 150, cardW, 32);
        statusLabel.setForeground(Color.decode("#7CFFB2"));
        statusLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        card.add(statusLabel);

        // helper text under status
        helperLabel = new JLabel("Preparing your order...", SwingConstants.CENTER);
        helperLabel.setBounds(0, 186, cardW, 20);
        helperLabel.setForeground(new Color(255, 255, 255, 185));
        helperLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        card.add(helperLabel);

        // rating panel hidden until delivered
        ratingPanel = new JPanel(null);
        ratingPanel.setOpaque(false);
        ratingPanel.setBounds(0, 225, cardW, 210);
        ratingPanel.setVisible(false);
        card.add(ratingPanel);

        // rating title
        JLabel rateLabel = new JLabel("Please rate order", SwingConstants.CENTER);
        rateLabel.setBounds(0, 0, cardW, 20);
        rateLabel.setForeground(new Color(255, 255, 255, 210));
        rateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        ratingPanel.add(rateLabel);

        // stars layout info
        int starY = 35;
        int starSize = 42;
        int gap = 10;
        int totalW = (starSize * 5) + (gap * 4);
        int sx = (cardW - totalW) / 2;

        // create 5 star buttons
        for (int i = 1; i <= 5; i++) {
            JButton starBtn = makeStarButton(i);
            starBtn.setBounds(sx + (i - 1) * (starSize + gap), starY, starSize, starSize);
            ratingPanel.add(starBtn);
        }

        // text that shows selected rating
        starsLabel = new JLabel("", SwingConstants.CENTER);
        starsLabel.setBounds(0, 90, cardW, 18);
        starsLabel.setForeground(new Color(255, 255, 255, 190));
        starsLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        ratingPanel.add(starsLabel);

        // submit button starts disabled until user picks stars
        sendRatingBtn = new RoundedButton("Submit", 18);
        sendRatingBtn.setBounds((cardW - 160) / 2, 120, 160, 52);
        sendRatingBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        sendRatingBtn.setEnabled(false);
        ratingPanel.add(sendRatingBtn);

        // when user clicks submit we validate then move to order test screen
        sendRatingBtn.addActionListener(e -> {
            if (rating == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please choose a rating first.",
                        "Missing Rating",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // API place to send rating to backend

            JOptionPane.showMessageDialog(this,
                    "Thanks! Rating sent: " + rating + " stars",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            new OrderTestScreen();
            dispose();
        });

        // rejected panel hidden unless rejected
        rejectedPanel = new JPanel(null);
        rejectedPanel.setOpaque(false);
        rejectedPanel.setBounds(0, 225, cardW, 210);
        rejectedPanel.setVisible(false);
        card.add(rejectedPanel);

        // rejected message
        rejectedMsgLabel = new JLabel("", SwingConstants.CENTER);
        rejectedMsgLabel.setBounds(16, 10, cardW - 32, 60);
        rejectedMsgLabel.setForeground(new Color(255, 255, 255, 220));
        rejectedMsgLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rejectedPanel.add(rejectedMsgLabel);

        // button to start new order after reject
        newOrderBtn = new RoundedButton("New Order", 18);
        newOrderBtn.setBounds((cardW - 170) / 2, 95, 170, 55);
        newOrderBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        rejectedPanel.add(newOrderBtn);

        newOrderBtn.addActionListener(e -> {
            new OrderTestScreen();
            dispose();
        });

        // start status changes simulation
        startFlowSimulation();

        setVisible(true);
    }

    // simulate status flow for UI testing
    private void startFlowSimulation() {
        boolean simulateReject = false; // change to true to test reject

        Timer t1 = new Timer(1200, e -> {
            if (simulateReject) {
                showRejected("Busy now — order rejected.");
            } else {
                setStatus("Accepted", "#7CFFB2", "Preparing your order...");
            }
        });
        t1.setRepeats(false);
        t1.start();

        Timer t2 = new Timer(3200, e -> {
            if (!simulateReject) {
                setStatus("On the way", "#FFD36E", "Drone is on the way...");
            }
        });
        t2.setRepeats(false);
        t2.start();

        Timer t3 = new Timer(5200, e -> {
            if (!simulateReject) {
                setStatus("Delivered", "#7CFFB2", "Delivered successfully!");
                showRating();
            }
        });
        t3.setRepeats(false);
        t3.start();

        // API place to track real order status
    }

    // update status labels and hide panels
    private void setStatus(String text, String hexColor, String helperText) {
        statusLabel.setText(text);
        statusLabel.setForeground(Color.decode(hexColor));
        helperLabel.setText(helperText);

        ratingPanel.setVisible(false);
        rejectedPanel.setVisible(false);
    }

    // show rating panel and reset rating
    private void showRating() {
        rejectedPanel.setVisible(false);
        ratingPanel.setVisible(true);
        rating = 0;
        updateStarsUI();
    }

    // show rejected panel with message
    private void showRejected(String reason) {
        setStatus("Rejected", "#FF6B6B", "Sorry, your order was rejected.");
        ratingPanel.setVisible(false);
        rejectedPanel.setVisible(true);

        rejectedMsgLabel.setText("<html><div style='text-align:center;'>"
                + "Order rejected.<br/>"
                + "<span style='opacity:0.85;'>" + reason + "</span>"
                + "</div></html>");
    }

    // create one star button that sets rating value
    private JButton makeStarButton(int value) {
        JButton b = new JButton("☆");
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(Color.decode("#FFD36E"));
        b.setFont(new Font("SansSerif", Font.BOLD, 30));
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));

        b.addActionListener((ActionEvent e) -> {
            rating = value;
            updateStarsUI();
        });

        return b;
    }

    // update stars visuals and enable submit button
    private void updateStarsUI() {
        Component[] comps = ratingPanel.getComponents();
        int starIndex = 0;

        for (Component c : comps) {
            if (c instanceof JButton) {
                starIndex++;
                JButton sb = (JButton) c;
                sb.setText(starIndex <= rating ? "★" : "☆");
            }
        }

        starsLabel.setText("Selected: " + rating + " / 5");

        if (rating == 0) {
            sendRatingBtn.setEnabled(false);
            sendRatingBtn.setText("Submit");
        } else {
            sendRatingBtn.setEnabled(true);
            sendRatingBtn.setText("Submit (" + rating + "/5)");
        }
    }

    // safely load images from resources folder
    private Image loadImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            return new ImageIcon(url).getImage();
        } catch (Exception ignored) {
            return null;
        }
    }

    // custom panel that draws rounded glass card with shadow
    static class GlassCardPanel extends JPanel {
        private final int radius;
        private final Color fill;

        public GlassCardPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // shadow behind card
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(6, 6, getWidth() - 12, getHeight() - 12, radius * 2, radius * 2);

            // main glass fill
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 12, radius * 2, radius * 2);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}