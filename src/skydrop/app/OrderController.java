package skydrop.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Manage order operations between the application and the database
public class OrderController {

    private final List<Order> orders;
    private final DatabaseController db;

    public OrderController(DatabaseController db) {

        // Store runtime order objects used by active threads
        this.orders = Collections.synchronizedList(new ArrayList<>());

        this.db = db;
    }

    // Create a new order and save it in the database
    public synchronized Order createOrder(String userPhone, String placeType, String placeName,
                                          String itemName, String district) {

        // Create the runtime order object
        Order order = new Order(
                0,
                userPhone,
                placeType,
                placeName,
                itemName,
                district
        );

        // Save the order and keep a runtime copy for active threads
        if (db.insertOrder(order)) {

            orders.add(order);
        }

        return order;
    }

    // Find an order from memory first, then check the database if needed
    public synchronized Order findOrderById(int orderId) {

        for (Order order : orders) {

            if (order.getOrderId() == orderId) {

                return order;
            }
        }

        // Load the order from the database if it is not in memory
        return db.findOrderById(orderId);
    }

    // Update the order status and save the changes
    public synchronized boolean updateOrderStatus(int orderId, String status) {

        // Find the order object before updating it
        Order order = findOrderById(orderId);

        if (order == null) {

            return false;
        }

        // Update the runtime object first
        order.updateStatus(status);

        // Save the updated state in the database
        return db.updateOrder(order);
    }

    // Assign a drone to the order and save the changes
    public synchronized boolean assignDroneToOrder(int orderId, int droneId) {

        // Find the order object before assigning the drone
        Order order = findOrderById(orderId);

        if (order == null) {

            return false;
        }

        // Update the runtime order object
        order.assignDrone(droneId);

        // Save the updated order in the database
        return db.updateOrder(order);
    }

    // Save the user rating after delivery
    public synchronized boolean saveRating(int orderId, int rating) {

        // Find the order before saving the rating
        Order order = findOrderById(orderId);

        if (order == null) {

            return false;
        }

        // Update the rating inside the order object
        order.addRating(rating);

        // Save the updated order in the database
        return db.updateOrder(order);
    }
}