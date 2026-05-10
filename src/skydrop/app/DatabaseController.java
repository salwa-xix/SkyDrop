package skydrop.app;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {

    private Connection connection;

    // Connect to the SkyDrop database
    public void connect() {

        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/skydrop",
                    "root",
                    "123321"
            );

            System.out.println("Database connected successfully.");

        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // Insert a new user into the database
    public void insertUser(User user) {

        String sql = """
            INSERT INTO users(name, phone, password, district)
            VALUES (?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getPhone());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getDistrict());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    // Check if a user already exists using the phone number
    public boolean userExists(String phone) {

        String sql = "SELECT 1 FROM users WHERE phone = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, phone);

            ResultSet rs = stmt.executeQuery();

            return rs.next();

        } catch (SQLException e) {
            System.out.println("Error checking user: " + e.getMessage());
            return false;
        }
    }

    // Find a user by phone number
    public User findUserByPhone(String phone) {

        String sql = "SELECT * FROM users WHERE phone = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, phone);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("name"),
                        rs.getString("phone"),
                        rs.getString("password"),
                        rs.getString("district")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    // Insert a new order and save the generated order ID in the Order object
    public void insertOrder(Order order) {

        String sql = """
            INSERT INTO orders
            (user_phone, place_type, place_name, item_name, district, status, rating, assigned_drone_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(
                sql,
                Statement.RETURN_GENERATED_KEYS
        )) {

            stmt.setString(1, order.getUserPhone());
            stmt.setString(2, order.getPlaceType());
            stmt.setString(3, order.getPlaceName());
            stmt.setString(4, order.getItemName());
            stmt.setString(5, order.getDistrict());
            stmt.setString(6, order.getStatus());
            stmt.setInt(7, order.getRating());

            if (order.getAssignedDroneId() == null) {
                stmt.setNull(8, Types.INTEGER);
            } else {
                stmt.setInt(8, order.getAssignedDroneId());
            }

            stmt.executeUpdate();

            // Get the auto-generated order ID from MySQL
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            }

            // Update the queue count after adding a new waiting order
            updateQueueCountForDistrict(order.getDistrict());

        } catch (SQLException e) {
            System.out.println("Error inserting order: " + e.getMessage());
        }
    }

    // Get the next waiting order ID for a specific district
    public synchronized Integer getNextWaitingOrderId(String district) {

        String sql = """
            SELECT order_id
            FROM orders
            WHERE district = ? AND status = 'Waiting'
            ORDER BY order_id ASC
            LIMIT 1
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, district);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("order_id");
            }

        } catch (SQLException e) {
            System.out.println("Error getting next waiting order: " + e.getMessage());
        }

        return null;
    }

    // Assign an order to a drone in the orders table
    public synchronized void assignOrderToDrone(int orderId, int droneId) {

        String sql = """
            UPDATE orders
            SET assigned_drone_id = ?
            WHERE order_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, droneId);
            stmt.setInt(2, orderId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error assigning order to drone: " + e.getMessage());
        }
    }

    // Update the current status of an order
    public synchronized void updateOrderStatus(int orderId, String status) {

        String sql = """
            UPDATE orders
            SET status = ?
            WHERE order_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, orderId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    // Update the current status of a drone
    public synchronized void updateDroneStatus(int droneId, String status) {

        String sql = """
            UPDATE drones
            SET status = ?
            WHERE drone_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, droneId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating drone status: " + e.getMessage());
        }
    }

    // Update the current order assigned to a drone
    public synchronized void updateDroneCurrentOrder(int droneId, Integer orderId) {

        String sql = """
            UPDATE drones
            SET current_order_id = ?
            WHERE drone_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            if (orderId == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, orderId);
            }

            stmt.setInt(2, droneId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating drone current order: " + e.getMessage());
        }
    }

    // Mark the drone as busy and assign the current order to it
    public void markDroneBusy(int droneId, int orderId) {

        updateDroneStatus(droneId, "Busy");
        updateDroneCurrentOrder(droneId, orderId);
    }

    // Mark the drone as idle after completing or rejecting an order
    public void markDroneIdle(int droneId) {

        updateDroneStatus(droneId, "Idle");
        updateDroneCurrentOrder(droneId, null);
    }

    // Increase the delivered count for a specific drone
    public synchronized void incrementDroneDeliveredCount(int droneId) {

        String sql = """
            UPDATE drones
            SET delivered_count = delivered_count + 1
            WHERE drone_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, droneId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error incrementing drone delivered count: " + e.getMessage());
        }
    }

    // Update the queue count for the drone assigned to this district
    public synchronized void updateQueueCountForDistrict(String district) {

        String sql = """
            UPDATE drones
            SET queue_count = (
                SELECT COUNT(*)
                FROM orders
                WHERE district = ? AND status = 'Waiting'
            )
            WHERE district = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, district);
            stmt.setString(2, district);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating queue count: " + e.getMessage());
        }
    }

    // Get the latest order status from the database
    public String getOrderStatus(int orderId) {

        String sql = """
            SELECT status
            FROM orders
            WHERE order_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, orderId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("status");
            }

        } catch (SQLException e) {
            System.out.println("Error getting order status: " + e.getMessage());
        }

        return null;
    }

    // Save a rating for a delivered order
    public void saveRating(int orderId, int rating) {

        String sql = """
            UPDATE orders
            SET rating = ?
            WHERE order_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, rating);
            stmt.setInt(2, orderId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error saving rating: " + e.getMessage());
        }
    }

    // Insert the fixed drones once at system startup
    public void insertInitialDrones() {

        insertDroneIfNotExists(new Drone(1, "Al Rawdah"));
        insertDroneIfNotExists(new Drone(2, "Al Hamra"));
        insertDroneIfNotExists(new Drone(3, "Al Naeem"));
    }

    // Insert a drone only if it does not already exist
    private void insertDroneIfNotExists(Drone drone) {

        String sql = """
            INSERT IGNORE INTO drones
            (drone_id, district, status, current_order_id, delivered_count, queue_count)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, drone.getDroneId());
            stmt.setString(2, drone.getDistrict());
            stmt.setString(3, drone.getStatus());

            if (drone.getCurrentOrderId() == null) {
                stmt.setNull(4, Types.INTEGER);
            } else {
                stmt.setInt(4, drone.getCurrentOrderId());
            }

            stmt.setInt(5, drone.getDeliveredCount());
            stmt.setInt(6, drone.getQueueCount());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting initial drone: " + e.getMessage());
        }
    }

    // Load all drones from the database
    public ArrayList<Drone> loadDrones() {

        ArrayList<Drone> drones = new ArrayList<>();

        String sql = "SELECT * FROM drones";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {

                Drone drone = new Drone(
                        rs.getInt("drone_id"),
                        rs.getString("district")
                );

                drone.setStatus(rs.getString("status"));

                Object currentOrderId = rs.getObject("current_order_id");

                if (currentOrderId == null) {
                    drone.setCurrentOrderId(null);
                } else {
                    drone.setCurrentOrderId((Integer) currentOrderId);
                }

                drone.setDeliveredCount(rs.getInt("delivered_count"));
                drone.setQueueCount(rs.getInt("queue_count"));

                drones.add(drone);
            }

        } catch (SQLException e) {
            System.out.println("Error loading drones: " + e.getMessage());
        }

        return drones;
    }

    // Count the waiting orders for a district
    public int getWaitingQueueCount(String district) {

        String sql = """
            SELECT COUNT(*)
            FROM orders
            WHERE district = ? AND status = 'Waiting'
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, district);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting queue count: " + e.getMessage());
        }

        return 0;
    }

    // Count all orders in the system
    public int getTotalOrders() {

        String sql = "SELECT COUNT(*) FROM orders";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting total orders: " + e.getMessage());
        }

        return 0;
    }

    // Count all orders that were accepted or completed
    public int getAcceptedOrdersCount() {

        String sql = """
            SELECT COUNT(*)
            FROM orders
            WHERE status IN ('Accepted', 'On the air', 'Delivered')
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting accepted orders: " + e.getMessage());
        }

        return 0;
    }

    // Count all rejected orders
    public int getRejectedOrdersCount() {

        String sql = """
            SELECT COUNT(*)
            FROM orders
            WHERE status = 'Rejected'
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error getting rejected orders: " + e.getMessage());
        }

        return 0;
    }

    // Get the delivered count for one drone
    public int getDeliveredCountForDrone(int droneId) {

        String sql = """
            SELECT delivered_count
            FROM drones
            WHERE drone_id = ?
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, droneId);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("delivered_count");
            }

        } catch (SQLException e) {
            System.out.println("Error getting drone delivered count: " + e.getMessage());
        }

        return 0;
    }

}