package com.app.OnlineCareTDC_Dr.model;

import java.util.ArrayList;

public class DrAppointmentModel {
	private String id;
	//private String dr_schedule_id;
	private String patient_id;
	private String sub_patient_id;
	private String time;
	private String appointment_date;
	private String date;
	private String payment_method;
	private String free_reason;
	private String symptom_id;
	private String condition_id;
	private String description;
	private String diagnosis;
	private String treatment;
	private String doctor_notification;
	private String patient_notification;
	private String status;
	private String first_name;
	private String last_name;
	private String symptom_name;
	private String condition_name;
	private String image;
	private String patient_image;
	private String day;
	private String from_time;
	private String to_time;
	
	public boolean isSelected;
	public ArrayList<ReportsModel> sharedReports;
	
	
	private String birthdate;
	private String gender;
	private String residency;
	private String phone;
	private String StoreName;
	private String PhonePrimary;
	public String pharmacy_address;
	
	public DrAppointmentModel(String id, String patient_id,
			String sub_patient_id, String time, String appointment_date,
			String date, String payment_method, String free_reason,
			String symptom_id, String condition_id, String description,
			String diagnosis, String treatment, String doctor_notification,
			String patient_notification, String status, String first_name,
			String last_name, String symptom_name, String condition_name,
			String image, String patient_image, String day, String from_time,
			String to_time,String birthdate,String gender,String residency,String phone,String StoreName,String PhonePrimary, String pharmacy_address) {
		super();
		this.id = id;
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
		this.first_name = first_name;
		this.last_name = last_name;
		this.symptom_name = symptom_name;
		this.condition_name = condition_name;
		this.image = image;
		this.patient_image = patient_image;
		this.day = day;
		this.from_time = from_time;
		this.to_time = to_time;
		
		this.birthdate = birthdate;
		this.gender = gender;
		this.residency = residency;
		this.phone = phone;
		this.StoreName = StoreName;
		this.PhonePrimary = PhonePrimary;
		this.pharmacy_address = pharmacy_address;
	}
	/*public DrAppointmentModel(String id, 
			String patient_id, String sub_patient_id, String time,
			String appointment_date, String date, String payment_method,
			String free_reason, String symptom_id, String condition_id,
			String description, String diagnosis, String treatment,
			String doctor_notification, String patient_notification,
			String status, String first_name, String last_name,
			String symptom_name, String condition_name, String image,
			String patient_image) {
		super();
		this.id = id;
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
		this.first_name = first_name;
		this.last_name = last_name;
		this.symptom_name = symptom_name;
		this.condition_name = condition_name;
		this.image = image;
		this.patient_image = patient_image;
	}*/
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public String getPatient_id() {
		return patient_id;
	}
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}
	public String getSub_patient_id() {
		return sub_patient_id;
	}
	public void setSub_patient_id(String sub_patient_id) {
		this.sub_patient_id = sub_patient_id;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getAppointment_date() {
		return appointment_date;
	}
	public void setAppointment_date(String appointment_date) {
		this.appointment_date = appointment_date;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getPayment_method() {
		return payment_method;
	}
	public void setPayment_method(String payment_method) {
		this.payment_method = payment_method;
	}
	public String getFree_reason() {
		return free_reason;
	}
	public void setFree_reason(String free_reason) {
		this.free_reason = free_reason;
	}
	public String getSymptom_id() {
		return symptom_id;
	}
	public void setSymptom_id(String symptom_id) {
		this.symptom_id = symptom_id;
	}
	public String getCondition_id() {
		return condition_id;
	}
	public void setCondition_id(String condition_id) {
		this.condition_id = condition_id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}
	public String getTreatment() {
		return treatment;
	}
	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}
	public String getDoctor_notification() {
		return doctor_notification;
	}
	public void setDoctor_notification(String doctor_notification) {
		this.doctor_notification = doctor_notification;
	}
	public String getPatient_notification() {
		return patient_notification;
	}
	public void setPatient_notification(String patient_notification) {
		this.patient_notification = patient_notification;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public String getSymptom_name() {
		return symptom_name;
	}
	public void setSymptom_name(String symptom_name) {
		this.symptom_name = symptom_name;
	}
	public String getCondition_name() {
		return condition_name;
	}
	public void setCondition_name(String condition_name) {
		this.condition_name = condition_name;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getPatient_image() {
		return patient_image;
	}
	public void setPatient_image(String patient_image) {
		this.patient_image = patient_image;
	}
	public String getDay() {
		return day;
	}
	public String getFrom_time() {
		return from_time;
	}
	public String getTo_time() {
		return to_time;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public String getGender() {
		return gender;
	}
	public String getResidency() {
		return residency;
	}
	public String getPhone() {
		return phone;
	}
	public String getStoreName() {
		return StoreName;
	}
	public String getPhonePrimary() {
		return PhonePrimary;
	}
	
	
}
