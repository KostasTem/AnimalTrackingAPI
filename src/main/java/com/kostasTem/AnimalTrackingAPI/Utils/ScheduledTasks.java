package com.kostasTem.AnimalTrackingAPI.Utils;

import com.kostasTem.AnimalTrackingAPI.DataClasses.EncounteredPost;
import com.kostasTem.AnimalTrackingAPI.DataClasses.MissingPost;
import com.kostasTem.AnimalTrackingAPI.Services.EncounteredPostService;
import com.kostasTem.AnimalTrackingAPI.Services.MissingPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduledTasks {

    private final EncounteredPostService encounteredPostService;
    private final MissingPostService missingPostService;

    //@Scheduled(fixedDelay = 3600000, initialDelay = 60000)
    public void archivePosts(){
        List<MissingPost> missingPosts = missingPostService.getAllPosts();
        long oneMonthAgoMillis = LocalDateTime.now().minusMonths(1).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        for(MissingPost post: missingPosts){
            if(post.getTimestamp() < oneMonthAgoMillis){
                post.setArchived(true);
                missingPostService.savePost(post);
            }
        }
        List<EncounteredPost> encounteredPosts = encounteredPostService.getAllPosts();
        for(EncounteredPost post: encounteredPosts){
            if(post.getTimestamp() < oneMonthAgoMillis){
                post.setArchived(true);
                encounteredPostService.savePost(post);
            }
        }
    }

}
