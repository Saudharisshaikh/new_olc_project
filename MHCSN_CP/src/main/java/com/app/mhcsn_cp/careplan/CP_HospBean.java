package com.app.mhcsn_cp.careplan;

/**
 * Created by Engr G M on 7/18/2018.
 */

public class CP_HospBean {

    public String id;
    public String added_by;
    public String patient_id;
    public String careplan_id;
    public String description;
    public String date_admitted;
    public String comments;
    public String created_at;
    public String save_from;

    public CP_HospBean(String id, String added_by, String patient_id, String careplan_id, String description, String date_admitted, String comments, String created_at, String save_from) {
        this.id = id;
        this.added_by = added_by;
        this.patient_id = patient_id;
        this.careplan_id = careplan_id;
        this.description = description;
        this.date_admitted = date_admitted;
        this.comments = comments;
        this.created_at = created_at;
        this.save_from = save_from;
    }
}
