package com.app.emcuradr.model;


public class ReportsModel {
	
	public String userId = "";
	public String date = "";
	public String name = "";
	public String url;
	public String patientID;
	

	
	
	public ReportsModel() {
	}
	
	public void setDateName(String date, String name) {
		
		this.date = date;
		this.name = name;
	}
	

}
