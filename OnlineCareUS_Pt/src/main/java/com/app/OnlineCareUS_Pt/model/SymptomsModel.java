package com.app.OnlineCareUS_Pt.model;

import java.util.List;

public class SymptomsModel {
	
	public String symptomId;
	public String symptomName;
	public List<ConditionsModel> conditionsModelList;


    public SymptomsModel(String symptomId, String symptomName, List<ConditionsModel> conditionsModelList) {
        this.symptomId = symptomId;
        this.symptomName = symptomName;
        this.conditionsModelList = conditionsModelList;
    }

    public SymptomsModel() {
    }


    //@androidx.annotation.NonNull
    @Override
    public String toString() {
        return symptomName;
    }
}
