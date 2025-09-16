import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PropertySearchManager {
    private Connection conn;

    public PropertySearchManager(Connection conn) {
        this.conn = conn;
    }

    public List<Property> searchProperties(String location, double minPrice, double maxPrice, String status) {
        List<Property> properties = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM properties WHERE 1=1");
        
        if (location != null && !location.trim().isEmpty()) {
            query.append(" AND location LIKE ?");
        }
        if (minPrice > 0) {
            query.append(" AND price >= ?");
        }
        if (maxPrice < Double.MAX_VALUE) {
            query.append(" AND price <= ?");
        }
        if (status != null && !status.trim().isEmpty()) {
            query.append(" AND status = ?");
        }

        try (PreparedStatement ps = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (location != null && !location.trim().isEmpty()) {
                ps.setString(paramIndex++, "%" + location + "%");
            }
            if (minPrice > 0) {
                ps.setDouble(paramIndex++, minPrice);
            }
            if (maxPrice < Double.MAX_VALUE) {
                ps.setDouble(paramIndex++, maxPrice);
            }
            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex, status);
            }

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
            throw new RuntimeException("Failed to search properties: " + e.getMessage(), e);
        }

        return properties;
    }

    public List<Property> searchPropertiesByTenant(String tenantEmail) {
        List<Property> properties = new ArrayList<>();
        String query = "SELECT * FROM properties WHERE tenantEmail = ? AND status = 'booked'";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, tenantEmail);
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
            throw new RuntimeException("Failed to search tenant properties: " + e.getMessage(), e);
        }

        return properties;
    }
}