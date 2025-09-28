package com.rentx.rentalsystem.repository;

import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for Property entity
 */
@Repository
public interface PropertyRepository extends JpaRepository<Property, Long> {
    
    List<Property> findByLandlord(User landlord);
    
    List<Property> findByLandlordId(Long landlordId);
    
    List<Property> findByPropertyType(Property.PropertyType propertyType);
    
    List<Property> findByAvailabilityStatus(Property.AvailabilityStatus status);
    
    @Query("SELECT p FROM Property p WHERE p.rent BETWEEN :minRent AND :maxRent")
    List<Property> findByRentBetween(@Param("minRent") BigDecimal minRent, @Param("maxRent") BigDecimal maxRent);
    
    @Query("SELECT p FROM Property p WHERE " +
           "(:propertyType IS NULL OR p.propertyType = :propertyType) AND " +
           "(:status IS NULL OR p.availabilityStatus = :status) AND " +
           "(:minRent IS NULL OR p.rent >= :minRent) AND " +
           "(:maxRent IS NULL OR p.rent <= :maxRent) AND " +
           "(:address IS NULL OR LOWER(p.address) LIKE LOWER(CONCAT('%', :address, '%')))")
    List<Property> findPropertiesWithFilters(
            @Param("propertyType") Property.PropertyType propertyType,
            @Param("status") Property.AvailabilityStatus status,
            @Param("minRent") BigDecimal minRent,
            @Param("maxRent") BigDecimal maxRent,
            @Param("address") String address
    );
    
    @Query("SELECT p FROM Property p WHERE " +
           "p.latitude BETWEEN :latMin AND :latMax AND " +
           "p.longitude BETWEEN :lngMin AND :lngMax AND " +
           "p.availabilityStatus = :status")
    List<Property> findPropertiesInBounds(
            @Param("latMin") Double latMin,
            @Param("latMax") Double latMax,
            @Param("lngMin") Double lngMin,
            @Param("lngMax") Double lngMax,
            @Param("status") Property.AvailabilityStatus status
    );
    
    @Query("SELECT COUNT(p) FROM Property p WHERE p.landlord.id = :landlordId")
    Long countByLandlordId(@Param("landlordId") Long landlordId);
}