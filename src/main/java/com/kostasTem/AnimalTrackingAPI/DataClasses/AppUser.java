package com.kostasTem.AnimalTrackingAPI.DataClasses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kostasTem.AnimalTrackingAPI.Utils.StringListConverter;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String username;
    @Column(unique=true)
    private String email;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String imagePath;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition="TEXT")
    private List<String> deviceID;
    private LocalDate age;
    @JsonIgnore
    private String provider;
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition="TEXT",nullable = false)
    private List<String> roles;
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String token;
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String firebaseAuthToken;
    private String countryCode;
    private Boolean accountVerified;

    public AppUser(Long id, String username, String email, String password, LocalDate age, String provider, List<String> roles, List<String> deviceID, String countryCode) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.age = age;
        this.provider = provider;
        this.roles = roles;
        this.deviceID = deviceID;
        this.countryCode = countryCode;
        this.accountVerified = false;
    }

    public AppUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public List<String> getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(List<String> deviceID) {
        this.deviceID = deviceID;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public LocalDate getAge() {
        return age;
    }

    public void setAge(LocalDate age) {
        this.age = age;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFirebaseAuthToken() {
        return firebaseAuthToken;
    }

    public void setFirebaseAuthToken(String firebaseAuthToken) {
        this.firebaseAuthToken = firebaseAuthToken;
    }

    public Boolean getAccountVerified() {
        return accountVerified;
    }

    public void setAccountVerified(Boolean accountVerified) {
        this.accountVerified = accountVerified;
    }
}
