import skydrop.GUI.screens.DashboardScreen;
import skydrop.GUI.screens.SplashScreen;

import javax.swing.*;

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {

        // Simulate multiple users for thread testing
        new SplashScreen();
        new SplashScreen();
        new SplashScreen();

        // Open employee dashboard
        new DashboardScreen();
    });
}