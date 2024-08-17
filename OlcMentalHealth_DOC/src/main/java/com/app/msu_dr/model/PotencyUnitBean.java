package com.app.msu_dr.model;

public class PotencyUnitBean {

	private String potency_unit;
	private String potency_code;
	public PotencyUnitBean(String potency_unit, String potency_code) {
		super();
		this.potency_unit = potency_unit;
		this.potency_code = potency_code;
	}
	public String getPotency_unit() {
		return potency_unit;
	}
	public String getPotency_code() {
		return potency_code;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return potency_unit;
	}
}
