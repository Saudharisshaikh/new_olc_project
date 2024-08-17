package com.app.fivestaruc.b_health.assessment.new_assesment;

public class OCDlistBean {


    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String form_data;
    public String start_time;
    public String end_time;
    public String score;
    public String is_lock;
    public String severity;


    public OCDlistBean(String id, String patient_id, String author_id, String dateof, String form_data, String start_time, String end_time,
                       String score, String is_lock, String severity) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.form_data = form_data;
        this.start_time = start_time;
        this.end_time = end_time;
        this.score = score;
        this.is_lock = is_lock;
        this.severity = severity;
    }
}
