package com.app.mhcsn_cp.model;

/**
 * Created by Engr G M on 4/11/2017.
 */

public class FollowupBean {

    public String first_name;
    public String last_name;
    public String image;
    public String patient_category;
    public String patient_id;
    public String doctor_nurses_id;
    public String followup_id;
    public String dateof;
    public String is_online;

    public FollowupBean(String first_name, String last_name, String image, String patient_category,
                        String patient_id, String doctor_nurses_id, String followup_id,String dateof,String is_online) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.patient_category = patient_category;
        this.patient_id = patient_id;
        this.doctor_nurses_id = doctor_nurses_id;
        this.followup_id = followup_id;
        this.dateof = dateof;
        this.is_online = is_online;
    }
}
