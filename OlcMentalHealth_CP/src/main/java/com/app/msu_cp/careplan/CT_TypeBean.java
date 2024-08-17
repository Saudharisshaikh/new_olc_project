package com.app.msu_cp.careplan;

/**
 * Created by Engr G M on 7/7/2018.
 */

public class CT_TypeBean {

    public String kay;
    public String value;

    public CT_TypeBean(String kay, String value) {
        this.kay = kay;
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
