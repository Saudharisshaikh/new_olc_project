package com.app.OnlineCareUS_Dr.model;

/**
 * Created by Engr G M on 3/29/2018.
 */

public class CallParticipant {

    public String to_id;
    public String callto;
    public String join_user;

    public CallParticipant(String to_id, String callto, String join_user) {
        this.to_id = to_id;
        this.callto = callto;
        this.join_user = join_user;
    }

    @Override
    public String toString() {
        return join_user;
    }
}
