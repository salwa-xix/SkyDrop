package skydrop.app;
import skydrop.screens.SplashScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SplashScreen::new);
    }
}