package com.app.fivestardoc.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.app.fivestardoc.R;
import com.app.fivestardoc.util.DATA;


public class PermissionsActivity extends AppCompatActivity {

    Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 37110;
    private PermissionsChecker checker;

    Button btnTurnOn;

    @Override
    protected void onResume() {
        super.onResume();
        if (checker.lacksPermissions(PermissionsChecker.PERMISSIONS)) {
            // requestPermissions(PermissionsChecker.PERMISSIONS);
        } else {
            allPermissionsGranted();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_permissions);

        activity = this;
        checker = new PermissionsChecker(this);

        btnTurnOn = findViewById(R.id.btnTurnOn);
        btnTurnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissions();
            }
        });
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checker.lacksPermissions(PermissionsChecker.PERMISSIONSANDROID13)) {
                requestPermissions(PermissionsChecker.PERMISSIONSANDROID13);
            } else {
                allPermissionsGranted();
            }
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
            if (checker.lacksPermissions(PermissionsChecker.PERMISSIONANDROID12)) {
                requestPermissions(PermissionsChecker.PERMISSIONANDROID12);
            } else {
                allPermissionsGranted();
            }
        } else {
            if (checker.lacksPermissions(PermissionsChecker.PERMISSIONS)) {
                requestPermissions(PermissionsChecker.PERMISSIONS);
            } else {
                allPermissionsGranted();
            }
        }
    }

    private void requestPermissions(String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_REQUEST_CODE);
    }

    private void allPermissionsGranted() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE && hasAllPermissionsGranted(grantResults)) {
            // requiresCheck = true;
            allPermissionsGranted();
        } else {
            // requiresCheck = false;
            showDeniedResponse(grantResults);
            // finish();
        }
    }

    private void showDeniedResponse(int[] grantResults) {
        boolean shouldShowDialog = false;
        String msg = "Permission not granted for: ";

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                // Toast.makeText(activity, "Permission not granted for: "+permissionFeatures.values()[i], Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    msg = msg + "\n* " + PermissionsChecker.permissionFeaturesAndroid13.values()[i];
                    shouldShowDialog = true;
                } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.S) {
                    msg = msg + "\n* " + PermissionsChecker.permissionFeatures12.values()[i];
                    shouldShowDialog = true;
                } else {
                    msg = msg + "\n* " + PermissionsChecker.permissionFeatures.values()[i];
                    shouldShowDialog = true;
                }
            }
        }

        if (shouldShowDialog) {
            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    //.setTitle("Permission ")
                    .setMessage(msg)
                    .setNegativeButton("Allow Permissions", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            openAppSettings();
                            //checkPermissions();
                        }
                    })
                    .setPositiveButton("Not Now", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    })
                    .create().show();
        }
    }

    private boolean hasAllPermissionsGranted(@NonNull int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }

    private void openAppSettings(){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
}

