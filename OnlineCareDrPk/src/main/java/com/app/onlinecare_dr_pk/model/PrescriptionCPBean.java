package com.app.onlinecare_dr_pk.model;

public class PrescriptionCPBean {

	public String first_name;
	public String last_name;
	public String image;
	public String signature;
	public String dateof;
	public String prescribed_by;
	public String drug_name;
	public String ppstatus;
	public String id;
	public String directions;
	public String quantity;
	public String refill;

	public PrescriptionCPBean(String first_name, String last_name, String image, String signature, String dateof,
							  String prescribed_by, String drug_name,
							  String ppstatus, String id, String directions, String quantity, String refill) {
		this.first_name = first_name;
		this.last_name = last_name;
		this.image = image;
		this.signature = signature;
		this.dateof = dateof;
		this.prescribed_by = prescribed_by;
		this.drug_name = drug_name;
		this.ppstatus = ppstatus;
		this.id = id;
		this.directions = directions;
		this.quantity = quantity;
		this.refill = refill;
	}
}
