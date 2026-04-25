package skydrop.app;

import java.util.ArrayList;

public class DroneController {
    private ArrayList<Drone> drones;

    public DroneController(DatabaseController databaseController) {
        this.drones = databaseController.loadDrones();
    }

    public ArrayList<Drone> getAllDrones() {
        return drones;
    }

    public Drone findDroneById(int droneId) {
        for (Drone drone : drones) {
            if (drone.getDroneId() == droneId) {
                return drone;
            }
        }
        return null;
    }

    public Drone findAvailableDrone(String district) {
        for (Drone drone : drones) {
            if (drone.getDistrict().equalsIgnoreCase(district)
                    && drone.isAvailable()) {
                return drone;
            }
        }
        return null;
    }

    public Drone assignDroneToOrder(Order order) {
        Drone drone = findAvailableDrone(order.getDistrict());

        if (drone != null) {
            drone.assignOrder(order.getOrderId());
            order.assignDrone(drone.getDroneId());
        }

        return drone;
    }

    public void finishDelivery(Order order) {
        if (order.getAssignedDroneId() != null) {
            Drone drone = findDroneById(order.getAssignedDroneId());

            if (drone != null) {
                drone.releaseOrder();
                drone.incrementDeliveredCount();
            }

            order.removeDrone();
        }
    }

    public void refreshQueues(ArrayList<Order> orders) {
        for (Drone drone : drones) {
            int count = 0;

            for (Order order : orders) {
                if (order.getDistrict().equalsIgnoreCase(drone.getDistrict())
                        && order.getStatus().equalsIgnoreCase("Waiting")) {
                    count++;
                }
            }

            drone.setQueueCount(count);
        }
    }
}