package com.example.jorge.job_interview.classes.controllers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.models.dao.RunDao;
import com.example.jorge.job_interview.classes.models.dao.RunnerDao;
import com.example.jorge.job_interview.classes.models.vo.Comment;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.example.jorge.job_interview.helpers.MSQLiteHelper;
import com.example.jorge.job_interview.helpers.ParserHelper;
import com.example.jorge.job_interview.interfaces.OnTaskCompletedGeneric;
import com.example.jorge.job_interview.services.ApiService;
import com.example.jorge.job_interview.ui.fragments.TimelineFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jorge on 1/06/16.
 */
public class TimelineController {

    private boolean refreshFromSystem = true; //if true, when receive new runs, shows new runs message dialog
    private static boolean isTimelineReaded = false;

    private boolean isFragmentNull = true;

    Context context;
    FragmentActivity activity;

    OnTaskCompletedGeneric presenterListener;

    TimelineFragment timelineFragment;

    ProgressReceiver rcv;

    private ArrayList<Runner> gRunnerList;
    private ArrayList<Run> gRunList;
    private ArrayList<Comment> gCommentList;

    public TimelineController(AppCompatActivity activity) {
        this.activity = activity;
        gRunnerList = new ArrayList<>();
        gRunList = new ArrayList<>();
        gCommentList = new ArrayList<>();
        //Register new receiver for the service goes to be created in next lines..
        registerReceiver();
    }

    public void setFragmentView(TimelineFragment timelineFragment) {
        this.timelineFragment = timelineFragment;
        setListener(this.timelineFragment);
    }

    public void setListener(OnTaskCompletedGeneric presenterListener) {
        this.presenterListener = presenterListener;
    }

    /**
     * call service to start from himself, this is an IntentService that's stop automatic from himself too.
     */
    public void mStartService() {
        refreshFromSystem = true;

        if (!isTimelineReaded) {
            //start timeline service
            ApiService.startActionGetRuns(activity, ApiService.ACTION_GET_TIMELINE);
        } else {
            //start anyNew service
            ApiService.startActionGetRuns(activity, ApiService.ACTION_ANY_NEW);
        }

    }

    public void changeFragment() {
        if (timelineFragment.isAdded()) {
            isFragmentNull = false;
            //refresh list
        } else {
            activity.getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, timelineFragment).commit();
            Log.e("TimelineCont", "ADDED TIMELINE FRAGMENT");
        }
    }

    public void init() {
        mStartService();
        //act.getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new TimelineFragment()).commit();
    }



    /**
     * register receiver to bind it to container activity,
     * with this can refresh the view directly when receive data from service
     */
    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ApiService.ACTION_GET_TIMELINE);
        filter.addAction(ApiService.ACTION_ANY_NEW);
        filter.addAction(ApiService.ACTION_START);
        rcv = new ProgressReceiver(presenterListener); //create receiver
        activity.registerReceiver(rcv, filter); //register receiver
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(rcv);
    }

    private void sendResult(String action, ArrayList<Runner> lRunnerList, ArrayList<Run> lRunList) {
        timelineFragment.onTaskCompleted(action, lRunnerList, lRunList);
    }

    private void sendResultWithHandler(final String action, final ArrayList<Runner> lRunnerList, final ArrayList<Run> lRunList) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                timelineFragment.onTaskCompleted(action, lRunnerList, lRunList);
                Log.e("TimelineCont", "Fragment was null");
            }
        }, 400);
    }

    /**
     * This is the implementation of Broadcast receiver to receive data when service return result
     */
    public class ProgressReceiver extends BroadcastReceiver {

        OnTaskCompletedGeneric presenterListener;

        public ProgressReceiver(OnTaskCompletedGeneric presenterListener) {
            this.presenterListener = presenterListener;
        }

        @Override
        public void onReceive(Context context, Intent intent) {

            changeFragment();

            final String action = intent.getAction();

            final ArrayList<Runner> lRunnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
            final ArrayList<Run> lRunList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

            if (ApiService.ACTION_ANY_NEW.equalsIgnoreCase(action)) {
                sendResult(action, lRunnerList, lRunList); // if fragment is ready send data..
            }

            // IF RESULT RESULT IS TIMELINE CHECKS IF FRAGMENT IS READY AND SEND RESULT
            else if (ApiService.ACTION_GET_TIMELINE.equalsIgnoreCase(action) || ApiService.ACTION_NULL.equalsIgnoreCase(action)) {
                isTimelineReaded = true;
                if (isFragmentNull) {
                    sendResultWithHandler(action, lRunnerList, lRunList); // if fragment is not ready, wait 400ms and send data.. NO ME GUSTA
                } else {
                    sendResult(action, lRunnerList, lRunList); // if fragment is ready send data..
                    Log.e("TimelineCont", "Fragment wasn't null");
                }
            }

        }
    }
}
