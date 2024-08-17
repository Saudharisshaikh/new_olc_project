package com.app.emcuradr.model;

public class RefillBean {

	private String id;
	private String parent_id;
	private String prescription_id;
	private String olc_message_id;
	private String surescript_message_id;
	private String pon;
	private String drug_descriptor_id;
	private String potency_unit_code;
	private String quantity;
	private String refill;
	private String start_date;
	private String end_date;
	private String directions;
	private String refill_status;
	private String drug_name;
	private String dateof;
	private String first_name;
	private String last_name;
	private String image;
	
	private String birthdate;
	private String residency;
	private String phone;
	private String gender;
	private String StoreName;
	private String PhonePrimary;
	
	//private String live_checkup_id;
	private String patient_id;
	
	private String vitals;
	private String diagnosis;
	
	public RefillBean(String id, String parent_id, String prescription_id, String olc_message_id,
			String surescript_message_id, String pon, String drug_descriptor_id, String potency_unit_code,
			String quantity, String refill, String start_date, String end_date, String directions, String refill_status,
			String drug_name, String dateof, String first_name, String last_name, String image,
			String birthdate,String residency,String phone,String gender,String StoreName,String PhonePrimary,
			String patient_id,String vitals,String diagnosis) {
		super();
		this.id = id;
		this.parent_id = parent_id;
		this.prescription_id = prescription_id;
		this.olc_message_id = olc_message_id;
		this.surescript_message_id = surescript_message_id;
		this.pon = pon;
		this.drug_descriptor_id = drug_descriptor_id;
		this.potency_unit_code = potency_unit_code;
		this.quantity = quantity;
		this.refill = refill;
		this.start_date = start_date;
		this.end_date = end_date;
		this.directions = directions;
		this.refill_status = refill_status;
		this.drug_name = drug_name;
		this.dateof = dateof;
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		
		this.birthdate = birthdate;
		this.residency = residency;
		this.phone = phone;
		this.gender = gender;
		this.StoreName = StoreName;
		this.PhonePrimary = PhonePrimary;
		
		//this.live_checkup_id = live_checkup_id;
		this.patient_id = patient_id;
		
		this.vitals = vitals;
		this.diagnosis = diagnosis;
	}
	public String getId() {
		return id;
	}
	public String getParent_id() {
		return parent_id;
	}
	public String getPrescription_id() {
		return prescription_id;
	}
	public String getOlc_message_id() {
		return olc_message_id;
	}
	public String getSurescript_message_id() {
		return surescript_message_id;
	}
	public String getPon() {
		return pon;
	}
	public String getDrug_descriptor_id() {
		return drug_descriptor_id;
	}
	public String getPotency_unit_code() {
		return potency_unit_code;
	}
	public String getQuantity() {
		return quantity;
	}
	public String getRefill() {
		return refill;
	}
	public String getStart_date() {
		return start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public String getDirections() {
		return directions;
	}
	public String getRefill_status() {
		return refill_status;
	}
	public String getDrug_name() {
		return drug_name;
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
		return image;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public String getResidency() {
		return residency;
	}
	public String getPhone() {
		return phone;
	}
	public String getGender() {
		return gender;
	}
	public String getStoreName() {
		return StoreName;
	}
	public String getPhonePrimary() {
		return PhonePrimary;
	}
	/*public String getLive_checkup_id() {
		return live_checkup_id;
	}*/
	public String getPatient_id() {
		return patient_id;
	}
	public String getVitals() {
		return vitals;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public void setPatient_id(String patient_id) {
		this.patient_id = patient_id;
	}
	public void setPrescription_id(String prescription_id) {
		this.prescription_id = prescription_id;
	}
	
	
	
}
