package com.app.OnlineCareTDC_Pt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
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
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.app.OnlineCareTDC_Pt.adapter.LabRequestAdapter;
import com.app.OnlineCareTDC_Pt.api.ApiCallBack;
import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Pt.util.ChoosePictureDialog;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.ImageCompression;
import com.app.OnlineCareTDC_Pt.util.OpenActivity;
import com.app.OnlineCareTDC_Pt.util.UriUtilGM;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;

public class ActivityUploadLabRequest extends BaseActivity {


    ImageView ivResImg, ivDeleteVCfrontImg;
    Button btnSaveCard, btnNotNow;

    Activity activity;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;

    public static final int IMAGE_PICKER_CODE13 = 422;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_lab_request);

        activity = ActivityUploadLabRequest.this;
        apiCallBack = this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        openActivity = new OpenActivity(activity);

        ivResImg = findViewById(R.id.ivResImg);
        ivDeleteVCfrontImg = findViewById(R.id.ivDeleteVCfrontImg);

        btnSaveCard = findViewById(R.id.btnSaveCard);
        btnNotNow = findViewById(R.id.btnNotNow);


        View.OnClickListener onClickListener = view -> {

            //dialog.dismiss();

            switch (view.getId()) {
                case R.id.ivResImg:
                    //callPicCardImgMethod(1, ActivityAddTestResults.this);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_PICKER_CODE13);
                    } else if (checkPermission()) {
                        FilePickerBuilder.getInstance()
                                .setMaxCount(1)
                                .setSelectedFiles(photoPaths)
                                .setActivityTheme(R.style.FilePickerTheme)
                                .setActivityTitle("Please select a card picture")
                                .enableVideoPicker(false)
                                .enableCameraSupport(true)
                                .showGifs(false)
                                .showFolderView(false)
                                .enableSelectAll(true)
                                .enableImagePicker(true)
                                .setCameraPlaceholder(R.drawable.custom_camera)
                                .withOrientation(Orientation.PORTRAIT_ONLY)
                                .pickPhoto(activity, DATA.CUSTOM_REQUEST_CODE);
                    } else {
                        requestPermissions();
                    }
                    break;
                case R.id.ivDeleteVCfrontImg:
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to delete this file")
                            .setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    vacCardFrontImgPath = "";
                                    ivDeleteVCfrontImg.setVisibility(View.GONE);
                                    ivResImg.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
                                    ivResImg.setScaleType(ImageView.ScaleType.CENTER);
                                }
                            })
                            .setNegativeButton("Not Now", null)
                            .create().show();
                    break;


                case R.id.btnSaveCard:

                    //String result = resultArray[spResult.getSelectedItemPosition()];

                    boolean validate = true;
                    if (TextUtils.isEmpty(vacCardFrontImgPath)) {
                        customToast.showToast("Please add a picture of your result.", 0, 0);
                        validate = false;
                    }

                    if (validate) {
                        RequestParams params = new RequestParams();
                        params.put("patient_id", prefs.getString("id", ""));
                        params.put("id", LabRequestAdapter.labId);

                        try {
                            params.put("report_pic", new File(vacCardFrontImgPath));
                            //params.put("back_pic", new File(vacCardFrontImgPath));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        ApiManager apiManager = new ApiManager(ApiManager.UPLOAD_LAB_REQUEST_RESULT, "post", params, apiCallBack, activity);
                        apiManager.loadURL();
                    }

                    break;

                case R.id.btnNotNow:
                    finish();
                    break;

                default:
                    AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Comming soon")
                            .setMessage("This feature will be available in the near future")
                            .setPositiveButton("Done", null).create();
                    alertDialog.show();
                    break;
            }
        };
        ivResImg.setOnClickListener(onClickListener);
        ivDeleteVCfrontImg.setOnClickListener(onClickListener);
        btnSaveCard.setOnClickListener(onClickListener);
        btnNotNow.setOnClickListener(onClickListener);
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if (apiName.equalsIgnoreCase(ApiManager.UPLOAD_LAB_REQUEST_RESULT)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                String status = jsonObject.getString("status");
                if (status.equalsIgnoreCase("success")) {
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                    .setTitle("Info")
                                    .setMessage("Your results uploaded successfully")
                                    .setPositiveButton("Done", null)
                                    .create();
                    alertDialog.setOnDismissListener(dialog -> {
                        finish();
                    });
                    alertDialog.show();
                } else {
                    //{"error":1,"message":"Please attach test picture"}
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                            .setTitle("Error")
                            .setMessage(jsonObject.optString("message", DATA.CMN_ERR_MSG))
                            .setPositiveButton("Done", null)
                            .create().show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    private String vacCardFrontImgPath = "";//, vacCardBackImgPath = "";

    private ArrayList<String> photoPaths = new ArrayList<>();
    private ArrayList<String> docPaths = new ArrayList<>();//not using doc yet

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        photoPaths = new ArrayList<>();
        docPaths = new ArrayList<>();
        switch (requestCode) {
            case DATA.CUSTOM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    photoPaths = new ArrayList<>();
                    photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                }
                break;
            case FilePickerConst.REQUEST_CODE_DOC:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    docPaths = new ArrayList<>();
                    docPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS));
                }
                break;
            case IMAGE_PICKER_CODE13: {
                if (resultCode == activity.RESULT_OK && data != null) {
                    Uri uri = null;
                    if (data != null) {

                        uri = data.getData();
                        File file = new File(UriUtilGM.getRealPathFromUri(activity, getContentResolver(), uri));
                        photoPaths = new ArrayList<>();
                        DATA.print("-- photoPaths data 13 FIle  " + file);
                        photoPaths.add(String.valueOf(file));
                    }
                }
            }
        }
        //addThemToView(photoPaths, docPaths);
        ArrayList<String> filePaths = new ArrayList<>();
        if (photoPaths != null) filePaths.addAll(photoPaths);
        if (docPaths != null) filePaths.addAll(docPaths);
        System.out.println("--total files: " + filePaths.size());
        if (!filePaths.isEmpty()) {
            String filePath = filePaths.get(0);

            /*File rotatedImg = ImageRotator.Companion.prepareAndStoreImageFile(new File(filePath), new File(filePath));
			System.out.println("-- Image Rotated "+rotatedImg.getAbsolutePath());
            filePath = rotatedImg.getAbsolutePath();*/
            String compressImagePath = new ImageCompression(activity).ImageCompression(filePaths.get(0));
            displayICfrontImg(compressImagePath);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void displayICfrontImg(String imgPath) {
        try {
            vacCardFrontImgPath = imgPath;
            ivDeleteVCfrontImg.setVisibility(View.VISIBLE);

            File imageFile = new File(vacCardFrontImgPath);
            String uri = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName() + ".fileprovider", imageFile).toString();
            final String decoded = Uri.decode(uri);
            DATA.loadImageFromURL(decoded, R.drawable.ic_placeholder_2, ivResImg);
            ivResImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*============ Check Permission Code ===================*/
    private String[] PERMISSIONS = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private static final int PERMISSION_REQ_CODE = 1 << 4;

    private boolean checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        if (granted) {

        } else {
            requestPermissions();
        }
        return granted;
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(activity, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {

            } else {
                showDeniedResponse(grantResults);
                // customToast.showToast(getResources().getString(R.string.need_necessary_permissions),0,0);
            }
        }
    }


    private void showDeniedResponse(int[] grantResults) {

        boolean shouldShowDialog = false;
        String msg = getString(R.string.allow_camera_permission_msg);

        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(activity, "Permission not granted for: "+permissionFeatures.values()[i], Toast.LENGTH_SHORT).show();
                //msg = msg + "\n* "+permissionFeatures.values()[i];
                shouldShowDialog = true;
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
                            /*checkPermissions();*/
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

    private void openAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }
    /*============ Check Permission Code ===================*/
}