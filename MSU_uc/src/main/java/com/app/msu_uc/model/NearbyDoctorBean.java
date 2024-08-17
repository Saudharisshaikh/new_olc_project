package com.app.msu_uc.model;

public class NearbyDoctorBean {
	 
	private String id;
	private String latitude;
	private String longitude;
	private String zip_code;
	private String first_name;
	private String last_name;
	private String is_online;
	private String image;
	public NearbyDoctorBean(String id, String latitude, String longitude,
			String zip_code, String first_name, String last_name,
			String is_online, String image) {
		super();
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
		this.zip_code = zip_code;
		this.first_name = first_name;
		this.last_name = last_name;
		this.is_online = is_online;
		this.image = image;
	}
	public String getId() {
		return id;
	}
	public String getLatitude() {
		return latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public String getZip_code() {
		return zip_code;
	}
	public String getFirst_name() {
		return first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public String getIs_online() {
		return is_online;
	}
	public String getImage() {
		return image;
	}
	
	
	
	
}
