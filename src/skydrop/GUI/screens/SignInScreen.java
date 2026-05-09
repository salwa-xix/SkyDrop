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

        setTitle("SkyDrop - Sign In");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);

        BaseScreen root = new BaseScreen(getClass());
        setContentPane(root);

        int cw = 295, ch = 50, x = (W - cw) / 2, y = 300, g = 20;

        RoundedInputField phoneField = new RoundedInputField("Phone Number", 18, false);
        phoneField.setBounds(x, y, cw, ch);
        root.add(phoneField);

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

        signUpLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                SignUpScreen screen = new SignUpScreen();
                screen.setLocation(SignInScreen.this.getLocation());

                dispose();
            }
        });

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

            String response = SkyDropClient.sendRequest(
                    "LOGIN|" + phone + "|" + pass
            );

            if (response != null && response.startsWith("SUCCESS")) {

                String[] parts = response.split("\\|");

                String name = parts[1];
                String userPhone = parts[2];
                String district = parts[3];

                User user = new User(name, userPhone, pass, district);

                JOptionPane.showMessageDialog(this,
                        "Login successful!");

                OrderTestScreen screen = new OrderTestScreen(user);
                screen.setLocation(this.getLocation());
                dispose();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Wrong phone number or password.",
                        "Login Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        getRootPane().setDefaultButton(signInButton);

        setVisible(true);

        SwingUtilities.invokeLater(() -> {
            root.requestFocusInWindow();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
        });
    }
}