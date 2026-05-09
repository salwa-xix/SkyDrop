package skydrop.app;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {
    private Connection connection;

    // Connect to database
    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/skydrop", "root", "2244!mawadah"
            );
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // Insert new user into database
    public void insertUser(User user) {
        String sql = "INSERT INTO users(name, phone, password, district) VALUES (?, ?, ?, ?)";

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

    // Check if user already exists by phone
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

    // Find user by phone
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

    // Insert new order into database
    public void insertOrder(Order order) {

        String sql = "INSERT INTO orders(user_phone, place_type, place_name, item_name, district, status, rating, assigned_drone_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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

            // Get generated order id from MySQL
            ResultSet rs = stmt.getGeneratedKeys();

            if (rs.next()) {
                order.setOrderId(rs.getInt(1));
            }

        } catch (SQLException e) {
            System.out.println("Error inserting order: " + e.getMessage());
        }
    }

    // Update order status
    public void updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    // Update assigned drone for an order
    public void updateAssignedDrone(int orderId, Integer droneId) {
        String sql = "UPDATE orders SET assigned_drone_id = ? WHERE order_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (droneId == null) {
                stmt.setNull(1, Types.INTEGER);
            } else {
                stmt.setInt(1, droneId);
            }

            stmt.setInt(2, orderId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating assigned drone: " + e.getMessage());
        }
    }

    // Save rating for an order
    public void saveRating(int orderId, int rating) {
        String sql = "UPDATE orders SET rating = ? WHERE order_id = ?";

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

    public String getOrderStatus(int orderId) {
        String sql = "SELECT status FROM orders WHERE order_id = ?";

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

    // Insert drone only if it does not already exist
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

    // Update drone runtime data
    public void updateDrone(Drone drone) {
        String sql = "UPDATE drones SET status = ?, current_order_id = ?, delivered_count = ?, queue_count = ? WHERE drone_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, drone.getStatus());

            if (drone.getCurrentOrderId() == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, drone.getCurrentOrderId());
            }

            stmt.setInt(3, drone.getDeliveredCount());
            stmt.setInt(4, drone.getQueueCount());
            stmt.setInt(5, drone.getDroneId());

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error updating drone: " + e.getMessage());
        }
    }
    public synchronized Integer tryClaimNextWaitingOrder(int droneId, String district) {
        try {
            connection.setAutoCommit(false);

            String findSql =
                    "SELECT order_id FROM orders " +
                            "WHERE district = ? AND status = 'Waiting' " +
                            "ORDER BY order_id ASC LIMIT 1 FOR UPDATE";

            try (PreparedStatement findStmt = connection.prepareStatement(findSql)) {
                findStmt.setString(1, district);
                ResultSet rs = findStmt.executeQuery();

                if (!rs.next()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                    return null;
                }

                int orderId = rs.getInt("order_id");

                String claimOrderSql =
                        "UPDATE orders SET status = 'Accepted', assigned_drone_id = ? WHERE order_id = ?";

                try (PreparedStatement stmt = connection.prepareStatement(claimOrderSql)) {
                    stmt.setInt(1, droneId);
                    stmt.setInt(2, orderId);
                    stmt.executeUpdate();
                }

                String claimDroneSql =
                        "UPDATE drones SET status = 'Busy', current_order_id = ? WHERE drone_id = ?";

                try (PreparedStatement stmt = connection.prepareStatement(claimDroneSql)) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, droneId);
                    stmt.executeUpdate();
                }

                connection.commit();
                connection.setAutoCommit(true);
                return orderId;
            }

        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ignored) {}

            System.out.println("Error claiming waiting order: " + e.getMessage());
            return null;
        }
    }
    public int getWaitingQueueCount(String district) {
        String sql = "SELECT COUNT(*) FROM orders WHERE district = ? AND status = 'Waiting'";

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
    public synchronized void releaseDrone(int droneId) {
        String sql =
                "UPDATE drones " +
                        "SET status = 'Idle', current_order_id = NULL, " +
                        "delivered_count = delivered_count + 1 " +
                        "WHERE drone_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, droneId);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error releasing drone: " + e.getMessage());
        }
    }
    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) FROM orders";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error getting total orders: " + e.getMessage());
        }

        return 0;
    }

    public int getAcceptedOrdersCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status != 'Rejected'";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error getting accepted orders: " + e.getMessage());
        }

        return 0;
    }

    public int getRejectedOrdersCount() {
        String sql = "SELECT COUNT(*) FROM orders WHERE status = 'Rejected'";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) return rs.getInt(1);

        } catch (SQLException e) {
            System.out.println("Error getting rejected orders: " + e.getMessage());
        }

        return 0;
    }

    public int getDeliveredCountForDrone(int droneId) {
        String sql = "SELECT delivered_count FROM drones WHERE drone_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, droneId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt("delivered_count");

        } catch (SQLException e) {
            System.out.println("Error getting drone delivered count: " + e.getMessage());
        }

        return 0;
    }
    public void insertDemoUsers() {

        String sql = """
        INSERT IGNORE INTO users(name, phone, password, district)
        VALUES
        ('Sara',   '11', '1', 'Al Rawdah'),
        ('Lama',   '22', '2', 'Al Rawdah'),
        ('Nora',   '33', '3', 'Al Hamra'),
        ('Raneem', '44', '4', 'Al Hamra'),
        ('Haya',   '55', '5', 'Al Naeem')
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error inserting demo users: " + e.getMessage());
        }
    }
}