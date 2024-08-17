package com.app.emcurauc.util;

import com.app.emcurauc.model.Organization;
import com.app.emcurauc.model.PatientInsuranceModel;

public interface NewInsuranceListener {

    public void selectedInsurance(PatientInsuranceModel patientInsuranceModel);
    public void unSelectedInsurance(PatientInsuranceModel patientInsuranceModel);
}
