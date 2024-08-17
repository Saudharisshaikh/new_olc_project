package com.app.amnm_dr;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.amnm_dr.adapter.ProgressNotesAdapter;
import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.model.DoctorsModel;
import com.app.amnm_dr.model.ProgressNoteBean;
import com.app.amnm_dr.util.DATA;
import com.app.amnm_dr.util.DatePickerFragment;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityProgressNotesView extends BaseActivity {

	ListView lvProgressNotes;
	TextView tvNoData;

	EditText etSOAPDate;
	Spinner spSOAPDoc;

	public static boolean shouldRefreshPrNotes = false;

	@Override
	protected void onResume() {
		if(shouldRefreshPrNotes){
			shouldRefreshPrNotes = false;
			getPrNotes();
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progressnotes_view);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setTitle("Medical History");
		Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
		/*if(DATA.isFromDocToDoc){
			btnToolbar.setVisibility(View.GONE);
		}*/
		btnToolbar.setText("Add Progress Notes");
		btnToolbar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(activity,ActivityProgressNotes.class);
				intent.putExtra("isFromListScreen",true);
				startActivity(intent);
			}
		});

		lvProgressNotes = (ListView) findViewById(R.id.lvProgressNotes);
		tvNoData = (TextView) findViewById(R.id.tvNoData);
		etSOAPDate = (EditText) findViewById(R.id.etSOAPDate);
		spSOAPDoc = (Spinner) findViewById(R.id.spSOAPDoc);


		etSOAPDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(etSOAPDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		spSOAPDoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if(position != 0){
					selectedProviderId = notesDoctors.get(position).id;
					getPrNotes();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		lvProgressNotes.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ActivityProgressNotes.progressNoteBean = progressNoteBeans.get(position);
				Intent intent = new Intent(activity, ActivityProgressNotes.class);
				intent.putExtra("isForEdit",true);
				startActivity(intent);
			}
		});

		getPrNotes();

		ApiManager apiManager = new ApiManager(ApiManager.NOTES_DOCTORS,"post",null,apiCallBack, activity);
		ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}

	public void getPrNotes(){

		String date = "";
		if(!etSOAPDate.getText().toString().equalsIgnoreCase("According to Date")){
			date = etSOAPDate.getText().toString();
		}

		RequestParams params = new RequestParams();
		params.put("patient_id",DATA.selectedUserCallId);
		params.put("search_date",date);
		params.put("search_doctor_id",selectedProviderId);
		ApiManager apiManager = new ApiManager(ApiManager.GET_PROGRESS_NOTE,"post",params,apiCallBack, activity);
		//ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}

	String selectedProviderId = "";
	ArrayList<DoctorsModel> notesDoctors;
	ArrayList<ProgressNoteBean> progressNoteBeans;
	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		super.fetchDataCallback(status, apiName, content);

		if(apiName.equalsIgnoreCase(ApiManager.GET_PROGRESS_NOTE)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");
				if(data.length() == 0){
					tvNoData.setVisibility(View.VISIBLE);
				}else {
					tvNoData.setVisibility(View.GONE);
				}
				progressNoteBeans = new ArrayList<>();
				ProgressNoteBean progressNoteBean;
				for (int i = 0; i < data.length(); i++) {
					String id = data.getJSONObject(i).getString("id");
					String dateof = data.getJSONObject(i).getString("dateof");
					String patient_id = data.getJSONObject(i).getString("patient_id");
					String author_id = data.getJSONObject(i).getString("author_id");
					String call_id = data.getJSONObject(i).getString("call_id");
					String virtual_visit_id = data.getJSONObject(i).getString("virtual_visit_id");
					String explanatory_notes = data.getJSONObject(i).getString("explanatory_notes");
					String interventions = data.getJSONObject(i).getString("interventions");
					String feedback = data.getJSONObject(i).getString("feedback");
					String ddate = data.getJSONObject(i).getString("ddate");
					String ttime = data.getJSONObject(i).getString("ttime");
					String session_length = data.getJSONObject(i).getString("session_length");
					String first_name = data.getJSONObject(i).getString("first_name");
					String last_name = data.getJSONObject(i).getString("last_name");
					String patient_name = data.getJSONObject(i).getString("patient_name");
					String ot_data = data.getJSONObject(i).getString("ot_data");
					String symptom_name = data.getJSONObject(i).getString("symptom_name");
					String condition_name = data.getJSONObject(i).getString("condition_name");
					String care_plan = data.getJSONObject(i).getString("care_plan");

					progressNoteBean = new ProgressNoteBean(id,dateof,patient_id,author_id,call_id,virtual_visit_id,explanatory_notes,interventions
					,feedback,ddate,ttime,session_length,first_name,last_name,patient_name,ot_data,symptom_name,condition_name,care_plan);

					progressNoteBeans.add(progressNoteBean);
					progressNoteBean = null;
				}

				lvProgressNotes.setAdapter(new ProgressNotesAdapter(activity,progressNoteBeans));
			} catch (JSONException e) {
				e.printStackTrace();
				customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.NOTES_DOCTORS)){

			try {
				JSONArray data = new JSONArray(content);
				notesDoctors = new ArrayList<DoctorsModel>();
				DoctorsModel temp = null;

				temp = new DoctorsModel();
				temp.fName="Select ";
				temp.lName="Provider";
				notesDoctors.add(temp);
				temp = null;


				if (data.length() == 0) {
					//showMessageBox(activity, "We are sorry", "Currently no doctors available");
                    /*tvNoData.setVisibility(View.VISIBLE);
                    usersAdapter = new UsersAdapter(activity);
                    lvUsersList.setAdapter(usersAdapter);*/
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
						temp.speciality_name=object.getString("speciality_name");

						if(temp.current_app.equalsIgnoreCase("nurse")){
							temp.lName = temp.lName+" ("+object.getString("doctor_category")+")";
						}else {
							temp.lName = temp.lName+" ("+temp.current_app+")";
						}

						notesDoctors.add(temp);
						temp = null;
					}

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
                    /*UsersAdapter usersAdapter = new UsersAdapter(activity);
                    spSOAPDoc.setAdapter(usersAdapter);*/

					//R.layout.spinner_item_lay
					ArrayAdapter<DoctorsModel> spPtencyUnitAdapter = new ArrayAdapter<>(
							activity,
							R.layout.sp_soap_item,
							notesDoctors
					);
					spPtencyUnitAdapter.setDropDownViewResource(R.layout.sp_soap_item);
					spSOAPDoc.setAdapter(spPtencyUnitAdapter);

				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
