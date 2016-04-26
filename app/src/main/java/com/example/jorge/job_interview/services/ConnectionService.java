package com.example.jorge.job_interview.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.AuthFailureError;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
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

    private boolean readed = false;

    private ArrayList<Runner> gRunnerList;
    private ArrayList<Run> gRunList;
    private ArrayList<Comment> gCommentList;

    private String timelineUrl = "http://wispy-wave-1292.getsandbox.com/timeline";
    private String anyNewRunUrl = "http://wispy-wave-1292.getsandbox.com/timeline/anyNewRun";

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

            ArrayList<Runner> runnerList;
            ArrayList<Run> runList;

            if (response != null) {
                if (response.getString("status").equalsIgnoreCase("ok")) {

                    ParserHelper parserHelper = new ParserHelper(response, getApplicationContext());

                    runnerList = parserHelper.getRunners();
                    runList = parserHelper.getRuns();

                    sendResponse(runnerList, runList);

                    storeToDb(runnerList, runList);
                }
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
        gRunnerList = runnerDao.getRunners();
        gRunList = runDao.getRuns();

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

        db.close();
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
        MySingletonVolley.getInstance(this).addToRequestQueue(request);
    }
}
