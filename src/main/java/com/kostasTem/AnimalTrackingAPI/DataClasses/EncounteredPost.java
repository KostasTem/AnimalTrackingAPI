package com.kostasTem.AnimalTrackingAPI.DataClasses;

import com.kostasTem.AnimalTrackingAPI.Utils.PostStatus;
import com.kostasTem.AnimalTrackingAPI.Utils.StringListConverter;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class EncounteredPost {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String animalType;
    private String animalImage;
    private String comment;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition="TEXT",nullable = false)
    private List<String> location;
    private Double latitude;
    private Double longitude;
    private String countryCode;
    private Long timestamp;
    private boolean archived;
    private boolean hostile;
    @Enumerated(EnumType.STRING)
    private PostStatus postStatus;

    public EncounteredPost(Long id, String username, String animalType, String comment, Double latitude, Double longitude, List<String> location, String countryCode, Long timestamp) {
        this.id = id;
        this.username = username;
        this.animalType = animalType;
        this.comment = comment;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
        this.location = location;
        this.countryCode = countryCode;
        this.archived = false;
        this.hostile = false;
        postStatus = PostStatus.NONE;
    }

    public EncounteredPost() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAnimalType() {
        return animalType;
    }

    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }

    public String getAnimalImage() {
        return animalImage;
    }

    public void setAnimalImage(String animalImage) {
        this.animalImage = animalImage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public List<String> getLocation() {
        return location;
    }

    public void setLocation(List<String> location) {
        this.location = location;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public boolean isHostile() {
        return hostile;
    }

    public void setHostile(boolean hostile) {
        this.hostile = hostile;
    }

    public PostStatus getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(PostStatus postStatus) {
        this.postStatus = postStatus;
    }
}
