package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/5/2018.
 */

public class CPListBean {

    public String id;
    public String first_name;
    public String last_name;
    public String doctor_category;
    public String created_at;
    public String method_of_communication;
    public String method_input;
    public String preferences_delivery_services;
    public String completed_planning_activities;
    public String life_planning_discussed;
    public String outcome_of_discussion;
    public String not_discussed_why;
    public String were_educational_materials_sent;
    public String name_of_materials;
    public String discussion_scheduled;


    public CPListBean(String id, String first_name, String last_name, String doctor_category, String created_at, String method_of_communication, String method_input, String preferences_delivery_services, String completed_planning_activities, String life_planning_discussed, String outcome_of_discussion, String not_discussed_why, String were_educational_materials_sent, String name_of_materials, String discussion_scheduled) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.doctor_category = doctor_category;
        this.created_at = created_at;
        this.method_of_communication = method_of_communication;
        this.method_input = method_input;
        this.preferences_delivery_services = preferences_delivery_services;
        this.completed_planning_activities = completed_planning_activities;
        this.life_planning_discussed = life_planning_discussed;
        this.outcome_of_discussion = outcome_of_discussion;
        this.not_discussed_why = not_discussed_why;
        this.were_educational_materials_sent = were_educational_materials_sent;
        this.name_of_materials = name_of_materials;
        this.discussion_scheduled = discussion_scheduled;
    }
}
