package com.kostasTem.AnimalTrackingAPI.Controllers;


import com.kostasTem.AnimalTrackingAPI.DataClasses.Report;
import com.kostasTem.AnimalTrackingAPI.Services.EncounteredPostService;
import com.kostasTem.AnimalTrackingAPI.Services.MissingPostService;
import com.kostasTem.AnimalTrackingAPI.Services.ReportService;
import com.kostasTem.AnimalTrackingAPI.Utils.PostType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    private final EncounteredPostService encounteredPostService;
    private final MissingPostService missingPostService;
    private final ReportService reportService;

    @GetMapping("/")
    public ResponseEntity<List<Report>> getReports(){
        return ResponseEntity.ok().body(reportService.getAllReports());
    }

    @GetMapping(value = "/", params = "id")
    public ResponseEntity<Report> getReport(@RequestParam Long id){
        Report report = reportService.getReport(id);
        if(report == null){
            ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Report ID Not Valid.").build();
        }
        return ResponseEntity.ok().body(report);
    }

    @GetMapping(value = "/",params = {"postID","postType"})
    public ResponseEntity<List<Report>> getReportsByPost(@RequestParam Long postID, @RequestParam PostType postType){
        List<Report> report = reportService.getReportsForPost(postID,postType);
        if(report == null){
            ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
        }
        return ResponseEntity.ok().body(report);
    }

    @PostMapping(value = "/")
    public ResponseEntity<Report> addReport(@RequestBody Report report){
        if(report.getPostType() == PostType.ENCOUNTERED){
            if(encounteredPostService.getPost(report.getPostID())==null){
                ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
            }
        }
        else if(report.getPostType() == PostType.MISSING){
            if(missingPostService.getPost(report.getPostID())==null) {
                ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
            }
        }
        return ResponseEntity.ok().body(reportService.saveReport(report));
    }
}
