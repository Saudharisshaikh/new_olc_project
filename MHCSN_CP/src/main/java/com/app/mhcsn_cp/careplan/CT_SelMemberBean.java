package com.app.mhcsn_cp.careplan;

/**
 * Created by Engr G M on 7/8/2018.
 */

public class CT_SelMemberBean {

    public String id;
    public String first_name;
    public String last_name;
    public String speciality_name;
    public String doctor_category;

    public CT_SelMemberBean(String id, String first_name, String last_name, String speciality_name, String doctor_category) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.speciality_name = speciality_name;
        this.doctor_category = doctor_category;
    }

    @Override
    public String toString() {
        if(!doctor_category.isEmpty()){
            return first_name+" "+last_name+" ("+doctor_category+")";
        }else if(!speciality_name.isEmpty()){
            return first_name+" "+last_name+" ("+speciality_name+")";
        }else {
            return first_name+" "+last_name;
        }
    }
}
