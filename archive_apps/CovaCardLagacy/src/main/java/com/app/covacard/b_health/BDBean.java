package com.app.covacard.b_health;

import java.util.List;

public class BDBean {

    public List<String> bdOptions;
    public int score;
    public int seletedRadioIndex = -1;
    public boolean isGroupSelected;


    public BDBean(List<String> bdOptions) {
        this.bdOptions = bdOptions;
    }
}
