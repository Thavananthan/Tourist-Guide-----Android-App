package com.example.nanthu.homeui;

public class Upload {

    private String description;
    private String PostImage;
    private String time, date, fullname, porfile ,uid,heading;

    public Upload() {

    }

    public Upload(String uid,String description, String PostImage, String time, String date, String fullname, String porfile,String heading) {

        this.uid=uid;
        this.description = description;
        this.PostImage = PostImage;
        this.time = time;
        this.date = date;
        this.fullname = fullname;
        this.porfile =  porfile;
        this.heading=heading;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getuid(){
        return uid;
    }

    public String getdescription() {
        return description;
    }

    public String getPostImage() {
        return PostImage;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getFullname() {
        return fullname;
    }

    public String getporfile() {
        return porfile;
    }



    public void setdescription(String description) {
        this.description = description;
    }

    public void setPostImage(String PostImage) {
        this.PostImage = PostImage;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFullnmae(String fullname) {
        this.fullname = fullname;
    }

    public void setporfile(String  porfile) {
        this.porfile =  porfile;
    }
    public void setuid(String uid){
        this.uid=uid;

    }
}