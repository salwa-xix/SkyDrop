package skydrop.app;

/**
 * Represents one delivery drone.
 *
 * The model owns runtime state changes such as assigning an order, becoming
 * idle, and increasing the delivery count. The database layer saves the state
 * after these methods are called.
 */
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
        this.status = STATUS_IDLE;
        this.currentOrderId = null;
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

    /**
     * Returns true when the drone can accept another order.
     */
    public boolean isAvailable() {
        return STATUS_IDLE.equalsIgnoreCase(status);
    }

    /**
     * Assigns a new order and marks the drone as busy.
     */
    public void assignOrder(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID must be positive.");
        }
        this.currentOrderId = orderId;
        this.status = STATUS_BUSY;
    }

    /**
     * Releases the current order and marks the drone as idle.
     */
    public void releaseOrder() {
        this.currentOrderId = null;
        this.status = STATUS_IDLE;
    }

    /**
     * Increases the successful delivery count in the runtime object.
     */
    public void incrementDeliveredCount() {
        this.deliveredCount++;
    }

    /**
     * Updates the number of waiting orders in this drone's district.
     */
    public void updateQueueCount(int queueCount) {
        if (queueCount < 0) {
            throw new IllegalArgumentException("Queue count cannot be negative.");
        }
        this.queueCount = queueCount;
    }

    /**
     * Used only when loading an existing drone from the database.
     */
    public void loadSavedState(String status, Integer currentOrderId,
                               int deliveredCount, int queueCount) {
        this.status = status;
        this.currentOrderId = currentOrderId;
        this.deliveredCount = deliveredCount;
        this.queueCount = queueCount;
    }
}
