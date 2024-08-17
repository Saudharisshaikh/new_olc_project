package com.app.OnlineCareTDC_Dr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.app.OnlineCareTDC_Dr.adapter.AppsExpAdapter;
import com.app.OnlineCareTDC_Dr.adapter.DialogOlcAppsAdapter;
import com.app.OnlineCareTDC_Dr.api.ApiManager;
import com.app.OnlineCareTDC_Dr.model.AppBean;
import com.app.OnlineCareTDC_Dr.model.AppBean2;
import com.app.OnlineCareTDC_Dr.util.DATA;
import com.app.OnlineCareTDC_Dr.util.ExpandableHeightGridView;
import com.app.OnlineCareTDC_Dr.util.HideShowKeypad;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityAppInvite extends BaseActivity {


    ImageView ivBack;
    EditText etAppInviteApp,etAppInviteCellNo,etAppInviteEmail,etAppInviteMessage;
    ExpandableHeightGridView gvAppInviteApps;
    Button btnAppInviteSend,btnAppInviteNotNow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_invite);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        ivBack = findViewById(R.id.ivBack);
        etAppInviteApp = findViewById(R.id.etAppInviteApp);
        etAppInviteCellNo = findViewById(R.id.etAppInviteCellNo);
        etAppInviteEmail = findViewById(R.id.etAppInviteEmail);
        etAppInviteMessage = findViewById(R.id.etAppInviteMessage);
        gvAppInviteApps = findViewById(R.id.gvAppInviteApps);
        btnAppInviteSend = findViewById(R.id.btnAppInviteSend);
        btnAppInviteNotNow = findViewById(R.id.btnAppInviteNotNow);


        gvAppInviteApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppsExpAdapter.selectedPos = position;
                if(appsExpAdapter != null){appsExpAdapter.notifyDataSetChanged();}

                String etMsgTxt = message;
                etMsgTxt = etMsgTxt.replace("|link_here|", appBeansDerived.get(position).appLink);
                etAppInviteMessage.setText(etMsgTxt);
                etAppInviteMessage.setSelection(etAppInviteMessage.getText().toString().length());
                etAppInviteMessage.setError(null);
            }
        });


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.ivBack:
                        onBackPressed();
                        break;
                    case R.id.etAppInviteApp:
                        showOLC_AppsDialog();
                        break;
                    case R.id.btnAppInviteSend:
                        sendAppInvite();
                        break;
                    case R.id.btnAppInviteNotNow:
                        onBackPressed();
                        break;
                    default:
                        break;
                }
            }
        };
        ivBack.setOnClickListener(onClickListener);
        etAppInviteApp.setOnClickListener(onClickListener);
        btnAppInviteSend.setOnClickListener(onClickListener);
        btnAppInviteNotNow.setOnClickListener(onClickListener);



        getApps();
    }


    private void getApps(){
        String checheData = sharedPrefsHelper.get(APPS_PREFS_KEY, "");
        if(! TextUtils.isEmpty(checheData)){
            parseAppsData(checheData);
            ApiManager.shouldShowPD = false;
        }
        ApiManager apiManager = new ApiManager(ApiManager.SMS_GET_APPS,"post",null,apiCallBack, activity);
        apiManager.loadURL();
    }


    private void sendAppInvite(){

        //String appName = etAppInviteApp.getText().toString().trim();

        String sms_number = etAppInviteCellNo.getText().toString().trim();
        String email = etAppInviteEmail.getText().toString().trim();
        String message_text = etAppInviteMessage.getText().toString().trim();

        boolean validated = true;
        /*if(TextUtils.isEmpty(appName)){
            etAppInviteApp.setError("Required");
            validated = false;
        }*/
        /*if(TextUtils.isEmpty(sms_number)){
            etAppInviteCellNo.setError("Required");
            validated = false;
        }
        if(! Signup.isValidEmail(email)){
            etAppInviteEmail.setError("Invalid email address");
            validated = false;
        }*/

        if(TextUtils.isEmpty(sms_number) && ! Signup.isValidEmail(email)){
            validated = false;
            customToast.showToast("Please enter phone no or a valid email address", 0 ,0);
        }

        if(TextUtils.isEmpty(message_text)){
            etAppInviteMessage.setError("Required");
            validated = false;
        }
        if(!validated){
            customToast.showToast("Please enter the required information",0,0);
            return;
        }

        RequestParams params = new RequestParams();
        //params.put("user_id", prefs.getString("id", ""));
        params.put("sms_number", sms_number);
        params.put("email", email);
        params.put("message_text", message_text);

        ApiManager apiManager = new ApiManager(ApiManager.SMS_SEND_INVITE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        super.fetchDataCallback(httpStatus, apiName, content);

        if(apiName.equalsIgnoreCase(ApiManager.SMS_GET_APPS)){
            parseAppsData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.SMS_SEND_INVITE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.optString("status").equalsIgnoreCase("success")){
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getResources().getString(R.string.app_name))
                                    .setMessage("App invitation has been sent successfully.")
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }else if(jsonObject.has("message")){
                    customToast.showToast(jsonObject.getString("message"),0,0);
                }else if(jsonObject.has("msg")){
                    customToast.showToast(jsonObject.getString("msg"),0,0);
                }else {
                    customToast.showToast(jsonObject.toString(),0,0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    private void parseAppsData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);

            String desktop_link = jsonObject.getString("desktop_link");
            message = jsonObject.getString("message");

            JSONArray our_apps = jsonObject.getJSONArray("our_apps");

            appBeans = gson.fromJson(our_apps.toString(), new TypeToken<ArrayList<AppBean>>() {}.getType());

            if(appBeans != null){
                if(lvApps != null){
                    dialCountriesAdapter = new DialogOlcAppsAdapter(activity, appBeans);
                    lvApps.setAdapter(dialCountriesAdapter);   
                }

                //new Work load apps in grid
                appBeansDerived = new ArrayList<>();
                for (int i = 0; i < appBeans.size(); i++) {

                    String app_type = appBeans.get(i).app_type;
                    String android_link = appBeans.get(i).android_link;
                    String iphone_link = appBeans.get(i).iphone_link;

                    int selecter1 = app_type.equalsIgnoreCase("patient") ? R.drawable.app_pt_android : R.drawable.app_dr_android;
                    int selecter2 = app_type.equalsIgnoreCase("patient") ? R.drawable.app_pt_apple : R.drawable.app_dr_apple;

                    AppBean2 appBeanAndroid = new AppBean2("Android "+app_type, android_link, selecter1);
                    AppBean2 appBeanApple = new AppBean2("Apple "+app_type, iphone_link, selecter2);

                    appBeansDerived.add(appBeanAndroid);
                    appBeansDerived.add(appBeanApple);
                }
                AppBean2 appBeanWeb = new AppBean2("Desktop/Web App", desktop_link, R.drawable.app_dr_web);
                appBeansDerived.add(appBeanWeb);

                appsExpAdapter = new AppsExpAdapter(activity, appBeansDerived);
                gvAppInviteApps.setAdapter(appsExpAdapter);
                gvAppInviteApps.setExpanded(true);

                //new Work load apps in grid ends
            }
            //etAppInviteMessage.setText(message);

            sharedPrefsHelper.save(APPS_PREFS_KEY, content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }

    }


    static final String APPS_PREFS_KEY = "app_chechedata_prefs";
    ListView lvApps;
    ArrayList<AppBean> appBeans;
    DialogOlcAppsAdapter dialCountriesAdapter;

    ArrayList<AppBean2> appBeansDerived;
    AppsExpAdapter appsExpAdapter;

    String message = "";

    public void showOLC_AppsDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_olcapps);
        dialogSupport.setCanceledOnTouchOutside(false);

        EditText etSerchApp = dialogSupport.findViewById(R.id.etSerchApp);
        lvApps = dialogSupport.findViewById(R.id.lvApps);

        dialogSupport.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSupport.dismiss();
            }
        });


        lvApps.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etAppInviteApp.setError(null);
                etAppInviteApp.setText(appBeans.get(position).app_name);

                StringBuilder sbApplink = new StringBuilder();
                sbApplink.append("App Store : ");
                sbApplink.append(appBeans.get(position).iphone_link);
                sbApplink.append("\n");
                sbApplink.append("Google Play : ");
                sbApplink.append(appBeans.get(position).android_link);

                String etMsgTxt = message;
                etMsgTxt = etMsgTxt.replace("|link_here|", sbApplink.toString());

                etAppInviteMessage.setText(etMsgTxt);
                etAppInviteMessage.setSelection(etAppInviteMessage.getText().toString().length());
                etAppInviteMessage.setError(null);

                new HideShowKeypad(activity).hidekeyboardOnDialog();

                dialogSupport.dismiss();
            }
        });

        etSerchApp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(dialCountriesAdapter != null){
                    dialCountriesAdapter.filter(s.toString());
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


        getApps();
    }
}
