package skydrop.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

// Handle one client connection and process requests from the GUI
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

            // Make sure the client sent a valid request
            if (request == null || request.trim().isEmpty()) {
                out.println("ERROR|Empty request");
                fileController.writeLog("Empty request received from client.");
                return;
            }

            // Split the request using the protocol separator
            String[] parts = request.split("\\|", -1);
            String command = parts[0];

            // Handle the request based on the command type
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

    // Check user login information and send the result back to the client
    private void handleLogin(String[] parts, PrintWriter out) {

        if (!hasLength(parts, 3, out)) {
            return;
        }

        String phone = parts[1];
        String password = parts[2];

        // Find the user using the phone number
        User user = db.findUserByPhone(phone);

        // Compare the password and send the login result
        if (user != null && user.getPassword().equals(password)) {

            fileController.writeLog("Login success for user phone: " + phone);

            out.println("SUCCESS|" + user.getName() + "|" + user.getPhone() + "|" + user.getDistrict());

        } else {

            fileController.writeLog("Login failed for phone: " + phone);
            out.println("FAIL");
        }
    }

    // Create a new user account if the phone number is not already used
    private void handleSignup(String[] parts, PrintWriter out) {

        if (!hasLength(parts, 5, out)) {
            return;
        }

        String name = parts[1];
        String phone = parts[2];
        String password = parts[3];
        String district = parts[4];

        // Check if the user already exists
        if (db.userExists(phone)) {

            fileController.writeLog("Sign up failed. User already exists: " + phone);
            out.println("USER_EXISTS");
            return;
        }

        // Create a new user object
        User user = new User(name, phone, password, district);

        // Save the new user in the database
        if (db.insertUser(user)) {

            fileController.writeLog("New user registered: " + phone);
            out.println("SIGNUP_SUCCESS");

        } else {

            fileController.writeLog("Sign up failed while saving user: " + phone);
            out.println("ERROR|Signup failed");
        }
    }

    // Create a new delivery order and save it through OrderController
    private void handleCreateOrder(String[] parts, PrintWriter out) {

        if (!hasLength(parts, 6, out)) {
            return;
        }

        // Create the order using the OrderController
        Order order = orderController.createOrder(
                parts[1],
                parts[2],
                parts[3],
                parts[4],
                parts[5]
        );

        // Send the order ID if the order was created successfully
        if (order.getOrderId() > 0) {

            fileController.writeLog("Order " + order.getOrderId() + " created for user " + parts[1]
                    + " in district " + parts[5]);

            out.println("ORDER_CREATED|" + order.getOrderId());

        } else {

            fileController.writeLog("Order creation failed for user " + parts[1]);
            out.println("ERROR|Order creation failed");
        }
    }

    // Get the latest order status from the database
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

    // Save the user rating after the order is delivered
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

            // Save the rating through the OrderController
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

    // Send current drone information to the dashboard
    private void handleGetDrones(PrintWriter out) {

        // Load the latest drone data from the database
        ArrayList<Drone> drones = db.loadDrones();

        StringBuilder response = new StringBuilder("DRONES|");

        for (int i = 0; i < drones.size(); i++) {

            Drone d = drones.get(i);

            // Show "None" if the drone does not have an active order
            String currentOrder = d.getCurrentOrderId() == null
                    ? "None"
                    : String.valueOf(d.getCurrentOrderId());

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

    // Generate report data for the dashboard report screen
    private void handleGetReport(PrintWriter out) {

        Report report = new Report(db);

        out.println(report.generateReportResponse());
    }

    // Save the generated report as a text file
    private void handleSaveReport(PrintWriter out) {

        Report report = new Report(db);

        String reportText = report.generateReportText();

        fileController.saveReportToFile(reportText);

        fileController.writeLog("Report saved to report.txt");

        out.println("REPORT_SAVED");
    }

    // Make sure the request contains the required number of parts
    private boolean hasLength(String[] parts, int expectedLength, PrintWriter out) {

        if (parts.length < expectedLength) {

            out.println("ERROR|Invalid request");

            fileController.writeLog(
                    "Invalid request. Expected "
                            + expectedLength
                            + " parts but received "
                            + parts.length
            );

            return false;
        }

        return true;
    }

    // Convert a string value into an integer safely
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