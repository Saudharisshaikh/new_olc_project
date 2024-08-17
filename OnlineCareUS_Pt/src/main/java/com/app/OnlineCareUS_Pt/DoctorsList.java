package com.app.OnlineCareUS_Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.app.OnlineCareUS_Pt.adapter.UsersAdapter;
import com.app.OnlineCareUS_Pt.api.ApiCallBack;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.api.CustomSnakeBar;
import com.app.OnlineCareUS_Pt.model.DoctorsModel;
import com.app.OnlineCareUS_Pt.model.UrgentCareBean;
import com.app.OnlineCareUS_Pt.util.CheckInternetConnection;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DoctorsList  extends AppCompatActivity implements ApiCallBack{
	
	Activity activity;
	ApiCallBack apiCallBack;
	CustomSnakeBar customSnakeBar;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	AlertDialog.Builder builder;
	UsersAdapter usersAdapter;
	LinearLayout urgentCareSpCont;
	Spinner spUrgentCareCenter;
	ListView lvUsersList;

	public static String zipcodeForApptmnt = "",apptmntDate = "";
	
	public static boolean goback = false;
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (goback) {
			goback = false;
			onBackPressed();
		}
		super.onResume();
	}
			 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_list);

        activity = DoctorsList.this;
		apiCallBack = this;
		customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		lvUsersList = (ListView) findViewById(R.id.lvUsersList);
		urgentCareSpCont = (LinearLayout) findViewById(R.id.urgentCareSpCont);
		spUrgentCareCenter = (Spinner) findViewById(R.id.spUrgentCareCenter);

		builder = new Builder(activity, R.style.CustomAlertDialogTheme);
		builder.setMessage("Currently no doctors available. Would you like other options? We can have our office manager to follow up with you.");// Do you want to look for other doctors or nearby hospitals?
		/*builder.setNeutralButton("All Doctors", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (checkInternetConnection.isConnectedToInternet()) {
					searchDoctors();
				} else {
					Toast.makeText(activity, "Please check internet connection", 0).show();
				}
			}
		});
		
		builder.setNegativeButton("Show nearby hospitals", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {


				 GPSTracker gpsTracker = new GPSTracker(activity);
				 if (gpsTracker.canGetLocation()) {
					 //mMaterialDialog.dismiss();
					double lat = gpsTracker.getLatitude();
					double lng = gpsTracker.getLongitude();
					getNearByHospitals(lat, lng);
				} else {
					gpsTracker.showSettingsAlert();
				}	
			
			
			}
		});
		builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});*/
		
		builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		});

		builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(activity, BookAppointment.class);
				intent.putExtra("isDirectApptment", true);
				startActivity(intent);
				finish();
			}
		});


		/*if(prefs.getString("hospital_id","").equalsIgnoreCase(DATA.urgentCareHospitalID)){
			urgentCareSpCont.setVisibility(View.VISIBLE);

			spUrgentCareCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					RequestParams params = new RequestParams();
					params.put("zipcode",zipcodeForApptmnt);
					params.put("appointment_date",apptmntDate);
					params.put("hospital_id",urgentCareBeans.get(position).id);
					ApiManager apiManager = new ApiManager(ApiManager.SEARCH_DOCTOR_BY_ZIPCODE2,"post",params,apiCallBack, activity);
					apiManager.loadURL();
				}

				@Override
				public void onNothingSelected(AdapterView<?> parent) {

				}
			});

			ApiManager apiManager = new ApiManager(ApiManager.GET_URGENT_CARES,"post",null,apiCallBack, activity);
			apiManager.loadURL();
		}else{
			urgentCareSpCont.setVisibility(View.GONE);
			RequestParams params = new RequestParams();
			params.put("zipcode",zipcodeForApptmnt);
			params.put("appointment_date",apptmntDate);
			ApiManager apiManager = new ApiManager(ApiManager.SEARCH_DOCTOR_BY_ZIPCODE2,"post",params,apiCallBack, activity);
			apiManager.loadURL();
		}*/

		urgentCareSpCont.setVisibility(View.GONE);
		RequestParams params = new RequestParams();
		params.put("zipcode",zipcodeForApptmnt);
		params.put("appointment_date",apptmntDate);
		ApiManager apiManager = new ApiManager(ApiManager.SEARCH_DOCTOR_BY_ZIPCODE2,"post",params,apiCallBack, activity);
		apiManager.loadURL();

        lvUsersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
					
					/*DATA.selectedDrId = DATA.allDoctors.get(position).id;
					DATA.selectedDrName = DATA.allDoctors.get(position).fName + " " + DATA.allDoctors.get(position).lName;
					DATA.selectedDrQbId = DATA.allDoctors.get(position).qb_id;
					DATA.selectedDrImage = DATA.allDoctors.get(position).image;
					DATA.selectedDrQualification = DATA.allDoctors.get(position).qualification;
					DATA.selectedDrDesignation = DATA.allDoctors.get(position).careerData;*/
				
					prefs.edit().putString("apptmntDate", prefs.getString("apptmntDateOriginal", "")).commit();
					
					DATA.doctorsModelForApptmnt = DATA.allDoctors.get(position);
					
					DATA.selectedSlotIdForAppointment = DATA.allDoctors.get(position).slot_id;
					DATA.print("-- slot id assigned "+ DATA.selectedSlotIdForAppointment);
					
					Intent intent = new Intent(activity,SelectedDoctorsProfile.class);
					startActivity(intent);
//					finish();
				
			}
		});

    }//oncreate

	ArrayList<UrgentCareBean> urgentCareBeans;
	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {
		if(apiName.equals(ApiManager.SEARCH_DOCTOR_BY_ZIPCODE2)){
			try {
				JSONObject jsonObject = new JSONObject(content);

				//DATA.selectedRGEveMor = jsonObject.getString("session");

				String status = jsonObject.getString("status");
				DATA.selected_dayForApptmnt = jsonObject.getString("selected_day");


				if(status.equals("success")) {

					String drsStr = jsonObject.getString("doctors_info");

					JSONArray doctorsArray = new JSONArray(drsStr);

					DATA.allDoctors = new ArrayList<DoctorsModel>();
					//DATA.allSchedule = new ArrayList<DrSheduleModel>();

					DoctorsModel temp;

					for(int i = 0; i<doctorsArray.length(); i++) {

						temp = new DoctorsModel();

						temp.id = doctorsArray.getJSONObject(i).getString("id");
						//temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
						temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
						temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
						temp.email = doctorsArray.getJSONObject(i).getString("email");
						temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
						temp.image = doctorsArray.getJSONObject(i).getString("image");


						temp.introduction = doctorsArray.getJSONObject(i).getString("introduction");
						temp.careerData = doctorsArray.getJSONObject(i).getString("career_data");
						temp.latitude = doctorsArray.getJSONObject(i).getString("latitude");
						temp.longitude = doctorsArray.getJSONObject(i).getString("longitude");

						if (doctorsArray.getJSONObject(i).has("mobile")) {
							temp.mobile = doctorsArray.getJSONObject(i).getString("mobile");
						}
						if (doctorsArray.getJSONObject(i).has("designation")) {
							temp.designation = doctorsArray.getJSONObject(i).getString("designation");
						}
						if (doctorsArray.getJSONObject(i).has("slot_id")) {
							temp.slot_id = doctorsArray.getJSONObject(i).getString("slot_id");
						}

						DATA.print("--online care callwebservice getOnline Doctors fname: "+temp.fName);

						DATA.allDoctors.add(temp);

						temp = null;

									/*String drSchedleStr = doctorsArray.getJSONObject(i).getString("doctor_schedules");

									drSheduleArray = new JSONArray(drSchedleStr);


									DrSheduleModel temp1;

									for(int j = 0; j<drSheduleArray.length(); j++) {

										temp1 = new DrSheduleModel();

										temp1.id = drSheduleArray.getJSONObject(j).getString("id");
										temp1.drId = drSheduleArray.getJSONObject(j).getString("doctor_id");
										temp1.day = drSheduleArray.getJSONObject(j).getString("day");
										temp1.fromTime = drSheduleArray.getJSONObject(j).getString("from_time");
										temp1.toTime = drSheduleArray.getJSONObject(j).getString("to_time");
										temp1.eveningFromTime = drSheduleArray.getJSONObject(j).getString("evening_from_time");
										temp1.eveningToTime = drSheduleArray.getJSONObject(j).getString("evening_to_time");

										DATA.allSchedule.add(temp1);

										temp1 = null;
									}*/

					}

					if(DATA.allDoctors.size() == 0) {
						final AlertDialog alert = builder.create();
						alert.setCancelable(false);
						if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
							alert.setOnShowListener(new DialogInterface.OnShowListener() {
								@Override
								public void onShow(DialogInterface dialog) {
									try {
										LinearLayout linearLayout = (LinearLayout) alert.getButton(DialogInterface.BUTTON_NEUTRAL).getParent();
										if (linearLayout != null) {
											linearLayout.setOrientation(LinearLayout.VERTICAL);
											linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
										}
									} catch (Exception ignored) {

									}
								}
							});
						}
						alert.show();
					}
					usersAdapter = new UsersAdapter(activity);
					lvUsersList.setAdapter(usersAdapter);
				}else {
					customSnakeBar.showToast("Something went wrong");
				}
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equals(ApiManager.GET_URGENT_CARES)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				urgentCareBeans = new ArrayList<>();
				UrgentCareBean bean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String center_name = data.getJSONObject(i).getString("center_name");
					String is_deleted = data.getJSONObject(i).getString("is_deleted");
					String latitude = "0";
					String longitude = "0";
					if (data.getJSONObject(i).has("latitude")){
						latitude = data.getJSONObject(i).getString("latitude");
					}
					if (data.getJSONObject(i).has("longitude")){
						longitude = data.getJSONObject(i).getString("longitude");
					}

					String total_doctors = "0";

					if(i == 0){
						//center_name = center_name+"\nTap to view OnlineCare Doctors";
					}else{
						total_doctors = data.getJSONObject(i).getString("total_doctors");
					}

					bean = new UrgentCareBean(id,center_name,is_deleted,latitude,longitude,total_doctors);
					urgentCareBeans.add(bean);
					bean = null;
				}
				ArrayAdapter<UrgentCareBean> hospitalAdapter = new ArrayAdapter<UrgentCareBean>(activity, android.R.layout.simple_dropdown_item_1line, urgentCareBeans);
				spUrgentCareCenter.setAdapter(hospitalAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}