package com.rentx.rentalsystem.controller;

import com.rentx.rentalsystem.dto.PropertyDto;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.service.PropertyService;
import com.rentx.rentalsystem.service.UserService;
import com.rentx.rentalsystem.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Property controller for property management operations
 */
@RestController
@RequestMapping("/api/properties")
@Tag(name = "Properties", description = "Property management endpoints")
public class PropertyController {

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Operation(summary = "Get all properties", description = "Retrieve all available properties")
    @GetMapping("/all")
    public ResponseEntity<List<Property>> getAllProperties() {
        List<Property> properties = propertyService.getAllProperties();
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "Get available properties", description = "Retrieve only available properties")
    @GetMapping("/available")
    public ResponseEntity<List<Property>> getAvailableProperties() {
        List<Property> properties = propertyService.getAvailableProperties();
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "Search properties", description = "Search properties with filters")
    @GetMapping("/search")
    public ResponseEntity<List<Property>> searchProperties(
            @Parameter(description = "Property type") @RequestParam(required = false) Property.PropertyType propertyType,
            @Parameter(description = "Availability status") @RequestParam(required = false) Property.AvailabilityStatus status,
            @Parameter(description = "Minimum rent") @RequestParam(required = false) BigDecimal minRent,
            @Parameter(description = "Maximum rent") @RequestParam(required = false) BigDecimal maxRent,
            @Parameter(description = "Address keyword") @RequestParam(required = false) String address) {

        List<Property> properties = propertyService.searchProperties(propertyType, status, minRent, maxRent, address);
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "Find properties near location", description = "Find properties within radius of given coordinates")
    @GetMapping("/near")
    public ResponseEntity<List<Property>> getPropertiesNearLocation(
            @Parameter(description = "Latitude") @RequestParam double latitude,
            @Parameter(description = "Longitude") @RequestParam double longitude,
            @Parameter(description = "Radius in kilometers", example = "5.0") @RequestParam(defaultValue = "5.0") double radiusKm,
            @Parameter(description = "Property type filter") @RequestParam(required = false) Property.PropertyType propertyType) {

        List<Property> properties = propertyService.getPropertiesNearLocation(latitude, longitude, radiusKm, propertyType);
        return ResponseEntity.ok(properties);
    }

    @Operation(summary = "Get property details", description = "Get detailed information about a specific property")
    @GetMapping("/{propertyId}")
    public ResponseEntity<Property> getPropertyDetails(@PathVariable Long propertyId) {
        try {
            Property property = propertyService.getPropertyById(propertyId);
            return ResponseEntity.ok(property);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Add new property", description = "Add a new property (landlords only)")
    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/add")
    public ResponseEntity<?> addProperty(@Valid @RequestBody PropertyDto propertyDto, 
                                       HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            Property property = propertyService.addProperty(propertyDto, landlordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Property added successfully");
            response.put("propertyId", property.getId());
            
            return ResponseEntity.status(201).body(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Update property", description = "Update property details (landlords only)")
    @PreAuthorize("hasRole('LANDLORD')")
    @PutMapping("/{propertyId}")
    public ResponseEntity<?> updateProperty(@PathVariable Long propertyId,
                                          @Valid @RequestBody PropertyDto propertyDto,
                                          HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            Property property = propertyService.updateProperty(propertyId, propertyDto, landlordId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Property updated successfully");
            response.put("property", property);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Delete property", description = "Delete a property (landlords only)")
    @PreAuthorize("hasRole('LANDLORD')")
    @DeleteMapping("/{propertyId}")
    public ResponseEntity<?> deleteProperty(@PathVariable Long propertyId,
                                          HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            propertyService.deleteProperty(propertyId, landlordId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Property deleted successfully");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Upload property photo", description = "Upload a photo for a property")
    @PreAuthorize("hasRole('LANDLORD')")
    @PostMapping("/{propertyId}/photos")
    public ResponseEntity<?> uploadPropertyPhoto(@PathVariable Long propertyId,
                                               @RequestParam("file") MultipartFile file,
                                               HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            String photoUrl = propertyService.uploadPropertyPhoto(propertyId, file, landlordId);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Photo uploaded successfully");
            response.put("photoUrl", photoUrl);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @Operation(summary = "Get landlord's properties", description = "Get all properties owned by the current landlord")
    @PreAuthorize("hasRole('LANDLORD')")
    @GetMapping("/my-properties")
    public ResponseEntity<List<Property>> getMyProperties(HttpServletRequest request) {
        try {
            Long landlordId = extractUserIdFromToken(request);
            List<Property> properties = propertyService.getPropertiesByLandlord(landlordId);
            return ResponseEntity.ok(properties);
            
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