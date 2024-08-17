package com.app.Olc_MentalHealth.model;

public class InsuranceModel {

    public String id, payer_name,policy_number;

    public InsuranceModel(String id, String payer_name, String policy_number) {
        this.id = id;
        this.payer_name = payer_name;
        this.policy_number = policy_number;
    }

    public InsuranceModel() {
    }

    @Override
    public String toString() {
        return payer_name +"\n"+ policy_number ;
    }
}
