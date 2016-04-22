package com.example.jorge.job_interview.classes;

import java.io.Serializable;

/**
 * Created by jorge on 20/04/16.
 */
public class Runner implements Serializable{
    private String imgUrl, runnerName;

    public Runner(String imgUrl, String runnerName) {
        this.imgUrl = imgUrl;
        this.runnerName = runnerName;
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
