package com.app.msu_cp.reliance.assessment;

import java.util.List;

public class AdlFormBean {


    public String question;
    public List<ADLoptionBean> options;
    public List<Integer> scores;

    public boolean isAnswered;
    public int score;
    public String selectedAns = "";//for IADL form submit


    public AdlFormBean() { }

    public AdlFormBean(String question, List<ADLoptionBean> options, List<Integer> scores) {
        this.question = question;
        this.options = options;
        this.scores = scores;
    }



    public class ADLoptionBean{
        public String optionTxt;
        public boolean isSelected;

        public ADLoptionBean(String optionTxt) {
            this.optionTxt = optionTxt;
        }

    }
}
