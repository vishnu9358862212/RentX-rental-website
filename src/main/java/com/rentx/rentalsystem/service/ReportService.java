package com.rentx.rentalsystem.service;

import com.rentx.rentalsystem.entity.Property;
import com.rentx.rentalsystem.entity.Report;
import com.rentx.rentalsystem.entity.User;
import com.rentx.rentalsystem.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service layer for Report operations
 */
@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PropertyService propertyService;

    public Report reportUser(Long reporterId, Long reportedUserId, Report.ReportType reportType, 
                           String reason, String description) {
        User reporter = userService.findById(reporterId);
        User reportedUser = userService.findById(reportedUserId);

        if (reporter.getId().equals(reportedUser.getId())) {
            throw new RuntimeException("You cannot report yourself");
        }

        Report report = new Report(reporter, reportType, reason);
        report.setReportedUser(reportedUser);
        report.setDescription(description);

        return reportRepository.save(report);
    }

    public Report reportProperty(Long reporterId, Long propertyId, Report.ReportType reportType,
                               String reason, String description) {
        User reporter = userService.findById(reporterId);
        Property property = propertyService.getPropertyById(propertyId);

        if (reporter.getId().equals(property.getLandlord().getId())) {
            throw new RuntimeException("You cannot report your own property");
        }

        Report report = new Report(reporter, reportType, reason);
        report.setProperty(property);
        report.setDescription(description);

        return reportRepository.save(report);
    }

    public Report resolveReport(Long reportId, Long adminId, String adminResponse, 
                              Report.ReportStatus status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User admin = userService.findById(adminId);
        if (admin.getUserType() != User.UserType.ADMIN) {
            throw new RuntimeException("Only admins can resolve reports");
        }

        report.setStatus(status);
        report.setAdminResponse(adminResponse);
        report.setResolvedAt(LocalDateTime.now());
        report.setResolvedByAdmin(admin);

        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public List<Report> getReportsByStatus(Report.ReportStatus status) {
        return reportRepository.findByStatus(status);
    }

    public List<Report> getReportsByReporter(Long reporterId) {
        User reporter = userService.findById(reporterId);
        return reportRepository.findByReporter(reporter);
    }

    public List<Report> getReportsAgainstUser(Long userId) {
        return reportRepository.findByReportedUserId(userId);
    }

    public List<Report> getReportsAgainstProperty(Long propertyId) {
        return reportRepository.findByPropertyId(propertyId);
    }

    public Report getReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public List<Report> getReportsByType(Report.ReportType reportType) {
        return reportRepository.findByReportType(reportType);
    }

    public Long getReportCountByStatus(Report.ReportStatus status) {
        return reportRepository.countByStatus(status);
    }

    public Long getReportCountAgainstUser(Long userId) {
        return reportRepository.countReportsAgainstUser(userId);
    }

    public Long getReportCountAgainstProperty(Long propertyId) {
        return reportRepository.countReportsAgainstProperty(propertyId);
    }

    public List<Report> getPendingReports() {
        return reportRepository.findByStatus(Report.ReportStatus.PENDING);
    }
}