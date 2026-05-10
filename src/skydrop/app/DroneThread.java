package skydrop.app;

public class DroneThread extends Thread {

    private Drone drone;
    private OrderController orderController;
    private DatabaseController databaseController;
    private WeatherController weatherController;
    private FileController fileController;
    private boolean running = true;

    public DroneThread(Drone drone,
                       OrderController orderController,
                       DatabaseController databaseController,
                       WeatherController weatherController,
                       FileController fileController) {

        this.drone = drone;
        this.orderController = orderController;
        this.databaseController = databaseController;
        this.weatherController = weatherController;
        this.fileController = fileController;
    }

    @Override
    public void run() {

        // Log thread startup
        fileController.writeLog(
                "Drone " + drone.getDroneId() + " thread started."
        );

        while (running) {

            try {

                // Check if the drone is available to handle a new order
                if (drone.isAvailable()) {

                    Integer orderId =
                            databaseController.getNextWaitingOrderId(
                                    drone.getDistrict()
                            );

                    if (orderId != null) {
                        processOrder(orderId);
                    }
                }

                // Wait before checking for another waiting order
                Thread.sleep(1000);

            } catch (InterruptedException e) {

                running = false;

                fileController.writeLog(
                        "Drone " + drone.getDroneId() + " thread stopped."
                );

            } catch (Exception e) {

                fileController.writeLog(
                        "Drone " + drone.getDroneId()
                                + " error: "
                                + e.getMessage()
                );

                System.out.println(
                        "Drone " + drone.getDroneId()
                                + " error: "
                                + e.getMessage()
                );
            }
        }
    }

    private void processOrder(int orderId) throws InterruptedException {

        // Check weather before accepting the order
        boolean weatherSuitable =
                weatherController.isWeatherSuitable(drone.getDistrict());

        // Reject the order if weather is unsafe
        if (!weatherSuitable) {

            databaseController.updateOrderStatus(orderId, "Rejected");

            // Update queue count after removing the waiting order
            databaseController.updateQueueCountForDistrict(
                    drone.getDistrict()
            );

            fileController.writeLog(
                    "Order " + orderId
                            + " rejected because of bad weather"
            );

            return;
        }

        // Assign the order to the drone
        databaseController.assignOrderToDrone(
                orderId,
                drone.getDroneId()
        );

        // Mark the order as accepted
        databaseController.updateOrderStatus(orderId, "Accepted");

        fileController.writeLog(
                "Drone " + drone.getDroneId()
                        + " accepted order "
                        + orderId
        );

        // Mark the drone as busy in the database
        databaseController.markDroneBusy(
                drone.getDroneId(),
                orderId
        );

        // Update runtime drone object
        drone.assignOrder(orderId);

        // Update runtime order object if it exists
        Order order = orderController.findOrderById(orderId);

        if (order != null) {

            order.assignDrone(drone.getDroneId());
            order.updateStatus("Accepted");
        }

        // Update queue count after removing the waiting order
        databaseController.updateQueueCountForDistrict(
                drone.getDistrict()
        );

        // Simulate preparing the delivery
        Thread.sleep(10000);

        // Update order status to on the way
        databaseController.updateOrderStatus(orderId, "On the way");

        fileController.writeLog(
                "Order " + orderId + " is on the way"
        );

        if (order != null) {
            order.updateStatus("On the way");
        }

        // Simulate delivery time
        Thread.sleep(10000);

        // Mark the order as delivered
        databaseController.updateOrderStatus(orderId, "Delivered");

        fileController.writeLog(
                "Order " + orderId
                        + " delivered by Drone "
                        + drone.getDroneId()
        );

        if (order != null) {
            order.updateStatus("Delivered");
        }

        // Increase delivered count in the database
        databaseController.incrementDroneDeliveredCount(
                drone.getDroneId()
        );

        // Mark drone as idle in the database
        databaseController.markDroneIdle(
                drone.getDroneId()
        );

        // Update runtime drone object
        drone.releaseOrder();
        drone.incrementDeliveredCount();
    }

    public void stopDrone() {

        running = false;
        this.interrupt();
    }
}