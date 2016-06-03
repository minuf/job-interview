package com.example.jorge.job_interview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.jorge.job_interview.classes.controllers.TimelineController;
import com.example.jorge.job_interview.ui.fragments.LoadFragment;
import com.example.jorge.job_interview.ui.fragments.TimelineFragment;

public class MainActivity extends AppCompatActivity{
    TimelineFragment timelineFragment = new TimelineFragment();
    TimelineController timelineController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, new LoadFragment()).commit();
        timelineController = new TimelineController(this);
        timelineController.setFragmentView(timelineFragment);
        timelineFragment.setController(timelineController);
        timelineController.init();
        timelineController.registerReceiver(this);
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        timelineController.mStopService();
        finish();
    }


    /**
     * Unregister Receiver from the container activity
     * when this fragment is killed
     */

    @Override
    public void onStop() {
        timelineController.mStopService();
        try {
            timelineController.unregisterReceiver(this);
        } catch (Exception e) {
            Log.e("MAINACTIVITY", "ERROR UNREGISTERING RECEIVER");
        }
        super.onStop();
    }

    @Override
    protected void onPause() {
        this.onStop();
        super.onPause();
    }
}
