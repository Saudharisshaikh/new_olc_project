package com.app.mhcsn_spe.reliance.assessment;

import java.util.List;

public class PHQfieldBean {


    public String question;
    public List<String> options;

    public int score;
    public int seletedRadioIndex = -1;
    public boolean isGroupSelected;


    public PHQfieldBean(String question, List<String> options) {
        this.question = question;
        this.options = options;
    }
}
