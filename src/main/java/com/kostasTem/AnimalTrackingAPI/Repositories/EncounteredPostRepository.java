package com.kostasTem.AnimalTrackingAPI.Repositories;

import com.kostasTem.AnimalTrackingAPI.DataClasses.EncounteredPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EncounteredPostRepository extends JpaRepository<EncounteredPost, Long> {

    List<EncounteredPost> findByUsername(String username);

    List<EncounteredPost> findByArchived(boolean archived);

    List<EncounteredPost> findByCountryCode(String countryCode);

}
