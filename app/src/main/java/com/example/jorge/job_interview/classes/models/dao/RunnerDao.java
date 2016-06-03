package com.example.jorge.job_interview.classes.models.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jorge.job_interview.classes.models.vo.Runner;

import java.util.ArrayList;

/**
 * Created by jorge on 26/04/16.
 */
public class RunnerDao {

    SQLiteDatabase db;

    public RunnerDao(SQLiteDatabase db) {
        this.db = db;
    }

    // READ RUNNERS FROM DATABASE TO RETURN ARRAYLIST<RUNNER> OR NULL
    public ArrayList<Runner> getRunners(){
        ArrayList<Runner> gRunnerList = new ArrayList<>();
        //prepare statement for runners table
        String[] runnerFields = {"user_id", "user_photo", "user_name"};
        Cursor c = db.query("runners", runnerFields, null, null, null, null, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                String user_id= c.getString(0);
                String user_photo = c.getString(1);
                String user_name = c.getString(2);
                gRunnerList.add(new Runner(user_id, user_photo, user_name));
                System.out.println("Selected. ID=" + user_id + ", userName=" + user_name + ", userPhoto=" + user_photo);
            } while(c.moveToNext());
            return gRunnerList;
        }
        return null;
    }

    public Runner getRunner(String id) {
        Runner lRunner;
        //prepare statement for runners table
        String[] runnerFields = {"user_id", "user_photo", "user_name"};
        String[] args = {id};
        Cursor c = db.query("runners", runnerFields, "user_id=?", args, null, null, null);

        //Nos aseguramos de que existe al menos un registro
        if (c.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                String user_id= c.getString(0);
                String user_photo = c.getString(1);
                String user_name = c.getString(2);
                lRunner = new Runner(user_id, user_photo, user_name);
                System.out.println("Selected. ID=" + user_id + ", userName=" + user_name + ", userPhoto=" + user_photo);
            } while(c.moveToNext());
            return lRunner;
        }
        return null;
    }

    public void saveRunners(ArrayList<Runner> runnerList) {
        if (db != null) {
            for (Runner runner : runnerList) {
                //Insertamos los datos en la tabla runners
                db.execSQL("INSERT OR IGNORE INTO runners (user_id, user_name, user_photo) " +
                        "VALUES ('" + runner.getUserId() + "', '" + runner.getRunnerName() + "', '" + runner.getImgUrl() + "')");
                System.out.println("runner inserted: ID->"+runner.getUserId()+", NOMBRE->"+runner.getRunnerName());
            }
        } else {
            Log.e("RunnerDao", "DB is null");
        }
    }
}
