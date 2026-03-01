package skydrop.GUI.components;

import javax.swing.*;
import java.awt.*;

public class InfoCard extends RoundedCard {

    public InfoCard(int radius) {
        super(radius);
    }

    public void addTitle(String text, int x, int y, int w, int h) {
        add(Label.createLabel(
                text,
                x, y, w, h,
                new Font("SansSerif", Font.BOLD, 16),
                Color.BLACK,
                SwingConstants.LEFT
        ));
    }

    public void addSubtitle(String text, int x, int y, int w, int h) {
        add(Label.createLabel(
                text,
                x, y, w, h,
                new Font("SansSerif", Font.PLAIN, 12),
                Color.DARK_GRAY,
                SwingConstants.LEFT
        ));
    }

    public void addInfoRow(String key, String value, int keyX, int keyW, int valueX, int valueW, int y) {

        add(Label.createLabel(
                key,
                keyX, y, keyW, 26,
                new Font("SansSerif", Font.BOLD, 14),
                Color.BLACK,
                SwingConstants.LEFT
        ));

        add(Label.createLabel(
                value,
                valueX, y, valueW, 26,
                new Font("SansSerif", Font.PLAIN, 14),
                Color.BLACK,
                SwingConstants.LEFT
        ));
    }

    public void addLines(String[] lines, int x, int startY, int w, int lineH, Font f, Color c) {
        int y = startY;
        for (String line : lines) {
            add(Label.createLabel(line, x, y, w, lineH, f, c, SwingConstants.LEFT));
            y += lineH;
        }
    }
}