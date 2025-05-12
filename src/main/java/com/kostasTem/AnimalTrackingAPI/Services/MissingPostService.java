package com.kostasTem.AnimalTrackingAPI.Services;


import com.kostasTem.AnimalTrackingAPI.DataClasses.MissingPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface MissingPostService {

    MissingPost savePost(MissingPost missingPost);
    MissingPost getPost(Long id);
    MissingPost archivePost(Long id);
    List<MissingPost> getUserPosts(String username);
    List<MissingPost> getAllPosts();
    List<MissingPost> getPendingPosts();
    List<MissingPost> getPostByCountryCode(String countryCode);
    List<MissingPost> getGovPosts();
    void deletePost(Long id);


}
