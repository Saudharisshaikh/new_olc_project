package com.app.mdlive_cp.reliance.idtnote;

public class CareGoalBean {


    public String id;
    public String goal;
    public String goal_priority;
    public String date_goal_set;
    public String caregiver_name;
    public String is_accumplish;
    public String care_team_member_type;

    public CareGoalBean(String id, String goal, String goal_priority, String date_goal_set, String caregiver_name, String is_accumplish, String care_team_member_type) {
        this.id = id;
        this.goal = goal;
        this.goal_priority = goal_priority;
        this.date_goal_set = date_goal_set;
        this.caregiver_name = caregiver_name;
        this.is_accumplish = is_accumplish;
        this.care_team_member_type = care_team_member_type;
    }
}
