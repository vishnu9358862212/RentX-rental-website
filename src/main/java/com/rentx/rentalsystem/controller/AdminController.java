package com.rentx.rentalsystem.controller;

import com.rentx.rentalsystem.entity.Booking;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.Report;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.service.BookingService;
import com.rentx.rentalsystem.service.PropertyService;
import com.rentx.rentalsystem.service.ReportService;
import com.rentx.rentalsystem.service.UserService;
import com.rentx.rentalsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Admin controller for administrative operations
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Administrative endpoints")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Get all users", description = "Retrieve all users except admin")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsersExceptAdmin();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get users by type", description = "Retrieve users by their type (TENANT or LANDLORD)")
    @GetMapping("/users/type/{userType}")
    public ResponseEntity<List<User>> getUsersByType(@PathVariable User.UserType userType) {
        List<User> users = userService.getUsersByType(userType);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Ban a user", description = "Ban a user account")
    @ApiResponse(responseCode = "200", description = "User banned successfully")
    @ApiResponse(responseCode = "400", description = "Ban operation failed")
    @PutMapping("/users/{userId}/ban")
    public ResponseEntity<?> banUser(@PathVariable Long userId) {
        try {
            User user = userService.banUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User banned successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("isBanned", user.getIsBanned());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Unban a user", description = "Unban a user account")
    @ApiResponse(responseCode = "200", description = "User unbanned successfully")
    @PutMapping("/users/{userId}/unban")
    public ResponseEntity<?> unbanUser(@PathVariable Long userId) {
        try {
            User user = userService.unbanUser(userId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User unbanned successfully");
            response.put("userId", user.getId());
            response.put("email", user.getEmail());
            response.put("isBanned", user.getIsBanned());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get all properties", description = "Retrieve all properties in the system")
    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "Delete a property", description = "Delete any property from the system")
    @ApiResponse(responseCode = "200", description = "Property deleted successfully")
    @DeleteMapping("/properties/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId) {
        try {
            propertyService.deletePropertyByAdmin(propertyId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Property deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get all reports", description = "Retrieve all reports in the system")
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getAllReports() {
        List<Report> reports = reportService.getAllReports();
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get pending reports", description = "Retrieve all pending reports")
    @GetMapping("/reports/pending")
    public ResponseEntity<List<Report>> getPendingReports() {
        List<Report> reports = reportService.getPendingReports();
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Resolve a report", description = "Resolve a report with admin response")
    @ApiResponse(responseCode = "200", description = "Report resolved successfully")
    @PutMapping("/reports/{reportId}/resolve")
    public ResponseEntity<?> resolveReport(@PathVariable Long reportId,
                                         @Parameter(description = "Admin response") @RequestParam String adminResponse,
                                         @Parameter(description = "Resolution status") @RequestParam Report.ReportStatus status,
                                         HttpServletRequest request) {
        try {
            Long adminId = extractUserIdFromToken(request);
            Report report = reportService.resolveReport(reportId, adminId, adminResponse, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report resolved successfully");
            response.put("reportId", report.getId());
            response.put("status", report.getStatus().name());
            response.put("adminResponse", report.getAdminResponse());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get all bookings", description = "Retrieve all bookings in the system")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @Operation(summary = "Get system statistics", description = "Get overall system statistics")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getSystemStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            
            // User statistics
            stats.put("totalTenants", userService.getUserCountByType(User.UserType.TENANT));
            stats.put("totalLandlords", userService.getUserCountByType(User.UserType.LANDLORD));
            stats.put("totalUsers", userService.getAllUsersExceptAdmin().size());
            
            // Property statistics
            stats.put("totalProperties", propertyService.getAllProperties().size());
            stats.put("availableProperties", propertyService.getAvailableProperties().size());
            
            // Booking statistics
            stats.put("totalBookings", bookingService.getAllBookings().size());
            stats.put("pendingBookings", bookingService.getBookingsByStatus(Booking.BookingStatus.PENDING).size());
            stats.put("confirmedBookings", bookingService.getBookingsByStatus(Booking.BookingStatus.CONFIRMED).size());
            
            // Report statistics
            stats.put("totalReports", reportService.getAllReports().size());
            stats.put("pendingReports", reportService.getReportCountByStatus(Report.ReportStatus.PENDING));
            stats.put("resolvedReports", reportService.getReportCountByStatus(Report.ReportStatus.RESOLVED));
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Get reports by type", description = "Retrieve reports filtered by type")
    @GetMapping("/reports/type/{reportType}")
    public ResponseEntity<List<Report>> getReportsByType(@PathVariable Report.ReportType reportType) {
        List<Report> reports = reportService.getReportsByType(reportType);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get reports against user", description = "Get all reports against a specific user")
    @GetMapping("/users/{userId}/reports")
    public ResponseEntity<List<Report>> getReportsAgainstUser(@PathVariable Long userId) {
        List<Report> reports = reportService.getReportsAgainstUser(userId);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get reports against property", description = "Get all reports against a specific property")
    @GetMapping("/properties/{propertyId}/reports")
    public ResponseEntity<List<Report>> getReportsAgainstProperty(@PathVariable Long propertyId) {
        List<Report> reports = reportService.getReportsAgainstProperty(propertyId);
        return ResponseEntity.ok(reports);
    }

    @Operation(summary = "Get user details", description = "Get detailed information about a specific user")
    @GetMapping("/users/{userId}")
    public ResponseEntity<User> getUserDetails(@PathVariable Long userId) {
        try {
            User user = userService.findById(userId);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private Long extractUserIdFromToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.extractUserId(token);
        }
        // For admin, return a fixed ID since admin is not stored in database
        return 0L;
    }
}