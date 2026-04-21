package skydrop.app;

import java.util.ArrayList;

public class OrderController {
    private ArrayList<Order> orders;
    private int nextOrderId;

    public OrderController() {
        orders = new ArrayList<>();
        nextOrderId = 1;
    }

    public Order createOrder(String userPhone, String placeType, String placeName,
                             String itemName, String district) {
        Order order = new Order(nextOrderId++, userPhone, placeType, placeName, itemName, district);
        orders.add(order);
        return order;
    }

    public Order findOrderById(int orderId) {
        for (Order order : orders) {
            if (order.getOrderId() == orderId) {
                return order;
            }
        }
        return null;
    }

    public ArrayList<Order> getAllOrders() {
        return orders;
    }
}