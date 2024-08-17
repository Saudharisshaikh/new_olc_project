package com.app.omrandr.model;

/**
 * Created by Engr G M on 2/16/2018.
 */

public class ReferedPatientBean {


    public String id;
    public String dateof;
    public String nurse_id;
    public String doctor_id;
    public String patient_id;
    public String patient_name;
    public String pimage;
    public String doctor_name;
    public String is_online;


    public ReferedPatientBean(String id, String dateof, String nurse_id, String doctor_id, String patient_id, String patient_name,
                              String pimage, String doctor_name, String is_online) {
        this.id = id;
        this.dateof = dateof;
        this.nurse_id = nurse_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.patient_name = patient_name;
        this.pimage = pimage;
        this.doctor_name = doctor_name;
        this.is_online = is_online;
    }
}
