package com.app.OnlineCareTDC_Pt.b_health.assessment.new_assesment;

import java.util.List;

public class OCDFormBean {


    public String question;
    public List<OCDoptionBean> options;

    public List<Integer> scores;//for stress_questioary form section 4 only

    public boolean isAnswered;
    public int score;

    public String selectedAns = "";//for stress_questioary form section 4 only - selected radio txt - may be used


    public OCDFormBean() { }

    public OCDFormBean(String question, List<OCDoptionBean> options) {//, List<Integer> scores
        this.question = question;
        this.options = options;
        //this.scores = scores;
        this.scores = null;//no need but double check b/c scores != null used in score calculation in LvOCDoptionAdapter
    }

    public OCDFormBean(String question, List<OCDoptionBean> options, List<Integer> scores) {
        this.question = question;
        this.options = options;
        this.scores = scores;
    }



    public class OCDoptionBean{
        public String optionTxt;
        public boolean isSelected;

        public OCDoptionBean(String optionTxt) {
            this.optionTxt = optionTxt;
        }

    }
}
