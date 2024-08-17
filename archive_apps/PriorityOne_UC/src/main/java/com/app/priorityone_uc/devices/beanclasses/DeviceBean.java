package com.app.priorityone_uc.devices.beanclasses;

/**
 * Created by aftab on 06/08/2016.
 */

public class DeviceBean {

    int deviceId,onlineCareUserId,deviceDrawableId;
    String deviceName,deviceImageUrl,oAuthToken,oAuthSecret;
    String deviceUserId;

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceImageUrl() {
        return deviceImageUrl;
    }

    public void setDeviceImageUrl(String deviceImageUrl) {
        this.deviceImageUrl = deviceImageUrl;
    }

    public int getDeviceDrawableId() {
        return deviceDrawableId;
    }

    public void setDeviceDrawableId(int deviceDrawableId) {
        this.deviceDrawableId = deviceDrawableId;
    }

    public int getOnlineCareUserId() {
        return onlineCareUserId;
    }

    public void setOnlineCareUserId(int onlineCareUserId) {
        this.onlineCareUserId = onlineCareUserId;
    }

    public String getDeviceUserId() {
        return deviceUserId;
    }

    public void setDeviceUserId(String deviceUserId) {
        this.deviceUserId = deviceUserId;
    }

    public String getoAuthToken() {
        return oAuthToken;
    }

    public void setoAuthToken(String oAuthToken) {
        this.oAuthToken = oAuthToken;
    }

    public String getoAuthSecret() {
        return oAuthSecret;
    }

    public void setoAuthSecret(String oAuthSecret) {
        this.oAuthSecret = oAuthSecret;
    }
}
