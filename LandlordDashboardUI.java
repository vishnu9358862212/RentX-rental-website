import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class LandlordDashboardUI extends JFrame {
    private Connection conn;
    private PropertyManager propertyManager;
    private String landlordEmail;
    private JTextArea propertiesArea;

    public LandlordDashboardUI(Connection conn, String landlordEmail) {
        this.conn = conn;
        this.landlordEmail = landlordEmail;
        this.propertyManager = new PropertyManager(conn);

        setTitle("Landlord Dashboard - Rentify");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel addPropertyPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JPanel managePropertyPanel = new JPanel(new FlowLayout());

        // Back Button
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);

        // Add Property Form
        JTextField titleField = new JTextField(20);
        JTextField descField = new JTextField(20);
        JTextField priceField = new JTextField(20);
        JTextField locationField = new JTextField(20);
        JButton addButton = new JButton("Add Property");

        addPropertyPanel.add(new JLabel("Title:"));
        addPropertyPanel.add(titleField);
        addPropertyPanel.add(new JLabel("Description:"));
        addPropertyPanel.add(descField);
        addPropertyPanel.add(new JLabel("Price:"));
        addPropertyPanel.add(priceField);
        addPropertyPanel.add(new JLabel("Location:"));
        addPropertyPanel.add(locationField);
        addPropertyPanel.add(new JLabel(""));
        addPropertyPanel.add(addButton);

        // Manage Properties
        JTextField propertyIdField = new JTextField(6);
        JButton removeButton = new JButton("Remove Property");
        JButton refreshButton = new JButton("Refresh");

        propertiesArea = new JTextArea(15, 60);
        propertiesArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(propertiesArea);

        addButton.addActionListener(e -> {
            try {
                String title = titleField.getText().trim();
                String desc = descField.getText().trim();
                String priceText = priceField.getText().trim();
                String location = locationField.getText().trim();

                if (title.isEmpty() || desc.isEmpty() || priceText.isEmpty() || location.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields.");
                    return;
                }

                double price = Double.parseDouble(priceText);
                propertyManager.addProperty(title, desc, price, location, landlordEmail);
                JOptionPane.showMessageDialog(this, "Property added successfully!");
                titleField.setText("");
                descField.setText("");
                priceField.setText("");
                locationField.setText("");
                refreshButton.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid price.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        removeButton.addActionListener(e -> {
            try {
                String idText = propertyIdField.getText().trim();
                if (idText.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please enter a property ID.");
                    return;
                }
                int propertyId = Integer.parseInt(idText);
                propertyManager.removeProperty(propertyId, landlordEmail);
                JOptionPane.showMessageDialog(this, "Property removed successfully!");
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
                List<Property> properties = propertyManager.getPropertiesByOwner(landlordEmail);
                StringBuilder sb = new StringBuilder();
                if (properties.isEmpty()) {
                    sb.append("No properties added by you.\n");
                } else {
                    for (Property p : properties) {
                        sb.append("ID: ").append(p.getId())
                                .append(", Title: ").append(p.getTitle())
                                .append(", Location: ").append(p.getLocation())
                                .append(", Price: ₹").append(p.getPrice())
                                .append(", Description: ").append(p.getDescription())
                                .append(", Status: ").append(p.getStatus())
                                .append(", Tenant: ").append(p.getTenantEmail() != null ? p.getTenantEmail() : "None")
                                .append("\n--------------------------------------------------\n");
                    }
                }
                propertiesArea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading properties: " + ex.getMessage());
                ex.printStackTrace(); // Log for debugging
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginPage(conn);
        });

        managePropertyPanel.add(new JLabel("Property ID:"));
        managePropertyPanel.add(propertyIdField);
        managePropertyPanel.add(removeButton);
        managePropertyPanel.add(refreshButton);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(addPropertyPanel, BorderLayout.CENTER);
        mainPanel.add(managePropertyPanel, BorderLayout.SOUTH);
        mainPanel.add(scrollPane, BorderLayout.EAST);

        add(mainPanel);
        refreshButton.doClick(); // Load properties on startup
        setVisible(true);
    }
}