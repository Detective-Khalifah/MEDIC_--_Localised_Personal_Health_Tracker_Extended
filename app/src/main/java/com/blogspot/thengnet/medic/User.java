package com.blogspot.thengnet.medic;

public class User {
    private final String fullName;
    private final String userName;

    public User (String fullName, String userName) {
        this.fullName = fullName;
        this.userName = userName;
    }

    public String getFullName () {
        return this.fullName;
    }

    public String getUserName() {
        return this.userName;
    }
}
