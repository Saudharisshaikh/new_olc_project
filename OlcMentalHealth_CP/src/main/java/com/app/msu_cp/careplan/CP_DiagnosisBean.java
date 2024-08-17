package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class CP_DiagnosisBean {

     public String id;
    public String added_by;
    public String patient_id;
    public String careplan_id;
    public String diagnosis;
    public String description;
    public String date_diagnosed;
    public String diagnosed_by;
    public String comments;
    public String created_at;
    public String save_from;


    public CP_DiagnosisBean(String id, String added_by, String patient_id, String careplan_id, String diagnosis, String description, String date_diagnosed, String diagnosed_by, String comments, String created_at, String save_from) {
        this.id = id;
        this.added_by = added_by;
        this.patient_id = patient_id;
        this.careplan_id = careplan_id;
        this.diagnosis = diagnosis;
        this.description = description;
        this.date_diagnosed = date_diagnosed;
        this.diagnosed_by = diagnosed_by;
        this.comments = comments;
        this.created_at = created_at;
        this.save_from = save_from;
    }
}
