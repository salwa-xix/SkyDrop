package skydrop.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages runtime drone objects and saves drone state changes.
 */
public class DroneController {

    private final List<Drone> drones;
    private final DatabaseController db;

    public DroneController(DatabaseController databaseController) {
        this.db = databaseController;
        this.drones = Collections.synchronizedList(databaseController.loadDrones());
    }

    /**
     * Returns the drones used by DroneThreadManager at startup.
     */
    public synchronized ArrayList<Drone> getAllDrones() {
        return new ArrayList<>(drones);
    }

    /**
     * Assigns an order through the Drone model, then saves it.
     */
    public synchronized boolean assignOrderToDrone(Drone drone, int orderId) {
        drone.assignOrder(orderId);
        return db.updateDrone(drone);
    }

    /**
     * Releases a drone through the model, then saves it.
     */
    public synchronized boolean releaseDrone(Drone drone) {
        drone.releaseOrder();
        return db.updateDrone(drone);
    }

    /**
     * Increments the delivered count through the model, then saves it.
     */
    public synchronized boolean incrementDeliveredCount(Drone drone) {
        drone.incrementDeliveredCount();
        return db.updateDrone(drone);
    }

    /**
     * Refreshes the queue count in the runtime object and database.
     */
    public synchronized void refreshQueueCount(Drone drone) {
        int count = db.getWaitingQueueCount(drone.getDistrict());
        drone.updateQueueCount(count);
        db.updateDrone(drone);
    }
}
