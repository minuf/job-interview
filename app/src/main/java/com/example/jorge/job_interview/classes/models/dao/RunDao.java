package com.example.jorge.job_interview.classes.models.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;

import com.example.jorge.job_interview.classes.models.vo.Comment;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 26/04/16.
 */
public class RunDao {

    SQLiteDatabase db;
    private Geocoder geocoder;
    LatLng location;

    public RunDao(SQLiteDatabase db, Context context) {
        this.db = db;
        geocoder = new Geocoder(context);
    }

    public ArrayList<Run> getRuns() {
        ArrayList<Comment> gCommentList = new ArrayList<>();
        ArrayList<Run> gRunList = new ArrayList<>();
        String[] runFields = {"run_id", "dateTime", "distance", "pace_hour", "pace_minute", "pace_seconds",
                "duration", "country", "state", "city", "runner_photo_thumb", "likes", "user_id"};
        Cursor c = db.query("runs", runFields, null, null, null, null, null);

        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                String run_id= c.getString(0);
                String dateTime = c.getString(1);
                Double distance = c.getDouble(2);
                int pace_hour = c.getInt(3);
                int pace_minute = c.getInt(4);
                int pace_seconds = c.getInt(5);
                String duration = c.getString(6);
                String country = c.getString(7);
                String state = c.getString(8);
                String city = c.getString(9);
                String runnerImg = c.getString(10);
                int likes = c.getInt(11);
                String user_id = c.getString(12);

                CommentDao commentDao = new CommentDao();

                gCommentList = commentDao.getComments(db, run_id);

                List<Address> lList = null;
                try {
                    lList = geocoder.getFromLocationName(city+" "+country+", "+state, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Double lat = null, lon = null;
                if (lList != null) {
                    location = new LatLng(lList.get(0).getLatitude(), lList.get(0).getLongitude());
                    lat = location.latitude;
                    lon = location.longitude;
                } else {
                    location = null;
                }
                Run nRun = new Run(run_id, dateTime, distance, pace_hour, pace_minute, pace_seconds, duration, country, state, city, runnerImg, likes, gCommentList, user_id);
                nRun.setLat(lat);
                nRun.setLon(lon);
                gRunList.add(nRun);
                //nRun = null;
                //gCommentList.clear(); // reset commentList for next run

            } while(c.moveToNext());
            System.out.println("runs count: " + gRunList.size());
            return gRunList;
        }

        return null;
    }

    public void saveRuns(ArrayList<Run> runList) {
        ArrayList<Comment> commentList;
        CommentDao commentDao = new CommentDao();
        for (Run run: runList) {
            //Insertamos los datos en la tabla runners
            db.execSQL("INSERT OR IGNORE INTO runs (run_id, dateTime, distance, pace_hour, pace_minute, pace_seconds,"+
                    " duration, country, state, city, runner_photo_thumb, likes, user_id) " +
                    "VALUES ('" + run.getRunId() + "', '" + run.getDateTime() +"', "  + run.getDistance() + ", " + run.getPaceH() + ", " + run.getPaceM()
                    + ", " + run.getPaceS() + ", '" + run.getDuration()+ "', '" +run.getCountry()+ "', '" +run.getState()
                    + "', '" +run.getCity()+ "', '" +run.getRunnerImage()+ "', " +run.getLikes()+ ", '" +run.getUser_id()
                    +"')");
            System.out.println("run inserted");

            commentList = run.getCommentsList();
            commentDao.saveComments(commentList, db);
        }
    }
}
