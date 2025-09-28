package com.rentx.rentalsystem.repository;

import com.rentx.rentalsystem.entity.Report;
import com.rentx.rentalsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Report entity
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByReporter(User reporter);
    
    List<Report> findByReportedUser(User reportedUser);
    
    List<Report> findByReportType(Report.ReportType reportType);
    
    List<Report> findByStatus(Report.ReportStatus status);
    
    @Query("SELECT r FROM Report r WHERE r.property.id = :propertyId")
    List<Report> findByPropertyId(@Param("propertyId") Long propertyId);
    
    @Query("SELECT r FROM Report r WHERE r.reporter.id = :reporterId AND r.status = :status")
    List<Report> findByReporterIdAndStatus(@Param("reporterId") Long reporterId, @Param("status") Report.ReportStatus status);
    
    @Query("SELECT r FROM Report r WHERE r.reportedUser.id = :reportedUserId")
    List<Report> findByReportedUserId(@Param("reportedUserId") Long reportedUserId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.status = :status")
    Long countByStatus(@Param("status") Report.ReportStatus status);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.reportedUser.id = :userId")
    Long countReportsAgainstUser(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(r) FROM Report r WHERE r.property.id = :propertyId")
    Long countReportsAgainstProperty(@Param("propertyId") Long propertyId);
}