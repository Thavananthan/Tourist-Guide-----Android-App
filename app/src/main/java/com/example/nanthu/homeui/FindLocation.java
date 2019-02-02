package com.example.nanthu.homeui;

public class FindLocation {
    private String description,PostImage;

    public FindLocation(){

    }
    public FindLocation(String description, String postImage) {
        this.description = description;
        PostImage = postImage;
    }

    public String getDescription() {
        return description;
    }

    public String getPostImage() {
        return PostImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPostImage(String postImage) {
        PostImage = postImage;
    }
}
