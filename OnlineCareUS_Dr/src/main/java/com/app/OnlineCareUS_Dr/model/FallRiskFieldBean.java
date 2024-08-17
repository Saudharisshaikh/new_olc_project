package com.app.OnlineCareUS_Dr.model;

/**
 * Created by Engr G M on 9/3/2017.
 */

public class FallRiskFieldBean {

    public String key;
    public String value;
    public String radioValue;//n/a = 2, yes = 1, 0 = no

    public FallRiskFieldBean(String key, String value, String radioValue) {
        this.key = key;
        this.value = value;
        this.radioValue = radioValue;
    }
}
