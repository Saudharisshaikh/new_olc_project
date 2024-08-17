package com.app.OnlineCareUS_Dr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.OnlineCareUS_Dr.adapter.SubUsersAdapter;
import com.app.OnlineCareUS_Dr.util.CustomToast;
import com.app.OnlineCareUS_Dr.util.DATA;
import com.app.OnlineCareUS_Dr.util.Database;
import com.loopj.android.http.AsyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

public class SubUsersList  extends BaseActivity {
	
	ListView lvSubUsersList;
	
	Activity activity;
	SharedPreferences prefs;
	JSONObject jsonObject, userInfoObject, adminObject;
	JSONArray clientAgentArray;
	String msg, status;
	ActionBar ab;

	AsyncHttpClient client;
	
	Database db;

	SubUsersAdapter usersAdapter;
	
	
//	@Override
//	protected void onResume() {
//		super.onResume();
//		
//		if(prefs.getBoolean("subUserSelected", false)) {
//			
//			Intent intent = new Intent(activity,MainActivityNew.class);
//			startActivity(intent);
//			finish();
//
//		}
//	}
	
	CustomToast customToast;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subusers_list);

        activity = SubUsersList.this;
        
        db = new Database(activity);
        
        customToast = new CustomToast(activity);

		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		client = new AsyncHttpClient();

//		ab = getSupportActionBar(); 
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4b6162"));     
//		ab.setBackgroundDrawable(colorDrawable);
		        
		lvSubUsersList = (ListView) findViewById(R.id.lvSubUsersList);
        
        usersAdapter = new SubUsersAdapter(activity);
        lvSubUsersList.setAdapter(usersAdapter);

        lvSubUsersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				if(position == 0) {
					
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("subPatientID","0");
					ed.putBoolean("subUserSelected", true);

					ed.commit();
				}
				else {

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("subPatientID", DATA.allSubUsers.get(position).id);
					ed.putString("first_name", DATA.allSubUsers.get(position).firstName);
					ed.putString("last_name", DATA.allSubUsers.get(position).lastName);
					ed.putString("username", DATA.allSubUsers.get(position).username);
					ed.putString("gender", DATA.allSubUsers.get(position).gender);
					ed.putString("birthdate", DATA.allSubUsers.get(position).dob);
//					ed.putString("phone", user_info.getString("phone"));
					ed.putString("image", DATA.allSubUsers.get(position).image);
					ed.putString("occupation", DATA.allSubUsers.get(position).occupation);
					ed.putString("marital_status", DATA.allSubUsers.get(position).marital_status);
					
					ed.putBoolean("subUserSelected", true);
					ed.commit();
					
				}
				
				
				customToast.showToast("Welcome: "+prefs.getString("first_name", "") + " " +prefs.getString("last_name", ""), 0	, 0);
				
					Intent intent = new Intent(activity,Splash.class);
					startActivity(intent);
					finish();

//				}
				
			}
		});

    }

 }