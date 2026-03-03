package skydrop.GUI.screens;

import skydrop.GUI.components.*;
import static skydrop.GUI.components.Label.createLabel;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

public class SignInScreen extends JFrame {

    // Screen size
    private static final int W = 375, H = 812;

    public SignInScreen() {

        // Frame setup
        setTitle("SkyDrop - Sign In");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        // Root screen (background + logo)
        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        // Layout values
        int cw = 295, ch = 50, x = (W - cw) / 2, y = 300, g = 20;

        // Create phone number input field with rounded style and placeholder support
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(x, y, cw, ch);
        root.add(phoneField);

        // Create password input field with masking enabled
        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(x, y + (ch + g), cw, ch);
        root.add(passField);

        // Create and center the Sign In button
        int bw = cw / 2, bh = 55, bx = (W - bw) / 2, by = y + 2 * (ch + g) + 20;

        RoundedButton signInButton = new RoundedButton("Sign In", 18);
        signInButton.setBounds(bx, by, bw, bh);
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(signInButton);

        // Define normal and active button colors
        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        signInButton.setBackground(normalBg);
        signInButton.setForeground(normalFg);

        // Update button appearance only when both fields contain valid input
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

        // Attach listeners to detect text changes and refresh button state
        phoneField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        });

        passField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        });

        // Create centered "or" label using reusable Label component
        root.add(createLabel("or", 0, by + bh + 18, W, 20,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE, SwingConstants.CENTER));

        // Create clickable "Sign up" label that opens the SignUp screen
        JLabel signUpLabel = createLabel("<html><u>Sign up</u></html>", 0, by + bh + 43, W, 25,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE, SwingConstants.CENTER);
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        root.add(signUpLabel);

        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignUpScreen();
                dispose();
            }
        });

        // Handle Sign In button action and basic validation
        signInButton.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();

            if (phoneField.isPlaceholderActive()) phone = "";
            if (passField.isPlaceholderActive()) pass = "";

            if (phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter phone number and password.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                new OrderTestScreen();
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Order screen failed to open:\n"
                                + ex.getClass().getSimpleName() + " - " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Allow pressing Enter to trigger the Sign In button
        getRootPane().setDefaultButton(signInButton);

        setVisible(true);

        // Clear initial focus so no field appears auto-selected on startup
        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }
}
