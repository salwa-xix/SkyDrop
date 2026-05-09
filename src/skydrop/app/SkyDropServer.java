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
        db.insertDemoUsers();

        OrderController orderController = new OrderController();
        DroneController droneController = new DroneController(db);
        FileController fileController = new FileController();

        DroneThreadManager droneThreadManager = new DroneThreadManager();

        droneThreadManager.startDroneThreads(
                droneController.getAllDrones(),
                orderController,
                droneController,
                db,
                fileController
        );

        try (ServerSocket server = new ServerSocket(PORT)) {

            System.out.println("SkyDrop Server is running on port " + PORT);

            while (true) {

                Socket clientSocket = server.accept();

                ClientHandler handler = new ClientHandler(
                        clientSocket,
                        db,
                        orderController
                );

                handler.start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}