package com.app.amnm_dr.model;

public class DrugRefillBean {

	
	private String refill_id;
	private String drug_descriptor_id;
	private String drug_name;
	private String strength;
	private String dosage_form;
	private String refill;
	private String selected_potency_code;
	private String quantity;
	private String directions;
	private String start_date;
	private String end_date;
	private String potency_unit;
	private String potency_code;
	private String request_type;
	private String prescribedBy;
	private String supply_days;
	private String selected_potency_unit;
	private String refill_qualifier;
	
	public String note;
	public String substitutions;
	
	public DrugRefillBean(String refill_id, String drug_descriptor_id, String drug_name, String strength,
			String dosage_form, String refill, String selected_potency_code, String quantity, String directions,
			String start_date, String end_date, String potency_unit, String potency_code, String request_type,
			String prescribedBy, String supply_days, String selected_potency_unit,String refill_qualifier) {
		super();
		this.refill_id = refill_id;
		this.drug_descriptor_id = drug_descriptor_id;
		this.drug_name = drug_name;
		this.strength = strength;
		this.dosage_form = dosage_form;
		this.refill = refill;
		this.selected_potency_code = selected_potency_code;
		this.quantity = quantity;
		this.directions = directions;
		this.start_date = start_date;
		this.end_date = end_date;
		this.potency_unit = potency_unit;
		this.potency_code = potency_code;
		this.request_type = request_type;
		this.prescribedBy = prescribedBy;
		this.supply_days = supply_days;
		this.selected_potency_unit = selected_potency_unit;
		
		this.refill_qualifier = refill_qualifier;
	}
	public String getRefill_id() {
		return refill_id;
	}
	public String getDrug_descriptor_id() {
		return drug_descriptor_id;
	}
	public String getDrug_name() {
		return drug_name;
	}
	public String getStrength() {
		return strength;
	}
	public String getDosage_form() {
		return dosage_form;
	}
	public String getRefill() {
		return refill;
	}
	public String getSelected_potency_code() {
		return selected_potency_code;
	}
	public String getQuantity() {
		return quantity;
	}
	public String getDirections() {
		return directions;
	}
	public String getStart_date() {
		return start_date;
	}
	public String getEnd_date() {
		return end_date;
	}
	public String getPotency_unit() {
		return potency_unit;
	}
	public String getPotency_code() {
		return potency_code;
	}
	public String getRequest_type() {
		return request_type;
	}
	public String getPrescribedBy() {
		return prescribedBy;
	}
	public String getSupply_days() {
		return supply_days;
	}
	public String getSelected_potency_unit() {
		return selected_potency_unit;
	}
	public String getRefill_qualifier() {
		return refill_qualifier;
	}
	
	
	
}
