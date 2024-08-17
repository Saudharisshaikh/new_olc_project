package com.app.amnm_dr.model;

public class UnknownPatientMedicationBean {

	
	 public String prescription_id;
	 public String drug_descriptor_id;
	 public String route_of_administration;
	 public String strength_unit_of_measure;
	 public String drug_name;
	 public String strength;
	 public String dosage_form;
	 public String potency_unit;
	 public String potency_code;
	
	 
	 public UnknownPatientMedicationBean(String prescription_id, String drug_descriptor_id,
			String route_of_administration, String strength_unit_of_measure, String drug_name, String strength,
			String dosage_form, String potency_unit, String potency_code) {
		super();
		this.prescription_id = prescription_id;
		this.drug_descriptor_id = drug_descriptor_id;
		this.route_of_administration = route_of_administration;
		this.strength_unit_of_measure = strength_unit_of_measure;
		this.drug_name = drug_name;
		this.strength = strength;
		this.dosage_form = dosage_form;
		this.potency_unit = potency_unit;
		this.potency_code = potency_code;
	}
	 
	 
	 
}
