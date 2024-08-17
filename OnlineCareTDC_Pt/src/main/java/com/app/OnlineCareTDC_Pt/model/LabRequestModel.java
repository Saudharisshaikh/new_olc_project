package com.app.OnlineCareTDC_Pt.model;

public class LabRequestModel {

    String id,title,first_name,last_name,dateof;

    public LabRequestModel() {
    }

    public LabRequestModel(String id, String title, String first_name, String last_name, String dateof) {
        this.id = id;
        this.title = title;
        this.first_name = first_name;
        this.last_name = last_name;
        this.dateof = dateof;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getDateof() {
        return dateof;
    }

    public void setDateof(String dateof) {
        this.dateof = dateof;
    }
}
