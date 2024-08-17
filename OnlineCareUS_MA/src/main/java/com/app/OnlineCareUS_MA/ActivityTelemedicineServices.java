package com.app.OnlineCareUS_MA;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
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

import com.app.OnlineCareUS_MA.adapter.TelemedicineAdapter;
import com.app.OnlineCareUS_MA.adapter.TelemedicineAdapter2;
import com.app.OnlineCareUS_MA.api.ApiCallBack;
import com.app.OnlineCareUS_MA.api.ApiManager;
import com.app.OnlineCareUS_MA.model.TelemedicineCatData;
import com.app.OnlineCareUS_MA.model.TelemedicineCategories;
import com.app.OnlineCareUS_MA.model.TimeDiff;
import com.app.OnlineCareUS_MA.reliance.DialogAllEncNotes;
import com.app.OnlineCareUS_MA.util.CheckInternetConnection;
import com.app.OnlineCareUS_MA.util.CustomToast;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.GloabalMethods;
import com.app.OnlineCareUS_MA.util.OpenActivity;
import com.app.OnlineCareUS_MA.util.SharedPrefsHelper;
import com.diegocarloslima.fgelv.lib.FloatingGroupExpandableListView;
import com.diegocarloslima.fgelv.lib.WrapperExpandableListAdapter;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ActivityTelemedicineServices extends AppCompatActivity implements OnClickListener,ApiCallBack {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;
	ApiCallBack apiCallBack;

	FloatingGroupExpandableListView lvTelemedicineData;
	StringBuilder sbSelectedTMSCodes,sbSelectedTMSCodesWithNames;

	public static String tmsCodes = "",tmsCodesWithNames = "";

	Button btnAddToFav,btnRemoveFav;

	ListView lvTelemed2;
	CheckBox cbToggleExpList;
	TextView tvEliveSessionTime;

	RelativeLayout layTMScont;

	SharedPrefsHelper sharedPrefsHelper;
	public static final String TELEMEDICINE_PREFS_KEY = "onlinecare_telemedicine_services_data";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_telemedicine_services);

		sharedPrefsHelper = SharedPrefsHelper.getInstance();

		activity = ActivityTelemedicineServices.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		apiCallBack = this;


		sbSelectedTMSCodes = new StringBuilder();
		sbSelectedTMSCodesWithNames = new StringBuilder();

		lvTelemedicineData = (FloatingGroupExpandableListView) findViewById(R.id.lvTelemedicineData);

		Button btnDone = (Button) findViewById(R.id.btnDone);
		btnDone.setOnClickListener(this);

		btnAddToFav = (Button) findViewById(R.id.btnAddToFav);
		btnRemoveFav = (Button) findViewById(R.id.btnRemoveFav);
		btnAddToFav.setOnClickListener(this);
		btnRemoveFav.setOnClickListener(this);

		lvTelemed2 = findViewById(R.id.lvTelemed2);
		cbToggleExpList = findViewById(R.id.cbToggleExpList);
		tvEliveSessionTime = findViewById(R.id.tvEliveSessionTime);

		lvTelemed2.setVisibility(View.VISIBLE);
		cbToggleExpList.setVisibility(View.VISIBLE);
		tvEliveSessionTime.setVisibility(View.VISIBLE);

		layTMScont = findViewById(R.id.layTMScont);




		if (checkInternetConnection.isConnectedToInternet()) {
			getTelemedicineServices();
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
		}

		cbToggleExpList.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				lvTelemed2.setVisibility(isChecked ? View.GONE : View.VISIBLE);
			}
		});


		new GloabalMethods(activity).getPatientDetails(false);


		DATA.elivecare_end_time = new SimpleDateFormat("HH:mm:ss").format(new Date());

		try {
			Date elivecareStartTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_start_time);
			Date elivecareEndTime = new SimpleDateFormat("HH:mm:ss").parse(DATA.elivecare_end_time);

			TimeDiff timeDiff = getTimeDiff(elivecareStartTime,elivecareEndTime);

			long mm = timeDiff.diffMinutes, ss = timeDiff.diffSeconds, hh = timeDiff.diffHours;
			StringBuilder sb = new StringBuilder();

			StringBuilder sb2 = new StringBuilder();

			if(hh<10){
				sb.append("0"+String.valueOf(hh));
				if(hh > 0){
					sb2.append("0"+String.valueOf(hh)+" hr, ");
				}
			}else{
				sb.append(String.valueOf(hh));
				if(hh > 0){
					sb2.append(String.valueOf(hh)+" hr, ");
				}
			}
			sb.append(":");

			if(mm < 10){
				sb.append("0"+String.valueOf(mm));
				sb2.append("0"+String.valueOf(mm)+" min, ");
			}else {
				sb.append(String.valueOf(mm));
				sb2.append(String.valueOf(mm)+" min, ");
			}
			sb.append(":");
			if(ss < 10){
				sb.append("0"+String.valueOf(ss));
				sb2.append("0"+String.valueOf(ss)+" sec");
			}else {
				sb.append(String.valueOf(ss));
				sb2.append(String.valueOf(ss)+" sec");
			}

			tvEliveSessionTime.setText("Session Time : "+sb2.toString());

		} catch (ParseException e) {
			e.printStackTrace();
		}




		if(ActivitySOAP.addSoapFlag == 5){//this condition is in oncreate - without selecting services
			getMedicalNoteTxt();
			layTMScont.setVisibility(View.INVISIBLE);
		}else {
			layTMScont.setVisibility(View.VISIBLE);
		}
	}

	public TimeDiff getTimeDiff(Date startTime, Date endTime){
		// d1, d2 are dates
		long diff = endTime.getTime() - startTime.getTime();

		long diffSeconds = diff / 1000 % 60;
		long diffMinutes = diff / (60 * 1000) % 60;
		long diffHours = diff / (60 * 60 * 1000) % 24;
		long diffDays = diff / (24 * 60 * 60 * 1000);

        /*DATA.print("-- Time diffrence");
        System.out.print("-- "+diffDays + " days, ");
        System.out.print(diffHours + " hours, ");
        System.out.print(diffMinutes + " minutes, ");
        System.out.print(diffSeconds + " seconds.");*/

		return new TimeDiff(diffSeconds,diffMinutes,diffHours,diffDays);
	}

	ArrayList<TelemedicineCategories> telemedicineCategories;
	public void getTelemedicineServices() {
		String checheData = sharedPrefsHelper.get(TELEMEDICINE_PREFS_KEY, "");
		if(!TextUtils.isEmpty(checheData)){
			parseTelemedicineData(checheData);
			//ApiManager.shouldShowPD = false;
		}
		RequestParams params = new RequestParams();
		params.put("doctor_id",prefs.getString("id",""));
		ApiManager apiManager = new ApiManager(ApiManager.GET_TELEMEDICINE_SERVICES,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_skip, menu);//menu_soap_notes
		//boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawer);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {
			doneProceed();
		}else if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		if (view.getId() == R.id.btnDone) {
			doneProceed();
		}else if(view.getId() == R.id.btnAddToFav){
			addToFav();
		}else if(view.getId() == R.id.btnRemoveFav){
			removeFav();
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

								if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
									openActivity.open(ActivitySoapNotesNew.class, true);//ActivitySoapNotes
								}else if(ActivitySOAP.addSoapFlag == 2){
									openActivity.open(ActivitySoapNotesEmpty.class, true);
								}else if(ActivitySOAP.addSoapFlag == 4){
								    finish();
                                }
                                /*else if(ActivitySOAP.addSoapFlag == 5){//this condition is in oncreate - without selecting services
									getMedicalNoteTxt();
								}*/
							}
						}).setNegativeButton("No",null).create().show();
			} else {
				tmsCodes = tmsCodes.substring(0, tmsCodes.length()-1);
				tmsCodesWithNames = tmsCodesWithNames.substring(0, tmsCodesWithNames.length()-1);
				DATA.print("-- selected tms codes: "+tmsCodes+"-- selected tmsCodesWithNames: "+tmsCodesWithNames);

				if(ActivitySOAP.addSoapFlag == 1 || ActivitySOAP.addSoapFlag == 3){
					openActivity.open(ActivitySoapNotesNew.class, true);////ActivitySoapNotes
				}else if(ActivitySOAP.addSoapFlag == 2){
					openActivity.open(ActivitySoapNotesEmpty.class, true);
				}else if(ActivitySOAP.addSoapFlag == 4){
				    initNoteDialog();
                }
                /*else if(ActivitySOAP.addSoapFlag == 5){////this condition is in oncreate - without selecting services
					getMedicalNoteTxt();
				}*/
			}

		} else {
			DATA.print("-- telemedicineCategories list is null !");
		}

	}

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
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
		}else if(apiName.equalsIgnoreCase(ApiManager.BILL_WITHOUT_NOTE)){
			//{"success":1,"message":"Saved.","note_id":1878}
            try {
                JSONObject jsonObject = new JSONObject(content);
                if(jsonObject.has("success")){
                	customToast.showToast("Your service billing codes request has been submitted successfully",0,0);
					DATA.isSOAP_NotesSent = true;
					finish();
				}
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_MEDICAL_NOTE_TXT)){

			try {
				JSONObject jsonObject = new JSONObject(content);
				String message = jsonObject.optString("message");
				initMedicalNoteDialog(message);

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.SEND_MEDICAL_NOTE)){
			//{"status":"success","message":"Note has been sent."}
			try {
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.optString("status").equalsIgnoreCase("success")){
					customToast.showToast(jsonObject.optString("message"),0,0);
					DATA.isSOAP_NotesSent = true;
					finish();
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

				if(category_name.equalsIgnoreCase("Most Common")){
					lvTelemed2.setAdapter(new TelemedicineAdapter2(activity, catData));
				}

			}
			TelemedicineAdapter adapter = new TelemedicineAdapter(activity, telemedicineCategories);
			WrapperExpandableListAdapter wrapperAdapter = new WrapperExpandableListAdapter(adapter);
			lvTelemedicineData.setAdapter(wrapperAdapter);

			sharedPrefsHelper.save(TELEMEDICINE_PREFS_KEY, content);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
			e.printStackTrace();
		}

	}


	public void billWithoutNote(String tmsCodes, String note_text){
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));
        params.put("patient_id", DATA.selectedUserCallId);
        params.put("treatment_codes", tmsCodes);
        params.put("start_time", DATA.elivecare_start_time);
        params.put("note_text",note_text);

		//GM added call_id
		String callId = "0";
		if(DATA.incomingCall){
			callId = DATA.incommingCallId;
		}else{
			callId = prefs.getString("callingID","");
		}
		params.put("call_id", callId);
		//GM added call_id

        ApiManager apiManager = new ApiManager(ApiManager.BILL_WITHOUT_NOTE,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }


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

		tvNotePatientName.setText(DATA.selectedUserCallName);
		tvNoteBillingCodes.setText(tmsCodesWithNames);


		ivClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogNote.dismiss();
			}
		});
		tvPreviousNotes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DialogAllEncNotes(activity).getNotes(DATA.selectedUserCallId);
			}
		});
		tvSubmitNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String note_text = etNote.getText().toString().trim();
				if (note_text.isEmpty()) {
					customToast.showToast("Please enter your virtual visit note",0,0);
					etNote.setError("Please enter your virtual visit note");
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						dialogNote.dismiss();
						billWithoutNote(tmsCodes, note_text);
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


	public void getMedicalNoteTxt(){
		RequestParams params = new RequestParams();
		//params.put("doctor_id", prefs.getString("id", ""));
		params.put("patient_id", DATA.selectedUserCallId);

		ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_NOTE_TXT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void sendMedicalNote(String message_txt){
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("patient_id", DATA.selectedUserCallId);
		params.put("message_txt", message_txt);


		ApiManager apiManager = new ApiManager(ApiManager.SEND_MEDICAL_NOTE,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}


	public void initMedicalNoteDialog(String noteTxt) {
		Dialog dialogNote = new Dialog(activity);
		dialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogNote.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		dialogNote.setContentView(R.layout.dialog_medical_note);
		dialogNote.setCanceledOnTouchOutside(false);

		TextView tvNotePatientName = dialogNote.findViewById(R.id.tvNotePatientName);
		//TextView tvNoteBillingCodes = dialogNote.findViewById(R.id.tvNoteBillingCodes);
		EditText etNote = dialogNote.findViewById(R.id.etNote);
		TextView tvSubmitNote = dialogNote.findViewById(R.id.tvSubmitNote);
		ImageView ivClose = dialogNote.findViewById(R.id.ivClose);

		tvNotePatientName.setText(DATA.selectedUserCallName);
		//tvNoteBillingCodes.setText(tmsCodesWithNames);

		etNote.setText(noteTxt);
		etNote.setSelection(etNote.getText().toString().length());


		ivClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogNote.dismiss();
			}
		});
		tvSubmitNote.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				String note_text = etNote.getText().toString().trim();
				if (note_text.isEmpty()) {
					customToast.showToast("Please enter your virtual visit note",0,0);
					etNote.setError("Please enter your virtual visit note");
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						dialogNote.dismiss();
						sendMedicalNote(note_text);
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
}
