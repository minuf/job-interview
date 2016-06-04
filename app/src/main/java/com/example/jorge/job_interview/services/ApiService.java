package com.example.jorge.job_interview.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.jorge.job_interview.classes.models.dao.RunDao;
import com.example.jorge.job_interview.classes.models.dao.RunnerDao;
import com.example.jorge.job_interview.classes.models.vo.Comment;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.example.jorge.job_interview.classes.singleton.MySingletonVolley;
import com.example.jorge.job_interview.helpers.MSQLiteHelper;
import com.example.jorge.job_interview.helpers.ParserHelper;
import com.example.jorge.job_interview.interfaces.OnTaskCompletedGeneric;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class
ApiService extends IntentService{

    private String ACTION= "";
    public static final String ACTION_GET_TIMELINE =
            "com.example.jorge.intent.action.GET_TIMELINE";
    public static final String ACTION_ANY_NEW =
            "com.example.jorge.intent.action.ANY_NEW";
    public static final String ACTION_START =
            "com.example.jorge.intent.action.END";
    public static final String ACTION_NULL =
            "com.example.jorge.intent.action.NULL";
    public static final String ACTION_NO_CONNECTION =
            "com.example.jorge.intent.action.NO_CONNECTION";


    private boolean readed = false;

    private ArrayList<Runner> gRunnerList;
    private ArrayList<Run> gRunList;
    private ArrayList<Comment> gCommentList;

    public OnTaskCompletedGeneric listener;



    public ApiService() {
        super("ApiService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionGetRuns(Context context, String action) {
        Intent intent = new Intent(context, ApiService.class);
        intent.setAction(action);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        cancelAllRequests();

        if (intent != null) {

            String timelineUrl = "http://wispy-wave-1292.getsandbox.com/timeline";
            String anyNewRunUrl = "http://wispy-wave-1292.getsandbox.com/timeline/anyNewRun";
            //generateRequest();

            final String action = intent.getAction();

            if (ACTION_GET_TIMELINE.equalsIgnoreCase(action)) {
                ACTION = ACTION_GET_TIMELINE;
                if (readFromDb()) {
                    sendResponse(gRunnerList, gRunList);
                    System.out.println("READED TIMELINE FROM DATABASE");
                } else {
                        requestWithSomeHttpHeaders(timelineUrl);
                    System.out.println("READED TIMELINE FROM SERVER");
                }
            }
            else if (ACTION_ANY_NEW.equalsIgnoreCase(action)) {
                ACTION = ACTION_ANY_NEW;
                    requestWithSomeHttpHeaders(anyNewRunUrl);
                System.out.println("READED ANYNEW FROM SERVER");
            }
        }
    }

    private void processResponse(JSONObject response) throws JSONException {

            ArrayList<Runner> runnerList;
            ArrayList<Run> runList;

            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("ok")) {

                    ParserHelper parserHelper = new ParserHelper(response, getApplicationContext());

                    runnerList = parserHelper.getRunners();
                    runList = parserHelper.getRuns();

                    //Collections.sort(runList);
                    //Collections.reverse(runList);
/*
                    for (Run run: runList) {
                        for (Runner runner: runnerList) {
                            if (runner.getUserId().equalsIgnoreCase(run.getUser_id())) {
                                runnerList.set(0, runner);
                            }
                        }
                    }
*/
                    sendResponse(runnerList, runList);
                    storeToDb(runnerList, runList);
                }
            } else {
                ACTION = ACTION_NULL;
                sendResponse(null, null);
            }
    }

    public boolean readFromDb() {

        gRunnerList = new ArrayList<>();
        gRunList = new ArrayList<>();
        gCommentList = new ArrayList<>();

        MSQLiteHelper mDbHelper = new MSQLiteHelper(this, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        RunnerDao runnerDao = new RunnerDao(db);
        RunDao runDao = new RunDao(db, getApplicationContext());
        //gRunnerList = runnerDao.getRunners();
        gRunList = runDao.getRuns();

        if (gRunList != null) {
            for (Run run : gRunList) {
                gRunnerList.add(runnerDao.getRunner(run.getUser_id()));
            }
        }

        db.close();
        if (gRunnerList != null && !gRunnerList.isEmpty() && gRunList != null && !gRunList.isEmpty()) return true;
        return false;
    }

    public void storeToDb(ArrayList<Runner> runnerList, ArrayList<Run> runList) {



        MSQLiteHelper mDbHelper = new MSQLiteHelper(this, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //save runs
        RunDao runDao = new RunDao(db, getApplicationContext());
        runDao.saveRuns(runList);
        //save runners
        RunnerDao runnerDao = new RunnerDao(db);
        runnerDao.saveRunners(runnerList);

        Log.e("APISERVICE", ".STORE TO DB");

        db.close();
    }

    public void sendResponse(ArrayList<Runner> runnerList, ArrayList<Run> runList) {

        Log.e("APISERVICE", ".SEND RESPONSE");


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
                        ACTION = ACTION_ANY_NEW;
                        sendResponse(null, null);
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                1500,
                3,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setTag("RUN");
        MySingletonVolley.getInstance(this).addToRequestQueue(request);
    }

    public void cancelAllRequests() {
        MySingletonVolley.getInstance(this).getRequestQueue().cancelAll("RUN");
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("APISERVICE", "SERVICE DESTROYED");
    }
}
