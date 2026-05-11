package skydrop.app;

import java.util.ArrayList;

// Manage all drone worker threads in the system
public class DroneThreadManager {


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


            // Start the delivery simulation
            thread.start();
        }
    }

}