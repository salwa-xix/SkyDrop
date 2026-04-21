package skydrop.app;

public class OrderProcessThread extends Thread {
    private Order order;
    private OrderController orderController;
    private DroneController droneController;
    private WeatherController weatherController;
    private FileController fileController;
    private DatabaseController databaseController;

    public OrderProcessThread(Order order,
                              OrderController orderController,
                              DroneController droneController,
                              WeatherController weatherController,
                              FileController fileController,
                              DatabaseController databaseController) {
        this.order = order;
        this.orderController = orderController;
        this.droneController = droneController;
        this.weatherController = weatherController;
        this.fileController = fileController;
        this.databaseController = databaseController;
    }

    @Override
    public void run() {
        try {
            if (!weatherController.isWeatherSuitable(order.getDistrict())) {
                updateOrderState("Rejected", "Order " + order.getOrderId() + " rejected بسبب الطقس");
                return;
            }

            Drone drone = droneController.assignDroneToOrder(order);

            if (drone == null) {
                updateOrderState("Waiting", "Order " + order.getOrderId() + " waiting for available drone");
                return;
            }

            updateOrderState("Accepted", "Order " + order.getOrderId() + " accepted");

            Thread.sleep(3000);

            updateOrderState("On the way", "Order " + order.getOrderId() + " on the way");

            Thread.sleep(3000);

            updateOrderState("Delivered", "Order " + order.getOrderId() + " delivered");
            droneController.finishDelivery(order);
            droneController.refreshQueues(orderController.getAllOrders());

        } catch (InterruptedException e) {
            fileController.writeLog("Thread interrupted for order " + order.getOrderId());
        } catch (Exception e) {
            fileController.writeLog("Thread error for order " + order.getOrderId() + ": " + e.getMessage());
        }
    }

    private void updateOrderState(String status, String logMessage) {
        order.updateStatus(status);
        databaseController.updateOrderStatus(order.getOrderId(), status);
        fileController.writeLog(logMessage);
    }
}