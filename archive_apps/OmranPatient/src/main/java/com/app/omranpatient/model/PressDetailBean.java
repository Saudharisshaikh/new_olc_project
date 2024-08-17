package com.app.omranpatient.model;

/**
 * Created by Engr G M on 3/23/2017.
 */

public class PressDetailBean {

    public String prescription_id;
    public String id;
    public String drug_name;
    public String refill;

    public PressDetailBean(String prescription_id, String id, String drug_name, String refill) {
        this.prescription_id = prescription_id;
        this.id = id;
        this.drug_name = drug_name;
        this.refill = refill;
    }
}
