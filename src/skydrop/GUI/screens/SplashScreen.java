package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;

import skydrop.GUI.components.RoundedButton;
import skydrop.GUI.components.Label;

public class SplashScreen extends JFrame {

    // window setting
    private static final int W = 375;
    private static final int H = 812;

    // Background and logo images
    private Image bgImage;
    private Image logoImage;

    // Fallback background color (used if background image fails to load)
    private final Color bgFallback = Color.decode("#262525");

    public SplashScreen() {

        // Window settings
        setTitle("SkyDrop"); // window bar
        setSize(W, H); // window size
        setLocationRelativeTo(null); // center the window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // close app when splash closes
        setResizable(false); // disable resizing

        //  panel responsible for drawing the background
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Draw background image if available
                if (bgImage != null) {
                    g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                }
                // Otherwise use fallback background color
                else {
                    g.setColor(bgFallback);
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        setContentPane(root);

        // Load images from resources
        bgImage = loadImage("/Images/wallpaper.png");
        logoImage = loadImage("/Images/skydrop logo.png");

        // Layout values
        int logoSize = 250;
        int logoYTop = 30;

        // Create and position the logo
        JLabel logoLabel = new JLabel("", SwingConstants.CENTER);
        logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);

        if (logoImage != null) {
            Image scaled = logoImage.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(scaled));
        }

        root.add(logoLabel);

        // Create and position welcome text
        int textY = logoYTop + logoSize + 300;

        root.add(Label.createLabel("Welcome to SkyDrop", 0, textY, W, 30,
                new Font("SansSerif", Font.BOLD, 18),
                Color.WHITE, SwingConstants.CENTER));

        root.add(Label.createLabel("Where delivery meets the sky", 0, textY + 30, W, 25,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE, SwingConstants.CENTER));

        // Create the "Start" action button
        RoundedButton startButton = new RoundedButton("Start", 18);
        startButton.setBounds((W - 280) / 2, 650, 280, 54); // positioning
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);

        // Hover effect
        startButton.enableHover(Color.WHITE, Color.decode("#0092D9"));
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.WHITE);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.BLACK);
            }
        });

        //  navigates the user to the SignInPage
        startButton.addActionListener(e -> {
            new SignInScreen();  // open Sign In screen
            dispose();         // close Splash screen
        });

        // Pressing Enter triggers the Start button
        getRootPane().setDefaultButton(startButton);

        root.add(startButton);

        // Display the UI after all components are added
        setVisible(true);
    }

    // Load image safely from resources
    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) return null;
        return new ImageIcon(url).getImage();
    }
}
