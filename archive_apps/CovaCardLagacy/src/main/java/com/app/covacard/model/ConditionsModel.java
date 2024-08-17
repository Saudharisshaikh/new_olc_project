package com.app.covacard.model;

public class ConditionsModel {

	public String conditionId = "";
	public String symptomId = "";
	public String conditionName = "";

    public ConditionsModel(String conditionId, String symptomId, String conditionName) {
        this.conditionId = conditionId;
        this.symptomId = symptomId;
        this.conditionName = conditionName;
    }


    public ConditionsModel() {
    }


    //@androidx.annotation.NonNull
    @Override
    public String toString() {
        return conditionName;
    }
}
