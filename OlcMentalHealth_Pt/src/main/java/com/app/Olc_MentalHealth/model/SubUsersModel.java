package com.app.Olc_MentalHealth.model;

import com.google.gson.annotations.SerializedName;

public class SubUsersModel {
	
	public String id = "";
	public String patient_id = "";
	@SerializedName("first_name")
	public String firstName = "";
	@SerializedName("last_name")
	public String lastName = "";
	public String username = "";
	public String gender = "";
	public String dob = "";
	public String marital_status = "";
	public String relationship = "";
	public String occupation = "";
	public String image = "";

	public String insurance = "";

	public String is_primary = "";


	public String phone;
	public String residency;
	public String city;
	public String state;
	public String country;
	public String zipcode;


}
