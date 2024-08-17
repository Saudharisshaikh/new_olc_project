package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class CP_GoalBean {

    public String id;
    public String goal;
    public String goal_priority;
    public String date_goal_set;
    public String caregiver_name;
    public String is_accumplish;

    public CP_GoalBean(String id, String goal, String goal_priority, String date_goal_set, String caregiver_name, String is_accumplish) {
        this.id = id;
        this.goal = goal;
        this.goal_priority = goal_priority;
        this.date_goal_set = date_goal_set;
        this.caregiver_name = caregiver_name;
        this.is_accumplish = is_accumplish;
    }
}
