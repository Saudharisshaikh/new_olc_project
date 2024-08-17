package com.app.msu_uc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.Toast;

import com.app.msu_uc.adapter.MessageAdapter;
import com.app.msu_uc.api.ApiManager;
import com.app.msu_uc.api.Dialog_CustomProgress;
import com.app.msu_uc.model.MessageBean;
import com.app.msu_uc.util.ChoosePictureDialog;
import com.app.msu_uc.util.DATA;
import com.app.msu_uc.util.EasyAES;
import com.app.msu_uc.util.GloabalMethods;
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

public class ViewConversationActivity extends BaseActivity{


	TextView tvNoMsgs;
	ListView lvMessages;
	EditText etSendMessage;
	ImageView ivSendMessage,ivSendImage;
	ArrayList<MessageBean> messageBeans;
	Dialog_CustomProgress dialog_customProgress;

	BroadcastReceiver refreshChatMsgDelBC;
	public static final String REFRESH_CHAT_ACTION_MSG_DEL = BuildConfig.APPLICATION_ID+".refresh_messages_chat_action_one_message_deleted";

	@Override
	protected void onStart() {
		//DATA.shouldNotify = false;
		registerReceiver(refreshChatMsgDelBC, new IntentFilter(REFRESH_CHAT_ACTION_MSG_DEL));
		super.onStart();
	}
	@Override
	protected void onStop() {
		//DATA.shouldNotify = true;
		unregisterReceiver(refreshChatMsgDelBC);
		super.onStop();
	}

	@Override
	protected void onResume() {

		if(DATA.isImageCaptured){

			DATA.isImageCaptured = false;

			showPicDialog();

			super.onResume();

			return;
		}

		if (checkInternetConnection.isConnectedToInternet()) {
			if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")){

				String doctorID = "0";
				if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
					doctorID = DATA.selecetedBeanFromConversation.getDoctor_id();
				} else {
					doctorID = DATA.selecetedBeanFromConversation.getTo_id();
				}
				viewConversation(doctorID);
				checkBlockPatient(doctorID);
			}else {
				String speID = "0";
				if (DATA.selecetedBeanFromConversation.getTo_id().equalsIgnoreCase(prefs.getString("id", ""))) {
					speID = DATA.selecetedBeanFromConversation.getSpecialist_id();
				} else {
					speID = DATA.selecetedBeanFromConversation.getTo_id();
				}
				viewConversationspecialist(speID);
				checkBlockPatient(speID);

			}
		} else {
			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_conversation);


		dialog_customProgress = new Dialog_CustomProgress(activity);

		tvNoMsgs =(TextView) findViewById(R.id.tvNoMsgs);
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		etSendMessage = (EditText) findViewById(R.id.etSendMessage);
		ivSendMessage = (ImageView) findViewById(R.id.ivSendMessage);
		ivSendImage = (ImageView) findViewById(R.id.ivSendImage);

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
						Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
					} else {

						/*if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
							sendMsg(DATA.selecetedBeanFromConversation.getDoctor_id(), etSendMessage.getText().toString(),"doctor");
						} else {
							sendMsg(DATA.selecetedBeanFromConversation.getSpecialist_id(), etSendMessage.getText().toString(),"specialist");
						}*/


						if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")){

							if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
								//viewConversation(DATA.selecetedBeanFromConversation.getDoctor_id());
								sendMsg(DATA.selecetedBeanFromConversation.getDoctor_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"doctor",false);
							} else {
								//viewConversation(DATA.selecetedBeanFromConversation.getTo_id());
								sendMsg(DATA.selecetedBeanFromConversation.getTo_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"doctor",false);
							}
						}else {
							if (DATA.selecetedBeanFromConversation.getTo_id().equalsIgnoreCase(prefs.getString("id", ""))) {
								//viewConversationspecialist(DATA.selecetedBeanFromConversation.getSpecialist_id());
								sendMsg(DATA.selecetedBeanFromConversation.getSpecialist_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"specialist",false);
							} else {
								//viewConversationspecialist(DATA.selecetedBeanFromConversation.getTo_id());
								sendMsg(DATA.selecetedBeanFromConversation.getTo_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"specialist",false);
							}

						}

					}
				} else {
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
				}
			}
		});


		lvMessages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (messageBeans.get(position).msg_type.equalsIgnoreCase("file")) {
					showImgMsgDialog(activity, messageBeans.get(position).files,
							DATA.selecetedBeanFromConversation.getFirst_name()+" "+DATA.selecetedBeanFromConversation.getLast_name());
				}
			}
		});

		lvMessages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

				if (messageBeans.get(position).getFrom().equalsIgnoreCase("doctor") || messageBeans.get(position).getFrom().equalsIgnoreCase("specialist")) {
					return false;
				}else{

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
				}
			}
		});

		refreshChatMsgDelBC = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				onResume();
			}
		};
	}


	public void viewConversation(String doctorId) {

		ApiManager apiManager = new ApiManager(ApiManager.VIEW_CONVERSATIONS+doctorId+"/"+prefs.getString("id", "0"),"post",null,apiCallBack, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();

		DATA.print("-- url in viewConversation: "+DATA.baseUrl+ApiManager.VIEW_CONVERSATIONS+doctorId+"/"+prefs.getString("id", "0"));
		//DATA.print("-- url in viewConversation: "+DATA.baseUrl+"/viewConversation/patient/"+doctorId+"/"+prefs.getString("id", ""));
		//client.post(DATA.baseUrl+"/viewConversation/patient/"+doctorId+"/"+prefs.getString("id", ""), new AsyncHttpResponseHandler() {

	}//getpatientMessages

	public void viewConversationspecialist(String doctorId) {

		ApiManager apiManager = new ApiManager(ApiManager.VIEW_CONVERSATIONS_SPECIALIST+doctorId+"/"+prefs.getString("id", ""),"post",null,apiCallBack, activity);
		ApiManager.shouldShowLoader = false;
		apiManager.loadURL();

		DATA.print("-- url in viewConversation: "+DATA.baseUrl+ApiManager.VIEW_CONVERSATIONS_SPECIALIST+doctorId+"/"+prefs.getString("id", ""));
		//DATA.print("-- url in viewConversation: "+DATA.baseUrl+"/viewConversationspecialist/patient/"+doctorId+"/"+prefs.getString("id", ""));
		//client.post(DATA.baseUrl+"/viewConversationspecialist/patient/"+doctorId+"/"+prefs.getString("id", ""), new AsyncHttpResponseHandler() {

	}//getpatientMessages

	public void sendMsg(final String doctorId,String msgText,final String to, boolean isImage) {
		dialog_customProgress.showProgressDialog();
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);

		RequestParams params = new RequestParams();

		params.put("doctor_id", doctorId);
		params.put("patient_id",   prefs.getString("id", "0"));
		params.put("message_text", msgText);
		params.put("from", "patient");
		params.put("to", to);
		if(isImage){
			try {
				params.put("file", new File(DATA.imagePath));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		DATA.print("--params in sendmessagetoDoctor "+params.toString());

		client.post(DATA.baseUrl+"/sendmessagetoDoctor",params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				dialog_customProgress.dismissProgressDialog();
				try{
					String content = new String(response);

					DATA.print("--reaponce in sendmessagetoDoctor: "+content);
					//  tvDrsNotes.setText(content);

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							//dialogSendMessage.dismiss();
							customToast.showToast("Your message has been sent successfully",0,0);
							//finish();
							etSendMessage.setText("");
							//viewConversation(DATA.selecetedBeanFromConversation.getDoctor_id());
							if (to.equalsIgnoreCase("doctor")) {
								viewConversation(doctorId);
							} else {
								viewConversationspecialist(doctorId);
							}
						}else if (jsonObject.has("error")) {
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).
									setTitle("Info").setMessage(jsonObject.getString("message")).
									setPositiveButton("OK",null).show();
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
					DATA.print("-- responce onsuccess: sendmessagetoDoctor, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				dialog_customProgress.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail sendmessagetoDoctor" +content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end sendMsg

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.contains(ApiManager.VIEW_CONVERSATIONS)){
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

						message_text = EasyAES.decryptString(message_text);

						temp = new MessageBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image);
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(apiName.contains(ApiManager.VIEW_CONVERSATIONS_SPECIALIST)){
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

						message_text = EasyAES.decryptString(message_text);

						temp = new MessageBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image);
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
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.DELETE_MESSAGE)){
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
				//invalidateOptionsMenu();
				etSendMessage.setEnabled(!isBlocked);
				ivSendImage.setEnabled(!isBlocked);
				ivSendMessage.setEnabled(!isBlocked);
				if(isBlocked){
					etSendMessage.setHint("You can not send text messages to this provider.");
				}else {
					etSendMessage.setHint("Type your message");
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
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
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
					return;
				}

				picDialog.dismiss();



						/*if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")) {
							sendMsg(DATA.selecetedBeanFromConversation.getDoctor_id(), etSendMessage.getText().toString(),"doctor");
						} else {
							sendMsg(DATA.selecetedBeanFromConversation.getSpecialist_id(), etSendMessage.getText().toString(),"specialist");
						}*/


				if (DATA.selecetedBeanFromConversation.getUser_type().equalsIgnoreCase("doctor")){

					if (DATA.selecetedBeanFromConversation.getFrom().equalsIgnoreCase("doctor")) {
						//viewConversation(DATA.selecetedBeanFromConversation.getDoctor_id());
						sendMsg(DATA.selecetedBeanFromConversation.getDoctor_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"doctor", true);
					} else {
						//viewConversation(DATA.selecetedBeanFromConversation.getTo_id());
						sendMsg(DATA.selecetedBeanFromConversation.getTo_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"doctor", true);
					}
				}else {
					if (DATA.selecetedBeanFromConversation.getTo_id().equalsIgnoreCase(prefs.getString("id", ""))) {
						//viewConversationspecialist(DATA.selecetedBeanFromConversation.getSpecialist_id());
						sendMsg(DATA.selecetedBeanFromConversation.getSpecialist_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"specialist", true);
					} else {
						//viewConversationspecialist(DATA.selecetedBeanFromConversation.getTo_id());
						sendMsg(DATA.selecetedBeanFromConversation.getTo_id(), EasyAES.encryptString(etSendMessage.getText().toString()),"specialist", true);
					}

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


	public static void showImgMsgDialog(Activity activity, String imgURL, String tittleTxt){
		final Dialog picDialog = new Dialog(activity);
		picDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		picDialog.setContentView(R.layout.pic_dialog);

		PhotoView ivPhoto = (PhotoView) picDialog.findViewById(R.id.ivPhoto);
		TextView dialogTittle = (TextView) picDialog.findViewById(R.id.dialogTittle);

		dialogTittle.setText(tittleTxt);

		DATA.loadImageFromURL(imgURL,R.drawable.ic_placeholder_2,ivPhoto);

		picDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				picDialog.dismiss();
			}
		});

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(picDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		picDialog.show();
		picDialog.getWindow().setAttributes(lp);
	}


	String patientIDConv = "", blockID = "";
	boolean isBlocked;
	private void checkBlockPatient(String doctor_id){
		RequestParams params = new RequestParams();
		params.put("doctor_id", doctor_id);
		params.put("patient_id", prefs.getString("id", ""));

		ApiManager.shouldShowLoader= false;
		ApiManager apiManager = new ApiManager(ApiManager.MESSAGES_CHECK_BLOCKPATIENT,"post",params,apiCallBack, activity);
		//apiManager.loadURL();//just open this for block function activate
	}
}
