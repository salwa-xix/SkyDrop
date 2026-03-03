package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

// Custom button with rounded corners
public class RoundedButton extends JButton {

    private final int radius;       // Corner roundness
    private Color normalColor;      // Normal background color
    private Color hoverColor;       // Hover background color

    public RoundedButton(String text, int radius) {
        super(text); // Set button text
        this.radius = radius;

        // Remove default button styling
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
    }

    // Enable hover effect (color change on mouse over)
    public void enableHover(Color normal, Color hover) {

        this.normalColor = normal;
        this.hoverColor = hover;

        setBackground(normalColor);

        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (hoverColor != null) {
                    setBackground(hoverColor);
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (normalColor != null) {
                    setBackground(normalColor);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();

        // Smooth edges
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Create rounded shape
        Shape round = new RoundRectangle2D.Float(
                0, 0,
                getWidth(), getHeight(),
                radius * 2f, radius * 2f
        );

        // Draw background
        g2.setColor(getBackground());
        g2.fill(round);

        // Draw centered text
        FontMetrics fm = g2.getFontMetrics();
        int textW = fm.stringWidth(getText());
        int textH = fm.getAscent();

        int x = (getWidth() - textW) / 2;
        int y = (getHeight() + textH) / 2 - 2;

        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}