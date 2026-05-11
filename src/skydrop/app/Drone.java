package skydrop.app;

// Represents one delivery drone in the system
public class Drone {

    public static final String STATUS_IDLE = "Idle";
    public static final String STATUS_BUSY = "Busy";

    private int droneId;
    private String district;
    private String status;
    private Integer currentOrderId;
    private int deliveredCount;
    private int queueCount;

    public Drone(int droneId, String district) {

        this.droneId = droneId;
        this.district = district;

        // New drones start as available with no active order
        this.status = STATUS_IDLE;
        this.currentOrderId = null;

        // Delivery statistics start from zero
        this.deliveredCount = 0;
        this.queueCount = 0;
    }

    public int getDroneId() {
        return droneId;
    }

    public String getDistrict() {
        return district;
    }

    public String getStatus() {
        return status;
    }

    public Integer getCurrentOrderId() {
        return currentOrderId;
    }

    public int getDeliveredCount() {
        return deliveredCount;
    }

    public int getQueueCount() {
        return queueCount;
    }

    // Check if the drone is free to accept a new order
    public boolean isAvailable() {

        return STATUS_IDLE.equalsIgnoreCase(status);
    }

    // Assign an order to the drone and mark it as busy
    public void assignOrder(int orderId) {

        // Make sure the order ID is valid
        if (orderId <= 0) {

            throw new IllegalArgumentException("Order ID must be positive.");
        }

        this.currentOrderId = orderId;
        this.status = STATUS_BUSY;
    }

    // Clear the current order and make the drone available again
    public void releaseOrder() {

        this.currentOrderId = null;
        this.status = STATUS_IDLE;
    }

    // Increase the number of completed deliveries
    public void incrementDeliveredCount() {

        this.deliveredCount++;
    }

    // Update the waiting queue count for the drone district
    public void updateQueueCount(int queueCount) {

        // Queue count cannot be less than zero
        if (queueCount < 0) {

            throw new IllegalArgumentException("Queue count cannot be negative.");
        }

        this.queueCount = queueCount;
    }

    // Restore saved drone data loaded from the database
    public void loadSavedState(String status, Integer currentOrderId,
                               int deliveredCount, int queueCount) {

        this.status = status;
        this.currentOrderId = currentOrderId;
        this.deliveredCount = deliveredCount;
        this.queueCount = queueCount;
    }
}