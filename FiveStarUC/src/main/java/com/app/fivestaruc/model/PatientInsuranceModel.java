package com.app.fivestaruc.model;

public class PatientInsuranceModel {
    String id;
    String patient_id;
    String insurance;
    String policy_number;

    String payer_name;
    String insurance_group;

    String getInsurance_code;

    public String getInsurance_group() {
        return insurance_group;
    }

    public void setInsurance_group(String insurance_group) {
        this.insurance_group = insurance_group;
    }

    public String getGetInsurance_code() {
        return getInsurance_code;
    }

    public void setGetInsurance_code(String getInsurance_code) {
        this.getInsurance_code = getInsurance_code;
    }

    boolean isChecked;

    public String getPayer_name() {
        return payer_name;
    }

    public void setPayer_name(String payer_name) {
        this.payer_name = payer_name;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public PatientInsuranceModel() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getPolicy_number() {
        return policy_number;
    }

    public void setPolicy_number(String policy_number) {
        this.policy_number = policy_number;
    }
}
