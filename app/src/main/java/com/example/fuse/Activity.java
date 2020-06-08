package com.example.fuse;

import android.net.Uri;

import java.net.URI;

public class Activity {
    private String heading;
    private String userName;
    private String description;
    private String email;
    private String date;
    private String userAge;
    private String imageUri;
    private String userGender;

    public String getHeading() {
        return heading;
    }

    public String getUserName() {
        return userName;
    }

    public String getDescription() {
        return description;
    }

    public String getEmail() {
        return email;
    }

    public String getDate() {
        return date;
    }

    public String getUserAge() {
        return userAge;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getUserGender() {
        return userGender;
    }

    public Activity()
    {

    }
    public Activity(String heading, String name, String desc, String email, String date, String age, String imageuri, String gender){
        this.heading = heading; this.userName = name; this.description = desc; this.userAge= age;
        this.email = email; this.date = date; this.imageUri = imageuri;  this.userGender = gender;
    }



}
