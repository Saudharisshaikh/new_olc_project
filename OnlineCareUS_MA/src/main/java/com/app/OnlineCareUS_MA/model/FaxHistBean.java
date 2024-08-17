package com.app.OnlineCareUS_MA.model;

/**
 * Created by Engr G M on 10/2/2017.
 */

public class FaxHistBean {

    public String id;
    public String name;
    public String email;
    public String fax_number;

    public FaxHistBean(String id, String name, String email, String fax_number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.fax_number = fax_number;
    }

    @Override
    public String toString() {
        return name;
    }
}
