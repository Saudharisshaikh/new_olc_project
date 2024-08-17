package com.app.msu_cp.reliance.counter;

public class FallListBean {

    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String fall_date;
    public String is_injury;
    public String description;
    public String medical_care;
    public String injury_result;

    public FallListBean(String id, String patient_id, String author_id, String dateof, String fall_date, String is_injury, String description, String medical_care, String injury_result) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.fall_date = fall_date;
        this.is_injury = is_injury;
        this.description = description;
        this.medical_care = medical_care;
        this.injury_result = injury_result;
    }
}
