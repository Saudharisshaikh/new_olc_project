package com.app.onlinecare_dr_pk.model;

public class StateBean {

	private String name;
	private String abbreviation;
	public StateBean(String name, String abbreviation) {
		super();
		this.name = name;
		this.abbreviation = abbreviation;
	}
	public String getName() {
		return name;
	}
	public String getAbbreviation() {
		return abbreviation;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		//return name+" ("+abbreviation+")";
		return name;
	}
	
}
