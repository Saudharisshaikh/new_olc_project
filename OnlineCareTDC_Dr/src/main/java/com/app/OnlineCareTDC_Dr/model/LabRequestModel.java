package com.app.OnlineCareTDC_Dr.model;

public class LabRequestModel {

    String id,first_name,last_name,dateof,result_id,doctor_id,patient_id;

    public LabRequestModel() {
    }

    public LabRequestModel(String id, String first_name, String last_name, String dateof, String result_id, String doctor_id, String patient_id) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.dateof = dateof;
        this.result_id = result_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDateof() {
        return dateof;
    }

    public void setDateof(String dateof) {
        this.dateof = dateof;
    }

    public String getResult_id() {
        return result_id;
    }

    public void setResult_id(String result_id) {
        this.result_id = result_id;
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
}
