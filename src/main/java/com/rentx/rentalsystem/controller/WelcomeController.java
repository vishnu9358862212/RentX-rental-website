package com.rentx.rentalsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Welcome controller for basic application info
 */
@RestController
@RequestMapping("/public")
@Tag(name = "Public", description = "Public endpoints that don't require authentication")
public class WelcomeController {

    @Operation(summary = "Welcome message", description = "Get welcome message and API information")
    @GetMapping("/welcome")
    public Map<String, Object> welcome() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to RentX Rental Property Management System!");
        response.put("description", "A comprehensive Spring Boot-based rental property management system");
        response.put("version", "2.0.0");
        response.put("technology", "Spring Boot + REST API");
        response.put("features", new String[]{
            "JWT-based Authentication",
            "Role-based Access Control (Admin, Tenant, Landlord)",
            "Property Management",
            "User Management",
            "Booking System",
            "RESTful API",
            "Swagger Documentation"
        });
        response.put("endpoints", new String[]{
            "/api/auth/login - User login",
            "/api/auth/register - User registration",
            "/api/properties/all - View all properties",
            "/swagger-ui.html - API documentation",
            "/h2-console - Database console (development)"
        });
        return response;
    }

    @Operation(summary = "Health check", description = "Check if the application is running")
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "RentX Application is running successfully");
        return response;
    }
}