package com.digihealthcard.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.digihealthcard.ActivityVerification;
import com.digihealthcard.BuildConfig;
import com.digihealthcard.Login;
import com.digihealthcard.R;
import com.digihealthcard.Signup;
import com.digihealthcard.SubUsersList;
import com.digihealthcard.adapter.DialCountriesAdapter;
import com.digihealthcard.api.ApiCallBack;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.api.Dialog_CustomProgress;
import com.digihealthcard.model.CountryBean;
import com.digihealthcard.model.SubUsersModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

public class GloabalMethods implements ApiCallBack {

    Activity activity;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    SharedPrefsHelper sharedPrefsHelper;
    OpenActivity openActivity;
    Dialog_CustomProgress dialog_customProgress;

    public static final String SHOW_PHARMACY_BROADCAST_ACTION = BuildConfig.APPLICATION_ID+".onlinecare_show_patient_selected_pharmacy";

    public static String userVerFName , userVerLName , userVerphone , userVerEmail , userVerfyId;

    public GloabalMethods(Activity activity) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        customToast = new CustomToast(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        dialog_customProgress = new Dialog_CustomProgress(activity);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
    }



    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "-- googleplayservices";
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
	/*public boolean checkGooglePlayservices() {
		// Check status of Google Play Services
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

		// Check Google Play Service Available
		try {
			if (status != ConnectionResult.SUCCESS) {
				GooglePlayServicesUtil.getErrorDialog(status, activity, 10).show();
				System.out.println("--not equal inside if");
				return false;
			}else {
				System.out.println("--inside else block");
				return true;
			}
		} catch (Exception e) {
			System.out.println("--inside exception");
			Log.e("GooglePlayServiceUtil", "" + e);
			return false;
		}
	}*/

    public void showWebviewDialog(final String webURL, String dialogTittle){
        System.out.println("-- showWebviewDialog url : "+webURL);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lay_webview);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        ProgressBar pbWebview = dialog.findViewById(R.id.pbWebview);
        pbWebview.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);

        WebView webviewBill = (WebView) dialog.findViewById(R.id.webviewBill);
        webviewBill.getSettings().setJavaScriptEnabled(true);
        //webviewBill.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webviewBill.getSettings().setLoadWithOverviewMode(true);
        //webviewBill.getSettings().setUseWideViewPort(true);
        //webviewBill.setWebViewClient(new Callback());
        //webviewBill.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webviewBill.setHorizontalScrollBarEnabled(false);
        //webviewBill.getSettings().setLoadWithOverviewMode(true);
        //webviewBill.setWebChromeClient(new WebChromeClient());
        webviewBill.setInitialScale(100);
        webviewBill.getSettings().setUseWideViewPort(true);
        webviewBill.getSettings().setLoadWithOverviewMode(true);
        webviewBill.getSettings().setBuiltInZoomControls(true);

        TextView tvDialogTittle = (TextView) dialog.findViewById(R.id.dialogTittle);
        tvDialogTittle.setText(dialogTittle);

        webviewBill.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return super.shouldOverrideUrlLoading(view, request);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                //DATA.dismissLoaderDefault();
                pbWebview.setVisibility(View.GONE);
            }
        });
        webviewBill.loadUrl(webURL);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button btnDone = dialog.findViewById(R.id.btnWebviewDone);
        Button btnClose = dialog.findViewById(R.id.btnWebviewCancel);

        btnClose.setVisibility(View.GONE);
        btnDone.setText("Done");

        /*if(!(activity instanceof Signup)){
            btnClose.setVisibility(View.GONE);
            btnDone.setText("Print");
        }
        if(activity instanceof ActivityCardDetail){
            btnDone.setText("Done");
        }*/




        //create object of print manager in your device
        /*PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        //create object of print adapter
        PrintDocumentAdapter printAdapter = webviewBill.createPrintDocumentAdapter();
        //provide name to your newly generated pdf file
        String jobName = activity.getResources().getString(R.string.app_name) + " Print Document";*/

        btnDone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                /*if(activity instanceof ActivityCardDetail){
                    dialog.dismiss();
                }else {
                    try {
                    //open print dialog
                    printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                }catch (Exception e){e.printStackTrace();}
                }*/
            }
        });
        btnClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(activity instanceof Signup){
                    if(webURL.equalsIgnoreCase(DATA.PRIVACY_POLICY_URL)){
                        ((Signup)activity).cbPrivacy.setChecked(false);
                    }else if(webURL.equalsIgnoreCase(DATA.USER_AGREEMENT_URL)){
                        ((Signup)activity).cbUserAgreement.setChecked(false);
                    }
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //DATA.showLoaderDefault(activity,"");
    }


    //===========Check authtoken expiry======================
    public void checkLogin(String jsonStr){//Socket (logout) can't be emitted from here will work on this in future
        //{"error":"expired_token","error_description":"The access token provided has expired"}
        //{"error":"invalid_request","error_description":"Malformed auth header"}
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            if (jsonObject.optString("error").equalsIgnoreCase("expired_token") ||
                    jsonObject.optString("error").equalsIgnoreCase("invalid_request")){
                customToast.showToast("You session has been expired. Please login again",0,1);
                logout();
                SharedPreferences.Editor ed = prefs.edit();
                ed.putBoolean("HaveShownPrefs", false);
                ed.putBoolean("subUserSelected", false);
                ed.putBoolean("livecareTimerRunning", false);
                ed.putBoolean("isConcentFilled", false);

                ed.putBoolean("isInsuranceInfoAdded", false);
                ed.putString("insurance", "");
                ed.putString("policy_number", "");
                ed.putString("group", "");
                ed.putString("code", "");

                ed.commit();

                ed.clear();
                ed.apply();

                //new Database(activity).emptyFirstLoginUser();

                Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void logout(){
        RequestParams params = new RequestParams();
        params.put("id", prefs.getString("id", "0"));
        params.put("type", "patient");

        ApiManager apiManager = new ApiManager(ApiManager.LOGOUT,"post",params,GloabalMethods.this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }



    public void getFirebaseToken(){

        /*String save_token_responce = sharedPrefsHelper.get("save_token_responce", "");
        if(!TextUtils.isEmpty(save_token_responce)){
            parseSavetokenResponce(save_token_responce);
        }*/

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        saveToken(token);
                    }
                });


       /* FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            System.out.println("-- getInstanceId failed"+ task.getException());
                            return;
                        }
                        String token = task.getResult().getToken();
                        saveToken(token);
                    }
                });*/
    }

    public void saveToken(String device_token) {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();*/

		/*if (device_token.contains("|ID|")) {
			device_token = device_token.substring(device_token.indexOf(":") +1);
		}*/
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        ApiManager.setupKeystores(client);
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("platform", "android");
        params.put("device_token", device_token);

        params.put("app_version", BuildConfig.VERSION_NAME);

        if(SharedPrefsHelper.getInstance().get("debug_logs", false)){
            System.out.println("--params in saveToken : "+params.toString());
        }

        client.post(DATA.baseUrl+"/saveToken", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    String content = new String(response);

                    parseSavetokenResponce(content);

                }catch (Exception e){
                    e.printStackTrace();
                    if(SharedPrefsHelper.getInstance().get("debug_logs", false)){
                        System.out.println("-- responce onsuccess: saveToken, http status code: "+statusCode+" Byte responce: "+response);
                    }
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String content = new String(errorResponse);
                    if(SharedPrefsHelper.getInstance().get("debug_logs", false)){
                        System.out.println("--reaponce in onfailure saveToken: "+content);
                    }
                    new GloabalMethods(activity).checkLogin(content);
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end saveToken


    private void parseSavetokenResponce(String content){
        try {

            if(SharedPrefsHelper.getInstance().get("debug_logs", false)){
                System.out.println("--reaponce in saveToken "+content);
            }

            JSONObject jsonObject = new JSONObject(content);

            if(jsonObject.optString("is_online").equalsIgnoreCase("0")){

                prefs.edit().clear().apply();
                SharedPrefsHelper.getInstance().clearAllData();

                Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intent);
                activity.finish();
                return;
            }

            String userStr = jsonObject.getString("user");
            JSONObject user_info = new JSONObject(userStr);
            //Check user Verification status here
            String userVerifyStatus = user_info.getString("is_approved");
            userVerFName = user_info.getString("first_name");
            userVerLName = user_info.getString("last_name");
            userVerphone = user_info.getString("phone");
            userVerEmail = user_info.getString("email");
            userVerfyId = user_info.getString("id");

            if (userVerifyStatus.equalsIgnoreCase("0")) {
                Signup.fromSignUp = "2";
                openActivity.open(ActivityVerification.class , true);
            }
            compareVersions(content);
            sharedPrefsHelper.save("save_token_responce", content);
        }catch (JSONException e){
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }
    }

    //===============Get Firebase Token Ends here


    //--------------------------------App Versions Check Starts--------------------------------------------------------------------
    public static boolean shouldUpdatePopAppear = true;

    public void compareVersions(String content){

        if(shouldUpdatePopAppear){
            shouldUpdatePopAppear = false;

            try {
                JSONObject jsonObject = new JSONObject(content);
                String app_version = jsonObject.getString("app_version");//"1.03"
                if(TextUtils.isEmpty(app_version)){
                    return;
                }
                String myAppVersion = BuildConfig.VERSION_NAME;//"1.02";
                String coloredVer = "<b><font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+app_version+"</font></b>";

                String updateMsg = "New version "+coloredVer+" is available on the google play. Please keep your app up to date in order to get latest features and better performance.";
                try {

                    double storeVersion = Double.parseDouble(app_version);
                    double localVersion = Double.parseDouble(myAppVersion);

                    if(localVersion < storeVersion){
                        showAppUpdateDialog(updateMsg);
                    }

                    System.out.println("-- store ver : "+storeVersion + " , Local ver: "+ localVersion
                            +" Condition : (localVersion < storeVersion) = "+(localVersion < storeVersion));

                    /*String myAppVerBeforeLastDecimal = myAppVersion.substring(0, myAppVersion.lastIndexOf("."));
                    String storeAppVerBeforeLastDecimal = app_version.substring(0, app_version.lastIndexOf("."));
                    System.out.println("-- substr BeforeLastDecimal myVer : "+myAppVerBeforeLastDecimal+" , Store Ver: "+ storeAppVerBeforeLastDecimal);

                    double myVerPre = Double.parseDouble(myAppVerBeforeLastDecimal);
                    double storeVerPre = Double.parseDouble(storeAppVerBeforeLastDecimal);

                    System.out.println("-- before last decimal after cast to doub myVer: "+myVerPre+" ** PlayStore ver: "+storeVerPre);

                    String myAppVerAfterLastDecimal = myAppVersion.substring(myAppVersion.lastIndexOf(".")+1);
                    String storeAppVerAfterLastDecimal = app_version.substring(app_version.lastIndexOf(".")+1);

                    System.out.println("-- substr AfterLastDecimal myVer : "+myAppVerAfterLastDecimal+" , Store Ver: "+ storeAppVerAfterLastDecimal);

                    int myVerPost = Integer.parseInt(myAppVerAfterLastDecimal);
                    int storeVerPost = Integer.parseInt(storeAppVerAfterLastDecimal);
                    System.out.println("-- after last decimal after cast to int myVer: "+myVerPost+" ** PlayStore ver: "+storeVerPost);


                    if(myVerPre < storeVerPre ||  myVerPost < storeVerPost){
                        showAppUpdateDialog(updateMsg);
                    }*/
                }catch (Exception e){
                    e.printStackTrace();
                    if(! myAppVersion.equalsIgnoreCase(app_version)){
                        showAppUpdateDialog(updateMsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void compareVersions2(String content){

        if(shouldUpdatePopAppear){
            shouldUpdatePopAppear = false;

            try {
                JSONObject jsonObject = new JSONObject(content);
                String app_version = jsonObject.getString("app_version");//"1.1.0"
                if(TextUtils.isEmpty(app_version)){
                    return;
                }
                String myAppVersion = BuildConfig.VERSION_NAME;//"1.0.19"
                String coloredVer = "<b><font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+app_version+"</font></b>";

                String updateMsg = "New version "+coloredVer+" is available on the google play. Please keep your app up to date in order to get latest features and better performance.";
                try {

                    String myAppVerBeforeLastDecimal = myAppVersion.substring(0, myAppVersion.lastIndexOf("."));
                    String storeAppVerBeforeLastDecimal = app_version.substring(0, app_version.lastIndexOf("."));
                    System.out.println("-- substr BeforeLastDecimal myVer : "+myAppVerBeforeLastDecimal+" , Store Ver: "+ storeAppVerBeforeLastDecimal);

                    double myVerPre = Double.parseDouble(myAppVerBeforeLastDecimal);
                    double storeVerPre = Double.parseDouble(storeAppVerBeforeLastDecimal);

                    System.out.println("-- before last decimal after cast to doub myVer: "+myVerPre+" ** PlayStore ver: "+storeVerPre);

                    String myAppVerAfterLastDecimal = myAppVersion.substring(myAppVersion.lastIndexOf(".")+1);
                    String storeAppVerAfterLastDecimal = app_version.substring(app_version.lastIndexOf(".")+1);

                    System.out.println("-- substr AfterLastDecimal myVer : "+myAppVerAfterLastDecimal+" , Store Ver: "+ storeAppVerAfterLastDecimal);

                    int myVerPost = Integer.parseInt(myAppVerAfterLastDecimal);
                    int storeVerPost = Integer.parseInt(storeAppVerAfterLastDecimal);
                    System.out.println("-- after last decimal after cast to int myVer: "+myVerPost+" ** PlayStore ver: "+storeVerPost);


                    if(myVerPre < storeVerPre ||  myVerPost < storeVerPost){
                        showAppUpdateDialog(updateMsg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if(! myAppVersion.equalsIgnoreCase(app_version)){
                        showAppUpdateDialog(updateMsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showAppUpdateDialog(String updateMsg){

        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_app_update);
        //dialogSupport.setCancelable(false);

        Button btnUpdateApp = dialogSupport.findViewById(R.id.btnUpdateApp);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        TextView tvUpdateMsg = dialogSupport.findViewById(R.id.tvUpdateMsg);

        tvUpdateMsg.setText(Html.fromHtml(updateMsg));

        btnUpdateApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                playStoreApp();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }

    private void playStoreApp(){
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
        } catch (android.content.ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
        }
    }

    //--------------------------------App Versions Check Ends--------------------------------------------------------------------




    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }



    public void showContrySelectionDialog(final EditText etCountryInput){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_countries);
        dialogSupport.setCanceledOnTouchOutside(false);

        EditText etSerchCountry = dialogSupport.findViewById(R.id.etSerchCountry);
        ListView lvCountry = dialogSupport.findViewById(R.id.lvCountry);

        dialogSupport.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSupport.dismiss();
            }
        });

        final ArrayList<CountryBean> countryBeans = DATA.loadCountries(activity);

        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etCountryInput.setError(null);
                etCountryInput.setText(countryBeans.get(position).getName());

                new HideShowKeypad(activity).hidekeyboardOnDialog();

                dialogSupport.dismiss();
            }
        });

        final DialCountriesAdapter lvCountriesAdapter = new DialCountriesAdapter(activity, countryBeans);
        lvCountry.setAdapter(lvCountriesAdapter);

        etSerchCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvCountriesAdapter != null){
                    lvCountriesAdapter.filter(s.toString());
                }
            }
        });

        //dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogSupport.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);

        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);
    }



    public void showDebugLogsDialog(){
        final Dialog dialogDebugLogs = new Dialog(activity);
        dialogDebugLogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDebugLogs.setContentView(R.layout.dialog_debug_logs);
        dialogDebugLogs.setCanceledOnTouchOutside(false);

        CheckBox cbDebugMode = dialogDebugLogs.findViewById(R.id.cbDebugMode);
        CheckBox cbDemoSubs = dialogDebugLogs.findViewById(R.id.cbDemoSubs);
        Button btnDone = dialogDebugLogs.findViewById(R.id.btnDone);

        cbDebugMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefsHelper.getInstance().save("debug_logs", isChecked);
            String msg = "Debug mode is set to "+ (isChecked ? "on" : "off");
            customToast.showToast(msg,0,0);
        });
        cbDebugMode.setChecked(SharedPrefsHelper.getInstance().get("debug_logs", false));


        cbDemoSubs.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefsHelper.getInstance().save("dummy_subscription", isChecked);
            String msg = "Subscription is set to "+ (isChecked ? "Dummy/Fake Mode" : "Production Mode");
            customToast.showToast(msg,0,0);
        });
        cbDemoSubs.setChecked(SharedPrefsHelper.getInstance().get("dummy_subscription", false));

        btnDone.setOnClickListener(v -> dialogDebugLogs.dismiss());

        dialogDebugLogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDebugLogs.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogDebugLogs.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogDebugLogs.show();
        dialogDebugLogs.getWindow().setAttributes(lp);*/
    }


    public void loadSubPatients(){
        RequestParams params = new RequestParams();
        params.put("patient_id", SharedPrefsHelper.getInstance().getParentUser().id);

        ApiManager apiManager = new ApiManager(ApiManager.SUB_PATIENTS,"post",params,GloabalMethods.this, activity);
        apiManager.loadURL();
    }

    public ArrayList<SubUsersModel> parseSubUsers(String content){

        ArrayList<SubUsersModel> subUsersModels = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray sub_users = jsonObject.getJSONArray("sub_users");

            SubUsersModel parentUser = SharedPrefsHelper.getInstance().getParentUser();

            subUsersModels.add(parentUser);

            SubUsersModel temp;

            for(int j = 0; j<sub_users.length(); j++) {

                temp = new SubUsersModel();

                temp.id = sub_users.getJSONObject(j).getString("id");
                temp.firstName = sub_users.getJSONObject(j).getString("first_name");
                temp.lastName = sub_users.getJSONObject(j).getString("last_name");
                temp.patient_id = sub_users.getJSONObject(j).getString("patient_id");
                temp.image = sub_users.getJSONObject(j).getString("image");
                temp.gender = sub_users.getJSONObject(j).getString("gender");
                temp.dob = sub_users.getJSONObject(j).getString("dob");
                temp.marital_status = sub_users.getJSONObject(j).getString("marital_status");
                //temp.relationship = "Family Member - "+sub_users.getJSONObject(j).getString("relationship");
                temp.relationship = sub_users.getJSONObject(j).getString("relationship");
                temp.occupation = sub_users.getJSONObject(j).getString("occupation");

                temp.insurance = sub_users.getJSONObject(j).getString("insurance");

                temp.is_primary = sub_users.getJSONObject(j).getString("is_primary");

                temp.phone = sub_users.getJSONObject(j).getString("phone");
                temp.residency = sub_users.getJSONObject(j).getString("residency");
                temp.city = sub_users.getJSONObject(j).getString("city");
                temp.state = sub_users.getJSONObject(j).getString("state");
                temp.country = sub_users.getJSONObject(j).getString("country");
                temp.zipcode = sub_users.getJSONObject(j).getString("zipcode");

                subUsersModels.add(temp);

                temp  = null;
            }

            SharedPrefsHelper.getInstance().saveAllSubUsers(subUsersModels);

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }

        return subUsersModels;
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

        if(apiName.equalsIgnoreCase(ApiManager.SUB_PATIENTS)){
            if(activity instanceof SubUsersList){
                ((SubUsersList) activity).displaySubUsersList(content);
            }
        }
    }
}
