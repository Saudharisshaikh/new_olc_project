package com.app.mdlive_cp.reliance.counter;

public class HospAdmListBean {

    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String admit;
    public String discharge;
    public String diagnosis;
    public String description;

    public String hospital;
    public String discharge_summary;


    public HospAdmListBean(String id, String patient_id, String author_id, String dateof, String admit, String discharge, String diagnosis, String description, String hospital ,String discharge_summary) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.admit = admit;
        this.discharge = discharge;
        this.diagnosis = diagnosis;
        this.description = description;
        this.hospital = hospital;
        this.discharge_summary = discharge_summary;
    }
}
