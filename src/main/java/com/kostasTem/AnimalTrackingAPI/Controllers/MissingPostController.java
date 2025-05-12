package com.kostasTem.AnimalTrackingAPI.Controllers;


import com.google.firebase.messaging.FirebaseMessagingException;
import com.kostasTem.AnimalTrackingAPI.DataClasses.AppUser;
import com.kostasTem.AnimalTrackingAPI.DataClasses.MissingPost;
import com.kostasTem.AnimalTrackingAPI.Services.FirebaseMessagingService;
import com.kostasTem.AnimalTrackingAPI.Services.MissingPostService;
import com.kostasTem.AnimalTrackingAPI.Services.UserService;
import com.kostasTem.AnimalTrackingAPI.Utils.PostStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/missing")
@RequiredArgsConstructor
@Slf4j
public class MissingPostController {

    private final MissingPostService missingPostService;
    private final UserService userService;
    private final FirebaseMessagingService firebaseMessagingService;

    @GetMapping(value = "/", params = "countryCode")
    public ResponseEntity<List<MissingPost>> getMissingPosts(@RequestParam String countryCode, @RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal) {
        List<MissingPost> toFetch;
        if(principal != null) {
            toFetch = missingPostService.getUserPosts(userService.getUserByEmail(principal.getName()).getUsername());
        }
        else{
            toFetch = missingPostService.getPostByCountryCode(countryCode);
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
    public ResponseEntity<List<MissingPost>> getAllMissingPosts(@RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal) {
        List<MissingPost> toFetch;
        if(principal != null) {
            toFetch = missingPostService.getUserPosts(userService.getUserByEmail(principal.getName()).getUsername());
        }
        else{
            toFetch = missingPostService.getAllPosts();
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

    @GetMapping(value = "/", params = "id")
    public ResponseEntity<MissingPost> getMissingPost(@RequestParam Long id) {
        MissingPost toFetch = missingPostService.getPost(id);
        if (toFetch != null) {
            return ResponseEntity.ok().body(toFetch);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @GetMapping(value = "/gov/")
    @PreAuthorize("hasRole('ROLE_GOV')")
    public ResponseEntity<List<MissingPost>> getMissingPostsForGov(@RequestParam(name = "countryCode") String countryCode, @RequestParam(name = "location", required = false, defaultValue = "") List<String> locations, @RequestParam(name = "animal", required = false, defaultValue = "") List<String> animals, Principal principal) {
        if (countryCode == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header("Custom-Message", "The Format Of Your Request Is Invalid. You Need To Provide A countryCode To Receive A Response").build();
        }
        List<MissingPost> missingPosts = missingPostService.getGovPosts();
        if (!countryCode.equalsIgnoreCase(userService.getUserByEmail(principal.getName()).getCountryCode())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "You Don't Have Access To This Data.").build();
        }
        //missingPosts = missingPosts.stream().filter(post -> post.getPostStatus() != PostStatus.ANIMAL_FOUND_BY_OFFICIALS && post.getPostStatus() != PostStatus.ANIMAL_NOT_FOUND).collect(Collectors.toList());
        missingPosts = missingPosts.stream().filter(post -> post.getCountryCode().equalsIgnoreCase(countryCode)).collect(Collectors.toList());
        if (locations != null && locations.size() > 0) {
            missingPosts = missingPosts.stream().filter(post -> locations.contains(post.getLocation().get(2))).collect(Collectors.toList());
        }
        if (animals != null && animals.size() > 0) {
            missingPosts = missingPosts.stream().filter(post -> animals.contains(post.getAnimalType())).collect(Collectors.toList());
        }
        return ResponseEntity.ok().body(missingPosts);
    }

    @GetMapping("/archive/{id}")
    @PreAuthorize("hasRole('ROLE_USER') and #userEmail==#principal.name")
    public ResponseEntity<MissingPost> archivePost(@PathVariable Long id, Principal principal) {
        MissingPost toArchive = missingPostService.archivePost(id);
        if (toArchive != null) {
            return ResponseEntity.ok().body(toArchive);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MissingPost> addMissingPost(@RequestBody MissingPost missingPost, Principal principal) {
        missingPost.setId(null);
        missingPost.setUsername(userService.getUserByEmail(principal.getName()).getUsername());
        missingPost.setTimestamp(System.currentTimeMillis());
        missingPost.setArchived(false);
        missingPost.setPostStatus(PostStatus.NONE);
        MissingPost saved = missingPostService.savePost(missingPost);
        try {
            firebaseMessagingService.sendNotificationToMissingNearby(saved.getId().toString(), saved.getLatitude(), saved.getLongitude(), "An Animal Went Missing Near Your Location. Can You Help Find It?", saved.getUsername());
        } catch (FirebaseMessagingException e) {
            log.error(e.getLocalizedMessage());
        }
        return ResponseEntity.ok().body(saved);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<MissingPost> updateMissingPost(@PathVariable Long id, @RequestBody MissingPost missingPost, Principal principal) {
        MissingPost toPatch = missingPostService.getPost(id);
        if (toPatch != null) {
            if (!Objects.equals(toPatch.getUsername(), userService.getUserByEmail(principal.getName()).getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Unauthorized API Interaction").build();
            }
            missingPost.setId(id);
            missingPost.setUsername(toPatch.getUsername());
            missingPost.setTimestamp(toPatch.getTimestamp());
            return ResponseEntity.ok().body(missingPostService.savePost(missingPost));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> deleteMissingPost(@PathVariable Long id, Principal principal) {
        MissingPost toDelete = missingPostService.getPost(id);
        if (toDelete != null) {
            if (!Objects.equals(toDelete.getUsername(), userService.getUserByEmail(principal.getName()).getUsername())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).header("Custom-Message", "Unauthorized API Interaction").build();
            }
            missingPostService.deletePost(toDelete.getId());
            return ResponseEntity.ok().body("Success");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    @PatchMapping("/updateStatus/{id}")
    @PreAuthorize("hasRole('ROLE_GOV')")
    public ResponseEntity<MissingPost> updatePostStatus(@PathVariable Long id, @RequestBody PostStatus postStatus, Principal principal) {
        MissingPost toPatch = missingPostService.getPost(id);
        AppUser appUser = userService.getUser(toPatch.getUsername());
        if (toPatch != null && appUser != null && appUser.getCountryCode().equalsIgnoreCase(toPatch.getCountryCode())) {
            toPatch.setPostStatus(postStatus);
            appUser.getDeviceID().forEach(deviceID -> {
                try {
                    firebaseMessagingService.sendNotificationToDeviceID(deviceID, toPatch.getId().toString(), "Missing", "Your Post Status Has Been Updated.",appUser.getUsername());
                } catch (FirebaseMessagingException e) {
                    log.error(e.getLocalizedMessage());
                    e.printStackTrace();
                }
            });
            return ResponseEntity.ok().body(missingPostService.savePost(toPatch));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).header("Custom-Message", "Post ID Not Valid.").build();
    }

    public boolean isOwnedByUser(Long id, Principal principal) {
        return Objects.equals(missingPostService.getPost(id).getUsername(), userService.getUserByEmail(principal.getName()).getUsername());
    }

}
