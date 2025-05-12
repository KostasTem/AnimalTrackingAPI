package com.kostasTem.AnimalTrackingAPI.Controllers;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import com.kostasTem.AnimalTrackingAPI.DataClasses.EncounteredPost;
import com.kostasTem.AnimalTrackingAPI.Services.EncounteredPostService;
import com.kostasTem.AnimalTrackingAPI.Services.FirebaseMessagingService;
import com.kostasTem.AnimalTrackingAPI.Services.UserService;
import com.kostasTem.AnimalTrackingAPI.Utils.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/encountered")
@RequiredArgsConstructor
@Slf4j
public class EncounteredPostController {

    private final EncounteredPostService encounteredPostService;
    private final UserService userService;
    private final FirebaseMessagingService firebaseMessagingService;

    @GetMapping(value = "/",params = "countryCode")
    public ResponseEntity<List<EncounteredPost>> getEncounteredPosts(@RequestParam String countryCode, @RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal){
        List<EncounteredPost> toFetch;
        if(principal != null) {
            toFetch = encounteredPostService.getUserPosts(userService.getUserByEmail(principal.getName()).getUsername());
        }
        else{
            toFetch = encounteredPostService.getPostsByCountryCode(countryCode);
        }
        if (locations != null && locations.size() > 0) {
            toFetch = toFetch.stream().filter(post -> locations.contains(post.getLocation().get(2))).collect(Collectors.toList());
        }
        if (animals != null && animals.size() > 0) {
            toFetch = toFetch.stream().filter(post -> animals.contains(post.getAnimalType())).collect(Collectors.toList());
        }
        if (toFetch != null) {
            return ResponseEntity.ok().body(toFetch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "This User Doesn't Have Any Posts.").build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<EncounteredPost>> getAllEncounteredPosts(@RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal){
        List<EncounteredPost> toFetch;
        if(principal != null) {
            toFetch = encounteredPostService.getUserPosts(userService.getUserByEmail(principal.getName()).getUsername());
        }
        else{
            toFetch = encounteredPostService.getAllPosts();
        }
        if (locations != null && locations.size() > 0) {
            toFetch = toFetch.stream().filter(post -> locations.contains(post.getLocation().get(2))).collect(Collectors.toList());
        }
        if (animals != null && animals.size() > 0) {
            toFetch = toFetch.stream().filter(post -> animals.contains(post.getAnimalType())).collect(Collectors.toList());
        }
        if (toFetch != null) {
            return ResponseEntity.ok().body(toFetch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "This User Doesn't Have Any Posts.").build();
    }

    @GetMapping(value = "/",params = "id")
    public ResponseEntity<EncounteredPost> getEncounteredPost(@RequestParam Long id){
        EncounteredPost toFetch = encounteredPostService.getPost(id);
        if(toFetch != null) {
            return ResponseEntity.ok().body(toFetch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @GetMapping("/user/{userEmail}")
    @PreAuthorize("hasRole('ROLE_USER') and #userEmail==#principal.name")
    public ResponseEntity<List<EncounteredPost>> getUserEncounteredPosts(@PathVariable String userEmail, Principal principal){
        List<EncounteredPost> toFetch = encounteredPostService.getUserPosts(userEmail);
        if(toFetch != null) {
            return ResponseEntity.ok().body(toFetch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @GetMapping(value = "/gov/")
    @PreAuthorize("hasRole('ROLE_GOV')")
    public ResponseEntity<List<EncounteredPost>> getEncounteredPostsForGov(@RequestParam(name = "countryCode") String countryCode, @RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal){
        if(countryCode == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Custom-Message", "The Format Of Your Request Is Invalid. You Need To Provide A countryCode To Receive A Response").build();
        }
        List<EncounteredPost> encounteredPosts = encounteredPostService.getGovPosts();
        if(!countryCode.equalsIgnoreCase(userService.getUserByEmail(principal.getName()).getCountryCode())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "You Don't Have Access To This Data.").build();
        }
        //encounteredPosts = encounteredPosts.stream().filter(post -> post.getPostStatus() != PostStatus.ANIMAL_FOUND_BY_OFFICIALS && post.getPostStatus() != PostStatus.ANIMAL_NOT_FOUND).collect(Collectors.toList());
        encounteredPosts = encounteredPosts.stream().filter(post -> post.getCountryCode().equalsIgnoreCase(countryCode)).collect(Collectors.toList());
        if(locations!=null && locations.size() > 0){
            encounteredPosts = encounteredPosts.stream().filter(post -> locations.contains(post.getLocation().get(2))).collect(Collectors.toList());
        }
        if(animals!=null && animals.size() > 0){
            encounteredPosts = encounteredPosts.stream().filter(post -> animals.contains(post.getAnimalType())).collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(encounteredPosts);
    }

    @GetMapping("/archive/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<EncounteredPost> archivePost(@PathVariable Long id, Principal principal){
        EncounteredPost toArchive = encounteredPostService.archivePost(id);
        if(toArchive != null) {
            return ResponseEntity.ok().body(toArchive);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<EncounteredPost> addEncounteredPost(@RequestBody EncounteredPost encounteredPost, Principal principal){
        encounteredPost.setId(null);
        encounteredPost.setUsername(userService.getUserByEmail(principal.getName()).getUsername());
        encounteredPost.setTimestamp(Instant.now().toEpochMilli());
        encounteredPost.setArchived(false);
        encounteredPost.setPostStatus(PostStatus.NONE);
        EncounteredPost saved = encounteredPostService.savePost(encounteredPost);
        try {
            firebaseMessagingService.sendNotificationToEncounteredNearby(saved.getId().toString(), saved.getLatitude(), saved.getLongitude(), "An Animal Was Seen Near Your Location. Watch Out For It!",saved.getUsername());
        } catch (FirebaseMessagingException e) {
            log.error(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().body(encounteredPostService.savePost(encounteredPost));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<EncounteredPost> updateEncounteredPost(@PathVariable Long id,@RequestBody EncounteredPost encounteredPost, Principal principal){
        EncounteredPost toPatch = encounteredPostService.getPost(id);
        if(toPatch != null) {
            if (!Objects.equals(toPatch.getUsername(), userService.getUserByEmail(principal.getName()).getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Unauthorized API Interaction").build();
            }
            encounteredPost.setId(id);
            encounteredPost.setUsername(toPatch.getUsername());
            encounteredPost.setTimestamp(toPatch.getTimestamp());
            return ResponseEntity.ok().body(encounteredPostService.savePost(encounteredPost));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteEncounteredPost(@PathVariable Long id, Principal principal){
        EncounteredPost toDelete = encounteredPostService.getPost(id);
        if(toDelete != null) {
            if (!Objects.equals(toDelete.getUsername(), userService.getUserByEmail(principal.getName()).getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Unauthorized API Interaction").build();
            }
            encounteredPostService.deletePost(toDelete.getId());
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @PatchMapping("/updateStatus/{id}")
    @PreAuthorize("hasRole('ROLE_GOV')")
    public ResponseEntity<EncounteredPost> updatePostStatus(@PathVariable Long id, @RequestBody PostStatus postStatus, Principal principal){
        EncounteredPost toPatch = encounteredPostService.getPost(id);
        AppUser appUser = userService.getUser(toPatch.getUsername());
        if(toPatch != null && appUser.getCountryCode().equalsIgnoreCase(toPatch.getCountryCode())){
            toPatch.setPostStatus(postStatus);
            appUser.getDeviceID().forEach(deviceID -> {
                try {
                    firebaseMessagingService.sendNotificationToDeviceID(deviceID, toPatch.getId().toString(), "Encountered","Your Post Status Has Been Updated.",appUser.getUsername());
                } catch (FirebaseMessagingException e) {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
            return ResponseEntity.ok().body(encounteredPostService.savePost(toPatch));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

}
