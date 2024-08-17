package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/16/2018.
 */

public class CP_AllergyBean {


    public String id;
    public String added_by;
    public String patient_id;
    public String careplan_id;
    public String substance;
    public String date_occurred;
    public String type;
    public String documented_by;
    public String reaction;
    public String created_at;
    public String save_from;

    public CP_AllergyBean(String id, String added_by, String patient_id, String careplan_id, String substance, String date_occurred, String type, String documented_by, String reaction, String created_at, String save_from) {
        this.id = id;
        this.added_by = added_by;
        this.patient_id = patient_id;
        this.careplan_id = careplan_id;
        this.substance = substance;
        this.date_occurred = date_occurred;
        this.type = type;
        this.documented_by = documented_by;
        this.reaction = reaction;
        this.created_at = created_at;
        this.save_from = save_from;
    }
}
