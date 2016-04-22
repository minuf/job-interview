package com.example.jorge.job_interview.interfaces;

/**
 * Created by jorge on 21/04/16.
 */
public interface OnTaskCompletedGeneric {
    <T> void onTaskCompleted(T... args);
}
