package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import skydrop.app.SkyDropClient;
import skydrop.app.User;

import static skydrop.GUI.components.Label.createLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

public class SignInScreen extends JFrame {

    private static final int W = 375, H = 812;

    public SignInScreen() {

        // Setup the frame
        setTitle("SkyDrop - Sign In");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Create the main screen with background and logo
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        int cw = 295, ch = 50, x = (W - cw) / 2, y = 300, g = 20;

        // Phone input
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(x, y, cw, ch);
        root.add(phoneField);

        // Password input
        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(x, y + (ch + g), cw, ch);
        root.add(passField);

        int bw = cw / 2, bh = 55, bx = (W - bw) / 2, by = y + 2 * (ch + g) + 20;

        RoundedButton signInButton = new RoundedButton("Sign In", 18);
        signInButton.setBounds(bx, by, bw, bh);
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(signInButton);

        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        signInButton.setBackground(normalBg);
        signInButton.setForeground(normalFg);

        // Change button color when all required fields are filled
        Runnable updateButtonState = () -> {
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();

            boolean phoneValid = !phone.isEmpty() && !phoneField.isPlaceholderActive();
            boolean passValid = !pass.isEmpty() && !passField.isPlaceholderActive();

            if (phoneValid && passValid) {
                signInButton.setBackground(activeBg);
                signInButton.setForeground(activeFg);
            } else {
                signInButton.setBackground(normalBg);
                signInButton.setForeground(normalFg);
            }
        };

        // Listen to phone field changes
        phoneField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        });

        // Listen to password field changes
        passField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        });

        root.add(createLabel("or", 0, by + bh + 18, W, 20,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE,
                SwingConstants.CENTER));

        JLabel signUpLabel = createLabel("<html><u>Sign up</u></html>", 0, by + bh + 43, W, 25,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE,
                SwingConstants.CENTER);

        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        root.add(signUpLabel);

        // Open Sign Up screen
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                SignUpScreen screen = new SignUpScreen();
                screen.setLocation(SignInScreen.this.getLocation());

                dispose();
            }
        });

        // Send login request to the backend
        signInButton.addActionListener(e -> {

            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();

            // Remove placeholder values
            if (phoneField.isPlaceholderActive()) phone = "";
            if (passField.isPlaceholderActive()) pass = "";

            // Validate required fields
            if (phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter phone number and password.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prevent breaking the request format
            if (phone.contains("|") || pass.contains("|")) {
                JOptionPane.showMessageDialog(this,
                        "Please do not use the | symbol.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Build the request using the format expected by ClientHandler
            String request = "LOGIN|" + phone + "|" + pass;

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

            /*
             Expected success format:
             SUCCESS|name|phone|district
            */
            if (response.startsWith("SUCCESS|")) {

                String[] parts = response.split("\\|", -1);

                // Make sure the response has all user data
                if (parts.length < 4) {
                    JOptionPane.showMessageDialog(this,
                            "Invalid login response from server.",
                            "Login Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String name = parts[1];
                String userPhone = parts[2];
                String district = parts[3];

                // Create user object to pass it to the next GUI screen
                User user = new User(name, userPhone, pass, district);

                JOptionPane.showMessageDialog(this,
                        "Login successful!");

                OrderTestScreen screen = new OrderTestScreen(user);
                screen.setLocation(this.getLocation());
                dispose();

            } else if (response.equals("FAIL")) {

                JOptionPane.showMessageDialog(this,
                        "Wrong phone number or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);

            } else {

                JOptionPane.showMessageDialog(this,
                        "Login failed.\nServer response: " + response,
                        "Login Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        getRootPane().setDefaultButton(signInButton);

        setVisible(true);

        // Remove focus from fields when screen opens
        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }
}