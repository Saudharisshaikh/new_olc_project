package com.app.priorityone_dr.model;

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


    public EnvirBean(String id, String patient_id, String doctor_id, String data, String dateof, String status, String is_deleted, String delete_date) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.data = data;
        this.dateof = dateof;
        this.status = status;
        this.is_deleted = is_deleted;
        this.delete_date = delete_date;
    }
}
