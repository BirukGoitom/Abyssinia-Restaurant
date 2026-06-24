

import java.sql.*;


public class DatabaseManager {

    private static final String DB_URL      = "jdbc:mysql://localhost:3306/abyssinia_market"
                                            + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String DB_USER     = "root";
    private static final String DB_PASSWORD = "";

    private Connection connection;

    public DatabaseManager() {
        try {
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("Connected to MySQL database: abyssinia_market");
        } catch (SQLException e) {
            System.out.println("Failed to connect to MySQL: " + e.getMessage());
            System.out.println("Check that MySQL is running and DB_PASSWORD in DatabaseManager.java is correct.");
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
