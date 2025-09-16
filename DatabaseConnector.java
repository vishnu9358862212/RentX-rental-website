import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/rental_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
    private static final String URL = System.getenv("DB_URL") != null ? System.getenv("DB_URL") : DEFAULT_URL;
    private static final String USERNAME = System.getenv("DB_USERNAME") != null ? System.getenv("DB_USERNAME") : "vschoudhary";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? System.getenv("DB_PASSWORD") : "jaat@2212";

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null || isConnectionClosed()) {
            try {
                System.out.println("Attempting to connect to: " + URL);
                // Explicitly load the MySQL driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("MySQL JDBC driver not found. Ensure mysql-connector-j-9.3.0.jar is in the classpath: " + e.getMessage(), e);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to connect to database. Check URL, credentials, and MySQL server status: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    private static boolean isConnectionClosed() {
        try {
            return connection == null || connection.isClosed();
        } catch (SQLException e) {
            return true;
        }
    }
}