package skydrop.app;

public class DroneThread extends Thread {

    private Drone drone;
    private OrderController orderController;
    private DroneController droneController;
    private DatabaseController databaseController;
    private FileController fileController;
    private boolean running = true;

    public DroneThread(Drone drone,
                       OrderController orderController,
                       DroneController droneController,
                       DatabaseController databaseController,
                       FileController fileController) {

        this.drone = drone;
        this.orderController = orderController;
        this.droneController = droneController;
        this.databaseController = databaseController;
        this.fileController = fileController;
    }

    @Override
    public void run() {

        fileController.writeLog(
                "Drone " + drone.getDroneId() + " thread started."
        );

        while (running) {

            try {

                if (drone.isAvailable()) {

                    Integer orderId = databaseController.tryClaimNextWaitingOrder(
                            drone.getDroneId(),
                            drone.getDistrict()
                    );

                    if (orderId != null) {
                        deliverOrder(orderId);
                    }
                }

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
            }
        }
    }

    private void deliverOrder(int orderId) throws InterruptedException {

        drone.assignOrder(orderId);

        Order order = orderController.findOrderById(orderId);

        if (order != null) {
            order.assignDrone(drone.getDroneId());
            order.updateStatus("Accepted");
        }

        fileController.writeLog(
                "Drone " + drone.getDroneId()
                        + " accepted order "
                        + orderId
        );

        Thread.sleep(10000);

        databaseController.updateOrderStatus(orderId, "On the way");

        if (order != null) {
            order.updateStatus("On the way");
        }

        fileController.writeLog(
                "Order " + orderId + " is on the way"
        );

        Thread.sleep(10000);

        databaseController.updateOrderStatus(orderId, "Delivered");

        if (order != null) {
            order.updateStatus("Delivered");
        }

        databaseController.releaseDrone(drone.getDroneId());

        drone.releaseOrder();
        drone.incrementDeliveredCount();

        droneController.refreshQueues(orderController.getAllOrders());

        fileController.writeLog(
                "Order " + orderId
                        + " delivered by Drone "
                        + drone.getDroneId()
        );
    }

    public void stopDrone() {
        running = false;
        this.interrupt();
    }
}