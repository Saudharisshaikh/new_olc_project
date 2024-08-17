package com.digihealthcard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.digihealthcard.BuildConfig;
import com.digihealthcard.R;
import com.digihealthcard.util.SharedPrefsHelper;

import org.json.JSONObject;

import java.util.Calendar;

public class ActivityBaseDrawer extends BaseActivity {


    //Toolbar mToolbar;
    ActionBarDrawerToggle mDrawerToggle;
    public DrawerLayout mDrawerLayout;
    LinearLayout left_drawer;
    public LinearLayout container_frame,mainContentFrame;
    //View vTop, vBottom;
    ImageView ivUserImage;
    TextView tvUserName,tvEmail,tvPhone,tvAddress, tvAppNameCopyRight,tvVersionName;
    LinearLayout layHome,layScanCard,layShowCard,layTestResults,layTestLocations,layIdCard,layTeleHealth,layMyProfile,layLogout,
            layPrivacy, layTerms,laySubscPolicy,layEmail_1,layEmail_2 , layAppInfo,laySubscPlans;
    RelativeLayout ll_user;
    View viewScanCardDim,viewShowCardDim,viewTestResultsDim,viewTestLocationsDim,viewIdCardDim;
    CardView cvToolbar;
    public ImageView ivToolbarBack,ivToolbarMenu, ivToolbarHome;
    public TextView tvToolbarTitle;
    public Button btnToolbarAdd;


    @Override
    protected void onResume() {
        super.onResume();

        showUserAndAppInfo();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_drawer);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getWindow().getAttributes());
        //lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        //lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;


        //mToolbar = findViewById(R.id.toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        container_frame = findViewById(R.id.container_frame);
        left_drawer = findViewById(R.id.left_drawer);
        mainContentFrame = findViewById(R.id.mainContentFrame);


        ivUserImage = findViewById(R.id.ivUserImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        tvAppNameCopyRight = findViewById(R.id.tvAppNameCopyRight);
        tvVersionName = findViewById(R.id.tvVersionName);
        layHome = findViewById(R.id.layHome);
        layScanCard = findViewById(R.id.layScanCard);
        layShowCard = findViewById(R.id.layShowCard);
        layTestResults = findViewById(R.id.layTestResults);
        layTestLocations = findViewById(R.id.layTestLocations);
        layIdCard = findViewById(R.id.layIdCard);
        layTeleHealth = findViewById(R.id.layTeleHealth);
        layMyProfile = findViewById(R.id.layMyProfile);
        layLogout = findViewById(R.id.layLogout);
        layPrivacy = findViewById(R.id.layPrivacy);
        layTerms = findViewById(R.id.layTerms);
        laySubscPolicy = findViewById(R.id.laySubscPolicy);
        layEmail_1 = findViewById(R.id.layEmail_1);
        layEmail_2 = findViewById(R.id.layEmail_2);
        ll_user = findViewById(R.id.ll_user);
        laySubscPlans = findViewById(R.id.laySubscPlans);
        layAppInfo = findViewById(R.id.layAppInfo);

        viewScanCardDim = findViewById(R.id.viewScanCardDim);
        viewShowCardDim = findViewById(R.id.viewShowCardDim);
        viewTestResultsDim = findViewById(R.id.viewTestResultsDim);
        viewTestLocationsDim = findViewById(R.id.viewTestLocationsDim);
        viewIdCardDim = findViewById(R.id.viewIdCardDim);


        cvToolbar = findViewById(R.id.cvToolbar);
        ivToolbarBack = findViewById(R.id.ivToolbarBack);
        ivToolbarMenu = findViewById(R.id.ivToolbarMenu);
        tvToolbarTitle = findViewById(R.id.tvToolbarTitle);
        btnToolbarAdd = findViewById(R.id.btnToolbarAdd);
        ivToolbarHome = findViewById(R.id.ivToolbarHome);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("-- Drawer click activity instanceof: "+activity.getClass().getSimpleName());
                if(v.getId() == R.id.layHome){

                    //openActivity.open(ActivityPackages.class, false);
                    if(activity instanceof ActivityCovacardHome){return;}
                    mDrawerLayout.closeDrawers();
                    Intent intent = new Intent(activity.getApplicationContext(), ActivityCovacardHome.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }else if(v.getId() == R.id.layScanCard){
                    if(activity instanceof ActivityAddCard){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityAddCard.class, false);
                }else if(v.getId() == R.id.layShowCard){
                    if(activity instanceof ActivityCardsList){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityCardsList.class, false);
                }else if(v.getId() == R.id.layTestResults){
                    if(activity instanceof ActivityTestResultsList){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityTestResultsList.class, false);
                }else if(v.getId() == R.id.layTestLocations){
                    if(activity instanceof ActivityTestLocations){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityTestLocations.class, false);
                }else if(v.getId() == R.id.layIdCard){
                    if(activity instanceof ActivityIdCardsList){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityIdCardsList.class, false);
                }else if(v.getId() == R.id.layTeleHealth){
                    showTeleHealthDialog();
                }else if(v.getId() == R.id.layMyProfile || v.getId() == R.id.ll_user){
                    if(activity instanceof UpdateProfile){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(UpdateProfile.class, false);
                }else if(v.getId() == R.id.laySubscPlans){
                    if(activity instanceof ActivityPackages){return;}
                    mDrawerLayout.closeDrawers();
                    openActivity.open(ActivityPackages.class, false);
                }else if(v.getId() == R.id.layLogout){
                    new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
                            .setMessage("Are you sure ? Do you want to logout ?")
                            .setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    mDrawerLayout.closeDrawers();
                                    boolean debug_logs = sharedPrefsHelper.get("debug_logs", false);
                                    boolean dummy_subscription = sharedPrefsHelper.get("dummy_subscription", false);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.clear();
                                    editor.apply();
                                    SharedPrefsHelper.getInstance().clearAllData();
                                    sharedPrefsHelper.save("debug_logs", debug_logs);
                                    sharedPrefsHelper.save("dummy_subscription", dummy_subscription);

                                    Intent intent1 = new Intent(getApplicationContext(), Login.class);
                                    intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent1);
                                }
                            })
                            .setNegativeButton("Not Now", null).create().show();
                }else if(v.getId() == R.id.layPrivacy){
                    //new GloabalMethods(activity).showWebviewDialog("https://digihealthcard.com/app/patient/privacy_policy", "Privacy Policy");
                    String url = "https://digihealthcard.com/app/patient/privacy_policy";
                    try {
                        String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
                        if(!TextUtils.isEmpty(app_general_labels)){
                            url = new JSONObject(app_general_labels).getJSONObject("data").getJSONArray("patient_registeration").getJSONObject(0).getString("url");
                            if(sharedPrefsHelper.get("debug_logs", false)){System.out.println("-- Dynamic URL : "+url);}
                        }
                    }catch (Exception e){e.printStackTrace();}

                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else if(v.getId() == R.id.layTerms){
                    //new GloabalMethods(activity).showWebviewDialog("https://digihealthcard.com/app/patient/user_agreement", "Terms and Conditions");
                    String url = "https://digihealthcard.com/app/patient/user_agreement";
                    try {
                        String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
                        if(!TextUtils.isEmpty(app_general_labels)){
                            url = new JSONObject(app_general_labels).getJSONObject("data").getJSONArray("patient_registeration").getJSONObject(1).getString("url");
                            if(sharedPrefsHelper.get("debug_logs", false)){System.out.println("-- Dynamic URL : "+url);}
                        }
                    }catch (Exception e){e.printStackTrace();}
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else if(v.getId() == R.id.laySubscPolicy){
                    //new GloabalMethods(activity).showWebviewDialog("https://digihealthcard.com/app/patient/subscription_policy", "Subscription Policy");
                    String url = "https://digihealthcard.com/app/patient/subscription_policy";
                    try {
                        String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
                        if(!TextUtils.isEmpty(app_general_labels)){
                            url = new JSONObject(app_general_labels).getJSONObject("data").getJSONArray("patient_registeration").getJSONObject(2).getString("url");
                            if(sharedPrefsHelper.get("debug_logs", false)){System.out.println("-- Dynamic URL : "+url);}
                        }
                    }catch (Exception e){e.printStackTrace();}
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }else if(v.getId() == R.id.layEmail_1){

                    String subject = getString(R.string.app_name)+" customer support";
                    String[] addresses = {"info@digihealthcard.com"};//"support@onlinecare.com"

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","info@digihealthcard.com", null));//"support@onlinecare.com"
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                }else if(v.getId() == R.id.layEmail_2){

                    String subject = getString(R.string.app_name)+" customer support";
                    String[] addresses = {"support@digihealthcard.com"};//"support@onlinecare.com"

                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto","support@digihealthcard.com", null));//"support@onlinecare.com"
                    emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    //emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));

                }else if(v.getId() == R.id.layAppInfo){
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }else if(v.getId() == R.id.ivToolbarMenu){
                    mDrawerLayout.openDrawer(left_drawer);
                }else if(v.getId() == R.id.ivToolbarHome){
                    Intent intent = new Intent(getApplicationContext(), ActivityCovacardHome.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }else if(v.getId() == R.id.ivToolbarBack){
                    onBackPressed();
                }
            }
        };
        layHome.setOnClickListener(onClickListener);
        layScanCard.setOnClickListener(onClickListener);
        layShowCard.setOnClickListener(onClickListener);
        layTestResults.setOnClickListener(onClickListener);
        layTestLocations.setOnClickListener(onClickListener);
        layIdCard.setOnClickListener(onClickListener);
        layTeleHealth.setOnClickListener(onClickListener);
        layMyProfile.setOnClickListener(onClickListener);
        layLogout.setOnClickListener(onClickListener);

        layPrivacy.setOnClickListener(onClickListener);
        layTerms.setOnClickListener(onClickListener);
        laySubscPolicy.setOnClickListener(onClickListener);
        layEmail_1.setOnClickListener(onClickListener);
        layEmail_2.setOnClickListener(onClickListener);
        ll_user.setOnClickListener(onClickListener);
        laySubscPlans.setOnClickListener(onClickListener);
        layAppInfo.setOnClickListener(onClickListener);

        ivToolbarMenu.setOnClickListener(onClickListener);
        ivToolbarHome.setOnClickListener(onClickListener);
        ivToolbarBack.setOnClickListener(onClickListener);





        /*View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.tvDashboard:
                        mDrawerLayout.closeDrawers();
                        if(activity instanceof ActivityDashboard){
                            System.out.println("activity instanceof "+activity.getClass().getSimpleName());
                            return;
                        }
                        Intent intent = new Intent(activity.getApplicationContext(), ActivityDashboard.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        break;
                    case R.id.tvFindProperty:
                        mDrawerLayout.closeDrawers();
                        if(activity instanceof ActivityFindProperty){
                            System.out.println("activity instanceof "+activity.getClass().getSimpleName());
                            return;
                        }
                        mDrawerLayout.closeDrawers();
                        openActivity.open(ActivityFindProperty.class, false);
                        break;
                    case R.id.tvLogout:
                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? Do you want to logout?")
                                .setPositiveButton(
                                        "Yes Logout", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                ApiManager2 apiManager = new ApiManager2(ApiManager2.LOGOUT_CI, "post", null, apiCallBack, activity);
                                                apiManager.loadURL();
                                            }
                                        })
                                .setNegativeButton("Not Now", null)
                                .create().show();
                        break;

                    case R.id.ivUser:
                        //mDrawerLayout.closeDrawers();
                        if(activity instanceof ActivityMyProfile){
                            System.out.println("activity instanceof "+activity.getClass().getSimpleName());
                            return;
                        }
                        mDrawerLayout.closeDrawers();
                        openActivity.open(ActivityMyProfile.class, false);
                        break;


                }
            }
        };*/


        //mToolbar.setLogo(R.mipmap.app_icon);
//        setSupportActionBar(mToolbar);
        //getSupportActionBar().setTitle("Home Town");
        //mDrawerToggle= new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,null,R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //linearLayout.removeAllViews();
                //linearLayout.invalidate();

                /*vTop.setVisibility(View.GONE);
                vBottom.setVisibility(View.GONE);
                vTop.animate().translationY(-vTop.getHeight()).setInterpolator(new AccelerateInterpolator(2));
                vTop.setVisibility(View.GONE);
                if(vBottom.getVisibility() == View.VISIBLE){
                    Animation bottomDown = AnimationUtils.loadAnimation(activity, R.anim.bottom_down);
                    vBottom.startAnimation(bottomDown);
                    vBottom.setVisibility(View.GONE);
                }*/

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);

                mainContentFrame.setTranslationX(slideOffset * left_drawer.getWidth());
                mDrawerLayout.bringChildToFront(drawerView);
                mDrawerLayout.requestLayout();

                //if (slideOffset > 0.6 && linearLayout.getChildCount() == 0)
                // viewAnimator.showMenuContent();
                /*if(sharedPrefsHelper.get("isRTL",false)){
                    container_frame.setTranslationX(-slideOffset * left_drawer.getWidth());
                    mDrawerLayout.bringChildToFront(drawerView);
                    mDrawerLayout.requestLayout();
                }else {
                    container_frame.setTranslationX(slideOffset * left_drawer.getWidth());
                    mDrawerLayout.bringChildToFront(drawerView);
                    mDrawerLayout.requestLayout();
                }*/

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                /*vTop.setVisibility(View.VISIBLE);
                vBottom.setVisibility(View.VISIBLE);
                vTop.setVisibility(View.VISIBLE);
                vTop.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2));
                if(vBottom.getVisibility() != View.VISIBLE){
                    Animation bottomUp = AnimationUtils.loadAnimation(activity, R.anim.bottom_up);
                    vBottom.startAnimation(bottomUp);
                    vBottom.setVisibility(View.VISIBLE);
                }*/

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);//to restore drawer open this code and oncreate line and in layout width


    }

    @Override   //to restore drawer open this code and oncreate line and in layout width
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(Gravity.LEFT)){
            mDrawerLayout.closeDrawers();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            mDrawerLayout.openDrawer(GravityCompat.START);
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }




    //===============================Drawer===========================================================



    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

    }


    BottomSheetDialog dialogTeleHealth;
    public void showTeleHealthDialog() {
        if(dialogTeleHealth != null){
            dialogTeleHealth.dismiss();
        }
        dialogTeleHealth = new BottomSheetDialog(activity);
        dialogTeleHealth.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTeleHealth.setContentView(R.layout.dialog_telehealth);
        dialogTeleHealth.setCanceledOnTouchOutside(false);

        Button btnDownloadNow = dialogTeleHealth.findViewById(R.id.btnDownloadNow);
        Button btnCancel = dialogTeleHealth.findViewById(R.id.btnCancel);

        TextView tvAppMsg = dialogTeleHealth.findViewById(R.id.tvAppMsg);

        if(isOnlineCareAppInstalled()){
            tvAppMsg.setText("OnlineCare telehealth app is already installed on your device.");
            btnDownloadNow.setText("Launch App Now");
        }else {
            tvAppMsg.setText("Download our OnlineCare Telehealth app from the google play store.");
            btnDownloadNow.setText("Download Now");
        }


        btnDownloadNow.setOnClickListener(v -> {
            dialogTeleHealth.dismiss();
            launchOnlineCareUS_App();
        });

        btnCancel.setOnClickListener(v -> {
            dialogTeleHealth.dismiss();
        });

        dialogTeleHealth.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTeleHealth.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTeleHealth.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogTeleHealth.show();
        dialogTeleHealth.getWindow().setAttributes(lp);*/
    }

    public void launchOnlineCareUS_App(){
        final String olcUS_PkgName = "com.app.OnlineCareUS_Pt";
        Intent intent = getPackageManager().getLaunchIntentForPackage(olcUS_PkgName);
        if (intent != null) {
            // We found the activity now start the activity
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + olcUS_PkgName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                anfe.printStackTrace();
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + olcUS_PkgName)));
            }
        }
    }

    private boolean isOnlineCareAppInstalled(){
        final String olcUS_PkgName = "com.app.OnlineCareUS_Pt";
        Intent intent = getPackageManager().getLaunchIntentForPackage(olcUS_PkgName);
        return intent != null;
    }



    protected void lockApp(boolean lock){
        if(lock){
            layScanCard.setEnabled(false);
            layShowCard.setEnabled(false);
            layTestLocations.setEnabled(false);
            layTestResults.setEnabled(false);
            layIdCard.setEnabled(false);

            layScanCard.setAlpha(0.5f);
            layShowCard.setAlpha(0.5f);
            layTestLocations.setAlpha(0.5f);
            layTestResults.setAlpha(0.5f);
            layIdCard.setAlpha(0.5f);

            viewScanCardDim.setVisibility(View.VISIBLE);
            viewShowCardDim.setVisibility(View.VISIBLE);
            viewTestLocationsDim.setVisibility(View.VISIBLE);
            viewTestResultsDim.setVisibility(View.VISIBLE);
            viewIdCardDim.setVisibility(View.VISIBLE);
            //layTrialExpired.setVisibility(View.VISIBLE);
        }else {
            layScanCard.setEnabled(true);
            layShowCard.setEnabled(true);
            layTestLocations.setEnabled(true);
            layTestResults.setEnabled(true);
            layIdCard.setEnabled(true);

            layScanCard.setAlpha(1.0f);
            layShowCard.setAlpha(1.0f);
            layTestLocations.setAlpha(1.0f);
            layTestResults.setAlpha(1.0f);
            layIdCard.setAlpha(1.0f);

            viewScanCardDim.setVisibility(View.GONE);
            viewShowCardDim.setVisibility(View.GONE);
            viewTestLocationsDim.setVisibility(View.GONE);
            viewTestResultsDim.setVisibility(View.GONE);
            viewIdCardDim.setVisibility(View.GONE);
            //layTrialExpired.setVisibility(View.GONE);
        }
    }



    private void showUserAndAppInfo(){
        //DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, ivUserImage);
        Glide.with(activity).setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_default_user_squire).error(R.drawable.ic_default_user_squire)).load(prefs.getString("image", "")).into(ivUserImage);
        String name = prefs.getString("first_name", "") + " "+prefs.getString("last_name", "");
        tvUserName.setText(name);
        tvEmail.setText(prefs.getString("email", ""));
        tvPhone.setText(prefs.getString("phone", ""));
        String address = prefs.getString("address", "") + ", "+prefs.getString("city", "")+", "+
                prefs.getString("state", "")+", "+prefs.getString("zipcode", "")+
                ", "+prefs.getString("country", "");
        tvAddress.setText(address);
        String appname = "© "+ Calendar.getInstance().get(Calendar.YEAR)+ " " + getResources().getString(R.string.app_name)+"  Mobile App.";
        tvAppNameCopyRight.setText(appname);
        tvVersionName.setText("Version "+ BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")");
    }


    public void shareApp(){
        //Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)
        //String textToShare = "Hi there. I am using "+getString(R.string.app_name)+" app (developed by Gexton INC) for online shopping. Checkout this app on google play.";
        String appURL = "https://play.google.com/store/apps/details?id=" + getPackageName();
        String textToShare = "Hi there. I am using "+getString(R.string.app_name)+" app to store my ID cards and documents. Checkout this app on google play. "+appURL;

        try {
            String app_general_labels = sharedPrefsHelper.get("app_general_labels", "");
            if(!TextUtils.isEmpty(app_general_labels)){
                textToShare = new JSONObject(app_general_labels).getJSONObject("data").getJSONObject("share_text").getString("text");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textToShare);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share "+getString(R.string.app_name)));
    }
}
