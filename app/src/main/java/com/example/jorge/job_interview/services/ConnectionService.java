package com.example.jorge.job_interview.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.jorge.job_interview.classes.Comment;
import com.example.jorge.job_interview.classes.MySingletonVolley;
import com.example.jorge.job_interview.classes.Run;
import com.example.jorge.job_interview.classes.Runner;
import com.example.jorge.job_interview.interfaces.OnTaskCompletedGeneric;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    public static final String ACTION_GET_RUNS =
            "com.example.jorge.intent.action.GET_RUNS";
    public static final String ACTION_ANY_NEW =
            "com.example.jorge.intent.action.ANY_NEW";
    public static final String ACTION_END =
            "com.example.jorge.intent.action.END";

    private String timelineUrl = "http://wispy-wave-1292.getsandbox.com/timeline";

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
        //intent.setAction(ACTION_GET_RUNS);
        intent.setAction(ConnectionService.ACTION_END);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            //generateRequest();

            final String action = intent.getAction();
            if (ACTION_ANY_NEW.equals(action)) {

            } else if (ACTION_GET_RUNS.equals(action)) {

            } else if (ACTION_END.equals(action)) {
                requestWithSomeHttpHeaders();
            }
        }
    }

    private void processResponse(JSONObject response) throws JSONException {
        ArrayList<Runner> runnerList = new ArrayList<>();
        ArrayList<Run> runList = new ArrayList<>();
        ArrayList<Comment> commentList = new ArrayList<>();

        JSONObject run, dateTime, pace, runnatorUser, likes, commentsGroup, lastComment, lastCommentUser;
        JSONArray comments;

        if (response != null) {
            if (response.getString("status").equalsIgnoreCase("ok")) {
                JSONObject data = response.getJSONObject("data");
                JSONArray runsList = data.getJSONArray("cards");
                for (int i=0; i<runsList.length(); i++) {
                    JSONObject card = (JSONObject) runsList.get(i);
                    //parse data
                    runnerList.add(new Runner(card.getString("user_photo"), card.getString("user_name")));

                    run = card.getJSONObject("run");
                    dateTime = run.getJSONObject("created_at");
                    pace = card.getJSONObject("pace");
                    runnatorUser = run.getJSONObject("runator_user");
                    likes = card.getJSONObject("likes");
                    commentsGroup = card.getJSONObject("comment_group");

                    comments = commentsGroup.getJSONArray("comments");
                    if (comments != null) {
                        if (comments.length() != 0) {
                            for (int j=0; j<comments.length(); j++) {
                                //commentList.add(new Comment(comments.getString()))
                            }
                        }
                    }
                    if (!commentsGroup.isNull("last_comment")) {
                        lastComment = commentsGroup.getJSONObject("last_comment");
                        lastCommentUser = lastComment.getJSONObject("user");
                        commentList.add(new Comment(lastComment.getString("id"),
                                lastCommentUser.getString("id"),
                                lastCommentUser.getString("photo_thumb"),
                                lastCommentUser.getString("name"),
                                lastComment.getString("comment")
                        ));
                    }



                    runList.add(new Run(
                            dateTime.getString("date"),
                            card.getInt("distance"),
                            pace.getInt("hours"),
                            pace.getInt("minutes"),
                            pace.getInt("seconds"),
                            card.getString("duration"),
                            card.getString("country"),
                            card.getString("state"),
                            card.getString("city"),
                            runnatorUser.getString("photo_thumb"),
                            likes.getInt("count"),
                            commentList
                    ));

                    //System.out.println(card.getString("user_name")) ;
                }/*
                for (int i=0; i<runsList.length(); i++) {
                    System.out.println(runnerList.get(i).getImgUrl()+"\n"+runnerList.get(i).getRunnerName());
                }*/
                sendResponse(runnerList, runList);

            }
        }
    }

    public void sendResponse(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION_END);
        bcIntent.putExtra("RUNNERS", runnerList);
        bcIntent.putExtra("RUNS", runList);
        sendBroadcast(bcIntent);
    }

    public void requestWithSomeHttpHeaders() {

        //GENERATE SIMPLE JSON REQUEST

        String url = "http://wispy-wave-1292.getsandbox.com/timeline";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        try {
                            processResponse(response);
                        } catch (JSONException e) {
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
