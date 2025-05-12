package com.kostasTem.AnimalTrackingAPI.Services;


import com.kostasTem.AnimalTrackingAPI.DataClasses.MissingPost;
import com.kostasTem.AnimalTrackingAPI.Repositories.MissingPostRepository;
import com.kostasTem.AnimalTrackingAPI.Utils.PostStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MissingPostServiceImpl implements MissingPostService {

    private final MissingPostRepository missingPostRepository;

    @Override
    public MissingPost savePost(MissingPost missingPost) {
        return missingPostRepository.save(missingPost);
    }

    @Override
    public MissingPost getPost(Long id) {
        return missingPostRepository.findById(id).orElse(null);
    }

    @Override
    public MissingPost archivePost(Long id) {
        MissingPost toArchive = missingPostRepository.findById(id).orElse(null);
        if(toArchive != null) {
             toArchive.setArchived(true);
             return missingPostRepository.save(toArchive);
        }
        return null;
    }

    @Override
    public List<MissingPost> getUserPosts(String username) {
        return missingPostRepository.findByUsername(username);
    }

    @Override
    public List<MissingPost> getAllPosts() {
        return missingPostRepository.findAll();
    }

    @Override
    public List<MissingPost> getPendingPosts() {
        return missingPostRepository.findByArchived(false);
    }
    //&& Instant.ofEpochMilli(post.getTimestamp()).isAfter(Instant.now().minus(30, ChronoUnit.DAYS))
    @Override
    public List<MissingPost> getPostByCountryCode(String countryCode) {
        return missingPostRepository.findByCountryCode(countryCode).stream().filter(post -> !post.isArchived()).collect(Collectors.toList());
    }

    @Override
    public List<MissingPost> getGovPosts() {
        return missingPostRepository.findAll().stream().filter(post -> post.getPostStatus() != PostStatus.NONE).collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long id) {
        missingPostRepository.deleteById(id);
    }
}
