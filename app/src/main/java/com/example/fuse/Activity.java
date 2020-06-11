package com.example.fuse;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;

public class Activity implements Parcelable {
    private String heading;
    private String userName;
    private String description;
    private String email;
    private String date;
    private String userAge;
    private String imageUri;
    private String userGender;

    protected Activity(Parcel in) {
        heading = in.readString();
        userName = in.readString();
        description = in.readString();
        email = in.readString();
        date = in.readString();
        userAge = in.readString();
        imageUri = in.readString();
        userGender = in.readString();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(heading);
        dest.writeString(userName);
        dest.writeString(description);
        dest.writeString(email);
        dest.writeString(date);
        dest.writeString(userAge);
        dest.writeString(imageUri);
        dest.writeString(userGender);
    }
}
