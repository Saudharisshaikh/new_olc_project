package com.app.mdlive_uc.model;

/**
 * Created by Engr G M on 6/2/2017.
 */

public class HospitalBean {

    public String id;
    public String hospital_name;
    public String folder_name;

    public String category_id;
    public String hospital_zipcode;


    /*public HospitalBean(String id, String hospital_name, String folder_name) {
        this.id = id;
        this.hospital_name = hospital_name;
        this.folder_name = folder_name;
    }*/

    public HospitalBean(String id, String hospital_name, String folder_name, String category_id, String hospital_zipcode) {
        this.id = id;
        this.hospital_name = hospital_name;
        this.folder_name = folder_name;
        this.category_id = category_id;
        this.hospital_zipcode = hospital_zipcode;
    }

    @Override
    public String toString() {
        return hospital_name;
    }
}
