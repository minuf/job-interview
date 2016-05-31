package com.example.jorge.job_interview.classes.controllers;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.example.jorge.job_interview.classes.models.dao.RunDao;
import com.example.jorge.job_interview.classes.models.dao.RunnerDao;
import com.example.jorge.job_interview.classes.models.vo.Comment;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.example.jorge.job_interview.helpers.MSQLiteHelper;
import com.example.jorge.job_interview.helpers.ParserHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by jorge on 27/04/16.
 */
public class MainController {
    Context context;

    private ArrayList<Runner> gRunnerList;
    private ArrayList<Run> gRunList;
    private ArrayList<Comment> gCommentList;

    public MainController(Context context) {
        this.context = context;
    }

    public void getAnyNewRun() {

    }

    public void getTimeLine() {

    }

    public void getSavedTimeLine() {
        
    }

    public void processResponse(JSONObject response, String ACTION) throws JSONException {

        ArrayList<Runner> runnerList;
        ArrayList<Run> runList;

        if (response != null) {
            if (response.getString("status").equalsIgnoreCase("ok")) {

                ParserHelper parserHelper = new ParserHelper(response, this.context);

                runnerList = parserHelper.getRunners();
                runList = parserHelper.getRuns();

                sendResponse(runnerList, runList, ACTION);

                storeToDb(runnerList, runList);
            }
        }
    }

    public boolean readFromDb() {
        gRunnerList = new ArrayList<>();
        gRunList = new ArrayList<>();
        gCommentList = new ArrayList<>();

        MSQLiteHelper mDbHelper = new MSQLiteHelper(context, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        RunnerDao runnerDao = new RunnerDao(db);
        RunDao runDao = new RunDao(db, context);
        gRunnerList = runnerDao.getRunners();
        gRunList = runDao.getRuns();

        db.close();
        if (gRunnerList != null && !gRunnerList.isEmpty() && gRunList != null && !gRunList.isEmpty()) return true;
        return false;
    }


    public void storeToDb(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        MSQLiteHelper mDbHelper = new MSQLiteHelper(this.context, "test_db", null, 1);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //save runs
        RunDao runDao = new RunDao(db, this.context);
        runDao.saveRuns(runList);
        //save runners
        RunnerDao runnerDao = new RunnerDao(db);
        runnerDao.saveRunners(runnerList);

        db.close();
    }

    public void sendResponse(ArrayList<Runner> runnerList, ArrayList<Run> runList,final String ACTION) {
        Intent bcIntent = new Intent();
        bcIntent.setAction(ACTION);
        bcIntent.putExtra("RUNNERS", runnerList);
        bcIntent.putExtra("RUNS", runList);
        context.sendBroadcast(bcIntent);
    }
}
