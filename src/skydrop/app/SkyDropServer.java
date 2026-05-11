package skydrop.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SkyDropServer {

    private static final int PORT = 8189;

    public static void main(String[] args) {

        CreateDataBase.createTables();

        DatabaseController db = new DatabaseController();
        db.connect();

        db.insertInitialDrones();

        OrderController orderController = new OrderController(db);
        DroneController droneController = new DroneController(db);
        FileController fileController = new FileController();
        WeatherController weatherController =
                new WeatherController();

        DroneThreadManager droneThreadManager = new DroneThreadManager();

        droneThreadManager.startDroneThreads(
                droneController.getAllDrones(),
                droneController,
                orderController,
                db,
                weatherController,
                fileController
        );

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("SkyDrop Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = server.accept();

                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        db,
                        orderController,
                        fileController
                );

                handler.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}