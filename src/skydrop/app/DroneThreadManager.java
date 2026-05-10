package skydrop.app;

import java.util.ArrayList;

public class DroneThreadManager {

    private ArrayList<DroneThread> droneThreads = new ArrayList<>();

    // Create and start one thread for each drone
    public void startDroneThreads(ArrayList<Drone> drones,
                                  OrderController orderController,
                                  DatabaseController databaseController,
                                  WeatherController weatherController,
                                  FileController fileController) {

        for (Drone drone : drones) {

            DroneThread thread = new DroneThread(
                    drone,
                    orderController,
                    databaseController,
                    weatherController,
                    fileController
            );

            droneThreads.add(thread);

            thread.start();
        }
    }

    // Stop all running drone threads
    public void stopAllThreads() {

        for (DroneThread thread : droneThreads) {
            thread.stopDrone();
        }
    }
}