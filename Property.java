public class Property {
    private int id;
    private String title;
    private String description;
    private double price;
    private String location;
    private String ownerEmail;
    private String status;
    private String tenantEmail;

    public Property(int id, String title, String description, double price, String location, String ownerEmail, String status, String tenantEmail) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.location = location;
        this.ownerEmail = ownerEmail;
        this.status = status;
        this.tenantEmail = tenantEmail;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getLocation() {
        return location;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public String getStatus() {
        return status;
    }

    public String getTenantEmail() {
        return tenantEmail;
    }

    // For TenantDashboard compatibility (assuming address is location)
    public String getAddress() {
        return location;
    }
}