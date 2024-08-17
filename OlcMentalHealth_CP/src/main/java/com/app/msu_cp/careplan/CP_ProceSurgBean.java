package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/18/2018.
 */

public class CP_ProceSurgBean {


    public String id;
    public String added_by;
    public String careplan_id;
    public String hospital;
    public String date;
    public String procedures_surgeries;
    public String created_at;


    public CP_ProceSurgBean(String id, String added_by, String careplan_id, String hospital, String date, String procedures_surgeries, String created_at) {
        this.id = id;
        this.added_by = added_by;
        this.careplan_id = careplan_id;
        this.hospital = hospital;
        this.date = date;
        this.procedures_surgeries = procedures_surgeries;
        this.created_at = created_at;
    }
}
