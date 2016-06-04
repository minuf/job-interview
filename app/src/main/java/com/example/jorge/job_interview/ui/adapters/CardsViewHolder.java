package com.example.jorge.job_interview.ui.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.jorge.job_interview.R;
import com.example.jorge.job_interview.classes.models.vo.Run;
import com.example.jorge.job_interview.classes.models.vo.Runner;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * Created by jorge on 25/04/16.
 */
public class CardsViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

    ImageView ivRunnerImage, ivMap;
    ImageView ivRunThumb, ivCommentThumb, btnComment, btnLike;
    TextView tvRunnerName, tvRunLocation, tvRunDate, tvRunTime, tvRunDistance, tvRunPace, tvRunDuration, tvRunLikes, tvCommentRunnerName, tvCommentRunnerComment;
    String date, time, pace;
    LinearLayout commentsLay;
    FrameLayout mapLay;
    //public MapView mapView;
    Picasso picasso;
    LatLng location = new LatLng(39.4666667, -0.3666667);

    @Override
    public void onMapReady(GoogleMap map) {
        /*
        CameraPosition camPos = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .build();
        CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
        map.moveCamera(camUpd);

        /*GoogleMapOptions options = new GoogleMapOptions()
                .liteMode(true)
                .camera(camPos)
                .compassEnabled(true)
                .zOrderOnTop(true)
                .tiltGesturesEnabled(false)
                .ambientEnabled(false)
                .rotateGesturesEnabled(false)
                .scrollGesturesEnabled(false)
                ;
        */
    }


    public CardsViewHolder(View itemView) {
        super(itemView);
        ivRunnerImage = (ImageView) itemView.findViewById(R.id.iv_user_thumb);
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
        commentsLay = (LinearLayout) itemView.findViewById(R.id.comments_layout);
        ivMap = (ImageView) itemView.findViewById(R.id.iv_map);
        //mapView = (MapView) itemView.findViewById(R.id.map_layout);
        //mapView.onCreate(null);
        //mapView.getMapAsync(this);
    }
    public void bindItem(Run run){
        date = run.getDateTime().substring(0, run.getDateTime().indexOf(" "));
        time = run.getDateTime().substring(run.getDateTime().indexOf(" "), run.getDateTime().length());
        pace = "";
        if (run.getPaceH() != 0) pace += run.getPaceH()+":";
        pace += run.getPaceM()+"'"+run.getPaceS()+"''";

        tvRunnerName.setText(run.getRunner().getRunnerName());
        tvRunLocation.setText(run.getCity());
        tvRunDate.setText(date);
        tvRunTime.setText(time);
        tvRunDistance.setText(run.getDistance()+"");
        tvRunPace.setText(pace);
        tvRunDuration.setText(run.getDuration());
        tvRunLikes.setText(run.getLikes() + " ");

        if (run.getRunnerImage() != null && !run.getRunnerImage().isEmpty() && run.getRunnerImage() != "") {
            //set image for run if exist
            ivRunThumb = (ImageView)itemView.findViewById(R.id.iv_run_thumb);
            picasso.with(ivRunThumb.getContext())
                    .load(run.getRunnerImage())
                    .into(ivRunThumb);
        }

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(3)
                .cornerRadiusDp(30)
                .oval(false)
                .build();

        picasso.with(ivRunnerImage.getContext())
                .load(run.getRunner().getImgUrl())
                .fit()
                .transform(transformation)
                        //.resize(android.R.attr.actionBarSize, android.R.attr.actionBarSize)
                        //.centerCrop()
                .into(ivRunnerImage);

        if (run.getCommentsList() != null && !run.getCommentsList().isEmpty()) {
            commentsLay.setVisibility(View.VISIBLE);

            picasso.with(ivCommentThumb.getContext())
                    .load(run.getCommentsList().get(0).getImgUrl())
                    .fit()
                    .transform(transformation)
                    .into(ivCommentThumb);
            tvCommentRunnerName.setText(run.getCommentsList().get(0).getRunnerName());
            tvCommentRunnerComment.setText(run.getCommentsList().get(0).getComment());
        }
        else { commentsLay.setVisibility(View.GONE); }

        Double lat = run.getLat();
        Double lon = run.getLon();

        if (lat != null && lon != null) {
            location = new LatLng(lat, lon);
        }

        String polyline = run.getPolyLineEncoded();
        List<LatLng> runRoutes = PolyUtil.decode(polyline);
        String baseUrl = "http://maps.googleapis.com/maps/api/staticmap?size=400x300&path=color:0x0AD1CA|weight:13";
        String endUrl = "";

        int itemRmv = 1;
        while (runRoutes.size() > 6) {
            int size = runRoutes.size();
            runRoutes.remove(itemRmv);
            if (size>6) {
                runRoutes.remove(size - 1 - itemRmv);
            }
            //itemRmv++;
        }

        for (LatLng latLng: runRoutes) {
            endUrl += "|"+latLng.latitude+","+latLng.longitude;
        }


        //Log.e("cardsviewholder", baseUrl+endUrl);


        picasso.with(ivMap.getContext())
                .load(baseUrl+endUrl)
                .fit()
                .into(ivMap);

        /*
        CameraPosition camPos = new CameraPosition.Builder()
                .target(location)
                .zoom(15)
                .build();
        CameraUpdate camUpd = CameraUpdateFactory.newCameraPosition(camPos);
        //mapView.getMap().moveCamera(camUpd);//getMap deprecated, best implementation in onMapReadyCallback
*/
    }

}

