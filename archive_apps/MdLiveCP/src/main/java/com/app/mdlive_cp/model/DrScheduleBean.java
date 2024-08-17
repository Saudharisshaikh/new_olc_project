package com.app.mdlive_cp.model;

public class DrScheduleBean {
	private String id;
	private String doctor_id;
	private String day;
	private String from_time;
	private String to_time;
	private String evening_from_time;
	private String evening_to_time;
	private String is_morning;
	private String is_evening;
	
	
	public DrScheduleBean(String id, String doctor_id, String day,
                          String from_time, String to_time, String evening_from_time,
                          String evening_to_time, String is_morning, String is_evening) {
		super();
		this.id = id;
		this.doctor_id = doctor_id;
		this.day = day;
		this.from_time = from_time;
		this.to_time = to_time;
		this.evening_from_time = evening_from_time;
		this.evening_to_time = evening_to_time;
		this.is_morning = is_morning;
		this.is_evening = is_evening;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDoctor_id() {
		return doctor_id;
	}
	public void setDoctor_id(String doctor_id) {
		this.doctor_id = doctor_id;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getFrom_time() {
		return from_time;
	}
	public void setFrom_time(String from_time) {
		this.from_time = from_time;
	}
	public String getTo_time() {
		return to_time;
	}
	public void setTo_time(String to_time) {
		this.to_time = to_time;
	}
	public String getEvening_from_time() {
		return evening_from_time;
	}
	public void setEvening_from_time(String evening_from_time) {
		this.evening_from_time = evening_from_time;
	}
	public String getEvening_to_time() {
		return evening_to_time;
	}
	public void setEvening_to_time(String evening_to_time) {
		this.evening_to_time = evening_to_time;
	}
	public String getIs_morning() {
		return is_morning;
	}
	public void setIs_morning(String is_morning) {
		this.is_morning = is_morning;
	}
	public String getIs_evening() {
		return is_evening;
	}
	public void setIs_evening(String is_evening) {
		this.is_evening = is_evening;
	}
	
	
}
