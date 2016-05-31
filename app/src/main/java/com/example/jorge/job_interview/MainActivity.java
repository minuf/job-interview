package com.example.jorge.job_interview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.jorge.job_interview.ui.fragments.TimelineFragment;

public class MainActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, new TimelineFragment()).commit();

    }

}
