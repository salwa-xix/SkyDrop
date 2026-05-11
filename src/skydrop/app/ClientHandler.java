package skydrop.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Handles one client request sent from the GUI.
 *
 * The request protocol is kept unchanged so the frontend screens do not need
 * to be modified. This class validates incoming requests, delegates work to
 * controllers, and sends a simple text response back to the client.
 */
public class ClientHandler extends Thread {

    private final Socket socket;
    private final DatabaseController db;
    private final OrderController orderController;
    private final FileController fileController;

    public ClientHandler(Socket socket,
                         DatabaseController db,
                         OrderController orderController,
                         FileController fileController) {
        this.socket = socket;
        this.db = db;
        this.orderController = orderController;
        this.fileController = fileController;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request = in.readLine();

            if (request == null || request.trim().isEmpty()) {
                out.println("ERROR|Empty request");
                fileController.writeLog("Empty request received from client.");
                return;
            }

            String[] parts = request.split("\\|", -1);
            String command = parts[0];

            switch (command) {
                case "LOGIN" -> handleLogin(parts, out);
                case "SIGNUP" -> handleSignup(parts, out);
                case "CREATE_ORDER" -> handleCreateOrder(parts, out);
                case "GET_STATUS" -> handleGetStatus(parts, out);
                case "SAVE_RATING" -> handleSaveRating(parts, out);
                case "GET_DRONES" -> handleGetDrones(out);
                case "GET_REPORT" -> handleGetReport(out);
                case "SAVE_REPORT" -> handleSaveReport(out);
                default -> {
                    fileController.writeLog("Unknown command received: " + command);
                    out.println("UNKNOWN_COMMAND");
                }
            }

        } catch (IOException e) {
            fileController.writeLog("Client error: " + e.getMessage());
            System.out.println("Client error: " + e.getMessage());
        } catch (Exception e) {
            fileController.writeLog("Request handling error: " + e.getMessage());
            System.out.println("Request handling error: " + e.getMessage());
        }
    }

    /**
     * Handles user login without changing the existing response format.
     */
    private void handleLogin(String[] parts, PrintWriter out) {
        if (!hasLength(parts, 3, out)) {
            return;
        }

        String phone = parts[1];
        String password = parts[2];
        User user = db.findUserByPhone(phone);

        if (user != null && user.getPassword().equals(password)) {
            fileController.writeLog("Login success for user phone: " + phone);
            out.println("SUCCESS|" + user.getName() + "|" + user.getPhone() + "|" + user.getDistrict());
        } else {
            fileController.writeLog("Login failed for phone: " + phone);
            out.println("FAIL");
        }
    }

    /**
     * Creates a new user account.
     */
    private void handleSignup(String[] parts, PrintWriter out) {
        if (!hasLength(parts, 5, out)) {
            return;
        }

        String name = parts[1];
        String phone = parts[2];
        String password = parts[3];
        String district = parts[4];

        // conditions
        if (db.userExists(phone)) {
            fileController.writeLog("Sign up failed. User already exists: " + phone);
            out.println("USER_EXISTS");
            return;
        }

        User user = new User(name, phone, password, district);
        if (db.insertUser(user)) {
            fileController.writeLog("New user registered: " + phone);
            out.println("SIGNUP_SUCCESS");
        } else {
            fileController.writeLog("Sign up failed while saving user: " + phone);
            out.println("ERROR|Signup failed");
        }
    }

    /**
     * Creates an order through OrderController so the Order model is used.
     */
    private void handleCreateOrder(String[] parts, PrintWriter out) {
        if (!hasLength(parts, 6, out)) {
            return;
        }

        Order order = orderController.createOrder(
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5]
        );

        if (order.getOrderId() > 0) {
            fileController.writeLog("Order " + order.getOrderId() + " created for user " + parts[1]
                    + " in district " + parts[5]);
            out.println("ORDER_CREATED|" + order.getOrderId());
        } else {
            fileController.writeLog("Order creation failed for user " + parts[1]);
            out.println("ERROR|Order creation failed");
        }
    }

    /**
     * Reads the latest status from the database.
     */
    private void handleGetStatus(String[] parts, PrintWriter out) {
        if (!hasLength(parts, 2, out)) {
            return;
        }

        Integer orderId = parseInteger(parts[1], out);
        if (orderId == null) {
            return;
        }

        String status = db.getOrderStatus(orderId);
        out.println(status != null ? "STATUS|" + status : "STATUS|UNKNOWN");
    }

    /**
     * Saves a rating through the Order model and then persists the order.
     */
    private void handleSaveRating(String[] parts, PrintWriter out) {
        if (!hasLength(parts, 3, out)) {
            return;
        }

        Integer orderId = parseInteger(parts[1], out);
        Integer rating = parseInteger(parts[2], out);
        if (orderId == null || rating == null) {
            return;
        }

        try {
            if (orderController.saveRating(orderId, rating)) {
                fileController.writeLog("Rating " + rating + " saved for order " + orderId);
                out.println("RATING_SAVED");
            } else {
                fileController.writeLog("Rating save failed for order " + orderId);
                out.println("ERROR|Rating not saved");
            }
        } catch (IllegalArgumentException e) {
            out.println("ERROR|" + e.getMessage());
        }
    }

    /**
     * Sends current drone data to the dashboard screen.
     */
    private void handleGetDrones(PrintWriter out) {
        ArrayList<Drone> drones = db.loadDrones();
        StringBuilder response = new StringBuilder("DRONES|");

        for (int i = 0; i < drones.size(); i++) {
            Drone d = drones.get(i);
            String currentOrder = d.getCurrentOrderId() == null ? "None" : String.valueOf(d.getCurrentOrderId());

            response.append(d.getDroneId())
                    .append(",")
                    .append(d.getDistrict())
                    .append(",")
                    .append(d.getStatus())
                    .append(",")
                    .append(currentOrder)
                    .append(",")
                    .append(d.getQueueCount());

            if (i < drones.size() - 1) {
                response.append(";");
            }
        }

        out.println(response.toString());
    }

    /**
     * Builds the report response used by ReportScreen.
     */
    private void handleGetReport(PrintWriter out) {
        Report report = new Report(db);
        out.println(report.generateReportResponse());
    }

    /**
     * Saves a report text file without changing the frontend command.
     */
    private void handleSaveReport(PrintWriter out) {
        Report report = new Report(db);
        String reportText = report.generateReportText();
        fileController.saveReportToFile(reportText);
        fileController.writeLog("Report saved to report.txt");
        out.println("REPORT_SAVED");
    }

    private boolean hasLength(String[] parts, int expectedLength, PrintWriter out) {
        if (parts.length < expectedLength) {
            out.println("ERROR|Invalid request");
            fileController.writeLog("Invalid request. Expected " + expectedLength + " parts but received " + parts.length);
            return false;
        }
        return true;
    }

    private Integer parseInteger(String value, PrintWriter out) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            out.println("ERROR|Invalid number");
            fileController.writeLog("Invalid number received: " + value);
            return null;
        }
    }
}
