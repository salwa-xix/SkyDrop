package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

import skydrop.GUI.components.BaseScreen;
import skydrop.GUI.components.Label;
import skydrop.GUI.components.RoundedButton;
import skydrop.GUI.components.RoundedInputField;

public class SignUpScreen extends BaseScreen {

    private static final int W = 375;
    private static final int H = 812;

    public SignUpScreen() {

        // Initialize this screen using BaseScreen which loads the wallpaper and logo
        super(SignUpScreen.class);

        // Configure the main window
        JFrame frame = new JFrame("SkyDrop - Sign Up");
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
        int startY = 18 + 150 + 60;

        // Create name input field with rounded style and placeholder support
        RoundedInputField nameField = new RoundedInputField("Name", 18, false);
        nameField.setBounds(formX, startY, fieldW, fieldH);
        add(nameField);

        // Create phone number input field
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(formX, startY + 70, fieldW, fieldH);
        add(phoneField);

        // Create district dropdown
        String[] jeddahDistricts = {
                "Your District",
                "Al Rawdah",
                "Al Safa",
                "Al Hamra",
                "Al Salamah",
                "Al Rehab"
        };

        RoundedComboBox districtBox = new RoundedComboBox(jeddahDistricts, 18);
        districtBox.setBounds(formX, startY + 140, fieldW, fieldH);
        add(districtBox);

        // Create password input field with masking enabled
        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(formX, startY + 210, fieldW, fieldH);
        add(passField);

        // Create and center the Sign Up button
        int buttonWidth = fieldW / 2;
        int buttonX = (W - buttonWidth) / 2;

        RoundedButton signUpButton = new RoundedButton("Sign Up", 18);
        signUpButton.setBounds(buttonX, startY + 280, buttonWidth, 55);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(signUpButton);

        // Define normal and active button colors
        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        signUpButton.setBackground(normalBg);
        signUpButton.setForeground(normalFg);

        // Update button appearance only when all fields are valid
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

        // Attach listeners to detect text changes and refresh button state
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        };

        nameField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        passField.getDocument().addDocumentListener(dl);

        districtBox.addActionListener(e -> updateButtonState.run());

        // Create centered "or" label using reusable Label component
        JLabel orLabel = Label.createLabel(
                "or",
                0, startY + 350, W, 20,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE,
                SwingConstants.CENTER
        );
        add(orLabel);

        // Create clickable "Sign in" label that opens the SignInPage
        JLabel signInLabel = Label.createLabel(
                "<html><u>Sign in</u></html>",
                0, startY + 375, W, 25,
                new Font("SansSerif", Font.BOLD, 14),
                Color.WHITE,
                SwingConstants.CENTER
        );
        signInLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(signInLabel);

        signInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignInPage();
                frame.dispose();
            }
        });

        // Handle Sign Up button action and basic validation
        signUpButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String pass = passField.getText().trim();
            String district = (String) districtBox.getSelectedItem();

            if (nameField.isPlaceholderActive()) name = "";
            if (phoneField.isPlaceholderActive()) phone = "";
            if (passField.isPlaceholderActive()) pass = "";

            boolean districtValid = district != null && !district.equals("Your District");

            if (name.isEmpty() || phone.isEmpty() || pass.isEmpty() || !districtValid) {
                JOptionPane.showMessageDialog(frame,
                        "Please fill all fields and select your district.",
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

        // Allow pressing Enter to trigger the Sign Up button
        frame.getRootPane().setDefaultButton(signUpButton);

        frame.setVisible(true);

        // Clear initial focus so no field appears auto-selected on startup
        SwingUtilities.invokeLater(() -> {
            requestFocusInWindow();
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