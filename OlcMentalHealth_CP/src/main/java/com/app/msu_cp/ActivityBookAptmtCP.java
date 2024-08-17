package com.app.msu_cp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.app.msu_cp.model.ConditionsModel;
import com.app.msu_cp.model.SymptomsModel;
import com.app.msu_cp.util.DATA;
import com.app.msu_cp.util.DatePickerFragment3;
import com.app.msu_cp.util.GloabalMethods;

import java.util.ArrayList;

public class ActivityBookAptmtCP extends BaseActivity {


	Spinner spSelectSymptom,spSelectCondtn;
	Button btnSbmtSymptom;
	EditText etExtraInfo,etZipCode,etApptmntDate;
	//SingleDateAndTimePicker dateTimePicer;// note : etApptmntDate is  hidden and not used
	ImageView imgAdSearchadr;
	Button btnPharmacy;
	TextView tvSelPharmacy;

	//private String selectedSympId = "";

	GloabalMethods gloabalMethods;
	BroadcastReceiver showSelectedPharmacyBroadcast;

	ArrayAdapter<ConditionsModel> conditionsAdapter;

	public static String symptom_id = "",condition_id = "";

	/*LinearLayout layPharmacyInfo;
	TextView tvNoPharmacyAdded;
	TextView tvPharmacyName,tvPharmacyStreetAddress,tvPharmacyCity,tvPharmacyState,tvPharmacyZipcode,tvPharmacyPhone,tvPharmacyCategories;
	Button btnSelectPharmacy;*/



	@Override
	protected void onStart() {
		registerReceiver(showSelectedPharmacyBroadcast, new IntentFilter(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION));
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(showSelectedPharmacyBroadcast);
		super.onStop();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_apptmnt_cp);

		btnSbmtSymptom = (Button) findViewById(R.id.btnSbmtSymptom);
		spSelectCondtn =(Spinner) findViewById(R.id.spSelectCondtn);
		spSelectSymptom =(Spinner) findViewById(R.id.spSelectSymptom);
		etExtraInfo = (EditText) findViewById(R.id.etExtraInfo);
		etZipCode = (EditText) findViewById(R.id.etZipCode);
		etApptmntDate = (EditText) findViewById(R.id.etApptmntDate);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);

		etApptmntDate.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment3(etApptmntDate,activity);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});


		AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				switch (parent.getId()){
					case R.id.spSelectSymptom:
						if(position == 0){
							prefs.edit().putString("selectedSympId", "").commit();

							DATA.allConditions = new ArrayList<ConditionsModel>();
							DATA.allConditions.add(new ConditionsModel("","","Possible Condition"));
							conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allConditions);
							spSelectCondtn.setAdapter(conditionsAdapter);
						}else {
							prefs.edit().putString("selectedSympId", DATA.allSymptoms.get(position).symptomId).commit();

							DATA.allConditions = database.getAllConditions(DATA.allSymptoms.get(position).symptomId);

							if(DATA.allConditions != null) {
								conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allConditions);
								spSelectCondtn.setAdapter(conditionsAdapter);

								for (int i = 0; i < DATA.allConditions.size(); i++) {
									if(DATA.allConditions.get(i).conditionId.equalsIgnoreCase(condition_id)){
										spSelectCondtn.setSelection(i);
									}
								}
							}
						}
						break;
					case R.id.spSelectCondtn:
						prefs.edit().putString("selectedCondId", DATA.allConditions.get(position).conditionId).commit();
						break;
					default:
						break;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		};

		spSelectSymptom.setOnItemSelectedListener(onItemSelectedListener);
		spSelectCondtn.setOnItemSelectedListener(onItemSelectedListener);

		//symptoms from db....
		DATA.allSymptoms = database.getAllSymptoms();
		DATA.allSymptoms.add(0, new SymptomsModel("", "Select Symptom"));
		ArrayAdapter<SymptomsModel> symptomsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allSymptoms);
		spSelectSymptom.setAdapter(symptomsAdapter);

		etExtraInfo.setText(DATA.selectedUserCallDescription);
		for (int i = 0; i < DATA.allSymptoms.size(); i++) {
			if(DATA.allSymptoms.get(i).symptomId.equalsIgnoreCase(symptom_id)){
				spSelectSymptom.setSelection(i);
			}
		}


		btnSbmtSymptom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				hideShowKeypad.hideSoftKeyboard();

				prefs.edit().putString("extraInfo",etExtraInfo.getText().toString()).commit();

				//if(etZipCode.getText().toString().isEmpty()) {
					//customToast.showToast("Please enter the zip code", 0, 0);
				//}else
				if(etApptmntDate.getText().toString().isEmpty()) {
					customToast.showToast("Please enter the date for the appointment", 0, 0);
				}else if(prefs.getString("selectedCondId", "").isEmpty() || prefs.getString("selectedCondId", "").isEmpty()) {
					customToast.showToast("Please enter your symptoms and conditions", 0, 0);
				} else {

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("apptmntDate",etApptmntDate.getText().toString());
					ed.putString("apptmntDateOriginal",etApptmntDate.getText().toString());
					ed.commit();

					//DoctorsList.zipcodeForApptmnt = etZipCode.getText().toString();
					//DoctorsList.apptmntDate = etApptmntDate.getText().toString();
					//openActivity.open(DoctorsList.class, false);

					//go to selected dr screen
					openActivity.open(ActivityBookAptmtCP2.class,false);
				}
			}
		});

		imgAdSearchadr = (ImageView) findViewById(R.id.imgAdSearchadr);
		imgAdSearchadr.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				DATA.addIntent(activity);
			}
		});


		/*btnSelectPharmacy = (Button) findViewById(btnSelectPharmacy);
		layPharmacyInfo = (LinearLayout) findViewById(layPharmacyInfo);
		tvNoPharmacyAdded = (TextView) findViewById(tvNoPharmacyAdded);
	    tvPharmacyName = (TextView) findViewById(tvPharmacyName);
	    tvPharmacyStreetAddress = (TextView) findViewById(tvPharmacyStreetAddress);
	    tvPharmacyCity = (TextView) findViewById(tvPharmacyCity);
	    tvPharmacyState = (TextView) findViewById(tvPharmacyState);
	    tvPharmacyZipcode = (TextView) findViewById(tvPharmacyZipcode);
	    tvPharmacyPhone = (TextView) findViewById(tvPharmacyPhone);
	    tvPharmacyCategories = (TextView) findViewById(tvPharmacyCategories);*/

		btnPharmacy = (Button) findViewById(R.id.btnPharmacy);
		tvSelPharmacy = (TextView) findViewById(R.id.tvSelPharmacy);

		gloabalMethods = new GloabalMethods(activity);

		if (checkInternetConnection.isConnectedToInternet()) {
			gloabalMethods.getPharmacy("",false);
		} else {
			customToast.showToast("Network not connected", 0, 0);
		}

		btnPharmacy.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (GloabalMethods.pharmacyBeans != null) {
					gloabalMethods.showPharmacyDialog();
				} else {
					DATA.print("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true);
					} else {
						customToast.showToast("Network not connected", 0, 0);
					}
				}
			}
		});

		showSelectedPharmacyBroadcast = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION)) {


			    /*gloabalMethods.showPharmacyMap(GloabalMethods.selectedPharmacyBean);

			    if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
			    	layPharmacyInfo.setVisibility(View.GONE);
			    	tvNoPharmacyAdded.setVisibility(View.VISIBLE);
				}else {
					layPharmacyInfo.setVisibility(View.VISIBLE);
					tvNoPharmacyAdded.setVisibility(View.GONE);
				    tvPharmacyName.setText(GloabalMethods.selectedPharmacyBean.StoreName);
				    tvPharmacyStreetAddress.setText(GloabalMethods.selectedPharmacyBean.address);
				    tvPharmacyCity.setText(GloabalMethods.selectedPharmacyBean.city);
				    tvPharmacyState.setText(GloabalMethods.selectedPharmacyBean.state);
				    tvPharmacyZipcode.setText(GloabalMethods.selectedPharmacyBean.zipcode);
				    tvPharmacyPhone.setText(GloabalMethods.selectedPharmacyBean.PhonePrimary);
				    tvPharmacyCategories.setText(GloabalMethods.selectedPharmacyBean.SpecialtyName);
				}*/

					if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
						btnPharmacy.setText("Select Pharmacy");
						tvSelPharmacy.setText("You don't have selected your pharmacy. Please select pharmacy before apply for appointment.");
						btnSbmtSymptom.setEnabled(false);
					}else {
						btnPharmacy.setText("Change Pharmacy");
						btnSbmtSymptom.setEnabled(true);
						tvSelPharmacy.setText("Selected Pharmacy: "+GloabalMethods.selectedPharmacyBean.StoreName);
					}
				}
			}
		};
	}
}

