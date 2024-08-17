package com.app.amnm_ma.model;

/**
 * Created by Engr G M on 3/29/2017.
 */

public class ProblemBean {

    public String dateof;
    public String symptom_name;
    public String condition_name;

    public ProblemBean(String dateof, String symptom_name, String condition_name) {
        this.dateof = dateof;
        this.symptom_name = symptom_name;
        this.condition_name = condition_name;
    }
}
