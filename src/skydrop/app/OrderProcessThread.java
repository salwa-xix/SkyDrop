package skydrop.app;

public class OrderProcessThread extends Thread {

    private Order order;
    private WeatherController weatherController;
    private DatabaseController databaseController;
    private FileController fileController;

    public OrderProcessThread(Order order,
                              WeatherController weatherController,
                              DatabaseController databaseController,
                              FileController fileController) {

        this.order = order;
        this.weatherController = weatherController;
        this.databaseController = databaseController;
        this.fileController = fileController;
    }

    @Override
    public void run() {

        if (!weatherController.isWeatherSuitable(order.getDistrict())) {

            order.updateStatus("Rejected");

            databaseController.updateOrderStatus(
                    order.getOrderId(),
                    "Rejected"
            );

            fileController.writeLog(
                    "Order "
                            + order.getOrderId()
                            + " rejected — bad weather"
            );

            return;
        }

        fileController.writeLog(
                "Order "
                        + order.getOrderId()
                        + " is waiting for a drone"
        );
    }
}







//package skydrop.app;
//
//public class OrderProcessThread extends Thread {
//    private Order order;
//    private OrderController orderController;
//    private DroneController droneController;
//    private WeatherController weatherController;
//    private FileController fileController;
//    private DatabaseController databaseController;
//
//    public OrderProcessThread(Order order,
//                              OrderController orderController,
//                              DroneController droneController,
//                              WeatherController weatherController,
//                              FileController fileController,
//                              DatabaseController databaseController) {
//        this.order = order;
//        this.orderController = orderController;
//        this.droneController = droneController;
//        this.weatherController = weatherController;
//        this.fileController = fileController;
//        this.databaseController = databaseController;
//    }
//
//    @Override
//    public void run() {
//
//        try {
//
//            // Check weather first
//            if (!weatherController.isWeatherSuitable(order.getDistrict())) {
//
//                updateOrderState(
//                        "Rejected",
//                        "Order " + order.getOrderId() + " rejected بسبب الطقس"
//                );
//
//                return;
//            }
//
//            // Find available drone
//            Drone drone = droneController.assignDroneToOrder(order);
//
//            // No drone available
//            if (drone == null) {
//
//                updateOrderState(
//                        "Waiting",
//                        "Order " + order.getOrderId() + " waiting for available drone"
//                );
//
//                return;
//            }
//
//            // Save drone assignment in DB
//            databaseController.updateAssignedDrone(
//                    order.getOrderId(),
//                    drone.getDroneId()
//            );
//
//            databaseController.updateDrone(drone);
//
//            // Accepted
//            updateOrderState(
//                    "Accepted",
//                    "Order " + order.getOrderId() + " accepted"
//            );
//
//            Thread.sleep(20000);
//
//            // On the way
//            updateOrderState(
//                    "On the way",
//                    "Order " + order.getOrderId() + " on the way"
//            );
//
//            Thread.sleep(20000);
//
//            // Delivered
//            updateOrderState(
//                    "Delivered",
//                    "Order " + order.getOrderId() + " delivered"
//            );
//
//            // Release drone
//            droneController.finishDelivery(order);
//
//            // Update drone in DB after delivery
//            databaseController.updateDrone(drone);
//
//            // Refresh queues
//            droneController.refreshQueues(
//                    orderController.getAllOrders()
//            );
//
//        } catch (InterruptedException e) {
//
//            fileController.writeLog(
//                    "Thread interrupted for order "
//                            + order.getOrderId()
//            );
//
//        } catch (Exception e) {
//
//            fileController.writeLog(
//                    "Thread error for order "
//                            + order.getOrderId()
//                            + ": "
//                            + e.getMessage()
//            );
//        }
//    }
//
//    private void updateOrderState(String status, String logMessage) {
//        order.updateStatus(status);
//        databaseController.updateOrderStatus(order.getOrderId(), status);
//        fileController.writeLog(logMessage);
//    }
//}