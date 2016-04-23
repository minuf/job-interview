package com.example.jorge.job_interview.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jorge.job_interview.classes.Comment;
import com.example.jorge.job_interview.classes.MySingletonVolley;
import com.example.jorge.job_interview.classes.Run;
import com.example.jorge.job_interview.classes.Runner;
import com.example.jorge.job_interview.helpers.MSQLiteHelper;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ConnectionService extends IntentService{
    private String ACTION= "";
    public static final String ACTION_GET_RUNS =
            "com.example.jorge.intent.action.GET_RUNS";
    public static final String ACTION_ANY_NEW =
            "com.example.jorge.intent.action.ANY_NEW";
    public static final String ACTION_START =
            "com.example.jorge.intent.action.END";

    private static boolean readed = false;

    private ArrayList<Runner> gRunnerList;
    private ArrayList<Run> gRunList;
    private ArrayList<Comment> gCommentList;

    private String timelineUrl = "http://wispy-wave-1292.getsandbox.com/timeline";
    private String anyNewRunUrl = "http://wispy-wave-1292.getsandbox.com/timeline/anyNewRun";
    private Geocoder geocoder;
    private LatLng location, location2;

    public ConnectionService() {
        super("ConnectionService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetRuns(Context context) {
        Intent intent = new Intent(context, ConnectionService.class);
        intent.setAction(ConnectionService.ACTION_START);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            geocoder = new Geocoder(getApplicationContext());
            //generateRequest();

            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                if (readFromDb()) {
                    ACTION = ACTION_ANY_NEW;
                    if (!readed) {
                        sendResponse(gRunnerList, gRunList);
                        System.out.println("READED FROM DATABASE AND LATER FROM SERVER");
                        requestWithSomeHttpHeaders(anyNewRunUrl);
                        readed = true;
                    }else {
                        System.out.println("READED FROM FROM SERVER NEW RUNS");
                        requestWithSomeHttpHeaders(anyNewRunUrl);
                    }
                } else {
                    ACTION = ACTION_GET_RUNS;
                    requestWithSomeHttpHeaders(timelineUrl);
                    System.out.println("READED FROM SERVER");
                }
            }
        }
    }

    private void processResponse(JSONObject response) throws JSONException {

            ArrayList<Runner> runnerList = new ArrayList<>();
            ArrayList<Run> runList = new ArrayList<>();
            ArrayList<Comment> commentList;

            JSONObject run, dateTime, pace, runnatorUser, likes, commentsGroup, lastComment, lastCommentUser;
            JSONArray comments;

            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("ok")) {
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
                            location = null;
                        }
                        runList.add(new Run(
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
                                card.getString("user_id"),
                                lat,
                                lon
                        ));

                        //System.out.println(card.getString("user_name")) ;
                    }/*
                for (int i=0; i<runsList.length(); i++) {
                    System.out.println(runnerList.get(i).getImgUrl()+"\n"+runnerList.get(i).getRunnerName());
                }*/
                    sendResponse(runnerList, runList);
                    storeToDb(runnerList, runList);
                    //readFromDb();

                }
            }
    }

    public boolean readFromDb() {
        gRunnerList = new ArrayList<>();
        gRunList = new ArrayList<>();
        gCommentList = new ArrayList<>();

        MSQLiteHelper mDbHelper = new MSQLiteHelper(this, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //prepare statement for runners table
        String[] runnerFields = {"user_id", "user_photo", "user_name"};
        Cursor c = db.query("runners", runnerFields, null, null, null, null, null);

//Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                String user_id= c.getString(0);
                String user_name = c.getString(1);
                String user_photo = c.getString(2);
                gRunnerList.add(new Runner(user_id, user_name, user_photo));
                System.out.println("Selected. ID=" + user_id + ", userName=" + user_name + ", userPhoto=" + user_photo);
            } while(c.moveToNext());
        }

        String[] runFields = {"run_id", "dateTime", "distance", "pace_hour", "pace_minute", "pace_seconds",
                "duration", "country", "state", "city", "runner_photo_thumb", "likes", "user_id"};
        c = db.query("runs", runFields, null, null, null, null, null);

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

                String[] args = {run_id};
                String[] commentFields = {"comment_id", "user_id", "run_id", "user_photo", "user_name", "comment_text"};
                Cursor cc = db.query("comments", commentFields, "run_id=?", args, null, null, null);
                gCommentList = new ArrayList<>();
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
                }
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
                gRunList.add(new Run(run_id, dateTime, distance, pace_hour, pace_minute, pace_seconds, duration, country, state, city, runnerImg, likes, gCommentList, user_id, lat, lon));
                System.out.println("com count: " + gCommentList.size());
                //gCommentList.clear(); // reset commentList for next run


            } while(c.moveToNext());
        }
        db.close();
        if (gRunnerList != null && !gRunnerList.isEmpty() && gRunList != null && !gRunList.isEmpty()) return true;
        return false;
    }

    public void storeToDb(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        MSQLiteHelper mDbHelper = new MSQLiteHelper(this, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        ArrayList<Comment> commentList;
        //Si hemos abierto correctamente la base de datos
        if(db != null){
            for (Runner runner: runnerList) {
                //Insertamos los datos en la tabla runners
                db.execSQL("INSERT OR IGNORE INTO runners (user_id, user_name, user_photo) " +
                        "VALUES ('" + runner.getUserId() + "', '" + runner.getRunnerName() + "', '" + runner.getImgUrl() +"')");
                System.out.println("runner inserted");
            }

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
                if (commentList != null && !commentList.isEmpty()) {
                    for (Comment comment: commentList) {
                        db.execSQL("INSERT OR IGNORE INTO comments (comment_id, user_id, run_id, user_photo, user_name, comment_text)"+
                        "VALUES ('" + comment.getComId() + "', '" +comment.getUserId()+"', '"+comment.getRunId()+"', '"+comment.getImgUrl()+"', '"+comment.getRunnerName()+"', '"+comment.getComment()+"')"
                        );
                        System.out.println("comment inserted");
                    }
                }
            }

            //Cerramos la base de datos
            db.close();
        }
    }

    public void sendResponse(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION);
        bcIntent.putExtra("RUNNERS", runnerList);
        bcIntent.putExtra("RUNS", runList);
        sendBroadcast(bcIntent);
    }

    public void requestWithSomeHttpHeaders(String url) {
        //GENERATE SIMPLE JSON REQUEST

        //String url = "http://wispy-wave-1292.getsandbox.com/timeline";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        try {
                            String newStr = URLDecoder.decode(URLEncoder.encode(response.toString(), "iso8859-1"), "UTF-8");
                            JSONObject res = new JSONObject(newStr);
                            processResponse(res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("ERROR","error => "+error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Auth", "Bearer 63bea7d5e84b6c45a4af9f9d3db714a8");
                params.put("content-type","text/html; charset=utf-8");

                return params;
            }
        };
        MySingletonVolley.getInstance(this).addToRequestQueue(request);
    }
}
