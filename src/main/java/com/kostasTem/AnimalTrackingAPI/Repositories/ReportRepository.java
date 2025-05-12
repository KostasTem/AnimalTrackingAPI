package com.kostasTem.AnimalTrackingAPI.Repositories;

import com.kostasTem.AnimalTrackingAPI.DataClasses.Report;
import com.kostasTem.AnimalTrackingAPI.Utils.PostType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByPostIDAndPostType(Long postID, PostType postType);

}
