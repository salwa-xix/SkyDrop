package skydrop.app;

import java.util.ArrayList;

import java.util.ArrayList;

public class DroneThreadManager {

    private ArrayList<DroneThread> droneThreads = new ArrayList<>();

    public void startDroneThreads(ArrayList<Drone> drones,
                                  OrderController orderController,
                                  DroneController droneController,
                                  DatabaseController databaseController,
                                  FileController fileController) {

        for (Drone drone : drones) {

            DroneThread thread = new DroneThread(
                    drone,
                    orderController,
                    droneController,
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