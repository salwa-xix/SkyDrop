package skydrop.app;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseController {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/skydrop";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123321";

    private Connection connection;

    // Open one shared connection to the database
    public void connect() {

        try {

            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            System.out.println("Database connected successfully.");

        } catch (SQLException e) {

            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    // Make sure the database connection is active before running queries
    private void ensureConnected() throws SQLException {

        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database is not connected.");
        }
    }

    // Save a new user account in the database
    public synchronized boolean insertUser(User user) {

        String sql = """
            INSERT INTO users(name, phone, password, district)
            VALUES (?, ?, ?, ?)
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, user.getName());
                stmt.setString(2, user.getPhone());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getDistrict());

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error inserting user: " + e.getMessage());

            return false;
        }
    }

    // Check if a phone number already exists in the users table
    public synchronized boolean userExists(String phone) {

        String sql = "SELECT 1 FROM users WHERE phone = ?";

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, phone);

                try (ResultSet rs = stmt.executeQuery()) {

                    return rs.next();
                }
            }

        } catch (SQLException e) {

            System.out.println("Error checking user: " + e.getMessage());

            return false;
        }
    }

    // Find a user using the phone number
    public synchronized User findUserByPhone(String phone) {

        String sql = "SELECT name, phone, password, district FROM users WHERE phone = ?";

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, phone);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        return new User(
                                rs.getString("name"),
                                rs.getString("phone"),
                                rs.getString("password"),
                                rs.getString("district")
                        );
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error finding user: " + e.getMessage());
        }

        return null;
    }

    // Save a new order and store the generated order ID inside the object
    public synchronized boolean insertOrder(Order order) {

        String sql = """
            INSERT INTO orders
            (user_phone, place_type, place_name, item_name, district, status, rating, assigned_drone_id)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                // Fill the SQL statement using order object data
                fillOrderStatement(stmt, order);

                int affectedRows = stmt.executeUpdate();

                // Get the generated order ID from the database
                try (ResultSet rs = stmt.getGeneratedKeys()) {

                    if (rs.next()) {
                        order.setOrderId(rs.getInt(1));
                    }
                }

                // Refresh waiting queue count for the district
                updateQueueCountForDistrict(order.getDistrict());

                return affectedRows > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error inserting order: " + e.getMessage());

            return false;
        }
    }

    // Save the updated state of an existing order
    public synchronized boolean updateOrder(Order order) {

        String sql = """
            UPDATE orders
            SET user_phone = ?, place_type = ?, place_name = ?, item_name = ?,
                district = ?, status = ?, rating = ?, assigned_drone_id = ?
            WHERE order_id = ?
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                // Fill all order fields before updating
                fillOrderStatement(stmt, order);

                stmt.setInt(9, order.getOrderId());

                boolean updated = stmt.executeUpdate() > 0;

                // Refresh queue count after updating the order
                updateQueueCountForDistrict(order.getDistrict());

                return updated;
            }

        } catch (SQLException e) {

            System.out.println("Error updating order: " + e.getMessage());

            return false;
        }
    }

    // Fill common order fields for insert and update queries
    private void fillOrderStatement(PreparedStatement stmt, Order order) throws SQLException {

        stmt.setString(1, order.getUserPhone());
        stmt.setString(2, order.getPlaceType());
        stmt.setString(3, order.getPlaceName());
        stmt.setString(4, order.getItemName());
        stmt.setString(5, order.getDistrict());
        stmt.setString(6, order.getStatus());
        stmt.setInt(7, order.getRating());

        // Store NULL if the order does not have an assigned drone
        if (order.getAssignedDroneId() == null) {

            stmt.setNull(8, Types.INTEGER);

        } else {

            stmt.setInt(8, order.getAssignedDroneId());
        }
    }

    // Load an order from the database using its ID
    public synchronized Order findOrderById(int orderId) {

        String sql = """
            SELECT order_id, user_phone, place_type, place_name, item_name,
                   district, status, rating, assigned_drone_id
            FROM orders
            WHERE order_id = ?
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setInt(1, orderId);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        // Create the order object using saved database data
                        Order order = new Order(
                                rs.getInt("order_id"),
                                rs.getString("user_phone"),
                                rs.getString("place_type"),
                                rs.getString("place_name"),
                                rs.getString("item_name"),
                                rs.getString("district")
                        );

                        Integer assignedDroneId = getNullableInteger(rs, "assigned_drone_id");

                        // Restore the saved order state
                        order.loadSavedState(
                                rs.getString("status"),
                                rs.getInt("rating"),
                                assignedDroneId
                        );

                        return order;
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error finding order: " + e.getMessage());
        }

        return null;
    }

    // Get the oldest waiting order for a district
    public synchronized Integer getNextWaitingOrderId(String district) {

        String sql = """
            SELECT order_id
            FROM orders
            WHERE district = ? AND status = ?
            ORDER BY order_id ASC
            LIMIT 1
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, district);
                stmt.setString(2, Order.STATUS_WAITING);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        return rs.getInt("order_id");
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error getting next waiting order: " + e.getMessage());
        }

        return null;
    }

    // Get the latest saved order status
    public synchronized String getOrderStatus(int orderId) {

        Order order = findOrderById(orderId);

        return order == null ? null : order.getStatus();
    }

    // Save the updated state of a drone
    public synchronized boolean updateDrone(Drone drone) {

        String sql = """
            UPDATE drones
            SET district = ?, status = ?, current_order_id = ?, delivered_count = ?, queue_count = ?
            WHERE drone_id = ?
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, drone.getDistrict());
                stmt.setString(2, drone.getStatus());

                // Store NULL if the drone is not delivering any order
                if (drone.getCurrentOrderId() == null) {

                    stmt.setNull(3, Types.INTEGER);

                } else {

                    stmt.setInt(3, drone.getCurrentOrderId());
                }

                stmt.setInt(4, drone.getDeliveredCount());
                stmt.setInt(5, drone.getQueueCount());
                stmt.setInt(6, drone.getDroneId());

                return stmt.executeUpdate() > 0;
            }

        } catch (SQLException e) {

            System.out.println("Error updating drone: " + e.getMessage());

            return false;
        }
    }

    // Refresh waiting queue count for all drones in a district
    public synchronized void updateQueueCountForDistrict(String district) {

        String sql = """
            UPDATE drones
            SET queue_count = (
                SELECT COUNT(*)
                FROM orders
                WHERE district = ? AND status = ?
            )
            WHERE district = ?
            """;

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, district);
                stmt.setString(2, Order.STATUS_WAITING);
                stmt.setString(3, district);

                stmt.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println("Error updating queue count: " + e.getMessage());
        }
    }

    // Insert demo drones when the server starts
    public synchronized void insertInitialDrones() {

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

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setInt(1, drone.getDroneId());
                stmt.setString(2, drone.getDistrict());
                stmt.setString(3, drone.getStatus());

                stmt.setNull(4, Types.INTEGER);

                stmt.setInt(5, drone.getDeliveredCount());
                stmt.setInt(6, drone.getQueueCount());

                stmt.executeUpdate();
            }

        } catch (SQLException e) {

            System.out.println("Error inserting initial drone: " + e.getMessage());
        }
    }

    // Load all drones with their latest saved state
    public synchronized ArrayList<Drone> loadDrones() {

        ArrayList<Drone> drones = new ArrayList<>();

        String sql = "SELECT drone_id, district, status, current_order_id, delivered_count, queue_count FROM drones";

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {

                    Drone drone = new Drone(
                            rs.getInt("drone_id"),
                            rs.getString("district")
                    );

                    // Restore the saved drone state
                    drone.loadSavedState(
                            rs.getString("status"),
                            getNullableInteger(rs, "current_order_id"),
                            rs.getInt("delivered_count"),
                            rs.getInt("queue_count")
                    );

                    drones.add(drone);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error loading drones: " + e.getMessage());
        }

        return drones;
    }

    // Count waiting orders for a district
    public synchronized int getWaitingQueueCount(String district) {

        String sql = "SELECT COUNT(*) FROM orders WHERE district = ? AND status = ?";

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, district);
                stmt.setString(2, Order.STATUS_WAITING);

                try (ResultSet rs = stmt.executeQuery()) {

                    if (rs.next()) {

                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {

            System.out.println("Error getting queue count: " + e.getMessage());
        }

        return 0;
    }

    // Get the total number of orders
    public synchronized int getTotalOrders() {

        return getCount("SELECT COUNT(*) FROM orders");
    }

    // Count accepted and delivered orders
    public synchronized int getAcceptedOrdersCount() {

        return getCount("SELECT COUNT(*) FROM orders WHERE status IN ('Accepted', 'On the air', 'Delivered')");
    }

    // Count rejected orders
    public synchronized int getRejectedOrdersCount() {

        return getCount("SELECT COUNT(*) FROM orders WHERE status = 'Rejected'");
    }

    // Execute a COUNT query and return the total result
    private int getCount(String sql) {

        try {

            ensureConnected();

            try (PreparedStatement stmt = connection.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {

                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error running count query: " + e.getMessage());
        }

        return 0;
    }

    // Return null instead of 0 when the database value is actually NULL
    private Integer getNullableInteger(ResultSet rs, String columnName) throws SQLException {

        int value = rs.getInt(columnName);

        return rs.wasNull() ? null : value;
    }
}