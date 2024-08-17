package com.app.fivestardoc.model;

public class PatientMedicalHistoryBean {

    public String Drug;
    public String PrescriberName;
    public String Quantity;
    public String DaysSupply;

    public PatientMedicalHistoryBean() {
    }

    public PatientMedicalHistoryBean(String drug, String prescriberName, String quantity, String daysSupply, String lastFillDate, String pharmacy) {
        Drug = drug;
        PrescriberName = prescriberName;
        Quantity = quantity;
        DaysSupply = daysSupply;
        LastFillDate = lastFillDate;
        Pharmacy = pharmacy;
    }

    public String LastFillDate;
    public String Pharmacy;

    public String getDrug() {
        return Drug;
    }

    public void setDrug(String drug) {
        Drug = drug;
    }

    public String getPrescriberName() {
        return PrescriberName;
    }

    public void setPrescriberName(String prescriberName) {
        PrescriberName = prescriberName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getDaysSupply() {
        return DaysSupply;
    }

    public void setDaysSupply(String daysSupply) {
        DaysSupply = daysSupply;
    }

    public String getLastFillDate() {
        return LastFillDate;
    }

    public void setLastFillDate(String lastFillDate) {
        LastFillDate = lastFillDate;
    }

    public String getPharmacy() {
        return Pharmacy;
    }

    public void setPharmacy(String pharmacy) {
        Pharmacy = pharmacy;
    }
}
