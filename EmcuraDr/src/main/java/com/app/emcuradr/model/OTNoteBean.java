package com.app.emcuradr.model;

/**
 * Created by Engr G M on 5/12/2017.
 */

public class OTNoteBean {

    public String id;
    public String author_by;
    public String patient_id;
    public String dateof;
    public String first_name;
    public String last_name;
    public String image;
    //"note_data" json object contains:
    public String ot_dmeneed;
    public String ot_date;
    public String ot_iadl;
    public String ot_respirations;
    public String ot_adl;
    public String ot_hr;
    public String ot_plan;
    public String ot_bp;
    public String ot_mobility;
    public String ot_subjective;
    public String ot_saturation;
    public String ot_timein;
    public String ot_timeout;
    public String ot_blood_sugar;
    public String num_images;
    public String ot_temperature;
    public String ot_generic_assessment;
    public String dme_referral;

    public OTNoteBean(String id, String author_by, String patient_id, String dateof, String first_name, String last_name,
                      String image, String ot_dmeneed, String ot_date, String ot_iadl, String ot_respirations, String ot_adl,
                      String ot_hr, String ot_plan, String ot_bp, String ot_mobility, String ot_subjective, String ot_saturation,
                      String ot_timein, String ot_timeout,String ot_blood_sugar,String num_images,String ot_temperature,String ot_generic_assessment,
                      String dme_referral) {
        this.id = id;
        this.author_by = author_by;
        this.patient_id = patient_id;
        this.dateof = dateof;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.ot_dmeneed = ot_dmeneed;
        this.ot_date = ot_date;
        this.ot_iadl = ot_iadl;
        this.ot_respirations = ot_respirations;
        this.ot_adl = ot_adl;
        this.ot_hr = ot_hr;
        this.ot_plan = ot_plan;
        this.ot_bp = ot_bp;
        this.ot_mobility = ot_mobility;
        this.ot_subjective = ot_subjective;
        this.ot_saturation = ot_saturation;
        this.ot_timein = ot_timein;
        this.ot_timeout = ot_timeout;
        this.ot_blood_sugar = ot_blood_sugar;
        this.num_images = num_images;
        this.ot_temperature = ot_temperature;
        this.ot_generic_assessment = ot_generic_assessment;
        this.dme_referral = dme_referral;
    }
}
