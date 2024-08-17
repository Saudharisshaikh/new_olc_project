package com.onlinecare.model;

import java.util.List;

public class CovidFormItemBean {

    public String label;
    public List<CovidOptionBean> options;

    public boolean isAnswered;

    public String selectedAns = "";
    public boolean isCellVisible;


    public CovidFormItemBean() {}

    public CovidFormItemBean(String label, List<CovidOptionBean> options) {
        this.label = label;
        this.options = options;
    }




    public class CovidOptionBean{
        public String optionTxt;
        public boolean isSelected;

        public CovidOptionBean(String optionTxt) {
            this.optionTxt = optionTxt;
        }

    }
}
