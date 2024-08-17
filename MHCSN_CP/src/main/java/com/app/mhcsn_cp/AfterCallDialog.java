package com.app.mhcsn_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import androidx.fragment.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.adapters.DrugsAdapter;
import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.model.DrugBean;
import com.app.mhcsn_cp.model.PotencyUnitBean;
import com.app.mhcsn_cp.reliance.therapist.ActivityTherapyNote;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.CustomToast;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.DatePickerFragment;
import com.app.mhcsn_cp.util.ExpandableHeightListView;
import com.app.mhcsn_cp.util.GloabalMethods;
import com.app.mhcsn_cp.util.OpenActivity;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class AfterCallDialog extends BaseActivity {

	Activity activity;

	TextView tvDialogCompleteConversation,tvDialogRecall,tvDialogSendPrescription,tvAddProgressNotes;
	Button btnAfterCallAddNote,btnAfterCallNotePended,btnAfterCallComplete;
	ProgressDialog pd;
	AsyncHttpClient client;
	String status = "";
	JSONObject jsonObject;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;

	@Override
	public void onBackPressed() {
		//super.onBackPressed();  // no action occur on back button
		androidx.appcompat.app.AlertDialog alertDialog = new androidx.appcompat.app.AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
				.setTitle(getResources().getString(R.string.app_name))
				.setMessage("Are you sure ? Do you want to exit.")
				.setPositiveButton("Yes Exit", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("Not Now",null)
				.create();
		alertDialog.setCancelable(false);
		alertDialog.show();
	}

	@Override
	protected void onResume() {
		if (DATA.isSOAP_NotesSent) {
			//initRemoveDialog();
			DATA.isSOAP_NotesSent = false;
			//finish();
            if(DATA.isVideoCallFromLiveCare){//Same code on complete the conversation click
                DATA.isVideoCallFromLiveCare = false;
                initRemoveDialog(true);
            }
            /*else if(DATA.isVideoCallFromRefPt){
                DATA.isVideoCallFromRefPt = false;
                initRemoveFromMyPatientsDialog();
            }*/
            else {
                finish();
            }
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.after_call_dialog);

		activity = AfterCallDialog.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		pd = new ProgressDialog(activity);
		pd.setTitle("Please wait..");
		pd.setMessage("Removing the current appointment from queue");

		checkInternetConnection = new CheckInternetConnection(activity);

		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);

		tvDialogCompleteConversation = findViewById(R.id.tvDialogCompleteConversation);
		tvDialogRecall = findViewById(R.id.tvDialogRecall);
		tvDialogSendPrescription = findViewById(R.id.tvDialogSendPrescription);
		tvAddProgressNotes = findViewById(R.id.tvAddProgressNotes);

		btnAfterCallAddNote = findViewById(R.id.btnAfterCallAddNote);
		btnAfterCallNotePended = findViewById(R.id.btnAfterCallNotePended);
		btnAfterCallComplete = findViewById(R.id.btnAfterCallComplete);


		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.btnAfterCallAddNote:
						openActivity.open(ActivityTherapyNote.class, false);
						break;
					case R.id.btnAfterCallNotePended:
						customToast.showToast("Comming Soon",0,0);
						break;
					case R.id.btnAfterCallComplete:
						if(DATA.isVideoCallFromLiveCare){//Same code in onResume if DATA.isSoapnotessent true
							DATA.isVideoCallFromLiveCare = false;
							initRemoveDialog(false);
						}else {
							Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
							//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
						}
						break;
				}
			}
		};
		btnAfterCallAddNote.setOnClickListener(onClickListener);
		btnAfterCallNotePended.setOnClickListener(onClickListener);
		btnAfterCallComplete.setOnClickListener(onClickListener);


		tvDialogCompleteConversation.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				//initRemoveDialog();
				//startActivity(new Intent(activity,MainActivityNew.class));
				//finish();
				Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		});

		tvDialogRecall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(activity, MainActivity.class);//SampleActivity.class
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();

			}
		});

		tvDialogSendPrescription.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(checkInternetConnection.isConnectedToInternet()) {

					//sendCallDoneReport();  to remove patient from queue
					//initPrescripDialog();
					//startActivity(new Intent(activity,ActivityTelemedicineServices.class));
					new GloabalMethods(activity).showAddSOAPDialog();
				}
				else {
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
				}
			}
		});

		tvAddProgressNotes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityProgressNotes.class,false);
			}
		});



		View viewHideActivity = findViewById(R.id.viewHideActivity);

		boolean isForRemoveFromQueueFromUcDet = getIntent().getBooleanExtra("isForRemoveFromQueueFromUcDet", false);
		if(isForRemoveFromQueueFromUcDet){
			viewHideActivity.setVisibility(View.VISIBLE);
			initRemoveDialog(false);
		}else {
			viewHideActivity.setVisibility(View.GONE);
		}

	}

	/*private void sendCallDoneReport() {

		pd.show();
		//		RequestParams params = new RequestParams();
		//		params.put("appointment_id", DATA.selectedUserAppntID);

		String removeUrl = "";
		if (cbSendAVS.isChecked()) {
			removeUrl = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID+"/refcheckup/"+DATA.selectedUserQbid+"?send_avs=1";
		} else {
			removeUrl = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID+"/refcheckup/"+DATA.selectedUserQbid+"?send_avs=0";
		}
		DATA.print("-- url in sendCallDoneReport: "+removeUrl);
		client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		client.get(removeUrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in sendCAllDoneReport: "+content);

					try {
						jsonObject = new JSONObject(content);
						status = jsonObject.getString("success");
						if(status.equals("success")){
							finish();
						}else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						DATA.print("--online care exception in sendCallDoneReport: "+e);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: sendCallDoneReport, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure sendCallDoneReport: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}//sendCallDoneReport*/


	//---------------------------------------remove , add notes---------------------------------------------------
	Dialog dialogRemovePatient;
	CheckBox cbSendAVS,cbSendAVSToPCP,cbSendBillingSummarytoBillers,cbProcessTheBillingProcessDirectly,
			cbTCM,cbCC,cbHomeHealth,cbNursingHome;
	//RadioGroup rgPatientType;
	//EditText etNote;
	//TextView btnAddNotes;


	public void initRemoveDialog(boolean shouldShowAssignOptions){
		dialogRemovePatient = new Dialog(activity);
		dialogRemovePatient.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogRemovePatient.setContentView(R.layout.dialog_remove_from_queue);
		dialogRemovePatient.setCanceledOnTouchOutside(false);
		dialogRemovePatient.setCancelable(false);

		TextView btnRemove = dialogRemovePatient.findViewById(R.id.btnRemove);
		TextView btnNotRemove = dialogRemovePatient.findViewById(R.id.btnNotRemove);
		cbSendAVS = dialogRemovePatient.findViewById(R.id.cbSendAVS);
		cbSendAVSToPCP = dialogRemovePatient.findViewById(R.id.cbSendAVSToPCP);
		cbSendBillingSummarytoBillers = dialogRemovePatient.findViewById(R.id.cbSendBillingSummarytoBillers);
		cbProcessTheBillingProcessDirectly = dialogRemovePatient.findViewById(R.id.cbProcessTheBillingProcessDirectly);
		cbTCM = dialogRemovePatient.findViewById(R.id.cbTCM);
		cbCC = dialogRemovePatient.findViewById(R.id.cbCC);
		cbHomeHealth = dialogRemovePatient.findViewById(R.id.cbHomeHealth);
		cbNursingHome = dialogRemovePatient.findViewById(R.id.cbNursingHome);
		//rgPatientType = (RadioGroup) dialogRemovePatient.findViewById(R.id.rgPatientType);
		// btnAddNotes = (TextView) dialogRemovePatient.findViewById(R.id.btnAddNotes);
		//etNote = (EditText) dialogRemovePatient.findViewById(R.id.etNote);

		LinearLayout layAssignOptions = dialogRemovePatient.findViewById(R.id.layAssignOptions);
		int vis = shouldShowAssignOptions ? View.VISIBLE : View.GONE;
		layAssignOptions.setVisibility(vis);

		View d1 = dialogRemovePatient.findViewById(R.id.d1);
		View d2 = dialogRemovePatient.findViewById(R.id.d2);
		View d3 = dialogRemovePatient.findViewById(R.id.d3);

		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.btnRemove:
						dialogRemovePatient.dismiss();
						if (DATA.isFromAppointment) {
							saveAppointmentStatus();
						} else {
							sendCallDoneReport(); // to remove patient from queue
						}

						break;
					case R.id.btnNotRemove:
						dialogRemovePatient.dismiss();
						finish();
						break;
				/*case R.id.btnAddNotes:
					//dialogRemovePatient.dismiss();
					//initAddNotesDialog();
					//add notes

					String note = etNote.getText().toString();
					if (note.isEmpty()) {
						Toast.makeText(activity, "Please add a note !", Toast.LENGTH_SHORT).show();
					} else {

						if (checkInternetConnection.isConnectedToInternet()) {
							//add note here
							saveDrNotes(DATA.selectedUserAppntID, note);//userID here
						} else {
							Toast.makeText(activity, "No internet connection !", Toast.LENGTH_SHORT).show();
						}
					}

					break;*/

					default:
						break;
				}

			}
		};

		btnRemove.setOnClickListener(clickListener);
		btnNotRemove.setOnClickListener(clickListener);
		//btnAddNotes.setOnClickListener(clickListener);


		CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if(cbTCM.isChecked() && cbCC.isChecked()){
					d1.setBackgroundColor(getResources().getColor(android.R.color.white));
				}else {
					d1.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
				}
				if(cbCC.isChecked() && cbHomeHealth.isChecked()){
					d2.setBackgroundColor(getResources().getColor(android.R.color.white));
				}else {
					d2.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
				}
				if(cbHomeHealth.isChecked() && cbNursingHome.isChecked()){
					d3.setBackgroundColor(getResources().getColor(android.R.color.white));
				}else {
					d3.setBackgroundColor(getResources().getColor(R.color.app_blue_color));
				}

				switch (buttonView.getId()){
					case R.id.cbTCM:
						cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						break;
					case R.id.cbCC:
						cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						break;
					case R.id.cbHomeHealth:
						cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						break;
					case R.id.cbNursingHome:
						cbTCM.setTextColor(cbTCM.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbCC.setTextColor(cbCC.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbHomeHealth.setTextColor(cbHomeHealth.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						cbNursingHome.setTextColor(cbNursingHome.isChecked() ? getResources().getColor(android.R.color.white) : getResources().getColor(R.color.app_blue_color));
						break;
					default:
						break;
				}
			}
		};
		cbTCM.setOnCheckedChangeListener(onCheckedChangeListener);
		cbCC.setOnCheckedChangeListener(onCheckedChangeListener);
		cbHomeHealth.setOnCheckedChangeListener(onCheckedChangeListener);
		cbNursingHome.setOnCheckedChangeListener(onCheckedChangeListener);

		dialogRemovePatient.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogRemovePatient.show();
	}


	String patientType = "";
	private void sendCallDoneReport() {

		pd.setTitle("Please wait..");
		pd.setMessage("Removing the current appointment from queue");
		pd.show();

//		RequestParams params = new RequestParams();
//		params.put("appointment_id", DATA.selectedUserAppntID);

		client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		String removeURL = "";

		String apiPostFix = LiveCare.liveOrReferred == 1 ? "/refcheckup/" + DATA.selectedUserQbid : "/livecheckup";//note: refcheckup is in spe app

		if (cbSendAVS.isChecked()) {
			removeURL = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID + apiPostFix + "?send_avs=1";
		} else {
			removeURL = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID + apiPostFix +"?send_avs=0";
		}

		if (cbSendAVSToPCP.isChecked()) {
			removeURL = removeURL + "&is_avs_to_pcp=1";
		} else {
			removeURL = removeURL + "&is_avs_to_pcp=0";
		}


		if (cbSendBillingSummarytoBillers.isChecked()) {
			removeURL = removeURL + "&billing_summary=1";
		} else {
			removeURL = removeURL + "&billing_summary=0";
		}

		if (cbProcessTheBillingProcessDirectly.isChecked()) {
			removeURL = removeURL + "&billing_process=1";
		} else {
			removeURL = removeURL + "&billing_process=0";
		}

		patientType = "";
		if (cbTCM.isChecked()){
			patientType = patientType+"tcm,";
		}
		if (cbCC.isChecked()){
			patientType = patientType+"complex_care,";
		}
		if (cbHomeHealth.isChecked()){
			patientType = patientType+"home_health,";
		}
		if (cbNursingHome.isChecked()){
			patientType = patientType+"nursing_home,";
		}
		if(!patientType.isEmpty()){
			patientType = patientType.substring(0, patientType.lastIndexOf(","));
		}
		if (cbTCM.isChecked() || cbCC.isChecked() || cbHomeHealth.isChecked() || cbNursingHome.isChecked()){
			removeURL = removeURL + "&patient_category="+patientType;
		}else {
			patientType = "";
		}
		/*int checkedRGPatientType = rgPatientType.getCheckedRadioButtonId();
		switch (checkedRGPatientType){
			case R.id.rbTCM:
				removeURL = removeURL + "&patient_category=tcm";
				break;
			case R.id.rbCC:
				removeURL = removeURL + "&patient_category=complex_care";
				break;
			case R.id.rbHomeHealth:
				removeURL = removeURL + "&patient_category=home_health";
				break;
			case R.id.rbNursingHome:
				removeURL = removeURL + "&patient_category=nursing_home";
				break;
			default:

				break;
		}*/

		DATA.print("-- sendCallDoneReport: "+removeURL);

		String reqURL = removeURL;
		DATA.print("-- Request : "+reqURL);
		//DATA.print("-- params : "+params.toString());

		client.get(removeURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--online care response in sendCAllDoneReport: "+content);

					try {

						jsonObject = new JSONObject(content);

						status = jsonObject.getString("success");

						if(status.equals("success"))
						{

						/*Intent i = new Intent(activity, MainActivityNew.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(i);*/
							if (patientType.isEmpty()){
								finish();
							}else {
								/*Intent i = new Intent(activity, ActivityFindNurse.class);
								i.putExtra("patientType",patientType);
								//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);*/
								finish();
							}

						} else {
							Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
						}


					} catch (JSONException e) {
						DATA.print("--online care exception in getlivecare patients: "+e);

						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}


	public void saveAppointmentStatus() {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();
		params.put("id", DATA.drAppointmentModel.getId());
		params.put("status", "Completed");

		if (cbSendAVS.isChecked()) {
			params.put("send_avs", "1");
		} else {
			params.put("send_avs", "0");
		}

		if (cbSendAVSToPCP.isChecked()) {
			params.put("is_avs_to_pcp", "1");
		}else {
			params.put("is_avs_to_pcp", "0");
		}

		if (cbSendBillingSummarytoBillers.isChecked()) {
			params.put("billing_summary", "1");
		} else {
			params.put("billing_summary", "0");
		}

		if (cbProcessTheBillingProcessDirectly.isChecked()) {
			params.put("billing_process", "1");
		} else {
			params.put("billing_process", "0");
		}

		patientType = "";
		if (cbTCM.isChecked()){
			patientType = patientType+"tcm,";
		}
		if (cbCC.isChecked()){
			patientType = patientType+"complex_care,";
		}
		if (cbHomeHealth.isChecked()){
			patientType = patientType+"home_health,";
		}
		if (cbNursingHome.isChecked()){
			patientType = patientType+"nursing_home,";
		}
		if(!patientType.isEmpty()){
			patientType = patientType.substring(0, patientType.lastIndexOf(","));
		}
		if (cbTCM.isChecked() || cbCC.isChecked() || cbHomeHealth.isChecked() || cbNursingHome.isChecked()){
			//removeURL = removeURL + "&patient_category="+patientType;
			params.put("patient_category", patientType);
		}else {
			patientType = "";
		}
		/*int checkedRGPatientType = rgPatientType.getCheckedRadioButtonId();
		switch (checkedRGPatientType){
			case R.id.rbTCM:
				params.put("patient_category", "tcm");
				break;
			case R.id.rbCC:
				params.put("patient_category", "complex_care");
				break;
			case R.id.rbHomeHealth:
				params.put("patient_category", "home_health");
				break;
			case R.id.rbNursingHome:
				params.put("patient_category", "nursing_home");
				break;
			default:

				break;
		}*/

		//DATA.print("--in saveAppointmentStatus  params: "+params.toString());
		pd.show();

		String reqURL = DATA.baseUrl+"/saveAppointmentStatus";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL, params, new AsyncHttpResponseHandler() {




			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveAppointmentStatus "+content);
					//--reaponce in saveAppointmentStatus {"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							Toast.makeText(activity, "Appointment processed", Toast.LENGTH_SHORT).show();

					/*Intent i = new Intent(activity, MainActivityNew.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(i);*/

							if (patientType.isEmpty()){
								finish();
							}else {
								/*Intent i = new Intent(activity, ActivityFindNurse.class);
								i.putExtra("patientType",patientType);
								//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);*/
								finish();
							}
						} else {
							Toast.makeText(activity, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveAppointmentStatus





	public void saveDrNotes(String liveCareId,String note) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("id", liveCareId);
		params.put("notes", note);
		params.put("app_type", "specialist");
		//params.put("", liveCareId);
		DATA.print("--in saveNote params liveCareId "+liveCareId);
		client.post(DATA.baseUrl+"/saveNotes", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveDrNotes"+content);
					//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Your note saved successfully",0,0);
							//etNote.setText("");
							//btnAddNotes.setVisibility(View.GONE);
							//etNote.setVisibility(View.GONE);
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						DATA.print("--saveNotes json exception");
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}


					//dialog.dismiss();
				   /*if (connection.isConnectedToInternet()) {
						getDrNotes(DATA.selectedUserAppntID);
					} else {
						tvDrsNotes.setText("No internet connection. Can't load notes");
					}*/
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: saveDrNotes, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail saveDrNotes" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDrNotes


	EditText editText;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private void startVoiceRecognitionActivity(EditText editText) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// identifying your application to the Google service
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		// hint in the dialog
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");
		// hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// number of results
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		// recognition language
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		this.editText = editText;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			// do whatever you want with the results
			this.editText.setText(matches.get(0));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}










	//---------------------------------prescriptions-------------------------------------
	Dialog prescriptionsDialog;
	ImageView ivSignature;
	ExpandableHeightListView lvDrugs;

	ScrollView svSendPres;
	String vitals = "";
	String diagnoses = "";

	String treatments ="";

	String drugs = "",quantity = "",directions = "";
	String potency_code = "",refill = "";

	String start_date = "",end_date = "";

	public void initPrescripDialog() {
		DATA.drugBeans = new ArrayList<DrugBean>();
		prescriptionsDialog = new Dialog(AfterCallDialog.this);//, android.R.style.Theme_DeviceDefault_Light_Dialog
		prescriptionsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		prescriptionsDialog.setContentView(R.layout.send_prescription_dialog);
		prescriptionsDialog.setTitle("Send prescriptions to patient");
		prescriptionsDialog.setCanceledOnTouchOutside(false);
		prescriptionsDialog.setCancelable(false);

		TextView tvSendPres = prescriptionsDialog.findViewById(R.id.tvSendPresAndDone);
		TextView tvSendPresCencel = prescriptionsDialog.findViewById(R.id.tvSendPresCencel);
		final EditText etVitals = prescriptionsDialog.findViewById(R.id.etVitals);
		final EditText etDiagnoses = prescriptionsDialog.findViewById(R.id.etDiagnosis);
		//final EditText etTreatment =  (EditText) prescriptionsDialog.findViewById(R.id.etTreatment);
		lvDrugs = prescriptionsDialog.findViewById(R.id.lvDrugs);
		svSendPres = prescriptionsDialog.findViewById(R.id.svSendPres);

		TextView tvPrescPtName = prescriptionsDialog.findViewById(R.id.tvPrescPtName);
		TextView tvPrescDate = prescriptionsDialog.findViewById(R.id.tvPrescDate);

		TextView tvPrescPatientDOB = prescriptionsDialog.findViewById(R.id.tvPrescPatientDOB);
		TextView tvPrescPatientGender = prescriptionsDialog.findViewById(R.id.tvPrescPatientGender);
		TextView tvPrescPatientPhone = prescriptionsDialog.findViewById(R.id.tvPrescPatientPhone);
		TextView tvPrescPatientAdress = prescriptionsDialog.findViewById(R.id.tvPrescPatientAdress);
		TextView tvPrescPharmacy = prescriptionsDialog.findViewById(R.id.tvPrescPharmacy);
		TextView tvPrescPharmacyPhone = prescriptionsDialog.findViewById(R.id.tvPrescPharmacyPhone);
		TextView tvPrescDrName = prescriptionsDialog.findViewById(R.id.tvPrescDrName);
		TextView tvPrescDrPhone = prescriptionsDialog.findViewById(R.id.tvPrescDrPhone);

		if (DATA.selectedRefferedLiveCare != null) {
			tvPrescPatientDOB.setText(DATA.selectedRefferedLiveCare.birthdate);
			if (DATA.selectedRefferedLiveCare.gender.equals("1")) {
				tvPrescPatientGender.setText("Male");
			} else {
				tvPrescPatientGender.setText("Female");
			}
			tvPrescPatientPhone.setText(DATA.selectedRefferedLiveCare.patient_phone);
			tvPrescPatientAdress.setText(DATA.selectedRefferedLiveCare.residency);
			tvPrescPharmacy.setText(DATA.selectedRefferedLiveCare.StoreName);
			tvPrescPharmacyPhone.setText(DATA.selectedRefferedLiveCare.PhonePrimary);
		}

		tvPrescDrName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
		tvPrescDrPhone.setText(prefs.getString("mobile", ""));

		ivSignature = prescriptionsDialog.findViewById(R.id.ivSignature);
		ivSignature.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				initSignatureDialog();

			}
		});


		tvPrescPtName.setText("Patient Name: "+DATA.selectedUserCallName);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd  HH:mm:ss");
		String currentDateandTime = sdf.format(new Date());
		tvPrescDate.setText(currentDateandTime);

		tvSendPresCencel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				prescriptionsDialog.dismiss();
			}
		});

		tvSendPres.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				vitals = etVitals.getText().toString();
				diagnoses = etDiagnoses.getText().toString();
				//String treatments = etTreatment.getText().toString();
				/*String treatments ="";

				String drugs = "",quantity = "",directions = "";
				String potency_code = "",refill = "";

				String start_date = "",end_date = "";*/
				treatments ="";
				drugs = "";
				quantity = "";
				directions = "";
				potency_code = "";
				refill = "";
				start_date = "";
				end_date = "";

				if (DATA.drugBeans != null) {
					for (int i = 0; i < DATA.drugBeans.size(); i++) {
						treatments = treatments+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";

						drugs = drugs + DATA.drugBeans.get(i).getDrug_descriptor_id()+",";
						quantity = quantity + DATA.drugBeans.get(i).totalQuantity+",";
						directions = directions + DATA.drugBeans.get(i).instructions+"^|";

						potency_code = potency_code+DATA.drugBeans.get(i).getPotency_code()+",";
						refill = refill + DATA.drugBeans.get(i).refill+",";

						start_date = start_date + DATA.drugBeans.get(i).start_date+",";
						end_date = end_date + DATA.drugBeans.get(i).end_date+",";
					}
				}else {
					treatments ="";

					drugs = "";
					quantity = "";
					directions = "";

					potency_code = "";
					refill = "";

					start_date = "";
					end_date = "";
				}
				if (vitals.isEmpty() || diagnoses.isEmpty() || treatments.isEmpty() || drugs.isEmpty() || quantity.isEmpty() || directions.isEmpty() ||
						potency_code.isEmpty() || refill.isEmpty()) {
					Toast.makeText(activity, "All fields are required", Toast.LENGTH_SHORT).show();
					if (vitals.isEmpty()) {
						etVitals.setError("Vitals can't be empty");
					}
					if (diagnoses.isEmpty()) {
						etDiagnoses.setError("Diagnoses can't be empty");
					}
					svSendPres.scrollTo(0, 0);
				}else if(signaturePath.isEmpty()){
					Toast.makeText(activity, "Please add your signature to prescriptions", Toast.LENGTH_SHORT).show();
				}else {

					drugs = drugs.substring(0, (drugs.length()-1));
					quantity = quantity.substring(0, (quantity.length()-1));
					directions = directions.substring(0, (directions.length()-2));

					potency_code = potency_code.substring(0, (potency_code.length()-1));
					refill = refill.substring(0, (refill.length()-1));

					start_date = start_date.substring(0, (start_date.length()-1));
					end_date = end_date.substring(0, (end_date.length()-1));



					new AlertDialog.Builder(activity).setTitle("Confirm").
							setMessage("Are you sure? You are going to send prescriptions request to the pharmacy. Patient will recieve prescriptions from the pharmacy.").
							setPositiveButton("Yes", new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									if (checkInternetConnection.isConnectedToInternet()) {
										sendPresscription(prefs.getString("id", ""), DATA.selectedUserCallId, vitals, diagnoses, treatments,signaturePath,
												DATA.selectedUserQbid,drugs,quantity,directions,potency_code,refill,start_date,end_date);//DATA.selectedUserAppntID old_live_checkup_id
									} else {
										Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
									}
								}
							}).setNegativeButton("No", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).show();
				}



			}
		});



		ImageView ic_mike_vitals = prescriptionsDialog.findViewById(R.id.ic_mike_vitals);
		ic_mike_vitals.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startVoiceRecognitionActivity(etVitals);
			}
		});

		ImageView ic_mike_Diagnosis = prescriptionsDialog.findViewById(R.id.ic_mike_Diagnosis);
		ic_mike_Diagnosis.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startVoiceRecognitionActivity(etDiagnoses);
			}
		});




		Button btnAddDrugs = prescriptionsDialog.findViewById(R.id.btnAddDrugs);
		btnAddDrugs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				initDrugsDialog();
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(prescriptionsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		prescriptionsDialog.show();
		prescriptionsDialog.getWindow().setAttributes(lp);
	}
	public void sendPresscription(String doctor_id,String patient_id,String vitals,String diagnosis,String treatment,
								  String signFilePath,String live_checkup_id, String drugs, String quantity, String directions,
								  String potency_code,String refill,String start_date,String end_date) {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);
		params.put("patient_id", patient_id);
		params.put("vitals", vitals);
		params.put("diagnosis", diagnosis);
		params.put("treatment", treatment);
		params.put("send_email", "1");

		//params.put("live_checkup_id", live_checkup_id);   removed by Maaz
		params.put("drugs", drugs);
		params.put("quantity", quantity);
		params.put("directions", directions);
		params.put("potency_code", potency_code);
		params.put("refill", refill);

		params.put("start_date", start_date);
		params.put("end_date", end_date);

		try {
			params.put("signature", new File(signFilePath));
		} catch (FileNotFoundException e1) {
			DATA.print("-- sign file not found !");
			e1.printStackTrace();
		}
		//params.put("send_email", "1"); if dr want to email

		DATA.print("--params in savePrescriptions: "+params.toString());

		pd.setTitle("Please wait..");
		pd.setMessage("Sending prescriptions");
		pd.show();
		client.post(DATA.baseUrl+"/savePrescriptions", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--responce "+content);
					//responce {"success":1,"message":"Saved"}
					try {
						JSONObject obj = new JSONObject(content);
						String msg = obj.getString("message");
						if (obj.has("success")) {

							prescriptionsDialog.dismiss();

							//showMessageBox(AfterCallDialog.this, "Success", "Prescriptions sent to patient. Remove patient from live care queue?");
							//initRemoveDialog();

							AlertDialog.Builder b = new AlertDialog.Builder(activity);
							b.setMessage("Prescriptions has been successfully sent.").setPositiveButton("OK", null);
							AlertDialog d = b.create();
							d.show();
							d.setOnDismissListener(new DialogInterface.OnDismissListener() {

								@Override
								public void onDismiss(DialogInterface arg0) {
									// TODO Auto-generated method stub
									startActivity(new Intent(activity,ActivitySoapNotes.class));
								}
							});
						} else {

							//showMessageBoxError(AfterCallDialog.this, "Error", msg);
							//Toast.makeText(activity, "Something went wrong. Presscription not saved. Please try again.", Toast.LENGTH_LONG).show();
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: savePrescriptions, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					//forgetMsg = "Opps! Some thing went wrong please try again";
					DATA.print("--onfail savePrescriptions"+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}
	//-----------------------------------Signature------------------------------------------
	private String signaturePath = "";
	Dialog signatureDialog;
	SignaturePad mSignaturePad;
	Button mClearButton;
	Button mSaveButton;
	public void initSignatureDialog() {
		signatureDialog = new Dialog(activity);
		signatureDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		signatureDialog.setContentView(R.layout.dialog_signature);

		mSignaturePad = signatureDialog.findViewById(R.id.signature_pad);
		mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
			@Override
			public void onStartSigning() {
				Toast.makeText(activity, "OnStartSigning", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onSigned() {
				mSaveButton.setEnabled(true);
				mClearButton.setEnabled(true);
			}

			@Override
			public void onClear() {
				mSaveButton.setEnabled(false);
				mClearButton.setEnabled(false);
			}
		});

		mClearButton = signatureDialog.findViewById(R.id.clear_button);
		mSaveButton = signatureDialog.findViewById(R.id.save_button);

		mClearButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mSignaturePad.clear();
			}
		});

		mSaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
				String signPath = addSignatureToGallery(signatureBitmap);
				if(signPath != null) {
					Toast.makeText(activity, "Signature saved into the Gallery", Toast.LENGTH_SHORT).show();
					signaturePath = signPath;
					signatureDialog.dismiss();
					//ivSignature.setScaleType(ScaleType.CENTER_CROP);
					ivSignature.setImageBitmap(BitmapFactory.decodeFile(signPath));
				} else {
					Toast.makeText(activity, "Unable to store the signature", Toast.LENGTH_SHORT).show();
				}
			}
		});
		signatureDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		signatureDialog.show();
	}//end init

	public File getAlbumStorageDir(String albumName) {
		// Get the directory for the user's public pictures directory.
		File file = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), albumName);
		if (!file.mkdirs()) {
			Log.e("SignaturePad", "Directory not created");
		}
		return file;
	}

	public void saveBitmapToJPG(Bitmap bitmap, File photo) throws IOException {
		Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(newBitmap);
		canvas.drawColor(Color.WHITE);
		canvas.drawBitmap(bitmap, 0, 0, null);
		OutputStream stream = new FileOutputStream(photo);
		newBitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
		stream.close();
	}
	public String addSignatureToGallery(Bitmap signature) {
		//boolean result = false;
		try {
			File photo = new File(getAlbumStorageDir("Online Care Dr"), String.format("Signature_%d.jpg", System.currentTimeMillis()));
			saveBitmapToJPG(signature, photo);
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
			Uri contentUri = Uri.fromFile(photo);
			mediaScanIntent.setData(contentUri);
			AfterCallDialog.this.sendBroadcast(mediaScanIntent);
			//result = true;
			return photo.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}


	//----------------------------------Drugs medications--------------------------------------------------

	DrugBean selectedDrugBean;
	Dialog drugsDialog;
	Spinner spinnerDrugName;
	//String dosage_form = "";


	//String dosage = "",freq1 = "",freq2 = "";//dosage_formVal = "",
	public void initDrugsDialog() {
		drugsDialog = new Dialog(activity);
		drugsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		drugsDialog.setContentView(R.layout.dialog_add_drugs);
		drugsDialog.setCancelable(false);

		final EditText etSearchQuery = drugsDialog.findViewById(R.id.etSearchQuery);
		ImageView ivSearchQuery = drugsDialog.findViewById(R.id.ivSearchQuery);
		//final Spinner spinnerDrugForm = (Spinner) drugsDialog.findViewById(R.id.spinnerDrugForm);
		spinnerDrugName = drugsDialog.findViewById(R.id.spinnerDrugName);
		final Spinner spinnerRoute = drugsDialog.findViewById(R.id.spinner1);
		final Spinner spinnerDosageForm = drugsDialog.findViewById(R.id.spinner2);
		final Spinner spinnerStrength = drugsDialog.findViewById(R.id.spinner3);
		final Spinner spinnerUnit = drugsDialog.findViewById(R.id.spinner4);



		 /*Spinner spinnerDosage = (Spinner) drugsDialog.findViewById(R.id.spinner5);
		 Spinner spinnerFrequency1 = (Spinner) drugsDialog.findViewById(R.id.spinner6);
		 Spinner spinnerFrequency2 = (Spinner) drugsDialog.findViewById(R.id.spinner7);*/
		final Spinner spinnerRefill = drugsDialog.findViewById(R.id.spinner8);

		final Spinner spinner_potency_unit = drugsDialog.findViewById(R.id.spinner_potency_unit);
		final EditText etTotalQuantity = drugsDialog.findViewById(R.id.etTotalQuantity);

		Button btnAddDrugs = drugsDialog.findViewById(R.id.btnAddDrugs);
		Button btnAddDrugsCancel = drugsDialog.findViewById(R.id.btnAddDrugsCancel);
		final EditText etStartDate = drugsDialog.findViewById(R.id.etStartDate);
		final EditText etEndtDate = drugsDialog.findViewById(R.id.etEndtDate);

		final EditText etInstructions = drugsDialog.findViewById(R.id.etInstructions);
		ImageView ic_mike_Instructions = drugsDialog.findViewById(R.id.ic_mike_Instructions);
		ic_mike_Instructions.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startVoiceRecognitionActivity(etInstructions);
			}
		});

		etStartDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etStartDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		etEndtDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment(etEndtDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		 /*if (dosageFormsBeans != null) {
			 ArrayAdapter<DosageFormsBean> spDosageFormAdapter = new ArrayAdapter<DosageFormsBean>(
				        activity,
				        R.layout.view_spinner_item,
				        dosageFormsBeans
				);
			 spDosageFormAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			 spinnerDrugForm.setAdapter(spDosageFormAdapter);
		} else {
			if (checkInternetConnection.isConnectedToInternet()) {
				getDosageForms();
			} else {
				Toast.makeText(activity, "No internet connection", 0).show();
			}
		}*/


		 /*spinnerDrugForm.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				dosage_form = dosageFormsBeans.get(arg2).getField_value();
				dosage_formVal = dosageFormsBeans.get(arg2).getDosage_form();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});*/

		//final String[] dosage={"1","2","3","4","5","6","7","8","9","10"};//,"11","12","13","14","15","16","17","18","19","20"

		 /*ArrayAdapter<String> spDosageAdapter = new ArrayAdapter<String>(
			        this,
			         R.layout.spinner_item_lay,
			        dosage
			);
		 spDosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerDosage.setAdapter(spDosageAdapter);


			final String[] f1={"once","twice","thrice"};
			 ArrayAdapter<String> spFreq1Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f1
				);
			 spFreq1Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency1.setAdapter(spFreq1Adapter);

			final String[] f2={"day","week","month"};
			 ArrayAdapter<String> spFreq2Adapter = new ArrayAdapter<String>(
				        this,
				         R.layout.spinner_item_lay,
				        f2
				);
			 spFreq2Adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFrequency2.setAdapter(spFreq2Adapter);
		    spinnerRefill.setAdapter(spDosageAdapter);

		    spinnerDosage.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					AfterCallDialog.this.dosage = dosage[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency1.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq1 = f1[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});
		    spinnerFrequency2.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					freq2 = f2[arg2];
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}
			});*/

		ivSearchQuery.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length()==1) {
					Toast.makeText(activity, "Please enter at least 2 characters of a medication name to search the medication", Toast.LENGTH_LONG).show();
				}else if (!checkInternetConnection.isConnectedToInternet()) {
					Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
				} else {
					getDrugs(etSearchQuery.getText().toString().trim());
				}
			}
		});


		spinnerDrugName.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
									   int pos, long arg3) {
				// TODO Auto-generated method stub
				selectedDrugBean = drugBeans.get(pos);

				ArrayList<String> route = new ArrayList<String>();
				route.add(selectedDrugBean.getRoute());

				ArrayList<String> dosage_form = new ArrayList<String>();
				dosage_form.add(selectedDrugBean.getDosage_form());

				ArrayList<String> strength = new ArrayList<String>();
				strength.add(selectedDrugBean.getStrength());

				ArrayList<String> strength_unit_of_measure = new ArrayList<String>();
				strength_unit_of_measure.add(selectedDrugBean.getStrength_unit_of_measure());


//------------------------potency_unit------------------------------------------------

				final ArrayList<PotencyUnitBean> potencyUnitBeans = new ArrayList<>();
				String[] pu = selectedDrugBean.getPotency_unit().split(",");
				String[] pc = selectedDrugBean.getPotency_code().split(",");

				for (int i = 0; i < pu.length; i++) {
					potencyUnitBeans.add(new PotencyUnitBean(pu[i], pc[i]));
				}

				spinner_potency_unit.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int pos, long arg3) {
						// TODO Auto-generated method stub
						selectedDrugBean.setPotency_code(potencyUnitBeans.get(pos).getPotency_code());
						selectedDrugBean.setPotency_unit(potencyUnitBeans.get(pos).getPotency_unit());
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub

					}
				});

				ArrayAdapter<PotencyUnitBean> spPtencyUnitAdapter = new ArrayAdapter<PotencyUnitBean>(
						activity,
						R.layout.spinner_item_lay,
						potencyUnitBeans
				);
				spPtencyUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinner_potency_unit.setAdapter(spPtencyUnitAdapter);

				//--------------set Refill value for selectedDrugBean--------------------------
				final String[] refillArray={"0","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15"
						,"16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31",
						"32","33","34","35","36","37","38","39","40","41","42","43","44","45","46","47","48","49","50",
						"51","52","53","54","55","56","57","58","59","60","61","62","63","64","65","66","67","68","69","70",
						"71","72","73","74","75","76","77","78","79","80","81","82","83","84","85","86","87","88"
						,"89","90","91","92","93","94","95","96","97","98","99"};
				spinnerRefill.setOnItemSelectedListener(new OnItemSelectedListener() {

					@Override
					public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						// TODO Auto-generated method stub
						selectedDrugBean.refill = refillArray[arg2];
					}

					@Override
					public void onNothingSelected(AdapterView<?> arg0) {
						// TODO Auto-generated method stub
					}
				});


				ArrayAdapter<String> spRefillAdapter = new ArrayAdapter<String>(
						activity,
						R.layout.spinner_item_lay,
						refillArray
				);
				spRefillAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerRefill.setAdapter(spRefillAdapter);

				//---------------------set Refill value for selectedDrugBean----------------------

				//------------------------potency_unit----------------------------------------------------


				ArrayAdapter<String> sprouteAdapter = new ArrayAdapter<String>(
						activity,
						R.layout.spinner_item_lay,
						route
				);
				sprouteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerRoute.setAdapter(sprouteAdapter);

				ArrayAdapter<String> spdosage_formAdapter = new ArrayAdapter<String>(
						activity,
						R.layout.spinner_item_lay,
						dosage_form
				);
				spdosage_formAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerDosageForm.setAdapter(spdosage_formAdapter);

				ArrayAdapter<String> spstrengthAdapter = new ArrayAdapter<String>(
						activity,
						R.layout.spinner_item_lay,
						strength
				);
				spstrengthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerStrength.setAdapter(spstrengthAdapter);

				ArrayAdapter<String> spstrength_unit_of_measureAdapter = new ArrayAdapter<String>(
						activity,
						R.layout.spinner_item_lay,
						strength_unit_of_measure
				);
				spstrength_unit_of_measureAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				spinnerUnit.setAdapter(spstrength_unit_of_measureAdapter);

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

		etTotalQuantity.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (arg0.toString().startsWith(".")) {
					etTotalQuantity.setText("0"+arg0.toString());
					etTotalQuantity.setSelection(etTotalQuantity.getText().length());
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		etTotalQuantity.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					if (!etTotalQuantity.getText().toString().isEmpty()) {
						etTotalQuantity.setText(validateQuantity(etTotalQuantity.getText().toString()));
						Toast.makeText(activity, "Leading and trailing zeeros will be truncated from quantity.", Toast.LENGTH_LONG).show();
					}
				}
			}
		});
		btnAddDrugsCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				drugsDialog.dismiss();
			}
		});

		btnAddDrugs.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (selectedDrugBean!=null) {

					if (etStartDate.getText().toString().equalsIgnoreCase("Start Date") ||
							etEndtDate.getText().toString().equalsIgnoreCase("End Date")) {
						Toast.makeText(activity, "Please add medicine start date and end date", Toast.LENGTH_LONG).show();
					}else if (etTotalQuantity.getText().toString().isEmpty()) {
						Toast.makeText(activity, "Please enter total quantity for the medicine", Toast.LENGTH_SHORT).show();
					}else if (etInstructions.getText().toString().trim().isEmpty()) {
						Toast.makeText(activity, "Please enter usage instructions for the medicine", Toast.LENGTH_SHORT).show();
					} else {
						selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name());
						/*+"\n"+AfterCallDialog.this.dosage+" "+selectedDrugBean.getDfdesc()+" "+freq1+" a "+freq2
						+"\nFrom date: "+etStartDate.getText().toString()+" To date: "+etEndtDate.getText().toString()*/

						//this is total quantity for the medicine i.e sent in savePrescription
						//selectedDrugBean.setDosage_form(validateQuantity(etTotalQuantity.getText().toString()));//AfterCallDialog.this.dosage
						selectedDrugBean.totalQuantity = validateQuantity(etTotalQuantity.getText().toString());

						selectedDrugBean.start_date = etStartDate.getText().toString();
						selectedDrugBean.end_date = etEndtDate.getText().toString();

						if (!etInstructions.getText().toString().trim().isEmpty()) {
							/*selectedDrugBean.setDrug_name(selectedDrugBean.getDrug_name()
									+"\n\nUsage Instructions: "+etInstructions.getText().toString().trim()+"\n");*/

							selectedDrugBean.instructions = etInstructions.getText().toString().trim();
						}

						selectedDrugBean.duration = getDateDifference(etStartDate.getText().toString(), etEndtDate.getText().toString());

						DATA.drugBeans.add(selectedDrugBean);
						DrugsAdapter adapter = new DrugsAdapter(activity);
						lvDrugs.setAdapter(adapter);
						lvDrugs.setExpanded(true);// This actually does the magic
						//lvDrugs.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
						drugsDialog.dismiss();
					}
				} else {
					Toast.makeText(activity, "Please select a medicine to add", Toast.LENGTH_LONG).show();
				}

			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(drugsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		drugsDialog.show();
		drugsDialog.getWindow().setAttributes(lp);
	}


	ArrayList<DrugBean> drugBeans;
	public void getDrugs(String keyword) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("keyword", keyword);
		//params.put("dosage_form", dosage_form);

		client.post(DATA.baseUrl+"/getDrugs", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getDrugs "+content);
					try {
						drugBeans = new ArrayList<DrugBean>();
						DrugBean temp;

						JSONArray jsonArray = new JSONObject(content).getJSONArray("data");

						for (int i = 0; i < jsonArray.length(); i++) {
							String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
							String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
							String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
							String code = jsonArray.getJSONObject(i).getString("code");
							String route = jsonArray.getJSONObject(i).getString("route");
							String strength = jsonArray.getJSONObject(i).getString("strength");
							String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
							String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
							String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
							String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

							String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
							String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

							temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc,potency_unit,potency_code);
							drugBeans.add(temp);
							temp = null;
						}

						//setData here

						ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
								activity,
								R.layout.view_spinner_item,
								drugBeans
						);
						spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinnerDrugName.setAdapter(spDrugNameAdapter);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getDrugs, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail getDrugs " +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getDrugs

	public String getDateDifference(String startDate, String endDate) {
		int daysDiff = 0;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		try {
			Date sDate = sdf.parse(startDate);
			Date eDate = sdf.parse(endDate);
			long diff = eDate.getTime() - sDate.getTime();

			DATA.print("--days: "+(int) (diff / (1000*60*60*24)));
			daysDiff = (int) (diff / (1000*60*60*24));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return daysDiff+" Days";
	}

	public String validateQuantity(String q){
		int a = 0;
		float b = 0.0f;

		String result = "";


		String[] arr = q.split("\\.");

		//DATA.print("-- arr.length "+arr.length);
		//DATA.print("-- q = "+q);

		if (arr.length > 1) {

			a = Integer.parseInt(arr[0]);
			b = Float.parseFloat("."+arr[1]);

			if (b > 0) {
				result = (a+b)+"";
			} else {
				result = a+"";
			}

		} else if(arr.length == 1){
			a = Integer.parseInt(arr[0]);

			result = a+"";
		}



		DATA.print("--result: "+result);
		return result;
	}
}
