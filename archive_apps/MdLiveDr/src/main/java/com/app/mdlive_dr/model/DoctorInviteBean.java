package com.app.mdlive_dr.model;

/**
 * Created by Engr G M on 6/16/2017.
 */

public class DoctorInviteBean {

    public String id;
    public String doctor_from;
    public String doctor_to;
    public String dateof;
    public String status;
    public String first_name;
    public String last_name;
    public String image;

    public DoctorInviteBean(String id, String doctor_from, String doctor_to, String dateof, String status, String first_name, String last_name, String image) {
        this.id = id;
        this.doctor_from = doctor_from;
        this.doctor_to = doctor_to;
        this.dateof = dateof;
        this.status = status;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
    }
}
