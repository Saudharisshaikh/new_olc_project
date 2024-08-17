package com.app.fivestaruc.devices.asynctasks;

/**
 * Created by aftab on 06/08/2016.
 */

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.app.fivestaruc.devices.interfaces.FetchDataCallbackInterface;


/**
 * Allows to fetch string data from a server, given the url and a callable.
 * The callable is an object of a class implementing the FetchDataCallbackInterface
 * which defines the callback method fetchDataCallback
 */
public class FetchData extends AsyncTask<String,String,String> {

    private HttpURLConnection urlConnection;
    private String url;
    private FetchDataCallbackInterface callbackInterface;

    /**
     * Constructor
     * @param url url to hit
     * @param callbackInterface class which defines the callback method
     */

    public FetchData(String url, FetchDataCallbackInterface callbackInterface) {
        this.url = url;
        this.callbackInterface = callbackInterface;
    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(this.url);

            urlConnection = (HttpURLConnection) url.openConnection();

            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;

            while ((line = reader.readLine()) != null) {
                result.append(line);

            }
        } catch (Exception e) {
            e.printStackTrace();

            return null;

        } finally {
            urlConnection.disconnect();
        }
        return result.toString();
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        // pass the result to the callback function
        this.callbackInterface.fetchDataCallback(result);
    }


}


