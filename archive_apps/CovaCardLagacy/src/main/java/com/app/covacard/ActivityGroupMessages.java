package com.app.covacard;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.app.covacard.adapter.GroupMessageAdapter;
import com.app.covacard.api.ApiCallBack;
import com.app.covacard.api.ApiManager;
import com.app.covacard.model.GroupMemberBean;
import com.app.covacard.model.GroupMessageBean;
import com.app.covacard.util.CheckInternetConnection;
import com.app.covacard.util.CustomToast;
import com.app.covacard.util.DATA;
import com.app.covacard.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityGroupMessages extends AppCompatActivity implements ApiCallBack{
	
	static Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	ApiCallBack apiCallBack;
	
	EditText etSendMessage;
	ImageButton ivSendMessage;
	
	ListView lvMessages;
	TextView tvNoMsgs;


	@Override
	protected void onResume() {
		loadGroupMsges();
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_messages);

		if(ActivityGroupChat.selectedGroupBean != null){
			getSupportActionBar().setTitle(ActivityGroupChat.selectedGroupBean.group_name);
		}

		activity = ActivityGroupMessages.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		apiCallBack = this;
		
		etSendMessage = (EditText) findViewById(R.id.etSendMessage);
		ivSendMessage = (ImageButton) findViewById(R.id.ivSendMessage);

		
		lvMessages = (ListView) findViewById(R.id.lvMessages);
		tvNoMsgs = (TextView) findViewById(R.id.tvNoMsgs);
		
		ivSendMessage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (etSendMessage.getText().toString().isEmpty()) {
					etSendMessage.setError("Please type your message");
				} else {
					RequestParams params = new RequestParams();
					params.put("user_type", "patient");
					params.put("group_id", ActivityGroupChat.selectedGroupBean.id);
					params.put("user_id", prefs.getString("id",""));
					params.put("message_text", etSendMessage.getText().toString());

					ApiManager apiManager = new ApiManager(ApiManager.SEND_GROUP_MESSAGE,"post",params,apiCallBack, activity);
					apiManager.loadURL();
				}
			}
		});
	}

	public void loadGroupMsges(){
		RequestParams params = new RequestParams();
		params.put("group_id", ActivityGroupChat.selectedGroupBean.id);

		ApiManager apiManager = new ApiManager(ApiManager.GET_GROUP_MESSAGES,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_group_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}else if(item.getItemId() == R.id.action_info){
			if(groupMemberBeens != null){
				openActivity.open(ActivityGroupMembers.class,false);
			}else {
				customToast.showToast("Group info not available",0,0);
			}
		}
		return super.onOptionsItemSelected(item);
	}

	public static ArrayList<String> groupParticepentsDoctorIds = new ArrayList<>();
	public static ArrayList<String> groupParticepentsPatientIds = new ArrayList<>();
	public static ArrayList<GroupMemberBean> groupMemberBeens = new ArrayList<>();
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.GET_GROUP_MESSAGES)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				if(data.length() == 0){
					tvNoMsgs.setVisibility(View.VISIBLE);
				}else {
					tvNoMsgs.setVisibility(View.GONE);
				}

				ArrayList<GroupMessageBean> groupMessageBeens = new ArrayList<>();
				GroupMessageBean bean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String group_id = data.getJSONObject(i).getString("group_id");
					String message_text = data.getJSONObject(i).getString("message_text");
					String user_type = data.getJSONObject(i).getString("user_type");
					String user_id = data.getJSONObject(i).getString("user_id");
					String dateof = data.getJSONObject(i).getString("dateof");
					String is_read = data.getJSONObject(i).getString("is_read");
					String uname = data.getJSONObject(i).getString("uname");
					String image = data.getJSONObject(i).getString("image");

					bean = new GroupMessageBean(id,group_id,message_text,user_type,user_id,dateof,is_read,uname,image);
					groupMessageBeens.add(bean);
					bean = null;
				}

				GroupMessageAdapter groupMessageAdapter = new GroupMessageAdapter(activity,groupMessageBeens);
				lvMessages.setAdapter(groupMessageAdapter);

				JSONArray doctors = jsonObject.getJSONObject("users").getJSONArray("doctors");
				JSONArray patients = jsonObject.getJSONObject("users").getJSONArray("patients");
				groupParticepentsDoctorIds = new ArrayList<>();
				groupParticepentsPatientIds= new ArrayList<>();
				groupMemberBeens = new ArrayList<>();
				GroupMemberBean groupMemberBean;
				for (int i = 0; i < doctors.length(); i++) {
					groupParticepentsDoctorIds.add(doctors.getJSONObject(i).getString("user_id"));

					String user_id = doctors.getJSONObject(i).getString("user_id");
					String first_name = doctors.getJSONObject(i).getString("first_name");
					String last_name = doctors.getJSONObject(i).getString("last_name");
					String image = doctors.getJSONObject(i).getString("image");
					String doctor_category = doctors.getJSONObject(i).getString("doctor_category");
					String is_online = doctors.getJSONObject(i).getString("is_online");
					if(doctor_category.isEmpty()){
						doctor_category = "Doctor";
					}

					groupMemberBean = new GroupMemberBean(user_id,first_name,last_name,image,doctor_category,is_online);
					groupMemberBeens.add(groupMemberBean);
					groupMemberBean = null;
				}
				for (int i = 0; i < patients.length(); i++) {
					groupParticepentsPatientIds.add(patients.getJSONObject(i).getString("user_id"));
					String user_id = patients.getJSONObject(i).getString("user_id");
					String first_name = patients.getJSONObject(i).getString("first_name");
					String last_name = patients.getJSONObject(i).getString("last_name");
					String image = patients.getJSONObject(i).getString("image");
					String is_online = patients.getJSONObject(i).getString("is_online");

					groupMemberBean = new GroupMemberBean(user_id,first_name,last_name,image,"Patient",is_online);
					groupMemberBeens.add(groupMemberBean);
					groupMemberBean = null;
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equals(ApiManager.SEND_GROUP_MESSAGE)){
			//result: {"status":"success","message":"Message has been sent.","data":{"group_id":"11","message_text":"Hi",
			// "user_type":"doctor","user_id":"8","dateof":"05\/17\/2017 17:55","is_read":0}}
			try {
				JSONObject jsonObject = new JSONObject(content);
				String message = jsonObject.getString("message");

				customToast.showToast(message,0,0);
				if(jsonObject.getString("status").equals("success")){
					etSendMessage.setText("");

					loadGroupMsges();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
