package com.app.fivestardoc.model;

public class IdentifyPatientBean {
	

    private String id;
    private String first_name;
    private String last_name;
    private String birthdate;
    private String residency;
    private String gender;
    private String pimage;
    
    
    
    
	public IdentifyPatientBean(String id, String first_name, String last_name, String birthdate, String residency,
			String gender, String pimage) {
		super();
		this.id = id;
		this.first_name = first_name;
		this.last_name = last_name;
		this.birthdate = birthdate;
		this.residency = residency;
		this.gender = gender;
		this.pimage = pimage;
	}
	public String getId() {
		return id;
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
	public String getResidency() {
		return residency;
	}
	public String getGender() {
		return gender;
	}
	public String getPimage() {
		return pimage;
	}
    
    
    

}
