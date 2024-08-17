package com.app.OnlineCareUS_Pt.b_health2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.BaseActivity;
import com.app.OnlineCareUS_Pt.MainActivityNew;
import com.app.OnlineCareUS_Pt.MyAppointments;
import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.model.SlotBean;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityDoctorSlots extends BaseActivity {


	ListView lvDrSchedule;
	TextView tvSelectedDate,tvNoData;
	Button btnContinue,btnNotNow;
	EditText etDateApt;

	ImageView ivProvider,ivIsonline;
	TextView tvProviderName,tvProviderDesig;

	protected static DoctorBean doctorBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dr_slots);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Schedule an Appointment");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		tvSelectedDate = (TextView) findViewById(R.id.tvSelectedDate);
		tvNoData = findViewById(R.id.tvNoData);
		lvDrSchedule = (ListView) findViewById(R.id.lvDrSchedule);
		etDateApt = findViewById(R.id.etDateApt);

		ivProvider = findViewById(R.id.ivProvider);
		ivIsonline = findViewById(R.id.ivIsonline);
		tvProviderName = findViewById(R.id.tvProviderName);
		tvProviderDesig = findViewById(R.id.tvProviderDesig);


		//tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));

		btnContinue = (Button) findViewById(R.id.btnContinue);
		btnNotNow = (Button) findViewById(R.id.btnNotNow);


		tvProviderName.setText(doctorBean.first_name+ " "+doctorBean.last_name);
		tvProviderDesig.setText(doctorBean.designation);
		DATA.loadImageFromURL(doctorBean.image,R.drawable.icon_call_screen, ivProvider);
		int resID = doctorBean.is_online.equals("1") ? R.drawable.icon_online:R.drawable.icon_notification;
		ivIsonline.setImageResource(resID);


		etDateApt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(etDateApt);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		btnNotNow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});

		btnContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if(TextUtils.isEmpty(slot_id)){
					customToast.showToast("Please select a time for the appointment",0,0);
				}else {
					String msg = "Are you sure ? Do you want to book appointment with "+doctorBean.first_name+" "+doctorBean.last_name+" at "+tvSelectedDate.getText().toString();
					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
							.setTitle("Confirm")
							.setMessage(msg)
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									bookAppointment();
								}
							})
							.setNegativeButton("No", null)
							.create().show();
				}
			}
		});



		lvDrSchedule.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				DrSlotsAdapter.selectedPos = position;
				drSlotsAdapter.notifyDataSetChanged();
				String dateTimeApt = slotBeans.get(position).getDay()+" "+etDateApt.getText().toString().trim()+ " "+slotBeans.get(position).getFrom_time();
				tvSelectedDate.setText(dateTimeApt);
				slot_id = slotBeans.get(position).getId();
			}
		});
	}


	public void getDoctorsSlots() {
		RequestParams params = new RequestParams();
		params.put("doctor_id", doctorBean.id);
		params.put("appointment_date", etDateApt.getText().toString().trim());

		ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTOR_SLOTS2,"post",params,apiCallBack, activity);
		apiManager.loadURL();

	}//end getDoctorsSlots

	public void bookAppointment() {
		RequestParams params;
		if(GetLiveCareFormBhealth.eliveCareParams != null){
			params = GetLiveCareFormBhealth.eliveCareParams;
		}else {
			params = new RequestParams();
		}
		//params.put("doctor_id", doctorBean.id);
		params.put("slot_id", slot_id);
		params.put("appointment_date", etDateApt.getText().toString().trim());
		params.put("patient_id", prefs.getString("id",""));

		ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_BOOK_APPOINTMENT,"post",params,apiCallBack, activity);
		apiManager.loadURL();

	}//end getDoctorsSlots


	ArrayList<SlotBean> slotBeans;
	DrSlotsAdapter drSlotsAdapter;
	String slot_id = "";
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.GET_DOCTOR_SLOTS2)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				//String status = jsonObject.getString("status");
				JSONArray slots = jsonObject.getJSONArray("slots");

				slotBeans = new ArrayList<SlotBean>();
				SlotBean temp = null;

				for (int i = 0; i < slots.length(); i++) {
					String id = slots.getJSONObject(i).getString("id");
					String day = slots.getJSONObject(i).getString("day");
					String from_time = slots.getJSONObject(i).getString("from_time");
					String to_time = slots.getJSONObject(i).getString("to_time");
					String status = slots.getJSONObject(i).getString("status");
					String is_morning_evening = slots.getJSONObject(i).getString("is_morning_evening");

					temp = new SlotBean(id, day, from_time, to_time, status,is_morning_evening);
					slotBeans.add(temp);
					temp = null;
				}
				drSlotsAdapter = new DrSlotsAdapter(activity,slotBeans);
				lvDrSchedule.setAdapter(drSlotsAdapter);

				int vis = slotBeans.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoData.setVisibility(vis);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_BOOK_APPOINTMENT)){
			//{"status":"success","message":"Your appointment has been scheduled"}
			try {
				JSONObject jsonObject = new JSONObject(content);
				AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle(getResources().getString(R.string.app_name))
						.setMessage(jsonObject.optString("message"))
						.setPositiveButton("View Appointment", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								openActivity.open(MyAppointments.class, false);
							}
						})
						.setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
								//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
								intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								startActivity(intent);
							}
						})
						.create();
				if(jsonObject.optString("status").equalsIgnoreCase("success")){
					alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
						@Override
						public void onDismiss(DialogInterface dialog) {
							finish();
						}
					});
				}
				alertDialog.show();
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}
	}
}
