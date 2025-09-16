import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PropertyManager {
    private Connection conn;

    public PropertyManager(Connection conn) {
        if (conn == null) {
            throw new IllegalArgumentException("Database connection cannot be null");
        }
        this.conn = conn;
    }

    public void addProperty(String title, String desc, double price, String location, String ownerEmail) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (desc == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }
        if (ownerEmail == null || !ownerEmail.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid owner email");
        }

        String sql = "INSERT INTO properties (title, description, price, location, ownerEmail, status, tenantEmail) VALUES (?, ?, ?, ?, ?, 'free', NULL)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, desc);
            ps.setDouble(3, price);
            ps.setString(4, location);
            ps.setString(5, ownerEmail);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Failed to add property");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to add property: " + e.getMessage(), e);
        }
    }

    public List<Property> getAllProperties() {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT * FROM properties";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Property property = new Property(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getString("ownerEmail"),
                    rs.getString("status"),
                    rs.getString("tenantEmail")
                );
                properties.add(property);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve properties: " + e.getMessage(), e);
        }

        return properties;
    }

    public List<Property> getPropertiesByOwner(String ownerEmail) {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT * FROM properties WHERE ownerEmail = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, ownerEmail);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Property property = new Property(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getDouble("price"),
                    rs.getString("location"),
                    rs.getString("ownerEmail"),
                    rs.getString("status"),
                    rs.getString("tenantEmail")
                );
                properties.add(property);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve properties by owner: " + e.getMessage(), e);
        }

        return properties;
    }

    public void bookProperty(int propertyId, String tenantEmail) {
        String sql = "UPDATE properties SET status = 'booked', tenantEmail = ? WHERE id = ? AND status = 'free'";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tenantEmail);
            ps.setInt(2, propertyId);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Property not found or already booked");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to book property: " + e.getMessage(), e);
        }
    }

    public void unbookProperty(int propertyId, String tenantEmail) {
        String sql = "UPDATE properties SET status = 'free', tenantEmail = NULL WHERE id = ? AND status = 'booked' AND tenantEmail = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            ps.setString(2, tenantEmail);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Property not found, not booked, or booked by another tenant");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to unbook property: " + e.getMessage(), e);
        }
    }

    public void removeProperty(int propertyId, String ownerEmail) {
        String sql = "DELETE FROM properties WHERE id = ? AND ownerEmail = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, propertyId);
            ps.setString(2, ownerEmail);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new SQLException("Property not found or not owned by this landlord");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove property: " + e.getMessage(), e);
        }
    }
}