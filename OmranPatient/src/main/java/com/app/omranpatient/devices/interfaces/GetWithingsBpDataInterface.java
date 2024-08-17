package com.app.omranpatient.devices.interfaces;

import java.util.ArrayList;

import com.app.omranpatient.devices.beanclasses.BpHeartRateValuesBean;


/**
 * Created by aftab on 15/08/2016.
 */

public  interface GetWithingsBpDataInterface {

         ArrayList<BpHeartRateValuesBean> getDataList();
}
