package skydrop.app;

/**
 * Runs the delivery simulation for one drone.
 *
 * The thread only controls timing and delegates state changes to the model
 * controllers. Model objects are updated first, then saved in the database.
 */
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
                if (drone.isAvailable()) {
                    Integer orderId = databaseController.getNextWaitingOrderId(drone.getDistrict());
                    if (orderId != null) {
                        processOrder(orderId);
                    }
                }

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

    /**
     * Handles the full lifecycle of one order for this drone.
     */
    private void processOrder(int orderId) throws InterruptedException {
        Order order = orderController.findOrderById(orderId);
        if (order == null) {
            fileController.writeLog("Order " + orderId + " was not found for drone " + drone.getDroneId());
            return;
        }

        if (!weatherController.isWeatherSuitable(drone.getDistrict())) {
            orderController.updateOrderStatus(orderId, Order.STATUS_REJECTED);
            databaseController.updateQueueCountForDistrict(drone.getDistrict());
            droneController.refreshQueueCount(drone);
            fileController.writeLog("Order " + orderId + " rejected because of bad weather");
            return;
        }

        orderController.assignDroneToOrder(orderId, drone.getDroneId());
        orderController.updateOrderStatus(orderId, Order.STATUS_ACCEPTED);
        droneController.assignOrderToDrone(drone, orderId);
        databaseController.updateQueueCountForDistrict(drone.getDistrict());
        droneController.refreshQueueCount(drone);

        fileController.writeLog("Drone " + drone.getDroneId() + " accepted order " + orderId);

        Thread.sleep(PREPARING_TIME_MS);

        orderController.updateOrderStatus(orderId, Order.STATUS_ON_THE_AIR);
        fileController.writeLog("Order " + orderId + " is on the air");

        Thread.sleep(DELIVERY_TIME_MS);

        orderController.updateOrderStatus(orderId, Order.STATUS_DELIVERED);
        droneController.incrementDeliveredCount(drone);
        droneController.releaseDrone(drone);

        fileController.writeLog("Order " + orderId + " delivered by Drone " + drone.getDroneId());
    }

    /**
     * Stops this drone thread safely.
     */
    public void stopDrone() {
        running = false;
        this.interrupt();
    }
}
