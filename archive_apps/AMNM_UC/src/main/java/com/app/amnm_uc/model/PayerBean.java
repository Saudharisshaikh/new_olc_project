package com.app.amnm_uc.model;

/**
 * Created by Engr G M on 2/20/2017.
 */

public class PayerBean {

    public String id;
    public String payer_id;
    public String payer_name;

    public PayerBean(String id, String payer_id, String payer_name) {
        this.id = id;
        this.payer_id = payer_id;
        this.payer_name = payer_name;
    }

    @Override
    public String toString() {
        return payer_name;
    }
}
