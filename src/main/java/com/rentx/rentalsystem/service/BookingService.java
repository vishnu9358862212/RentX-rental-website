package com.rentx.rentalsystem.service;

import com.rentx.rentalsystem.entity.Booking;
import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Booking operations
 */
@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private UserService userService;

    public Booking createBooking(Long propertyId, Long tenantId, String notes) {
        Property property = propertyService.getPropertyById(propertyId);
        User tenant = userService.findById(tenantId);

        if (tenant.getUserType() != User.UserType.TENANT) {
            throw new RuntimeException("Only tenants can book properties");
        }

        if (property.getAvailabilityStatus() != Property.AvailabilityStatus.AVAILABLE) {
            throw new RuntimeException("Property is not available for booking");
        }

        // Check if tenant already has a pending or confirmed booking for this property
        Optional<Booking> existingBooking = bookingRepository.findByPropertyAndTenantAndStatus(
                property, tenant, Booking.BookingStatus.PENDING);
        if (existingBooking.isPresent()) {
            throw new RuntimeException("You already have a pending booking for this property");
        }

        existingBooking = bookingRepository.findByPropertyAndTenantAndStatus(
                property, tenant, Booking.BookingStatus.CONFIRMED);
        if (existingBooking.isPresent()) {
            throw new RuntimeException("You already have a confirmed booking for this property");
        }

        Booking booking = new Booking(property, tenant);
        booking.setNotes(notes);

        return bookingRepository.save(booking);
    }

    public Booking confirmBooking(Long bookingId, Long landlordId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getProperty().getLandlord().getId().equals(landlordId)) {
            throw new RuntimeException("You can only confirm bookings for your own properties");
        }

        if (booking.getStatus() != Booking.BookingStatus.PENDING) {
            throw new RuntimeException("Only pending bookings can be confirmed");
        }

        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        
        // Update property availability
        Property property = booking.getProperty();
        property.setAvailabilityStatus(Property.AvailabilityStatus.BOOKED);
        propertyService.updateAvailabilityStatus(property.getId(), Property.AvailabilityStatus.BOOKED);

        return bookingRepository.save(booking);
    }

    public Booking cancelBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Check if user is the tenant who made the booking or the landlord of the property
        boolean canCancel = booking.getTenant().getId().equals(userId) ||
                           booking.getProperty().getLandlord().getId().equals(userId);

        if (!canCancel) {
            throw new RuntimeException("You don't have permission to cancel this booking");
        }

        if (booking.getStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);

        // If booking was confirmed, make property available again
        if (booking.getStatus() == Booking.BookingStatus.CONFIRMED) {
            propertyService.updateAvailabilityStatus(booking.getProperty().getId(), 
                    Property.AvailabilityStatus.AVAILABLE);
        }

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByTenant(Long tenantId) {
        return bookingRepository.findByTenantId(tenantId);
    }

    public List<Booking> getBookingsByLandlord(Long landlordId) {
        return bookingRepository.findByLandlordId(landlordId);
    }

    public List<Booking> getBookingsByProperty(Long propertyId) {
        return bookingRepository.findByPropertyId(propertyId);
    }

    public Booking getBookingById(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public List<Booking> getBookingsByStatus(Booking.BookingStatus status) {
        return bookingRepository.findByStatus(status);
    }

    public Long getBookingCountByTenant(Long tenantId) {
        return bookingRepository.countByTenantId(tenantId);
    }

    public Long getBookingCountByLandlord(Long landlordId) {
        return bookingRepository.countByLandlordId(landlordId);
    }
}