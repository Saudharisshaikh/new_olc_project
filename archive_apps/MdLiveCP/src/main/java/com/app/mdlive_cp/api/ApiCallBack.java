package com.app.mdlive_cp.api;

/**
 * Created by aftab on 06/08/2016.
 */

/**
 * Interface defining a callable to be used as callback when fetching server data
 */
public interface ApiCallBack {
    // method called when server's data get fetched
    public void fetchDataCallback(String httpStatus, String apiName, String content);
}
