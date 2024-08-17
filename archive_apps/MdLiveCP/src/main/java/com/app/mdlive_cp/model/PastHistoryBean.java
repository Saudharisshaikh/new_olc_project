package com.app.mdlive_cp.model;

import com.google.gson.annotations.SerializedName;

public class PastHistoryBean {

	private int id;
	@SerializedName("dname")
	private String diseases;
	private boolean selected;

	public PastHistoryBean(int id, String diseases, boolean selected) {
		super();
		this.id = id;
		this.diseases = diseases;
		this.selected = selected;
	}
	public int getId() {
		return id;
	}
	public String getDiseases() {
		return diseases;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setDiseases(String diseases) {
		this.diseases = diseases;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
}
