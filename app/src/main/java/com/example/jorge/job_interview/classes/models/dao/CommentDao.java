package com.example.jorge.job_interview.classes.models.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.jorge.job_interview.classes.models.vo.Comment;

import java.util.ArrayList;

/**
 * Created by jorge on 26/04/16.
 */
public class CommentDao {

    public CommentDao() {
    }

    public ArrayList<Comment> getComments(SQLiteDatabase db, String run_id) {
        String[] args = {run_id};
        String[] commentFields = {"comment_id", "user_id", "run_id", "user_photo", "user_name", "comment_text"};
        Cursor cc = db.query("comments", commentFields, "run_id=?", args, null, null, null);
        ArrayList<Comment> gCommentList = new ArrayList<>();
        if (cc.moveToFirst()) {
            do {
                String comment_id = cc.getString(0);
                String usr_id = cc.getString(1);
                String rn_id = cc.getString(2);
                String usr_photo = cc.getString(3);
                String usr_name = cc.getString(4);
                String com_text = cc.getString(5);
                gCommentList.add(new Comment(comment_id, usr_id, rn_id, usr_photo, usr_name, com_text));
            }while (cc.moveToNext());

            return gCommentList;
        }
        return null;
    }

    public void saveComments(ArrayList<Comment> commentList, SQLiteDatabase db) {
        if (commentList != null && !commentList.isEmpty()) {
            for (Comment comment: commentList) {
                db.execSQL("INSERT OR IGNORE INTO comments (comment_id, user_id, run_id, user_photo, user_name, comment_text)"+
                                "VALUES ('" + comment.getComId() + "', '" +comment.getUserId()+"', '"+comment.getRunId()+"', '"+comment.getImgUrl()+"', '"+comment.getRunnerName()+"', '"+comment.getComment()+"')"
                );
                System.out.println("comment inserted");
            }
        }
    }
}
