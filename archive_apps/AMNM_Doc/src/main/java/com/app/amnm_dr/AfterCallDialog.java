package com.app.amnm_dr;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.util.CheckInternetConnection;
import com.app.amnm_dr.util.DATA;
import com.app.amnm_dr.util.GloabalMethods;
import com.app.amnm_dr.util.HideShowKeypad;
import com.app.amnm_dr.util.OpenActivity;
import com.app.amnm_dr.util.PrescriptionModule;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class AfterCallDialog extends BaseActivity {
	
	AppCompatActivity activity;
	OpenActivity openActivity;
	HideShowKeypad hideShowKeypad;
	GloabalMethods gloabalMethods;
	LinearLayout patientOptions,doctorOptions;
	TextView tvDialogCompleteConversation,tvDialogAddSOAP,tvDialogBillWithNote,tvDialogMedNoteACD,tvDialogSendPrescription,tvDialogRecall,tvDialogShareToSpecialist,tvAddProgressNotes,
			 tvDialogDiscusToSpecialist, tvSymptoms, tvSubmitRating,tvNoThanks;
	RatingBar ratingBar;
	
	ProgressDialog pd;
	AsyncHttpClient client;
	String status = "";
	JSONObject jsonObject;
	CheckInternetConnection checkInternetConnection;
	
	SharedPreferences prefs;

	

	
	@Override
	protected void onResume() {

		 if(DATA.isFromDocToDoc){
			 patientOptions.setVisibility(View.GONE);
			 doctorOptions.setVisibility(View.VISIBLE);
			 startActivity(new Intent(activity,CustomDialogActivity.class));
			 finish();
			 /*tvDialogOKDONE.setVisibility(View.GONE);
			 tvDialogRecall.setVisibility(View.GONE);
			 tvSymptoms.setVisibility(View.GONE);*/
			 
		 }else if(MainActivity.isFromCallToCoordinator){
			 patientOptions.setVisibility(View.GONE);
			 doctorOptions.setVisibility(View.VISIBLE);
		 }else if(MainActivity.isFromInstantConnect){
			 patientOptions.setVisibility(View.GONE);
			 doctorOptions.setVisibility(View.VISIBLE);
		 }else if(DATA.isFromCallHistoryOrMsgs){
			 patientOptions.setVisibility(View.GONE);
			 doctorOptions.setVisibility(View.VISIBLE);
		 }else{
			 patientOptions.setVisibility(View.VISIBLE);
			 doctorOptions.setVisibility(View.GONE);
			 /*tvDialogOKDONE.setVisibility(View.VISIBLE);
			 tvDialogRecall.setVisibility(View.VISIBLE);
			 tvSymptoms.setVisibility(View.VISIBLE);*/
		 }
		 
		 if (DATA.isSOAP_NotesSent) {
			 System.out.println("-- code executed after finish");
			 DATA.isSOAP_NotesSent = false;

			 if(DATA.isVideoCallFromLiveCare){//Same code on complete the conversation click
				 DATA.isVideoCallFromLiveCare = false;
				 initRemoveDialog(true);
			 }

			 if(DATA.isVideoCallFromRefPt){
				 DATA.isVideoCallFromRefPt = false;
				 initRemoveFromMyPatientsDialog();
			 }
		}
		super.onResume();
	}
	
	@Override
	public void onBackPressed() {
		//super.onBackPressed();  // no action occur on back button
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.after_call_dialog);
		
		activity = AfterCallDialog.this;
		
		hideShowKeypad = new HideShowKeypad(activity);
		openActivity = new OpenActivity(activity);
		gloabalMethods = new GloabalMethods(activity);

		pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		pd.setCanceledOnTouchOutside(false);
		pd.setTitle("Please wait..");
		pd.setMessage("Removing the current appointment from queue");
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		
		patientOptions = findViewById(R.id.patientOptions);
		doctorOptions = findViewById(R.id.doctorOptions);
		
		tvDialogCompleteConversation = findViewById(R.id.tvDialogCompleteConversation);
		tvDialogAddSOAP = findViewById(R.id.tvDialogAddSOAP);
        tvDialogBillWithNote = findViewById(R.id.tvDialogBillWithNote);
		tvDialogMedNoteACD = findViewById(R.id.tvDialogMedNoteACD);
		tvDialogSendPrescription = findViewById(R.id.tvDialogSendPrescription);
		tvAddProgressNotes = findViewById(R.id.tvAddProgressNotes);

		tvDialogRecall = findViewById(R.id.tvDialogRecall);
		tvSymptoms = findViewById(R.id.textView1);
		
		tvSubmitRating = findViewById(R.id.tvSubmitRating);
		tvNoThanks = findViewById(R.id.tvNoThanks);
		ratingBar = findViewById(R.id.ratingBar1);
		
		Button btnReferToPCP = findViewById(R.id.btnReferToPCP);
		btnReferToPCP.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new GloabalMethods(activity).referToPCP(DATA.selectedUserAppntID);
			}
		});
		
		//initPrescripDialog();//shifted to separite module

        tvDialogBillWithNote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivitySOAP.addSoapFlag = 4;
                openActivity.open(ActivityTelemedicineServices.class, false);
            }
        });
		tvDialogMedNoteACD.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ActivitySOAP.addSoapFlag = 5;
				openActivity.open(ActivityTelemedicineServices.class, false);
			}
		});

		tvDialogAddSOAP.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//initRemoveDialog();
				//startActivity(new Intent(activity,ActivityTelemedicineServices.class));
				new GloabalMethods(activity).showAddSOAPDialog();
			}
		});
		tvDialogCompleteConversation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(DATA.isVideoCallFromLiveCare){//Same code in onResume if DATA.isSoapnotessent true
					DATA.isVideoCallFromLiveCare = false;
					initRemoveDialog(false);
				}else {
					Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
				}
			}
		});
		tvDialogSendPrescription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PrescriptionModule(activity).initDoubleVerficationDialog();
			}
		});
		
		tvDialogRecall.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				/*Intent i = new Intent(activity, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();*/
				Intent i = new Intent(activity, MainActivity.class);//SampleActivity.class
				i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				startActivity(i);
				finish();

			}
		});
		
		tvDialogShareToSpecialist = findViewById(R.id.tvDialogShareToSpecialist);//tvShare
		
		tvDialogShareToSpecialist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DATA.referToSpecialist = true;
				DATA.onlyReports = "0";
				//DATA.onlyReports = "1"; //this value was not removing case from my queue
				startActivity(new Intent(activity,DocToDoc.class));
				finish();
				
			}
		});
		
		
		tvDialogDiscusToSpecialist = findViewById(R.id.tvDialogDiscusToSpecialist);
		tvDialogDiscusToSpecialist.setVisibility(View.GONE);
		tvDialogDiscusToSpecialist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DATA.referToSpecialist = false;
				startActivity(new Intent(activity,DocToDoc.class));
				finish();
				
			}
		});
		
		tvSubmitRating.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Toast.makeText(activity, "Thank you!", Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		tvNoThanks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});


		tvAddProgressNotes.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityProgressNotes.class,false);
			}
		});
	}//end oncreate

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
		if (cbSendAVS.isChecked()) {
		     removeURL = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID+"/livecheckup?send_avs=1";
		  } else {
		   removeURL = DATA.baseUrl+"appointmentDone/"+DATA.selectedUserAppntID+"/livecheckup?send_avs=0";
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
		
		System.out.println("-- sendCallDoneReport: "+removeURL);

		String reqURL = removeURL;

		client.get(removeURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--online care response in sendCAllDoneReport: "+content);

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
								Intent i = new Intent(activity, ActivityFindNurse.class);
								i.putExtra("patientType",patientType);
								//i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								startActivity(i);
								finish();
							}

						} else {
							Toast.makeText(activity, "Something went wrong, please try again.", Toast.LENGTH_LONG).show();
						}


					} catch (JSONException e) {
						System.out.println("--online care exception in getlivecare patients: "+e);

						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("-- onfailure : "+reqURL+content);
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
		 
		 //System.out.println("--in saveAppointmentStatus  params: "+params.toString());
		 pd.show();


		 String reqURL = DATA.baseUrl+"/saveAppointmentStatus";

		System.out.println("-- Request : "+reqURL);
		System.out.println("-- params saveAppointmentStatus : "+params.toString());

		  client.post(reqURL, params, new AsyncHttpResponseHandler() {




			  @Override
			  public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				  // called when response HTTP status is "200 OK"
				  pd.dismiss();
				  try{
					  String content = new String(response);

					  System.out.println("--reaponce in saveAppointmentStatus "+content);
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
								  Intent i = new Intent(activity, ActivityFindNurse.class);
								  i.putExtra("patientType",patientType);
								  //i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
								  startActivity(i);
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
					  System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				  }
			  }

			  @Override
			  public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				  // called when response HTTP status is "4XX" (eg. 401, 403, 404)
				  pd.dismiss();
				  try {
					  String content = new String(errorResponse);
					  System.out.println("-- onfailure saveAppointmentStatus : "+reqURL+content);
					  new GloabalMethods(activity).checkLogin(content);
					  customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				  }catch (Exception e1){
					  e1.printStackTrace();
					  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				  }
			  }
		  });

		 }//end saveAppointmentStatus
	

	
	EditText editText;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	public void startVoiceRecognitionActivity(EditText editText) {
	    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
	    // identifying your application to the Google service
	    intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
	    // hint in the dialog
	    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name));
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
	

	
	/*Dialog dialogAddNotes;
	public void initAddNotesDialog(){
		
		dialogAddNotes = new Dialog(activity);
		dialogAddNotes.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		dialogAddNotes.setContentView(R.layout.dialog_addnotes);
		TextView tvNotePatientName = (TextView) dialogAddNotes.findViewById(R.id.tvNotePatientName);
		final EditText etNote = (EditText) dialogAddNotes.findViewById(R.id.etNote);
		TextView btnAddNote = (TextView) dialogAddNotes.findViewById(R.id.btnAddNote);
		btnAddNote.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String note = etNote.getText().toString();
				if (note.isEmpty()) {
					Toast.makeText(activity, "Please add a note !", Toast.LENGTH_SHORT).show();
				} else {
					//add note here
					saveDrNotes(DATA.selectedUserCallId, note);//userID here
				}
			}
		});
	
	
		dialogAddNotes.show();
	
	}*/
	
	public void saveDrNotes(String liveCareId,String note) {
		 
		pd.setMessage("Please wait...    ");
		pd.show();
			  AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

			  RequestParams params = new RequestParams();
			  
			  params.put("id", liveCareId);
			  params.put("notes", note);
			  //params.put("", liveCareId);
			  System.out.println("--in saveNote params liveCareId "+liveCareId);

			  String reqURL = DATA.baseUrl+"/saveNotes";

		System.out.println("-- Request : "+reqURL);
		System.out.println("-- params : "+params.toString());

			  client.post(reqURL, params, new AsyncHttpResponseHandler() {




				  @Override
				  public void onSuccess(int statusCode, Header[] headers, byte[] response) {
					  // called when response HTTP status is "200 OK"
					  pd.dismiss();
					  try{
						  String content = new String(response);

						  System.out.println("--reaponce in saveDrNotes"+content);
						  //--reaponce in saveDrNotes{"success":1,"message":"Saved."}
						  try {
							  JSONObject jsonObject = new JSONObject(content);
							  if (jsonObject.has("success")) {
								  Toast.makeText(activity, "Your note saved successfully", Toast.LENGTH_LONG).show();
								  //etNote.setText("");
								  //btnAddNotes.setVisibility(View.GONE);
								  //etNote.setVisibility(View.GONE);
							  } else {
								  Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
							  }
						  } catch (JSONException e) {
							  System.out.println("--saveNotes json exception");
							  Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
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
						  System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }

				  @Override
				  public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
					  // called when response HTTP status is "4XX" (eg. 401, 403, 404)
					  pd.dismiss();
					  try {
						  String content = new String(errorResponse);
						  System.out.println("-- onfailure : "+reqURL+content);
						  new GloabalMethods(activity).checkLogin(content);
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);

					  }catch (Exception e1){
						  e1.printStackTrace();
						  customToast.showToast(DATA.CMN_ERR_MSG,0,0);
					  }
				  }

			  });

			 }//end saveDrNotes
	/*MaterialDialog mMaterialDialog;
	public void showMessageBox(Context context, String tittle , String content) {
		mMaterialDialog = new MaterialDialog(context)
	    .setTitle(tittle)
	    .setMessage(content)
	    .setPositiveButton("YES REMOVE", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
	            if (DATA.isFromAppointment) {
					saveAppointmentStatus();
				}else {
					sendCallDoneReport(); // to remove patient from queue
				}
	            
	           
	        }
	    })
	
		
		
		.setNegativeButton("NOT NOW", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
	         
	        }
	    });
	
	mMaterialDialog.show();
	
	}*/
	
	

	
	/*public void showMessageBoxError(Context context, String tittle , String content) {
		mMaterialDialog = new MaterialDialog(context)
	    .setTitle(tittle)
	    .setMessage(content)
	    .setPositiveButton("OK", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
	             
	           
	        }
	    });
	
		
		
//		.setNegativeButton("NOT NOW", new View.OnClickListener() {
//	        @Override
//	        public void onClick(View v) {
//	            mMaterialDialog.dismiss();
//	         
//	        }
//	    });
	
	mMaterialDialog.show();
	
	}*/
	 
	 
	 /*ArrayList<DosageFormsBean> dosageFormsBeans;
	 public void getDosageForms() {

		 pd.setMessage("Please wait...    ");
		 pd.show();
		 AsyncHttpClient client = new AsyncHttpClient();

		 client.post(DATA.baseUrl+"/getDosageForms", new AsyncHttpResponseHandler() {
			 @Override

			 public void onSuccess(int statusCode, String content) {
				 pd.dismiss();
				 System.out.println("--reaponce in getDosageForms "+content);
				 
				 try {
					JSONObject jsonObject = new JSONObject(content);
					DosageFormsBean bean;
					 dosageFormsBeans = new ArrayList<DosageFormsBean>();
					JSONArray data = jsonObject.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						 String field_value = data.getJSONObject(i).getString("field_value");
						 String dosage_form = data.getJSONObject(i).getString("dosage_form");
						 
						 bean = new DosageFormsBean(field_value, dosage_form);
						 dosageFormsBeans.add(bean);
						 bean = null;
					}
					
					
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 }

			 @Override

			 public void onFailure(Throwable error, String content) {
				 pd.dismiss();
				 System.out.println("--onfail getDosageForms " +content);
				 Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
			 }
		 });

	 }//end getDosageForms
*/	



	public void initRemoveFromMyPatientsDialog(){
		final Dialog dialogRemovePatient = new Dialog(activity);
		dialogRemovePatient.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogRemovePatient.setContentView(R.layout.dialog_remove_from_mypatients);

		TextView btnRemove = dialogRemovePatient.findViewById(R.id.btnRemove);
		TextView btnNotRemove = dialogRemovePatient.findViewById(R.id.btnNotRemove);

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				switch (view.getId()) {
					case R.id.btnRemove:
						dialogRemovePatient.dismiss();

						removeFromMyPatients();

						break;
					case R.id.btnNotRemove:
						dialogRemovePatient.dismiss();
						finish();
						break;

					default:
						break;
				}

			}
		};

		btnRemove.setOnClickListener(clickListener);
		btnNotRemove.setOnClickListener(clickListener);
		//btnAddNotes.setOnClickListener(clickListener);

		dialogRemovePatient.show();
	}


	public void removeFromMyPatients(){
		RequestParams params = new RequestParams();
		//params.put("user_id", prefs.getString("id", ""));
		params.put("id", ActivityRefferedPatients.referredId);

		ApiManager apiManager = new ApiManager(ApiManager.REMOVE_REFERRED,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.REMOVE_REFERRED)){
			try {//{"status":"success","message":"Deleted."}
				JSONObject jsonObject = new JSONObject(content);
				if(jsonObject.getString("status").equalsIgnoreCase("success")){
					ActivityRefferedPatients.shouldRefresh = true;
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
