package com.rentx.rentalsystem.controller;

import com.rentx.rentalsystem.entity.Booking;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.Report;
import com.rentx.rentalsystem.service.BookingService;
import com.rentx.rentalsystem.service.PropertyService;
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
 * Landlord controller for landlord-specific operations
 */
@RestController
@RequestMapping("/api/landlord")
@PreAuthorize("hasRole('LANDLORD')")
@Tag(name = "Landlord", description = "Landlord-specific endpoints")
public class LandlordController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Get landlord's properties", description = "Retrieve all properties owned by the landlord")
    @GetMapping("/properties")
    public ResponseEntity<List<Property>> getMyProperties(HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            List<Property> properties = propertyService.getPropertiesByLandlord(landlordId);
            return ResponseEntity.ok(properties);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Get bookings for landlord's properties", description = "Retrieve all bookings for landlord's properties")
    @GetMapping("/bookings")
    public ResponseEntity<List<Booking>> getMyBookings(HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            List<Booking> bookings = bookingService.getBookingsByLandlord(landlordId);
            return ResponseEntity.ok(bookings);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Confirm a booking", description = "Confirm a pending booking request")
    @ApiResponse(responseCode = "200", description = "Booking confirmed successfully")
    @ApiResponse(responseCode = "400", description = "Confirmation failed")
    @PutMapping("/bookings/{bookingId}/confirm")
    public ResponseEntity<?> confirmBooking(@PathVariable Long bookingId,
                                          HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            Booking booking = bookingService.confirmBooking(bookingId, landlordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking confirmed successfully");
            response.put("bookingId", booking.getId());
            response.put("status", booking.getStatus().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Cancel a booking", description = "Cancel a booking for landlord's property")
    @ApiResponse(responseCode = "200", description = "Booking cancelled successfully")
    @PutMapping("/bookings/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId,
                                         HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            Booking booking = bookingService.cancelBooking(bookingId, landlordId);
            
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

    @Operation(summary = "Report a tenant", description = "Report a tenant for suspicious activity")
    @ApiResponse(responseCode = "201", description = "Report submitted successfully")
    @PostMapping("/reports/tenant")
    public ResponseEntity<?> reportTenant(@Parameter(description = "Tenant ID") @RequestParam Long tenantId,
                                        @Parameter(description = "Report type") @RequestParam Report.ReportType reportType,
                                        @Parameter(description = "Report reason") @RequestParam String reason,
                                        @Parameter(description = "Detailed description") @RequestParam(required = false) String description,
                                        HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            Report report = reportService.reportUser(landlordId, tenantId, reportType, reason, description);
            
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

    @Operation(summary = "Get landlord's reports", description = "Retrieve all reports submitted by the landlord")
    @GetMapping("/reports")
    public ResponseEntity<List<Report>> getMyReports(HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            List<Report> reports = reportService.getReportsByReporter(landlordId);
            return ResponseEntity.ok(reports);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Update property availability", description = "Update the availability status of a property")
    @ApiResponse(responseCode = "200", description = "Property status updated successfully")
    @PutMapping("/properties/{propertyId}/availability")
    public ResponseEntity<?> updatePropertyAvailability(@PathVariable Long propertyId,
                                                       @Parameter(description = "New availability status") @RequestParam Property.AvailabilityStatus status,
                                                       HttpServletRequest request) {
        try {
            // Verify property ownership
            Long landlordId = extractUserIdFromToken(request);
            Property property = propertyService.getPropertyById(propertyId);
            
            if (!property.getLandlord().getId().equals(landlordId)) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "You can only update your own properties");
                return ResponseEntity.status(403).body(error);
            }
            
            Property updatedProperty = propertyService.updateAvailabilityStatus(propertyId, status);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Property availability updated successfully");
            response.put("propertyId", updatedProperty.getId());
            response.put("newStatus", updatedProperty.getAvailabilityStatus().name());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get property bookings", description = "Get all bookings for a specific property")
    @GetMapping("/properties/{propertyId}/bookings")
    public ResponseEntity<List<Booking>> getPropertyBookings(@PathVariable Long propertyId,
                                                            HttpServletRequest request) {
        try {
            // Verify property ownership
            Long landlordId = extractUserIdFromToken(request);
            Property property = propertyService.getPropertyById(propertyId);
            
            if (!property.getLandlord().getId().equals(landlordId)) {
                return ResponseEntity.status(403).build();
            }
            
            List<Booking> bookings = bookingService.getBookingsByProperty(propertyId);
            return ResponseEntity.ok(bookings);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @Operation(summary = "Get dashboard stats", description = "Get landlord dashboard statistics")
    @GetMapping("/dashboard/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalProperties", propertyService.getPropertiesByLandlord(landlordId).size());
            stats.put("totalBookings", bookingService.getBookingCountByLandlord(landlordId));
            stats.put("pendingBookings", bookingService.getBookingsByLandlord(landlordId).stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count());
            stats.put("confirmedBookings", bookingService.getBookingsByLandlord(landlordId).stream()
                    .filter(b -> b.getStatus() == Booking.BookingStatus.CONFIRMED).count());
            
            return ResponseEntity.ok(stats);
            
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
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