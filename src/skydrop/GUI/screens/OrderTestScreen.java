package skydrop.GUI.screens;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.InfoCard;
import skydrop.GUI.components.RoundedButton;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import static skydrop.GUI.components.Label.createLabel;

public class OrderTestScreen extends JFrame {

    // Screen size
    private static final int W = 375, H = 812;

    // Button colors
    private static final Color BTN_NORMAL = Color.WHITE;
    private static final Color BTN_HOVER  = Color.decode("#0092D9");

    // Demo data (type -> places -> items)
    private final Map<String, String[]> restaurants = new LinkedHashMap<>() {{
        put("Al Baik", new String[]{"Broast", "Spicy Broast", "Nuggets"});
        put("Kudu", new String[]{"Chicken Sandwich", "Burger", "Fries"});
    }};
    private final Map<String, String[]> cafes = new LinkedHashMap<>() {{
        put("Barn's", new String[]{"Latte", "Cappuccino"});
        put("Starbucks", new String[]{"Americano", "Matcha"});
    }};

    public OrderTestScreen() {

        // Frame setup
        setTitle("SkyDrop - Create Order");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Root screen (background + logo)
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        // Title texts
        root.add(createLabel("Create Order", 0, 175, W, 30,
                new Font("SansSerif", Font.BOLD, 20),
                Color.WHITE, SwingConstants.CENTER));

        root.add(createLabel("Choose type, place, then item", 0, 205, W, 18,
                new Font("SansSerif", Font.PLAIN, 12),
                new Color(255, 255, 255, 180),
                SwingConstants.CENTER));

        // Layout values
        int cw = 320, ch = 78, x = (W - cw) / 2, y = 250, g = 14;

        // Dropdowns (place/item start disabled)
        JComboBox<String> type  = box("Choose place type", "Restaurant", "Cafe");
        JComboBox<String> place = box("Choose place"); place.setEnabled(false);
        JComboBox<String> item  = box("Choose item");  item.setEnabled(false);

        // Cards that hold dropdowns
        root.add(card("Type",  x, y, cw, ch, type));
        root.add(card("Place", x, y + (ch + g), cw, ch, place));
        root.add(card("Item",  x, y + 2 * (ch + g), cw, ch, item));

        // Submit button (disabled until all selections are done)
        RoundedButton submit = new RoundedButton("Submit", 18);
        submit.setBounds((W - 160) / 2, H - 95, 160, 48);
        submit.setFont(new Font("SansSerif", Font.BOLD, 14));
        submit.setBackground(BTN_NORMAL);
        submit.setForeground(Color.BLACK);
        submit.setEnabled(false);

        // Hover effect (only when enabled)
        submit.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e){
                if (!submit.isEnabled()) return;
                submit.setBackground(BTN_HOVER);
                submit.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e){
                if (!submit.isEnabled()) return;
                submit.setBackground(BTN_NORMAL);
                submit.setForeground(Color.BLACK);
            }
        });

        root.add(submit);

        // When type changes: reset place + item, then enable place
        type.addActionListener(e -> {
            set(place, "Choose place"); place.setEnabled(false);
            set(item,  "Choose item");  item.setEnabled(false);

            String t = (String) type.getSelectedItem();
            if ("Restaurant".equals(t)) { set(place, list(restaurants)); place.setEnabled(true); }
            else if ("Cafe".equals(t))  { set(place, list(cafes));       place.setEnabled(true); }

            // Update submit button state
            updateSubmit(submit, type, place, item);
        });

        // When place changes: reset item, then enable item
        place.addActionListener(e -> {
            set(item, "Choose item"); item.setEnabled(false);

            String t = (String) type.getSelectedItem();
            String p = (String) place.getSelectedItem();
            if (p == null || p.startsWith("Choose")) {
                updateSubmit(submit, type, place, item);
                return;
            }

            String[] items = "Restaurant".equals(t) ? restaurants.get(p) : cafes.get(p);
            if (items != null) { set(item, list(items)); item.setEnabled(true); }

            // Update submit button state
            updateSubmit(submit, type, place, item);
        });

        // When item changes: enable submit if everything is selected
        item.addActionListener(e -> updateSubmit(submit, type, place, item));

        // Submit: validate selections then open status screen
        submit.addActionListener(e -> {
            String t  = (String) type.getSelectedItem();
            String p  = (String) place.getSelectedItem();
            String it = (String) item.getSelectedItem();

            if (bad(t) || bad(p) || bad(it)) {
                JOptionPane.showMessageDialog(this,
                        "Please complete: type, place, and item.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Open next screen with a demo order id
            new OrderStatusScreen(100 + new Random().nextInt(900), t, p, it);
            dispose();
        });

        setVisible(true);
    }

    // Enable submit only when type/place/item are valid
    private void updateSubmit(RoundedButton submit, JComboBox<String> type, JComboBox<String> place, JComboBox<String> item) {
        boolean ok = !bad((String) type.getSelectedItem())
                && !bad((String) place.getSelectedItem())
                && !bad((String) item.getSelectedItem());

        submit.setEnabled(ok);

        // Keep normal look when disabled
        if (!ok) {
            submit.setBackground(BTN_NORMAL);
            submit.setForeground(Color.BLACK);
        }
    }

    // Small check for default choices
    private boolean bad(String s) { return s == null || s.startsWith("Choose"); }

    // Create a simple combo box style
    private JComboBox<String> box(String... items) {
        JComboBox<String> b = new JComboBox<>(items);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setFocusable(false);
        b.setBackground(Color.WHITE);
        return b;
    }

    // Wrap dropdown inside an InfoCard
    private InfoCard card(String title, int x, int y, int w, int h, JComboBox<String> box) {
        InfoCard c = new InfoCard(18);
        c.setBounds(x, y, w, h);
        c.setLayout(null);
        c.addSubtitle(title, 14, 8, 120, 18);
        box.setBounds(14, 30, w - 28, 36);
        c.add(box);
        return c;
    }

    // Replace combo items 
    private void set(JComboBox<String> b, String first) {
        b.setModel(new DefaultComboBoxModel<>(new String[]{first}));
    }

    // Replace combo items (full list)
    private void set(JComboBox<String> b, String[] items) {
        b.setModel(new DefaultComboBoxModel<>(items));
    }

    // Build list of places from a map
    private String[] list(Map<String, String[]> m) {
        String[] a = new String[m.size() + 1];
        a[0] = "Choose place";
        int i = 1;
        for (String k : m.keySet()) a[i++] = k;
        return a;
    }

    // Build list of items from array
    private String[] list(String[] items) {
        String[] a = new String[items.length + 1];
        a[0] = "Choose item";
        System.arraycopy(items, 0, a, 1, items.length);
        return a;
    }
}
