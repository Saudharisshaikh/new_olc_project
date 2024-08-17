package com.app.onlinecare.model;


public class MyAppointmentsModel {
	
	/*public String id = "";
	public String date = "";
	public String drName = "";
	public String drImageUrl = "";
	public String time = "";
	public String eveningFromTime = "";
	public String morningFromTime = "";
	public String eveningToTime = "";
	public String morningToTime = "";
	public String symptomName = "";
	public String conditionName = "";
	public String description = "";
	public String status = "";*/

	
	public String appointment_id;
	public String appointment_date;
	public String first_name;
	public String last_name;
	public String dr_image;
	public String status;
	public String time;
	public String from_time;
	public String to_time;
	public String symptom_name;
	public String condition_name;
	public String description;
	public String date;
	public String slot_id;
	public String reason;
	public String doctor_category;
	
	public MyAppointmentsModel(String appointment_id, String appointment_date,
			String first_name, String last_name, String dr_image,
			String status, String time, String from_time, String to_time,
			String symptom_name, String condition_name, String description,
			String date,String slot_id,String reason,String doctor_category) {
		super();
		this.appointment_id = appointment_id;
		this.appointment_date = appointment_date;
		this.first_name = first_name;
		this.last_name = last_name;
		this.dr_image = dr_image;
		this.status = status;
		this.time = time;
		this.from_time = from_time;
		this.to_time = to_time;
		this.symptom_name = symptom_name;
		this.condition_name = condition_name;
		this.description = description;
		this.date = date;
		this.slot_id = slot_id;
		this.reason = reason;
		this.doctor_category = doctor_category;
	}
	public String getAppointment_id() {
		return appointment_id;
	}
	public String getAppointment_date() {
		return appointment_date;
	}
	public String getFirst_name() {
		return first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public String getDr_image() {
		return dr_image;
	}
	public String getStatus() {
		return status;
	}
	public String getTime() {
		return time;
	}
	public String getFrom_time() {
		return from_time;
	}
	public String getTo_time() {
		return to_time;
	}
	public String getSymptom_name() {
		return symptom_name;
	}
	public String getCondition_name() {
		return condition_name;
	}
	public String getDescription() {
		return description;
	}
	public String getDate() {
		return date;
	}
	public String getSlot_id() {
		return slot_id;
	}
	
	
	
}
