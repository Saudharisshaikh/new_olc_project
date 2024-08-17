package com.app.covacard.model;

/**
 * Created by Engr G M on 4/5/2017.
 */

public class PrimaryCareBean {

    public String id;
    public String first_name;
    public String last_name;
    public String designation;
    public String image;
    public String is_online;

    public String email;
    public String mobile;
    public String current_app;
    public String address1;
    public boolean isMyPrimaryCare;


    public PrimaryCareBean(String id, String first_name, String last_name, String designation, String image,
                           String is_online, String email, String mobile, String current_app, String address1,
                           boolean isMyPrimaryCare) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.designation = designation;
        this.image = image;
        this.is_online = is_online;
        this.email = email;
        this.mobile = mobile;
        this.current_app = current_app;
        this.address1 = address1;
        this.isMyPrimaryCare = isMyPrimaryCare;
    }
}
