package com.app.msu_cp.model;

public class DoctorsModel {
	
	public String id="";
	public String qb_id="";
	public String fName="";
	public String lName="";
	public String email="";
	public String qualification="";
	public String designation="";
	public String careerData="";
	public String image="";
	
	public String mobile="";
	public String introduction="";	
	public String latitude="";
	public String longitude="";
	public String zip_code;
	public String is_online;
	public String speciality_id;
	public String current_app;
	
	public String speciality_name = "";

	public String listdoc = "";
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return fName+" "+lName;
	}
}
