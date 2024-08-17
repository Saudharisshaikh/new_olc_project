package com.app.emcurauc.model;


import java.util.ArrayList;

public class Organization{
    public String org_name;
    public String alt;
    public String fsl;
    public String cov;
    public String identification_code;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public ArrayList<String> pharmacy;
    public String plans;
    public String member_id;

    public  String firstName;
    public  String lastName;
    public PatientInfo patient_info;

    public Organization() {
    }

    public Organization(String org_name, String alt, String fsl, String cov, String identification_code, ArrayList<String> pharmacy, String plans, String member_id, PatientInfo patient_info) {
        this.org_name = org_name;
        this.alt = alt;
        this.fsl = fsl;
        this.cov = cov;
        this.identification_code = identification_code;
        this.pharmacy = pharmacy;
        this.plans = plans;
        this.member_id = member_id;
        this.patient_info = patient_info;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getFsl() {
        return fsl;
    }

    public void setFsl(String fsl) {
        this.fsl = fsl;
    }

    public String getCov() {
        return cov;
    }

    public void setCov(String cov) {
        this.cov = cov;
    }

    public String getIdentification_code() {
        return identification_code;
    }

    public void setIdentification_code(String identification_code) {
        this.identification_code = identification_code;
    }

    public ArrayList<String> getPharmacy() {
        return pharmacy;
    }

    public void setPharmacy(ArrayList<String> pharmacy) {
        this.pharmacy = pharmacy;
    }

    public String getPlans() {
        return plans;
    }

    public void setPlans(String plans) {
        this.plans = plans;
    }

    public String getMember_id() {
        return member_id;
    }

    public void setMember_id(String member_id) {
        this.member_id = member_id;
    }

    public PatientInfo getPatient_info() {
        return patient_info;
    }

    public void setPatient_info(PatientInfo patient_info) {
        this.patient_info = patient_info;
    }
}
