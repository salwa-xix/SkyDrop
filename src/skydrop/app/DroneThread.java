package skydrop.app;

import java.util.ArrayList;

public class DroneThread extends Thread {

    private Drone drone;
    private ArrayList<Order> orders;
    private DatabaseController databaseController;
    private FileController fileController;
    private boolean running = true;

    public DroneThread(Drone drone,
                       ArrayList<Order> orders,
                       DatabaseController databaseController,
                       FileController fileController) {
        this.drone = drone;
        this.orders = orders;
        this.databaseController = databaseController;
        this.fileController = fileController;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Order order = findWaitingOrderForDrone();

                if (order != null) {
                    processOrder(order);
                }

                Thread.sleep(1000); // drone checks every 1 second

            } catch (InterruptedException e) {
                running = false;
                fileController.writeLog("Drone " + drone.getDroneId() + " thread stopped.");
            } catch (Exception e) {
                fileController.writeLog("Error in Drone " + drone.getDroneId() + ": " + e.getMessage());
            }
        }
    }

    private synchronized Order findWaitingOrderForDrone() {
        for (Order order : orders) {
            if (order.getStatus().equalsIgnoreCase("Waiting")
                    && order.getDistrict().equalsIgnoreCase(drone.getDistrict())) {

                order.assignDrone(drone.getDroneId());
                order.updateStatus("In Progress");

                drone.assignOrder(order.getOrderId());

                databaseController.updateOrderStatus(order.getOrderId(), "In Progress");
                fileController.writeLog("Drone " + drone.getDroneId()
                        + " assigned to order " + order.getOrderId());

                return order;
            }
        }
        return null;
    }

    private void processOrder(Order order) throws InterruptedException {
        Thread.sleep(5000); // simulate delivery time

        order.updateStatus("Delivered");
        databaseController.updateOrderStatus(order.getOrderId(), "Delivered");

        drone.releaseOrder();
        drone.incrementDeliveredCount();

        fileController.writeLog("Order " + order.getOrderId()
                + " delivered by Drone " + drone.getDroneId());
    }

    public void stopDrone() {
        running = false;
        this.interrupt();
    }
}