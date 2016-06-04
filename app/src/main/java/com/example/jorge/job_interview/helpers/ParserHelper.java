package com.example.jorge.job_interview.helpers;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.jorge.job_interview.classes.models.vo.Comment;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jorge on 26/04/16.
 */
public class ParserHelper {
    JSONObject response;
    ArrayList<Runner> runnerList = new ArrayList<>();
    ArrayList<Run> runList = new ArrayList<>();
    ArrayList<Comment> commentList = new ArrayList<>();
    JSONObject run, dateTime, pace, runnatorUser, likes, commentsGroup, lastComment, lastCommentUser;
    JSONArray comments;
    private Geocoder geocoder;
    private LatLng location;


    public ParserHelper(JSONObject response, Context context) throws JSONException {
        this.response = response;
        geocoder = new Geocoder(context);
        parseResponse();
    }

    public ArrayList<Runner> getRunners() {
        return runnerList;
    }

    public ArrayList<Run> getRuns(){
        return runList;
    }

    // THIS METHOD PARSE JSON RESPONSE FROM SERVER TO 2 ARRAY LIST WITH PARSED MODELS
    public void parseResponse() throws JSONException {
        JSONObject data = response.getJSONObject("data");
        JSONArray runsList = data.getJSONArray("cards");
        for (int i = 0; i < runsList.length(); i++) {
            JSONObject card = (JSONObject) runsList.get(i);
            //parse data
            runnerList.add(new Runner(card.getString("user_id"), card.getString("user_photo"), card.getString("user_name")));

            run = card.getJSONObject("run");
            dateTime = run.getJSONObject("created_at");
            pace = card.getJSONObject("pace");
            runnatorUser = run.getJSONObject("runator_user");
            likes = card.getJSONObject("likes");
            commentsGroup = card.getJSONObject("comment_group");
            commentList = new ArrayList<>();
            comments = commentsGroup.getJSONArray("comments");
            if (comments != null) {
                if (comments.length() != 0) {
                    for (int j = 0; j < comments.length(); j++) {
                        //commentList.add(new Comment(comments.getString()))
                    }
                }
            }
            if (!commentsGroup.isNull("last_comment")) {
                lastComment = commentsGroup.getJSONObject("last_comment");
                lastCommentUser = lastComment.getJSONObject("user");
                commentList.add(new Comment(lastComment.getString("id"),
                        lastCommentUser.getString("id"),
                        card.getString("run_id"),
                        lastCommentUser.getString("photo_thumb"),
                        lastCommentUser.getString("name"),
                        lastComment.getString("comment")
                ));
            }

            List<Address> lList = null;
            try {
                lList = geocoder.getFromLocationName(card.getString("city")+" "+card.getString("country")+", "+card.getString("state"), 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Double lat = null, lon = null;
            if (lList != null) {
                location = new LatLng(lList.get(0).getLatitude(), lList.get(0).getLongitude());
                lat = location.latitude;
                lon = location.longitude;
            } else {
                location = new LatLng(0,0);
            }
            Run nRun = new Run(
                    card.getString("run_id"),
                    dateTime.getString("date"),
                    card.getDouble("distance"),
                    pace.getInt("hours"),
                    pace.getInt("minutes"),
                    pace.getInt("seconds"),
                    card.getString("duration"),
                    card.getString("country"),
                    card.getString("state"),
                    card.getString("city"),
                    runnatorUser.getString("photo_thumb"),
                    likes.getInt("count"),
                    commentList,
                    card.getString("user_id"));
            nRun.setLat(lat);
            nRun.setLon(lon);
            nRun.setPolyLineEncoded(card.getString("polyline"));
            runList.add(nRun);

            //System.out.println(card.getString("user_name")) ;
        }

        //for debug
        /*
        for (Runner runner: runnerList) {
            Log.e("PARSEHELPER","ID="+runner.getUserId()+" , NAME= "+runner.getRunnerName());
        }
        */
    }

}


