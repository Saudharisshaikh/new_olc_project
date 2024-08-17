package com.covacard;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.covacard.R;
import com.covacard.api.ApiManager;
import com.covacard.adapter.PackageAdapter;
import com.covacard.model.PackageBean;
import com.covacard.model.SubscriptionPlanBean;
import com.covacard.util.DATA;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityPackages extends ActivityBaseDrawer{


    ListView lvCardsList;
    TextView tvNoData;
    SwipeRefreshLayout swiperefresh;

    TextView tvIndividual,tvFamily;
    ViewFlipper vfPackages;
    SwipeRefreshLayout swiperefreshFamily;
    ListView lvCardsListFamily;
    TextView tvNoDataFamily;

    TextView tvPkgName,tvPkgType,tvPkgStrt,tvPkgEnd,tvPkgMode,tvPkgAmount,tvPkgMsg;
    Button btnCancelSubs,btnViewInvoices;
    LinearLayout layMyPkg;
    RelativeLayout layAllPkgs;

    int selectedChild;

    static boolean isPaymentDone = false,
    isSubcriptionCancelled = false;


    @Override
    protected void onResume() {
        super.onResume();

        if(isPaymentDone){
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_packages);
        getLayoutInflater().inflate(R.layout.activity_packages, container_frame);

        lvCardsList = findViewById(R.id.lvCardsList);
        tvNoData = findViewById(R.id.tvNoData);

        tvIndividual = findViewById(R.id.tvIndividual);
        tvFamily = findViewById(R.id.tvFamily);
        vfPackages = findViewById(R.id.vfPackages);
        swiperefreshFamily = findViewById(R.id.swiperefreshFamily);
        lvCardsListFamily = findViewById(R.id.lvCardsListFamily);
        tvNoDataFamily = findViewById(R.id.tvNoDataFamily);

        tvPkgName = findViewById(R.id.tvPkgName);
        tvPkgType = findViewById(R.id.tvPkgType);
        tvPkgStrt = findViewById(R.id.tvPkgStrt);
        tvPkgEnd = findViewById(R.id.tvPkgEnd);
        tvPkgMode = findViewById(R.id.tvPkgMode);
        tvPkgAmount = findViewById(R.id.tvPkgAmount);
        tvPkgMsg = findViewById(R.id.tvPkgMsg);
        btnCancelSubs = findViewById(R.id.btnCancelSubs);
        btnViewInvoices = findViewById(R.id.btnViewInvoices);
        layMyPkg = findViewById(R.id.layMyPkg);
        layAllPkgs = findViewById(R.id.layAllPkgs);

        SubscriptionPlanBean subscriptionPlanBean = sharedPrefsHelper.getSubscriptionPlan();
        if(subscriptionPlanBean != null){
            layMyPkg.setVisibility(View.VISIBLE);
            layAllPkgs.setVisibility(View.GONE);

            tvPkgName.setText(subscriptionPlanBean.package_name);
            tvPkgType.setText(subscriptionPlanBean.pkg_type);

            String formatedStrtDate = subscriptionPlanBean.dateof;//2021-09-21 11:38:43
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedStrtDate);
                formatedStrtDate = new SimpleDateFormat("MM/dd/yyyy").format(d);
                //commenceDate = commenceDate.replace(" ","\n");
            }catch (Exception e){e.printStackTrace();}
            tvPkgStrt.setText(formatedStrtDate);

            String formatedEndDate = subscriptionPlanBean.expiry;//2021-09-21
            try {
                Date d = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(formatedEndDate);
                formatedEndDate = new SimpleDateFormat("MM/dd/yyyy").format(d);
                //commenceDate = commenceDate.replace(" ","\n");
            }catch (Exception e){e.printStackTrace();}
            tvPkgEnd.setText(formatedEndDate);

            tvPkgMode.setText(subscriptionPlanBean.pkg_mode);
            tvPkgAmount.setText("US$ "+subscriptionPlanBean.amount);
            tvPkgMsg.setText(subscriptionPlanBean.message);
        }else {
            layMyPkg.setVisibility(View.GONE);
            layAllPkgs.setVisibility(View.VISIBLE);
        }


        btnViewInvoices.setOnClickListener(v -> {
            openActivity.open(ActivityInvoices.class, false);
        });

        int vis = sharedPrefsHelper.get("debug_logs", false) ? View.VISIBLE : View.GONE;
        btnCancelSubs.setVisibility(vis);

        btnCancelSubs.setOnClickListener(v -> {
            AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle("Confirm")
                            .setMessage("Are you sure? Do you want to cancel your "+getResources().getString(R.string.app_name)+" subscription?")
                            .setPositiveButton("Yes Cancel", (dialog, which) -> {
                                RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
                                ApiManager apiManager = new ApiManager(ApiManager.CANCEL_SUBSCRIPTION, "post", params, apiCallBack, activity);
                                apiManager.loadURL();
                            })
                            .setNegativeButton("Not Now",null)
                            .create();
            alertDialog.show();
        });

        View.OnClickListener onClickListener = v -> {
            if(v.getId() == R.id.tvIndividual){
                tvIndividual.setBackgroundResource(R.drawable.cust_border_green_half2);
                tvIndividual.setTextColor(getResources().getColor(android.R.color.white));
                tvFamily.setBackgroundColor(getResources().getColor(android.R.color.white));
                tvFamily.setTextColor(getResources().getColor(R.color.theme_red));

                selectedChild = 0;
                if (selectedChild > vfPackages.getDisplayedChild()) {
                    vfPackages.setInAnimation(activity, R.anim.in_right);
                    vfPackages.setOutAnimation(activity, R.anim.out_left);
                } else {
                    vfPackages.setInAnimation(activity, R.anim.in_left);
                    vfPackages.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfPackages.getDisplayedChild() != selectedChild) {
                    vfPackages.setDisplayedChild(selectedChild);
                }
            }else if(v.getId() == R.id.tvFamily){
                tvIndividual.setBackgroundColor(getResources().getColor(android.R.color.white));
                tvIndividual.setTextColor(getResources().getColor(R.color.theme_red));
                tvFamily.setBackgroundResource(R.drawable.cust_border_green_half2);
                tvFamily.setTextColor(getResources().getColor(android.R.color.white));

                selectedChild = 1;
                if (selectedChild > vfPackages.getDisplayedChild()) {
                    vfPackages.setInAnimation(activity, R.anim.in_right);
                    vfPackages.setOutAnimation(activity, R.anim.out_left);
                } else {
                    vfPackages.setInAnimation(activity, R.anim.in_left);
                    vfPackages.setOutAnimation(activity, R.anim.out_right);
                }
                if (vfPackages.getDisplayedChild() != selectedChild) {
                    vfPackages.setDisplayedChild(selectedChild);
                }
            }
        };
        tvIndividual.setOnClickListener(onClickListener);
        tvFamily.setOnClickListener(onClickListener);


        /*lvCardsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);
                selectedPackageBean = packageBeans.get(position);
            }
        });*/

        /*setSupportActionBar(findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Test Results");
        Button btnToolbar = findViewById(R.id.btnToolbar);

        btnToolbar.setText("Add New");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ActivityAddTestResults.class, false);
            }
        });*/

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Subscription Packages");
            getSupportActionBar().hide();
        }

        tvToolbarTitle.setText("Subscription Packages");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/


        //======================swip to refresh==================================
        SwipeRefreshLayout.OnRefreshListener onRefreshListener = () -> {
            if(!checkInternetConnection.isConnectedToInternet()){
                swiperefresh.setRefreshing(false);
                swiperefreshFamily.setRefreshing(false);
            }
            loadListData();
        };
        swiperefresh = findViewById(R.id.swiperefresh);
        int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
        swiperefresh.setColorSchemeColors(colorsArr);
        swiperefresh.setOnRefreshListener(onRefreshListener);
        swiperefreshFamily.setOnRefreshListener(onRefreshListener);
        //======================swip to refresh ends=============================



        loadListData();


        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }

    public void loadListData(){
        String all_test_results = sharedPrefsHelper.get("covacard_subscription_packages", "");
        if(!TextUtils.isEmpty(all_test_results)){
            parseData(all_test_results);
            ApiManager.shouldShowLoader = false;
        }
        swiperefresh.setRefreshing(true);
        RequestParams params = new RequestParams("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_PACKAGES, "post", params, apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<PackageBean> packageBeans, packageBeansFamily;
    PackageAdapter packageAdapter, packageAdapterFamily;
    public static PackageBean selectedPackageBean;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_PACKAGES)){
            swiperefresh.setRefreshing(false);
            swiperefreshFamily.setRefreshing(false);
            parseData(content);
        }else if(apiName.equalsIgnoreCase(ApiManager.BUY_WITH_CODE)){
            //{"status":"error","message":"Invalid Code."}
            //{"status":"success","data":{"id":"3","package_name":"code40","duration_month":"6","amount":"2.99","is_deleted":"0","pkg_type":"Code"}}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle("Subscription Successful")
                            .setMessage(jsonObject.optString("message", "You have successfully subscribed to this package."))
                            .setPositiveButton("Done", null)
                            .create();
                    alertDialog.setOnDismissListener(dialog -> {

                        if(dialogTrialExpired != null){
                            dialogTrialExpired.dismiss();
                        }
                        ActivityPackages.isPaymentDone = true;
                        onResume();
                    });
                    alertDialog.show();
                }else {
                    customToast.showToast(jsonObject.optString("message"),0,1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.CANCEL_SUBSCRIPTION)){
            //{"status":"success","message":"Your subscription has been cancelled."}
            //{"status":"success","message":"Your subscription has been cancelled."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.optString("status").equalsIgnoreCase("success")){
                    AlertDialog alertDialog = new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
                            .setTitle("Info")
                            .setMessage(jsonObject.optString("message", "Your subscription has been cancelled."))
                            .setPositiveButton("Done", null)
                            .create();
                    alertDialog.setOnDismissListener(dialog -> {
                        isSubcriptionCancelled = true;
                        sharedPrefsHelper.saveSubscriptionPlan(null);
                        finish();

                    });
                    alertDialog.show();
                }else {
                    customToast.showToast(jsonObject.optString("message"),0,1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }

    private void parseData(String content){
        try {
            JSONObject jsonObject = new JSONObject(content);

            JSONArray data = jsonObject.getJSONArray("data");
            tvNoData.setVisibility(data.length() == 0 ? View.VISIBLE : View.GONE);
            Type listType = new TypeToken<ArrayList<PackageBean>>() {}.getType();
            packageBeans = gson.fromJson(data.toString(), listType);
            packageBeans.add(new PackageBean("Subscription Code", true, "Code", "Self", "  -"));
            packageAdapter = new PackageAdapter(activity, packageBeans);
            lvCardsList.setAdapter(packageAdapter);

            JSONArray family_packages = jsonObject.getJSONArray("family_packages");
            tvNoDataFamily.setVisibility(family_packages.length() == 0 ? View.VISIBLE : View.GONE);
            packageBeansFamily = gson.fromJson(family_packages.toString(), listType);
            packageBeansFamily.add(new PackageBean("Subscription Code", true, "Code", "Family", "  -"));
            packageAdapterFamily = new PackageAdapter(activity, packageBeansFamily);
            lvCardsListFamily.setAdapter(packageAdapterFamily);

            sharedPrefsHelper.save("covacard_subscription_packages", content);

        } catch (JSONException e) {
            e.printStackTrace();
            customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
        }
    }



    /*public void viewOrEditForm(int position, boolean isReadOnly){

        selectedDastListBean = dastListBeans.get(position);

        Intent intent = new Intent(activity,ActivityDAST_Form.class);
        intent.putExtra("isEdit",true);

        intent.putExtra("isReadOnly", isReadOnly);

        startActivity(intent);
    }*/





    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_new, menu);

        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);

        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_schedule) {

            openActivity.open(ActivityBradenScale.class, false);//not used !

        }else if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }*/



    Dialog dialogTrialExpired;
    public void showBuyWithCodeDialog() {
        if(dialogTrialExpired != null){
            dialogTrialExpired.dismiss();
        }
        dialogTrialExpired = new Dialog(activity);
        dialogTrialExpired.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogTrialExpired.setContentView(R.layout.dialog_buy_code);
        dialogTrialExpired.setCanceledOnTouchOutside(false);


        EditText etCode = dialogTrialExpired.findViewById(R.id.etCode);
        Button btnSubscribeNow = dialogTrialExpired.findViewById(R.id.btnSubscribeNow);
        Button btnCancel = dialogTrialExpired.findViewById(R.id.btnCancel);


        btnSubscribeNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogTrialExpired.dismiss();
                //openActivity.open(ActivityPackages.class, false);

                String code = etCode.getText().toString();
                if(TextUtils.isEmpty(code)){
                    etCode.setError("Please enter the subscription code");
                    return;
                }
                RequestParams params = new RequestParams();
                params.put("patient_id", prefs.getString("id", ""));
                params.put("package_code", code);
                ApiManager apiManager = new ApiManager(ApiManager.BUY_WITH_CODE, "post", params, apiCallBack, activity);
                apiManager.loadURL();
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialogTrialExpired.dismiss();
        });

        dialogTrialExpired.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogTrialExpired.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTrialExpired.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogTrialExpired.show();
        dialogTrialExpired.getWindow().setAttributes(lp);*/
    }
}
