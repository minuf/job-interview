package com.example.jorge.job_interview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jorge.job_interview.classes.controllers.TimelineController;
import com.example.jorge.job_interview.ui.fragments.LoadFragment;
import com.example.jorge.job_interview.ui.fragments.TimelineFragment;

public class MainActivity extends AppCompatActivity{
    TimelineFragment timelineFragment = new TimelineFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, new LoadFragment()).commit();
        TimelineController timelineController = new TimelineController(this);
        timelineController.setFragmentView(timelineFragment);
        timelineFragment.setController(timelineController);
        timelineController.init();
    }

}
