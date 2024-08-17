package com.app.msu_cp.reliance.assessment;

public class AssessmentsSummaryBean {


    public String id;
    public String patient_id;
    public String form_name;
    public String form_id;
    public String dateof;
    public String url;
    public String doctor_name;


    public AssessmentsSummaryBean(String id, String patient_id, String form_name, String form_id, String dateof, String url, String doctor_name) {
        this.id = id;
        this.patient_id = patient_id;
        this.form_name = form_name;
        this.form_id = form_id;
        this.dateof = dateof;
        this.url = url;
        this.doctor_name = doctor_name;
    }
}
