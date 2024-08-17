package com.app.greatriverma.model;

public class PastHistoryBean {

	private int id;
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
