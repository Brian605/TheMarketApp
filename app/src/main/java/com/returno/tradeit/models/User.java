package com.returno.tradeit.models;

public class User {
private final String userId;
    private final String userName;
    private final String phoneNumber;
    private final String location;
private final float userRating;
private String userStatus;

    public User(String userId, String userName, String phoneNumber, String location, float userRating) {
        this.userId = userId;
        this.userName = userName;
        this.phoneNumber = phoneNumber;
        this.location = location;
        this.userRating = userRating;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public float getUserRating() {
        return userRating;
    }
}
