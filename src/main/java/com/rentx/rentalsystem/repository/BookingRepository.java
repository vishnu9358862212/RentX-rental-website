package com.rentx.rentalsystem.repository;

import com.rentx.rentalsystem.entity.Booking;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Booking entity
 */
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    List<Booking> findByTenant(User tenant);
    
    List<Booking> findByTenantId(Long tenantId);
    
    List<Booking> findByProperty(Property property);
    
    List<Booking> findByPropertyId(Long propertyId);
    
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.property.landlord.id = :landlordId")
    List<Booking> findByLandlordId(@Param("landlordId") Long landlordId);
    
    @Query("SELECT b FROM Booking b WHERE b.tenant.id = :tenantId AND b.status = :status")
    List<Booking> findByTenantIdAndStatus(@Param("tenantId") Long tenantId, @Param("status") Booking.BookingStatus status);
    
    @Query("SELECT b FROM Booking b WHERE b.property.id = :propertyId AND b.status = :status")
    List<Booking> findByPropertyIdAndStatus(@Param("propertyId") Long propertyId, @Param("status") Booking.BookingStatus status);
    
    Optional<Booking> findByPropertyAndTenantAndStatus(Property property, User tenant, Booking.BookingStatus status);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.tenant.id = :tenantId")
    Long countByTenantId(@Param("tenantId") Long tenantId);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.property.landlord.id = :landlordId")
    Long countByLandlordId(@Param("landlordId") Long landlordId);
}