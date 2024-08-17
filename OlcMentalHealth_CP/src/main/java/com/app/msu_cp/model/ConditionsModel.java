package com.app.msu_cp.model;

public class ConditionsModel {

	public String conditionId = "";
	public String symptomId = "";
	public String conditionName = "";

	public ConditionsModel() {
	}

	public ConditionsModel(String conditionId, String symptomId, String conditionName) {
		this.conditionId = conditionId;
		this.symptomId = symptomId;
		this.conditionName = conditionName;
	}

	@Override
	public String toString() {
		return conditionName;
	}
}
