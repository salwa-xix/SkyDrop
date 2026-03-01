package skydrop.app;

import javax.swing.SwingUtilities;
import skydrop.GUI.screens.SplashScreen;
import skydrop.GUI.screens.DashboardScreen;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SplashScreen();     // Client side
            new DashboardScreen();  // Employee side
        });
    }
}