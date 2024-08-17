package com.app.fivestaruc.model;

public class LabReqDetailsModel {

   public String id;
   public String doctor_id ;
   public String patient_id ;
   public String diagnosis_desc ;
   public String email_html ;
   public String dateof ;


    public LabReqDetailsModel() {
    }

    public LabReqDetailsModel(String id, String doctor_id, String patient_id, String diagnosis_desc, String email_html, String dateof) {
        this.id = id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.diagnosis_desc = diagnosis_desc;
        this.email_html = email_html;
        this.dateof = dateof;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDiagnosis_desc() {
        return diagnosis_desc;
    }

    public void setDiagnosis_desc(String diagnosis_desc) {
        this.diagnosis_desc = diagnosis_desc;
    }

    public String getEmail_html() {
        return email_html;
    }

    public void setEmail_html(String email_html) {
        this.email_html = email_html;
    }

    public String getDateof() {
        return dateof;
    }

    public void setDateof(String dateof) {
        this.dateof = dateof;
    }
}
