package com.example.jorge.job_interview.classes;

/**
 * Created by jorge on 22/04/16.
 */
public class Comment extends Runner {
    private String comId, userId, comment;
    public Comment(String comId, String userId, String imgUrl, String runnerName, String comment) {
        super(imgUrl, runnerName);
        this.comId = comId;
        this.userId = userId;
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }
}
