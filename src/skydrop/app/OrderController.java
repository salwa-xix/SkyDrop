package skydrop.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordinates order actions between the application logic and the database.
 *
 * The controller uses Order model methods first, then asks DatabaseController
 * to save the updated object. This keeps business changes inside the model and
 * persistence inside the database layer.
 */
public class OrderController {

    private final List<Order> orders;
    private final DatabaseController db;

    public OrderController(DatabaseController db) {
        this.orders = Collections.synchronizedList(new ArrayList<>());
        this.db = db;
    }

    /**
     * Creates a new order, saves it, and keeps a runtime copy for active threads.
     */
    public synchronized Order createOrder(String userPhone, String placeType, String placeName,
                                          String itemName, String district) {
        Order order = new Order(0, userPhone, placeType, placeName, itemName, district);

        if (db.insertOrder(order)) {
            orders.add(order);
        }

        return order;
    }

    /**
     * Finds an order from memory first, then falls back to the database.
     */
    public synchronized Order findOrderById(int orderId) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        return db.findOrderById(orderId);
    }

    /**
     * Updates an order status through the model, then saves it.
     */
    public synchronized boolean updateOrderStatus(int orderId, String status) {
        Order order = findOrderById(orderId);
        if (order == null) {
            return false;
        }

        order.updateStatus(status);
        return db.updateOrder(order);
    }

    /**
     * Assigns a drone to an order through the model, then saves it.
     */
    public synchronized boolean assignDroneToOrder(int orderId, int droneId) {
        Order order = findOrderById(orderId);
        if (order == null) {
            return false;
        }

        order.assignDrone(droneId);
        return db.updateOrder(order);
    }

    /**
     * Saves a user rating through the Order model.
     */
    public synchronized boolean saveRating(int orderId, int rating) {
        Order order = findOrderById(orderId);
        if (order == null) {
            return false;
        }

        order.addRating(rating);
        return db.updateOrder(order);
    }
}
