import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginManager {
    private Connection connection;

    public LoginManager() {
        connection = DatabaseConnector.getConnection();
        if (connection == null) {
            throw new IllegalStateException("Failed to initialize database connection");
        }
    }

    public User login(String email, String password) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND isBanned = 0";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setUserType(rs.getString("userType"));
                    user.setBanned(rs.getBoolean("isBanned"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Login failed: " + e.getMessage(), e);
        }
        return null;
    }

    public boolean register(User user) {
        if (user == null || user.getUserType().equals("admin")) {
            throw new IllegalArgumentException("Cannot register admin users");
        }

        String sql = "INSERT INTO users (name, email, password, userType, isBanned) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, user.getUserType());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Registration failed: " + e.getMessage(), e);
        }
    }
}