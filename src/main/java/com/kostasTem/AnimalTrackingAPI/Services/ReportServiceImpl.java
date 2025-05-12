package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.Report;
import com.kostasTem.AnimalTrackingAPI.Repositories.ReportRepository;
import com.kostasTem.AnimalTrackingAPI.Utils.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService{

    private final ReportRepository reportRepository;

    @Override
    public Report saveReport(Report report) {
        return reportRepository.save(report);
    }

    @Override
    public Report getReport(Long id) {
        return reportRepository.findById(id).orElse(null);
    }

    @Override
    public List<Report> getReportsForPost(Long postID, PostType postType) {
        return reportRepository.findByPostIDAndPostType(postID,postType);
    }

    @Override
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    @Override
    public void deleteReport(Long id) {
        reportRepository.deleteById(id);
    }
}
