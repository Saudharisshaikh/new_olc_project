package com.app.mhcsn_cp.reliance.preschistory;

public class PrescHistoryBean {


    public String drugDescription;
    public String quantity;
    public String days_supply;
    public String note;
    public String last_fill_date;
    public String pharmacy;


    public PrescHistoryBean(String drugDescription, String quantity, String days_supply, String note, String last_fill_date, String pharmacy) {
        this.drugDescription = drugDescription;
        this.quantity = quantity;
        this.days_supply = days_supply;
        this.note = note;
        this.last_fill_date = last_fill_date;
        this.pharmacy = pharmacy;
    }
}
