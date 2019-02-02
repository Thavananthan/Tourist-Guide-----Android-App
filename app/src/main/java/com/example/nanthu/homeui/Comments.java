package com.example.nanthu.homeui;

public class Comments {
    public String username,uid,time,date,comment;

    public Comments(){

    }

    public Comments(String username, String uid, String time, String date, String comment) {
        this.username = username;
        this.uid = uid;
        this.time = time;
        this.date = date;
        this.comment = comment;
    }

    public String getUsername() {
        return username;
    }

    public String getUid() {
        return uid;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getComment() {
        return comment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
