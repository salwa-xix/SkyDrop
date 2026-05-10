package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;
import skydrop.app.SkyDropClient;
import skydrop.app.User;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static skydrop.GUI.components.Label.createLabel;

public class OrderTestScreen extends JFrame {

    private static final int W = 375, H = 812;

    private static final Color BTN_NORMAL = Color.WHITE;
    private static final Color BTN_HOVER = Color.decode("#0092D9");

    // Static GUI data because places/items are not stored in database
    private final Map<String, String[]> restaurants = new LinkedHashMap<>() {{
        put("Al Baik", new String[]{"Broast", "Spicy Broast", "Nuggets"});
        put("Kudu", new String[]{"Chicken Sandwich", "Burger", "Fries"});
    }};

    private final Map<String, String[]> cafes = new LinkedHashMap<>() {{
        put("Barn's", new String[]{"Latte", "Cappuccino"});
        put("Starbucks", new String[]{"Americano", "Matcha"});
    }};

    private final User currentUser;

    public OrderTestScreen(User user) {

        this.currentUser = user;

        // Setup the frame
        setTitle("SkyDrop - Create Order");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        root.add(createLabel("Create Order", 0, 175, W, 30,
                new Font("SansSerif", Font.BOLD, 20),
                Color.WHITE,
                SwingConstants.CENTER));

        root.add(createLabel("Choose type, place, then item", 0, 205, W, 18,
                new Font("SansSerif", Font.PLAIN, 12),
                new Color(255, 255, 255, 180),
                SwingConstants.CENTER));

        int cw = 320, ch = 78, x = (W - cw) / 2, y = 250, g = 14;

        // Dropdowns
        JComboBox<String> typeBox = box("Choose place type", "Restaurant", "Cafe");
        JComboBox<String> placeBox = box("Choose place");
        JComboBox<String> itemBox = box("Choose item");

        placeBox.setEnabled(false);
        itemBox.setEnabled(false);

        root.add(card("Type", x, y, cw, ch, typeBox));
        root.add(card("Place", x, y + (ch + g), cw, ch, placeBox));
        root.add(card("Item", x, y + 2 * (ch + g), cw, ch, itemBox));

        RoundedButton submitButton = new RoundedButton("Submit", 18);
        submitButton.setBounds((W - 160) / 2, H - 95, 160, 48);
        submitButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        submitButton.setBackground(BTN_NORMAL);
        submitButton.setForeground(Color.BLACK);
        submitButton.setEnabled(false);
        root.add(submitButton);

        // Change button color when mouse enters/exits
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!submitButton.isEnabled()) return;
                submitButton.setBackground(BTN_HOVER);
                submitButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!submitButton.isEnabled()) return;
                submitButton.setBackground(BTN_NORMAL);
                submitButton.setForeground(Color.BLACK);
            }
        });

        // When type changes, load places
        typeBox.addActionListener(e -> {

            set(placeBox, "Choose place");
            set(itemBox, "Choose item");

            placeBox.setEnabled(false);
            itemBox.setEnabled(false);

            String selectedType = (String) typeBox.getSelectedItem();

            if ("Restaurant".equals(selectedType)) {
                set(placeBox, listPlaces(restaurants));
                placeBox.setEnabled(true);
            } else if ("Cafe".equals(selectedType)) {
                set(placeBox, listPlaces(cafes));
                placeBox.setEnabled(true);
            }

            updateSubmit(submitButton, typeBox, placeBox, itemBox);
        });

        // When place changes, load items
        placeBox.addActionListener(e -> {

            set(itemBox, "Choose item");
            itemBox.setEnabled(false);

            String selectedType = (String) typeBox.getSelectedItem();
            String selectedPlace = (String) placeBox.getSelectedItem();

            if (bad(selectedPlace)) {
                updateSubmit(submitButton, typeBox, placeBox, itemBox);
                return;
            }

            String[] items;

            if ("Restaurant".equals(selectedType)) {
                items = restaurants.get(selectedPlace);
            } else {
                items = cafes.get(selectedPlace);
            }

            if (items != null) {
                set(itemBox, listItems(items));
                itemBox.setEnabled(true);
            }

            updateSubmit(submitButton, typeBox, placeBox, itemBox);
        });

        // When item changes, check if submit can be enabled
        itemBox.addActionListener(e ->
                updateSubmit(submitButton, typeBox, placeBox, itemBox)
        );

        // Create order through backend
        submitButton.addActionListener(e -> {

            String selectedType = (String) typeBox.getSelectedItem();
            String selectedPlace = (String) placeBox.getSelectedItem();
            String selectedItem = (String) itemBox.getSelectedItem();

            // Validate selections
            if (bad(selectedType) || bad(selectedPlace) || bad(selectedItem)) {
                JOptionPane.showMessageDialog(this,
                        "Please complete: type, place, and item.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prevent breaking the request format
            if (selectedType.contains("|") || selectedPlace.contains("|") || selectedItem.contains("|")) {
                JOptionPane.showMessageDialog(this,
                        "Please do not use the | symbol.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            /*
             Expected request format:
             CREATE_ORDER|phone|type|place|item|district
            */
            String request =
                    "CREATE_ORDER|" +
                            currentUser.getPhone() + "|" +
                            selectedType + "|" +
                            selectedPlace + "|" +
                            selectedItem + "|" +
                            currentUser.getDistrict();

            // Send request to SkyDropServer through SkyDropClient
            String response = SkyDropClient.sendRequest(request);

            if (response == null) {
                JOptionPane.showMessageDialog(this,
                        "Cannot connect to the server.\nPlease run SkyDropServer first.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            /*
             Expected success response:
             ORDER_CREATED|orderId
            */
            if (response.startsWith("ORDER_CREATED|")) {

                String[] parts = response.split("\\|", -1);

                if (parts.length < 2) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid order response from server.",
                            "Order Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int orderId;

                try {
                    orderId = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid order ID from server.",
                            "Order Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Open status screen with the created order ID
                OrderStatusScreen screen =
                        new OrderStatusScreen(orderId, selectedType, selectedPlace, selectedItem, currentUser);

                screen.setLocation(this.getLocation());
                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Could not create order.\nServer response: " + response,
                        "Order Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    // Enable submit only when all dropdowns are selected
    private void updateSubmit(RoundedButton submit,
                              JComboBox<String> type,
                              JComboBox<String> place,
                              JComboBox<String> item) {

        boolean ok =
                !bad((String) type.getSelectedItem()) &&
                        !bad((String) place.getSelectedItem()) &&
                        !bad((String) item.getSelectedItem());

        submit.setEnabled(ok);

        if (!ok) {
            submit.setBackground(BTN_NORMAL);
            submit.setForeground(Color.BLACK);
        }
    }

    // Check if combo box still has default value
    private boolean bad(String value) {
        return value == null || value.startsWith("Choose");
    }

    // Create dropdown
    private JComboBox<String> box(String... items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        comboBox.setFocusable(false);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    // Put dropdown inside a card
    private InfoCard card(String title, int x, int y, int w, int h, JComboBox<String> comboBox) {
        InfoCard card = new InfoCard(18);
        card.setBounds(x, y, w, h);
        card.setLayout(null);

        card.addSubtitle(title, 14, 8, 120, 18);

        comboBox.setBounds(14, 30, w - 28, 36);
        card.add(comboBox);

        return card;
    }

    // Reset combo box with one default item
    private void set(JComboBox<String> comboBox, String firstItem) {
        comboBox.setModel(new DefaultComboBoxModel<>(new String[]{firstItem}));
    }

    // Replace combo box items
    private void set(JComboBox<String> comboBox, String[] items) {
        comboBox.setModel(new DefaultComboBoxModel<>(items));
    }

    // Convert restaurant/cafe map keys into place list
    private String[] listPlaces(Map<String, String[]> map) {
        String[] places = new String[map.size() + 1];
        places[0] = "Choose place";

        int i = 1;
        for (String place : map.keySet()) {
            places[i++] = place;
        }

        return places;
    }

    // Convert items array into combo box list
    private String[] listItems(String[] items) {
        String[] result = new String[items.length + 1];
        result[0] = "Choose item";

        System.arraycopy(items, 0, result, 1, items.length);

        return result;
    }
}