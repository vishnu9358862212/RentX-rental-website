import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.sql.Connection;

public class BookingPropertyUI extends JFrame {
    private Connection conn;
    private PropertySearchManager searchManager;
    private PropertyManager propertyManager;
    private String tenantEmail;
    private JTextArea resultsArea;

    public BookingPropertyUI(Connection conn, String tenantEmail) {
        this.conn = conn;
        this.tenantEmail = tenantEmail;
        this.searchManager = new PropertySearchManager(conn);
        this.propertyManager = new PropertyManager(conn);

        setTitle("Book a Property - Rentify");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel bookingPanel = new JPanel(new FlowLayout());

        JButton backButton = new JButton("Back");
        topPanel.add(backButton);

        JTextField propertyIdField = new JTextField(6);
        JButton bookButton = new JButton("Book Property");
        JButton unbookButton = new JButton("Unbook Property");
        JButton refreshButton = new JButton("Refresh");

        resultsArea = new JTextArea(20, 60);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        bookButton.addActionListener(e -> {
            try {
                String idText = propertyIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a property ID.");
                    return;
                }
                int propertyId = Integer.parseInt(idText);
                propertyManager.bookProperty(propertyId, tenantEmail);
                JOptionPane.showMessageDialog(this, "Property booked successfully!");
                propertyIdField.setText("");
                refreshButton.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid property ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        unbookButton.addActionListener(e -> {
            try {
                String idText = propertyIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a property ID.");
                    return;
                }
                int propertyId = Integer.parseInt(idText);
                propertyManager.unbookProperty(propertyId, tenantEmail);
                JOptionPane.showMessageDialog(this, "Property unbooked successfully!");
                propertyIdField.setText("");
                refreshButton.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid property ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        refreshButton.addActionListener(e -> {
            try {
                // Fetch free properties for booking
                List<Property> freeProperties = searchManager.searchProperties("", 0, Double.MAX_VALUE, "free");
                // Fetch properties booked by this tenant
                List<Property> bookedProperties = searchManager.searchPropertiesByTenant(tenantEmail);
                StringBuilder sb = new StringBuilder();

                // Display Available Properties
                sb.append("Available Properties:\n");
                if (freeProperties.isEmpty()) {
                    sb.append("  No free properties available.\n");
                } else {
                    for (Property p : freeProperties) {
                        sb.append("  ID: ").append(p.getId())
                                .append(", Title: ").append(p.getTitle())
                                .append(", Location: ").append(p.getLocation())
                                .append(", Price: ₹").append(p.getPrice())
                                .append(", Description: ").append(p.getDescription())
                                .append("\n  --------------------------------------------------\n");
                    }
                }

                // Display Your Booked Properties
                sb.append("\nYour Booked Properties:\n");
                if (bookedProperties.isEmpty()) {
                    sb.append("  You have no booked properties.\n");
                } else {
                    for (Property p : bookedProperties) {
                        sb.append("  ID: ").append(p.getId())
                                .append(", Title: ").append(p.getTitle())
                                .append(", Location: ").append(p.getLocation())
                                .append(", Price: ₹").append(p.getPrice())
                                .append(", Description: ").append(p.getDescription())
                                .append("\n  --------------------------------------------------\n");
                    }
                }

                resultsArea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading properties: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginPage(conn);
        });

        bookingPanel.add(new JLabel("Property ID:"));
        bookingPanel.add(propertyIdField);
        bookingPanel.add(bookButton);
        bookingPanel.add(unbookButton);
        bookingPanel.add(refreshButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(bookingPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        refreshButton.doClick(); // Load properties on startup
        setVisible(true);
    }
}