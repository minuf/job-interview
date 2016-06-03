package com.example.jorge.job_interview.ui.fragments;

import android.animation.Animator;
import android.os.Bundle;
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

/**
 * Created by jorge on 27/04/16.
 */
public class TimelineFragment extends Fragment implements OnTaskCompletedGeneric{

    public static boolean isTymelineSync = false;

    boolean clicked = false;

    FrameLayout lay_refreshListMessage;
    Button btn_addToList;
    TimelineController timelineController;
    Fragment loadFragment;
    TimelineController.ProgressReceiver rcv;
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
                timelineController.mStartService();
            }
        });
        mSwipeRefreshLayout.setRefreshing(true);

        rvRunCards = (RecyclerView)v.findViewById(R.id.runList);
        rvRunCards.setHasFixedSize(true);
        rvRunCards.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        rvRunCards.setItemAnimator(new DefaultItemAnimator());

        lay_refreshListMessage = (FrameLayout) v.findViewById(R.id.frame_btnAddToListContainer);

        btn_addToList = (Button) v.findViewById(R.id.btn_addToList);


        Log.e("TimelineFrag", "VIEWS INITIATED");
    }

    private void showNewRunsMessage(final int size) {
/*
        String runsMsg = (size>1)?" nuevas carreras!":" nueva carrera!";
        final String msg = String.format("Hay %d %s", size, runsMsg);
*/
        clicked = false;
        rvRunCards.animate()
                .translationY(lay_refreshListMessage.getHeight()+10)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        btn_addToList.setText("Hay "+size+" nuevas carreras!");
                        if (!clicked) {
                            lay_refreshListMessage.setVisibility(View.VISIBLE);
                        }
                        btn_addToList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clicked = true;
                                lay_refreshListMessage.setVisibility(View.INVISIBLE);
                                rvRunCards.animate()
                                        .translationY(0)
                                        .setDuration(300)
                                        .start();
                                runsAdapter.notifyDataSetChanged();
                            }
                        });
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

/*
        Snackbar.make(rvRunCards, "HAY "+size+" CARRERAS NUEVAS!", Snackbar.LENGTH_LONG)
                .setAction("CARGAR", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //rvRunCards.setAdapter(runsAdapter);
                        runsAdapter.notifyDataSetChanged();
                    }
                })
                .show();
*/
    }


    @Override
    public <T> void onTaskCompleted(T... args) {
        Log.e("TimelineFrag", "RECEIVING PARAMS FROM CONTROLLER TO REFRESH LIST");
        String action = args[0].toString();
        Log.e("ACTION", action);

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

            for (int i=0; i<lRunList.size(); i++) {
                runList.add(0, lRunList.get(i));
                runnerList.add(0, lRunnerList.get(i));
                if (runList.size() > 50) {
                    runList.remove(runList.size()-1);
                    runnerList.remove(runnerList.size()-1);
                }
            }
            if (runsAdapter == null) {
                runsAdapter = new RunsListAdapter(runnerList, runList);
                rvRunCards.setAdapter(runsAdapter);
            }else {
                if (lRunList != null && lRunList.size() > 0) {
                    showNewRunsMessage(lRunList.size());
                }
            }

            //runsAdapter.notifyDataSetChanged();
        }
        else if(action.equals(ApiService.ACTION_GET_TIMELINE)) {
            runnerList = (ArrayList<Runner>) args[1];
            runList = (ArrayList<Run>) args[2];

            if (runnerList != null && runList != null) {
                runsAdapter = new RunsListAdapter(runnerList, runList);
                rvRunCards.setAdapter(runsAdapter);
            }
        } else if (action.equalsIgnoreCase(ApiService.ACTION_NULL)) {
            Snackbar.make(rvRunCards, "Lo sentimos, no se puede conectar con el servidor. Intentelo en unos segundos...", Snackbar.LENGTH_LONG).show();
            rvRunCards.setAdapter(new DefaultEmptyAdapter(myDataset));
        }

        if (runsAdapter != null)
        Log.e("TimelineFrag", "THE LIST CONTAINS "+runsAdapter.getItemCount()+" ITEMS");
    }


    public void setController(TimelineController controller) {
        this.timelineController = controller;
    }


    /**
     * Unregister Receiver from the container activity
     * when this fragment is killed
    */
    @Override
    public void onStop() {
        timelineController.unregisterReceiver();
        super.onStop();
    }

}
