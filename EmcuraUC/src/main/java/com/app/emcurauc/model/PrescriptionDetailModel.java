package com.app.emcurauc.model;

public class PrescriptionDetailModel {


    public String id;
    public String status;
    public String request_type;
    public String request_message;
    public String first_name;
    public String last_name;
    public String image;
    public String signature;
    public String dateof;
    public String notes;
    public String patient_id;
    public String doctor_id;
    public String vitals;
    public String diagnosis;
    public String treatment;
    public String prescribed_by;
    public String ppstatus;

    String send_education;

    public String getSend_education() {
        return send_education;
    }

    public void setSend_education(String send_education) {
        this.send_education = send_education;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_message() {
        return request_message;
    }

    public void setRequest_message(String request_message) {
        this.request_message = request_message;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getDateof() {
        return dateof;
    }

    public void setDateof(String dateof) {
        this.dateof = dateof;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getDoctor_id() {
        return doctor_id;
    }

    public void setDoctor_id(String doctor_id) {
        this.doctor_id = doctor_id;
    }

    public String getVitals() {
        return vitals;
    }

    public void setVitals(String vitals) {
        this.vitals = vitals;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getPrescribed_by() {
        return prescribed_by;
    }

    public void setPrescribed_by(String prescribed_by) {
        this.prescribed_by = prescribed_by;
    }

    public String getPpstatus() {
        return ppstatus;
    }

    public void setPpstatus(String ppstatus) {
        this.ppstatus = ppstatus;
    }

    public String getDirections() {
        return directions;
    }

    public void setDirections(String directions) {
        this.directions = directions;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getRefill() {
        return refill;
    }

    public void setRefill(String refill) {
        this.refill = refill;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String directions;
    public String quantity;
    public String refill;
    public String drug_name;
}
