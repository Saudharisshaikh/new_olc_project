package com.app.onlinecare.util;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import com.baoyz.actionsheet.ActionSheet;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;

import java.io.File;
import java.util.List;

import static com.app.onlinecare.util.ChoosePictureDialog.REQUEST_IMAGE_CAPTURE;

/**
 * Created by Engr G M on 12/15/2017.
 */

public class ActionSheetPopup implements ActionSheet.ActionSheetListener{

    AppCompatActivity activity;

    public ActionSheetPopup(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void showActionSheet(){
        /*if(activity instanceof ActivityMyChannel){

        }else{

        }*/
        ActionSheet.createBuilder(activity, activity.getSupportFragmentManager())
                .setCancelButtonTitle("Cancel")
                .setOtherButtonTitles("Capture By Camera","Pick From Gallery")
                .setCancelableOnTouchOutside(true)
                .setListener(this).show();

    }
    @Override
    public void onDismiss(ActionSheet actionSheet, boolean isCancel) {
        //Toast.makeText(activity, "dismissed isCancle = " + isCancel, 0).show();
    }

    @Override
    public void onOtherButtonClick(ActionSheet actionSheet, int index) {
        //Toast.makeText(activity, "click item index = " + index, 0).show();
        if(index == 0){
            dispatchTakePictureIntent();
        }else if(index == 1){
            Intent intent = new Intent(activity, AlbumSelectActivity.class);
            intent.putExtra(Constants.INTENT_EXTRA_LIMIT, 15);
            activity.startActivityForResult(intent, Constants.REQUEST_CODE);
        }
    }


    public static Uri outputFileUriVV;
    public static String capturedImagePath;
    private void dispatchTakePictureIntent() {

        long tim = System.currentTimeMillis();
        String imgName = "image_"+tim+".jpg";

        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File op = new File(dir, imgName);

        //outputFileUriVV = Uri.fromFile(op);//get uri from file provider  ...Android 26 GM

        outputFileUriVV = FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", op);
        capturedImagePath = op.getAbsolutePath();

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUriVV);

        //===============android19
        List<ResolveInfo> resolvedIntentActivities = activity.getPackageManager().queryIntentActivities(takePictureIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolvedIntentInfo : resolvedIntentActivities) {
            String packageName = resolvedIntentInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, outputFileUriVV, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //context.revokeUriPermissionfileUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //==============android19

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    /*new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("Confirm").setMessage("Are you sure ? Do you want to delete video ?")
                    .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            RequestParams params = new RequestParams();
                            params.put("rowid", userVideoBean.id);
                            ApiManager apiManager = new ApiManager(ApiManager.DELETE_VIDEO,"post",params,SingleUserVideoAdapter.this, activity);
                            apiManager.loadURL();
                        }
                    })
                    .setNegativeButton("Cancel",null).create().show();*/
}
