package com.app.msu_dr.permission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;

public class PermissionsChecker {
    private final Context context;

    public PermissionsChecker(Context context) {
        this.context = context;
    }

    public boolean lacksPermissions(String... permissions) {
        for (String permission : permissions) {
            if (lacksPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public boolean lacksPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_DENIED;
    }



    public static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE};


    public enum permissionFeatures {
        CAMERA,
        MICROPHONE,
        LOCATION,
        STORAGE,
        PHONE,
        CALL
    }

    public static final String[] PERMISSIONANDROID12 = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE};

    public enum permissionFeatures12 {
        CAMERA,
        MICROPHONE,
        LOCATION,
        BLUETOOTH_CONNECT,
        STORAGE,
        PHONE,
        CALL
    }

    public static final String[] PERMISSIONSANDROID13 = {
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.POST_NOTIFICATIONS,
            //Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE};

    public enum permissionFeaturesAndroid13 {
        CAMERA,
        MICROPHONE,
        LOCATION,
        POST_NOTIFICATIONS,
        READ_MEDIA_VIDEO,
        READ_MEDIA_IMAGES,
        BLUETOOTH_CONNECT,
        //STORAGE,
        PHONE,
        CALL
    }
}