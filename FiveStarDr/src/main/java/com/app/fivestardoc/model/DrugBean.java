package com.app.fivestardoc.model;

public class DrugBean {

	private String drug_descriptor_id;
	private String route_of_administration;
	private String drug_name;
    private String code;
    private String route;
    private String strength;
    private String strength_unit_of_measure;
    private String dosage_form;
    private String dfcode;
    private String dfdesc;
    
    private String potency_unit;
    private String potency_code;
    
    public String instructions;//this var is used to send prescrtions in sendPres_ new work_ medispan/newRX/Surescript
    public String refill;//this var is used to send prescrtions in sendPres_ new work_ medispan
    public String start_date;//this var is used to send prescrtions in sendPres_ new work_ medispan
    public String end_date;//this var is used to send prescrtions in sendPres_ new work_ medispan
    public String totalQuantity;//this var is used to send prescrtions in sendPres_ new work_ medispan
    public String duration;//this var is used to send prescrtions in sendPres_ new work_ medispan newRX

	public String rxfillIndicator;//this var is used to send prescrtions in sendPres_ new work_ medispan newRX
	public String instructionNote;//this var is used to send prescrtions in sendPres_ new work_ medispan newRX

	public boolean isTemplateDrug = false;
    
	public DrugBean(String drug_descriptor_id , String route_of_administration,
			String drug_name, String code, String route, String strength,
			String strength_unit_of_measure, String dosage_form, String dfcode,
			String dfdesc,String potency_unit,String potency_code) {
		super();
		this.drug_descriptor_id = drug_descriptor_id;
		this.route_of_administration = route_of_administration;
		this.drug_name = drug_name;
		this.code = code;
		this.route = route;
		this.strength = strength;
		this.strength_unit_of_measure = strength_unit_of_measure;
		this.dosage_form = dosage_form;
		this.dfcode = dfcode;
		this.dfdesc = dfdesc;
		this.potency_unit = potency_unit;
		this.potency_code = potency_code;
	}
	public String getDrug_descriptor_id() {
		return drug_descriptor_id;
	}
	public String getRoute_of_administration() {
		return route_of_administration;
	}
	public String getDrug_name() {
		return drug_name;
	}
	public String getCode() {
		return code;
	}
	public String getRoute() {
		return route;
	}
	public String getStrength() {
		return strength;
	}
	public String getStrength_unit_of_measure() {
		return strength_unit_of_measure;
	}
	public String getDosage_form() {
		return dosage_form;
	}
	public String getDfcode() {
		return dfcode;
	}
	public String getDfdesc() {
		return dfdesc;
	}
	
	
	
    
    public void setDrug_descriptor_id(String drug_descriptor_id) {
		this.drug_descriptor_id = drug_descriptor_id;
	}
	public void setRoute_of_administration(String route_of_administration) {
		this.route_of_administration = route_of_administration;
	}
	public void setDrug_name(String drug_name) {
		this.drug_name = drug_name;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setRoute(String route) {
		this.route = route;
	}
	public void setStrength(String strength) {
		this.strength = strength;
	}
	public void setStrength_unit_of_measure(String strength_unit_of_measure) {
		this.strength_unit_of_measure = strength_unit_of_measure;
	}
	public void setDosage_form(String dosage_form) {
		this.dosage_form = dosage_form;
	}
	public void setDfcode(String dfcode) {
		this.dfcode = dfcode;
	}
	public void setDfdesc(String dfdesc) {
		this.dfdesc = dfdesc;
	}
	@Override
    public String toString() {
    	// TODO Auto-generated method stub
    	return drug_name;
    }
	public String getPotency_unit() {
		return potency_unit;
	}
	public void setPotency_unit(String potency_unit) {
		this.potency_unit = potency_unit;
	}
	public String getPotency_code() {
		return potency_code;
	}
	public void setPotency_code(String potency_code) {
		this.potency_code = potency_code;
	}
	
	
    
}
