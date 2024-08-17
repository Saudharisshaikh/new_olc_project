package com.app.OnlineCareUS_MA.model;

/**
 * Created by Engr G M on 8/24/2017.
 */

public class SoapReferralBean {

    public String id;
    public String notes_date;
    public String dme_referral;
    public String skilled_nursing;
    public String dme_status;
    public String skilled_nursing_status;
    public String first_name;
    public String last_name;

    public String homecare_referral;
    public String homecare_status;

    public String patient_firstname;
    public String patient_lastname;
    public String patient_birthdate;
    public String patient_address;
    public String patient_zipcode;
    public String patient_phone;

    public SoapReferralBean(String id, String notes_date, String dme_referral,
                            String skilled_nursing, String dme_status, String skilled_nursing_status,
                            String first_name, String last_name,String homecare_referral,String homecare_status,
                            String patient_firstname,String patient_lastname,String patient_birthdate,String patient_address,
                            String patient_zipcode,String patient_phone) {
        this.id = id;
        this.notes_date = notes_date;
        this.dme_referral = dme_referral;
        this.skilled_nursing = skilled_nursing;
        this.dme_status = dme_status;
        this.skilled_nursing_status = skilled_nursing_status;
        this.first_name = first_name;
        this.last_name = last_name;
        this.homecare_referral = homecare_referral;
        this.homecare_status = homecare_status;

        this.patient_firstname = patient_firstname;
        this.patient_lastname = patient_lastname;
        this.patient_birthdate = patient_birthdate;
        this.patient_address = patient_address;
        this.patient_zipcode = patient_zipcode;
        this.patient_phone = patient_phone;
    }
}
