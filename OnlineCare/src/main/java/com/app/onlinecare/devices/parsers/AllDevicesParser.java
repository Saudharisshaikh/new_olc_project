package com.app.onlinecare.devices.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.onlinecare.devices.beanclasses.DeviceBean;

import java.util.ArrayList;


/**
 * Created by aftab on 16/08/2016.
 */

public class AllDevicesParser extends Parser<DeviceBean> {

    private static final String TAG = "MyDevicesParser";

    public AllDevicesParser(String result) {
        super(result);
    }


    @Override
    public void getData(ArrayList<DeviceBean> list) {

        try
        {
            JSONArray jsonArray = new JSONArray(result);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                DeviceBean data = new DeviceBean();
                data.setDeviceId(jsonObject.getString("id").equals("null") ? 0 : jsonObject.getInt("id"));
                data.setDeviceName(jsonObject.getString("name"));
                data.setDeviceImageUrl(jsonObject.getString("thumbnail"));

                Log.e(TAG,"parsing devices = "+data.getDeviceName());

                list.add(data);
            }
        } catch (JSONException e)
        {
            Log.e(TAG, e.toString());
        }

    }
}
