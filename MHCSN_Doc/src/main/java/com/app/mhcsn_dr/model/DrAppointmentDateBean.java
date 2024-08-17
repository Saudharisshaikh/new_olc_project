package com.app.mhcsn_dr.model;

public class DrAppointmentDateBean {
	
	private String appointment_date;
	private String appointment_count;
	public DrAppointmentDateBean(String appointment_date,
			String appointment_count) {
		super();
		this.appointment_date = appointment_date;
		this.appointment_count = appointment_count;
	}
	public String getAppointment_date() {
		return appointment_date;
	}
	public String getAppointment_count() {
		return appointment_count;
	}

	
	
}
