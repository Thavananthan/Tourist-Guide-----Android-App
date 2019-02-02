package com.example.nanthu.homeui;

public class FindFriend {

    private String ProfileImage,fullname,status;

    public FindFriend(){

    }

    public FindFriend(String profileImage, String fullname, String status) {
        ProfileImage = profileImage;
        this.fullname = fullname;
        this.status = status;
    }

    public String getProfileImage() {
        return ProfileImage;
    }

    public String getFullname() {
        return fullname;
    }

    public String getStatus() {
        return status;
    }

    public void setProfileImage(String profileImage) {
        ProfileImage = profileImage;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
