package com.kostasTem.AnimalTrackingAPI.Repositories;

import com.kostasTem.AnimalTrackingAPI.DataClasses.MissingPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissingPostRepository extends JpaRepository<MissingPost, Long> {

    List<MissingPost> findByUsername(String username);

    List<MissingPost> findByArchived(boolean archived);

    List<MissingPost> findByCountryCode(String countryCode);

}
