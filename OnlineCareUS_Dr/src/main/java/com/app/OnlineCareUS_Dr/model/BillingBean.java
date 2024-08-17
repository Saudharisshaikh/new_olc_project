package com.app.OnlineCareUS_Dr.model;

/**
 * Created by Engr G M on 2/16/2017.
 */

public class BillingBean {

    public String id;
    public String assesment;
    public String family;
    public String history;
    public String objective;
    public String plan;
    public String subjective;
    public String notes_date;
    public String laid;
    public String treatment_codes;
    public String author_by;
    public String is_approved;
    public String patient_name;
    public String doctor_name;
    public String care_plan;

    public BillingBean(String id, String assesment, String family, String history, String objective, String plan,
                       String subjective, String notes_date, String laid, String treatment_codes, String author_by,
                       String is_approved, String patient_name, String doctor_name,String care_plan) {
        this.id = id;
        this.assesment = assesment;
        this.family = family;
        this.history = history;
        this.objective = objective;
        this.plan = plan;
        this.subjective = subjective;
        this.notes_date = notes_date;
        this.laid = laid;
        this.treatment_codes = treatment_codes;
        this.author_by = author_by;
        this.is_approved = is_approved;
        this.patient_name = patient_name;
        this.doctor_name = doctor_name;
        this.care_plan = care_plan;
    }
}
