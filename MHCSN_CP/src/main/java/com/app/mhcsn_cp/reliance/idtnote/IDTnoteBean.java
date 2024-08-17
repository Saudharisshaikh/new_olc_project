package com.app.mhcsn_cp.reliance.idtnote;

public class IDTnoteBean {


    public String id;
    public String list_id;
    public String author_id;
    public String notes;
    public String patient_location;
    public String services;
    public String first_name;
    public String last_name;
    public String pfirst_name;
    public String plast_name;
    public String dateof;
    public String is_lock;
    public String duration;


    public IDTnoteBean(String id, String list_id, String author_id, String notes, String patient_location, String services, String first_name, String last_name, String pfirst_name, String plast_name, String dateof, String is_lock, String duration) {
        this.id = id;
        this.list_id = list_id;
        this.author_id = author_id;
        this.notes = notes;
        this.patient_location = patient_location;
        this.services = services;
        this.first_name = first_name;
        this.last_name = last_name;
        this.pfirst_name = pfirst_name;
        this.plast_name = plast_name;
        this.dateof = dateof;
        this.is_lock = is_lock;
        this.duration = duration;
    }
}
