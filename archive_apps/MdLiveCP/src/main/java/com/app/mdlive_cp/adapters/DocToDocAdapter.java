package com.app.mdlive_cp.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_cp.ActivityBookAptmtCP;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.DoctorsModel;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.EasyAES;
import com.app.mdlive_cp.util.GloabalMethods;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class DocToDocAdapter extends ArrayAdapter<DoctorsModel> implements ApiCallBack{

	Activity activity;
	CustomToast customToast;

	TextView tvUsersRowStatus,tvUsersRowName,tvDocOrSp,tvZipcode,tvCall,tvSendMsg, tvReqCare;

	ImageView imgUsersRow,imgUserStatus,ivIsonline;

	ArrayList<DoctorsModel> doctorsModels;
	ArrayList<DoctorsModel> doctorsModelsOrg;

	SharedPreferences prefs;
	CheckInternetConnection connection;

	public static int reqCareVisibility = View.GONE;

	public DocToDocAdapter(Activity activity,ArrayList<DoctorsModel> doctorsModels) {
		super(activity, R.layout.doc_to_doc_row, doctorsModels);
		this.activity = activity;
		customToast = new CustomToast(activity);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME,Context.MODE_PRIVATE);
		connection = new CheckInternetConnection(activity);
		this.doctorsModels = doctorsModels;
		doctorsModelsOrg = new ArrayList<DoctorsModel>();
		doctorsModelsOrg.addAll(doctorsModels);
	}


	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return super.getCount();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = layoutInflater.inflate(R.layout.doc_to_doc_row, null);

		tvUsersRowStatus = (TextView) convertView.findViewById(R.id.tvUsersRowStatus);
		tvUsersRowName = (TextView) convertView.findViewById(R.id.tvUsersRowName);
		tvDocOrSp = (TextView) convertView.findViewById(R.id.tvDocOrSp);
		tvZipcode = (TextView) convertView.findViewById(R.id.tvZipcode);

		imgUserStatus = (ImageView) convertView.findViewById(R.id.imgUserStatus);
		imgUsersRow = (ImageView) convertView.findViewById(R.id.imgUsersRow);
		ivIsonline = (ImageView) convertView.findViewById(R.id.ivIsonline);

		tvCall = (TextView) convertView.findViewById(R.id.tvCall);
		tvSendMsg = (TextView) convertView.findViewById(R.id.tvSendMsg);
		tvReqCare = (TextView) convertView.findViewById(R.id.tvReqCare);


		DATA.loadImageFromURL(doctorsModels.get(position).image,R.drawable.icon_call_screen,imgUsersRow);
		tvUsersRowName.setText(doctorsModels.get(position).fName + " "+doctorsModels.get(position).lName);

		if (doctorsModels.get(position).current_app.contains("doctor")) {//equalsIgnoreCase replaced with contain b/c it maybe doctor_emcura, doctor_conuc etc
			tvUsersRowStatus.setText("Doctor");
		} else {
			tvUsersRowStatus.setText(doctorsModels.get(position).speciality_name);
		}
		if (doctorsModels.get(position).is_online.equals("1")) {
			ivIsonline.setImageResource(R.drawable.icon_online);
		}else{
			ivIsonline.setImageResource(R.drawable.icon_notification);
		}
		//tvUsersRowStatus.setText(doctorsModels.get(position).designation);
		tvDocOrSp.setText(doctorsModels.get(position).current_app);
		tvZipcode.setText("Zipcode: "+doctorsModels.get(position).zip_code);
		/*if (doctorsModels.get(position).speciality_id.equalsIgnoreCase("null")) {
			tvDocOrSp.setText("Doctor");
		} else {
			tvDocOrSp.setText("Specialist");
		}*/

		tvCall.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DATA.selectedDrId = doctorsModels.get(position).id;
				// DATA.selectedUserCallId = doctorsModels.get(position).id;
				DATA.selectedDrName = doctorsModels.get(position).fName + " " + doctorsModels.get(position).lName;
				DATA.selectedDrQbId = doctorsModels.get(position).qb_id;
				DATA.selectedDrImage = doctorsModels.get(position).image;
				DATA.selectedDrQualification = doctorsModels.get(position).qualification;
				DATA.isFromDocToDoc = true;
				DATA.selectedDoctorsModel = doctorsModels.get(position);

				if(DATA.selectedDoctorsModel.is_online.equals("1")){
					DATA.incomingCall = false;
					Intent myIntent = new Intent(activity, MainActivity.class);//SampleActivity.class
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					activity.startActivity(myIntent);
					//activity.finish();
				}else {
					new AlertDialog.Builder(activity).setTitle("Doctor Offline")
							.setMessage("Doctor is offline and can't be connected right now. Leave a message instead ?")
							.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									initMsgDialog();
								}
							}).setNegativeButton("Cancel",null).show();
				}


			}
		});


		tvSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				DATA.selectedDrId = doctorsModels.get(position).id;
				// DATA.selectedUserCallId = doctorsModels.get(position).id;
				DATA.selectedDrName = doctorsModels.get(position).fName + " " + doctorsModels.get(position).lName;
				DATA.selectedDrQbId = doctorsModels.get(position).qb_id;
				DATA.selectedDrImage = doctorsModels.get(position).image;
				DATA.selectedDrQualification = doctorsModels.get(position).qualification;
				DATA.isFromDocToDoc = true;
				DATA.selectedDoctorsModel = doctorsModels.get(position);


				initMsgDialog();
			}
		});

		tvReqCare.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DATA.doctorsModelForApptmnt = doctorsModels.get(position);
				activity.startActivity(new Intent(activity,ActivityBookAptmtCP.class));

				/*AlertDialog alertDialog = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm")
								.setMessage("Are you sure? Do you want to refer patient "+DATA.selectedUserCallName+
								" to the doctor "+doctorsModels.get(position).fName + " " + doctorsModels.get(position).lName
								+" for care")
								.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										referPatient(doctorsModels.get(position).id);
									}
								})
								.setNegativeButton("No",null).create();

				alertDialog.show();*/
			}
		});

		tvReqCare.setVisibility(reqCareVisibility);

		return convertView;
	}




	Dialog dialogSendMessage;
	public void initMsgDialog() {
		dialogSendMessage = new Dialog(activity);
		dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSendMessage.setContentView(R.layout.dialog_send_msg);
		final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
		TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);

		TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);
		if (DATA.isFromDocToDoc) {
			tvMsgPatientName.setText("To: "+DATA.selectedDrName);
		} else {
			tvMsgPatientName.setText("To: "+DATA.selectedUserCallName);
		}


		btnSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etMsg.getText().toString().isEmpty()) {
					Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
				} else {
					if (connection.isConnectedToInternet()) {
						if (DATA.isFromDocToDoc) {
							sendMsgSP(DATA.selectedDrId, EasyAES.encryptString(etMsg.getText().toString()));
						} else {
							//sendMsg(DATA.selectedUserCallId, etMsg.getText().toString());
						}
						//sendMsg(DATA.selectedUserCallId, etMsg.getText().toString());
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


	public void sendMsgSP(String doctorId,String msgText) {

		DATA.showLoaderDefault(activity,"");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		params.put("doctor_id", doctorId);
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("message_text", msgText);
		params.put("from", "specialist");
		params.put("to", DATA.selectedDoctorsModel.current_app);//"doctor"

		System.out.println("-- params in sendmessage: "+params.toString());
		client.post(DATA.baseUrl+"/sendmessage",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--reaponce in sendMsg"+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							dialogSendMessage.dismiss();
							customToast.showToast("Message Sent",0,0);
						}else if (jsonObject.has("error")) {
							dialogSendMessage.dismiss();
							new AlertDialog.Builder(activity).setTitle("Info").setMessage(jsonObject.getString("message")).
									setPositiveButton("OK",null).show();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.out.println("--json eception sendMsg" +content);
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: sendmessage, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail sendMsg" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg

	public void filter(String filterText) {

		doctorsModels.clear();

		filterText = filterText.toLowerCase(Locale.getDefault());

		System.out.println("---doctorsModelsOrg size: "+doctorsModelsOrg.size());

		if(filterText.length() == 0) {
			doctorsModels.addAll(doctorsModelsOrg);
		}

		else {

			for(DoctorsModel temp :doctorsModelsOrg) {

				if(temp.zip_code.toLowerCase(Locale.getDefault()).contains(filterText)) {

					doctorsModels.add(temp);
				}

			}
		}


		notifyDataSetChanged();

	}//end filter



	public void referPatient(String doctorID) {
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("patient_id", DATA.selectedUserCallId);
		params.put("refered_id", doctorID);

		ApiManager apiManager = new ApiManager(ApiManager.REFERED_PATIENT,"post",params,DocToDocAdapter.this, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.REFERED_PATIENT)){
			try {//{"status":"success","message":"Patient Sucessfully refered."}
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				String message = jsonObject.getString("message");
				if(status.equalsIgnoreCase("success")){
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity).setTitle(activity.getResources().getString(R.string.app_name))
									.setMessage(message)
									.setPositiveButton("Done",null).create();
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							activity.finish();
						}
					});
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();
				}else {
					customToast.showToast(message,0,1);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
