package com.app.emcurama.model;

/**
 * Created by Engr G M on 8/29/2017.
 */

public class FallRiskBean {


    public String id;
    public String patient_id;
    public String doctor_id;
    public String age_65;
    public String diagnosis;
    public String prior_history;
    public String incontinence;
    public String visual_impairment;
    public String impaired_functional_mobility;
    public String environment_hazards;
    public String poly_pharmacy;
    public String pain_affection;
    public String cognitive_impairment;
    public String score;
    public String dateof;
    public String status;
    public String is_deleted;
    public String delete_date;

    public FallRiskBean(String id, String patient_id, String doctor_id, String age_65, String diagnosis, String prior_history, String incontinence, String visual_impairment, String impaired_functional_mobility, String environment_hazards, String poly_pharmacy, String pain_affection, String cognitive_impairment, String score, String dateof, String status, String is_deleted, String delete_date) {
        this.id = id;
        this.patient_id = patient_id;
        this.doctor_id = doctor_id;
        this.age_65 = age_65;
        this.diagnosis = diagnosis;
        this.prior_history = prior_history;
        this.incontinence = incontinence;
        this.visual_impairment = visual_impairment;
        this.impaired_functional_mobility = impaired_functional_mobility;
        this.environment_hazards = environment_hazards;
        this.poly_pharmacy = poly_pharmacy;
        this.pain_affection = pain_affection;
        this.cognitive_impairment = cognitive_impairment;
        this.score = score;
        this.dateof = dateof;
        this.status = status;
        this.is_deleted = is_deleted;
        this.delete_date = delete_date;
    }
}
