package com.kostasTem.AnimalTrackingAPI.Utils;

import java.time.LocalDate;

public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String imagePath;
    private LocalDate age;
    private String countryCode;

    public RegisterRequest(String username, String email, String password, String imagePath, LocalDate age, String countryCode) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.imagePath = imagePath;
        this.age = age;
        this.countryCode = countryCode;
    }

    public RegisterRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public LocalDate getAge() {
        return age;
    }

    public void setAge(LocalDate age) {
        this.age = age;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }
}
