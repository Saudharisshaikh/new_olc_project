package com.app.emcurauc.model;

import android.text.TextUtils;

public class ScheduleLocModel {

    public String key;
    public String valueloc;

    public ScheduleLocModel(String key, String valueloc) {
        this.key = key;
        this.valueloc = valueloc;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValueloc() {
        return valueloc;
    }

    public void setValueloc(String valueloc) {
        this.valueloc = valueloc;
    }

    @Override
    public String toString() {
        //return value;
        return valueloc;

    }
}
