package com.app.mdlive_cp.model;

public class SlotBean {

	 private String id;
	 private String day;
	 private String from_time;
	 private String to_time;
	 private String status;
	public SlotBean(String id, String day, String from_time, String to_time,
                    String status) {
		super();
		this.id = id;
		this.day = day;
		this.from_time = from_time;
		this.to_time = to_time;
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public String getDay() {
		return day;
	}
	public String getFrom_time() {
		return from_time;
	}
	public String getTo_time() {
		return to_time;
	}
	public String getStatus() {
		return status;
	}
	 
	 
	 
}
