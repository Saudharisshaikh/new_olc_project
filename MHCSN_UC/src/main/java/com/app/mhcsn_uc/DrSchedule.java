package com.app.mhcsn_uc;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.app.mhcsn_uc.adapter.DrScheduleAdapter;
import com.app.mhcsn_uc.api.ApiManager;
import com.app.mhcsn_uc.model.SlotBean;
import com.app.mhcsn_uc.util.DATA;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DrSchedule extends BaseActivity {


	ListView lvDrSchedule;
	TextView tvSelectedDate,tvNoData;
	Button btnOtherDoctor,btnChangeDate,btnProceed;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dr_schedule);


		tvSelectedDate = (TextView) findViewById(R.id.tvSelectedDate);
		lvDrSchedule = (ListView) findViewById(R.id.lvDrSchedule);
		tvNoData = findViewById(R.id.tvNoData);

		tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));

		btnOtherDoctor = (Button) findViewById(R.id.btnOtherDoctor);
		btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
		btnProceed = (Button) findViewById(R.id.btnProceed);

		btnOtherDoctor.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.goBack = true;
				onBackPressed();
			}
		});
		btnChangeDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.goBackToForm = true;
				onBackPressed();
			}
		});
		btnProceed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SelectedDoctorsProfile.shouldProceed = true;
				onBackPressed();
			}
		});



		lvDrSchedule.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				DATA.print("-------click");
				String dt = prefs.getString("apptmntDate", "");
				String timetoreplace = dt.split(" ")[1]+" "+dt.split(" ")[2];
				dt = dt.replace(timetoreplace, slotBeans.get(arg2).getFrom_time());
				prefs.edit().putString("apptmntDate", dt).commit();

				DATA.print("-------dt "+dt);
				drScheduleAdapter.notifyDataSetChanged();
				tvSelectedDate.setText(DATA.selected_dayForApptmnt+" "+prefs.getString("apptmntDate", ""));

				DATA.selectedSlotIdForAppointment = slotBeans.get(arg2).getId();
				DATA.print("-- slot id Re_assigned "+ DATA.selectedSlotIdForAppointment);
			}
		});


		getDoctorsSlots(DATA.doctorsModelForApptmnt.id, prefs.getString("apptmntDate", ""));
	}


	public void getDoctorsSlots(String drId,String aptmntDate) {

		RequestParams params = new RequestParams();
		params.put("doctor_id", drId);
		params.put("appointment_date", aptmntDate);

		ApiManager apiManager = new ApiManager(ApiManager.GET_DOCTOR_SLOTS,"post",params,apiCallBack, activity);
		apiManager.loadURL();

	}//end getDoctorsSlots


	ArrayList<SlotBean> slotBeans;
	DrScheduleAdapter drScheduleAdapter;
	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(ApiManager.GET_DOCTOR_SLOTS)){
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
				drScheduleAdapter = new DrScheduleAdapter(activity,slotBeans);
				lvDrSchedule.setAdapter(drScheduleAdapter);

				int vis = slotBeans.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoData.setVisibility(vis);


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}
}
