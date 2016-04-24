package com.example.jorge.job_interview;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jorge.job_interview.classes.Run;
import com.example.jorge.job_interview.classes.Runner;
import com.example.jorge.job_interview.services.ConnectionService;
import com.example.jorge.job_interview.ui.adapters.DefaultEmptyAdapter;
import com.example.jorge.job_interview.ui.adapters.RunsListAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{
    ProgressReceiver rcv;
    RecyclerView rvRunCards;
    RunsListAdapter runsAdapter;
    ArrayList<Runner> runnerList;
    ArrayList<Run> runList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] myDataset = new String[]{"AQUÍ APARECERÁN LOS RUNS TUYOS Y DE TUS AMIGOS"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        runnerList = new ArrayList<>();
        runList = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectionService.startActionGetRuns(MainActivity.this);
            }
        });

        rvRunCards = (RecyclerView)findViewById(R.id.runList);
        rvRunCards.setHasFixedSize(true);
        rvRunCards.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvRunCards.setAdapter(new DefaultEmptyAdapter(myDataset));
        rvRunCards.setItemAnimator(new DefaultItemAnimator());
/*
        Intent msgIntent = new Intent(MainActivity.this, ConnectionService.class);
        msgIntent.setAction(ConnectionService.ACTION_START);
        startService(msgIntent);
*/
        //call service to start from himself, this is an IntentService that's stop automatic from himself too.
        ConnectionService.startActionGetRuns(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.ACTION_GET_RUNS);
        filter.addAction(ConnectionService.ACTION_ANY_NEW);
        filter.addAction(ConnectionService.ACTION_START);
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
                ArrayList<Runner> lRunnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
                ArrayList<Run> lRunList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

                if (lRunList == null || lRunnerList == null && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(rvRunCards, "Lo sentimos, no se puede conectar con el servidor. Intentelo en unos segundos...", Snackbar.LENGTH_LONG).show();
                    return;
                }
                if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

                for (int i=0; i<lRunList.size(); i++) {
                    runList.add(0, lRunList.get(i));
                    runnerList.add(0, lRunnerList.get(i));
                }
                if (runsAdapter == null) {
                    runsAdapter = new RunsListAdapter(runnerList, runList, MainActivity.this);
                }else {
                    runsAdapter.notifyDataSetChanged();
                }
                rvRunCards.setAdapter(runsAdapter);
                //runsAdapter.notifyDataSetChanged();
            }
            else if(intent.getAction().equals(ConnectionService.ACTION_GET_RUNS)) {
                runnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
                runList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

                if (runnerList != null && runList != null) {
                    runsAdapter = new RunsListAdapter(runnerList, runList, MainActivity.this);
                    rvRunCards.setAdapter(runsAdapter);

                    //System.out.println("NOMBRE::::::" + runList.get(0).getCommentsList().get(0).getRunnerName());
                }

            }
        }
    }
}
