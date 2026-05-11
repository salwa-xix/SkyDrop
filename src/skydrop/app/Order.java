package skydrop.app;

/**
 * Represents one delivery order in the SkyDrop system.
 *
 * The model owns the order state changes, such as assigning a drone,
 * changing delivery status, and saving a rating. DatabaseController is
 * responsible only for saving the final state to the database.
 */
public class Order {

    public static final String STATUS_WAITING = "Waiting";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_ON_THE_AIR = "On the air";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_REJECTED = "Rejected";

    private int orderId;
    private String userPhone;
    private String placeType;
    private String placeName;
    private String itemName;
    private String district;
    private String status;
    private int rating;
    private Integer assignedDroneId;

    public Order(int orderId, String userPhone, String placeType, String placeName,
                 String itemName, String district) {

        this.orderId = orderId;
        this.userPhone = userPhone;
        this.placeType = placeType;
        this.placeName = placeName;
        this.itemName = itemName;
        this.district = district;
        this.status = STATUS_WAITING;
        this.rating = 0;
        this.assignedDroneId = null;
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

    /**
     * Changes the delivery status using the model instead of updating raw
     * database fields directly from other classes.
     */
    public void updateStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Order status cannot be empty.");
        }
        this.status = status;
    }

    /**
     * Stores the drone that handled this order. The value is kept even after
     * delivery so the system can still know which drone delivered the order.
     */
    public void assignDrone(int droneId) {
        if (droneId <= 0) {
            throw new IllegalArgumentException("Drone ID must be positive.");
        }
        this.assignedDroneId = droneId;
    }

    /**
     * Saves the user's rating after delivery.
     */
    public void addRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    /**
     * Used only when loading an existing order from the database.
     */
    public void loadSavedState(String status, int rating, Integer assignedDroneId) {
        this.status = status;
        this.rating = rating;
        this.assignedDroneId = assignedDroneId;
    }
}
