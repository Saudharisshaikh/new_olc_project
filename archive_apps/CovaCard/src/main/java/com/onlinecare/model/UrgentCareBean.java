package com.onlinecare.model;

/**
 * Created by Engr G M on 6/20/2017.
 */

public class UrgentCareBean {

    public String id;
    public String center_name;
    public String is_deleted;
    public String latitude;
    public String longitude;
    public String total_doctors;

    public UrgentCareBean(String id, String center_name, String is_deleted,String latitude,String longitude,String total_doctors) {
        this.id = id;
        this.center_name = center_name;
        this.is_deleted = is_deleted;
        this.latitude = latitude;
        this.longitude = longitude;
        this.total_doctors = total_doctors;
    }

    @Override
    public String toString() {
        return center_name;
    }
}
