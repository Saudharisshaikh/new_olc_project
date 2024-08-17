package com.app.covacard.devices.customclasses;


import android.content.Context;
import android.content.SharedPreferences;

public class LocalSharedPreferences {
    private static Context context;

    private static final String STORAGE_NAME = "OnlineCareApp";
    private static final String IS_BP_DEVICE_ADDED = "isBPDeviceAdded";

    private static final String WITHINGS_USER_ID = "withingsUserId";
    private static final String WITHINGS_BP_OAUTH_TOKEN = "withingsBpOauthToken";
    private static final String WITHINGS_BP_OAUTH_TOKEN_SECRET = "withingsBpOauthTokenSecret";

    private static final String SELECTED_DEVICE_ID_FOR_OAUTH = "selectedDeviceId";

    public LocalSharedPreferences(Context context) {
        this.context = context;
    }


    public boolean isBPDeviceAdded() {
        return get(IS_BP_DEVICE_ADDED, false);
    }

    public void addBPDevice(boolean isBpDeviceAdded) {
        save(IS_BP_DEVICE_ADDED, isBpDeviceAdded);

    }

    public String getWithingsBpOAuthToken() {
        return get(WITHINGS_BP_OAUTH_TOKEN, "");
    }

    public void saveWithingsBpOAuthToken(String oauthToken) {
        save(WITHINGS_BP_OAUTH_TOKEN, oauthToken);

    }

    public String getWithingsBpOauthTokenSecret() {
        return get(WITHINGS_BP_OAUTH_TOKEN_SECRET, "");
    }

    public void saveWithingsBpOAuthTokenSecret(String oauthTokenSecret) {
        save(WITHINGS_BP_OAUTH_TOKEN_SECRET, oauthTokenSecret);

    }

    public int getSelectedDeviceIdForOauth() {
        return get(SELECTED_DEVICE_ID_FOR_OAUTH, 0);
    }

    public void setSelectedDeviceIdForOauth(int selectedDeviceIdForOauth) {
        save(SELECTED_DEVICE_ID_FOR_OAUTH, selectedDeviceIdForOauth);

    }


    public String getWithingsUserId() {
        return get(WITHINGS_USER_ID, "");
    }

    public void saveWithingsUserId(String withingsUserId) {
        save(WITHINGS_USER_ID, withingsUserId);

    }



    public void clearAll() {

       SharedPreferences.Editor sp = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE).edit();
        sp.clear();
        sp.apply();
    }

    private void save(String key,String value)
    {
        context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).edit().putString(key, value.trim()).commit();
    }

    private void save(String key,int value)
    {
        context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }

    private void save(String key,boolean value)
    {
        context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }


    private String get(String key,String def)
    {
        return context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).getString(key, def);
    }

    private int get(String key,int def)
    {
        return context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).getInt(key, def);
    }
    private boolean get(String key,boolean def)
    {
        return context.getSharedPreferences(STORAGE_NAME,Context.MODE_PRIVATE).getBoolean(key, def);
    }

}
