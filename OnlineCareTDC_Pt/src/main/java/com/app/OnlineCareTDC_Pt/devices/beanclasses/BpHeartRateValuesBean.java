package com.app.OnlineCareTDC_Pt.devices.beanclasses;

/**
 * Created by aftab on 15/08/2016.
 */

public  class BpHeartRateValuesBean {

    int systolic, dystolic;
    int heartRate;
    String date;


    public int getSystolic() {
        return systolic;
    }

    public void setSystolic(int systolic) {
        this.systolic = systolic;
    }

    public int getDystolic() {
        return dystolic;
    }

    public void setDystolic(int dystolic) {
        this.dystolic = dystolic;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
