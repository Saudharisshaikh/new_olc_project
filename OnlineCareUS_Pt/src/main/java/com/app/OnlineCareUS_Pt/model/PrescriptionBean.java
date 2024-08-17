package com.app.OnlineCareUS_Pt.model;

public class PrescriptionBean {

	private String id;
	private String  patient_id;
	private String  doctor_id;
	private String  vitals;
	private String  diagnosis;
	private String  treatment;
	private String  dateof;
	private String  first_name;
	private String  last_name;
	private String  image;
	private String  signature;

	public PrescriptionBean(String id, String patient_id, String doctor_id,
			String vitals, String diagnosis, String treatment, String dateof,
			String first_name, String last_name, String image, String signature) {
		super();
		this.id = id;
		this.patient_id = patient_id;
		this.doctor_id = doctor_id;
		this.vitals = vitals;
		this.diagnosis = diagnosis;
		this.treatment = treatment;
		this.dateof = dateof;
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		this.signature = signature;
	}
	public String getId() {
		return id;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public String getDoctor_id() {
		return doctor_id;
	}
	public String getVitals() {
		return vitals;
	}
	public String getDiagnosis() {
		return diagnosis;
	}
	public String getTreatment() {
		return treatment;
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
	public String getSignature() {
		return signature;
	}
	
	
	
}
