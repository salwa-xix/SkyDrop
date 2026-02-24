package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import skydrop.GUI.components.RoundedButton;

public class OrderTestScreen extends JFrame {

    // screen size like mobile
    private static final int W = 375;
    private static final int H = 812;

    // background and logo images
    private Image bgImage;
    private Image originalLogo;

    // fallback color if background image not found
    private final Color bgFallback = Color.decode("#262525");

    // local demo data for restaurants and cafes
    private final Map<String, String[]> restaurants = new LinkedHashMap<>();
    private final Map<String, String[]> cafes = new LinkedHashMap<>();

    public OrderTestScreen() {

        // frame basic setup
        setTitle("SkyDrop - Create Order");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // fill demo data
        seedData();

        // root panel draws background
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();

                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(bgFallback);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
            }
        };
        setContentPane(root);

        // load images from resources
        bgImage = loadImage("/Images/wallpaper.png");
        originalLogo = loadImage("/Images/skydrop logo.png");

        // logo at the top
        int logoSize = 170;
        int logoYTop = 35;

        JLabel logoLabel = new JLabel();
        logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);

        if (originalLogo != null) {
            logoLabel.setIcon(new ImageIcon(
                    originalLogo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH)
            ));
        }
        root.add(logoLabel);

        // title card panel
        int titleY = logoYTop + logoSize + 45;

        JPanel titleCard = new RoundedGlassPanel(20, new Color(0, 0, 0, 130));
        titleCard.setBounds(30, titleY, W - 60, 90);
        titleCard.setLayout(null);
        root.add(titleCard);

        // title text
        JLabel title = new JLabel("Create Order", SwingConstants.CENTER);
        title.setBounds(0, 15, W - 60, 30);
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        titleCard.add(title);

        // subtitle text
        JLabel subtitle = new JLabel("Choose type, place, then item", SwingConstants.CENTER);
        subtitle.setBounds(0, 50, W - 60, 18);
        subtitle.setForeground(new Color(255, 255, 255, 180));
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        titleCard.add(subtitle);

        // form layout values
        int formX = 40;
        int fieldW = W - 80;
        int fieldH = 55;
        int formY = titleY + 125;

        // dropdown for type
        RoundedComboBox typeBox = new RoundedComboBox(
                new String[]{"Choose place type", "Restaurant", "Cafe"}, 18
        );
        typeBox.setBounds(formX, formY, fieldW, fieldH);
        root.add(typeBox);

        // dropdown for place disabled until type chosen
        RoundedComboBox placeBox = new RoundedComboBox(
                new String[]{"Choose place"}, 18
        );
        placeBox.setBounds(formX, formY + 80, fieldW, fieldH);
        placeBox.setEnabled(false);
        root.add(placeBox);

        // dropdown for item disabled until place chosen
        RoundedComboBox itemBox = new RoundedComboBox(
                new String[]{"Choose item"}, 18
        );
        itemBox.setBounds(formX, formY + 160, fieldW, fieldH);
        itemBox.setEnabled(false);
        root.add(itemBox);

        // submit button
        RoundedButton submitBtn = new RoundedButton("Submit", 18);
        submitBtn.setBounds((W - fieldW / 2) / 2, formY + 260, fieldW / 2, 55);
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        submitBtn.setBackground(Color.WHITE);
        submitBtn.setForeground(Color.BLACK);
        submitBtn.setFocusPainted(false);
        root.add(submitBtn);

        // simple pressed effect colors
        Color normal = Color.WHITE;
        Color pressed = new Color(120, 190, 255);

        // change color when button is pressed
        submitBtn.getModel().addChangeListener(e -> {
            ButtonModel model = submitBtn.getModel();
            if (model.isPressed()) {
                submitBtn.setBackground(pressed);
            } else {
                submitBtn.setBackground(normal);
            }
        });

        // when type changes we reset and fill places
        typeBox.addActionListener(e -> {
            placeBox.setModel(new DefaultComboBoxModel<>(new String[]{"Choose place"}));
            itemBox.setModel(new DefaultComboBoxModel<>(new String[]{"Choose item"}));
            placeBox.setEnabled(false);
            itemBox.setEnabled(false);

            String type = (String) typeBox.getSelectedItem();
            if ("Restaurant".equals(type)) {
                placeBox.setModel(new DefaultComboBoxModel<>(buildPlacesList(restaurants)));
                placeBox.setEnabled(true);
            }
            if ("Cafe".equals(type)) {
                placeBox.setModel(new DefaultComboBoxModel<>(buildPlacesList(cafes)));
                placeBox.setEnabled(true);
            }

        });

        // when place changes we fill items for that place
        placeBox.addActionListener(e -> {
            itemBox.setModel(new DefaultComboBoxModel<>(new String[]{"Choose item"}));
            itemBox.setEnabled(false);

            String type = (String) typeBox.getSelectedItem();
            String place = (String) placeBox.getSelectedItem();
            if (place == null || place.startsWith("Choose")) return;

            String[] items =
                    "Restaurant".equals(type) ? restaurants.get(place) : cafes.get(place);

            if (items != null) {
                itemBox.setModel(new DefaultComboBoxModel<>(buildItemsList(items)));
                itemBox.setEnabled(true);
            }

        });

        // submit creates an order and opens status screen
        submitBtn.addActionListener(e -> {
            String type = (String) typeBox.getSelectedItem();
            String place = (String) placeBox.getSelectedItem();
            String item = (String) itemBox.getSelectedItem();

            if (type.startsWith("Choose") || place.startsWith("Choose") || item.startsWith("Choose")) {
                JOptionPane.showMessageDialog(this,
                        "Please complete: type, place, and item.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }


            new OrderStatusScreen(generateOrderId(), type, place, item);
            dispose();
        });

        setVisible(true);
    }

    // fill sample data for demo
    private void seedData() {
        restaurants.put("Al Baik", new String[]{"Broast", "Spicy Broast", "Nuggets"});
        restaurants.put("Kudu", new String[]{"Chicken Sandwich", "Burger", "Fries"});
        cafes.put("Barn's", new String[]{"Latte", "Cappuccino"});
        cafes.put("Starbucks", new String[]{"Americano", "Matcha"});
    }

    // build list of places for combo box
    private String[] buildPlacesList(Map<String, String[]> map) {
        String[] arr = new String[map.size() + 1];
        arr[0] = "Choose place";
        int i = 1;
        for (String k : map.keySet()) arr[i++] = k;
        return arr;
    }

    // build list of items for combo box
    private String[] buildItemsList(String[] items) {
        String[] arr = new String[items.length + 1];
        arr[0] = "Choose item";
        System.arraycopy(items, 0, arr, 1, items.length);
        return arr;
    }

    // generate random order id for demo only
    private int generateOrderId() {
        return 100 + new Random().nextInt(900);
    }

    // load image safely from resources
    private Image loadImage(String path) {
        try {
            URL url = getClass().getResource(path);
            if (url == null) return null;
            return new ImageIcon(url).getImage();
        } catch (Exception e) {
            return null;
        }
    }

    // combo box with rounded white background
    static class RoundedComboBox extends JComboBox<String> {
        private final int radius;

        public RoundedComboBox(String[] items, int radius) {
            super(items);
            this.radius = radius;
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // rounded glass panel used for title card
    static class RoundedGlassPanel extends JPanel {
        private final int radius;
        private final Color fill;

        public RoundedGlassPanel(int radius, Color fill) {
            this.radius = radius;
            this.fill = fill;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}