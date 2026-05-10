package skydrop.app;

import java.util.ArrayList;

public class DroneController {

    private ArrayList<Drone> drones;

    public DroneController(DatabaseController databaseController) {

        // Load all drones from the database when the server starts
        this.drones = databaseController.loadDrones();
    }

    // Return all drones so the DroneThreadManager can start one thread for each drone
    public ArrayList<Drone> getAllDrones() {
        return drones;
    }

    // Find a drone by its ID from the runtime list
    public Drone findDroneById(int droneId) {

        for (Drone drone : drones) {

            if (drone.getDroneId() == droneId) {
                return drone;
            }
        }

        return null;
    }
}