package com.rentx.rentalsystem.util;

import org.springframework.stereotype.Component;

/**
 * Utility class for distance calculations using Haversine formula
 */
@Component
public class DistanceUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Calculate distance between two points using Haversine formula
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @return Distance in kilometers
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Calculate distance and return in specified unit
     * 
     * @param lat1 Latitude of first point
     * @param lon1 Longitude of first point
     * @param lat2 Latitude of second point
     * @param lon2 Longitude of second point
     * @param unit Distance unit (km, mi)
     * @return Distance in specified unit
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2, String unit) {
        double distance = calculateDistance(lat1, lon1, lat2, lon2);
        
        if ("mi".equalsIgnoreCase(unit) || "miles".equalsIgnoreCase(unit)) {
            return distance * 0.621371; // Convert km to miles
        }
        
        return distance; // Default to kilometers
    }

    /**
     * Check if a point is within a certain radius of another point
     * 
     * @param centerLat Center point latitude
     * @param centerLon Center point longitude
     * @param pointLat Point latitude to check
     * @param pointLon Point longitude to check
     * @param radiusKm Radius in kilometers
     * @return true if point is within radius
     */
    public boolean isWithinRadius(double centerLat, double centerLon, 
                                double pointLat, double pointLon, double radiusKm) {
        double distance = calculateDistance(centerLat, centerLon, pointLat, pointLon);
        return distance <= radiusKm;
    }
}