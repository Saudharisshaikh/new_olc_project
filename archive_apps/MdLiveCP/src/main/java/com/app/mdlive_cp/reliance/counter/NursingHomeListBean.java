package com.app.mdlive_cp.reliance.counter;

public class NursingHomeListBean {


    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String name_of_facility;
    public String admit;
    public String diagnosis;
    public String discharge;
    public String discharge_summary;

    public String date_reported;
    public String admission_summary;


    public NursingHomeListBean(String id, String patient_id, String author_id, String dateof, String name_of_facility, String admit, String diagnosis, String discharge, String discharge_summary, String date_reported, String admission_summary) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.name_of_facility = name_of_facility;
        this.admit = admit;
        this.diagnosis = diagnosis;
        this.discharge = discharge;
        this.discharge_summary = discharge_summary;
        this.date_reported = date_reported;
        this.admission_summary = admission_summary;
    }
}
