import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class AdminDashboardUI extends JFrame {
    private Connection conn;
    private AdminManager adminManager;

    public AdminDashboardUI(Connection conn) {
        this.conn = conn;
        this.adminManager = new AdminManager(conn);

        setTitle("Admin Dashboard - Rentify");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        topPanel.add(backButton);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Users", createUserPanel());
        tabs.addTab("Manage Properties", createPropertyPanel());

        backButton.addActionListener(e -> {
            dispose();
            new LoginPage(conn);
        });

        add(topPanel, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createUserPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton refreshBtn = new JButton("Refresh User List");
        JButton banBtn = new JButton("Ban User");
        JButton unbanBtn = new JButton("Unban User");
        JTextField userIdField = new JTextField(10);

        refreshBtn.addActionListener(e -> {
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM users WHERE userType != 'admin'");
                
                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                            .append(", Name: ").append(rs.getString("name"))
                            .append(", Email: ").append(rs.getString("email"))
                            .append(", Type: ").append(rs.getString("userType"))
                            .append(", Banned: ").append(rs.getBoolean("isBanned"))
                            .append("\n");
                }
                textArea.setText(sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        banBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(userIdField.getText().trim());
                adminManager.banUser(id);
                JOptionPane.showMessageDialog(this, "User Banned Successfully!");
                userIdField.setText("");
                refreshBtn.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid user ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        unbanBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(userIdField.getText().trim());
                adminManager.unbanUser(id);
                JOptionPane.showMessageDialog(this, "User Unbanned Successfully!");
                userIdField.setText("");
                refreshBtn.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid user ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(new JLabel("User ID:"));
        buttonPanel.add(userIdField);
        buttonPanel.add(banBtn);
        buttonPanel.add(unbanBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPropertyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(20, 60);
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);

        JButton refreshBtn = new JButton("Refresh Property List");
        JButton removeBtn = new JButton("Remove Property");
        JTextField propertyIdField = new JTextField(10);

        refreshBtn.addActionListener(e -> {
            try {
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM properties");

                StringBuilder sb = new StringBuilder();
                while (rs.next()) {
                    sb.append("ID: ").append(rs.getInt("id"))
                            .append(", Title: ").append(rs.getString("title"))
                            .append(", Location: ").append(rs.getString("location"))
                            .append(", Price: ₹").append(rs.getDouble("price"))
                            .append(", Status: ").append(rs.getString("status"))
                            .append("\n");
                }
                textArea.setText(sb.toString());
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        removeBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(propertyIdField.getText().trim());
                adminManager.removeProperty(id);
                JOptionPane.showMessageDialog(this, "Property Removed Successfully!");
                propertyIdField.setText("");
                refreshBtn.doClick();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid property ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshBtn);
        buttonPanel.add(new JLabel("Property ID:"));
        buttonPanel.add(propertyIdField);
        buttonPanel.add(removeBtn);

        panel.add(buttonPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
}