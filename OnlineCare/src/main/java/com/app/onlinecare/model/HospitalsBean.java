package com.app.onlinecare.model;

public class HospitalsBean {

	 private double lat;
	 private double lng;
	 private String icon;
	 private String name;
	 private String vicinity;
	 
	 
	 
	 
	public HospitalsBean(double lat, double lng, String icon, String name,
			String vicinity) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.icon = icon;
		this.name = name;
		this.vicinity = vicinity;
	}
	public double getLat() {
		return lat;
	}
	public double getLng() {
		return lng;
	}
	public String getIcon() {
		return icon;
	}
	public String getName() {
		return name;
	}
	public String getVicinity() {
		return vicinity;
	}
	 
	 
}
