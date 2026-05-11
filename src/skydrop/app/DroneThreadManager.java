package skydrop.app;

import java.util.ArrayList;

/**
 * Starts and stops the worker threads that simulate drone delivery.
 */
public class DroneThreadManager {

    private final ArrayList<DroneThread> droneThreads = new ArrayList<>();

    /**
     * Creates and starts one thread for each drone.
     */
    public void startDroneThreads(ArrayList<Drone> drones,
                                  DroneController droneController,
                                  OrderController orderController,
                                  DatabaseController databaseController,
                                  WeatherController weatherController,
                                  FileController fileController) {
        for (Drone drone : drones) {
            DroneThread thread = new DroneThread(
                    drone,
                    droneController,
                    orderController,
                    databaseController,
                    weatherController,
                    fileController
            );

            droneThreads.add(thread);
            thread.start();
        }
    }

    /**
     * Requests all running drone threads to stop.
     */
    public void stopAllThreads() {
        for (DroneThread thread : droneThreads) {
            thread.stopDrone();
        }
    }
}
