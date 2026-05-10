package skydrop.app;

import java.time.LocalDateTime;

public class Order {

    private int orderId;
    private String userPhone;
    private String placeType;
    private String placeName;
    private String itemName;
    private String district;
    private String status;
    private int rating;
    private Integer assignedDroneId;
    private LocalDateTime createdAt;

    public Order(int orderId, String userPhone, String placeType, String placeName,
                 String itemName, String district) {

        this.orderId = orderId;
        this.userPhone = userPhone;
        this.placeType = placeType;
        this.placeName = placeName;
        this.itemName = itemName;
        this.district = district;

        // New orders always start as waiting until a drone handles them
        this.status = "Waiting";
        this.rating = 0;
        this.assignedDroneId = null;
        this.createdAt = LocalDateTime.now();
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public String getPlaceType() {
        return placeType;
    }

    public String getPlaceName() {
        return placeName;
    }

    public String getItemName() {
        return itemName;
    }

    public String getDistrict() {
        return district;
    }

    public String getStatus() {
        return status;
    }

    public int getRating() {
        return rating;
    }

    public Integer getAssignedDroneId() {
        return assignedDroneId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Update the order status during the delivery process
    public void updateStatus(String status) {
        this.status = status;
    }

    // Assign a drone to this order
    public void assignDrone(int droneId) {
        this.assignedDroneId = droneId;
    }

    // Remove the assigned drone when the delivery is finished
    public void removeDrone() {
        this.assignedDroneId = null;
    }

    // Save the user's rating after delivery
    public void addRating(int rating) {
        this.rating = rating;
    }
}