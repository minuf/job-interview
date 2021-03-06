package com.example.jorge.job_interview.ui.adapters;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;

import java.util.ArrayList;

/**
 * Created by jorge on 22/04/16.
 */
public class RunsListAdapter extends RecyclerView.Adapter<CardsViewHolder> {
    private ArrayList<Run> runList;
    private Context context;

    AppCompatActivity activity;


    /**
     * CUSTOM Default Adapter, setted to recyclerview in oncreateview method,
     * for solve issue with recycler view
     * EXCEPTION: 'no adapter attached, skipping layout',
     * that breaks app main thread on some devices
     */
    public RunsListAdapter(ArrayList<Run> runList, Context context) {
        this.runList = runList;
        this.context = context;
    }

    @Override
    public CardsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_run, parent, false);
        CardsViewHolder vh = new CardsViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CardsViewHolder holder, int position) {
        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
        animation.setDuration(150);
        holder.itemView.startAnimation(animation);
        holder.bindItem(runList.get(position));

    }

    @Override
    public int getItemCount() {
        return runList.size();
    }




    /**
     public class ViewHolder extends RecyclerView.ViewHolder {
     public ViewHolder(View v) {
     super(v);
     }
     }*/
}

