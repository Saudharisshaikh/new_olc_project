package com.app.mhcsn_spe.model;

/**
 * Created by Engr G M on 5/16/2017.
 */

public class GroupBean {

    public String id;
    public String group_name;
    public String created_by;
    public String created_date;

    public GroupBean(String id, String group_name, String created_by, String created_date) {
        this.id = id;
        this.group_name = group_name;
        this.created_by = created_by;
        this.created_date = created_date;
    }

    @Override
    public String toString() {
        return group_name;
    }
}
