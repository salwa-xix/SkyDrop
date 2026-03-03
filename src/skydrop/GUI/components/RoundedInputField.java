package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

// Custom rounded input field with placeholder support
public class RoundedInputField extends JPasswordField {

    private final int radius;                 // Corner roundness
    private final String placeholder;         // Placeholder text
    private final boolean isPassword;         // Is this a password field?
    private final char passwordEchoChar = '•'; // Bullet character for password

    public RoundedInputField(String placeholder, int radius, boolean isPassword) {

        super(placeholder); // Set initial text as placeholder

        this.radius = radius;
        this.placeholder = placeholder;
        this.isPassword = isPassword;

        setOpaque(false); //  draw background manually
        setForeground(Color.GRAY); // Placeholder color
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); // Inner padding
        setFont(new Font("SansSerif", Font.PLAIN, 14));

        setEchoChar((char) 0); // Show text normally at start

        // Handle focus events (when user clicks in/out)
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                // If placeholder is showing, clear it
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.BLACK);

                    // If password field, enable bullet masking
                    if (isPassword) {
                        setEchoChar(passwordEchoChar);
                    } else {
                        setEchoChar((char) 0);
                    }
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                // If user leaves field empty, restore placeholder
                if (getText().trim().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    setEchoChar((char) 0);
                }
            }
        });
    }

    // Check if placeholder is currently visible
    public boolean isPlaceholderActive() {
        return getText().equals(placeholder);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        // Smooth rounded edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw white rounded background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                radius * 2, radius * 2);

        g2.dispose();

        // Draw text on top
        super.paintComponent(g);
    }
}