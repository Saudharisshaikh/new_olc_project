package com.app.mdlive_cp.reliance.careteam;

public class CareTeamBean {

    public String id;
    public String is_online;
    public String first_name;
    public String last_name;
    public String image;
    public String doctor_category;
    public String current_app;
    public String latitude;
    public String longitude;
    public String patient_category;
    public String is_added;

    public CareTeamBean(String id, String is_online, String first_name, String last_name, String image, String doctor_category, String current_app, String latitude, String longitude, String patient_category, String is_added) {
        this.id = id;
        this.is_online = is_online;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.doctor_category = doctor_category;
        this.current_app = current_app;
        this.latitude = latitude;
        this.longitude = longitude;
        this.patient_category = patient_category;
        this.is_added = is_added;
    }
}
