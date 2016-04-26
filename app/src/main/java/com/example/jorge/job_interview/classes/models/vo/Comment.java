package com.example.jorge.job_interview.classes.models.vo;

import com.example.jorge.job_interview.classes.models.vo.Runner;

/**
 * Created by jorge on 22/04/16.
 */
public class Comment extends Runner {
    private String comId, userId, runId, comment;
    public Comment(String comId, String userId, String runId, String imgUrl, String runnerName, String comment) {
        super(userId, imgUrl, runnerName);
        this.comId = comId;
        this.userId = userId;
        this.runId = runId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public String getComId() {
        return comId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String getImgUrl() {
        return super.getImgUrl();
    }


    @Override
    public String getRunnerName() {
        return super.getRunnerName();
    }

    public String getRunId() {
        return runId;
    }
}

