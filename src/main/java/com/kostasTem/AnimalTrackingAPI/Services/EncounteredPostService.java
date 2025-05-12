package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.EncounteredPost;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EncounteredPostService {

    EncounteredPost savePost(EncounteredPost encounteredPost);
    EncounteredPost getPost(Long id);
    EncounteredPost archivePost(Long id);
    List<EncounteredPost> getUserPosts(String username);
    List<EncounteredPost> getAllPosts();
    List<EncounteredPost> getPendingPosts();
    List<EncounteredPost> getPostsByCountryCode(String countryCode);
    List<EncounteredPost> getGovPosts();
    void deletePost(Long id);

}
