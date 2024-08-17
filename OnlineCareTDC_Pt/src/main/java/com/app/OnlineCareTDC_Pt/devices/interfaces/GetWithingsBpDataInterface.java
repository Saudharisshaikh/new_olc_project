package com.app.OnlineCareTDC_Pt.devices.interfaces;

import java.util.ArrayList;

import com.app.OnlineCareTDC_Pt.devices.beanclasses.BpHeartRateValuesBean;


/**
 * Created by aftab on 15/08/2016.
 */

public  interface GetWithingsBpDataInterface {

         ArrayList<BpHeartRateValuesBean> getDataList();
}
