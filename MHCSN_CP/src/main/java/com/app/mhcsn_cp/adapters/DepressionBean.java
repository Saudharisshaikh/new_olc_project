package com.app.mhcsn_cp.adapters;

/**
 * Created by Engr G M on 8/29/2017.
 */

public class DepressionBean {

    public String id;
    public String patient_id;
    public String doctor_id;
    public String depression_data;
    public String score;
    public String dateof;


    public DepressionBean(String id, String patient_id, String doctor_id, String depression_data, String score, String dateof) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.depression_data = depression_data;
        this.score = score;
        this.dateof = dateof;
    }
}
