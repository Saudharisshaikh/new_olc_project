package com.app.omrandr.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.omrandr.ActivityTelemedicineServices;
import com.app.omrandr.R;
import com.app.omrandr.adapter.TelemedicineAdapter;
import com.app.omrandr.adapter.TelemedicineAdapter2;
import com.app.omrandr.api.ApiCallBack;
import com.app.omrandr.api.ApiManager;
import com.app.omrandr.model.CallLogBean;
import com.app.omrandr.model.TelemedicineCatData;
import com.app.omrandr.model.TelemedicineCategories;
import com.app.omrandr.reliance.DialogAllEncNotes;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DialogAddEncNoteFromCallHist implements ApiCallBack{


    Activity activity;

    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    OpenActivity openActivity;
    ApiCallBack apiCallBack;
    SharedPrefsHelper sharedPrefsHelper;

    CallLogBean callLogBean;
    RefreshEncounterNotesInterface refreshEncounterNotesInterface;

    public DialogAddEncNoteFromCallHist(Activity activity, CallLogBean callLogBean, RefreshEncounterNotesInterface refreshEncounterNotesInterface) {
        this.activity = activity;

        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        this.callLogBean = callLogBean;
        this.refreshEncounterNotesInterface = refreshEncounterNotesInterface;
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
    }


    //Telemedicine billing codes

    ArrayList<TelemedicineCategories> telemedicineCategories;
    public void getTelemedicineServices() {

        String checheData = sharedPrefsHelper.get(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, "");
        if(!TextUtils.isEmpty(checheData)){
            parseTelemedicineData(checheData);
            //ApiManager.shouldShowPD = false;
        }

        RequestParams params = new RequestParams();
        params.put("doctor_id",prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    FloatingGroupExpandableListView lvTelemedicineData;
    ListView lvTelemed2;
    Dialog dialogTelemedicineSer;
    public void showTelemedicineDialog(){
        dialogTelemedicineSer = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogTelemedicineSer.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        //dialogAddTemplPresc.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogTelemedicineSer.setContentView(R.layout.dialog_telemedicine_services);

        FloatingGroupExpandableListView lvTelemedicineData = (FloatingGroupExpandableListView) dialogTelemedicineSer.findViewById(R.id.lvTelemedicineData);
        Button btnDone = (Button) dialogTelemedicineSer.findViewById(R.id.btnDone);

        Button btnAddToFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnAddToFav);
        Button btnRemoveFav = (Button) dialogTelemedicineSer.findViewById(R.id.btnRemoveFav);

        ListView lvTelemed2 = dialogTelemedicineSer.findViewById(R.id.lvTelemed2);
        CheckBox cbToggleExpList = dialogTelemedicineSer.findViewById(R.id.cbToggleExpList);
        TextView tvEliveSessionTime = dialogTelemedicineSer.findViewById(R.id.tvEliveSessionTime);

        Button btnUseForBillingYes = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingYes);
        Button btnUseForBillingNo = dialogTelemedicineSer.findViewById(R.id.btnUseForBillingNo);
        RelativeLayout layUseForBilling = dialogTelemedicineSer.findViewById(R.id.layUseForBilling);

        lvTelemed2.setVisibility(View.VISIBLE);
        cbToggleExpList.setVisibility(View.VISIBLE);
        tvEliveSessionTime.setVisibility(View.VISIBLE);


        tvEliveSessionTime.setText("Session Time : "+callLogBean.duration);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO Auto-generated method stub
                if (view.getId() == R.id.btnDone) {
                    doneProceed();
                }else if(view.getId() == R.id.btnAddToFav){
                    addToFav();
                }else if(view.getId() == R.id.btnRemoveFav){
                    removeFav();
                }else if(view.getId() == R.id.btnUseForBillingYes){
                    layUseForBilling.setVisibility(View.GONE);
                    use_for_billing = "Yes";
                }else if(view.getId() == R.id.btnUseForBillingNo){
                    use_for_billing = "No";
                    tmsCodes = "";
                    tmsCodesWithNames = "";
                    DATA.print("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);

                    if(dialogTelemedicineSer != null){
                        dialogTelemedicineSer.dismiss();
                    }
                    initNoteDialog();
                }
            }
        };
        btnDone.setOnClickListener(onClickListener);
        btnAddToFav.setOnClickListener(onClickListener);
        btnRemoveFav.setOnClickListener(onClickListener);

        btnUseForBillingYes.setOnClickListener(onClickListener);
        btnUseForBillingNo.setOnClickListener(onClickListener);

        cbToggleExpList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                lvTelemed2.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            }
        });

        ImageView ivClose = dialogTelemedicineSer.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(v -> dialogTelemedicineSer.dismiss());



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogTelemedicineSer.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogTelemedicineSer.setCanceledOnTouchOutside(false);
        dialogTelemedicineSer.show();
        dialogTelemedicineSer.getWindow().setAttributes(lp);

        //dialogForDismiss = dialogAddTemplPresc;

        this.lvTelemedicineData = lvTelemedicineData;
        this.lvTelemed2 = lvTelemed2;

        getTelemedicineServices();
    }


    StringBuilder sbSelectedTMSCodes,sbSelectedTMSCodesWithNames;
    String tmsCodes, tmsCodesWithNames;
    public void doneProceed() {

        if (telemedicineCategories != null) {
            sbSelectedTMSCodes = new StringBuilder();
            sbSelectedTMSCodesWithNames = new StringBuilder();
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        sbSelectedTMSCodes.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code);
                        sbSelectedTMSCodes.append(",");

                        sbSelectedTMSCodesWithNames.append(telemedicineCategories.get(i).telemedicineCatDatas.get(j).hcpcs_code
                                +" - "+telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_name);
                        sbSelectedTMSCodesWithNames.append(",");
                        sbSelectedTMSCodesWithNames.append("\n");
                    }
                }
            }

            tmsCodes = sbSelectedTMSCodes.toString();
            tmsCodesWithNames = sbSelectedTMSCodesWithNames.toString();
            if (tmsCodes.isEmpty()) {
                //customToast.showToast("Please select telemedicine services", 0, 1);
                new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
                        setMessage("You do not selected any service. Do you want to skip ?")
                        .setPositiveButton("Skip", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tmsCodes = "";
                                tmsCodesWithNames = "";
                                DATA.print("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);

                                if(dialogTelemedicineSer != null){
                                    dialogTelemedicineSer.dismiss();
                                }
                                initNoteDialog();

								/*if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
									openActivity.open(ActivitySoapNotesNew.class, true);//ActivitySoapNotes
								}else if(ActivitySOAP.addSoapFlag == 2){
									openActivity.open(ActivitySoapNotesEmpty.class, true);
								}else if(ActivitySOAP.addSoapFlag == 4){
									finish();
								}*/

                                /*else if(ActivitySOAP.addSoapFlag == 5){//this condition is in oncreate - without selecting services
									getMedicalNoteTxt();
								}*/
                            }
                        }).setNegativeButton("No",null).create().show();
            } else {
                tmsCodes = tmsCodes.substring(0, tmsCodes.length()-1);
                tmsCodesWithNames = tmsCodesWithNames.substring(0, tmsCodesWithNames.length()-1);
                DATA.print("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);

                if(dialogTelemedicineSer != null){
                    dialogTelemedicineSer.dismiss();
                }
                initNoteDialog();

				/*if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
					openActivity.open(ActivitySoapNotesNew.class, true);////ActivitySoapNotes
				}else if(ActivitySOAP.addSoapFlag == 2){
					openActivity.open(ActivitySoapNotesEmpty.class, true);
				}else if(ActivitySOAP.addSoapFlag == 4){
					initNoteDialog();
				}*/
                /*else if(ActivitySOAP.addSoapFlag == 5){////this condition is in oncreate - without selecting services
					getMedicalNoteTxt();
				}*/
            }

        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }

    }


    public void addToFav(){
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id",prefs.getString("id",""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services["+(i+j)+"]",telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if(isServicesSelected){
                ApiManager apiManager = new ApiManager(ApiManager.SAVE_FAVOURITE_SERVICES,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }else{
                customToast.showToast("Please select telemedicine services",0,0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }

    public void removeFav(){
        boolean isServicesSelected = false;
        RequestParams params = new RequestParams();
        params.put("doctor_id",prefs.getString("id",""));

        if (telemedicineCategories != null) {
            for (int i = 0; i < telemedicineCategories.size(); i++) {
                for (int j = 0; j < telemedicineCategories.get(i).telemedicineCatDatas.size(); j++) {

                    if (telemedicineCategories.get(i).telemedicineCatDatas.get(j).isSelected) {
                        params.put("services["+(i+j)+"]",telemedicineCategories.get(i).telemedicineCatDatas.get(j).service_id);
                        isServicesSelected = true;
                    }
                }
            }
            if(isServicesSelected){
                ApiManager apiManager = new ApiManager(ApiManager.REMOVE_FAVOURITE_SERVICES,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }else{
                customToast.showToast("Please select telemedicine services",0,0);
            }
        } else {
            DATA.print("-- telemedicineCategories list is null !");
        }
    }


    //============Virtual Visit Note
    String use_for_billing = "Yes";
    public void billWithoutNote(String tmsCodes, String note_text){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", callLogBean.getPatient_id());
        params.put("treatment_codes", tmsCodes);
        params.put("call_id", callLogBean.getId());
        params.put("note_text",note_text);
        params.put("use_for_billing", use_for_billing);//(Yes,No)

        ApiManager apiManager = new ApiManager(ApiManager.BILL_WITHOUT_NOTE_CALL_HISTORY,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


    String note_text = "";
    public void initNoteDialog() {
        Dialog dialogNote = new Dialog(activity);
        dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogNote.setContentView(R.layout.dialog_billwithnote);
        dialogNote.setCanceledOnTouchOutside(false);

        TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
        TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
        EditText etNote = dialogNote.findViewById(R.id.etNote);
        TextView tvSubmitNote = dialogNote.findViewById(R.id.tvSubmitNote);
        TextView tvPreviousNotes = dialogNote.findViewById(R.id.tvPreviousNotes);
        ImageView ivClose = dialogNote.findViewById(R.id.ivClose);

        tvNotePatientName.setText(callLogBean.getFirst_name()+" "+callLogBean.getLast_name());
        tvNoteBillingCodes.setText(tmsCodesWithNames);


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogNote.dismiss();
            }
        });
        tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogAllEncNotes(activity).getNotes(callLogBean.getPatient_id());
            }
        });
        tvSubmitNote.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                note_text = etNote.getText().toString().trim();
                if (note_text.isEmpty()) {
                    customToast.showToast("Please enter your virtual visit note",0,0);
                    etNote.setError("Please enter your virtual visit note");
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {

                        new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
                                .setTitle("Confirm")
                                .setMessage("Are you sure? You want to save your notes or edit and review first.")
                                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialogNote.dismiss();
                                        //billWithoutNote(tmsCodes, note_text);
                                        billWithoutNote(tmsCodes, note_text);
                                    }
                                })
                                .setNegativeButton("Edit", null)
                                .create()
                                .show();

                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogNote.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogNote.show();
        dialogNote.getWindow().setAttributes(lp);
    }


    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.GET_TELEMEDICINE_SERVICES)){

            parseTelemedicineData(content);

        }else if(apiName.equals(ApiManager.SAVE_FAVOURITE_SERVICES)){
            //{"status":"success","message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast("Selected services has been saved to your favourite",0,1);
                    getTelemedicineServices();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equals(ApiManager.REMOVE_FAVOURITE_SERVICES)){
            //{"status":"success","message":"Saved"}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.getString("status").equalsIgnoreCase("success")){
                    customToast.showToast("Selected services has been removed from your favourite",0,1);
                    getTelemedicineServices();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        } else if(apiName.equalsIgnoreCase(ApiManager.BILL_WITHOUT_NOTE_CALL_HISTORY)){
            //{"success":1,"message":"Saved.","note_id":1878}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                    customToast.showToast("Your service billing codes request has been submitted successfully",0,0);
                    refreshEncounterNotesInterface.refreshNotes();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }

    public void parseTelemedicineData(String content){
        telemedicineCategories = new ArrayList<>();
        TelemedicineCategories temp;

        try {
            JSONArray jsonArray = new JSONArray(content);

            for (int i = 0; i < jsonArray.length(); i++) {

                ArrayList<TelemedicineCatData> catData = new ArrayList<>();
                TelemedicineCatData telemedicineCatDataTEMP;

                String category_name = jsonArray.getJSONObject(i).getString("category_name");
                JSONArray data = jsonArray.getJSONObject(i).getJSONArray("data");

                for (int j = 0; j < data.length(); j++) {
                    String category_name1 = data.getJSONObject(j).getString("category_name");
                    String hcpcs_code = data.getJSONObject(j).getString("hcpcs_code");
                    String service_name = data.getJSONObject(j).getString("service_name");
                    String category_id = data.getJSONObject(j).getString("category_id");
                    String non_fac_fee = data.getJSONObject(j).getString("non_fac_fee");
                    String service_id = data.getJSONObject(j).getString("service_id");

                    telemedicineCatDataTEMP = new TelemedicineCatData(category_name1, hcpcs_code, service_name, category_id, non_fac_fee,false,service_id);
                    catData.add(telemedicineCatDataTEMP);
                    telemedicineCatDataTEMP = null;
                }

                temp = new TelemedicineCategories(category_name, catData);
                telemedicineCategories.add(temp);

                if(category_name.equalsIgnoreCase("Most Common") && lvTelemed2!=null){
                    lvTelemed2.setAdapter(new TelemedicineAdapter2(activity, catData));
                }

            }
            TelemedicineAdapter adapter = new TelemedicineAdapter(activity, telemedicineCategories);
            WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
            if(lvTelemedicineData != null){
                lvTelemedicineData.setAdapter(wrapperAdapter);
            }

            sharedPrefsHelper.save(ActivityTelemedicineServices.TELEMEDICINE_PREFS_KEY, content);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
            e.printStackTrace();
        }
    }



    public interface RefreshEncounterNotesInterface{
        public void refreshNotes();
    }
}
