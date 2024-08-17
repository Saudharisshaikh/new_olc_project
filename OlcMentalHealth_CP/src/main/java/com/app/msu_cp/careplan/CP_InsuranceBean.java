package com.app.msu_cp.careplan;

public class CP_InsuranceBean {

    public String id;
    public String patient_id;
    public String insurance;
    public String policy_number;
    public String insurance_group;
    public String insurance_code;
    public String payer_name;


    public CP_InsuranceBean(String id, String patient_id, String insurance, String policy_number, String insurance_group, String insurance_code, String payer_name) {
        this.id = id;
        this.patient_id = patient_id;
        this.insurance = insurance;
        this.policy_number = policy_number;
        this.insurance_group = insurance_group;
        this.insurance_code = insurance_code;
        this.payer_name = payer_name;
    }
}
