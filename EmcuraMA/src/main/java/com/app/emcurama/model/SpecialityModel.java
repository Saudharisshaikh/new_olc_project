package com.app.emcurama.model;

import com.google.gson.annotations.SerializedName;

public class SpecialityModel {

	//{"id":"25","speciality_name":"ADDICTION PSYCHIATRIST"}

	@SerializedName("id")
	public String specialityId = "";

	@SerializedName("speciality_name")
	public String specialityName = "";
	
	public SpecialityModel() {
		// TODO Auto-generated constructor stub
	}
	public SpecialityModel(String specialityId, String specialityName) {
		super();
		this.specialityId = specialityId;
		this.specialityName = specialityName;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return specialityName;
	}
}
