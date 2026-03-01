package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;

public final class Label {

    private Label() {} // prevent creating objects

    // Create a JLabel with common settings
    public static JLabel createLabel(String text, int x, int y, int w, int h, Font f, Color c, int alignment) {
        JLabel l = new JLabel(text, alignment);
        l.setBounds(x, y, w, h);
        l.setFont(f);
        l.setForeground(c);
        return l;
    }
}