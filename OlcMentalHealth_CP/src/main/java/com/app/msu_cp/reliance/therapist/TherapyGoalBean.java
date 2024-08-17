package com.app.msu_cp.reliance.therapist;

import androidx.annotation.NonNull;

public class TherapyGoalBean {

    public String id;
    public String goal_name;


    @NonNull
    @Override
    public String toString() {
        return goal_name;
    }
}
