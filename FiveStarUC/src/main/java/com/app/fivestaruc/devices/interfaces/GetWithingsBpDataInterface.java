package com.app.fivestaruc.devices.interfaces;

import java.util.ArrayList;

import com.app.fivestaruc.devices.beanclasses.BpHeartRateValuesBean;


/**
 * Created by aftab on 15/08/2016.
 */

public  interface GetWithingsBpDataInterface {

         ArrayList<BpHeartRateValuesBean> getDataList();
}
