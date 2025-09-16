import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class RegisterForm extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleBox;
    private JButton registerButton;
    private JButton backButton;

    public RegisterForm() {
        setTitle("User Registration - Rentify");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        formPanel.add(emailField);

        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        formPanel.add(new JLabel("Role:"));
        String[] roles = {"tenant", "landlord"};
        roleBox = new JComboBox<>(roles);
        formPanel.add(roleBox);

        registerButton = new JButton("Register");
        formPanel.add(registerButton);
        formPanel.add(new JLabel());

        backButton = new JButton("Back");
        buttonPanel.add(backButton);

        registerButton.addActionListener(e -> registerUser());
        backButton.addActionListener(e -> {
            dispose();
            try {
                Connection conn = DatabaseConnector.getConnection();
                new LoginPage(conn);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Database connection failed: " + ex.getMessage());
            }
        });

        add(buttonPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }
        
        if (password.length() < 8) {
            JOptionPane.showMessageDialog(this, "Password must be at least 8 characters long.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection()) {
            String query = "INSERT INTO users (name, email, password, userType, isBanned) VALUES (?, ?, ?, ?, 0)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, password);
            ps.setString(4, role);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful!");
                dispose();
                new LoginPage(conn);
            }
        } catch (SQLIntegrityConstraintViolationException dupEx) {
            JOptionPane.showMessageDialog(this, "Email already exists. Try another.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterForm());
    }
}