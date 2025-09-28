package com.rentx.rentalsystem.controller;

import com.rentx.rentalsystem.dto.LoginRequestDto;
import com.rentx.rentalsystem.dto.LoginResponseDto;
import com.rentx.rentalsystem.dto.RegisterRequestDto;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.service.UserService;
import com.rentx.rentalsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication controller for login and registration
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication endpoints for login and registration")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            // Special handling for admin login
            if (loginRequest.getUserType() != null && 
                (loginRequest.getUserType() == User.UserType.ADMIN || 
                 userService.findByEmail(loginRequest.getEmail()).isPresent() && 
                 userService.findByEmail(loginRequest.getEmail()).get().getUserType() == User.UserType.ADMIN)) {
                
                User user = userService.validateUserLogin(loginRequest.getEmail(), 
                        loginRequest.getPassword(), User.UserType.ADMIN);
                
                String token = jwtUtil.generateToken(user.getEmail(), 
                        user.getUserType().name(), user.getId());
                
                return ResponseEntity.ok(new LoginResponseDto(token, user.getEmail(), 
                        user.getName(), user.getUserType().name(), user.getId()));
            }

            // Validate user credentials
            User user = userService.validateUserLogin(loginRequest.getEmail(), 
                    loginRequest.getPassword(), loginRequest.getUserType());

            // Authenticate with Spring Security (for consistency)
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(user.getEmail(), 
                    user.getUserType().name(), user.getId());

            return ResponseEntity.ok(new LoginResponseDto(token, user.getEmail(), 
                    user.getName(), user.getUserType().name(), user.getId()));

        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            return ResponseEntity.status(401).body(error);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "User registration", description = "Register a new tenant or landlord")
    @ApiResponse(responseCode = "201", description = "Registration successful")
    @ApiResponse(responseCode = "400", description = "Registration failed")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto registerRequest) {
        try {
            // Only allow tenant and landlord registration
            if (registerRequest.getUserType() == User.UserType.ADMIN) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Admin registration is not allowed");
                return ResponseEntity.status(400).body(error);
            }

            User user = userService.registerUser(registerRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("userType", user.getUserType().name());
            
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get user profile", description = "Get current user's profile information")
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        try {
            String email = authentication.getName();
            User user = userService.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Map<String, Object> profile = new HashMap<>();
            profile.put("id", user.getId());
            profile.put("name", user.getName());
            profile.put("email", user.getEmail());
            profile.put("userType", user.getUserType().name());
            profile.put("phoneNumber", user.getPhoneNumber());
            profile.put("createdAt", user.getCreatedAt());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }
}