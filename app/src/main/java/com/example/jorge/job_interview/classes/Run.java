package com.example.jorge.job_interview.classes;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by jorge on 20/04/16.
 */
public class Run implements Serializable{

    private String dateTime, duration, country, state, city, runnerImage;
    private float distance;
    private int paceH, paceM, paceS, likes;
    private ArrayList<Comment> commentsList;

    public Run(String dateTime, float distance, int paceH, int paceM, int paceS, String duration,
               String country, String state, String city, String runnerImage,
               int likes, ArrayList<Comment> commentsList) {

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
    }

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

    public float getDistance() {
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
}
