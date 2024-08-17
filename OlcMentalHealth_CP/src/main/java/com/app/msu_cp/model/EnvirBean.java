package com.app.msu_cp.model;

/**
 * Created by Engr G M on 9/4/2017.
 */

public class EnvirBean {

    public String id;
    public String patient_id;
    public String doctor_id;
    public String data;
    public String dateof;
    public String status;
    public String is_deleted;
    public String delete_date;
    public String is_lock;

    public String doctor_name;
    public String doctor_category;


    public EnvirBean(String id, String patient_id, String doctor_id, String data, String dateof, String status, String is_deleted, String delete_date, String is_lock, String doctor_name, String doctor_category) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.data = data;
        this.dateof = dateof;
        this.status = status;
        this.is_deleted = is_deleted;
        this.delete_date = delete_date;
        this.is_lock = is_lock;
        this.doctor_name = doctor_name;
        this.doctor_category = doctor_category;
    }
}
