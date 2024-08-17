package com.app.mhcsn_spe;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_spe.adapter.MessageAdapter;
import com.app.mhcsn_spe.adapter.MessageAdapterForDoctor;
import com.app.mhcsn_spe.adapter.MessageAdapterForSpecialist;
import com.app.mhcsn_spe.api.ApiManager;
import com.app.mhcsn_spe.model.DoctorsModel;
import com.app.mhcsn_spe.model.MessageBean;
import com.app.mhcsn_spe.util.CheckInternetConnection;
import com.app.mhcsn_spe.util.ChoosePictureDialog;
import com.app.mhcsn_spe.util.DATA;
import com.app.mhcsn_spe.util.DialogPatientInfo;
import com.app.mhcsn_spe.util.EasyAES;
import com.app.mhcsn_spe.util.GloabalMethods;
import com.github.chrisbanes.photoview.PhotoView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ViewConversationActivity extends BaseActivity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	ProgressDialog pd;
	TextView tvNoMsgs;
	ListView lvMessages;
	EditText etSendMessage;
	ImageView ivSendMessage,ivSendImage;
	//Button btnReply;
	ArrayList<MessageBean> messageBeans;

	//BroadcastReceiver refreshBrodcast;

	public static boolean isFamilyMember = false;

	BroadcastReceiver refreshChatMsgDelBC;
	public static final String REFRESH_CHAT_ACTION_MSG_DEL = BuildConfig.APPLICATION_ID+".refresh_messages_chat_action_one_message_deleted";

	public static final String REFRESH_CHAT_ACTION_NEW_MSG = BuildConfig.APPLICATION_ID+".refreshmsges";


	String patientIDConv = "", blockID = "";
	boolean isBlocked;

	@Override
	protected void onStart() {
		DATA.shouldNotify = false;
		//registerReceiver(refreshBrodcast, new IntentFilter(REFRESH_CHAT_ACTION_NEW_MSG));
		registerReceiver(refreshChatMsgDelBC, new IntentFilter(REFRESH_CHAT_ACTION_MSG_DEL));
		super.onStart();
	}
	@Override
	protected void onStop() {
		//unregisterReceiver(refreshBrodcast);
		DATA.shouldNotify = true;
		unregisterReceiver(refreshChatMsgDelBC);
		super.onStop();
	}
	@Override
	protected void onResume() {
		isFamilyMember = false;

		if(DATA.isImageCaptured){

			DATA.isImageCaptured = false;

			showPicDialog();

			super.onResume();

			return;
		}

		if (checkInternetConnection.isConnectedToInternet()) {
			/*if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
				viewConversationspecialist(DATA.selecetedBeanFromConversation.getDoctor_id());
			} else {
				viewConversation(DATA.selecetedBeanFromConversation.getPatient_id());
			}*/


			if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("patient")) {
				String patientId = "0";
				if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("patient")) {
					patientId = DATA.selecetedBeanFromConversation.getPatient_id();
				} else {
					patientId = DATA.selecetedBeanFromConversation.getTo_id();
				}
				viewConversation(patientId);

				//just open this for block function activate
				/*patientIDConv = patientId;
				checkBlockPatient(patientIDConv);
				invalidateOptionsMenu();*/

			}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
				String docID = "0";
				if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
					docID = DATA.selecetedBeanFromConversation.getDoctor_id();
				}else {
					docID = DATA.selecetedBeanFromConversation.getTo_id();
				}
				viewConversationDoctor(docID);
			}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("specialist")) {
				String speID = "0";
				if (DATA.selecetedBeanFromConversation.getSpecialist_id().equalsIgnoreCase(prefs.getString("id", ""))) {
					speID=DATA.selecetedBeanFromConversation.getTo_id();
				} else {
					speID = DATA.selecetedBeanFromConversation.getSpecialist_id();
				}
				viewConversationSpe_to_Spe(speID);
			}

			/*	String did = "0";
			if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("patient")) {
				did = DATA.selecetedBeanFromConversation.getPatient_id();
				viewConversationDoctor(did);
			} else if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
				did = DATA.selecetedBeanFromConversation.getDoctor_id();

				viewConversationDoctor(did);
			}else if(DATA.selecetedBeanFromConversation.getTo().equalsIgnoreCase("specialist") && DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("specialist")) {
				if (DATA.selecetedBeanFromConversation.getSpecialist_id().equalsIgnoreCase(prefs.getString("id", ""))) {
					did=DATA.selecetedBeanFromConversation.getTo_id();
				} else {
					did = DATA.selecetedBeanFromConversation.getSpecialist_id();
				}
			viewConversationSpe_to_Spe(did);

			}else {
				did = DATA.selecetedBeanFromConversation.getTo_id();
				viewConversationDoctor(did);
			}*/


		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_conversation);

		activity = ViewConversationActivity.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Sending message. Please wait...    ");
		pd.setCanceledOnTouchOutside(false);

		tvNoMsgs =(TextView) findViewById(R.id.tvNoMsgs);
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		etSendMessage = (EditText) findViewById(R.id.etSendMessage);
		ivSendMessage = (ImageView) findViewById(R.id.ivSendMessage);
		ivSendImage = (ImageView) findViewById(R.id.ivSendImage);
		//btnReply = (Button) findViewById(R.id.btnReply);

		getSupportActionBar().setTitle(DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());

		ivSendImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ChoosePictureDialog.class,false);
			}
		});

		ivSendMessage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					if (etSendMessage.getText().toString().isEmpty()) {
						customToast.showToast("Please type your message",0,0);
					} else {
						/*if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
							sendMsgSP(DATA.selecetedBeanFromConversation.getDoctor_id(), etSendMessage.getText().toString());
						}else {
							sendMsg(DATA.selecetedBeanFromConversation.getPatient_id(), etSendMessage.getText().toString());
						}*/

						if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("patient")) {
							String patientId = "0";
							if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("patient")) {
								patientId = DATA.selecetedBeanFromConversation.getPatient_id();
							} else {
								patientId = DATA.selecetedBeanFromConversation.getTo_id();
							}

							sendMsg(patientId, EasyAES.encryptString(etSendMessage.getText().toString()),false);

						}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
							String docID = "0";
							if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
								docID = DATA.selecetedBeanFromConversation.getDoctor_id();
							}else {
								docID = DATA.selecetedBeanFromConversation.getTo_id();
							}

							sendMsgSP(docID, EasyAES.encryptString(etSendMessage.getText().toString()),false);

						}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("specialist")) {
							String speID = "0";
							if (DATA.selecetedBeanFromConversation.getSpecialist_id().equalsIgnoreCase(prefs.getString("id", ""))) {
								speID=DATA.selecetedBeanFromConversation.getTo_id();
							} else {
								speID = DATA.selecetedBeanFromConversation.getSpecialist_id();
							}
							sendMsgSP(speID, EasyAES.encryptString(etSendMessage.getText().toString()),false);
						}

					}
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});

		/*refreshBrodcast = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent arg1) {
				if (checkInternetConnection.isConnectedToInternet()) {
					viewConversation(DATA.selecetedBeanFromConversation.getPatient_id());
				} else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		};*/


		lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (messageBeans.get(position).msg_type.equalsIgnoreCase("file")) {
					DialogPatientInfo.showImgMsgDialog(activity, messageBeans.get(position).files,
							DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());
				}
			}
		});
		lvMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				if (view.findViewById(R.id.contMe).getVisibility() == View.VISIBLE) {
					//final String refId = DATA.allAppointments.get(position).liveCheckupId;
					AlertDialog.Builder builder = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
					builder.setTitle("Delete Message");
					builder.setMessage("Are you sure you want to remove this message?");//You can't undo this action later.
					builder.setPositiveButton("Yes Delete", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							RequestParams params = new RequestParams();
							params.put("message_id",messageBeans.get(position).getId());
							params.put("my_id", prefs.getString("id", ""));
							params.put("current_app", Login.CURRENT_APP);

							ApiManager apiManager = new ApiManager(ApiManager.DELETE_MESSAGE,"post",params,apiCallBack, activity);
							apiManager.loadURL();
						}
					});
					builder.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
					AlertDialog dialog = builder.create();
					dialog.show();
					return true;
				}else {
					return false;
				}
			}
		});

		refreshChatMsgDelBC = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				onResume();
			}
		};
	}//oncreate


	public void sendMsg(final String patientId,String msgText, boolean isImage) {
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", prefs.getString("id", "0"));
		params.put("patient_id", patientId);
		params.put("message_text", msgText);
		params.put("from", "specialist");
		params.put("to", "patient");

		if(isImage){
			try {
				params.put("file", new File(DATA.imagePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		System.out.println("-- params in sendmessage: "+params.toString());

		client.post(DATA.baseUrl+"/sendmessage",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--reaponce in sendMsg"+content);
					// tvDrsNotes.setText(content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Your message has been sent",0,0);
							//finish();
							etSendMessage.setText("");
							viewConversation(patientId);//DATA.selecetedBeanFromConversation.getPatient_id()
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
					System.out.println("-- responce onsuccess: sendmessage, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail sendmessage: " +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg

	public void sendMsgSP(final String doctorId,String msgText, boolean isImage) {

		pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", doctorId);
		params.put("patient_id", prefs.getString("id", "0"));
		params.put("message_text", msgText);
		params.put("from", "specialist");
		params.put("to", DATA.selecetedBeanFromConversation.getUser_type());//"doctor"

		if(isImage){
			try {
				params.put("file", new File(DATA.imagePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		System.out.println("-- params in sendmessage: "+params.toString());

		client.post(DATA.baseUrl+"/sendmessage",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--reaponce in sendmessage: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {

							customToast.showToast("Your message has been sent",0,0);

							etSendMessage.setText("");
							//viewConversationDoctor(DATA.selecetedBeanFromConversation.getDoctor_id());
							if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
								viewConversationDoctor(doctorId);
							}else {
								viewConversationSpe_to_Spe(doctorId);
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
					System.out.println("-- responce onsuccess: sendmessage, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail sendmessage: " +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg


	public void viewConversation(String petientId) {

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		System.out.println("--url "+DATA.baseUrl+"/viewConversationSpecialist/patient/"+prefs.getString("id", "")+"/"+petientId);

		client.post(DATA.baseUrl+"/viewConversationSpecialist/patient/"+prefs.getString("id", "")+"/"+petientId, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--responce in viewConversation: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						JSONArray data = jsonObject.getJSONArray("data");
						if (data.length() == 0) {
							tvNoMsgs.setVisibility(View.VISIBLE);
						} else {

							tvNoMsgs.setVisibility(View.GONE);

							messageBeans = new ArrayList<MessageBean>();
							MessageBean temp;
							for (int i = 0; i < data.length(); i++) {

								String id = data.getJSONObject(i).getString("id");
								String doctor_id = data.getJSONObject(i).getString("doctor_id");
								String patient_id = data.getJSONObject(i).getString("patient_id");
								String message_text = data.getJSONObject(i).getString("message_text");
								String from = data.getJSONObject(i).getString("from");
								String dateof = data.getJSONObject(i).getString("dateof");
								String first_name = data.getJSONObject(i).getString("first_name");
								String last_name = data.getJSONObject(i).getString("last_name");
								String image = data.getJSONObject(i).getString("image");
								String to = data.getJSONObject(i).getString("to");

								message_text = EasyAES.decryptString(message_text);

								temp = new MessageBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image,to);
								if(data.getJSONObject(i).has("msg_type")){
									temp.msg_type = data.getJSONObject(i).getString("msg_type");
								}
								if(data.getJSONObject(i).has("files")){
									temp.files = data.getJSONObject(i).getString("files");
								}
								messageBeans.add(temp);
								temp = null;
							}

							MessageAdapter adapter = new MessageAdapter(activity, messageBeans);
							lvMessages.setAdapter(adapter);
						}

						if(jsonObject.has("extra_detail")){
							JSONObject extra_detail = jsonObject.getJSONObject("extra_detail");
							if(extra_detail.has("patient_data")){
								JSONObject patient_data = extra_detail.getJSONObject("patient_data");
								if(patient_data.has("patient_current_app")){
									String patient_current_app = patient_data.getString("patient_current_app");
									if(patient_current_app.equalsIgnoreCase("family")){
										isFamilyMember = true;
									}
								}
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: viewConversation, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//	pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure viewConversation: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//getpatientMessages


	public void viewConversationDoctor(final String doctorId) {

		final String reqURL = DATA.baseUrl+"/viewConversationspecialist/specialist/"+doctorId+"/"+prefs.getString("id", "");
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		client.post(reqURL, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--url "+DATA.baseUrl+"/viewConversationspecialist/specialist/"+doctorId+"/"+prefs.getString("id", ""));
					System.out.println("--reqURL: "+reqURL);
					System.out.println("--responce in viewConversationspecialist: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						JSONArray data = jsonObject.getJSONArray("data");
						if (data.length() == 0) {
							tvNoMsgs.setVisibility(View.VISIBLE);
						} else {

							tvNoMsgs.setVisibility(View.GONE);

							messageBeans = new ArrayList<MessageBean>();
							MessageBean temp;
							for (int i = 0; i < data.length(); i++) {

								String id = data.getJSONObject(i).getString("id");
								String doctor_id = data.getJSONObject(i).getString("doctor_id");
								String patient_id = data.getJSONObject(i).getString("specialist_id");//this is specialist_id for specialist
								String message_text = data.getJSONObject(i).getString("message_text");
								String from = data.getJSONObject(i).getString("from");
								String dateof = data.getJSONObject(i).getString("dateof");
								String first_name = data.getJSONObject(i).getString("first_name");
								String last_name = data.getJSONObject(i).getString("last_name");
								String image = data.getJSONObject(i).getString("image");
								String to = data.getJSONObject(i).getString("to");

								message_text = EasyAES.decryptString(message_text);

								temp = new MessageBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image,to);
								if(data.getJSONObject(i).has("msg_type")){
									temp.msg_type = data.getJSONObject(i).getString("msg_type");
								}
								if(data.getJSONObject(i).has("files")){
									temp.files = data.getJSONObject(i).getString("files");
								}
								messageBeans.add(temp);
								temp = null;
							}

							MessageAdapterForDoctor adapter = new MessageAdapterForDoctor(activity, messageBeans);

							lvMessages.setAdapter(adapter);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: viewConversationspecialist, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//	pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure viewConversationspecialist: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//viewConversationspecialist


	public void viewConversationSpe_to_Spe(final String specialistId) {

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);

		client.post(DATA.baseUrl+"/viewConversationspecialist/spe_to_spe/"+specialistId+"/"+prefs.getString("id", ""), new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--url "+DATA.baseUrl+"/viewConversationspecialist/specialist/"+specialistId+"/"+prefs.getString("id", ""));
					System.out.println("--responce in viewConversationSpe_to_Spe: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						JSONArray data = jsonObject.getJSONArray("data");
						if (data.length() == 0) {
							tvNoMsgs.setVisibility(View.VISIBLE);
						} else {

							tvNoMsgs.setVisibility(View.GONE);

							messageBeans = new ArrayList<MessageBean>();
							MessageBean temp;
							for (int i = 0; i < data.length(); i++) {

								String id = data.getJSONObject(i).getString("id");
								String doctor_id = data.getJSONObject(i).getString("doctor_id");
								String patient_id = data.getJSONObject(i).getString("specialist_id");//this is specialist_id for specialist
								String message_text = data.getJSONObject(i).getString("message_text");
								String from = data.getJSONObject(i).getString("from");
								String dateof = data.getJSONObject(i).getString("dateof");
								String first_name = data.getJSONObject(i).getString("first_name");
								String last_name = data.getJSONObject(i).getString("last_name");
								String image = data.getJSONObject(i).getString("image");
								String to = data.getJSONObject(i).getString("to");

								message_text = EasyAES.decryptString(message_text);

								temp = new MessageBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image,to);
								if(data.getJSONObject(i).has("msg_type")){
									temp.msg_type = data.getJSONObject(i).getString("msg_type");
								}
								if(data.getJSONObject(i).has("files")){
									temp.files = data.getJSONObject(i).getString("files");
								}
								messageBeans.add(temp);
								temp = null;
							}

							MessageAdapterForSpecialist adapter = new MessageAdapterForSpecialist(activity, messageBeans);

							lvMessages.setAdapter(adapter);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: viewConversationSpe_to_Spe, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//	pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure viewConversationSpe_to_Spe: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//viewConversationSpe_to_Spe


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_view_conv, menu);


		if(TextUtils.isEmpty(patientIDConv)){
			menu.getItem(0).setVisible(false);
		}else {
			menu.getItem(0).setVisible(true);
			if(isBlocked){
				menu.getItem(0).setTitle("Unblock");
			}else {
				menu.getItem(0).setTitle("Block");
			}
			etSendMessage.setEnabled(!isBlocked);
			ivSendImage.setEnabled(!isBlocked);
			ivSendMessage.setEnabled(!isBlocked);
			if(isBlocked){
				etSendMessage.setHint("You have blocked this user. Please unblock first to send text messages.");
			}else {
				etSendMessage.setHint("Type your message");
			}
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.action_block) {

			if(isBlocked){
				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Confirm")
						.setMessage("Are you sure? Do you want to unblock this patient?")
						.setPositiveButton("Yes Unblock", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								unBlockPatient(blockID);
							}
						})
						.setNegativeButton("Not Now", null)
						.create()
						.show();
			}else {
				new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Confirm")
						.setMessage("Are you sure? Do you want to block this patient?")
						.setPositiveButton("Yes Block", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								blockPatient(patientIDConv);
							}
						})
						.setNegativeButton("Not Now", null)
						.create()
						.show();
			}

		}else if (item.getItemId() == R.id.action_vcall) {
			if (checkInternetConnection.isConnectedToInternet()) {


				if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("patient")) {
					String patientId = "0";
					if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("patient")) {
						patientId = DATA.selecetedBeanFromConversation.getPatient_id();
					} else {
						patientId = DATA.selecetedBeanFromConversation.getTo_id();
					}
					//viewConversation(patientId);

					DATA.incomingCall = false;
					DATA.isFromDocToDoc = false;
					//DATA.selectedUserCallId = patientId;
					DATA.selectedUserCallName = DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name();
					DATA.selectedUserCallImage = DATA.selecetedBeanFromConversation.getImage();

					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

					if(isFamilyMember){
						myIntent.putExtra("isFromCallHistoryOrMsgs", true);
						myIntent.putExtra("isFromCallToFamily",true);
						ActivityTcmDetails.primary_patient_id = patientId;
						DATA.selectedUserCallId = "";
					}else {
						DATA.selectedUserCallId = patientId;
					}

					startActivity(myIntent);
					finish();

				}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
					String docID = "0";
					if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
						docID = DATA.selecetedBeanFromConversation.getDoctor_id();
					}else {
						docID = DATA.selecetedBeanFromConversation.getTo_id();
					}
					//viewConversationDoctor(docID);



					DATA.incomingCall = false;
					DATA.isFromDocToDoc = true;
					DATA.selectedDrName = DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name();
					DATA.selectedDrId = docID;
					DATA.selectedDrImage = DATA.selecetedBeanFromConversation.getImage();

					DATA.selectedDoctorsModel = new DoctorsModel();
					DATA.selectedDoctorsModel.current_app = "doctor";

					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					myIntent.putExtra("isFromCallHistoryOrMsgs", true);
					startActivity(myIntent);
					finish();


				}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("specialist")) {
					String speID = "0";
					if (DATA.selecetedBeanFromConversation.getSpecialist_id().equalsIgnoreCase(prefs.getString("id", ""))) {
						speID=DATA.selecetedBeanFromConversation.getTo_id();
					} else {
						speID = DATA.selecetedBeanFromConversation.getSpecialist_id();
					}
					//viewConversationSpe_to_Spe(speID);


					DATA.incomingCall = false;
					DATA.isFromDocToDoc = true;
					DATA.selectedDrName = DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name();
					DATA.selectedDrId = speID;
					DATA.selectedDrImage = DATA.selecetedBeanFromConversation.getImage();

					DATA.selectedDoctorsModel = new DoctorsModel();
					DATA.selectedDoctorsModel.current_app = "specialist";

					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					myIntent.putExtra("isFromCallHistoryOrMsgs", true);
					startActivity(myIntent);
					finish();

				}


			} else {
				customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
			}
		}
		return super.onOptionsItemSelected(item);
	}


	public void showPicDialog(){
		final Dialog picDialog = new Dialog(activity);
		picDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picDialog.setContentView(R.layout.dialog_img_msg);

		PhotoView ivMsgImg = (PhotoView) picDialog.findViewById(R.id.ivMsgImg);
		EditText etSendMessage = (EditText) picDialog.findViewById(R.id.etSendMessage);
		ImageButton ivSendMessage = (ImageButton) picDialog.findViewById(R.id.ivSendMessage);
		TextView dialogTittle = (TextView) picDialog.findViewById(R.id.dialogTittle);
		dialogTittle.setText("Send to: "+DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());
		etSendMessage.setText(this.etSendMessage.getText().toString());

		final String uri = Uri.fromFile(new File(DATA.imagePath)).toString();
		final String decoded = Uri.decode(uri);
		DATA.loadImageFromURL(decoded ,R.drawable.ic_placeholder_2,ivMsgImg);

		picDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				picDialog.dismiss();
			}
		});

		ivSendMessage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!checkInternetConnection.isConnectedToInternet()){
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					return;
				}

				picDialog.dismiss();


						/*if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
							sendMsgSP(DATA.selecetedBeanFromConversation.getDoctor_id(), etSendMessage.getText().toString());
						}else {
							sendMsg(DATA.selecetedBeanFromConversation.getPatient_id(), etSendMessage.getText().toString());
						}*/

				if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("patient")) {
					String patientId = "0";
					if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("patient")) {
						patientId = DATA.selecetedBeanFromConversation.getPatient_id();
					} else {
						patientId = DATA.selecetedBeanFromConversation.getTo_id();
					}

					sendMsg(patientId, EasyAES.encryptString(etSendMessage.getText().toString()), true);

				}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
					String docID = "0";
					if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
						docID = DATA.selecetedBeanFromConversation.getDoctor_id();
					}else {
						docID = DATA.selecetedBeanFromConversation.getTo_id();
					}

					sendMsgSP(docID, EasyAES.encryptString(etSendMessage.getText().toString()), true);

				}else if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("specialist")) {
					String speID = "0";
					if (DATA.selecetedBeanFromConversation.getSpecialist_id().equalsIgnoreCase(prefs.getString("id", ""))) {
						speID=DATA.selecetedBeanFromConversation.getTo_id();
					} else {
						speID = DATA.selecetedBeanFromConversation.getSpecialist_id();
					}
					sendMsgSP(speID, EasyAES.encryptString(etSendMessage.getText().toString()), true);
				}
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(picDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		picDialog.show();
		picDialog.getWindow().setAttributes(lp);
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.DELETE_MESSAGE)){
			onResume();
		}else if(apiName.equalsIgnoreCase(ApiManager.MESSAGES_CHECK_BLOCKPATIENT)){
			//{"status":"success","message":"Patient has been blocked","id":"8"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					isBlocked = true;
					blockID = jsonObject.getString("id");
				}else {
					isBlocked = false;
				}
				invalidateOptionsMenu();
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.MESSAGES_BLOCK_PATIENT)){
			//{"status":"success","message":"Patient has been blocked","id":12}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					isBlocked = true;
					blockID = jsonObject.getString("id");
					customToast.showToast(jsonObject.getString("message"), 0, 0);
					invalidateOptionsMenu();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.MESSAGES_UNBLOCK_PATIENT)){
			//{"status":"success","message":"Patient has been unblocked"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String status = jsonObject.getString("status");
				if(status.equalsIgnoreCase("success")){
					isBlocked = false;
					blockID = "";
					customToast.showToast(jsonObject.getString("message"), 0, 0);
					invalidateOptionsMenu();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}


	private void checkBlockPatient(String patient_id){
		RequestParams params = new RequestParams();
		params.put("patient_id", patient_id);
		params.put("doctor_id", prefs.getString("id", ""));

		ApiManager.shouldShowPD = false;
		ApiManager apiManager = new ApiManager(ApiManager.MESSAGES_CHECK_BLOCKPATIENT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}
	private void blockPatient(String patient_id){
		RequestParams params = new RequestParams();
		params.put("patient_id", patient_id);
		params.put("doctor_id", prefs.getString("id", ""));

		ApiManager apiManager = new ApiManager(ApiManager.MESSAGES_BLOCK_PATIENT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}
	private void unBlockPatient(String id){
		RequestParams params = new RequestParams();
		params.put("id", id);

		ApiManager apiManager = new ApiManager(ApiManager.MESSAGES_UNBLOCK_PATIENT,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

}
