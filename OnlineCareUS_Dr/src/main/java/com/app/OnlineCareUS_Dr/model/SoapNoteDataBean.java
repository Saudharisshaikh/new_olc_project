package com.app.OnlineCareUS_Dr.model;

public class SoapNoteDataBean {

	
	private String first_name;
	private String last_name;
	private String birthdate;
	private String symptom_name;
	private String condition_name;
	private String medical_history;
	private String is_smoke;
	private String smoke_detail;
	private String is_drink;
	private String drink_detail;
	private String is_drug;
	private String drug_detail;
	private String is_alergies;
	private String alergies_detail;
	private String treatment;
	private String description;
	private String phistory;
	public SoapNoteDataBean(String first_name, String last_name,
			String birthdate, String symptom_name, String condition_name,
			String medical_history, String is_smoke, String smoke_detail,
			String is_drink, String drink_detail, String is_drug,
			String drug_detail, String is_alergies, String alergies_detail,
			String treatment, String description, String phistory) {
		super();
		this.first_name = first_name;
		this.last_name = last_name;
		this.birthdate = birthdate;
		this.symptom_name = symptom_name;
		this.condition_name = condition_name;
		this.medical_history = medical_history;
		this.is_smoke = is_smoke;
		this.smoke_detail = smoke_detail;
		this.is_drink = is_drink;
		this.drink_detail = drink_detail;
		this.is_drug = is_drug;
		this.drug_detail = drug_detail;
		this.is_alergies = is_alergies;
		this.alergies_detail = alergies_detail;
		this.treatment = treatment;
		this.description = description;
		this.phistory = phistory;
	}
	public String getFirst_name() {
		return first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public String getBirthdate() {
		return birthdate;
	}
	public String getSymptom_name() {
		return symptom_name;
	}
	public String getCondition_name() {
		return condition_name;
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
	public String getIs_alergies() {
		return is_alergies;
	}
	public String getAlergies_detail() {
		return alergies_detail;
	}
	public String getTreatment() {
		return treatment;
	}
	public String getDescription() {
		return description;
	}
	public String getPhistory() {
		return phistory;
	}
	
	
}
