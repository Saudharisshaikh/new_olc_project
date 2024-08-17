package com.app.fivestardoc.model;

public class DiscNoteBean {

    public String id;
    public Notes notes;
    public String notes_date;
    public String laid;
    public String note_type;
    public String treatment_codes;
    public String author_by;
    public String is_approved;
    public String note_patient_id;
    public String is_amended;
    public String ot_data;
    public String note_status;
    public String is_deleted;
    public String prescription_id;
    public String examination;
    public String dme_referral;
    public String skilled_nursing;
    public String dme_status;
    public String skilled_nursing_status;
    public String homecare_referral;
    public String homecare_status;
    public String submit_type;
    public String call_time;
    public String is_visible;
    public String dr_name;
    public String patient_name;

    public static class Notes {
        public String note_text;
    }
    public DiscNoteBean() {
    }

    public DiscNoteBean(String id, Notes notes, String notes_date, String laid, String note_type, String treatment_codes, String author_by, String is_approved, String note_patient_id, String is_amended, String ot_data, String note_status, String is_deleted, String prescription_id, String examination, String dme_referral, String skilled_nursing, String dme_status, String skilled_nursing_status, String homecare_referral, String homecare_status, String submit_type, String call_time, String is_visible, String dr_name, String patient_name) {
        this.id = id;
        this.notes = notes;
        this.notes_date = notes_date;
        this.laid = laid;
        this.note_type = note_type;
        this.treatment_codes = treatment_codes;
        this.author_by = author_by;
        this.is_approved = is_approved;
        this.note_patient_id = note_patient_id;
        this.is_amended = is_amended;
        this.ot_data = ot_data;
        this.note_status = note_status;
        this.is_deleted = is_deleted;
        this.prescription_id = prescription_id;
        this.examination = examination;
        this.dme_referral = dme_referral;
        this.skilled_nursing = skilled_nursing;
        this.dme_status = dme_status;
        this.skilled_nursing_status = skilled_nursing_status;
        this.homecare_referral = homecare_referral;
        this.homecare_status = homecare_status;
        this.submit_type = submit_type;
        this.call_time = call_time;
        this.is_visible = is_visible;
        this.dr_name = dr_name;
        this.patient_name = patient_name;
    }
}
