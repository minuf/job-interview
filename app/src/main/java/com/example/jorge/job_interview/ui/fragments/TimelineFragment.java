package com.example.jorge.job_interview.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.example.jorge.job_interview.services.ConnectionService;
import com.example.jorge.job_interview.ui.adapters.DefaultEmptyAdapter;
import com.example.jorge.job_interview.ui.adapters.RunsListAdapter;

import java.util.ArrayList;

/**
 * Created by jorge on 27/04/16.
 */
public class TimelineFragment extends Fragment {

    ProgressReceiver rcv;
    RecyclerView rvRunCards;
    RunsListAdapter runsAdapter;
    ArrayList<Runner> runnerList;
    ArrayList<Run> runList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] myDataset = new String[]{"AQUÍ APARECERÁN LOS RUNS TUYOS Y DE TUS AMIGOS,\nDESLIZA HACIA ABAJO PARA VER SI HAY NUEVOS RUNS ;)"};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_timeline, container, false);

        runnerList = new ArrayList<>();
        runList = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ConnectionService.startActionGetRuns(getActivity());
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);

        rvRunCards = (RecyclerView)v.findViewById(R.id.runList);
        rvRunCards.setHasFixedSize(true);
        rvRunCards.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rvRunCards.setAdapter(new DefaultEmptyAdapter(myDataset));
        rvRunCards.setItemAnimator(new DefaultItemAnimator());
/*
        Intent msgIntent = new Intent(MainActivity.this, ConnectionService.class);
        msgIntent.setAction(ConnectionService.ACTION_START);
        startService(msgIntent);
*/
        //call service to start from himself, this is an IntentService that's stop automatic from himself too.
        ConnectionService.startActionGetRuns(getActivity());

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectionService.ACTION_GET_RUNS);
        filter.addAction(ConnectionService.ACTION_ANY_NEW);
        filter.addAction(ConnectionService.ACTION_START);
        rcv = new ProgressReceiver();
        getActivity().registerReceiver(rcv, filter);

        return v;
    }

    class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mSwipeRefreshLayout.setRefreshing(false);
            if(intent.getAction().equals(ConnectionService.ACTION_ANY_NEW)) {
                ArrayList<Runner> lRunnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
                ArrayList<Run> lRunList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

                if (lRunList == null || lRunnerList == null) {
                    //mSwipeRefreshLayout.setRefreshing(false);
                    Snackbar.make(rvRunCards, "Lo sentimos, no se puede conectar con el servidor. Intentelo en unos segundos...", Snackbar.LENGTH_LONG).show();
                    return;
                }
                //if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

                for (int i=0; i<lRunList.size(); i++) {
                    runList.add(0, lRunList.get(i));
                    runnerList.add(0, lRunnerList.get(i));
                }
                if (runsAdapter == null) {
                    runsAdapter = new RunsListAdapter(runnerList, runList);
                    rvRunCards.setAdapter(runsAdapter);
                }else {
                    runsAdapter.notifyDataSetChanged();
                }

                //runsAdapter.notifyDataSetChanged();
            }
            else if(intent.getAction().equals(ConnectionService.ACTION_GET_RUNS)) {
                runnerList = (ArrayList<Runner>) intent.getSerializableExtra("RUNNERS");
                runList = (ArrayList<Run>) intent.getSerializableExtra("RUNS");

                if (runnerList != null && runList != null) {
                    runsAdapter = new RunsListAdapter(runnerList, runList);
                    rvRunCards.setAdapter(runsAdapter);

                    //System.out.println("NOMBRE::::::" + runList.get(0).getCommentsList().get(0).getRunnerName());
                }

            }
        }
    }

    @Override
    public void onStop() {
        getActivity().unregisterReceiver(rcv);
        super.onStop();
    }
}
