package com.example.jorge.job_interview.classes.models.vo;

import java.io.Serializable;

/**
 * Created by jorge on 20/04/16.
 */
public class Runner implements Serializable{
    private String imgUrl, runnerName, userId;

    public Runner(String userId, String imgUrl, String runnerName) {
        this.userId = userId;
        this.imgUrl = imgUrl;
        this.runnerName = runnerName;
    }

    public String getUserId() {
        return userId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRunnerName() {
        return runnerName;
    }

    public void setRunnerName(String runnerName) {
        this.runnerName = runnerName;
    }
}
