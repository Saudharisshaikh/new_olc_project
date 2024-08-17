package com.app.omranpatient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.omranpatient.adapter.PrimaryCareAdapter;
import com.app.omranpatient.api.ApiCallBack;
import com.app.omranpatient.api.ApiManager;
import com.app.omranpatient.model.PrimaryCareBean;
import com.app.omranpatient.util.CheckInternetConnection;
import com.app.omranpatient.util.CustomToast;
import com.app.omranpatient.util.DATA;
import com.app.omranpatient.util.OpenActivity;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityPrimaryCareDoctors extends AppCompatActivity implements ApiCallBack{


    Activity activity;
    SharedPreferences prefs;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    ApiCallBack apiCallBack;

    ListView lvPrimaryCareDoctors;
    RelativeLayout lvPriCont;
    TextView tvNoData;

    Button btnToolbar;

    //My Primary Care Fields
    LinearLayout layMyPrimaryCare;
    TextView tvNurseName,tvNurseType,tvPriEmail,tvPriMobile,tvPriAddress;
    CircularImageView ivNurse;
    ImageView ivIsonline;
    //My Primary Care Fields Ends

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primary_care_doctors);

        activity = ActivityPrimaryCareDoctors.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);
        openActivity = new OpenActivity(activity);
        customToast = new CustomToast(activity);
        apiCallBack = this;


        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setTitle("Medical History");
        btnToolbar = (Button) findViewById(R.id.btnToolbar);
        btnToolbar.setText("Add PCP");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvPriCont.setVisibility(View.VISIBLE);
                layMyPrimaryCare.setVisibility(View.GONE);
            }
        });

        lvPrimaryCareDoctors = (ListView) findViewById(R.id.lvPrimaryCareDoctors);
        lvPriCont = findViewById(R.id.lvPriCont);
        tvNoData = findViewById(R.id.tvNoData);

        //My Primary Care Fields
        layMyPrimaryCare = findViewById(R.id.layMyPrimaryCare);
        tvNurseType = (TextView) findViewById(R.id.tvNurseType);
        ivNurse = (CircularImageView) findViewById(R.id.ivNurse);
        tvNurseName = (TextView) findViewById(R.id.tvNurseName);
        tvPriEmail = (TextView) findViewById(R.id.tvPriEmail);
        tvPriMobile = (TextView) findViewById(R.id.tvPriMobile);
        tvPriAddress = (TextView) findViewById(R.id.tvPriAddress);
        ivIsonline = (ImageView) findViewById(R.id.ivIsonline);
        //My Primary Care Fields Ends


        EditText etSearchDoc = findViewById(R.id.etSearchDoc);
        etSearchDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                if (primaryCareAdapter != null) {
                    primaryCareAdapter.filter(arg0.toString());
                }
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
            @Override
            public void afterTextChanged(Editable arg0) {}
        });


        lvPrimaryCareDoctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(! primaryCareBeens.get(position).isMyPrimaryCare){
                    addPrimaryCare(primaryCareBeens.get(position).id);
                }
            }
        });

        getPrimaryCare();
    }


    public void getPrimaryCare(){
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", "0"));
        ApiManager apiManager = new ApiManager(ApiManager.GET_PRIMARY_CARE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    public void getAllDoctors() {
        RequestParams params = new RequestParams();
        ApiManager apiManager = new ApiManager(ApiManager.GET_ALL_DOCTORS,"get",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    public void addPrimaryCare(String doctor_id) {
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", "0"));
        params.put("doctor_id", doctor_id);
        /*params.put("first_name", first_name);
        params.put("last_name", last_name);
        params.put("address1", address1);
        params.put("mobile", mobile);*/

        ApiManager apiManager = new ApiManager(ApiManager.ADD_PRIMARY_CARE,"get",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<PrimaryCareBean> primaryCareBeens;
    String myPrimaryCareID = "";
    PrimaryCareAdapter primaryCareAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_ALL_DOCTORS)){
            try {
                JSONArray jsonArray = new JSONArray(content);
                primaryCareBeens = new ArrayList<PrimaryCareBean>();
                PrimaryCareBean bean;

                PrimaryCareBean myPrimaryCare = null;

                for (int i = 0; i < jsonArray.length(); i++) {
                    String id = jsonArray.getJSONObject(i).getString("id");
                    String first_name = jsonArray.getJSONObject(i).getString("first_name");
                    String last_name = jsonArray.getJSONObject(i).getString("last_name");
                    String designation = jsonArray.getJSONObject(i).getString("designation");
                    String image = jsonArray.getJSONObject(i).getString("image");
                    String is_online = jsonArray.getJSONObject(i).getString("is_online");

                    String email = jsonArray.getJSONObject(i).getString("email");
                    String mobile = jsonArray.getJSONObject(i).getString("mobile");
                    String current_app = jsonArray.getJSONObject(i).getString("current_app");
                    String address1 = jsonArray.getJSONObject(i).getString("address1");

                    current_app = WordUtils.capitalize(current_app);

                    boolean isMyPriCare = false;
                    if(id.equalsIgnoreCase(myPrimaryCareID)){
                        isMyPriCare = true;
                    }else {
                        isMyPriCare = false;
                    }
                    bean = new PrimaryCareBean(id,first_name,last_name,designation,image,is_online,
                            email,mobile,current_app,address1,isMyPriCare);

                    if(!isMyPriCare){
                        primaryCareBeens.add(bean);
                    }else{
                        myPrimaryCare = bean;
                    }
                    bean = null;
                }

                if(myPrimaryCare != null){
                    primaryCareBeens.add(0,myPrimaryCare);
                    showMyPrimaryCareView(myPrimaryCare, true);//new code 1
                }else {
                    showMyPrimaryCareView(null, false);//new code 2
                }

                primaryCareAdapter = new PrimaryCareAdapter(activity,primaryCareBeens);
                lvPrimaryCareDoctors.setAdapter(primaryCareAdapter);
                if(primaryCareBeens.isEmpty()){
                    tvNoData.setVisibility(View.VISIBLE);
                }else {
                    tvNoData.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.ADD_PRIMARY_CARE)){
            //{"status":"error","msg":"This doctor is already using OnlineCare. Doctor is added as your primary care."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                /*if (jsonObject.getString("status").equalsIgnoreCase("success")) {
                    customToast.showToast(jsonObject.getString("msg"), 0, 1);
                }else{
                    customToast.showToast(jsonObject.getString("msg"), 0, 1);
                }
                getPrimaryCare();*/

                AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                        .setTitle(getResources().getString(R.string.app_name))
                        .setMessage(jsonObject.optString("msg"))
                        .setPositiveButton("OK",null)
                        .create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                alertDialog.show();

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_PRIMARY_CARE)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                int count = jsonObject.getInt("count");

                getAllDoctors();

                if (count > 0) {

                    JSONObject data = jsonObject.getJSONObject("data");

                    String id = data.getString("id");
                    String doctor_id = data.getString("doctor_id");
                    String patient_id = data.getString("patient_id");
                    String date = data.getString("date");
                    String first_name = data.getString("first_name");
                    String last_name = data.getString("last_name");
                    String email = data.getString("email");
                    String mobile = data.getString("mobile");
                    String address1 = data.getString("address1");

                    myPrimaryCareID = doctor_id;
                }else {
                    myPrimaryCareID = "";

                    /*new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
                            setMessage("Primary care is not added on your profile. You can add the primary care here. Please select a doctor from doctor's list to add as primary care").
                            setPositiveButton("Done", null).show();*/

                    //showAskPriCareDialog();

                    showAskPriCareDialog2();
                }



            } catch (JSONException e) {
                // TODO Auto-generated catch block
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                e.printStackTrace();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.PCP_REQUEST)){
            //{"success":"Request has been sent."}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    AlertDialog alertDialog =
                            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                                    .setTitle(getResources().getString(R.string.app_name))
                                    .setMessage(jsonObject.getString("success"))
                                    .setPositiveButton("Done",null).create();
                    alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            finish();
                        }
                    });
                    alertDialog.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }


    public void showAskPriCareDialog(){

        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_pri);
        dialogSupport.setCancelable(false);

        Button btnSelectDoc = dialogSupport.findViewById(R.id.btnSelectDoc);
        Button btnAssignPri = dialogSupport.findViewById(R.id.btnAssignPri);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        CheckBox cbDontHave = dialogSupport.findViewById(R.id.cbDontHave);
        LinearLayout layAssign = dialogSupport.findViewById(R.id.layAssign);

        cbDontHave.setOnCheckedChangeListener((compoundButton, b) -> {
            int vis = b ? View.VISIBLE : View.GONE;
            layAssign.setVisibility(vis);
        });

        btnSelectDoc.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();
            lvPriCont.setVisibility(View.VISIBLE);
            //openActivity.open(MainActivityNew.class, true);
        });
        btnAssignPri.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();

            //Request to OM
            pcpRequest();

            //openActivity.open(ActivityCareVisit.class,false);
            //activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
        });
		btnCancel.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();

            finish();
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


    //note : this dialog was used to match iphone similarties - above dialog is stylish dialog with checkbox but removed at the time
    public void showAskPriCareDialog2(){

        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_pri2);
        dialogSupport.setCancelable(false);

        Button btnSelectDoc = dialogSupport.findViewById(R.id.btnSelectDoc);
        Button btnAssignPri = dialogSupport.findViewById(R.id.btnAssignPri);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        CheckBox cbDontHave = dialogSupport.findViewById(R.id.cbDontHave);
        LinearLayout layAssign = dialogSupport.findViewById(R.id.layAssign);

        cbDontHave.setOnCheckedChangeListener((compoundButton, b) -> {
            int vis = b ? View.VISIBLE : View.GONE;
            layAssign.setVisibility(vis);
        });

        btnSelectDoc.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();
            lvPriCont.setVisibility(View.VISIBLE);
            //openActivity.open(MainActivityNew.class, true);
        });
        btnAssignPri.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();

            //Request to OM
            pcpRequest();

            //openActivity.open(ActivityCareVisit.class,false);
            //activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
        });
        btnCancel.setOnClickListener(v -> {
            // TODO Auto-generated method stub
            dialogSupport.dismiss();

            finish();
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


    public void pcpRequest(){
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));
        ApiManager apiManager = new ApiManager(ApiManager.PCP_REQUEST,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    public void showMyPrimaryCareView(PrimaryCareBean myPrimaryCareBean, boolean isMyPrimaryCareAdded){

        if(isMyPrimaryCareAdded){
            layMyPrimaryCare.setVisibility(View.VISIBLE);
            btnToolbar.setText("Change PCP");

            tvNurseName.setText(myPrimaryCareBean.first_name+" "+myPrimaryCareBean.last_name);
            tvNurseType.setText("Desig: "+myPrimaryCareBean.current_app);
            tvPriEmail.setText("Email: "+myPrimaryCareBean.email);
            tvPriMobile.setText("Ofice Phone: "+myPrimaryCareBean.mobile);
            tvPriAddress.setText("Address: "+myPrimaryCareBean.address1);
            DATA.loadImageFromURL(myPrimaryCareBean.image,R.drawable.icon_call_screen, ivNurse);
            int isOnlineRes = myPrimaryCareBean.is_online.equalsIgnoreCase("1") ? R.drawable.icon_online : R.drawable.icon_notification;
            ivIsonline.setImageResource(isOnlineRes);
        }else {
            layMyPrimaryCare.setVisibility(View.GONE);
            btnToolbar.setText("Add PCP");
        }
    }
}
