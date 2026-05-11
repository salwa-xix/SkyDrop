package skydrop.app;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDataBase {

    private static final String SERVER_URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/skydrop";
    private static final String USER = "root";
    private static final String PASSWORD = "123321";

    public static void createTables() {

        try (Connection connection = DriverManager.getConnection(SERVER_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS skydrop");
            System.out.println("Database created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating database: " + e.getMessage());
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
             Statement statement = connection.createStatement()) {

            String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    phone VARCHAR(20) NOT NULL UNIQUE,
                    password VARCHAR(100) NOT NULL,
                    district VARCHAR(100) NOT NULL
                )
                """;

            String createOrdersTable = """
                CREATE TABLE IF NOT EXISTS orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    user_phone VARCHAR(20) NOT NULL,
                    place_type VARCHAR(50) NOT NULL,
                    place_name VARCHAR(100) NOT NULL,
                    item_name VARCHAR(100) NOT NULL,
                    district VARCHAR(100) NOT NULL,
                    status VARCHAR(50) NOT NULL,
                    rating INT DEFAULT 0,
                    assigned_drone_id INT NULL,
                    FOREIGN KEY (user_phone) REFERENCES users(phone)
                )
                """;

            String createDronesTable = """
                CREATE TABLE IF NOT EXISTS drones (
                    drone_id INT PRIMARY KEY,
                    district VARCHAR(100) NOT NULL,
                    status VARCHAR(50) NOT NULL DEFAULT 'Idle',
                    current_order_id INT NULL,
                    delivered_count INT DEFAULT 0,
                    queue_count INT DEFAULT 0
                )
                """;

            statement.executeUpdate(createUsersTable);
            statement.executeUpdate(createOrdersTable);
            statement.executeUpdate(createDronesTable);

            System.out.println("Tables created successfully.");

        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }
}