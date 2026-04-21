package skydrop.app;
import java.time.LocalDateTime;
import java.util.List;

public class Report {
    private int totalOrders;
    private int acceptedOrders;
    private int rejectedOrders;
    private int deliveredOrders;
    private String droneSummary;
    private LocalDateTime generatedAt;

    public Report() {
        this.totalOrders = 0;
        this.acceptedOrders = 0;
        this.rejectedOrders = 0;
        this.deliveredOrders = 0;
        this.droneSummary = "";
        this.generatedAt = LocalDateTime.now();
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public int getAcceptedOrders() {
        return acceptedOrders;
    }

    public int getRejectedOrders() {
        return rejectedOrders;
    }

    public int getDeliveredOrders() {
        return deliveredOrders;
    }

    public String getDroneSummary() {
        return droneSummary;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void generateReport(List<Order> orders, List<Drone> drones) {
        reset();
        for (Order order : orders) {
            totalOrders++;
            if (order.getStatus().equalsIgnoreCase("Accepted")
                    || order.getStatus().equalsIgnoreCase("On the way")) {
                acceptedOrders++;
            }
            if (order.getStatus().equalsIgnoreCase("Rejected")) {
                rejectedOrders++;
            }
            if (order.getStatus().equalsIgnoreCase("Delivered")) {
                deliveredOrders++;
            }
        }
        buildDroneSummary(drones);
        generatedAt = LocalDateTime.now();
    }

    public void generateReportByDrone(List<Order> orders, List<Drone> drones, int droneId) {
        reset();
        for (Order order : orders) {
            if (order.getAssignedDroneId() != null && order.getAssignedDroneId() == droneId) {
                totalOrders++;
                if (order.getStatus().equalsIgnoreCase("Accepted")
                        || order.getStatus().equalsIgnoreCase("On the way")) {
                    acceptedOrders++;
                }
                if (order.getStatus().equalsIgnoreCase("Rejected")) {
                    rejectedOrders++;
                }
                if (order.getStatus().equalsIgnoreCase("Delivered")) {
                    deliveredOrders++;
                }
            }
        }

        StringBuilder sb = new StringBuilder();
        for (Drone drone : drones) {
            if (drone.getDroneId() == droneId) {
                sb.append("Drone ").append(drone.getDroneId())
                        .append(" - District: ").append(drone.getDistrict())
                        .append(" - Delivered: ").append(drone.getDeliveredCount())
                        .append("\n");
            }
        }
        droneSummary = sb.toString();
        generatedAt = LocalDateTime.now();
    }

    public void generateReportByDateRange(List<Order> orders, List<Drone> drones,
                                          LocalDateTime from, LocalDateTime to) {
        reset();
        for (Order order : orders) {
            if ((order.getCreatedAt().isEqual(from) || order.getCreatedAt().isAfter(from))
                    && (order.getCreatedAt().isEqual(to) || order.getCreatedAt().isBefore(to))) {
                totalOrders++;
                if (order.getStatus().equalsIgnoreCase("Accepted")
                        || order.getStatus().equalsIgnoreCase("On the way")) {
                    acceptedOrders++;
                }
                if (order.getStatus().equalsIgnoreCase("Rejected")) {
                    rejectedOrders++;
                }
                if (order.getStatus().equalsIgnoreCase("Delivered")) {
                    deliveredOrders++;
                }
            }
        }
        buildDroneSummary(drones);
        generatedAt = LocalDateTime.now();
    }

    private void buildDroneSummary(List<Drone> drones) {
        StringBuilder sb = new StringBuilder();
        for (Drone drone : drones) {
            sb.append("Drone ").append(drone.getDroneId())
                    .append(" - District: ").append(drone.getDistrict())
                    .append(" - Delivered: ").append(drone.getDeliveredCount())
                    .append("\n");
        }
        droneSummary = sb.toString();
    }

    private void reset() {
        totalOrders = 0;
        acceptedOrders = 0;
        rejectedOrders = 0;
        deliveredOrders = 0;
        droneSummary = "";
    }

    public String formatAsText() {
        return "Generated At: " + generatedAt + "\n"
                + "Total Orders: " + totalOrders + "\n"
                + "Accepted Orders: " + acceptedOrders + "\n"
                + "Rejected Orders: " + rejectedOrders + "\n"
                + "Delivered Orders: " + deliveredOrders + "\n\n"
                + droneSummary;
    }
}