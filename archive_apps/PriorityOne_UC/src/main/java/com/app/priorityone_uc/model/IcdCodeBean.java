package com.app.priorityone_uc.model;

/**
 * Created by Engr G M on 7/19/2018.
 */

public class IcdCodeBean {

    public String id;
    public String code;
    public String desc;


    public IcdCodeBean(String id, String code, String desc) {
        this.id = id;
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return "("+code+") "+desc;
        //return desc+" ("+code+")";
    }
}
