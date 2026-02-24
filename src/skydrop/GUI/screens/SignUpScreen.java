package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

import skydrop.GUI.components.RoundedButton;
import skydrop.GUI.components.RoundedInputField;

public class SignUpScreen extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private Image bgImage;
    private Image originalLogo;

    private final int logoSize = 250;
    private final int logoYTop = 30;

    private final Color bgFallback = Color.decode("#262525");

    public SignUpScreen() {

        setTitle("SkyDrop - Sign Up");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // ===== Root panel with background image =====
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
        root.setFocusable(true);
        setContentPane(root);

        // ===== Load background and logo =====
        bgImage = new ImageIcon(getClass().getResource("/Images/wallpaper.png")).getImage();
        originalLogo = new ImageIcon(getClass().getResource("/Images/skydrop logo.png")).getImage();

        // ===== Logo =====
        JLabel logoLabel = new JLabel();
        logoLabel.setSize(logoSize, logoSize);
        logoLabel.setLocation((W - logoSize) / 2, logoYTop);

        Image scaledLogo = originalLogo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaledLogo));
        root.add(logoLabel);

        // ===== Layout values =====
        int formX = 40;
        int fieldW = W - 80;
        int fieldH = 50;

        int startY = logoYTop + logoSize + 60;

        // ===== Fields =====
        RoundedInputField nameField = new RoundedInputField("Name", 18, false);
        nameField.setBounds(formX, startY, fieldW, fieldH);
        root.add(nameField);

        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(formX, startY + 70, fieldW, fieldH);
        root.add(phoneField);

        // ===== District dropdown =====
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
        root.add(districtBox);

        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(formX, startY + 210, fieldW, fieldH);
        root.add(passField);

        // ===== Sign Up button =====
        int buttonWidth = fieldW / 2;
        int buttonX = (W - buttonWidth) / 2;

        RoundedButton signUpButton = new RoundedButton("Sign Up", 18);
        signUpButton.setBounds(buttonX, startY + 280, buttonWidth, 55);
        signUpButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(signUpButton);

        // Colors
        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        signUpButton.setBackground(normalBg);
        signUpButton.setForeground(normalFg);

        // ===== Button color logic (includes district selection) =====
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

        // Listen for typing
        javax.swing.event.DocumentListener dl = new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateButtonState.run(); }
        };

        nameField.getDocument().addDocumentListener(dl);
        phoneField.getDocument().addDocumentListener(dl);
        passField.getDocument().addDocumentListener(dl);

        // Listen for district changes
        districtBox.addActionListener(e -> updateButtonState.run());

        // ===== OR label =====
        JLabel orLabel = new JLabel("or", SwingConstants.CENTER);
        orLabel.setBounds(0, startY + 350, W, 20);
        orLabel.setForeground(Color.WHITE);
        orLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        root.add(orLabel);

        // ===== Sign In label =====
        JLabel signInLabel = new JLabel("<html><u>Sign in</u></html>", SwingConstants.CENTER);
        signInLabel.setBounds(0, startY + 375, W, 25);
        signInLabel.setForeground(Color.WHITE);
        signInLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        signInLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        root.add(signInLabel);

        signInLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignInPage();
                dispose();
            }
        });

        // ===== Sign Up action =====
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
                JOptionPane.showMessageDialog(this,
                        "Please fill all fields and select your district.",
                        "Missing Info",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }


//-------------------------------------
            // GO TO ORDER TEST SCREEN (after sign up)
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




        getRootPane().setDefaultButton(signUpButton);

        setVisible(true);

        // No cursor by default
        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }

    // ===== Rounded ComboBox (same style as fields) =====
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