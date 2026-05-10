package skydrop.app;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Report {

    private DatabaseController db;
    private LocalDateTime generatedAt;

    public Report(DatabaseController db) {

        // Store the database controller so the report can get its data from DB queries
        this.db = db;

        // Store the time when this report object was created
        this.generatedAt = LocalDateTime.now();
    }

    // Generate structured report data for ReportScreen
    public String generateReportResponse() {

        int total = db.getTotalOrders();
        int accepted = db.getAcceptedOrdersCount();
        int rejected = db.getRejectedOrdersCount();

        ArrayList<Drone> drones = db.loadDrones();

        StringBuilder response = new StringBuilder("REPORT|");

        response.append(total)
                .append("|")
                .append(accepted)
                .append("|")
                .append(rejected)
                .append("|")
                .append(generatedAt)
                .append("|");

        // Add drone data as: droneId,district,deliveredCount,queueCount
        for (int i = 0; i < drones.size(); i++) {

            Drone drone = drones.get(i);

            response.append(drone.getDroneId())
                    .append(",")
                    .append(drone.getDistrict())
                    .append(",")
                    .append(drone.getDeliveredCount())
                    .append(",")
                    .append(drone.getQueueCount());

            if (i < drones.size() - 1) {
                response.append(";");
            }
        }

        return response.toString();
    }

    // Generate a readable text report for saving as a TXT file
    public String generateReportText() {

        int total = db.getTotalOrders();
        int accepted = db.getAcceptedOrdersCount();
        int rejected = db.getRejectedOrdersCount();

        ArrayList<Drone> drones = db.loadDrones();

        StringBuilder text = new StringBuilder();

        text.append("SkyDrop Delivery Report\n");
        text.append("=======================\n");
        text.append("Generated At: ").append(generatedAt).append("\n\n");

        text.append("Total Orders: ").append(total).append("\n");
        text.append("Accepted Orders: ").append(accepted).append("\n");
        text.append("Rejected Orders: ").append(rejected).append("\n\n");

        text.append("Drone Summary:\n");

        for (Drone drone : drones) {

            text.append("Drone ").append(drone.getDroneId())
                    .append(" | District: ").append(drone.getDistrict())
                    .append(" | Delivered: ").append(drone.getDeliveredCount())
                    .append(" | Queue: ").append(drone.getQueueCount())
                    .append("\n");
        }

        return text.toString();
    }
}