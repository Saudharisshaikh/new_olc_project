package com.app.fivestaruc.model;

import androidx.annotation.NonNull;

public class CovidTestLocationBean {

    public String id;
    public String location_name;


    @NonNull
    @Override
    public String toString() {
        return location_name;
    }
}
