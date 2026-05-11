package skydrop.app;

import java.util.ArrayList;

// Manage all drone worker threads in the system
public class DroneThreadManager {

    private final ArrayList<DroneThread> droneThreads = new ArrayList<>();

    // Create and start one thread for each drone
    public void startDroneThreads(ArrayList<Drone> drones,
                                  DroneController droneController,
                                  OrderController orderController,
                                  DatabaseController databaseController,
                                  WeatherController weatherController,
                                  FileController fileController) {

        for (Drone drone : drones) {

            // Create a delivery thread for this drone
            DroneThread thread = new DroneThread(
                    drone,
                    droneController,
                    orderController,
                    databaseController,
                    weatherController,
                    fileController
            );

            // Store the thread so it can be stopped later
            droneThreads.add(thread);

            // Start the delivery simulation
            thread.start();
        }
    }

    // Stop all running drone threads safely
    public void stopAllThreads() {

        for (DroneThread thread : droneThreads) {

            thread.stopDrone();
        }
    }
}