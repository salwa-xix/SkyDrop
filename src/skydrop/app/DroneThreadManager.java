package skydrop.app;

import java.util.ArrayList;

public class DroneThreadManager {

    private ArrayList<DroneThread> droneThreads = new ArrayList<>();

    public void startDroneThreads(ArrayList<Drone> drones,
                                  ArrayList<Order> orders,
                                  DatabaseController databaseController,
                                  FileController fileController) {

        for (Drone drone : drones) {
            DroneThread thread = new DroneThread(
                    drone,
                    orders,
                    databaseController,
                    fileController
            );

            droneThreads.add(thread);
            thread.start();
        }
    }

    public void stopAllThreads() {
        for (DroneThread thread : droneThreads) {
            thread.stopDrone();
        }
    }
}