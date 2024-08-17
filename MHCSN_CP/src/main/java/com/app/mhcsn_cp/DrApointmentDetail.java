package com.app.mhcsn_cp;

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
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.api.ApiManager;
import com.app.mhcsn_cp.util.CheckInternetConnection;
import com.app.mhcsn_cp.util.DATA;
import com.app.mhcsn_cp.util.EasyAES;
import com.app.mhcsn_cp.util.GloabalMethods;
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
	TextView tvPatientName,tvApptSymp,tvApptCond,tvApptDesc,tvApptDate,tvApptTime,tvApptStatus;
	Button btnApptCancel,btnCall,btnSendMsg,btnConfirmAppointment,btnCancelAppointment,btnSelPtViewReports,btnSelPtViewSelfAssess;
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

		btnApptCancel = (Button) findViewById(R.id.btnApptCancel);
		btnCall = (Button) findViewById(R.id.btnCall);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);
		btnConfirmAppointment = (Button) findViewById(R.id.btnConfirmAppointment);
		btnCancelAppointment = (Button) findViewById(R.id.btnCancelAppointment);
		btnSelPtViewReports = (Button) findViewById(R.id.btnSelPtViewReports);
		btnSelPtViewSelfAssess = findViewById(R.id.btnSelPtViewSelfAssess);
		//btnApptCancel.setVisibility(View.GONE);

		//btnCancelAppointment.setVisibility(View.GONE);

		tvPatientName.setText(DATA.drAppointmentModel.getFirst_name()+" "+DATA.drAppointmentModel.getLast_name());

		DATA.loadImageFromURL(DATA.drAppointmentModel.getPatient_image(), R.drawable.icon_call_screen,imgPatientImage);
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
			btnSelPtViewSelfAssess.setVisibility(View.GONE);
			btnConfirmAppointment.setVisibility(View.VISIBLE);
			btnCancelAppointment.setVisibility(View.VISIBLE);
		}else if (DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Confirmed")) {
			tvApptStatus.setTextColor(Color.parseColor("#3A7D00"));
			btnConfirmAppointment.setVisibility(View.GONE);
			btnCancelAppointment.setVisibility(View.VISIBLE);
			btnCall.setVisibility(View.VISIBLE);
			btnSendMsg.setVisibility(View.VISIBLE);
			btnSelPtViewReports.setVisibility(View.VISIBLE);
			btnSelPtViewSelfAssess.setVisibility(View.VISIBLE);
		}else if (DATA.drAppointmentModel.getStatus().equalsIgnoreCase("Cancelled")) {
			btnCall.setVisibility(View.GONE);
			btnSendMsg.setVisibility(View.GONE);
			btnSelPtViewReports.setVisibility(View.GONE);
			btnConfirmAppointment.setVisibility(View.GONE);
			btnSelPtViewSelfAssess.setVisibility(View.GONE);
			//btnCancelAppointment.setVisibility(View.VISIBLE);
			tvApptStatus.setText("Cancelled by patient");
			tvApptStatus.setTextColor(getResources().getColor(R.color.theme_red));
		}

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
			}
		});

		btnSelPtViewReports.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//startActivity(new Intent(DrApointmentDetail.this,ViewReport.class));
				openActivity.open(ActivityPinnedReports.class, false);//  ViewReport.class
			}
		});

		btnSelPtViewSelfAssess.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//new GloabalMethods(activity).showWellnessOptionsDialog();
				getAttachSurveryApptmnt();
			}
		});


		getPatientDetail();
	}


	public void saveAppointmentStatus(String appointmentId, final String status) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("id", appointmentId);
		params.put("status", status);

		DATA.print("--in saveAppointmentStatus  params: "+params.toString());
		pd.show();
		client.post(DATA.baseUrl+"/saveAppointmentStatus", params, new AsyncHttpResponseHandler() {
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
							customToast.showToast("Appointment status has been updated",0,0);
							DATA.drAppointmentModel.setStatus(status);

							if(status.equalsIgnoreCase("Confirmed")){
								recreate();
							}else {
								finish();
							}
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
					DATA.print("-- responce onsuccess: saveAppointmentStatus, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--onFailure in saveAppointmentStatus "+content);
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
						Toast.makeText(activity, "No network connection", Toast.LENGTH_SHORT).show();
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
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("patient_id", patientId);
		params.put("message_text", EasyAES.encryptString(msgText));
		params.put("from", "doctor");
		params.put("to", "patient");

		DATA.print("-- params in sendmessage: "+params.toString());

		client.post(DATA.baseUrl+"/sendmessage",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					DATA.print("--reaponce in sendmessage: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							dialogSendMessage.dismiss();
							customToast.showToast("Message Sent",0,0);
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
					DATA.print("-- responce onsuccess: sendmessage, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail sendmessage:" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg







	private void getPatientDetail(){
		ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+DATA.selectedUserCallId,"get",null,apiCallBack, activity);
		ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.contains(ApiManager.PATIENT_DETAIL)){
			try {
				JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

				JSONObject patient_data = jsonObject.getJSONObject("patient_data");

				DATA.selectedUserCallAge = patient_data.getString("age");

				ActivityTcmDetails.ptPolicyNo = patient_data.getString("policy_number");
				ActivityTcmDetails.ptDOB  = patient_data.getString("birthdate");
				ActivityTcmDetails.ptAddress = patient_data.getString("residency");
				//String address2 = patient_data.getString("address2");
				ActivityTcmDetails.pt_city = patient_data.optString("city");
				ActivityTcmDetails.pt_state = patient_data.optString("state");
				ActivityTcmDetails.ptZipcode  = patient_data.getString("zipcode");
				ActivityTcmDetails.ptPhone = patient_data.getString("phone");
				ActivityTcmDetails.ptEmail = patient_data.getString("email");
				ActivityTcmDetails.ptFname = patient_data.getString("first_name");
				ActivityTcmDetails.ptLname = patient_data.getString("last_name");

				ActivityTcmDetails.primary_patient_id = patient_data.getString("primary_patient_id");
				ActivityTcmDetails.family_is_online = patient_data.getString("family_is_online");

				ActivityTcmDetails.ptInsurancePayerName = patient_data.optString("payer_name", "-");

				/*DATA.selectedRefferedLiveCare.StoreName = patient_data.getString("StoreName");
				DATA.selectedRefferedLiveCare.PhonePrimary = patient_data.getString("PhonePrimary");

				DATA.selectedRefferedLiveCare.is_online = patient_data.getString("is_online");

				try{
					DATA.selectedUserLatitude = Double.parseDouble(patient_data.getString("latitude"));//zlat = patient zipcode latlong
					DATA.selectedUserLongitude = Double.parseDouble(patient_data.getString("longitude"));//zlong

					if (new GloabalMethods(activity).checkPlayServices()) {
						initilizeMap(DATA.selectedUserLatitude, DATA.selectedUserLongitude, DATA.selectedUserCallName);
					}
				}catch (Exception e){
					e.printStackTrace();
				}




				StringBuilder sbAddress = new StringBuilder();
				sbAddress.append("Address: ");
				if(!TextUtils.isEmpty(ptAddress)){
					sbAddress.append(ptAddress);
					sbAddress.append(", ");
				}
				if(!TextUtils.isEmpty(pt_city)){
					sbAddress.append(pt_city);
					sbAddress.append(", ");
				}
				if(!TextUtils.isEmpty(pt_state)){
					sbAddress.append(pt_state);
					sbAddress.append(", ");
				}
				if(!TextUtils.isEmpty(ptZipcode)){
					sbAddress.append(ptZipcode);
				}
				tvSelPtAddress.setText(sbAddress.toString());

				tvSelPtDOB.setText("DOB: "+ptDOB+"  |  Age: "+DATA.selectedUserCallAge);
				tvSelPtEmail.setText("Email: "+ptEmail);


				String styledText = "Mobile: " + "<font color='#2979ff'><u>"+ptPhone+"</u></font>";
				tvSelPtPhone.setText(Html.fromHtml(styledText));
				tvSelPtPhone.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:"+Uri.encode(ptPhone)));
							callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(callIntent);
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				});

				if(!jsonObject.optString("primary_care").isEmpty()){
					JSONObject primary_care = jsonObject.getJSONObject("primary_care");
					tvSelPtProviderName.setText("Provider Name: "+primary_care.getString("first_name")+ " " +primary_care.getString("last_name"));
					String provederPhone = primary_care.getString("mobile");
					//tvSelPtProviderPhone.setText(provederPhone);
					String styledText2 = "Provider Mobile: " + "<font color='#2979ff'><u>"+provederPhone+"</u></font>";
					tvSelPtProviderPhone.setText(Html.fromHtml(styledText2));

					//tvSelPtProviderPhone.setPaintFlags(tvSelPtProviderPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

					tvSelPtProviderPhone.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								Intent callIntent = new Intent(Intent.ACTION_CALL);
								callIntent.setData(Uri.parse("tel:"+Uri.encode(provederPhone)));
								callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(callIntent);
							}catch (Exception e){
								e.printStackTrace();
							}
						}
					});
				}else {
					tvSelPtProviderName.setVisibility(View.GONE);
					tvSelPtProviderPhone.setVisibility(View.GONE);
					divSepProvider.setVisibility(View.GONE);
				}


				if(!jsonObject.getString("virtual_visit").isEmpty()){
					JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");
					if(virtual_visit.has("dateof")){
						tvVitalsDate.setText("Date: "+virtual_visit.getString("dateof"));
					}

					if(virtual_visit.has("symptom_id")){
						ActivityBookAptmtCP.symptom_id = virtual_visit.getString("symptom_id");
					}
					if(virtual_visit.has("condition_id")){
						ActivityBookAptmtCP.condition_id = virtual_visit.getString("condition_id");
					}

					if(virtual_visit.has("symptom_name")){
						DATA.selectedUserCallSympTom = virtual_visit.getString("symptom_name");
						tvPtSymptom.setText(DATA.selectedUserCallSympTom);
						DATA.selectedRefferedLiveCare.symptom_name = DATA.selectedUserCallSympTom;
					}
					if(virtual_visit.has("condition_name")){
						DATA.selectedUserCallCondition = virtual_visit.getString("condition_name");
						tvPtCondition.setText(DATA.selectedUserCallCondition);
						DATA.selectedRefferedLiveCare.condition_name = DATA.selectedUserCallCondition;
					}
					if(virtual_visit.has("description")){
						DATA.selectedUserCallDescription = virtual_visit.getString("description");
						tvPtDescription.setText(DATA.selectedUserCallDescription);
						DATA.selectedRefferedLiveCare.description = DATA.selectedUserCallDescription;
					}

					if(virtual_visit.has("additional_data")){
						String additional_data = virtual_visit.getString("additional_data");
						if(!additional_data.isEmpty()){
							JSONObject additional_dataJSON = new JSONObject(additional_data);
							if(additional_dataJSON.has("pain_where")){
								DATA.selectedRefferedLiveCare.pain_where = additional_dataJSON.getString("pain_where");
								tvPain.setText(DATA.selectedRefferedLiveCare.pain_where);
							}
							if(additional_dataJSON.has("pain_severity")){
								DATA.selectedRefferedLiveCare.pain_severity = additional_dataJSON.getString("pain_severity");
								tvPainSeverity.setText(DATA.selectedRefferedLiveCare.pain_severity);
							}
						}
					}
					if(virtual_visit.has("symptom_details")){
						DATA.selectedRefferedLiveCare.symptom_details = virtual_visit.getString("symptom_details");
						tvSymptomDetails.setText(DATA.selectedRefferedLiveCare.symptom_details);
						ptPriHomeCareDiag = DATA.selectedRefferedLiveCare.symptom_details;
					}

					if(virtual_visit.has("pain_related")){
						DATA.selectedRefferedLiveCare.pain_related = virtual_visit.getString("pain_related");
						tvPainBodyPart.setText(DATA.selectedRefferedLiveCare.pain_related);
					}

					if(virtual_visit.has("ot_data")&& !virtual_visit.getString("ot_data").isEmpty()){
						JSONObject virtual_ot_data = new JSONObject(virtual_visit.getString("ot_data"));
						if(virtual_ot_data.has("ot_respirations")){
							String ot_respirations = virtual_ot_data.getString("ot_respirations");
							etOTRespirations.setText(ot_respirations);
						}
						if(virtual_ot_data.has("ot_blood_sugar")){
							String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
							etOTBloodSugar.setText(ot_blood_sugar);
						}
						if(virtual_ot_data.has("ot_hr")){
							String ot_hr = virtual_ot_data.getString("ot_hr");
							etOTHR.setText(ot_hr);
						}
						if(virtual_ot_data.has("ot_bp")){
							String ot_bp = virtual_ot_data.getString("ot_bp");
							etOTBP.setText(ot_bp);
						}
						if(virtual_ot_data.has("ot_saturation")){
							String ot_saturation = virtual_ot_data.getString("ot_saturation");
							etOTO2Saturations.setText(ot_saturation);
						}

						if(virtual_ot_data.has("ot_height")){
							String ot_height = virtual_ot_data.getString("ot_height");
							etOTHeight.setText(ot_height);
							DATA.selectedUserCallHeight = ot_height;
						}
						if(virtual_ot_data.has("ot_temperature")){
							String ot_temperature = virtual_ot_data.getString("ot_temperature");
							etOTTemperature.setText(ot_temperature);
						}
						if(virtual_ot_data.has("ot_weight")){
							String ot_weight = virtual_ot_data.getString("ot_weight");
							etOTWeight.setText(ot_weight);
							DATA.selectedUserCallWeight = ot_weight;
						}

						if(virtual_ot_data.has("ot_bmi")){
							String bmi = virtual_ot_data.getString("ot_bmi");
							etOTBMI.setText(bmi);
							DATA.selectedUserCallBMI = bmi;
						}
					}


					JSONArray reports = virtual_visit.getJSONArray("reports");
					final ArrayList<String> vvImgs = new ArrayList<>();
					for (int i = 0; i < reports.length(); i++) {
						vvImgs.add(reports.getJSONObject(i).getString("report_name"));
					}
					gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
					gvReportImages.setExpanded(true);
					gvReportImages.setPadding(5,5,5,5);
					*//*TextView gvHeader = new TextView(activity);
					gvHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					gvHeader.setText("Virtual Visit Medical Reports");
					gvHeader.setGravity(Gravity.CENTER);

					gvReportImages.addHeaderView(footerView);*//*

					gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							if(vvImgs.get(position).endsWith(".pdf")){
								DATA.selectedPtReportUrl = vvImgs.get(position);
								//openActivity.open(ViewReportFull.class, false);
								new ReportsDialog(appCompatActivity).showReportDialog();
							}else {
								DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
							}
						}
					});
				}*/

			} catch (JSONException e) {
				e.printStackTrace();
				//customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_ATTACH_SURVEY_APPTMNT)){

			new GloabalMethods(activity).parseAttachedSurveyResponce(content);
		}
	}



	public void getAttachSurveryApptmnt(){
		RequestParams params = new RequestParams();
		params.put("appointment_id", DATA.drAppointmentModel.getId());
		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_ATTACH_SURVEY_APPTMNT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}
}
