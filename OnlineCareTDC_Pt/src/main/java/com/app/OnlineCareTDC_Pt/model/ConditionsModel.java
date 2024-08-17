package com.app.OnlineCareTDC_Pt.model;

public class ConditionsModel {

	public String conditionId = "";
	public String symptomId = "";
	public String conditionName = "";
    public String langSpanishCondName;

    public boolean isEnglish = true;

    public ConditionsModel(String conditionId, String symptomId, String conditionName, String langSpanishCondName) {
        this.conditionId = conditionId;
        this.symptomId = symptomId;
        this.conditionName = conditionName;
        this.langSpanishCondName = langSpanishCondName;
    }


    public ConditionsModel() {
    }


    //@androidx.annotation.NonNull
    @Override
    public String toString() {
        if(isEnglish){
            return conditionName;
        }else {
            return langSpanishCondName;
        }
    }
}
