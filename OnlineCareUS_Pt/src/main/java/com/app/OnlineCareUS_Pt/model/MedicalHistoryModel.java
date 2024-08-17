package com.app.OnlineCareUS_Pt.model;

public class MedicalHistoryModel {

	private String id;
	private String patient_id;
	private String medical_history;
	private String is_smoke;
	private String smoke_detail;
	private String is_drink;
	private String drink_detail;
	private String is_drug;
	private String drug_detail;
	private String relation_had;
	private String relation_had_name;
	private String is_medication;
	private String medication_detail;
	private String is_alergies;
	private String alergies_detail;
	private String is_hospitalize;
	private String hospitalize_detail;
	private String dateof;
	public MedicalHistoryModel(String id, String patient_id,
			String medical_history, String is_smoke, String smoke_detail,
			String is_drink, String drink_detail, String is_drug,
			String drug_detail, String relation_had, String relation_had_name,
			String is_medication, String medication_detail, String is_alergies,
			String alergies_detail, String is_hospitalize,
			String hospitalize_detail, String dateof) {
		super();
		this.id = id;
		this.patient_id = patient_id;
		this.medical_history = medical_history;
		this.is_smoke = is_smoke;
		this.smoke_detail = smoke_detail;
		this.is_drink = is_drink;
		this.drink_detail = drink_detail;
		this.is_drug = is_drug;
		this.drug_detail = drug_detail;
		this.relation_had = relation_had;
		this.relation_had_name = relation_had_name;
		this.is_medication = is_medication;
		this.medication_detail = medication_detail;
		this.is_alergies = is_alergies;
		this.alergies_detail = alergies_detail;
		this.is_hospitalize = is_hospitalize;
		this.hospitalize_detail = hospitalize_detail;
		this.dateof = dateof;
	}
	public String getId() {
		return id;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public String getMedical_history() {
		return medical_history;
	}
	public String getIs_smoke() {
		return is_smoke;
	}
	public String getSmoke_detail() {
		return smoke_detail;
	}
	public String getIs_drink() {
		return is_drink;
	}
	public String getDrink_detail() {
		return drink_detail;
	}
	public String getIs_drug() {
		return is_drug;
	}
	public String getDrug_detail() {
		return drug_detail;
	}
	public String getRelation_had() {
		return relation_had;
	}
	public String getRelation_had_name() {
		return relation_had_name;
	}
	public String getIs_medication() {
		return is_medication;
	}
	public String getMedication_detail() {
		return medication_detail;
	}
	public String getIs_alergies() {
		return is_alergies;
	}
	public String getAlergies_detail() {
		return alergies_detail;
	}
	public String getIs_hospitalize() {
		return is_hospitalize;
	}
	public String getHospitalize_detail() {
		return hospitalize_detail;
	}
	public String getDateof() {
		return dateof;
	}
	
	
	
}
