package com.app.mhcsn_cp.model;

/**
 * Created by Engr G M on 6/2/2017.
 */

public class HospitalBean {

    public String id;
    public String hospital_name;
    public String folder_name;


    public HospitalBean(String id, String hospital_name, String folder_name) {
        this.id = id;
        this.hospital_name = hospital_name;
        this.folder_name = folder_name;
    }

    @Override
    public String toString() {
        return hospital_name;
    }
}
