package com.kostasTem.AnimalTrackingAPI.DataClasses;

import com.kostasTem.AnimalTrackingAPI.Utils.PostType;
import jakarta.persistence.*;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long postID;
    private String reason;
    private String username;
    private String comment;
    @Enumerated(EnumType.STRING)
    private PostType postType;

    public Report(Long id, Long postID, String reason, String username, String comment, PostType postType) {
        this.id = id;
        this.postID = postID;
        this.reason = reason;
        this.username = username;
        this.comment = comment;
        this.postType = postType;
    }

    public Report() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPostID() {
        return postID;
    }

    public void setPostID(Long postID) {
        this.postID = postID;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public PostType getPostType() {
        return postType;
    }

    public void setPostType(PostType postType) {
        this.postType = postType;
    }
}

