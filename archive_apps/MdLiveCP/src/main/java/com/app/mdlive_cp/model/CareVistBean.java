package com.app.mdlive_cp.model;

/**
 * Created by Engr G M on 6/29/2018.
 */

public class CareVistBean {

    public String id;
    public String slot_id;
    public String patient_id;
    public String sub_patient_id;
    public String time;
    public String appointment_date;
    public String date;
    public String payment_method;
    public String free_reason;
    public String symptom_id;
    public String condition_id;
    public String description;
    public String diagnosis;
    public String treatment;
    public String doctor_notification;
    public String patient_notification;
    public String status;
    public String dr_schedule_id;
    public String book_from;
    public String booker_id;
    public String first_name;
    public String last_name;
    public String symptom_name;
    public String condition_name;
    public String image;
    public String latitude;
    public String longitude;
    public String day;
    public String from_time;
    public String to_time;
    public String birthdate;
    public String gender;
    public String residency;
    public String phone;
    public String StoreName;
    public String PhonePrimary;
    public String doctor_id;
    public String doc_name;
    public String dimage;
    //public String reports;
    public String is_online;
    public String d_is_online;

    public String current_app;
    public String patient_current_app;
    public String primary_patient_id;


    public CareVistBean(String id, String slot_id, String patient_id, String sub_patient_id, String time, String appointment_date,
                        String date, String payment_method, String free_reason, String symptom_id, String condition_id, String description,
                        String diagnosis, String treatment, String doctor_notification, String patient_notification, String status,
                        String dr_schedule_id, String book_from, String booker_id, String first_name, String last_name, String symptom_name,
                        String condition_name, String image, String latitude, String longitude, String day, String from_time, String to_time,
                        String birthdate, String gender, String residency, String phone, String storeName, String phonePrimary, String doctor_id,
                        String doc_name, String dimage, String is_online, String d_is_online,String current_app,String patient_current_app,
                        String primary_patient_id) {//, String reports  after dimage
        this.id = id;
        this.slot_id = slot_id;
        this.patient_id = patient_id;
        this.sub_patient_id = sub_patient_id;
        this.time = time;
        this.appointment_date = appointment_date;
        this.date = date;
        this.payment_method = payment_method;
        this.free_reason = free_reason;
        this.symptom_id = symptom_id;
        this.condition_id = condition_id;
        this.description = description;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.doctor_notification = doctor_notification;
        this.patient_notification = patient_notification;
        this.status = status;
        this.dr_schedule_id = dr_schedule_id;
        this.book_from = book_from;
        this.booker_id = booker_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.symptom_name = symptom_name;
        this.condition_name = condition_name;
        this.image = image;
        this.latitude = latitude;
        this.longitude = longitude;
        this.day = day;
        this.from_time = from_time;
        this.to_time = to_time;
        this.birthdate = birthdate;
        this.gender = gender;
        this.residency = residency;
        this.phone = phone;
        StoreName = storeName;
        PhonePrimary = phonePrimary;
        this.doctor_id = doctor_id;
        this.doc_name = doc_name;
        this.dimage = dimage;
        //this.reports = reports;
        this.is_online = is_online;
        this.d_is_online = d_is_online;
        this.current_app = current_app;
        this.patient_current_app = patient_current_app;
        this.primary_patient_id = primary_patient_id;
    }
}
