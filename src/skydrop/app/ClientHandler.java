package skydrop.app;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private Socket socket;
    private DatabaseController db;
    private OrderController orderController;
    private FileController fileController;

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

        try (
                // Read the request sent from the GUI client
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                // Send the response back to the GUI client
                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {

            // Read one request from the client
            String request = in.readLine();

            // Stop if the request is empty
            if (request == null || request.trim().isEmpty()) {
                out.println("ERROR|Empty request");
                fileController.writeLog("Empty request received from client.");
                return;
            }

            // Split the request into parts using |
            String[] parts = request.split("\\|");

            // Get the command name from the first part
            String command = parts[0];

            // Handle login request
            if (command.equals("LOGIN")) {

                String phone = parts[1];
                String password = parts[2];

                User user = db.findUserByPhone(phone);

                if (user != null && user.getPassword().equals(password)) {

                    fileController.writeLog(
                            "Login success for user phone: " + phone
                    );

                    out.println("SUCCESS|"
                            + user.getName() + "|"
                            + user.getPhone() + "|"
                            + user.getDistrict());

                } else {

                    fileController.writeLog(
                            "Login failed for phone: " + phone
                    );

                    out.println("FAIL");
                }
            }

            // Handle sign up request
            else if (command.equals("SIGNUP")) {

                String name = parts[1];
                String phone = parts[2];
                String password = parts[3];
                String district = parts[4];

                if (db.userExists(phone)) {

                    fileController.writeLog(
                            "Sign up failed. User already exists: " + phone
                    );

                    out.println("USER_EXISTS");

                } else {

                    User user = new User(
                            name,
                            phone,
                            password,
                            district
                    );

                    db.insertUser(user);

                    fileController.writeLog(
                            "New user registered: " + phone
                    );

                    out.println("SIGNUP_SUCCESS");
                }
            }

            // Handle create order request
            else if (command.equals("CREATE_ORDER")) {

                String userPhone = parts[1];
                String placeType = parts[2];
                String placeName = parts[3];
                String itemName = parts[4];
                String district = parts[5];

                // Ask the OrderController to create and save the order
                Order order = orderController.createOrder(
                        userPhone,
                        placeType,
                        placeName,
                        itemName,
                        district
                );

                fileController.writeLog(
                        "Order " + order.getOrderId()
                                + " created for user "
                                + userPhone
                                + " in district "
                                + district
                );

                // Send the created order ID back to the GUI
                out.println("ORDER_CREATED|" + order.getOrderId());
            }

            // Handle order status request
            else if (command.equals("GET_STATUS")) {

                int orderId = Integer.parseInt(parts[1]);

                // Get the latest status from the database
                String status = db.getOrderStatus(orderId);

                if (status != null) {
                    out.println("STATUS|" + status);
                } else {
                    out.println("STATUS|UNKNOWN");
                }
            }

            // Handle rating save request
            else if (command.equals("SAVE_RATING")) {

                int orderId = Integer.parseInt(parts[1]);
                int rating = Integer.parseInt(parts[2]);

                // Save the rating in the database
                db.saveRating(orderId, rating);

                fileController.writeLog(
                        "Rating " + rating + " saved for order " + orderId
                );

                out.println("RATING_SAVED");
            }

            // Handle dashboard drones request
            else if (command.equals("GET_DRONES")) {

                // Load the latest drones data from the database
                ArrayList<Drone> drones = db.loadDrones();

                StringBuilder response = new StringBuilder("DRONES|");

                for (int i = 0; i < drones.size(); i++) {

                    Drone d = drones.get(i);

                    // Show None if the drone has no current order
                    String currentOrder =
                            d.getCurrentOrderId() == null
                                    ? "None"
                                    : String.valueOf(d.getCurrentOrderId());

                    // Use the stored queue count for this drone
                    int queue = d.getQueueCount();

                    response.append(d.getDroneId())
                            .append(",")
                            .append(d.getDistrict())
                            .append(",")
                            .append(d.getStatus())
                            .append(",")
                            .append(currentOrder)
                            .append(",")
                            .append(queue);

                    if (i < drones.size() - 1) {
                        response.append(";");
                    }
                }

                out.println(response.toString());
            }

            // Handle report request
            else if (command.equals("GET_REPORT")) {

                // Create report object and generate structured response for the screen
                Report report = new Report(db);

                String response = report.generateReportResponse();

                out.println(response);
            }

            // Handle save report request
            else if (command.equals("SAVE_REPORT")) {

                // Create report object and generate readable text
                Report report = new Report(db);

                String reportText = report.generateReportText();

                // Save the text using FileController
                fileController.saveReportToFile(reportText);

                fileController.writeLog("Report saved to report.txt");

                out.println("REPORT_SAVED");
            }

            // Handle unknown commands
            else {

                fileController.writeLog(
                        "Unknown command received: " + command
                );

                out.println("UNKNOWN_COMMAND");
            }

        } catch (IOException e) {

            fileController.writeLog(
                    "Client error: " + e.getMessage()
            );

            System.out.println(
                    "Client error: " + e.getMessage()
            );

        } catch (Exception e) {

            fileController.writeLog(
                    "Request handling error: " + e.getMessage()
            );

            System.out.println(
                    "Request handling error: " + e.getMessage()
            );
        }
    }
}
