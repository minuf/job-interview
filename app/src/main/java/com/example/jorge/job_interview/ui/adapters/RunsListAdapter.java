package com.example.jorge.job_interview.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.Run;
import com.example.jorge.job_interview.classes.Runner;
import com.example.jorge.job_interview.ui.views.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jorge on 22/04/16.
 */
public class RunsListAdapter extends RecyclerView.Adapter<RunsListAdapter.DefaultViewHolder> {
    private ArrayList<Runner> runnerList;
    private ArrayList<Run> runList;
    Picasso picasso;


    /**
     * CUSTOM Default Adapter, setted to recyclerview in oncreateview method,
     * for solve issue with recycler view
     * EXCEPTION: 'no adapter attached, skipping layout',
     * that breaks app main thread on some devices
     */
    public RunsListAdapter(ArrayList<Runner> runnerList, ArrayList<Run> runList) {
        this.runnerList = runnerList;
        this.runList = runList;
    }

    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_run, parent, false);
        DefaultViewHolder vh = new DefaultViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.bindItem(runnerList.get(position), runList.get(position));
    }

    @Override
    public int getItemCount() {
        return runList.size();
    }

    public class DefaultViewHolder extends RecyclerView.ViewHolder {

        RoundedImageView ivRunnerImage;
        ImageView ivRunThumb, ivCommentThumb, btnComment, btnLike;
        TextView tvRunnerName, tvRunLocation, tvRunDate, tvRunTime, tvRunDistance, tvRunPace, tvRunDuration, tvRunLikes, tvCommentRunnerName, tvCommentRunnerComment;
        String date, time, pace;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            ivRunnerImage = (RoundedImageView) itemView.findViewById(R.id.iv_user_thumb);
            ivCommentThumb = (ImageView) itemView.findViewById(R.id.iv_comment_runner_thumb);
            tvRunnerName = (TextView) itemView.findViewById(R.id.tv_runner_name);
            tvRunLocation = (TextView) itemView.findViewById(R.id.tv_run_location);
            tvRunDate = (TextView) itemView.findViewById(R.id.tv_run_date);
            tvRunTime = (TextView) itemView.findViewById(R.id.tv_run_time);
            tvRunDistance = (TextView) itemView.findViewById(R.id.tv_run_distance);
            tvRunPace = (TextView) itemView.findViewById(R.id.tv_run_pace);
            tvRunDuration = (TextView) itemView.findViewById(R.id.tv_run_duration);
            tvRunLikes = (TextView) itemView.findViewById(R.id.tv_run_likes);
            tvCommentRunnerName = (TextView) itemView.findViewById(R.id.tv_comment_runner_name);
            tvCommentRunnerComment = (TextView) itemView.findViewById(R.id.tv_comment_runner_comment);
        }
        public void bindItem(Runner runner, Run run){
            date = run.getDateTime().substring(0, run.getDateTime().indexOf(" "));
            time = run.getDateTime().substring(run.getDateTime().indexOf(" "), run.getDateTime().length());
            pace = "";
            if (run.getPaceH() != 0) pace += run.getPaceH()+":";
            pace += run.getPaceM()+"'"+run.getPaceS()+"''";

            tvRunnerName.setText(runner.getRunnerName());
            tvRunLocation.setText(run.getCity());
            tvRunDate.setText(date);
            tvRunTime.setText(time);
            tvRunDistance.setText(run.getDistance()+"");
            tvRunPace.setText(pace);
            tvRunDuration.setText(run.getDuration());
            tvRunLikes.setText(run.getLikes() + "");

            picasso.with(ivRunnerImage.getContext())
                    .load(runner.getImgUrl())
                    //.resize(android.R.attr.actionBarSize, android.R.attr.actionBarSize)
                    //.centerCrop()
                    .into(ivRunnerImage);
            picasso.with(ivCommentThumb.getContext())
                    .load(run.getCommentsList().get(0).getImgUrl())
                    .into(ivCommentThumb);
            tvCommentRunnerName.setText(run.getCommentsList().get(0).getRunnerName());
            tvCommentRunnerComment.setText(run.getCommentsList().get(0).getComment());
            if (run.getRunnerImage() != null && !run.getRunnerImage().isEmpty() && run.getRunnerImage() != "") {
                //set image for run if exist
                ivRunThumb = (ImageView)itemView.findViewById(R.id.iv_run_thumb);
                picasso.with(ivRunThumb.getContext())
                        .load(run.getRunnerImage())
                        .into(ivRunThumb);
            }
        }
    }

    /**
     public class ViewHolder extends RecyclerView.ViewHolder {
     public ViewHolder(View v) {
     super(v);
     }
     }*/
}

