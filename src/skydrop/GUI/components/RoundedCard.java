package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;

// for the trans shape
public class RoundedCard extends JPanel {

    private final int radius;
    private static final Color BG = new Color(255, 255, 255, 235);

    public RoundedCard(int radius) {
        this.radius = radius;
        setOpaque(false);
        setLayout(null);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(BG);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                radius * 2, radius * 2);
        g2.dispose();
        super.paintComponent(g);
    }
}