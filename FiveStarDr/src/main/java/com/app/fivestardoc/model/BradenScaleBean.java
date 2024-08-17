package com.app.fivestardoc.model;

/**
 * Created by Engr G M on 8/29/2017.
 */

public class BradenScaleBean {

    public String id;
    public String patient_id;
    public String doctor_id;
    public String sensory_perception;
    public String moisture;
    public String activity;
    public String mobility;
    public String nutrition;
    public String friction_shear;
    public String dateof;
    public String signature;
    public String score;
    public String status;
    public String is_deleted;
    public String delete_date;


    public BradenScaleBean(String id, String patient_id, String doctor_id, String sensory_perception,
                           String moisture, String activity, String mobility, String nutrition, String friction_shear,
                           String dateof, String signature, String score, String status, String is_deleted, String delete_date) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.sensory_perception = sensory_perception;
        this.moisture = moisture;
        this.activity = activity;
        this.mobility = mobility;
        this.nutrition = nutrition;
        this.friction_shear = friction_shear;
        this.dateof = dateof;
        this.signature = signature;
        this.score = score;
        this.status = status;
        this.is_deleted = is_deleted;
        this.delete_date = delete_date;
    }
}
