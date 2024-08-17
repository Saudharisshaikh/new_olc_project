package com.app.mhcsn_dr.model;

/**
 * Created by Engr G M on 8/28/2017.
 */

public class DepressionFieldBean {

    public String question;
    public String Yes;
    public String No;

    public boolean isChecked;


    public DepressionFieldBean(String question, String yes, String no, boolean isChecked) {
        this.question = question;
        Yes = yes;
        No = no;
        this.isChecked = isChecked;
    }
}
