package com.app.Olc_MentalHealth.devices.interfaces;

import java.util.ArrayList;

import com.app.Olc_MentalHealth.devices.beanclasses.BpHeartRateValuesBean;


/**
 * Created by aftab on 15/08/2016.
 */

public  interface GetWithingsBpDataInterface {

         ArrayList<BpHeartRateValuesBean> getDataList();
}
