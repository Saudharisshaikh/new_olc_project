package com.app.omranpatient.model;

public class MessageBean {

	private String id;
	private String doctor_id;
	private String patient_id;
	private String message_text;
	private String from;
	private String dateof;
	private String first_name;
	private String last_name;
	private String image;

	public String msg_type = "";
	public String files = "";

	public MessageBean(String id, String doctor_id, String patient_id,
			String message_text, String from, String dateof, String first_name,
			String last_name, String image) {
		super();
		this.id = id;
		this.doctor_id = doctor_id;
		this.patient_id = patient_id;
		this.message_text = message_text;
		this.from = from;
		this.dateof = dateof;
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
	}
	public String getId() {
		return id;
	}
	public String getDoctor_id() {
		return doctor_id;
	}
	public String getPatient_id() {
		return patient_id;
	}
	public String getMessage_text() {
		return message_text;
	}
	public String getFrom() {
		return from;
	}
	public String getDateof() {
		return dateof;
	}
	public String getFirst_name() {
		return first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public String getImage() {
		return image;
	}
	
	
}
