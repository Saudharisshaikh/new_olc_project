package com.app.emcurama;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.emcurama.adapter.ConversationAdapter;
import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.ConversationBean;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.Database;
import com.app.emcurama.util.EasyAES;
import com.app.emcurama.util.OpenActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ConversationsActivity extends BaseActivity implements ApiCallBack{

	Activity activity;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	TextView tvNoConv;
	
	ListView lvConversations;
	ArrayList<ConversationBean> conversationBeans;
	
	@Override
	protected void onResume() {
		ApiManager apiManager = new ApiManager(ApiManager.GET_MESSAGES_CONVERSATIONS+prefs.getString("id", "0"),"post",null,apiCallBack, activity);
		apiManager.loadURL();
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_conversations);
		
		activity = ConversationsActivity.this;
		apiCallBack = this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);

		
		tvNoConv = (TextView) findViewById(R.id.tvNoConv);
		lvConversations = (ListView) findViewById(R.id.lvConversations);
		
		lvConversations.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
					long arg3) {
				DATA.selecetedBeanFromConversation = conversationBeans.get(pos);
				openActivity.open(ViewConversationActivity.class, false);
				
			}
		});

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_MESSAGE);
		
	}//oncreate

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.contains(ApiManager.GET_MESSAGES_CONVERSATIONS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				if (data.length() == 0) {
					tvNoConv.setVisibility(View.VISIBLE);
				} else {
					tvNoConv.setVisibility(View.GONE);
				}

				conversationBeans = new ArrayList<ConversationBean>();
				ConversationBean bean;
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
					String specialist_id = data.getJSONObject(i).getString("specialist_id");
					String user_type = data.getJSONObject(i).getString("user_type");

					String to_id = data.getJSONObject(i).getString("to_id");

					String doctor_category = data.getJSONObject(i).getString("doctor_category");

					message_text = EasyAES.decryptString(message_text);

					bean = new ConversationBean(id, doctor_id, patient_id, message_text, from, dateof, first_name, last_name, image,to,specialist_id,user_type,to_id);
					bean.doctor_category = doctor_category;
					conversationBeans.add(bean);
					bean = null;
				}
				lvConversations.setAdapter(new ConversationAdapter(activity, conversationBeans));
			} catch (JSONException e) {
				DATA.print("-- json exception getMessagesConversation");
				e.printStackTrace();
			}
		}
	}
}
