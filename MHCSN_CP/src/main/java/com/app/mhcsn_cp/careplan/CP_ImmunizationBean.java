package com.app.mhcsn_cp.careplan;

/**
 * Created by Engr G M on 7/18/2018.
 */

public class CP_ImmunizationBean {

    public String id;
    public String added_by;
    public String patient_id;
    public String careplan_id;
    public String vaccine;
    public String sub_vaccine_id;
    public String dose;
    public String date;
    public String created_at;
    public String vaccine_name;
    public String sub_vaccine_name;

    public CP_ImmunizationBean(String id, String added_by, String patient_id, String careplan_id, String vaccine, String sub_vaccine_id, String dose, String date, String created_at, String vaccine_name, String sub_vaccine_name) {
        this.id = id;
        this.added_by = added_by;
        this.patient_id = patient_id;
        this.careplan_id = careplan_id;
        this.vaccine = vaccine;
        this.sub_vaccine_id = sub_vaccine_id;
        this.dose = dose;
        this.date = date;
        this.created_at = created_at;
        this.vaccine_name = vaccine_name;
        this.sub_vaccine_name = sub_vaccine_name;
    }
}
