package skydrop.app;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SkyDropServer {

    private static final int PORT = 8189;

    public static void main(String[] args) {

        // Create the database and tables if they do not already exist
        CreateDataBase.createTables();

        // Create and connect the main database controller
        DatabaseController db = new DatabaseController();

        db.connect();

        // Insert demo drones when the server starts
        db.insertInitialDrones();

        // Create backend controllers used by the system
        OrderController orderController = new OrderController(db);

        DroneController droneController = new DroneController(db);

        FileController fileController = new FileController();

        WeatherController weatherController =
                new WeatherController();

        // Manage all drone delivery threads
        DroneThreadManager droneThreadManager = new DroneThreadManager();

        // Start one delivery thread for each drone
        droneThreadManager.startDroneThreads(
                droneController.getAllDrones(),
                droneController,
                orderController,
                db,
                weatherController,
                fileController
        );

        // Start listening for client connections
        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("SkyDrop Server is running on port " + PORT);

            while (true) {

                // Wait for a new client connection
                Socket clientSocket = server.accept();

                // Handle each client request in a separate thread
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