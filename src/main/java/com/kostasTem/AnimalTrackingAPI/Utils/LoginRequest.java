package com.kostasTem.AnimalTrackingAPI.Utils;

public class LoginRequest {
    private String email;
    private String password;
    private String deviceID;
    private String googleToken;
    private Boolean staySignedIn;

    public LoginRequest(String email, String password, String deviceID, String googleToken, Boolean staySignedIn) {
        this.email = email;
        this.password = password;
        this.deviceID = deviceID;
        this.googleToken = googleToken;
        this.staySignedIn = staySignedIn;
    }

    public LoginRequest() {
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

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getGoogleToken() {
        return googleToken;
    }

    public void setGoogleToken(String googleToken) {
        this.googleToken = googleToken;
    }

    public Boolean getStaySignedIn() {
        return staySignedIn;
    }

    public void setStaySignedIn(Boolean staySignedIn) {
        this.staySignedIn = staySignedIn;
    }
}
