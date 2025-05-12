package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.Report;
import com.kostasTem.AnimalTrackingAPI.Utils.PostType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ReportService {
    Report saveReport(Report report);
    Report getReport(Long id);
    List<Report> getReportsForPost(Long postID, PostType postType);
    List<Report> getAllReports();
    void deleteReport(Long id);
}
