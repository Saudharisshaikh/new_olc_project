package com.app.priorityone_uc.model;

/**
 * Created by Engr G M on 10/31/2017.
 */

public class MyInsuranceBean {

    public String id;
    public String patient_id;
    public String insurance;
    public String policy_number;
    public String insurance_group;
    public String insurance_code;
    public String payer_name;
    public String copay_uc;


    public MyInsuranceBean(String id, String patient_id, String insurance, String policy_number, String insurance_group, String insurance_code, String payer_name,String copay_uc) {
        this.id = id;
        this.patient_id = patient_id;
        this.insurance = insurance;
        this.policy_number = policy_number;
        this.insurance_group = insurance_group;
        this.insurance_code = insurance_code;
        this.payer_name = payer_name;
        this.copay_uc = copay_uc;
    }
}
