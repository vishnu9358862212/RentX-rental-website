package com.rentx.rentalsystem.service;

import com.rentx.rentalsystem.dto.RegisterRequestDto;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Lazy;

/**
 * Service layer for User operations
 */
@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Check for admin credentials first
        if (adminEmail.equals(email)) {
            return createAdminUser();
        }

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private User createAdminUser() {
        User admin = new User();
        admin.setId(0L);
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setName("Admin");
        admin.setUserType(User.UserType.ADMIN);
        admin.setIsBanned(false);
        return admin;
    }

    public User registerUser(RegisterRequestDto registerDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registerDto.getEmail())) {
            throw new RuntimeException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setUserType(registerDto.getUserType());
        user.setPhoneNumber(registerDto.getPhoneNumber());
        user.setIsBanned(false);

        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        // Check for admin
        if (adminEmail.equals(email)) {
            return Optional.of(createAdminUser());
        }
        return userRepository.findByEmail(email);
    }

    public User validateUserLogin(String email, String password, User.UserType userType) {
        // Check for admin login
        if (adminEmail.equals(email) && adminPassword.equals(password)) {
            return createAdminUser();
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid credentials");
        }

        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (user.getIsBanned()) {
            throw new RuntimeException("Account is banned");
        }

        return user;
    }

    public List<User> getAllUsersExceptAdmin() {
        return userRepository.findAllExceptUserType(User.UserType.ADMIN);
    }

    public List<User> getUsersByType(User.UserType userType) {
        return userRepository.findByUserType(userType);
    }

    public User banUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getUserType() == User.UserType.ADMIN) {
            throw new RuntimeException("Cannot ban admin user");
        }

        user.setIsBanned(true);
        return userRepository.save(user);
    }

    public User unbanUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (user.getUserType() == User.UserType.ADMIN) {
            throw new RuntimeException("Cannot unban admin user");
        }

        user.setIsBanned(false);
        return userRepository.save(user);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Long getUserCountByType(User.UserType userType) {
        return userRepository.countByUserType(userType);
    }
}