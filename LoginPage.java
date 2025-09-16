import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;

public class LoginPage extends JFrame {
    private Connection conn;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeBox;

    public LoginPage(Connection conn) {
        this.conn = conn;
        setTitle("Rentify - Login");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 2, 10, 10));

        JLabel userTypeLabel = new JLabel("Login as:");
        userTypeBox = new JComboBox<>(new String[]{"Admin", "Landlord", "Tenant"});

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        add(userTypeLabel);
        add(userTypeBox);
        add(emailLabel);
        add(emailField);
        add(passwordLabel);
        add(passwordField);
        add(registerButton);
        add(loginButton);

        loginButton.addActionListener(e -> login());
        registerButton.addActionListener(e -> {
            dispose();
            new RegisterForm();
        });

        setVisible(true);
    }

    private void login() {
        String userType = ((String) userTypeBox.getSelectedItem()).toLowerCase();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            if (userType.equals("admin")) {
                AdminManager adminManager = new AdminManager(conn);
                if (adminManager.validateAdmin(email, password)) {
                    JOptionPane.showMessageDialog(this, "Admin login successful!");
                    new AdminDashboardUI(conn);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Admin credentials!");
                }
            } else {
                UserManager userManager = new UserManager(conn);
                if (userManager.validateUser(email, password, userType)) {
                    JOptionPane.showMessageDialog(this, userType + " login successful!");
                    if (userType.equals("tenant")) {
                        new BookingPropertyUI(conn, email);
                    } else if (userType.equals("landlord")) {
                        new LandlordDashboardUI(conn, email);
                    }
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid credentials for " + userType);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}