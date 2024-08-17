package com.app.mhcsn_ma;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_ma.api.ApiManager;
import com.app.mhcsn_ma.util.CheckInternetConnection;
import com.app.mhcsn_ma.util.DATA;
import com.app.mhcsn_ma.util.EasyAES;
import com.app.mhcsn_ma.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class DrApointmentDetail extends BaseActivity {
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	ProgressDialog pd;
	CircularImageView imgPatientImage;
	TextView tvPatientName,tvApptSymp,tvApptCond,tvApptDesc,tvApptDate,tvApptTime,tvApptStatus,tvApptPainBodyPart,tvApptPainSeverity;
	Button btnApptCancel,btnCall,btnSendMsg,btnConfirmAppointment,btnCancelAppointment,btnSelPtViewReports,btnBackToHome;
	ImageView ivIsonline;
	String is_online = "0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dr_apointment_detail);

		activity = DrApointmentDetail.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}
		pd.setMessage("Please wait...");
		pd.setCanceledOnTouchOutside(false);

		tvPatientName = (TextView) findViewById(R.id.tvPatientName);
		imgPatientImage = (CircularImageView) findViewById(R.id.imgPatientImage);
		tvApptSymp = (TextView) findViewById(R.id.tvApptSymp);
		tvApptCond = (TextView) findViewById(R.id.tvApptCond);
		tvApptDesc = (TextView) findViewById(R.id.tvApptDesc);
		tvApptDate = (TextView) findViewById(R.id.tvApptDate);
		tvApptTime = (TextView) findViewById(R.id.tvApptTime);
		tvApptStatus = (TextView) findViewById(R.id.tvApptStatus);
		tvApptPainBodyPart = (TextView) findViewById(R.id.tvApptPainBodyPart);
		tvApptPainSeverity = (TextView) findViewById(R.id.tvApptPainSeverity);
		ivIsonline = findViewById(R.id.ivIsonline);

		btnApptCancel = (Button) findViewById(R.id.btnApptCancel);
		btnCall = (Button) findViewById(R.id.btnCall);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnConfirmAppointment = (Button) findViewById(R.id.btnConfirmAppointment);
		btnCancelAppointment = (Button) findViewById(R.id.btnCancelAppointment);
		btnSelPtViewReports = (Button) findViewById(R.id.btnSelPtViewReports);
		btnBackToHome = findViewById(R.id.btnBackToHome);
		//btnApptCancel.setVisibility(View.GONE);

		btnCancelAppointment.setVisibility(View.GONE);

		tvPatientName.setText(DATA.drAppointmentModel.getFirst_name()+" "+DATA.drAppointmentModel.getLast_name());

		DATA.loadImageFromURL(DATA.drAppointmentModel.getPatient_image(), R.drawable.icon_call_screen, imgPatientImage);

		tvApptSymp.setText(DATA.drAppointmentModel.getSymptom_name());
		tvApptCond.setText(DATA.drAppointmentModel.getCondition_name());
		tvApptDesc.setText(DATA.drAppointmentModel.getDescription());
		tvApptDate.setText(DATA.drAppointmentModel.getAppointment_date());
		tvApptTime.setText(DATA.drAppointmentModel.getFrom_time()+" To "+DATA.drAppointmentModel.getTo_time());
		tvApptStatus.setText(DATA.drAppointmentModel.getStatus());

		if (DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Pending") || DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Declined")) {
			btnCall.setVisibility(View.GONE);
			btnSendMsg.setVisibility(View.GONE);
			btnSelPtViewReports.setVisibility(View.GONE);
			btnConfirmAppointment.setVisibility(View.VISIBLE);
			//btnCancelAppointment.setVisibility(View.VISIBLE);
			btnBackToHome.setVisibility(View.GONE);
		}else if (DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Confirmed")) {
			tvApptStatus.setTextColor(Color.parseColor("#3A7D00"));
			btnConfirmAppointment.setVisibility(View.GONE);
			//btnCancelAppointment.setVisibility(View.GONE);
			btnCall.setVisibility(View.VISIBLE);
			btnSendMsg.setVisibility(View.VISIBLE);
			btnSelPtViewReports.setVisibility(View.VISIBLE);
			btnBackToHome.setVisibility(View.VISIBLE);
		}else if (DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Cancelled")) {
			btnCall.setVisibility(View.GONE);
			btnSendMsg.setVisibility(View.GONE);
			btnSelPtViewReports.setVisibility(View.GONE);
			btnConfirmAppointment.setVisibility(View.GONE);
			//btnCancelAppointment.setVisibility(View.VISIBLE);
			tvApptStatus.setText("Cancelled by patient");
			tvApptStatus.setTextColor(getResources().getColor(R.color.theme_red));
			btnBackToHome.setVisibility(View.VISIBLE);
		}

		btnBackToHome.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(intent);
			}
		});

		btnConfirmAppointment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Confirm")
						.setMessage("Are you sure? Do you want to confirm the appointment?")
						.setPositiveButton("Yes Confirm", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (checkInternetConnection.isConnectedToInternet()) {
									saveAppointmentStatus(DATA.drAppointmentModel.getId(), "Confirmed");
								} else {
									customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
								}
							}
						})
						.setNegativeButton("Not Now", null)
						.create()
						.show();
			}
		});
		btnCancelAppointment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Confirm")
						.setMessage("Are you sure? Do you want to cancel the appointment?")
						.setPositiveButton("Yes Cancel", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								if (checkInternetConnection.isConnectedToInternet()) {
									saveAppointmentStatus(DATA.drAppointmentModel.getId(), "Declined");
								} else {
									customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
								}
							}
						})
						.setNegativeButton("Not Now", null)
						.create()
						.show();
			}
		});

		btnSendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				initMsgDialog();
			}
		});

		btnCall.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(is_online.equals("1")){
					//DATA.selectedUserQbid = DATA.allAppointments.get(position).patients_qbid;
					DATA.selectedUserCallId = DATA.drAppointmentModel.getPatient_id();
					DATA.selectedUserCallName = DATA.drAppointmentModel.getFirst_name()+" "+DATA.drAppointmentModel.getLast_name();
				/*DATA.selectedUserCallSympTom = DATA.allAppointments.get(position).symptom_name;
				DATA.selectedUserCallCondition = DATA.allAppointments.get(position).condition_name;
				DATA.selectedUserCallDescription = DATA.allAppointments.get(position).description;
				DATA.selectedUserAppntID = DATA.allAppointments.get(position).liveCheckupId;*/

				/*DATA.selectedUserLatitude =  DATA.allAppointments.get(position).latitude;
				DATA.selectedUserLongitude =  DATA.allAppointments.get(position).longitude;*/

					DATA.selectedUserCallImage = DATA.drAppointmentModel.getPatient_image();

					//filterReports(DATA.selectedUserCallId);

					DATA.isFromDocToDoc = false;
					//DATA.isConfirence = false;

				/*Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(myIntent);
				finish();	*/
					DATA.incomingCall = false;
					DATA.isVideoCallFromLiveCare = true;//to show remove dialog on aftercalldialog
					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
					myIntent.putExtra("isFromAppointment", true);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(myIntent);
					finish();
				}else {
					new AlertDialog.Builder(activity).setTitle("Patient Offline")
							.setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
							.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									initMsgDialog();
								}
							}).setNegativeButton("Cancel",null).show();
				}
			}
		});

		btnSelPtViewReports.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				startActivity(new Intent(DrApointmentDetail.this,ViewReport.class));
			}
		});


		getAppointmentDetails();
	}


	public void saveAppointmentStatus(String appointmentId, final String status) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("id", appointmentId);
		params.put("status", status);

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
							Toast.makeText(activity, "Appointment processed", 0).show();
							DATA.drAppointmentModel.setStatus(status);
							recreate();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
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



	Dialog dialogSendMessage;
	public void initMsgDialog() {
		dialogSendMessage = new Dialog(activity);
		dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSendMessage.setContentView(R.layout.dialog_send_msg);
		final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
		TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);

		TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);
		tvMsgPatientName.setText("To: "+DATA.drAppointmentModel.getFirst_name()+" "+DATA.drAppointmentModel.getLast_name());

		btnSendMsg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etMsg.getText().toString().isEmpty()) {
					Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						sendMsg(DATA.drAppointmentModel.getPatient_id(), etMsg.getText().toString());
					} else {
						Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
					}
				}

			}
		});


		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialogSendMessage.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		dialogSendMessage.show();
		dialogSendMessage.getWindow().setAttributes(lp);
	}

	public void sendMsg(String patientId,String msgText) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("patient_id", patientId);
		params.put("message_text", EasyAES.encryptString(msgText));
		params.put("from", "doctor");
		params.put("to", "patient");

		String reqURL = DATA.baseUrl+"/sendmessage";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL,params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in sendMsg"+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							dialogSendMessage.dismiss();
							Toast.makeText(activity, "Message Sent", Toast.LENGTH_LONG).show();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
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

	}//end sendMsg


	public void getAppointmentDetails(){
		String api = ApiManager.PATIENT_DETAIL_APPTMNT+DATA.drAppointmentModel.getPatient_id()+"/"+DATA.drAppointmentModel.getId();
		ApiManager apiManager = new ApiManager(api,"post",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.contains(ApiManager.PATIENT_DETAIL_APPTMNT)){
			try {
				JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

				JSONObject patient_data = jsonObject.getJSONObject("patient_data");
				is_online = patient_data.getString("is_online");

				if(is_online.equalsIgnoreCase("1")){
					ivIsonline.setImageResource(R.drawable.icon_online);
				}else {
					ivIsonline.setImageResource(R.drawable.icon_notification);
				}

				if(!jsonObject.getString("virtual_visit").isEmpty()){
					JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");
					if(virtual_visit.has("additional_data")){
						String additional_data = virtual_visit.getString("additional_data");
						if(!additional_data.isEmpty()){
							JSONObject additional_dataJSON = new JSONObject(additional_data);
							if(additional_dataJSON.has("pain_where")){
								//tvPain.setText(additional_dataJSON.getString("pain_where"));
							}
							if(additional_dataJSON.has("pain_severity")){
								tvApptPainSeverity.setText(additional_dataJSON.getString("pain_severity"));
							}
						}
					}

					if(virtual_visit.has("pain_related")){
						tvApptPainBodyPart.setText(virtual_visit.getString("pain_related"));
					}
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
