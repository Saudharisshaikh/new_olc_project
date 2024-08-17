package com.app.emcurauc.model;

import androidx.annotation.NonNull;

public class StatesBeans {
    public StatesBeans() {
    }

    public String id;
    public String full_name;
    public String short_name;

    public StatesBeans(String id, String full_name, String short_name) {
        this.id = id;
        this.full_name = full_name;
        this.short_name = short_name;
    }

    @NonNull
    @Override
    public String toString() {
        return full_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getShort_name() {
        return short_name;
    }

    public void setShort_name(String short_name) {
        this.short_name = short_name;
    }
}
