package com.app.priorityone_uc.devices.interfaces;

/**
 * Created by aftab on 06/08/2016.
 */

/**
 * Interface defining a callable to be used as callback when fetching server data
 */
public interface FetchDataCallbackInterface {
    // method called when server's data get fetched
    public void fetchDataCallback (String result);
}
