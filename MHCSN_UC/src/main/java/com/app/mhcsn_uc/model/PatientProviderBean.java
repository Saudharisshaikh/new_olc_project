package com.app.mhcsn_uc.model;

/**
 * Created by Engr G M on 10/11/2017.
 */

public class PatientProviderBean {

    public String is_online;
    public String first_name;
    public String last_name;
    public String doctor_category;
    public String image;
    public String current_app;
    public String doctor_id;

    public PatientProviderBean(String is_online, String first_name, String last_name, String doctor_category, String image, String current_app, String doctor_id) {
        this.is_online = is_online;
        this.first_name = first_name;
        this.last_name = last_name;
        this.doctor_category = doctor_category;
        this.image = image;
        this.current_app = current_app;
        this.doctor_id = doctor_id;
    }
}
