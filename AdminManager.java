import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminManager {
    private Connection conn;
    
    // Hardcoded admin credentials (replace with your actual details)
    private static final String ADMIN_EMAIL = "vishnu@rentify.com";
    private static final String ADMIN_PASSWORD = "jaat@2212";

    public AdminManager(Connection conn) {
        this.conn = conn;
    }

    public boolean validateAdmin(String email, String password) {
        // Only allow the hardcoded admin credentials
        return ADMIN_EMAIL.equals(email) && ADMIN_PASSWORD.equals(password);
    }

    public void banUser(int userId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET isBanned=1 WHERE id=? AND userType != 'admin'"
            );
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("User not found or cannot ban admin");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to ban user: " + e.getMessage());
        }
    }

    public void unbanUser(int userId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE users SET isBanned=0 WHERE id=? AND userType != 'admin'"
            );
            ps.setInt(1, userId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("User not found or cannot unban admin");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to unban user: " + e.getMessage());
        }
    }

    public void removeProperty(int propertyId) {
        try {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM properties WHERE id=?"
            );
            ps.setInt(1, propertyId);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new Exception("Property not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to remove property: " + e.getMessage());
        }
    }
}