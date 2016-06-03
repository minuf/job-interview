package com.example.jorge.job_interview.classes.models.vo;

import java.util.ArrayList;

/**
 * Created by jorge on 26/04/16.
 */
public class RunatorCard implements Comparable<RunatorCard>{
    String img_url, user_name, user_city, time, date, run_img_url;
    Double lat, lon;
    ArrayList<Comment> comments;

    public RunatorCard(String img_url, String user_name, String user_city, String time, String date, String run_img_url, Double lat, Double lon, ArrayList<Comment> comments) {
        this.img_url = img_url;
        this.user_name = user_name;
        this.user_city = user_city;
        this.time = time;
        this.date = date;
        this.run_img_url = run_img_url;
        this.lat = lat;
        this.lon = lon;
        this.comments = comments;
    }

    public String getImg_url() {
        return img_url;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_city() {
        return user_city;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getRun_img_url() {
        return run_img_url;
    }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public void setUser_city(String user_city) {
        this.user_city = user_city;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRun_img_url(String run_img_url) {
        this.run_img_url = run_img_url;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    //Implementation of compareTo method for easy ordering list later with sort method..
    @Override
    public int compareTo(RunatorCard another) {
        return date.compareTo(another.date);
    }
}
