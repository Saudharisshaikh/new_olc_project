package com.app.omranpatient.devices.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.omranpatient.devices.beanclasses.DeviceBean;

import java.util.ArrayList;


/**
 * Created by aftab on 16/08/2016.
 */

public class MyDevicesParser extends Parser<DeviceBean> {

    private static final String TAG = "MyDevicesParser";

    public MyDevicesParser(String result) {
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

                data.setoAuthSecret(jsonObject.getString("oauth_secret"));
                data.setoAuthToken(jsonObject.getString("oauth_token"));
                data.setDeviceUserId(jsonObject.getString("user_device_id"));

                Log.e(TAG,"parsing my devices = "+data.getDeviceName());

                list.add(data);
            }
        } catch (JSONException e)
        {
            Log.e(TAG, e.toString());
        }

    }
}
