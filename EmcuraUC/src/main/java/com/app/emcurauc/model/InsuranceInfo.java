package com.app.emcurauc.model;

import java.util.ArrayList;

public class InsuranceInfo {

    public ArrayList<Organization> organization;
    public ArrayList<PatientInfo> patient_info;
    public boolean change_flag;
    public String msg_id;
    public boolean active_coverage;

    public InsuranceInfo() {
    }

    public ArrayList<Organization> getOrganization() {
        return organization;
    }

    public void setOrganization(ArrayList<Organization> organization) {
        this.organization = organization;
    }

    public ArrayList<PatientInfo> getPatient_info() {
        return patient_info;
    }

    public void setPatient_info(ArrayList<PatientInfo> patient_info) {
        this.patient_info = patient_info;
    }

    public boolean isChange_flag() {
        return change_flag;
    }

    public void setChange_flag(boolean change_flag) {
        this.change_flag = change_flag;
    }

    public String getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(String msg_id) {
        this.msg_id = msg_id;
    }

    public boolean isActive_coverage() {
        return active_coverage;
    }

    public void setActive_coverage(boolean active_coverage) {
        this.active_coverage = active_coverage;
    }
}
