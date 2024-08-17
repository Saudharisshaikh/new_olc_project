package com.app.mhcsn_dr.model;

import android.text.TextUtils;

public class CallLogBean {

	private String id;
	private String doctor_id;
	private String patient_id;
	private String response;
	private String dateof;
	private String first_name;
	private String last_name;
	private String image;
	private String callto;
	private String callto1;

	public String doctor_category;
	public String is_online = "";
	public String current_app = "";



	/*public String id;
	public String doctor_id;
	public String patient_id;
	public String response;
	public String dateof;
	public String first_name;
	public String last_name;
	public String is_online;
	public String current_app;
	public String callto;*/
	public String callfrom;
	public String end_time;
	public String dateof2;
	public String duration;
	public String phone;
	public String residency;
	public String city;
	public String state;
	public String last_seen;

	public String rating;
	public String is_visible;
	public String extra_data;
	public String virtual_visit_id;
	public String call_session_id;
	public String doctor_name;
	public String patient_name;
	public String pimage;
	public String dimage;


	/*public CallLogBean(String id, String doctor_id, String patient_id,
					   String response, String dateof, String first_name,
					   String last_name, String image,String callto,String callto1, String end_time,String dateof2) {
		super();
		this.id = id;
		this.doctor_id = doctor_id;
		this.patient_id = patient_id;
		this.response = response;
		this.dateof = dateof;
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		this.callto = callto;
		this.callto1 = callto1;
		this.end_time = end_time;
		this.dateof2 = dateof2;
	}*/
	public String getId() {
		return id;
	}
	public String getDoctor_id() {
		return doctor_id;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public String getResponse() {
		return response;
	}
	public String getDateof() {
		return dateof;
	}
	public String getFirst_name() {
		return first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public String getImage() {
		if(!TextUtils.isEmpty(image)){
			return image;
		}else {
			return pimage;//for allCalls
		}
	}
	public String getCallto() {
		return callto;
	}
	public String getCallto1() {
		return callto1;
	}



}
