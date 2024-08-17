package com.app.mhcsn_ma.model;

/**
 * Created by Engr G M on 8/31/2017.
 */

public class HomeCareFieldBean {

    public String key;
    public String value;

    public boolean isChecked;
    public String textField;

    public HomeCareFieldBean(String key, String value, boolean isChecked, String textField) {
        this.key = key;
        this.value = value;
        this.isChecked = isChecked;
        this.textField = textField;
    }
}
