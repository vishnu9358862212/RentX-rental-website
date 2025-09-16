import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.sql.Connection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                Connection conn = DatabaseConnector.getConnection();
                JDialog welcomeDialog = new JDialog((Frame) null, "Welcome to Our Rental Website", true);
                welcomeDialog.setSize(600, 500);
                welcomeDialog.setLocationRelativeTo(null);
                welcomeDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                welcomeDialog.setLayout(new BorderLayout(20, 20));

                // Symbol Panel
                JPanel symbolPanel = new JPanel();
                JLabel symbolLabel = new JLabel(" $ RentX", SwingConstants.CENTER);
                symbolLabel.setFont(new Font("Arial", Font.BOLD, 30));
                symbolLabel.setForeground(new Color(0, 102, 204)); // Blue color
                symbolPanel.add(symbolLabel);
                welcomeDialog.add(symbolPanel, BorderLayout.NORTH);

                // Welcome Message
                JPanel messagePanel = new JPanel();
                JLabel messageLabel = new JLabel("Welcome to RentX! Please choose an option:");
                messageLabel.setFont(new Font("Arial", Font.PLAIN, 16));
                messagePanel.add(messageLabel);
                welcomeDialog.add(messagePanel, BorderLayout.CENTER);

                // Buttons Panel (Vertical)
                JPanel buttonPanel = new JPanel();
                buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
                buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0)); // Padding

                JButton registerButton = new JButton("Register");
                JButton loginButton = new JButton("Login");
                JButton exitButton = new JButton("Exit");

                // Set button sizes
                registerButton.setPreferredSize(new Dimension(150, 40));
                loginButton.setPreferredSize(new Dimension(150, 40));
                exitButton.setPreferredSize(new Dimension(100, 30));

                // Center buttons and add spacing
                registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                buttonPanel.add(registerButton);
                buttonPanel.add(Box.createVerticalStrut(10)); // 10-pixel gap
                buttonPanel.add(loginButton);
                buttonPanel.add(Box.createVerticalStrut(10)); // 10-pixel gap
                buttonPanel.add(exitButton);

                registerButton.addActionListener(e -> {
                    welcomeDialog.dispose();
                    new RegisterForm();
                });

                loginButton.addActionListener(e -> {
                    welcomeDialog.dispose();
                    new LoginPage(conn);
                });

                exitButton.addActionListener(e -> {
                    welcomeDialog.dispose();
                    System.exit(0);
                });

                welcomeDialog.add(buttonPanel, BorderLayout.SOUTH);

                welcomeDialog.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
                System.exit(1);
            }
        });
    }
}