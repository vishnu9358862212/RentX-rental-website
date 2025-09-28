package com.rentx.rentalsystem.dto;

/**
 * DTO for login response containing JWT token and user info
 */
public class LoginResponseDto {
    
    private String token;
    private String email;
    private String name;
    private String userType;
    private Long userId;

    // Constructors
    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String email, String name, String userType, Long userId) {
        this.token = token;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.userId = userId;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}