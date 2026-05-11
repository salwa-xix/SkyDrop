package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import skydrop.app.SkyDropClient;
import skydrop.app.User;

import static skydrop.GUI.components.Label.createLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

public class SignUpScreen extends JFrame {

    private static final int W = 375, H = 812;

    public SignUpScreen() {

        // Setup the frame
        setTitle("SkyDrop - Sign Up");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background and logo
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        int cw = 295, ch = 50, x = (W - cw) / 2, y = 240, g = 20;

        // Name field
        RoundedInputField nameField = new RoundedInputField("Name", 18, false);
        nameField.setBounds(x, y, cw, ch);
        root.add(nameField);

        // Phone field
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(x, y + (ch + g), cw, ch);
        root.add(phoneField);

        // District dropdown
        String[] jeddahDistricts = {
                "Your District",
                "Al Rawdah",
                "Al Naeem",
                "Al Hamra",
                "Al Salamah",
                "Al Rehab"
        };

        RoundedComboBox districtBox = new RoundedComboBox(jeddahDistricts, 18);
        districtBox.setBounds(x, y + 2 * (ch + g), cw, ch);
        root.add(districtBox);

        // Password field
        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(x, y + 3 * (ch + g), cw, ch);
        root.add(passField);

        int bw = cw / 2, bh = 55, bx = (W - bw) / 2, by = y + 4 * (ch + g) + 20;

        RoundedButton signUpButton = new RoundedButton("Sign Up", 18);
        signUpButton.setBounds(bx, by, bw, bh);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(signUpButton);

        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        signUpButton.setBackground(normalBg);
        signUpButton.setForeground(normalFg);

        // Change button color when all inputs are filled
        Runnable updateButtonState = () -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();
            String district = (String) districtBox.getSelectedItem();

            boolean nameValid = !name.isEmpty() && !nameField.isPlaceholderActive();
            boolean phoneValid = !phone.isEmpty() && !phoneField.isPlaceholderActive();
            boolean passValid = !pass.isEmpty() && !passField.isPlaceholderActive();
            boolean districtValid = district != null && !district.equals("Your District");

            if (nameValid && phoneValid && passValid && districtValid) {
                signUpButton.setBackground(activeBg);
                signUpButton.setForeground(activeFg);
            } else {
                signUpButton.setBackground(normalBg);
                signUpButton.setForeground(normalFg);
            }
        };

        // Listen to text changes
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        };

        nameField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        passField.getDocument().addDocumentListener(dl);
        districtBox.addActionListener(e -> updateButtonState.run());

        root.add(createLabel("or", 0, by + bh + 18, W, 20,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE, SwingConstants.CENTER));

        JLabel signInLabel = createLabel("<html><u>Sign in</u></html>", 0, by + bh + 43, W, 25,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE, SwingConstants.CENTER);

        signInLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        root.add(signInLabel);

        // Open Sign In screen
        signInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                SignInScreen screen = new SignInScreen();
                screen.setLocation(SignUpScreen.this.getLocation());
                dispose();
            }
        });

        // Send sign up request to the backend
        signUpButton.addActionListener(e -> {

            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();
            String district = (String) districtBox.getSelectedItem();

            // Remove placeholder values
            if (nameField.isPlaceholderActive()) name = "";
            if (phoneField.isPlaceholderActive()) phone = "";
            if (passField.isPlaceholderActive()) pass = "";

            boolean districtValid = district != null && !district.equals("Your District");

            // Validate required fields
            if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || !districtValid) {
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields and select your district.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prevent breaking the request format
            if (name.contains("|") || phone.contains("|") || pass.contains("|") || district.contains("|")) {
                JOptionPane.showMessageDialog(this,
                        "Please do not use the | symbol.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build the request using the format expected by ClientHandler
            String request = "SIGNUP|" + name + "|" + phone + "|" + pass + "|" + district;

            // Send the request to SkyDropServer through SkyDropClient
            String response = SkyDropClient.sendRequest(request);

            // Server is not running or connection failed
            if (response == null) {
                JOptionPane.showMessageDialog(this,
                        "Cannot connect to the server.\nPlease run SkyDropServer first.",
                        "Connection Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Account created successfully
            if (response.equals("SIGNUP_SUCCESS")) {

                User user = new User(name, phone, pass, district);

                JOptionPane.showMessageDialog(this,
                        "Account created successfully!");

                OrderTestScreen screen = new OrderTestScreen(user);
                screen.setLocation(SignUpScreen.this.getLocation());
                dispose();

            }
            // Phone number already exists in database
            else if (response.equals("USER_EXISTS")) {
                JOptionPane.showMessageDialog(this,
                        "This phone number is already registered.",
                        "Sign Up Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
            // Any unexpected response from server
            else {
                JOptionPane.showMessageDialog(this,
                        "Sign up failed.\nServer response: " + response,
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        getRootPane().setDefaultButton(signUpButton);

        setVisible(true);

        // Remove focus from fields when screen opens
        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }

    static class RoundedComboBox extends JComboBox<String> {

        private final int radius;

        public RoundedComboBox(String[] items, int radius) {
            super(items);
            this.radius = radius;

            setOpaque(false);
            setBackground(Color.WHITE);
            setForeground(Color.BLACK);
            setFont(new Font("SansSerif", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);

            g2.dispose();
            super.paintComponent(g);
        }
    }
}