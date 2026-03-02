package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;

import skydrop.GUI.components.RoundedButton;
import skydrop.GUI.components.Label;

public class SplashScreen extends JFrame {

    // Window size (designed to look like a mobile screen)
    private static final int W = 375;
    private static final int H = 812;

    // These variables will store the background image and logo image
    private Image bgImage;
    private Image originalLogo;

    // Values used to control the logo animation (starts small, grows bigger)
    private int logoSize = 20;              // Initial logo size
    private final int logoTargetSize = 250; // Final logo size
    private final int logoYTop = 30;        // Distance from top

    // UI components that will be animated
    private FadableLabel logoLabel;
    private FadableLabel line1;
    private FadableLabel line2;

    // Panel that holds the button (will fade and slide in)
    private AlphaPanel buttonsPanel;
    private RoundedButton startButton;

    // Fallback color if the background image fails to load
    private final Color bgFallback = Color.decode("#262525");

    public SplashScreen() {

        // Basic window settings
        setTitle("SkyDrop");
        setSize(W, H);
        setLocationRelativeTo(null); // Center window on screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Main panel responsible for drawing the background
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // Draw background image if available
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // Otherwise fill background with fallback color
                    g2.setColor(bgFallback);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.dispose();
            }
        };

        root.setBackground(bgFallback);
        setContentPane(root);

        // Load images from resources
        bgImage = loadImage("/Images/wallpaper.png");
        originalLogo = loadImage("/Images/skydrop logo.png");

        // Prepare logo label (starts small and invisible)
        logoLabel = new FadableLabel("", SwingConstants.CENTER);
        logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);
        logoLabel.setAlpha(0f); // Fully transparent at start
        root.add(logoLabel);
        updateLogoIcon(logoSize);

        // Calculate Y position for text below the logo
        int textY = logoYTop + logoTargetSize + 320;

        // First text line (starts small and invisible)
        line1 = makeFadableLabel(
                "Welcome to SkyDrop",
                0, textY, W, 30,
                new Font("SansSerif", Font.BOLD, 18),
                Color.WHITE,
                SwingConstants.CENTER
        );
        line1.setAlpha(0f);
        line1.setScale(0.70f);
        root.add(line1);

        // Second text line (same animation behavior)
        line2 = makeFadableLabel(
                "Where delivery meets the sky",
                0, textY + 30, W, 25,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.WHITE,
                SwingConstants.CENTER
        );
        line2.setAlpha(0f);
        line2.setScale(0.70f);
        root.add(line2);

        // Create button panel (starts invisible and below screen)
        buttonsPanel = buildButtonsPanel();
        buttonsPanel.setAlpha(0f);
        buttonsPanel.setBounds(0, H + 200, W, 140);
        root.add(buttonsPanel);

        // Attach button action (open SignIn page)
        hookStartButton();

        setVisible(true);

        // Start first animation phase (logo)
        animateLogoReveal();
    }

    // Uses your custom Label template and converts it into an animated label
    private static FadableLabel makeFadableLabel(String text,  int x, int y, int w, int h, Font font, Color color, int alignment) {

        JLabel base = Label.createLabel(text, x, y, w, h, font, color, alignment);

        FadableLabel f = new FadableLabel(base.getText(), base.getHorizontalAlignment());
        f.setFont(base.getFont());
        f.setForeground(base.getForeground());
        f.setBounds(base.getBounds());
        f.setOpaque(false);

        return f;
    }

    // Loads an image safely from resources
    private Image loadImage(String path) {
        var url = getClass().getResource(path);
        if (url == null) return null;
        return new ImageIcon(url).getImage();
    }

    // Defines what happens when Start button is clicked
    private void hookStartButton() {
        startButton.addActionListener(e -> {
            new SignInPage();
            dispose();
        });

        // Pressing Enter triggers the Start button
        getRootPane().setDefaultButton(startButton);
    }

    // Builds the panel that contains the Start button
    private AlphaPanel buildButtonsPanel() {

        AlphaPanel p = new AlphaPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        startButton = new RoundedButton("Start", 18);
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(280, 54));
        startButton.setMaximumSize(new Dimension(280, 54));
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));

        Color normalColor = Color.WHITE;
        Color hoverColor = Color.decode("#0092D9");

        startButton.setForeground(Color.BLACK);
        startButton.enableHover(normalColor, hoverColor);

        // Change text color when hovering
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.BLACK);
            }
        });

        p.add(Box.createVerticalStrut(20));
        p.add(startButton);

        return p;
    }

    // First animation: logo grows and fades in
    private void animateLogoReveal() {

        final int startSize = 20;
        final int endSize = logoTargetSize;
        final int stepSize = 6;
        final float alphaStep = 0.04f;

        logoSize = startSize;
        logoLabel.setAlpha(0f);
        logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);
        updateLogoIcon(logoSize);

        Timer t = new Timer(16, null);
        t.addActionListener(e -> {

            if (logoSize < endSize) {
                logoSize = Math.min(endSize, logoSize + stepSize);
                logoLabel.setBounds((W - logoSize) / 2, logoYTop, logoSize, logoSize);
                updateLogoIcon(logoSize);
            }

            if (logoLabel.getAlpha() < 1f) {
                logoLabel.setAlpha(Math.min(1f, logoLabel.getAlpha() + alphaStep));
            }

            if (logoSize == endSize && logoLabel.getAlpha() >= 1f) {
                t.stop();
                animateTextAndButtonTogether();
            }
        });

        t.start();
    }

    // Second animation: text fades/scales in and button slides up
    private void animateTextAndButtonTogether() {

        final float startScale = 0.70f;
        final float endScale = 1.00f;
        final float scaleStep = 0.03f;
        final float alphaStep = 0.05f;

        final int targetY = 650;
        final int slideStep = 25;

        line1.setAlpha(0f);
        line2.setAlpha(0f);
        line1.setScale(startScale);
        line2.setScale(startScale);

        buttonsPanel.setAlpha(0f);

        Timer t = new Timer(16, null);
        t.addActionListener(e -> {

            float a = line1.getAlpha();
            float s = line1.getScale();

            if (a < 1f) {
                float na = Math.min(1f, a + alphaStep);
                line1.setAlpha(na);
                line2.setAlpha(na);
            }

            if (s < endScale) {
                float ns = Math.min(endScale, s + scaleStep);
                line1.setScale(ns);
                line2.setScale(ns);
            }

            int y = buttonsPanel.getY();
            buttonsPanel.setLocation(0, Math.max(targetY, y - slideStep));

            float ab = buttonsPanel.getAlpha();
            if (ab < 1f) buttonsPanel.setAlpha(Math.min(1f, ab + alphaStep));

            boolean doneText = (line1.getAlpha() >= 1f && line1.getScale() >= endScale);
            boolean doneBtn = (buttonsPanel.getY() <= targetY && buttonsPanel.getAlpha() >= 1f);

            if (doneText && doneBtn) t.stop();
        });

        t.start();
    }

    // Resizes the logo image according to current animation size
    private void updateLogoIcon(int s) {
        if (originalLogo == null) return;
        Image scaled = originalLogo.getScaledInstance(s, s, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));
        logoLabel.repaint();
    }

    // Custom label that supports fade (alpha) and scale (zoom)
    static class FadableLabel extends JLabel {

        private float alpha = 1f;
        private float scale = 1f;

        public FadableLabel(String text, int alignment) {
            super(text, alignment);
            setOpaque(false);
        }

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0f, Math.min(1f, alpha));
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        public void setScale(float scale) {
            this.scale = Math.max(0.1f, Math.min(2f, scale));
            repaint();
        }

        public float getScale() {
            return scale;
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON
            );

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

            int w = getWidth();
            int h = getHeight();

            g2.translate(w / 2.0, h / 2.0);
            g2.scale(scale, scale);
            g2.translate(-w / 2.0, -h / 2.0);

            super.paintComponent(g2);
            g2.dispose();
        }
    }

    // Custom panel that supports fade effect for all its content
    static class AlphaPanel extends JPanel {

        private float alpha = 1f;

        public void setAlpha(float alpha) {
            this.alpha = Math.max(0f, Math.min(1f, alpha));
            repaint();
        }

        public float getAlpha() {
            return alpha;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}