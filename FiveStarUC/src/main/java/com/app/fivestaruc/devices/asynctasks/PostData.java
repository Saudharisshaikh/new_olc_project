package com.app.fivestaruc.devices.asynctasks;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by aftab on 06/08/2016.
 */

import android.os.AsyncTask;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.app.fivestaruc.devices.customclasses.LocalSharedPreferences;
import com.app.fivestaruc.devices.interfaces.FetchDataCallbackInterface;
import com.app.fivestaruc.util.DATA;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;


/**
 * Allows to fetch string data from a server, given the url and a callable.
 * The callable is an object of a class implementing the FetchDataCallbackInterface
 * which defines the callback method fetchDataCallback
 */
public class PostData extends AsyncTask<String,String,String> {

    private static final String TAG = "PostData";

    private HttpsURLConnection urlConnection;
    private String url;
    private FetchDataCallbackInterface callbackInterface;
    private LocalSharedPreferences lsp;
    SharedPreferences prefs;

    /**
     * Constructor
     * @param url url to hit
     * @param callbackInterface class which defines the callback method
     */

    public PostData(AppCompatActivity appCompatActivity, String url, FetchDataCallbackInterface callbackInterface) {
        this.url = url;
        this.callbackInterface = callbackInterface;

        lsp = new LocalSharedPreferences(appCompatActivity);
        prefs = appCompatActivity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Override
    protected String doInBackground(String... args) {

        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(this.url);

            urlConnection = (HttpsURLConnection) url.openConnection();


                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(false);

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (this.url.contains("addUserDevice")) {

                params.add(new BasicNameValuePair("user_id", prefs.getString("id", "")));
                params.add(new BasicNameValuePair("device_id", lsp.getSelectedDeviceIdForOauth() + ""));
                params.add(new BasicNameValuePair("user_device_id", lsp.getWithingsUserId()));
                params.add(new BasicNameValuePair("oauth_token", lsp.getWithingsBpOAuthToken()));
                params.add(new BasicNameValuePair("oauth_secret", lsp.getWithingsBpOauthTokenSecret()));

            }
            else if (this.url.contains("myDevices")){

                params.add(new BasicNameValuePair("user_id", prefs.getString("id", "")));

            }


                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

            urlConnection.connect();


           int responseCode = urlConnection.getResponseCode();
            Log.e(TAG,"--post data response code = "+responseCode);

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

        if (this.url.contains("addUserDevice")) {

            this.callbackInterface.fetchDataCallback(result + "addUserDevice");
        }
        else if (this.url.contains("myDevices")){
            this.callbackInterface.fetchDataCallback(result + "myDevices");

        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params)
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

}


