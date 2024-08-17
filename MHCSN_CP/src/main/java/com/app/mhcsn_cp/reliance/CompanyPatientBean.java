package com.app.mhcsn_cp.reliance;

public class CompanyPatientBean {

    public String first_name;
    public String last_name;
    public String image;
    public String phone;
    public String patient_id;
    public String is_active;
    public String list_id;
    public String is_online;

    public CompanyPatientBean(String first_name, String last_name, String image, String phone, String patient_id, String is_active, String list_id, String is_online) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.phone = phone;
        this.patient_id = patient_id;
        this.is_active = is_active;
        this.list_id = list_id;
        this.is_online = is_online;
    }
}
