package com.app.OnlineCareTDC_Dr.model;

/**
 * Created by Engr G M on 2/14/2017.
 */

public class NurseBean2 {

    public String id;
    public String is_online;
    public String first_name;
    public String last_name;
    public String image;
    public String doctor_category;


    public NurseBean2(String id, String is_online, String first_name, String last_name, String image, String doctor_category) {
        this.id = id;
        this.is_online = is_online;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.doctor_category = doctor_category;
    }
}
