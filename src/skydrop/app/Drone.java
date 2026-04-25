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

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCurrentOrderId(Integer currentOrderId) {
        this.currentOrderId = currentOrderId;
    }

    public void setDeliveredCount(int deliveredCount) {
        this.deliveredCount = deliveredCount;
    }

    public void setQueueCount(int queueCount) {
        this.queueCount = queueCount;
    }

    public boolean isAvailable() {
        return status.equalsIgnoreCase("Idle");
    }

    public void assignOrder(int orderId) {
        this.currentOrderId = orderId;
        this.status = "Busy";
    }

    public void releaseOrder() {
        this.currentOrderId = null;
        this.status = "Idle";
    }

    public void incrementDeliveredCount() {
        this.deliveredCount++;
    }
}