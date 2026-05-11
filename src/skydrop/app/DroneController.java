package skydrop.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Manage runtime drone objects and save their updated state
public class DroneController {

    private final List<Drone> drones;
    private final DatabaseController db;

    public DroneController(DatabaseController databaseController) {

        this.db = databaseController;

        // Load all drones from the database when the server starts
        this.drones = Collections.synchronizedList(databaseController.loadDrones());
    }

    // Return all runtime drone objects
    public synchronized ArrayList<Drone> getAllDrones() {

        return new ArrayList<>(drones);
    }

    // Assign an order to a drone and save the updated state
    public synchronized boolean assignOrderToDrone(Drone drone, int orderId) {

        // Update the drone object first
        drone.assignOrder(orderId);

        // Save the updated drone state in the database
        return db.updateDrone(drone);
    }

    // Release the drone after delivery and save the changes
    public synchronized boolean releaseDrone(Drone drone) {

        // Mark the drone as available again
        drone.releaseOrder();

        // Save the updated state in the database
        return db.updateDrone(drone);
    }

    // Increase the delivery count and save the updated drone state
    public synchronized boolean incrementDeliveredCount(Drone drone) {

        // Update the runtime object first
        drone.incrementDeliveredCount();

        // Save the updated delivery count in the database
        return db.updateDrone(drone);
    }

    // Refresh the queue count for the drone district
    public synchronized void refreshQueueCount(Drone drone) {

        // Get the latest waiting order count from the database
        int count = db.getWaitingQueueCount(drone.getDistrict());

        // Update the runtime drone object
        drone.updateQueueCount(count);

        // Save the updated queue count in the database
        db.updateDrone(drone);
    }
}