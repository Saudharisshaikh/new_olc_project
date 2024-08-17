package com.app.msu_cp.model;

/**
 * Created by Engr G M on 2/14/2017.
 */

public class NurseBean {

    public String id;
    public String first_name;
    public String last_name;
    public String image;
    public String doctor_category;
    public String is_added;
    //public String rec_id;


    public NurseBean(String id, String first_name, String last_name, String image, String doctor_category,String is_added) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.doctor_category = doctor_category;
        this.is_added = is_added;
        //this.rec_id = rec_id;
    }
}
