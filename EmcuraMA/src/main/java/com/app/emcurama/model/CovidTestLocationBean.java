package com.app.emcurama.model;

import androidx.annotation.NonNull;

public class CovidTestLocationBean {

    public String id;
    public String location_name;


    public CovidTestLocationBean(String id, String location_name) {
        this.id = id;
        this.location_name = location_name;
    }

    @NonNull
    @Override
    public String toString() {
        return location_name;
    }
}
