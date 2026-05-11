package skydrop.app;

public class DroneThread extends Thread {

    private static final int CHECK_INTERVAL_MS = 1000;
    private static final int PREPARING_TIME_MS = 10000;
    private static final int DELIVERY_TIME_MS = 10000;

    private final Drone drone;
    private final DroneController droneController;
    private final OrderController orderController;
    private final DatabaseController databaseController;
    private final WeatherController weatherController;
    private final FileController fileController;
    private boolean running = true;

    public DroneThread(Drone drone,
                       DroneController droneController,
                       OrderController orderController,
                       DatabaseController databaseController,
                       WeatherController weatherController,
                       FileController fileController) {

        this.drone = drone;
        this.droneController = droneController;
        this.orderController = orderController;
        this.databaseController = databaseController;
        this.weatherController = weatherController;
        this.fileController = fileController;
    }

    @Override
    public void run() {

        fileController.writeLog("Drone " + drone.getDroneId() + " thread started.");

        while (running) {

            try {

                // Only search for a new order when the drone is available
                if (drone.isAvailable()) {

                    // Get the oldest waiting order in the same district
                    Integer orderId = databaseController.getNextWaitingOrderId(drone.getDistrict());

                    if (orderId != null) {
                        processOrder(orderId);
                    }
                }

                // Wait before checking for new orders again
                Thread.sleep(CHECK_INTERVAL_MS);

            } catch (InterruptedException e) {

                running = false;

                fileController.writeLog("Drone " + drone.getDroneId() + " thread stopped.");

            } catch (Exception e) {

                fileController.writeLog("Drone " + drone.getDroneId() + " error: " + e.getMessage());

                System.out.println("Drone " + drone.getDroneId() + " error: " + e.getMessage());
            }
        }
    }

    // Handle the full delivery flow for one order
    private void processOrder(int orderId) throws InterruptedException {

        // Load the order object before changing its state
        Order order = orderController.findOrderById(orderId);

        if (order == null) {

            fileController.writeLog("Order " + orderId + " was not found for drone " + drone.getDroneId());

            return;
        }

        // Reject the order if the weather is not suitable for flying
        if (!weatherController.isWeatherSuitable(drone.getDistrict())) {

            orderController.updateOrderStatus(orderId, Order.STATUS_REJECTED);

            databaseController.updateQueueCountForDistrict(drone.getDistrict());

            droneController.refreshQueueCount(drone);

            fileController.writeLog("Order " + orderId + " rejected because of bad weather");

            return;
        }

        // Assign the drone to the order and mark the order as accepted
        orderController.assignDroneToOrder(orderId, drone.getDroneId());
        orderController.updateOrderStatus(orderId, Order.STATUS_ACCEPTED);

        // Mark the drone as busy with this order
        droneController.assignOrderToDrone(drone, orderId);

        // Refresh the waiting queue count after accepting the order
        databaseController.updateQueueCountForDistrict(drone.getDistrict());

        droneController.refreshQueueCount(drone);

        fileController.writeLog("Drone " + drone.getDroneId() + " accepted order " + orderId);

        // Simulate order preparation time
        Thread.sleep(PREPARING_TIME_MS);

        // Move the order to the flying stage
        orderController.updateOrderStatus(orderId, Order.STATUS_ON_THE_AIR);

        fileController.writeLog("Order " + orderId + " is on the air");

        // Simulate delivery time
        Thread.sleep(DELIVERY_TIME_MS);

        // Mark the order as delivered
        orderController.updateOrderStatus(orderId, Order.STATUS_DELIVERED);

        // Increase the drone delivery count
        droneController.incrementDeliveredCount(drone);

        // Release the drone so it can take another order
        droneController.releaseDrone(drone);

        fileController.writeLog("Order " + orderId + " delivered by Drone " + drone.getDroneId());
    }

    // Stop this drone thread safely
    public void stopDrone() {

        running = false;

        this.interrupt();
    }
}