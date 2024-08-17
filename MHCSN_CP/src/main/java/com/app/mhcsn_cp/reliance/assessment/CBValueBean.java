package com.app.mhcsn_cp.reliance.assessment;

/**
 * Created by Engr G M on 6/30/2018.
 */

public class CBValueBean {


    public String value;

    public boolean selectedByUSer = false;


    public CBValueBean(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
