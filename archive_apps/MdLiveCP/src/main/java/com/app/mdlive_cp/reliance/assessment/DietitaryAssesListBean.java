package com.app.mdlive_cp.reliance.assessment;

public class DietitaryAssesListBean {

    public String id;
    public String dateof;
    public String patient_id;
    public String author_id;
    public String form_data;
    public String start_time;
    public String end_time;
    public String is_lock;

    public String doctor_name;
    public String doctor_category;


    public DietitaryAssesListBean(String id, String dateof, String patient_id, String author_id, String form_data, String start_time, String end_time, String is_lock, String doctor_name, String doctor_category) {
        this.id = id;
        this.dateof = dateof;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.form_data = form_data;
        this.start_time = start_time;
        this.end_time = end_time;
        this.is_lock = is_lock;
        this.doctor_name = doctor_name;
        this.doctor_category = doctor_category;
    }
}
