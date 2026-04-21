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
        this.status = "Waiting";
        this.rating = 0;
        this.assignedDroneId = null;
        this.createdAt = LocalDateTime.now();
    }

    public int getOrderId() {
        return orderId;
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

    public void setPlaceType(String placeType) {
        this.placeType = placeType;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    public void assignDrone(int droneId) {
        this.assignedDroneId = droneId;
    }

    public void removeDrone() {
        this.assignedDroneId = null;
    }

    public void addRating(int rating) {
        this.rating = rating;
    }
}