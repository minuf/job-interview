package com.example.jorge.job_interview.ui.fragments;

import android.animation.Animator;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.controllers.TimelineController;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.example.jorge.job_interview.interfaces.OnTaskCompletedGeneric;
import com.example.jorge.job_interview.services.ApiService;
import com.example.jorge.job_interview.ui.adapters.DefaultEmptyAdapter;
import com.example.jorge.job_interview.ui.adapters.RunsListAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Created by jorge on 27/04/16.
 */
public class TimelineFragment extends Fragment implements OnTaskCompletedGeneric{

    private boolean isActionFromSystem = true;
    private boolean isMessageOpen = false;

    boolean clicked = false;

    FrameLayout lay_refreshListMessage;
    Button btn_addToList;
    TimelineController timelineController;
    RecyclerView rvRunCards;
    RunsListAdapter runsAdapter;
    ArrayList<Runner> runnerList;
    ArrayList<Run> runList;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private String[] myDataset = new String[]{"AQUÍ APARECERÁN LOS RUNS TUYOS Y DE TUS AMIGOS,\nDESLIZA HACIA ABAJO PARA VER SI HAY NUEVOS RUNS ;)"};
    View v;

    public TimelineFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // INIT THE VIEWS
        v = inflater.inflate(R.layout.fragment_timeline, container, false);
        initViews(v);

        //Start service
//        timelineController.mStartService();

        return v;
    }

    /**
     * Called for first time at start fragment.
     * @param v - the inflated view to retrieve all fragment views
     */
    private void initViews(View v) {
        runnerList = new ArrayList<>();
        runList = new ArrayList<>();

        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark), getResources().getColor(R.color.colorAccent));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isActionFromSystem = false;
                timelineController.mStartService();
            }
        });
        //mSwipeRefreshLayout.setRefreshing(true);

        rvRunCards = (RecyclerView)v.findViewById(R.id.runList);
        rvRunCards.setHasFixedSize(true);
        rvRunCards.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        rvRunCards.setItemAnimator(new DefaultItemAnimator());

        lay_refreshListMessage = (FrameLayout) v.findViewById(R.id.frame_btnAddToListContainer);

        btn_addToList = (Button) v.findViewById(R.id.btn_addToList);


        Log.e("TimelineFrag", "VIEWS INITIATED");
    }

    private void showNewRunsMessage(final ArrayList<Run> lRunList) {
/*
        String runsMsg = (size>1)?" nuevas carreras!":" nueva carrera!";
        final String msg = String.format("Hay %d %s", size, runsMsg);
*/

        final int size = lRunList.size();

        String newRuns = (size>1)?" nuevas carreras!":" nueva carrera!";

        lay_refreshListMessage.animate()
                .alpha(1.0f)
                .setDuration(400)
                .start();
        isMessageOpen = true;

        btn_addToList.setText("Hay "+size+newRuns);
        if (!clicked) {
            lay_refreshListMessage.animate().alpha(1.0f).setDuration(500).start();
        }
        btn_addToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNewRunsMessage();
                simpleLifo(lRunList);
                runsAdapter.notifyDataSetChanged();
                Log.e("TimelineFrag", "UPDATING LIST FROM MESSAGE CLICK");
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                closeNewRunsMessage();
            }
        }, 5000);

    }

    private void closeNewRunsMessage() {
        if (isMessageOpen) {
            lay_refreshListMessage.animate()
                    .alpha(0.0f)
                    .setDuration(300)
                    .start();
            isMessageOpen = false;
        }

    }


    @Override
    public <T> void onTaskCompleted(T... args) {
        Log.e("TimelineFrag", "RECEIVING PARAMS FROM CONTROLLER TO REFRESH LIST");
        String action = args[0].toString();
        Log.e("TimelineFrag", "ACTION"+action+"\nFROM SYSTEM : "+isActionFromSystem);

        // Init local arrays if not initialized
        if (runList == null || runnerList == null) {
            runList = new ArrayList<>();
            runnerList = new ArrayList<>();
        }
        /**/

        if (mSwipeRefreshLayout.isRefreshing()) mSwipeRefreshLayout.setRefreshing(false);

        if(action.equals(ApiService.ACTION_ANY_NEW)) {

                ArrayList<Runner> lRunnerList = (ArrayList<Runner>) args[1];
                ArrayList<Run> lRunList = (ArrayList<Run>) args[2];
            if (lRunList != null && lRunList != null) {
                /**sort and inflate Runs, setting his Runner**/
                lRunList = sortAndInflateRuns(lRunnerList, lRunList);


                /***/
                if (runsAdapter == null) {
                    runsAdapter = new RunsListAdapter(runList, getContext());
                    rvRunCards.setAdapter(runsAdapter);
                } else {
                    if (lRunList != null && lRunList.size() > 0) {
                        if (isActionFromSystem) {
                            Log.e("TimelineFrag", "SHOWING MESSAGE");
                            showNewRunsMessage(lRunList);
                        } else {
                            simpleLifo(lRunList);
                            runsAdapter.notifyDataSetChanged();
                            isActionFromSystem = true;
                            Log.e("TimelineFrag", "UPDATING LIST");
                        }
                    }
                }
            }
            else {
                if (!isActionFromSystem)
                Snackbar.make(rvRunCards, "Algo ha fallado en la comunicación, por favor revisa tu conexión a internet y vuelve a intentarlo..", Snackbar.LENGTH_LONG).show();
            }

        }
        else if(action.equals(ApiService.ACTION_GET_TIMELINE)) {
            runnerList = (ArrayList<Runner>) args[1];
            runList = (ArrayList<Run>) args[2];

            if (runnerList != null && runList != null) {
                /**sort and inflate Runs, setting his Runner**/
                runList = sortAndInflateRuns(runnerList, runList);

                //set runList to adapter and set adapter to list

                if (runList != null) {
                    runsAdapter = new RunsListAdapter(runList, getContext());
                    rvRunCards.setAdapter(runsAdapter);
                }
                /** INIT SERVICE ANY NEW RUNS **/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        timelineController.mStartService();
                    }
                }, 1000);
            }
            else {
                Snackbar.make(rvRunCards, "Lo sentimos, no se puede conectar con el servidor. Intentelo en unos segundos...", Snackbar.LENGTH_LONG).show();
            }

        } else if (action.equalsIgnoreCase(ApiService.ACTION_NULL)&& runsAdapter == null || ApiService.ACTION_NO_CONNECTION.equalsIgnoreCase(action)) {
            Snackbar.make(rvRunCards, "Lo sentimos, no se puede conectar con el servidor. Intentelo en unos segundos...", Snackbar.LENGTH_LONG).show();
            rvRunCards.setAdapter(new DefaultEmptyAdapter(myDataset));
        }

        if (runsAdapter != null)
        Log.e("TimelineFrag", "THE NEW ARRAY CONTAINS "+runsAdapter.getItemCount()+" ITEMS");

    }


    public void setController(TimelineController controller) {
        this.timelineController = controller;
    }

    public ArrayList<Run> sortAndInflateRuns(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        //parse RunnerList to hashmap
        HashMap<String, Runner> Runnersmap = new HashMap<>();
        for (Runner runner : runnerList) {
            Log.e("TIMELINEFRAG","CONVERTING TO MAP->"+runner.getUserId());
            Runnersmap.put(runner.getUserId(),runner);
        }

        //set Runner to Run for each Run
        for (Run run: runList) {
            Log.e("TIMELINEFRAG", "Setting runner to run ->"+Runnersmap.get(run.getUser_id()));
            run.setRunner(Runnersmap.get(run.getUser_id()));
        }

        //order by date (implemented in RunVo class
        Collections.sort(runList);
        Collections.reverse(runList);

        return runList;
    }

    public void simpleLifo(ArrayList<Run> lRunList) {
        /** simple lifo for list **/
        for (int i=0; i<lRunList.size(); i++) {
            runList.add(0, lRunList.get(i));
            if (runList.size() > 50) {
                runList.remove(runList.size()-1);
            }
        }
    }



}
