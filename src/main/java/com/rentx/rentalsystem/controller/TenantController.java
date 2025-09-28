package com.rentx.rentalsystem.controller;

import com.rentx.rentalsystem.entity.Booking;
import com.rentx.rentalsystem.entity.Report;
import com.rentx.rentalsystem.service.BookingService;
import com.rentx.rentalsystem.service.ReportService;
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
 * Tenant controller for tenant-specific operations
 */
@RestController
@RequestMapping("/api/tenant")
@PreAuthorize("hasRole('TENANT')")
@Tag(name = "Tenant", description = "Tenant-specific endpoints")
public class TenantController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Book a property", description = "Create a booking request for a property")
    @ApiResponse(responseCode = "201", description = "Booking created successfully")
    @ApiResponse(responseCode = "400", description = "Booking failed")
    @PostMapping("/bookings")
    public ResponseEntity<?> bookProperty(@Parameter(description = "Property ID") @RequestParam Long propertyId,
                                        @Parameter(description = "Booking notes") @RequestParam(required = false) String notes,
                                        HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            Booking booking = bookingService.createBooking(propertyId, tenantId, notes);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking request created successfully");
            response.put("bookingId", booking.getId());
            response.put("status", booking.getStatus().name());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get tenant's bookings", description = "Retrieve all bookings made by the tenant")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getMyBookings(HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            List<Booking> bookings = bookingService.getBookingsByTenant(tenantId);
            return ResponseEntity.ok(bookings);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Cancel a booking", description = "Cancel a booking made by the tenant")
    @ApiResponse(responseCode = "200", description = "Booking cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Cancellation failed")
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId,
                                         HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            Booking booking = bookingService.cancelBooking(bookingId, tenantId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking cancelled successfully");
            response.put("bookingId", booking.getId());
            response.put("status", booking.getStatus().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Report a landlord", description = "Report a landlord for suspicious activity")
    @ApiResponse(responseCode = "201", description = "Report submitted successfully")
    @PostMapping("/reports/landlord")
    public ResponseEntity<?> reportLandlord(@Parameter(description = "Landlord ID") @RequestParam Long landlordId,
                                          @Parameter(description = "Report type") @RequestParam Report.ReportType reportType,
                                          @Parameter(description = "Report reason") @RequestParam String reason,
                                          @Parameter(description = "Detailed description") @RequestParam(required = false) String description,
                                          HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            Report report = reportService.reportUser(tenantId, landlordId, reportType, reason, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report submitted successfully");
            response.put("reportId", report.getId());
            response.put("status", report.getStatus().name());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Report a property", description = "Report a property for fraudulent information")
    @ApiResponse(responseCode = "201", description = "Report submitted successfully")
    @PostMapping("/reports/property")
    public ResponseEntity<?> reportProperty(@Parameter(description = "Property ID") @RequestParam Long propertyId,
                                          @Parameter(description = "Report type") @RequestParam Report.ReportType reportType,
                                          @Parameter(description = "Report reason") @RequestParam String reason,
                                          @Parameter(description = "Detailed description") @RequestParam(required = false) String description,
                                          HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            Report report = reportService.reportProperty(tenantId, propertyId, reportType, reason, description);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Report submitted successfully");
            response.put("reportId", report.getId());
            response.put("status", report.getStatus().name());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get tenant's reports", description = "Retrieve all reports submitted by the tenant")
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getMyReports(HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            List<Report> reports = reportService.getReportsByReporter(tenantId);
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Get booking details", description = "Get details of a specific booking")
    @GetMapping("/bookings/{bookingId}")
    public ResponseEntity<Booking> getBookingDetails(@PathVariable Long bookingId,
                                                    HttpServletRequest request) {
        try {
            Long tenantId = extractUserIdFromToken(request);
            Booking booking = bookingService.getBookingById(bookingId);
            
            // Verify that the booking belongs to the current tenant
            if (!booking.getTenant().getId().equals(tenantId)) {
                return ResponseEntity.status(403).build();
            }
            
            return ResponseEntity.ok(booking);
            
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
        throw new RuntimeException("No valid token found");
    }
}