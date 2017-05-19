package com.app.todo.model;

/**
 * Created by bridgeit on 19/5/17.
 */

public class ProfileImage {


    String profilepicture;

    public  ProfileImage(){

    }
    public ProfileImage(String profilepicture) {
        this.profilepicture = profilepicture;
    }

    public String getProfilepicture() {
        return profilepicture;
    }

    public void setProfilepicture(String profilepicture) {
        this.profilepicture = profilepicture;
    }
}
