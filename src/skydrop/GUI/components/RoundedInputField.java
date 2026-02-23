package skydrop.GUI.components;
import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class RoundedInputField extends JPasswordField {

    private final int radius;
    private final String placeholder;
    private final boolean isPassword;
    private final char passwordEchoChar = '•';

    public RoundedInputField(String placeholder, int radius, boolean isPassword) {

        super(placeholder);

        this.radius = radius;
        this.placeholder = placeholder;
        this.isPassword = isPassword;

        setOpaque(false);
        setForeground(Color.GRAY);
        setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        setFont(new Font("SansSerif", Font.PLAIN, 14));

        // في وضع placeholder نخلي النص ظاهر
        setEchoChar((char) 0);

        addFocusListener(new FocusAdapter() {

            @Override
            public void focusGained(FocusEvent e) {
                if (getText().equals(placeholder)) {
                    setText("");
                    setForeground(Color.BLACK);

                    if (isPassword) {
                        setEchoChar(passwordEchoChar); // يخفي الباسورد
                    } else {
                        setEchoChar((char) 0);         // يخلي النص طبيعي
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (getText().trim().isEmpty()) {
                    setText(placeholder);
                    setForeground(Color.GRAY);
                    setEchoChar((char) 0); // يظهر placeholder كنص
                }
            }
        });
    }

    public boolean isPlaceholderActive() {
        return getText().equals(placeholder);
    }

    // رسم الخلفية المنحنية
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                radius * 2, radius * 2);

        g2.dispose();
        super.paintComponent(g);
    }
}