package com.digihealthcard.util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;


import com.digihealthcard.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ShareScreenshot {

    Activity activity;

    public ShareScreenshot(Activity activity) {
        this.activity = activity;
    }

    public Bitmap getScreenShot(View view) {
        /*View screenView = view;//view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;*/

        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight() , Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public String store(Bitmap bm){
        //final String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        final String dirPath = activity.getApplicationContext().getFilesDir() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }
        String fileName = "Img"+System.currentTimeMillis()+".jpg";
        File file = new File(dirPath, fileName);
        String filePath = null;
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();

            filePath = file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("-- Image saved on path: "+filePath);
        return filePath;
    }

    public void shareImage(File file){
        //Uri uri = Uri.fromFile(file);
        Uri uri = FileProvider.getUriForFile(activity, activity.getApplicationContext().getPackageName() + ".fileprovider", file);
        Intent intentShareFile = new Intent();
        intentShareFile.setAction(Intent.ACTION_SEND);
        intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intentShareFile.setType("image/*");

        intentShareFile.putExtra(Intent.EXTRA_SUBJECT, activity.getResources().getString(R.string.app_name)+ " Print Card");
        intentShareFile.putExtra(Intent.EXTRA_TEXT, activity.getResources().getString(R.string.app_name)+ " Print Card");
        intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);
        /*try {
            startActivity(Intent.createChooser(intent, "Share Receipt"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(activity, "No App Available", Toast.LENGTH_SHORT).show();
        }*/
        Intent chooser = Intent.createChooser(intentShareFile, "Share File");
        List<ResolveInfo> resInfoList = activity.getPackageManager().queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            activity.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        activity.startActivity(chooser);
    }


    /*ShareScreenshot shareScreenshot = new ShareScreenshot(activity);
                Bitmap bitmap = shareScreenshot.getScreenShot(viewRec);
                String path = shareScreenshot.store(bitmap);
                if(path != null){
                    shareScreenshot.shareImage(new File(path));
                }*/
}
