package com.example.nanthu.homeui;

public class Message {
    public String date,from,message,time,type;

    public Message(){

    }

    public Message(String date, String from, String message, String time, String type) {

        this.date = date;
        this.from = from;
        this.message = message;
        this.time = time;
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
