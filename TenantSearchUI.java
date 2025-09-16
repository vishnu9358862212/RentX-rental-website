import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class TenantSearchUI extends JFrame {
    private Connection conn;
    private PropertySearchManager searchManager;
    private String tenantEmail;

    public TenantSearchUI(Connection conn, String tenantEmail) {
        this.conn = conn;
        this.tenantEmail = tenantEmail;
        this.searchManager = new PropertySearchManager(conn);

        setTitle("Tenant Property Search - Rentify");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel searchPanel = new JPanel(new FlowLayout());

        JButton backButton = new JButton("Back");
        topPanel.add(backButton);

        JTextField locationField = new JTextField(15);
        JTextField minPriceField = new JTextField(6);
        JTextField maxPriceField = new JTextField(6);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"free", "waiting list", "booked"});

        JButton searchBtn = new JButton("Search");
        JTextArea resultsArea = new JTextArea(20, 60);
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        searchBtn.addActionListener(e -> {
            try {
                String location = locationField.getText().trim();
                double minPrice = minPriceField.getText().isEmpty() ? 0 : Double.parseDouble(minPriceField.getText());
                double maxPrice = maxPriceField.getText().isEmpty() ? Double.MAX_VALUE : Double.parseDouble(maxPriceField.getText());
                
                if (minPrice < 0 || maxPrice < 0 || minPrice > maxPrice) {
                    JOptionPane.showMessageDialog(this, "Invalid price range.");
                    return;
                }

                String status = (String) statusBox.getSelectedItem();
                List<Property> properties = searchManager.searchProperties(location, minPrice, maxPrice, status);
                
                StringBuilder sb = new StringBuilder();
                if (properties.isEmpty()) {
                    sb.append("No properties found matching your criteria.\n");
                } else {
                    for (Property p : properties) {
                        sb.append("ID: ").append(p.getId())
                                .append(", Title: ").append(p.getTitle())
                                .append(", Location: ").append(p.getLocation())
                                .append(", Price: ₹").append(p.getPrice())
                                .append(", Status: ").append(p.getStatus())
                                .append(", Description: ").append(p.getDescription())
                                .append("\n--------------------------------------------------\n");
                    }
                }
                resultsArea.setText(sb.toString());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid price values.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> {
            dispose();
            new LoginPage(conn);
        });

        searchPanel.add(new JLabel("Location:"));
        searchPanel.add(locationField);
        searchPanel.add(new JLabel("Min Price:"));
        searchPanel.add(minPriceField);
        searchPanel.add(new JLabel("Max Price:"));
        searchPanel.add(maxPriceField);
        searchPanel.add(new JLabel("Status:"));
        searchPanel.add(statusBox);
        searchPanel.add(searchBtn);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(searchPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}