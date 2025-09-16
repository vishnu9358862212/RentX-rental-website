import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TenantDashboard extends JFrame {
    private JTextArea propertiesArea;
    private Connection conn;

    public TenantDashboard(Connection conn) {
        this.conn = conn;
        setTitle("Tenant Dashboard - Search Properties");
        setSize(600, 500);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        propertiesArea = new JTextArea(25, 50);
        propertiesArea.setEditable(false);
        propertiesArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(propertiesArea);
        
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);
        topPanel.add(refreshButton);

        refreshButton.addActionListener(e -> loadProperties());
        backButton.addActionListener(e -> {
            dispose();
            new LoginPage(conn);
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);
        loadProperties();
        setVisible(true);
    }

    private void loadProperties() {
        try {
            PropertyManager manager = new PropertyManager(conn);
            List<Property> properties = manager.getAllProperties();

            StringBuilder sb = new StringBuilder();
            if (properties.isEmpty()) {
                sb.append("No properties available.\n");
            } else {
                for (Property p : properties) {
                    sb.append("Title: ").append(p.getTitle()).append("\n")
                      .append("Address: ").append(p.getAddress()).append("\n")
                      .append("Price: ₹").append(p.getPrice()).append("\n")
                      .append("Description: ").append(p.getDescription()).append("\n")
                      .append("Status: ").append(p.getStatus()).append("\n")
                      .append("--------------------------------------------------\n");
                }
            }
            propertiesArea.setText(sb.toString());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading properties: " + e.getMessage());
        }
    }
}