package com.app.mhcsn_cp.careplan;

/**
 * Created by Engr G M on 7/15/2018.
 */

public class CP_MedBean {

    public String id;
    public String patient_id;
    public String care_plan_id;
    public String start_date;
    public String prescribed_by;
    public String medication_name;
    public String direction;
    public String use;
    public String otc;
    public String b;
    public String l;
    public String d;
    public String n;
    public String comments;


    public CP_MedBean(String id, String patient_id, String care_plan_id, String start_date, String prescribed_by, String medication_name, String direction, String use, String otc, String b, String l, String d, String n, String comments) {
        this.id = id;
        this.patient_id = patient_id;
        this.care_plan_id = care_plan_id;
        this.start_date = start_date;
        this.prescribed_by = prescribed_by;
        this.medication_name = medication_name;
        this.direction = direction;
        this.use = use;
        this.otc = otc;
        this.b = b;
        this.l = l;
        this.d = d;
        this.n = n;
        this.comments = comments;
    }
}
