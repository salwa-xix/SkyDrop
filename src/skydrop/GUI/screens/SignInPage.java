package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.Label;
import skydrop.GUI.components.RoundedButton;
import skydrop.GUI.components.RoundedInputField;

public class SignInPage extends BaseScreen {

    private static final int W = 375;
    private static final int H = 812;

    public SignInPage() {

        // Initialize this screen using BaseScreen which loads the wallpaper and logo
        super(SignInPage.class);

        // Configure the main window
        JFrame frame = new JFrame("SkyDrop - Sign In");
        frame.setSize(W, H);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setContentPane(this);

        setFocusable(true);

        // Define layout measurements for the form elements
        int formX = 40;
        int fieldW = W - 80;
        int fieldH = 50;

        // Calculate the starting Y position based on logo placement in BaseScreen
        int startY = 18 + 150 + 200;

        // Create phone number input field with rounded style and placeholder support
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(formX, startY, fieldW, fieldH);
        add(phoneField);

        // Create password input field with masking enabled
        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(formX, startY + 70, fieldW, fieldH);
        add(passField);

        // Create and center the Sign In button
        int buttonWidth = fieldW / 2;
        int buttonX = (W - buttonWidth) / 2;

        RoundedButton signInButton = new RoundedButton("Sign In", 18);
        signInButton.setBounds(buttonX, startY + 140, buttonWidth, 55);
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(signInButton);

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
        JLabel orLabel = Label.createLabel(
                "or",
                0, startY + 210, W, 20,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE,
                SwingConstants.CENTER
        );
        add(orLabel);

        // Create clickable "Sign up" label that opens the SignUp screen
        JLabel signUpLabel = Label.createLabel(
                "<html><u>Sign up</u></html>",
                0, startY + 235, W, 25,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE,
                SwingConstants.CENTER
        );
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(signUpLabel);

        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignUpScreen();
                frame.dispose();
            }
        });

        // Handle Sign In button action and basic validation
        signInButton.addActionListener(e -> {
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();

            if (phoneField.isPlaceholderActive()) phone = "";
            if (passField.isPlaceholderActive()) pass = "";

            if (phone.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame,
                        "Please enter phone number and password.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                new OrderTestScreen();
                frame.dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame,
                        "Order screen failed to open:\n"
                                + ex.getClass().getSimpleName() + " - " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        // Allow pressing Enter to trigger the Sign In button
        frame.getRootPane().setDefaultButton(signInButton);

        frame.setVisible(true);

        // Clear initial focus so no field appears auto-selected on startup
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }
}