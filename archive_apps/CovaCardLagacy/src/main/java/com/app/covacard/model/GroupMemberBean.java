package com.app.covacard.model;

/**
 * Created by Engr G M on 5/24/2017.
 */

public class GroupMemberBean {

    public String user_id;
    public String first_name;
    public String last_name;
    public String image;
    public String type;
    public String is_online;

    public GroupMemberBean(String user_id, String first_name, String last_name, String image,String type,String is_online) {
        this.user_id = user_id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.image = image;
        this.type = type;
        this.is_online = is_online;
    }
}
