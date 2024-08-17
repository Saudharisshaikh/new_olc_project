package com.app.OnlineCareUS_Dr.model;

public class PharmacyBean {
	
	
	public String id;
	public String NPI;
	public String StoreName;
	public String PhonePrimary;
	public String Latitude;
	public String Longitude;
	public String status;
	public String address;
	public String city;
	public String state;
	public String zipcode;
	public String SpecialtyName;
	public String distance;
	
	
	public PharmacyBean(String id, String nPI, String storeName, String phonePrimary, String latitude, String longitude,
			String status, String address, String city, String state, String zipcode, String specialtyName,
			String distance) {
		super();
		this.id = id;
		NPI = nPI;
		StoreName = storeName;
		PhonePrimary = phonePrimary;
		Latitude = latitude;
		Longitude = longitude;
		this.status = status;
		this.address = address;
		this.city = city;
		this.state = state;
		this.zipcode = zipcode;
		SpecialtyName = specialtyName;
		this.distance = distance;
	}
	
	
	
}
