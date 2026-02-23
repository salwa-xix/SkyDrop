package skydrop.screens;

import javax.swing.*;
import java.awt.*;
import java.awt.KeyboardFocusManager;

import skydrop.components.RoundedButton;
import skydrop.components.RoundedInputField;

public class SignInPage extends JFrame {

    private static final int W = 375;
    private static final int H = 812;

    private Image bgImage;
    private Image originalLogo;

    private final int logoSize = 250;
    private final int logoYTop = 30;

    private final Color bgFallback = Color.decode("#262525");

    public SignInPage() {

        setTitle("SkyDrop - Sign In");
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

        // ===== Layout =====
        int formX = 40;
        int fieldW = W - 80;
        int fieldH = 50;
        int startY = logoYTop + logoSize + 200;

        // ===== Fields =====
        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(formX, startY, fieldW, fieldH);
        root.add(phoneField);

        RoundedInputField passField = new RoundedInputField("Password", 18, true);
        passField.setBounds(formX, startY + 70, fieldW, fieldH);
        root.add(passField);

        // ===== Sign In button =====
        int buttonWidth = fieldW / 2;
        int buttonX = (W - buttonWidth) / 2;

        RoundedButton signInButton = new RoundedButton("Sign In", 18);
        signInButton.setBounds(buttonX, startY + 140, buttonWidth, 55);
        signInButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        root.add(signInButton);

        // Colors
        Color normalBg = Color.WHITE;
        Color normalFg = Color.BLACK;
        Color activeBg = Color.decode("#0092D9");
        Color activeFg = Color.WHITE;

        // Default state
        signInButton.setBackground(normalBg);
        signInButton.setForeground(normalFg);

        // ===== Button color logic =====
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

        // Listen for typing
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

        // ===== OR label =====
        JLabel orLabel = new JLabel("or", SwingConstants.CENTER);
        orLabel.setBounds(0, startY + 210, W, 20);
        orLabel.setForeground(Color.WHITE);
        orLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        root.add(orLabel);

        // ===== Sign Up label (linked to SignUpScreen) =====
        JLabel signUpLabel = new JLabel("<html><u>Sign up</u></html>", SwingConstants.CENTER);
        signUpLabel.setBounds(0, startY + 235, W, 25);
        signUpLabel.setForeground(Color.WHITE);
        signUpLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        signUpLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        root.add(signUpLabel);

        // ✅ Open SignUpScreen when clicked
        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                new SignUpScreen(); // open sign up page
                dispose();          // close sign in page
            }
        });

        // ===== Sign In action =====
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

            JOptionPane.showMessageDialog(this,
                    "Signed in successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        getRootPane().setDefaultButton(signInButton);

        setVisible(true);

        // No cursor by default
        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }


}