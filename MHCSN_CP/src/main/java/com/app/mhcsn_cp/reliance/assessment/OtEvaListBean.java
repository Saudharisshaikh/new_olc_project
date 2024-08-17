package com.app.mhcsn_cp.reliance.assessment;

public class OtEvaListBean {


    public String id;
    public String dateof;
    public String patient_id;
    public String author_id;
    public String form_data;
    public String is_lock;

    public String doctor_name;
    public String doctor_category;


    public OtEvaListBean(String id, String dateof, String patient_id, String author_id, String form_data, String is_lock, String doctor_name, String doctor_category) {
        this.id = id;
        this.dateof = dateof;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.form_data = form_data;
        this.is_lock = is_lock;
        this.doctor_name = doctor_name;
        this.doctor_category = doctor_category;
    }
}
