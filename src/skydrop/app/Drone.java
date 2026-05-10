package skydrop.app;

public class Drone {

    private int droneId;
    private String district;
    private String status;
    private Integer currentOrderId;
    private int deliveredCount;
    private int queueCount;

    public Drone(int droneId, String district) {

        this.droneId = droneId;
        this.district = district;
        this.status = "Idle";
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

    // Update the current drone status
    public void setStatus(String status) {
        this.status = status;
    }

    // Set the current assigned order ID
    public void setCurrentOrderId(Integer currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    // Update delivered orders count manually if needed
    public void setDeliveredCount(int deliveredCount) {
        this.deliveredCount = deliveredCount;
    }

    // Check if the drone is free to accept a new order
    public boolean isAvailable() {
        return status.equalsIgnoreCase("Idle");
    }

    // Assign a new order to this drone
    public void assignOrder(int orderId) {

        this.currentOrderId = orderId;
        this.status = "Busy";
    }

    // Release the current order after delivery is completed
    public void releaseOrder() {

        this.currentOrderId = null;
        this.status = "Idle";
    }

    // Increase the successful delivery count
    public void incrementDeliveredCount() {
        this.deliveredCount++;
    }

    // Update the number of waiting orders for this drone
    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }
}