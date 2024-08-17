package com.app.mdlive_dr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.app.mdlive_dr.adapter.UsersAdapter;
import com.app.mdlive_dr.api.ApiCallBack;
import com.app.mdlive_dr.api.ApiManager;
import com.app.mdlive_dr.model.DoctorsModel;
import com.app.mdlive_dr.util.CheckInternetConnection;
import com.app.mdlive_dr.util.CustomToast;
import com.app.mdlive_dr.util.DATA;
import com.app.mdlive_dr.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReferAppointmentActivity extends AppCompatActivity implements ApiCallBack{

	Activity activity;
	OpenActivity openActivity;
	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	ApiCallBack apiCallBack;
	ListView lvDoctors;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_refer_appointment);

		activity = ReferAppointmentActivity.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		apiCallBack = this;

		lvDoctors = (ListView) findViewById(R.id.lvDoctors);

		lvDoctors.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
				// TODO Auto-generated method stub
				confirmDialog(activity, "Confirm Refer", "Are you sure? You want to refer "+DATA.selectedApptmntIdsForRefer.size()
								+" appointments to "+DATA.allDoctors.get(arg2).fName+" "+DATA.allDoctors.get(arg2).lName+" ?",
						DATA.allDoctors.get(arg2).id);


			}
		});

		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("user_type", "doctor");
		ApiManager apiManager = new ApiManager(ApiManager.SEARCHALLDOCTORS_,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}//oncreate



	public void confirmDialog(final Context context, String tittle , String content,final String drId) {
		new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkInternetConnection.isConnectedToInternet()) {
							matchreferedSlots(DATA.selectedApptmntIdsForRefer, drId);
						} else {
							customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
						}
					}
				})
				.setNegativeButton("Cancel", null)
				.create()
				.show();

	}

	public void matchreferedSlots(ArrayList<String> appointment_ids, String doctor_id) {
		String ids = "";
		for (int i = 0; i < appointment_ids.size(); i++) {
			ids = ids + appointment_ids.get(i) + ",";
		}
		ids = ids.substring(0, (ids.length()-1));

		RequestParams params = new RequestParams();
		params.put("appointment_ids", ids);
		params.put("doctor_id", doctor_id);
		ApiManager apiManager = new ApiManager(ApiManager.MATCHREFEREDSLOTS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}//end matchreferedSlots

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equals(ApiManager.SEARCHALLDOCTORS_)){
			try {
				JSONArray data = new JSONArray(content);
				DATA.allDoctors = new ArrayList<DoctorsModel>();
				DoctorsModel temp = null;

				if (data.length() == 0) {
					//showMessageBox(activity, "We are sorry", "Currently no doctors available");
				}else{
					for (int i = 0; i < data.length(); i++) {
						temp = new DoctorsModel();
						JSONObject object = data.getJSONObject(i);
						temp.id = object.getString("id");
						temp.latitude =object.getString("latitude");
						temp.longitude=object.getString("longitude");
						temp.zip_code=object.getString("zip_code");
						temp.fName=object.getString("first_name");
						temp.lName=object.getString("last_name");
						temp.is_online=object.getString("is_online");
						temp.image=object.getString("image");
						temp.designation=object.getString("designation");


						if (temp.latitude.equalsIgnoreCase("null")) {
							temp.latitude = "0.0";
						}
						if (temp.longitude.equalsIgnoreCase("null")) {
							temp.longitude = "0.0";
						}

						temp.speciality_id=object.getString("speciality_id");
						temp.current_app=object.getString("current_app");
						if (!temp.id.equalsIgnoreCase(prefs.getString("id", ""))) {
							DATA.allDoctors.add(temp);
						}

						temp = null;
					}

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
					UsersAdapter usersAdapter = new UsersAdapter(activity);
					lvDoctors.setAdapter(usersAdapter);

				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(apiName.equals(ApiManager.MATCHREFEREDSLOTS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				customToast.showToast(jsonObject.getString("msg"), 0, 1);
				finish();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
