package com.app.onlinecare.model;

/**
 * Created by Engr G M on 3/3/2017.
 */

public class NurseBean {

    public String id;
    public String my_id;
    public String doctor_id;
    public String patient_id;
    public String patient_category;
    public String first_name;
    public String last_name;
    public String doctor_category;
    public String image;
    public String is_online;

    public NurseBean(String id, String my_id, String doctor_id, String patient_id, String patient_category, String first_name,
                     String last_name, String doctor_category, String image,String is_online) {
        this.id = id;
        this.my_id = my_id;
        this.doctor_id = doctor_id;
        this.patient_id = patient_id;
        this.patient_category = patient_category;
        this.first_name = first_name;
        this.last_name = last_name;
        this.doctor_category = doctor_category;
        this.image = image;
        this.is_online = is_online;
    }
}
