package com.app.mdlive_dr.model;

public class DosageFormsBean {

	
	private String field_value;
	private String dosage_form;
	public DosageFormsBean(String field_value, String dosage_form) {
		super();
		this.field_value = field_value;
		this.dosage_form = dosage_form;
	}
	public String getField_value() {
		return field_value;
	}
	public String getDosage_form() {
		return dosage_form;
	}
	
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return dosage_form;
	}
}
