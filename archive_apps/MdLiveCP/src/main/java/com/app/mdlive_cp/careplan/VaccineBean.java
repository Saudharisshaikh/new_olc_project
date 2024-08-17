package com.app.mdlive_cp.careplan;

public class VaccineBean {

    public String id;
    public String vaccine_name;
    public String parent_id;


    public VaccineBean(String id, String vaccine_name, String parent_id) {
        this.id = id;
        this.vaccine_name = vaccine_name;
        this.parent_id = parent_id;
    }

    @Override
    public String toString() {
        return vaccine_name;
    }
}
