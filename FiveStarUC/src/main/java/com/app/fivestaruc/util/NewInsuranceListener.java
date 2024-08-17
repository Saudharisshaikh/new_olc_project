package com.app.fivestaruc.util;


import com.app.fivestaruc.model.PatientInsuranceModel;

public interface NewInsuranceListener {

    public void selectedInsurance(PatientInsuranceModel patientInsuranceModel);
    public void unSelectedInsurance(PatientInsuranceModel patientInsuranceModel);
}
