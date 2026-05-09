package skydrop.app;

import javax.swing.SwingUtilities;
import skydrop.GUI.screens.SplashScreen;
import skydrop.GUI.screens.DashboardScreen;

public class Main {

    public static DatabaseController db;
    public static OrderController orderController;
    public static DroneController droneController;
    public static WeatherController weatherController;
    public static FileController fileController;
    public static DroneThreadManager droneThreadManager;

    public static void main(String[] args) {

        CreateDataBase.createTables();

        db = new DatabaseController();
        db.connect();

        db.insertInitialDrones();
        db.insertDemoUsers();

        orderController = new OrderController();
        droneController = new DroneController(db);
        weatherController = new WeatherController();
        fileController = new FileController();


        SwingUtilities.invokeLater(() -> {
            new SplashScreen();
            new SplashScreen();
            new SplashScreen();

            new DashboardScreen();
        });
    }
}