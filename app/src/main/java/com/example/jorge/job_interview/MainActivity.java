package com.example.jorge.job_interview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.jorge.job_interview.classes.MySingletonVolley;
import com.example.jorge.job_interview.classes.Run;
import com.example.jorge.job_interview.classes.Runner;
import com.example.jorge.job_interview.interfaces.OnTaskCompletedGeneric;
import com.example.jorge.job_interview.services.ConnectionService;
import com.example.jorge.job_interview.ui.adapters.DefaultEmptyAdapter;
import com.example.jorge.job_interview.ui.adapters.RunsListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    ProgressReceiver rcv;
    RecyclerView rvRunCards;
    private String[] myDataset = new String[]{"AQUÍ APARECERÁN LOS RUNS TUYOS Y DE TUS AMIGOS"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvRunCards = (RecyclerView)findViewById(R.id.runList);
        rvRunCards.setHasFixedSize(true);
        rvRunCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRunCards.setAdapter(new DefaultEmptyAdapter(myDataset));
        rvRunCards.setItemAnimator(new DefaultItemAnimator());
/*
        Intent msgIntent = new Intent(MainActivity.this, ConnectionService.class);
        msgIntent.setAction(ConnectionService.ACTION_END);
        startService(msgIntent);
*/
        //call service to start from himself, this is an IntentService that's stop automatic from himself too.
        ConnectionService.startActionGetRuns(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.ACTION_GET_RUNS);
        filter.addAction(ConnectionService.ACTION_ANY_NEW);
        filter.addAction(ConnectionService.ACTION_END);
        rcv = new ProgressReceiver();
        registerReceiver(rcv, filter);
    }

    @Override
    protected void onStop() {
        unregisterReceiver(rcv);
        super.onStop();
    }

    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(ConnectionService.ACTION_ANY_NEW)) {
            }
            else if(intent.getAction().equals(ConnectionService.ACTION_END)) {
                ArrayList<Runner> runnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
                ArrayList<Run> runList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

                if (runnerList != null && runList != null) {
                    RunsListAdapter runsAdapter = new RunsListAdapter(runnerList, runList);
                    rvRunCards.setAdapter(runsAdapter);
                }

            }
        }
    }
}
