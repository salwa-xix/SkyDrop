package skydrop.app;

import javax.swing.SwingUtilities;

import skydrop.GUI.screens.DashboardScreen;
import skydrop.GUI.screens.SplashScreen;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();
            new SplashScreen();
            new SplashScreen();
            new DashboardScreen();
        });
    }
}