package com.app.mdlive_cp.reliance.assessment;

import java.util.ArrayList;

public class DietitaryAssesFormBean {

    public String label;
    public String key;
    public String type;
    public ArrayList<String> options;
    public String code;

    public String inputValue = "";
    public int spPosition;//for spinner
    public ArrayList<CBValueBean> cbValueBeans;//for checkboxes


    public DietitaryAssesFormBean(String label, String key, String type, ArrayList<String> options, String code) {
        this.label = label;
        this.key = key;
        this.type = type;
        this.options = options;
        this.code = code;
    }


}
