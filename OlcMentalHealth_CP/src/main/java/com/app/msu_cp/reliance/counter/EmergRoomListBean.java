package com.app.msu_cp.reliance.counter;

public class EmergRoomListBean {


    public String id;
    public String patient_id;
    public String author_id;
    public String dateof;
    public String trip_date;
    public String hospital;
    public String admitted_observation;
    public String avoidable_er;
    public String reason;
    public String type_of_visit;
    public String facility_name;
    public String additional_info;


    public EmergRoomListBean(String id, String patient_id, String author_id, String dateof, String trip_date, String hospital, String admitted_observation, String avoidable_er, String reason, String type_of_visit, String facility_name, String additional_info) {
        this.id = id;
        this.patient_id = patient_id;
        this.author_id = author_id;
        this.dateof = dateof;
        this.trip_date = trip_date;
        this.hospital = hospital;
        this.admitted_observation = admitted_observation;
        this.avoidable_er = avoidable_er;
        this.reason = reason;
        this.type_of_visit = type_of_visit;
        this.facility_name = facility_name;
        this.additional_info = additional_info;
    }
}
