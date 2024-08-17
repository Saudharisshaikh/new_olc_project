package com.app.emcuradr.model;

public class ConversationBean {

	private String id;
	private String doctor_id;
	private String patient_id;
	private String message_text;
	private String from;
	private String dateof;
	private String first_name;
	private String last_name;
	private String image;
	private String to;
	private String specialist_id;
	private String user_type;
	
	private String to_id;

	public String doctor_category;
	
	
	
	
	public ConversationBean(String id, String doctor_id, String patient_id,
			String message_text, String from, String dateof, String first_name,
			String last_name, String image, String to, String specialist_id,
			String user_type, String to_id) {
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
		this.to = to;
		this.specialist_id = specialist_id;
		this.user_type = user_type;
		this.to_id = to_id;
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
	public String getTo() {
		return to;
	}
	public String getSpecialist_id() {
		return specialist_id;
	}
	public String getUser_type() {
		return user_type;
	}
	public String getTo_id() {
		return to_id;
	}
	
	
}
