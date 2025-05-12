package com.kostasTem.AnimalTrackingAPI.Services;

import com.kostasTem.AnimalTrackingAPI.DataClasses.EncounteredPost;
import com.kostasTem.AnimalTrackingAPI.Repositories.EncounteredPostRepository;
import com.kostasTem.AnimalTrackingAPI.Utils.PostStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
public class EncounteredPostServiceImpl implements EncounteredPostService{

    private final EncounteredPostRepository encounteredPostRepository;

    @Override
    public EncounteredPost savePost(EncounteredPost encounteredPost) {
        return encounteredPostRepository.save(encounteredPost);
    }

    @Override
    public EncounteredPost getPost(Long id) {
        return encounteredPostRepository.findById(id).orElse(null);
    }

    @Override
    public EncounteredPost archivePost(Long id) {
        EncounteredPost toArchive = encounteredPostRepository.findById(id).orElse(null);
        if(toArchive != null) {
            toArchive.setArchived(true);
            return encounteredPostRepository.save(toArchive);
        }
        return null;
    }

    @Override
    public List<EncounteredPost> getUserPosts(String username) {
        return encounteredPostRepository.findByUsername(username);
    }

    @Override
    public List<EncounteredPost> getAllPosts() {
        return encounteredPostRepository.findAll();
    }

    @Override
    public List<EncounteredPost> getPendingPosts() {
        return encounteredPostRepository.findByArchived(false);
    }
    //&& Instant.ofEpochMilli(post.getTimestamp()).isAfter(Instant.now().minus(30, ChronoUnit.DAYS))
    @Override
    public List<EncounteredPost> getPostsByCountryCode(String countryCode) {
        return encounteredPostRepository.findByCountryCode(countryCode).stream().filter(post -> !post.isArchived()).collect(Collectors.toList());
    }
    @Override
    public List<EncounteredPost> getGovPosts() {
        return encounteredPostRepository.findAll().stream().filter(post -> post.getPostStatus() != PostStatus.NONE).collect(Collectors.toList());
    }

    @Override
    public void deletePost(Long id) {
        encounteredPostRepository.deleteById(id);
    }
}
