package com.app.emcuradr.model;

/**
 * Created by Engr G M on 5/17/2017.
 */

public class GroupMessageBean {

    public String id;
    public String group_id;
    public String message_text;
    public String user_type;
    public String user_id;
    public String dateof;
    public String is_read;
    public String uname;
    public String image;

    public GroupMessageBean(String id, String group_id, String message_text, String user_type, String user_id, String dateof, String is_read, String uname, String image) {
        this.id = id;
        this.group_id = group_id;
        this.message_text = message_text;
        this.user_type = user_type;
        this.user_id = user_id;
        this.dateof = dateof;
        this.is_read = is_read;
        this.uname = uname;
        this.image = image;
    }
}
