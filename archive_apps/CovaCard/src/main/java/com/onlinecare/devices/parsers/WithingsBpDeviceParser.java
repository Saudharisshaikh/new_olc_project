package com.onlinecare.devices.parsers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.onlinecare.devices.beanclasses.BpHeartRateValuesBean;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by aftab on 16/08/2016.
 */

public class WithingsBpDeviceParser extends Parser<BpHeartRateValuesBean> {

    private static final String TAG = "BpHeartRateValuesBean";

    public WithingsBpDeviceParser(String result) {
        super(result);
    }

    @Override
    public void getData(ArrayList<BpHeartRateValuesBean> list) {

        try
        {
            String bodyJson = new JSONObject(result).getString("body");

            Log.e(TAG,"--got body json"+bodyJson);

            String measuregrpsStr = new JSONObject(bodyJson).getString("measuregrps");

            Log.e(TAG,"--got measuregrpsStr json"+measuregrpsStr);

            JSONArray jsonArray = new JSONArray(measuregrpsStr);
            for (int i=0;i<jsonArray.length();i++)
            {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                Log.e(TAG,"--got json object in measuregrpsStr array"+jsonObject);

                BpHeartRateValuesBean data = new BpHeartRateValuesBean();

                if (jsonObject.getInt("category") == 1) {


                    long timeMillis = Long.parseLong(jsonObject.getString("date"));

                    timeMillis = timeMillis*1000;

                    Date dateTime = new Date(timeMillis);
                    SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy");

                    data.setDate(fmt.format(dateTime));

                    JSONArray measures = jsonObject.getJSONArray("measures");

                    Log.e(TAG,"--got measures json array"+measures);


                    for (int j = 0; j< measures.length(); j++) {

                        if (measures.getJSONObject(j).getInt("type") == 9) { // meastype = 9 = dystolic value
                            data.setDystolic(measures.getJSONObject(j).getString("value").equals("null") ? 0 : measures.getJSONObject(j).getInt("value"));
                        }
                        else if (measures.getJSONObject(j).getInt("type") == 10) { // meastype = 10 = systolic value
                            data.setSystolic(measures.getJSONObject(j).getString("value").equals("null") ? 0 : measures.getJSONObject(j).getInt("value"));
                        }
                        else if (measures.getJSONObject(j).getInt("type") == 11) { // meastype = 11 = heart rate value
                            data.setHeartRate(measures.getJSONObject(j).getString("value").equals("null") ? 0 : measures.getJSONObject(j).getInt("value"));
                        }
                    }

                    Log.e(TAG,"parsing withings bp data = "+data.getHeartRate());

                }

                list.add(data);
            }
        } catch (JSONException e)
        {
            Log.e(TAG, e.toString());
        }

    }
}
