package com.app.amnm_dr.model;

/**
 * Created by Engr G M on 1/24/2018.
 */

public class ProgressNoteBean {


    public String id;
    public String dateof;
    public String patient_id;
    public String author_id;
    public String call_id;
    public String virtual_visit_id;
    public String explanatory_notes;
    public String interventions;
    public String feedback;
    public String ddate;
    public String ttime;
    public String session_length;
    public String first_name;
    public String last_name;
    public String patient_name;
    public String ot_data;
    public String symptom_name;
    public String condition_name;
    public String care_plan;


    public ProgressNoteBean(String id, String dateof, String patient_id, String author_id, String call_id, String virtual_visit_id,
                            String explanatory_notes, String interventions, String feedback, String ddate, String ttime, String session_length,
                            String first_name, String last_name, String patient_name, String ot_data, String symptom_name, String condition_name,
                            String care_plan) {
        this.id = id;
        this.dateof = dateof;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.call_id = call_id;
        this.virtual_visit_id = virtual_visit_id;
        this.explanatory_notes = explanatory_notes;
        this.interventions = interventions;
        this.feedback = feedback;
        this.ddate = ddate;
        this.ttime = ttime;
        this.session_length = session_length;
        this.first_name = first_name;
        this.last_name = last_name;
        this.patient_name = patient_name;
        this.ot_data = ot_data;
        this.symptom_name = symptom_name;
        this.condition_name = condition_name;
        this.care_plan = care_plan;
    }
}
