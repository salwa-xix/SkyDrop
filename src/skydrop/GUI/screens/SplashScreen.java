package skydrop.GUI.screens;

import javax.swing.*;
import java.awt.*;

import skydrop.GUI.components.RoundedButton;

public class SplashScreen extends JFrame {

    // =======================
    // Screen size (mobile-like)
    // =======================
    private static final int W = 375;
    private static final int H = 812;

    // =======================
    // Images
    // =======================
    private Image bgImage;         // Background image
    private Image originalLogo;    // Original logo (we resize it during animation)

    // =======================
    // Logo animation values
    // =======================
    private int logoSize = 20;                 // Logo starts very small
    private final int logoTargetSize = 250;    // Final logo size after animation
    private final int logoYTop = 30;           // Logo position from top

    // =======================
    // UI components (fade + scale)
    // =======================
    private FadableLabel logoLabel;   // Logo label (fade only, size changes)
    private FadableLabel line1;       // First text line (fade + scale)
    private FadableLabel line2;       // Second text line (fade + scale)

    // =======================
    // Buttons container (fade + slide)
    // =======================
    private AlphaPanel buttonsPanel;

    // ✅ Start button as a field (so we can add listeners)
    private RoundedButton startButton; // <-- uses external RoundedButton.java

    // =======================
    // Fallback background color (if image not found)
    // =======================
    private final Color bgFallback = Color.decode("#262525");

    // =======================
    // Constructor: builds the splash screen UI
    // =======================
    public SplashScreen() {

        // ---- Window settings ----
        setTitle("SkyDrop");
        setSize(W, H);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // =======================
        // Root panel: draws the background image
        // =======================
        JPanel root = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Use Graphics2D for better quality
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                // Draw background image if loaded
                if (bgImage != null) {
                    g2.drawImage(bgImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    // If no image, use fallback solid color
                    g2.setColor(bgFallback);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                g2.dispose();
            }
        };

        root.setBackground(bgFallback);
        setContentPane(root);

        // =======================
        // Load background image from resources
        // =======================
        ImageIcon bgIcon = new ImageIcon(getClass().getResource("/Images/wallpaper.png"));
        bgImage = bgIcon.getImage();

        // =======================
        // Load logo image from resources
        // =======================
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/Images/skydrop logo.png"));
        originalLogo = logoIcon.getImage();

        // =======================
        // Logo label setup (starts hidden and small)
        // =======================
        logoLabel = new FadableLabel("");
        logoLabel.setSize(logoSize, logoSize);
        logoLabel.setLocation((W - logoSize) / 2, logoYTop);
        logoLabel.setAlpha(0f); // hidden at start
        logoLabel.setScale(1f); // scale not used for image, only size changes
        root.add(logoLabel);

        // Set the first resized logo icon
        updateLogoIcon(logoSize);

        // =======================
        // Text line 1 (hidden + scaled down at start)
        // =======================
        line1 = new FadableLabel("Welcome to SkyDrop", SwingConstants.CENTER);
        line1.setForeground(Color.WHITE);
        line1.setFont(new Font("SansSerif", Font.BOLD, 18));
        line1.setSize(W, 30);
        line1.setLocation(0, logoYTop + logoTargetSize + 320);
        line1.setAlpha(0f);       // hidden at start
        line1.setScale(0.70f);    // small at start
        root.add(line1);

        // =======================
        // Text line 2 (hidden + scaled down at start)
        // =======================
        line2 = new FadableLabel("Where delivery meets the sky", SwingConstants.CENTER);
        line2.setForeground(Color.WHITE);
        line2.setFont(new Font("SansSerif", Font.PLAIN, 14));
        line2.setSize(W, 25);
        line2.setLocation(0, line1.getY() + 30);
        line2.setAlpha(0f);       // hidden at start
        line2.setScale(0.70f);    // small at start
        root.add(line2);

        // =======================
        // Buttons panel (hidden + starts below the screen)
        // =======================
        buttonsPanel = buildButtonsPanel();
        buttonsPanel.setAlpha(0f);

        // Start outside screen (below)
        buttonsPanel.setBounds(0, H + 200, W, 140);
        root.add(buttonsPanel);

        // ✅ Hook Start button to open HomeScreen
        hookStartButton();

        // Show window
        setVisible(true);

        // Start animation sequence
        animateLogoReveal();
    }

    // =======================
    // Connect Start button click to open the next screen
    // =======================
    private void hookStartButton() {
        startButton.addActionListener(e -> {
            new SignInPage(); // opens your next page (separate class)
            dispose();        // closes splash screen
        });

        // Optional: pressing Enter triggers Start
        getRootPane().setDefaultButton(startButton);
    }

    // =======================
    // Build panel that contains Start button
    // =======================
    private AlphaPanel buildButtonsPanel() {

        // Panel supports alpha fade
        AlphaPanel p = new AlphaPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        // ✅ Uses external RoundedButton class
        startButton = new RoundedButton("Start", 18);

        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(280, 54));
        startButton.setMaximumSize(new Dimension(280, 54));
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));

// اللون الافتراضي
        Color normalColor = Color.WHITE;
        Color hoverColor  = Color.decode("#0092D9");

        startButton.setForeground(Color.BLACK);

// فعّل hover
        startButton.enableHover(normalColor, hoverColor);

// نغير لون النص عند hover
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                startButton.setForeground(Color.BLACK);
            }
        });        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setPreferredSize(new Dimension(280, 54));
        startButton.setMaximumSize(new Dimension(280, 54));
        startButton.setFont(new Font("SansSerif", Font.PLAIN, 18));
        startButton.setBackground(Color.WHITE);
        startButton.setForeground(Color.BLACK);

        // Add space on top then add the button
        p.add(Box.createVerticalStrut(20));
        p.add(startButton);

        return p;
    }

    // =======================
    // 1) Logo animation:
    // - Logo fades in
    // - Logo grows from small to big
    // =======================
    private void animateLogoReveal() {

        final int startSize = 20;           // small start
        final int endSize = logoTargetSize; // final size
        final int stepSize = 6;             // how fast it grows
        final float alphaStep = 0.04f;      // how fast it fades in

        // Reset values before animation
        logoSize = startSize;
        logoLabel.setAlpha(0f);
        logoLabel.setSize(logoSize, logoSize);
        logoLabel.setLocation((W - logoSize) / 2, logoYTop);
        updateLogoIcon(logoSize);

        // Timer = runs every 16ms (~60 FPS)
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {

            // Check if logo reached final size
            boolean doneSize = (logoSize >= endSize);

            // Current alpha
            float a = logoLabel.getAlpha();

            // Increase logo size step by step
            if (!doneSize) {
                logoSize = Math.min(endSize, logoSize + stepSize);
                logoLabel.setSize(logoSize, logoSize);
                logoLabel.setLocation((W - logoSize) / 2, logoYTop);
                updateLogoIcon(logoSize);
            }

            // Increase alpha step by step
            if (a < 1f) {
                logoLabel.setAlpha(Math.min(1f, a + alphaStep));
            }

            // When logo finished, start text + button animation
            if (doneSize && logoLabel.getAlpha() >= 1f) {
                t.stop();
                animateTextAndButtonTogether();
            }
        });

        t.start();
    }

    // =======================
    // 2) Text animation + 3) Button animation (together)
    // =======================
    private void animateTextAndButtonTogether() {

        final float startScale = 0.70f; // small start
        final float endScale = 1.00f;   // normal size
        final float scaleStep = 0.03f;  // scale speed
        final float alphaStep = 0.05f;  // fade speed

        // Final Y position for button panel
        final int targetY = 650;

        // Reset text before animation
        line1.setAlpha(0f);
        line2.setAlpha(0f);
        line1.setScale(startScale);
        line2.setScale(startScale);

        // Reset button alpha
        buttonsPanel.setAlpha(0f);

        // Timer for smooth animation
        Timer t = new Timer(16, null);
        t.addActionListener(e -> {

            // -------- Text update (fade + scale) --------
            float a1 = line1.getAlpha();
            float s1 = line1.getScale();

            if (a1 < 1f) {
                float newA = Math.min(1f, a1 + alphaStep);
                line1.setAlpha(newA);
                line2.setAlpha(newA);
            }

            if (s1 < endScale) {
                float newS = Math.min(endScale, s1 + scaleStep);
                line1.setScale(newS);
                line2.setScale(newS);
            }

            // -------- Button update (slide up + fade) --------
            int y = buttonsPanel.getY();
            if (y > targetY) buttonsPanel.setLocation(0, y - 25);
            else buttonsPanel.setLocation(0, targetY);

            float ab = buttonsPanel.getAlpha();
            if (ab < 1f) buttonsPanel.setAlpha(Math.min(1f, ab + alphaStep));

            // Stop animation when done
            boolean doneText = (line1.getAlpha() >= 1f && line1.getScale() >= endScale);
            boolean doneBtn = (buttonsPanel.getY() <= targetY && buttonsPanel.getAlpha() >= 1f);

            if (doneText && doneBtn) t.stop();
        });

        t.start();
    }

    // =======================
    // Resize the logo image and set it to the label
    // =======================
    private void updateLogoIcon(int s) {
        Image scaled = originalLogo.getScaledInstance(s, s, Image.SCALE_SMOOTH);
        logoLabel.setIcon(new ImageIcon(scaled));
        logoLabel.repaint();
    }


    // =======================
    // Custom Label that supports alpha + scale
    // =======================
    static class FadableLabel extends JLabel {

        private float alpha = 1f;
        private float scale = 1f;

        public FadableLabel(String text) {
            super(text);
            setOpaque(false);
        }

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
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

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

    // =======================
    // Custom Panel that supports alpha fade
    // =======================
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
        public void paint(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            super.paint(g2);
            g2.dispose();
        }
    }
}