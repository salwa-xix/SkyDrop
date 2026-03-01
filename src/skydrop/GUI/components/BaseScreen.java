package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;

// This class to display the image background & logo
public class BaseScreen extends JPanel {

    private final Image backgroundImage;
    private final Image logoImage;

    public BaseScreen(Class<?> ref) {

        // Absolute layout for full control
        setLayout(null);

        // Load shared images
        backgroundImage = loadImage(ref, "/Images/wallpaper.png");
        logoImage = loadImage(ref, "/Images/skydrop logo.png");

        // Add logo
        add(createLogoLabel(150, 18));
    }

    // Paint background image
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    // Create centered logo label
    private JLabel createLogoLabel(int size, int y) {

        JLabel logo = new JLabel();

        // Center horizontally
        int x = (375 - size) / 2;
        logo.setBounds(x, y, size, size);

        if (logoImage != null) {
            logo.setIcon(new ImageIcon(logoImage.getScaledInstance(size, size, Image.SCALE_SMOOTH)));
        }

        return logo;
    }

    // Image loader (internal use)
    private Image loadImage(Class<?> ref, String path) {

        java.net.URL url = ref.getResource(path);

        if (url == null) {
            System.out.println("Resource not found: " + path);
            return null;
        }

        return new ImageIcon(url).getImage();
    }
}