package skydrop.app;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler extends Thread {

    private OrderController orderController;
    private Socket socket;
    private DatabaseController db;

    public ClientHandler(Socket socket,
                         DatabaseController db,
                         OrderController orderController) {

        this.socket = socket;
        this.db = db;
        this.orderController = orderController;
    }

    @Override
    public void run() {

        try (
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream())
                );

                PrintWriter out = new PrintWriter(
                        socket.getOutputStream(),
                        true
                )
        ) {

            String request = in.readLine();

            if (request == null) {
                out.println("ERROR");
                return;
            }

            String[] parts = request.split("\\|");

            // LOGIN
            if (parts[0].equals("LOGIN")) {

                String phone = parts[1];
                String password = parts[2];

                User user = db.findUserByPhone(phone);

                if (user != null && user.getPassword().equals(password)) {

                    out.println("SUCCESS|"
                            + user.getName() + "|"
                            + user.getPhone() + "|"
                            + user.getDistrict());

                } else {
                    out.println("FAIL");
                }

            }

            // CREATE ORDER
            else if (parts[0].equals("CREATE_ORDER")) {

                String userPhone = parts[1];
                String placeType = parts[2];
                String placeName = parts[3];
                String itemName = parts[4];
                String district = parts[5];

                Order order = new Order(
                        0,
                        userPhone,
                        placeType,
                        placeName,
                        itemName,
                        district
                );

                db.insertOrder(order);

                orderController.getAllOrders().add(order);

                WeatherController weatherController =
                        new WeatherController();

                FileController fileController =
                        new FileController();

                new OrderProcessThread(
                        order,
                        weatherController,
                        db,
                        fileController
                ).start();

                out.println("ORDER_CREATED|" + order.getOrderId());
            }

            // GET STATUS
            else if (parts[0].equals("GET_STATUS")) {

                int orderId = Integer.parseInt(parts[1]);

                String status = db.getOrderStatus(orderId);

                if (status != null) {
                    out.println("STATUS|" + status);
                } else {
                    out.println("STATUS|UNKNOWN");
                }
            }

            // SAVE RATING
            else if (parts[0].equals("SAVE_RATING")) {

                int orderId = Integer.parseInt(parts[1]);
                int rating = Integer.parseInt(parts[2]);

                db.saveRating(orderId, rating);

                out.println("RATING_SAVED");
            }

            // GET DRONES
            else if (parts[0].equals("GET_DRONES")) {

                ArrayList<Drone> drones = db.loadDrones();

                StringBuilder response =
                        new StringBuilder("DRONES|");

                for (int i = 0; i < drones.size(); i++) {

                    Drone d = drones.get(i);

                    String currentOrder =
                            d.getCurrentOrderId() == null
                                    ? "None"
                                    : String.valueOf(
                                    d.getCurrentOrderId()
                            );

                    int queue =
                            db.getWaitingQueueCount(
                                    d.getDistrict()
                            );

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

            // GET REPORT
            else if (parts[0].equals("GET_REPORT")) {

                int total =
                        db.getTotalOrders();

                int accepted =
                        db.getAcceptedOrdersCount();

                int rejected =
                        db.getRejectedOrdersCount();

                int dr1 =
                        db.getDeliveredCountForDrone(1);

                int dr2 =
                        db.getDeliveredCountForDrone(2);

                int dr3 =
                        db.getDeliveredCountForDrone(3);

                out.println(
                        "REPORT|"
                                + total + "|"
                                + accepted + "|"
                                + rejected + "|"
                                + dr1 + "|"
                                + dr2 + "|"
                                + dr3
                );
            }

            // UNKNOWN
            else {
                out.println("UNKNOWN_COMMAND");
            }

        } catch (IOException e) {

            System.out.println(
                    "Client error: "
                            + e.getMessage()
            );
        }
    }
}