package skydrop.app;

import java.util.ArrayList;

public class OrderController {

    private ArrayList<Order> orders;
    private DatabaseController db;

    public OrderController(DatabaseController db) {

        // Store orders during runtime
        this.orders = new ArrayList<>();

        // Use the database to save orders permanently
        this.db = db;
    }

    // Create a new order, save it in the database, and keep it in memory
    public Order createOrder(String userPhone, String placeType, String placeName,
                             String itemName, String district) {

        Order order = new Order(
                0,
                userPhone,
                placeType,
                placeName,
                itemName,
                district
        );

        db.insertOrder(order);

        orders.add(order);

        return order;
    }

    // Find an order by its ID from the runtime list
    public Order findOrderById(int orderId) {

        for (Order order : orders) {

            if (order.getOrderId() == orderId) {
                return order;
            }
        }

        return null;
    }

    // Return all runtime orders
    public ArrayList<Order> getAllOrders() {
        return orders;
    }
}