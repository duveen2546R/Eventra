package com.evantra.evantra.dto;

import java.util.Map;

public class AuthResponse {
    private String token;
    private String message;
    private Map<String, Object> userData;

    public AuthResponse(String token) {
        this.token = token;
    }

    // Getters & Setters
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, Object> getUserData() {
        return userData;
    }
    public void setUserData(Map<String, Object> userData) {
        this.userData = userData;
    }
}
