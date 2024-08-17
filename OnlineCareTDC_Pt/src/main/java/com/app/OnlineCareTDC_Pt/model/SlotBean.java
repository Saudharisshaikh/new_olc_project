package com.app.OnlineCareTDC_Pt.model;

public class SlotBean {

	 private String id;
	 private String day;
	 private String from_time;
	 private String to_time;
	 private String status;
	 private String is_morning_evening;
	public SlotBean(String id, String day, String from_time, String to_time,
			String status,String is_morning_evening) {
		super();
		this.id = id;
		this.day = day;
		this.from_time = from_time;
		this.to_time = to_time;
		this.status = status;
		this.is_morning_evening = is_morning_evening;
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
	public String getIs_morning_evening() { return is_morning_evening; }
}
