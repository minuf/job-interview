package com.example.jorge.job_interview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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
        getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, new LoadFragment()).commit();
        if (true) {
            timelineController = new TimelineController(this);
            timelineController.setFragmentView(timelineFragment);
            timelineFragment.setController(timelineController);
            timelineController.init();
            timelineController.registerReceiver(this);
        } else {
            Toast.makeText(this, "Parece que no tienes conexión a internet, por favor asegurate de estar conectado.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {

        super.onResume();
    }

    @Override
    public void onBackPressed() {
        //moveTaskToBack(true);
        timelineController.mStopService();
        finish();
    }

    public boolean checkDeviceConnection() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null)
            return false;
        if (!i.isConnected())
            return false;
        if (!i.isAvailable())
            return false;
        return true;
    }


    /**
     * Unregister Receiver from the container activity
     * when the activity is killed
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
