package com.app.mhcsn_dr.reliance.assessment;

public class PHQlistBean {


    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String form_data;
    public String start_time;
    public String end_time;
    public String score;
    public String severity;

    public String is_lock;

    public String doctor_name;
    public String doctor_category;

    public PHQlistBean(String id, String patient_id, String author_id, String dateof, String form_data, String start_time, String end_time, String score, String severity, String is_lock, String doctor_name, String doctor_category) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.form_data = form_data;
        this.start_time = start_time;
        this.end_time = end_time;
        this.score = score;
        this.severity = severity;
        this.is_lock = is_lock;
        this.doctor_name = doctor_name;
        this.doctor_category = doctor_category;
    }
}
