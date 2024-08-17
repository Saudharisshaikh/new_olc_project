package com.onlinecare.b_health.assessment;

public class DastListBean {


    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String form_data;
    public String start_time;
    public String end_time;
    public String score;
    public String is_lock;
    public String doctor_name;
    public String doctor_category;
    public String severity;

    public DastListBean(String id, String patient_id, String author_id, String dateof, String form_data, String start_time, String end_time, String score, String is_lock,
                        String doctor_name, String doctor_category,String severity) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.form_data = form_data;
        this.start_time = start_time;
        this.end_time = end_time;
        this.score = score;
        this.is_lock = is_lock;
        this.doctor_name = doctor_name;
        this.doctor_category = doctor_category;
        this.severity = severity;
    }
}
