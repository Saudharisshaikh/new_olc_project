package com.app.greatriverma.model;

/**
 * Created by Engr G M on 3/24/2017.
 */
public class PatientRefillBean {


    public String id;
    public String prescription_detail_id;
    public String first_name;
    public String last_name;
    public String start_date;
    public String end_date;
    public String drug_name;
    public String patient_id;
    public String StoreName;

    public PatientRefillBean(String id, String prescription_detail_id,
     String first_name, String last_name, String start_date, String end_date,
                             String drug_name, String patient_id,String StoreName) {
        this.id = id;
        this.prescription_detail_id = prescription_detail_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.start_date = start_date;
        this.end_date = end_date;
        this.drug_name = drug_name;
        this.patient_id = patient_id;
        this.StoreName = StoreName;
    }
}
