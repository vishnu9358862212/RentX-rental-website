import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserManager {
    private Connection conn;

    public UserManager(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("Database connection cannot be null");
        }
        this.conn = conn;
    }

    public boolean validateUser(String email, String password, String userType) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (userType == null || !userType.matches("tenant|landlord")) {
            throw new IllegalArgumentException("Invalid user type");
        }

        String sql = "SELECT * FROM users WHERE email=? AND password=? AND userType=? AND isBanned=0";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ps.setString(3, userType);
            System.out.println("Executing query: " + sql);
            System.out.println("Parameters: email=" + email + ", password=" + password + ", userType=" + userType);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasResult = rs.next();
                System.out.println("Query result: " + (hasResult ? "User found" : "No user found"));
                return hasResult;
            }
        } catch (SQLException e) {
            System.err.println("SQLException details: ");
            System.err.println("SQL State: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            throw new RuntimeException("Failed to validate user: " + e.getMessage(), e);
        }
    }
}