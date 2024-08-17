package com.app.msu_dr.model;

import androidx.annotation.NonNull;

public class AttachSurveyBean {


    public String surveyId;
    public String surveyName;
    public int surveyIconID;
    public String surveyWebviewURL;


    public AttachSurveyBean(String surveyId, String surveyName, int surveyIconID, String surveyWebviewURL) {
        this.surveyId = surveyId;
        this.surveyName = surveyName;
        this.surveyIconID = surveyIconID;
        this.surveyWebviewURL = surveyWebviewURL;
    }

    @NonNull
    @Override
    public String toString() {
        return surveyName;
    }
}
