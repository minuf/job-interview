package com.example.jorge.job_interview.classes.models.vo;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jorge on 20/04/16.
 */
public class Run implements Serializable, Comparable<Run>{

    private String runId, dateTime, duration, country, state, city, runnerImage, user_id;
    private double distance;
    private int paceH, paceM, paceS, likes;
    private ArrayList<Comment> commentsList;
    Double lat, lon;

    public Run(String runId, String dateTime, double distance, int paceH, int paceM, int paceS, String duration,
               String country, String state, String city, String runnerImage,
               int likes, ArrayList<Comment> commentsList, String user_id) {

        this.runId = runId;
        this.dateTime = dateTime;
        this.distance = distance;
        this.paceH = paceH;
        this.paceM = paceM;
        this.paceS = paceS;
        this.duration = duration;
        this.country = country;
        this.state = state;
        this.city = city;
        this.runnerImage = runnerImage;
        this.likes = likes;
        this.commentsList = commentsList;
        this.user_id = user_id;
    }

    public String getRunId() { return runId; }

    public String getDateTime() {
        return dateTime;
    }

    public String getDuration() {
        return duration;
    }

    public String getCountry() {
        return country;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getRunnerImage() {
        return runnerImage;
    }

    public double getDistance() {
        return distance;
    }

    public int getPaceH() {
        return paceH;
    }

    public int getPaceM() {
        return paceM;
    }

    public int getPaceS() {
        return paceS;
    }

    public int getLikes() {
        return likes;
    }

    public ArrayList<Comment> getCommentsList() {
        return commentsList;
    }

    public String getUser_id() { return user_id; }

    public Double getLat() {
        return lat;
    }

    public Double getLon() {
        return lon;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    @Override
    public int compareTo(Run another) {
        return dateTime.compareTo(another.dateTime);
    }
}
