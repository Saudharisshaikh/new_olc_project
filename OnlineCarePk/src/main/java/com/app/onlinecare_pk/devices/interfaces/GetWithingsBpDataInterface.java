package com.app.onlinecare_pk.devices.interfaces;

import java.util.ArrayList;

import com.app.onlinecare_pk.devices.beanclasses.BpHeartRateValuesBean;


/**
 * Created by aftab on 15/08/2016.
 */

public  interface GetWithingsBpDataInterface {

         ArrayList<BpHeartRateValuesBean> getDataList();
}
