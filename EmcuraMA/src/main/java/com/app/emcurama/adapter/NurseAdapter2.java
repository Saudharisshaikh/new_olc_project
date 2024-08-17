package com.app.emcurama.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.app.emcurama.R;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.DoctorsModel;
import com.app.emcurama.model.NurseBean2;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.EasyAES;
import com.app.emcurama.util.GloabalMethods;
import com.app.emcurama.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class NurseAdapter2 extends ArrayAdapter<NurseBean2> {

	Activity activity;
	ArrayList<NurseBean2> nurseBeens;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;
	OpenActivity openActivity;

	public NurseAdapter2(Activity activity , ArrayList<NurseBean2> nurseBeens) {
		super(activity, R.layout.lv_nurse_row2, nurseBeens);

		this.activity = activity;
		this.nurseBeens = nurseBeens;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvNurseName,tvNurseType,tvAssign,tvSendMsg;
		CircularImageView ivNurse;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_nurse_row2, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNurseName = (TextView) convertView.findViewById(R.id.tvNurseName);
			viewHolder.tvNurseType = (TextView) convertView.findViewById(R.id.tvNurseType);
			viewHolder.tvAssign = (TextView) convertView.findViewById(R.id.tvAssign);
			viewHolder.ivNurse = (CircularImageView) convertView.findViewById(R.id.ivNurse);
			viewHolder.tvSendMsg = (TextView) convertView.findViewById(R.id.tvSendMsg);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNurseName, viewHolder.tvNurseName);
			convertView.setTag(R.id.tvNurseType, viewHolder.tvNurseType);
			convertView.setTag(R.id.tvAssign, viewHolder.tvAssign);
			convertView.setTag(R.id.ivNurse, viewHolder.ivNurse);
			convertView.setTag(R.id.tvSendMsg, viewHolder.tvSendMsg);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvNurseName.setText(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		viewHolder.tvNurseName.setTag(nurseBeens.get(position).first_name+" "+nurseBeens.get(position).last_name);
		
		
		viewHolder.tvNurseType.setText(nurseBeens.get(position).doctor_category);
		viewHolder.tvNurseType.setTag(nurseBeens.get(position).doctor_category);
		

		DATA.loadImageFromURL(nurseBeens.get(position).image , R.drawable.icon_call_screen, viewHolder.ivNurse);


		if(nurseBeens.get(position).is_online.equals("1")){
			viewHolder.tvAssign.setText("Call");
			viewHolder.tvAssign.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
			viewHolder.tvSendMsg.setBackgroundResource(R.drawable.apptmnt_confirm_drawable);
		}else {
			viewHolder.tvAssign.setBackgroundResource(R.drawable.btn_drawable_grey);
			viewHolder.tvSendMsg.setBackgroundResource(R.drawable.btn_drawable_grey);
			viewHolder.tvAssign.setText("Offline");
		}

		viewHolder.tvAssign.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInternetConnection.isConnectedToInternet()){
					if(nurseBeens.get(position).is_online.equals("1")){
						//DATA.selectedDrIdForNurse = DATA.allDoctors.get(position).id;
						DATA.selectedDrId = nurseBeens.get(position).id;
						//DATA.selectedUserCallId = nurseBeens.get(position).id;
						DATA.selectedDrName = nurseBeens.get(position).first_name + " " + nurseBeens.get(position).last_name;
						DATA.selectedDrQbId = "selectedDrQbId";
						DATA.selectedDrImage = nurseBeens.get(position).image;
						DATA.selectedDrQualification = "selectedDrQualification";
						DATA.selectedDoctorsModel = new DoctorsModel();
						DATA.selectedDoctorsModel.current_app = "specialist";

						DATA.isFromDocToDoc = true;
						DATA.incomingCall = false;

						openActivity.open(MainActivity.class,false);
					}else{
						AlertDialog.Builder builder = new AlertDialog.Builder(activity);
						builder.setTitle("Offline");
						builder.setMessage("Care Provider is offline and can't be connected right now");
						builder.setPositiveButton("Ok",null);
						builder.show();
					}
				}else{
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});

		viewHolder.tvSendMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				initMsgDialog(nurseBeens.get(position));
			}
		});

		return convertView;
	}


	Dialog dialogSendMessage;
	public void initMsgDialog(final NurseBean2 nurseBean) {
		dialogSendMessage = new Dialog(activity);
		dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSendMessage.setContentView(R.layout.dialog_send_msg);
		final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
		TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);

		TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);

			tvMsgPatientName.setText("To: "+nurseBean.first_name+" "+nurseBean.last_name);


		btnSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (etMsg.getText().toString().isEmpty()) {
					Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
				} else {
					if (checkInternetConnection.isConnectedToInternet()) {
						sendMsgSP(nurseBean.id, EasyAES.encryptString(etMsg.getText().toString()));
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
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("patient_id", doctorId);
		params.put("message_text", msgText);
		params.put("from", "doctor");
		params.put("to", "specialist");

		String reqURL = DATA.baseUrl+"/sendmessage";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params in sendmessage: "+params.toString());

		client.post(reqURL,params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in sendmessage: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							dialogSendMessage.dismiss();
							Toast.makeText(activity, "Message Sent", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						DATA.print("--json eception sendMsg" +content);
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
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure sendmessage : "+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg
}
