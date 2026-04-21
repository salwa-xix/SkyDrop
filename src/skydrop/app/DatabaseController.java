package skydrop.app;

import java.sql.*;

public class DatabaseController {
    private Connection connection;

    // Connect to database
    public void connect() {
        try {
            connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/skydrop", "root", ""
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

    // Find user by phone (used in login)
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
        String sql = "INSERT INTO orders(order_id, user_phone, place_type, place_name, item_name, district, status, rating, assigned_drone_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getOrderId());
            stmt.setString(2, order.getUserPhone());
            stmt.setString(3, order.getPlaceType());
            stmt.setString(4, order.getPlaceName());
            stmt.setString(5, order.getItemName());
            stmt.setString(6, order.getDistrict());
            stmt.setString(7, order.getStatus());
            stmt.setInt(8, order.getRating());

            if (order.getAssignedDroneId() == null) {
                stmt.setNull(9, Types.INTEGER);
            } else {
                stmt.setInt(9, order.getAssignedDroneId());
            }

            stmt.executeUpdate();

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
}