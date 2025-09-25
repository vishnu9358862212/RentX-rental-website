package com.rentx.rentalsystem.service;

import com.rentx.rentalsystem.dto.PropertyDto;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.repository.PropertyRepository;
import com.rentx.rentalsystem.util.DistanceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Property operations
 */
@Service
public class PropertyService {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DistanceUtil distanceUtil;

    @Autowired
    private FileStorageService fileStorageService;

    public Property addProperty(PropertyDto propertyDto, Long landlordId) {
        User landlord = userService.findById(landlordId);
        if (landlord.getUserType() != User.UserType.LANDLORD) {
            throw new RuntimeException("Only landlords can add properties");
        }

        Property property = new Property();
        property.setTitle(propertyDto.getTitle());
        property.setDescription(propertyDto.getDescription());
        property.setPropertyType(propertyDto.getPropertyType());
        property.setRent(propertyDto.getRent());
        property.setAddress(propertyDto.getAddress());
        property.setLatitude(propertyDto.getLatitude());
        property.setLongitude(propertyDto.getLongitude());
        property.setNumberOfRooms(propertyDto.getNumberOfRooms());
        property.setNumberOfBathrooms(propertyDto.getNumberOfBathrooms());
        property.setFurnishingStatus(propertyDto.getFurnishingStatus());
        property.setSecurityDeposit(propertyDto.getSecurityDeposit());
        property.setAmenities(propertyDto.getAmenities());
        property.setLandlord(landlord);
        property.setAvailabilityStatus(Property.AvailabilityStatus.AVAILABLE);

        return propertyRepository.save(property);
    }

    public Property updateProperty(Long propertyId, PropertyDto propertyDto, Long landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("You can only update your own properties");
        }

        property.setTitle(propertyDto.getTitle());
        property.setDescription(propertyDto.getDescription());
        property.setPropertyType(propertyDto.getPropertyType());
        property.setRent(propertyDto.getRent());
        property.setAddress(propertyDto.getAddress());
        property.setLatitude(propertyDto.getLatitude());
        property.setLongitude(propertyDto.getLongitude());
        property.setNumberOfRooms(propertyDto.getNumberOfRooms());
        property.setNumberOfBathrooms(propertyDto.getNumberOfBathrooms());
        property.setFurnishingStatus(propertyDto.getFurnishingStatus());
        property.setSecurityDeposit(propertyDto.getSecurityDeposit());
        property.setAmenities(propertyDto.getAmenities());

        return propertyRepository.save(property);
    }

    public void deleteProperty(Long propertyId, Long landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("You can only delete your own properties");
        }

        propertyRepository.delete(property);
    }

    public void deletePropertyByAdmin(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        propertyRepository.delete(property);
    }

    public List<Property> getPropertiesByLandlord(Long landlordId) {
        return propertyRepository.findByLandlordId(landlordId);
    }

    public List<Property> getAllProperties() {
        return propertyRepository.findAll();
    }

    public Property getPropertyById(Long propertyId) {
        return propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }

    public List<Property> searchProperties(Property.PropertyType propertyType,
                                         Property.AvailabilityStatus status,
                                         BigDecimal minRent,
                                         BigDecimal maxRent,
                                         String address) {
        return propertyRepository.findPropertiesWithFilters(propertyType, status, minRent, maxRent, address);
    }

    public List<Property> getPropertiesNearLocation(double latitude, double longitude, 
                                                   double radiusKm, Property.PropertyType propertyType) {
        // Calculate bounding box for efficient database query
        double latDelta = radiusKm / 111.0; // Roughly 111km per degree of latitude
        double lngDelta = radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)));

        double latMin = latitude - latDelta;
        double latMax = latitude + latDelta;
        double lngMin = longitude - lngDelta;
        double lngMax = longitude + lngDelta;

        List<Property> propertiesInBounds = propertyRepository.findPropertiesInBounds(
                latMin, latMax, lngMin, lngMax, Property.AvailabilityStatus.AVAILABLE);

        // Filter by exact distance and property type
        return propertiesInBounds.stream()
                .filter(property -> property.getLatitude() != null && property.getLongitude() != null)
                .filter(property -> distanceUtil.isWithinRadius(latitude, longitude, 
                        property.getLatitude(), property.getLongitude(), radiusKm))
                .filter(property -> propertyType == null || property.getPropertyType() == propertyType)
                .collect(Collectors.toList());
    }

    public String uploadPropertyPhoto(Long propertyId, MultipartFile file, Long landlordId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));

        if (!property.getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("You can only upload photos for your own properties");
        }

        String photoUrl = fileStorageService.storeFile(file, "properties");
        
        // Add photo URL to property
        if (property.getPhotoUrls() == null) {
            property.setPhotoUrls(List.of(photoUrl));
        } else {
            property.getPhotoUrls().add(photoUrl);
        }
        propertyRepository.save(property);

        return photoUrl;
    }

    public List<Property> getAvailableProperties() {
        return propertyRepository.findByAvailabilityStatus(Property.AvailabilityStatus.AVAILABLE);
    }

    public Property updateAvailabilityStatus(Long propertyId, Property.AvailabilityStatus status) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        
        property.setAvailabilityStatus(status);
        return propertyRepository.save(property);
    }
}