package com.app.mhcsn_ma;

import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mhcsn_ma.adapter.HistoryMediAdapter;
import com.app.mhcsn_ma.adapter.PastHistoryAdapter;
import com.app.mhcsn_ma.api.ApiManager;
import com.app.mhcsn_ma.model.PastHistoryBean;
import com.app.mhcsn_ma.model.RelativeHadBean;
import com.app.mhcsn_ma.util.DATA;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.app.mhcsn_ma.api.ApiManager.PREF_APP_LBL_KEY;

public class PatientMedicalHistory extends BaseActivity{


	Button btnFlipPrev,btnFlipNext;

	//past history layout...
	ListView lvMedicalHistory1;
	TextView tvNoPastHistory;
	EditText etPastOther;
	//MedicalHistoryAdapter medicalHistoryAdapter;
	StringBuilder s;

	//social history layout.,.
	RadioGroup rgMedicalSmoke, rgMedicalAlcohol, rgStreetDrug;
	EditText etAlcoholHowMuch, etAlcoholHowLong, etStreetDrugList,etSocialOther;//etSmokeHowMuch,etSmokeHowLong,
	RadioButton radioMedicalAlcoholYes,radioMedicalAlcoholNo,radioStreetDrugYes,radioStreetDrugNo;//radioMedicalSmokeYes,radioMedicalSmokeNo,
	//family history layout
	CheckBox checkDisease, checkHeart,checkMental, checkCancer, checkBp, checkStroke, checkDiabetes,
			checkKidney, checkTubr, checkEpilespy, checksickle, checkBleeming;

	EditText etDisease,etHeart, etMental, etCancer, etBp, etStroke, etDiabetes,
			etKidney, etTubr, etEpilespy, etsickle, etBleeming;

	//final layout
	RadioGroup rgMedications,rgAllergies;
	EditText etMedicationsList,etAllergiesList,etHospSergryList,etMedicalHistoryOther;
	Button btnUpdateMedical;
	RadioButton radioMedicationsYes,radioMedicationsNo,radioAllergiesYes,radioAllergiesNo;

	FrameLayout contMedications;
	RelativeLayout contAllergies,contHosptalization;
	TextView tvViewMed,tvViewAllergy,tvViewHosp;
	LinearLayout layNoNetwork;

	
	/*String pastHistryArr[] = {"Anemia","Heart Disease","Rheumetic Fever","Mitral Valve Prolapse Thyroid","Cancer","Gall Blader Disease","Blood Transfusion",
			"Pelvic Infection","Bladder Infections","Genital Herpes","Seizurs","Migrains","Liver Disease","Depression/Anxiety",
			"Drug or Alcohol Problem","Gonorrhea/Syphillis/Chlamydia","Asthma","Pneumonia","Diabetes","Sickle Cell Trait","Blood clot in legs/lungs",
			"Osteopenia",};*/

	ViewFlipper viewFlipper;


	//tabs
	TextView tvPast,tvSocial,tvRelatives,tvMedications;


	//New Smoke Section code
	RadioButton radioSmokeCurrentSmoker,radioSmokeFormerSmoker,radioSmokeNonSmoker;
	LinearLayout laySmokeDetail,laySmokeCurrentSmoker,laySmokeFormerSmoker;
	Spinner spSmokeType,spSmokeHowMuchPerDay,spSmokeReadyToQuit;
	EditText etSmokeWhatAge,etSmokeHowLongDidUsmoke,etSmokeQuitDate;
	TextView tvSmokeTypeLbl,tvSmokeAgeLbl,tvSmokeHowMuchPerDayLbl,tvSmokeReadyToQuitLbl,tvSmokeHowLongDidULbl,tvSmokeQuitDateLbl;//labels

	//Current Smoker=0 , Former Smoker=1, Non Smoker=2

	ArrayList<String> smokeTypeList;
	ArrayList<String> smokeHowMuchPerDayList;
	ArrayList<String> smokeReadyToQuitList;
	//New Smoke Section code

	@Override
	protected void onResume() {
		/*if (radioMedicalSmokeYes.isChecked()) {
			DATA.isSmoke = 1;
		} else {
			DATA.isSmoke = 0;
		}*/
		if (radioMedicalAlcoholYes.isChecked()) {
			DATA.isDrunk = 1;
		} else {
			DATA.isDrunk = 0;
		}
		if (radioStreetDrugYes.isChecked()) {
			DATA.isDrug=1;
		} else {
			DATA.isDrug=0;
		}
		if (radioAllergiesYes.isChecked()) {
			DATA.isAlergies=1;
		} else {
			DATA.isAlergies=0;
		}
		if (radioMedicationsYes.isChecked()) {
			DATA.isMedication = 1;
		} else {
			DATA.isMedication = 0;
		}
		if (etHospSergryList.getText().toString().isEmpty()) {
			DATA.isHospitalized = 0;
		} else {
			DATA.isHospitalized = 1;
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medical_history_new);

		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);

		btnFlipNext = (Button) findViewById(R.id.btnFlipNext);
		btnFlipPrev = (Button) findViewById(R.id.btnFlipPrev);


		s = new StringBuilder(200);

		//past history====================================================		
//		String pastHistryArr[] = {"Anemia","Heart Disease","Rheumetic Fever","Mitral Valve Prolapse Thyroid","Cancer","Gall Blader Disease","Blood Transfusion",
//				"Pelvic Infection","Bladder Infections","Genital Herpes","Seizurs","Migrains","Liver Disease","Depression/Anxiety",
//				"Drug or Alcohol Problem","Gonorrhea/Syphillis/Chlamydia","Asthma","Pneumonia","Diabetes","Sickle Cell Trait","Blood clot in legs/lungs",
//				"Osteopenia",};

		/*DATA.pastHistoryBeans = new ArrayList<PastHistoryBean>();
	    DATA.pastHistoryBeans.add(new PastHistoryBean(0, "Anemia", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(1, "Heart Disease", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(2, "Rheumetic Fever", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(3, "Mitral Valve Prolapse Thyroid", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(4, "Cancer", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(5, "Gall Blader Disease", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(6, "Blood Transfusion", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(7, "Pelvic Infection", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(8, "Bladder Infections", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(9, "Genital Herpes", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(10, "Seizurs", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(11, "Migrains", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(12, "Liver Disease", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(13, "Depression/Anxiety", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(14, "Drug or Alcohol Problem", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(15, "Gonorrhea/Syphillis/Chlamydia", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(16, "Asthma", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(17, "Pneumonia", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(18, "Diabetes", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(19, "Sickle Cell Trait", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(20, "Blood clot in legs/lungs", false));
	    DATA.pastHistoryBeans.add(new PastHistoryBean(21, "Osteopenia", false));*/


		lvMedicalHistory1 = (ListView) findViewById(R.id.lvMedicalHistory1);
		tvNoPastHistory = (TextView) findViewById(R.id.tvNoPastHistory);
		/*medicalHistoryAdapter = new MedicalHistoryAdapter(activity, pastHistryArr);
		lvMedicalHistory1.setAdapter(medicalHistoryAdapter);*/
//		DATA.allReports = new ArrayList<ReportsModel>();

//		ReportsModel temp;
//
//		for(int i = 0; i<pastHistryArr.length; i++) {
//
//			temp = new ReportsModel();			
//			temp.name = pastHistryArr[i];
//
//			DATA.allReports.add(temp);
//
//			temp = null;
//		}
//		selecReportsAdapter = new SelecReportsAdapter(activity);
//		lvMedicalHistory1.setAdapter(selecReportsAdapter);

		//past history ends====================================================

		//social history================================================
		rgMedicalAlcohol = (RadioGroup) findViewById(R.id.rgMedicalAlcohol);
		rgMedicalSmoke = (RadioGroup) findViewById(R.id.rgMedicalSmoke);
		rgStreetDrug = (RadioGroup) findViewById(R.id.rgStreetDrug);
		//etSmokeHowMuch = (EditText) findViewById(R.id.etSmokeHowMuch);
		//etSmokeHowLong = (EditText) findViewById(R.id.etSmokeHowLong);
		etAlcoholHowMuch = (EditText) findViewById(R.id.etAlcoholHowMuch);
		etAlcoholHowLong = (EditText) findViewById(R.id.etAlcoholHowLong);
		etStreetDrugList = (EditText) findViewById(R.id.etStreetDrugList);
		//radioMedicalSmokeYes=(RadioButton) findViewById(R.id.radioSmokeYes);
		//radioMedicalSmokeNo=(RadioButton) findViewById(R.id.radioSmokeNo);
		radioMedicalAlcoholYes=(RadioButton) findViewById(R.id.rdioAlcoholYes);
		radioMedicalAlcoholNo=(RadioButton) findViewById(R.id.radioAlcoholNo);
		radioStreetDrugYes=(RadioButton) findViewById(R.id.radioStreetYes);
		radioStreetDrugNo=(RadioButton) findViewById(R.id.radioStreetNo);
		//social history ends============================================================

		//Family History============================================================
		checkDisease = (CheckBox) findViewById(R.id.checkDisease);
		checkHeart = (CheckBox) findViewById(R.id.checkHeart);
		checkMental = (CheckBox) findViewById(R.id.checkMental);
		checkCancer = (CheckBox) findViewById(R.id.checkCancer);
		checkBp = (CheckBox) findViewById(R.id.checkBp);
		checkStroke = (CheckBox) findViewById(R.id.checkStroke);
		checkDiabetes = (CheckBox) findViewById(R.id.checkDiabetes);
		checkKidney = (CheckBox) findViewById(R.id.checkKidney);
		checkTubr = (CheckBox) findViewById(R.id.checkTubr);
		checkEpilespy = (CheckBox) findViewById(R.id.checkEpilespy);
		checksickle = (CheckBox) findViewById(R.id.checksickle);
		checkBleeming = (CheckBox) findViewById(R.id.checkBleeming);

		etDisease = (EditText) findViewById(R.id.etDisease);
		etHeart = (EditText) findViewById(R.id.etHeart);
		etMental = (EditText) findViewById(R.id.etMental);
		etCancer = (EditText) findViewById(R.id.etCancer);
		etBp = (EditText) findViewById(R.id.etBp);
		etStroke = (EditText) findViewById(R.id.etStroke);
		etDiabetes = (EditText) findViewById(R.id.etDiabetes);
		etKidney = (EditText) findViewById(R.id.etKidney);
		etTubr = (EditText) findViewById(R.id.etTubr);
		etEpilespy = (EditText) findViewById(R.id.etEpilespy);
		etsickle = (EditText) findViewById(R.id.etsickle);
		etBleeming = (EditText) findViewById(R.id.etBleeming);
		//Family History ends========================================================

		//Final History==============================================================
		rgMedications = (RadioGroup) findViewById(R.id.rgMedications);
		rgAllergies = (RadioGroup) findViewById(R.id.rgAllergies);
		etMedicationsList = (EditText) findViewById(R.id.etMedicationsList);
		etAllergiesList = (EditText) findViewById(R.id.etAllergiesList);
		etHospSergryList = (EditText) findViewById(R.id.etHospSergryList);
		btnUpdateMedical = (Button) findViewById(R.id.btnUpdateMedical);
		radioMedicationsYes=(RadioButton) findViewById(R.id.radioMedicationYes);
		radioMedicationsNo=(RadioButton) findViewById(R.id.radioMedicationNo);
		radioAllergiesYes=(RadioButton) findViewById(R.id.radioAllergiesYes);
		radioAllergiesNo=(RadioButton) findViewById(R.id.radioAllergiesNo);

		contMedications = findViewById(R.id.contMedications);
		contAllergies = findViewById(R.id.contAllergies);
		contHosptalization = findViewById(R.id.contHosptalization);
		tvViewMed = findViewById(R.id.tvViewMed);
		tvViewAllergy = findViewById(R.id.tvViewAllergy);
		tvViewHosp = findViewById(R.id.tvViewHosp);

		layNoNetwork = findViewById(R.id.layNoNetwork);

		//Final History ends=========================================================

		etPastOther = (EditText) findViewById(R.id.etPastOther);
		etSocialOther = (EditText) findViewById(R.id.etSocialOther);
		etMedicalHistoryOther = (EditText) findViewById(R.id.etMedicalHistoryOther);

		btnFlipNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				viewFlipper.showNext();

				int selectedChild = viewFlipper.getDisplayedChild();
				setupViewFiliperAndTabs(selectedChild);

				DATA.print("-- selectedChild btn next : "+selectedChild);

				if (selectedChild == 3) {
					btnFlipNext.setEnabled(false);
				} else {
					btnFlipNext.setEnabled(true);
				}

				if (selectedChild == 0) {
					btnFlipPrev.setEnabled(false);
				} else {
					btnFlipPrev.setEnabled(true);
				}

				setInd();
			}
		});
		btnFlipPrev.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				viewFlipper.showPrevious();

				int selectedChild = viewFlipper.getDisplayedChild();
				setupViewFiliperAndTabs(selectedChild);

				DATA.print("-- selectedChild btn previos : "+selectedChild);

				if (selectedChild == 3) {
					btnFlipNext.setEnabled(false);
				} else {
					btnFlipNext.setEnabled(true);
				}

				if (selectedChild == 0) {
					btnFlipPrev.setEnabled(false);
				} else {
					btnFlipPrev.setEnabled(true);
				}

				setInd();
			}
		});

		btnUpdateMedical.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});




		//New Smoke Section code
		tvSmokeTypeLbl = findViewById(R.id.tvSmokeTypeLbl);
		tvSmokeAgeLbl = findViewById(R.id.tvSmokeAgeLbl);
		tvSmokeHowMuchPerDayLbl = findViewById(R.id.tvSmokeHowMuchPerDayLbl);
		tvSmokeReadyToQuitLbl = findViewById(R.id.tvSmokeReadyToQuitLbl);
		tvSmokeHowLongDidULbl = findViewById(R.id.tvSmokeHowLongDidULbl);
		tvSmokeQuitDateLbl = findViewById(R.id.tvSmokeQuitDateLbl);

		radioSmokeCurrentSmoker = findViewById(R.id.radioSmokeCurrentSmoker);
		radioSmokeFormerSmoker  = findViewById(R.id.radioSmokeFormerSmoker);
		radioSmokeNonSmoker = findViewById(R.id.radioSmokeNonSmoker);

		laySmokeDetail  = findViewById(R.id.laySmokeDetail);
		laySmokeCurrentSmoker = findViewById(R.id.laySmokeCurrentSmoker);
		laySmokeFormerSmoker = findViewById(R.id.laySmokeFormerSmoker);

		spSmokeType = findViewById(R.id.spSmokeType);
		spSmokeHowMuchPerDay = findViewById(R.id.spSmokeHowMuchPerDay);
		spSmokeReadyToQuit = findViewById(R.id.spSmokeReadyToQuit);

		etSmokeWhatAge = findViewById(R.id.etSmokeWhatAge);
		etSmokeHowLongDidUsmoke = findViewById(R.id.etSmokeHowLongDidUsmoke);
		etSmokeQuitDate = findViewById(R.id.etSmokeQuitDate);

		/*etSmokeQuitDate.setOnClickListener(v -> {
			DialogFragment newFragment = new DatePickerFragment(etSmokeQuitDate);
			newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
		});*/

		String prefsDataStr = prefs.getString(PREF_APP_LBL_KEY, "");

		//empty check zarur

		if(! TextUtils.isEmpty(prefsDataStr)){
			try {
				JSONObject smokeLblJObj = new JSONObject(prefsDataStr);

				//tvSmokeTypeLbl.setText(smokeLblJObj.getString(""));
				tvSmokeAgeLbl.setText(smokeLblJObj.getString("start_age_lbl"));
				tvSmokeHowMuchPerDayLbl.setText(smokeLblJObj.getString("current_smoker_per_day_lbl"));
				tvSmokeReadyToQuitLbl.setText(smokeLblJObj.getString("ready_quit_lbl"));
				tvSmokeHowLongDidULbl.setText(smokeLblJObj.getString("how_long_smoke_lbl"));
				tvSmokeQuitDateLbl.setText(smokeLblJObj.getString("quit_date_lbl"));

				JSONArray current_smoker_types = smokeLblJObj.getJSONArray("current_smoker_types");

				Type listTypeStr = new TypeToken<ArrayList<String>>() {}.getType();
				smokeTypeList = gson.fromJson(current_smoker_types.toString(), listTypeStr);
				//ArrayAdapter<String> spSmokeTypeAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, smokeTypeList);
				spSmokeType.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, smokeTypeList));

				JSONArray current_smoker_per_day = smokeLblJObj.getJSONArray("current_smoker_per_day");
				smokeHowMuchPerDayList = gson.fromJson(current_smoker_per_day.toString(), listTypeStr);
				spSmokeHowMuchPerDay.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, smokeHowMuchPerDayList));

				JSONArray ready_quit_options = smokeLblJObj.getJSONArray("ready_quit_options");
				smokeReadyToQuitList = gson.fromJson(ready_quit_options.toString(), listTypeStr);
				spSmokeReadyToQuit.setAdapter(new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, smokeReadyToQuitList));



				spSmokeType.setEnabled(false);
				spSmokeHowMuchPerDay.setEnabled(false);
				spSmokeReadyToQuit.setEnabled(false);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}


		//New Smoke Section code



		rgMedicalSmoke.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				//	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

				switch (id) {
					case -1:
						DATA.print("--nochoice");
						break;

					case R.id.radioSmokeCurrentSmoker:
						laySmokeDetail.setVisibility(View.VISIBLE);
						laySmokeCurrentSmoker.setVisibility(View.VISIBLE);
						laySmokeFormerSmoker.setVisibility(View.GONE);

						DATA.isSmoke = 0;
						break;

					case R.id.radioSmokeFormerSmoker:
						laySmokeDetail.setVisibility(View.VISIBLE);
						laySmokeCurrentSmoker.setVisibility(View.GONE);
						laySmokeFormerSmoker.setVisibility(View.VISIBLE);

						DATA.isSmoke = 1;
						break;

					case R.id.radioSmokeNonSmoker:
						laySmokeDetail.setVisibility(View.GONE);
						//laySmokeCurrentSmoker.setVisibility(View.VISIBLE);
						//laySmokeFormerSmoker.setVisibility(View.GONE);

						DATA.isSmoke = 2;
						break;

					/*
					case R.id.radioSmokeYes:
						etSmokeHowLong.setVisibility(View.VISIBLE);
						etSmokeHowMuch.setVisibility(View.VISIBLE);
						DATA.isSmoke = 1;
						break;
					case R.id.radioSmokeNo:
						etSmokeHowLong.setVisibility(View.GONE);
						etSmokeHowMuch.setVisibility(View.GONE);
						DATA.isSmoke = 0;

						break;*/

					default:
						//Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();

						break;
				}

			}
		});
		rgMedicalAlcohol.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				//	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

				switch (id) {
					case -1:
						DATA.print("--nochoice");
						break;
					case R.id.rdioAlcoholYes:
						etAlcoholHowLong.setVisibility(View.VISIBLE);
						etAlcoholHowMuch.setVisibility(View.VISIBLE);
						DATA.isDrunk = 1;
						break;
					case R.id.radioAlcoholNo:
						etAlcoholHowLong.setVisibility(View.GONE);
						etAlcoholHowMuch.setVisibility(View.GONE);
						DATA.isDrunk = 0;

						break;

					default:
						Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();
						DATA.print("Huh?");
						break;
				}

			}
		});
		rgStreetDrug.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				//	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

				switch (id) {
					case -1:
						DATA.print("--nochoice");
						break;
					case R.id.radioStreetYes:
						etStreetDrugList.setVisibility(View.VISIBLE);
						DATA.isDrug = 1;

						break;
					case R.id.radioStreetNo:
						etStreetDrugList.setVisibility(View.GONE);
						DATA.isDrug = 0;
						break;

					default:
						Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();

						break;
				}

			}
		});

		rgMedications.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				//	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

				switch (id) {
					case -1:
						DATA.print("--nochoice");
						break;
					case R.id.radioMedicationYes:
						//etMedicationsList.setVisibility(View.VISIBLE);
						contMedications.setVisibility(View.VISIBLE);
						DATA.isMedication=1;

						break;
					case R.id.radioMedicationNo:
						contMedications.setVisibility(View.GONE);
						//etMedicationsList.setVisibility(View.GONE);
						DATA.isMedication=0;
						break;

					default:
						Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();

						break;
				}

			}
		});
		rgAllergies.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int id) {
				//	 int selectedId = rgMedicalAlcohol.getCheckedRadioButtonId();

				switch (id) {
					case -1:
						DATA.print("--nochoice");
						break;
					case R.id.radioAllergiesYes:
						contAllergies.setVisibility(View.VISIBLE);
						//etAllergiesList.setVisibility(View.VISIBLE);
						DATA.isAlergies = 1;

						break;
					case R.id.radioAllergiesNo:
						contAllergies.setVisibility(View.GONE);
						//etAllergiesList.setVisibility(View.GONE);
						DATA.isAlergies = 0;
						break;

					default:
						Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();
						break;
				}

			}
		});


		//Tabs
		tvPast = (TextView) findViewById(R.id.tvPast);
		tvSocial = (TextView) findViewById(R.id.tvSocial);
		tvRelatives = (TextView) findViewById(R.id.tvRelatives);
		tvMedications = (TextView) findViewById(R.id.tvMedications);

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){

					case R.id.tvPast:
						setupViewFiliperAndTabs(0);
						break;
					case R.id.tvSocial:
						setupViewFiliperAndTabs(1);
						break;
					case R.id.tvRelatives:
						setupViewFiliperAndTabs(2);
						break;
					case R.id.tvMedications:
						setupViewFiliperAndTabs(3);
						break;


					case R.id.tvViewMed:
						showMediDialog(1);
						break;
					case R.id.tvViewAllergy:
						showMediDialog(2);
						break;
					case R.id.tvViewHosp:
						showMediDialog(3);
						break;

					case R.id.layNoNetwork:
						loadMedicalHitory();
						break;
					default:
						break;
				}

				setInd();
				int selectedChild = viewFlipper.getDisplayedChild();
				DATA.print("-- selectedChild tab click : "+selectedChild);
				if (selectedChild == 3) {
					btnFlipNext.setEnabled(false);
				} else {
					btnFlipNext.setEnabled(true);
				}

				if (selectedChild == 0) {
					btnFlipPrev.setEnabled(false);
				} else {
					btnFlipPrev.setEnabled(true);
				}
			}
		};

		tvPast.setOnClickListener(onClickListener);
		tvSocial.setOnClickListener(onClickListener);
		tvRelatives.setOnClickListener(onClickListener);
		tvMedications.setOnClickListener(onClickListener);

		tvViewMed.setOnClickListener(onClickListener);
		tvViewAllergy.setOnClickListener(onClickListener);
		tvViewHosp.setOnClickListener(onClickListener);

		layNoNetwork.setOnClickListener(onClickListener);


		loadMedicalHitory();
	}//end oncreate

	private void loadMedicalHitory(){
		if (checkInternetConnection.isConnectedToInternet()) {

			getMedicalHistory(DATA.selectedUserCallId);//prefs.getString("id", "0")

			layNoNetwork.setVisibility(View.GONE);
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
			layNoNetwork.setVisibility(View.VISIBLE);
		}
	}


	void setupViewFiliperAndTabs(int pos){
		switch (pos){
			case 0:
				tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(0);
				break;
			case 1:
				tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvObjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(1);
				break;
			case 2:
				tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(2);
				break;
			case 3:
				tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvPlan.setTextColor(Color.parseColor("#FFFFFF"));

				viewFlipper.setDisplayedChild(3);
				break;
			default:

				break;
		}
	}

	public void setInd(){
		ImageView cir1 = (ImageView) findViewById(R.id.cir1);
		ImageView cir2 = (ImageView) findViewById(R.id.cir2);
		ImageView cir3 = (ImageView) findViewById(R.id.cir3);
		ImageView cir4 = (ImageView) findViewById(R.id.cir4);
		int pos = viewFlipper.getDisplayedChild();

		switch (pos){
			case 0:
				cir1.setImageResource(R.drawable.indicator_blavk_unselected);
				cir2.setImageResource(R.drawable.indicator_black_unselected);
				cir3.setImageResource(R.drawable.indicator_black_unselected);
				cir4.setImageResource(R.drawable.indicator_black_unselected);
				break;
			case 1:
				cir1.setImageResource(R.drawable.indicator_black_unselected);
				cir2.setImageResource(R.drawable.indicator_blavk_unselected);
				cir3.setImageResource(R.drawable.indicator_black_unselected);
				cir4.setImageResource(R.drawable.indicator_black_unselected);
				break;
			case 2:
				cir1.setImageResource(R.drawable.indicator_black_unselected);
				cir2.setImageResource(R.drawable.indicator_black_unselected);
				cir3.setImageResource(R.drawable.indicator_blavk_unselected);
				cir4.setImageResource(R.drawable.indicator_black_unselected);
				break;
			case 3:
				cir1.setImageResource(R.drawable.indicator_black_unselected);
				cir2.setImageResource(R.drawable.indicator_black_unselected);
				cir3.setImageResource(R.drawable.indicator_black_unselected);
				cir4.setImageResource(R.drawable.indicator_blavk_unselected);
				break;
		}
	}

	ArrayList<String> medicationList = new ArrayList<>();
	ArrayList<String> allergiesList = new ArrayList<>();
	ArrayList<String> hosptalizationList = new ArrayList<>();

	public void getMedicalHistory(String patient_id) {
		ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_HISTORY+patient_id,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);

		if(apiName.contains(ApiManager.GET_MEDICAL_HISTORY)){

            /*{
				    "success": 1,
				    "message": "Success",
				    "data": {
				        "id": "1",
				        "patient_id": "17",
				        "medical_history": "3,4,",
				        "is_smoke": "1",
				        "smoke_detail": "since 3 days/4",
				        "is_drink": "1",
				        "drink_detail": "3 months/3pags",
				        "is_drug": "1",
				        "drug_detail": "no drugs",
				        "relation_had": "Inherited Disease/Heart Trouble/Mental Illness",
				        "relation_had_name": "no one/abc/denial",
				        "is_medication": "1",
				        "medication_detail": "this is a list ofbmedications",
				        "is_alergies": "1",
				        "alergies_detail": "no akergies",
				        "is_hospitalize": "1",
				        "hospitalize_detail": "hospitalization list is there",
				        "dateof": "2015-09-30"
				    }
				}*/

			try {
				JSONObject jsonObject = new JSONObject(content);

				//==============================================================================
				JSONArray diseases = jsonObject.getJSONArray("diseases");
				DATA.pastHistoryBeans = new ArrayList<PastHistoryBean>();
				for (int i = 0; i < diseases.length(); i++) {

					/*String dName = "";
					if(diseases.getJSONObject(i).getString("name").isEmpty()){
						dName = diseases.getJSONObject(i).getString("dname");
					}else{
						dName = diseases.getJSONObject(i).getString("name")+" - "+
								diseases.getJSONObject(i).getString("dname");
					}*/
					String dName = diseases.getJSONObject(i).getString("dname");
					DATA.pastHistoryBeans.add(new PastHistoryBean(Integer.parseInt(diseases.getJSONObject(i).getString("id")), dName, false));
				}
				//adapter = new PastHistoryAdapter(activity, DATA.pastHistoryBeans);
				//lvMedicalHistory1.setAdapter(adapter);
				//==============================================================================

				int success = jsonObject.getInt("success");
				String message = jsonObject.getString("message");
				JSONObject data = jsonObject.getJSONObject("data");

				if(! data.has("id")){
					tvNoPastHistory.setVisibility(View.VISIBLE);
					return;
				}

				String id = data.getString("id");
				String patient_id = data.getString("patient_id");
				String medical_history = data.getString("medical_history");
				String is_smoke = data.getString("is_smoke");
				String smoke_detail = data.getString("smoke_detail");
				String is_drink = data.getString("is_drink");
				String drink_detail = data.getString("drink_detail");
				String is_drug = data.getString("is_drug");
				String drug_detail = data.getString("drug_detail");
				String relation_had = data.getString("relation_had");
				String relation_had_name = data.getString("relation_had_name");
				String is_medication = data.getString("is_medication");
				String medication_detail = data.getString("medication_detail");
				String is_alergies = data.getString("is_alergies");
				String alergies_detail = data.getString("alergies_detail");
				String is_hospitalize = data.getString("is_hospitalize");
				String hospitalize_detail = data.getString("hospitalize_detail");
				String dateof = data.getString("dateof");

				String other = data.getString("other");
				String social_other = data.getString("social_other");
				String medical_history_other = data.getString("medical_history_other");

				etMedicalHistoryOther.setText(other);
				etSocialOther.setText(social_other);
				etPastOther.setText(medical_history_other);

				/*if (is_smoke.equals("1")) {
					radioMedicalSmokeYes.setChecked(true);
					String arr[] = smoke_detail.split("/");
					String howLong = "",howMuch = "";
					if (arr.length > 0) {
						howLong = arr[0];
					}
					if (arr.length > 1) {
						howMuch = arr[1];
					}
					etSmokeHowLong.setText(howLong);
					etSmokeHowMuch.setText(howMuch);
				} else {
					radioMedicalSmokeNo.setChecked(true);
				}*/

				if(is_smoke.equalsIgnoreCase("0")){
					rgMedicalSmoke.check(R.id.radioSmokeCurrentSmoker);

					String arr[] = smoke_detail.split("\\|");
					String smokeType = "", smokeAge = "", smokeHowMuchPerDay = "", smokeReadyToQuit = "";
					try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}

					try {smokeAge = arr[1]; }catch (Exception e){e.printStackTrace();}

					try {smokeHowMuchPerDay = arr[2];}catch (Exception e){e.printStackTrace();}

					try {smokeReadyToQuit = arr[3];}catch (Exception e){e.printStackTrace();}


					etSmokeWhatAge.setText(smokeAge);

					try {
						for (int i = 0; i < smokeTypeList.size(); i++) {
							if(smokeType.equalsIgnoreCase(smokeTypeList.get(i))){
								spSmokeType.setSelection(i);
							}
						}
					}catch (Exception e){e.printStackTrace();}

					try {
						for (int i = 0; i < smokeHowMuchPerDayList.size(); i++) {
							if(smokeHowMuchPerDay.equalsIgnoreCase(smokeHowMuchPerDayList.get(i))){
								spSmokeHowMuchPerDay.setSelection(i);
							}
						}
					}catch (Exception e){e.printStackTrace();}

					try {
						for (int i = 0; i < smokeReadyToQuitList.size(); i++) {
							if(smokeReadyToQuit.equalsIgnoreCase(smokeReadyToQuitList.get(i))){
								spSmokeReadyToQuit.setSelection(i);
							}
						}
					}catch (Exception e){e.printStackTrace();}

				}else if(is_smoke.equalsIgnoreCase("1")){
					rgMedicalSmoke.check(R.id.radioSmokeFormerSmoker);


					String arr[] = smoke_detail.split("\\|");
					String smokeType = "", smokeHowLongDidU = "", smokeQuitDate = "";
					try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}

					try {smokeHowLongDidU = arr[1]; }catch (Exception e){e.printStackTrace();}

					try {smokeQuitDate = arr[2];}catch (Exception e){e.printStackTrace();}


					etSmokeHowLongDidUsmoke.setText(smokeHowLongDidU);
					etSmokeQuitDate.setText(smokeQuitDate);

					try {
						for (int i = 0; i < smokeTypeList.size(); i++) {
							if(smokeType.equalsIgnoreCase(smokeTypeList.get(i))){
								spSmokeType.setSelection(i);
							}
						}
					}catch (Exception e){e.printStackTrace();}



				}else if(is_smoke.equalsIgnoreCase("2")){
					rgMedicalSmoke.check(R.id.radioSmokeNonSmoker);
				}



				if (is_drink.equals("1")) {
					radioMedicalAlcoholYes.setChecked(true);
					String arr[] = drink_detail.split("/");

					String howLong = "",howMuch = "";
					if (arr.length > 0) {
						howLong = arr[0];
					}
					if (arr.length > 1) {
						howMuch = arr[1];
					}
					etAlcoholHowLong.setText(howLong);
					etAlcoholHowMuch.setText(howMuch);
				} else {
					radioMedicalAlcoholNo.setChecked(true);
				}
				if (is_drug.equals("1")) {
					radioStreetDrugYes.setChecked(true);
					etStreetDrugList.setText(drug_detail);
				} else {
					radioStreetDrugNo.setChecked(true);
				}
				if (is_alergies.equals("1")) {
					radioAllergiesYes.setChecked(true);
					etAllergiesList.setText(alergies_detail);

					allergiesList = new ArrayList<>();
					if(alergies_detail.isEmpty()){

					}else {
						String[] allergyArr = alergies_detail.split("\n");
						for (int i = 0; i < allergyArr.length; i++) {
							allergiesList.add(allergyArr[i]);
						}
					}
					tvViewAllergy.setText("View ("+allergiesList.size()+")");

				} else {
					radioAllergiesNo.setChecked(true);
				}
				if (is_medication.equals("1")) {
					radioMedicationsYes.setChecked(true);
					etMedicationsList.setText(medication_detail);

					medicationList = new ArrayList<>();
					if(medication_detail.isEmpty()){

					}else {
						String[] mediArr = medication_detail.split("\n");
						for (int i = 0; i < mediArr.length; i++) {
							medicationList.add(mediArr[i]);
						}
					}
					tvViewMed.setText("View ("+medicationList.size()+")");

				} else {
					radioMedicationsNo.setChecked(true);
				}
				if (is_hospitalize.equals("1")) {
					etHospSergryList.setText(hospitalize_detail);

					hosptalizationList = new ArrayList<>();
					if(hospitalize_detail.isEmpty()){

					}else {
						String[] hospArr = hospitalize_detail.split("\n");
						for (int i = 0; i < hospArr.length; i++) {
							hosptalizationList.add(hospArr[i]);
						}
					}
					tvViewHosp.setText("View ("+hosptalizationList.size()+")");

				} else {

				}
				//---------------------medicalHistory------------------------------
				if (medical_history.isEmpty()) {
					DATA.selectedMedicalHistoryPositions = new ArrayList<Integer>();
				} else {
					String[] medicalHistoryPositions = medical_history.split(",");
					DATA.selectedMedicalHistoryPositions = new ArrayList<Integer>();
					for (String string : medicalHistoryPositions) {
						int pos = Integer.parseInt(string);
						DATA.selectedMedicalHistoryPositions.add(pos);

						DATA.print("--position selected "+pos);
					}
				}
				//	medicalHistoryAdapter.notifyDataSetChanged();
					/*for (int i = 0; i < DATA.pastHistoryBeans.size(); i++) {
						if (DATA.selectedMedicalHistoryPositions.contains(DATA.pastHistoryBeans.get(i).getId())) {
							DATA.print("--in true bean id "+DATA.pastHistoryBeans.get(i).getId());
							DATA.pastHistoryBeans.get(i).setSelected(true);
						} else {
							DATA.print("--in false bean id "+DATA.pastHistoryBeans.get(i).getId());
							//DATA.pastHistoryBeans.get(i).setSelected(false);
							DATA.pastHistoryBeans.remove(DATA.pastHistoryBeans.get(i));
						}
					}*/


					/*for (Iterator<PastHistoryBean> it = DATA.pastHistoryBeans.iterator(); it.hasNext();) {
						if (! DATA.selectedMedicalHistoryPositions.contains(it.next().getId())) {
							//DATA.print("bean removed with id "+it.next().getId());
							it.next().setSelected(false);
							 it.remove();
						}else {

							 it.next().setSelected(true);
								DATA.print("bean added with id "+it.next().getId());
						}
						}*/

				for (int i = DATA.pastHistoryBeans.size()-1; i >= 0; i--){
					   /* if (data.get(i).getCaption().contains("_Hardi")){
					            data.remove(i);
					    }*/

					if (! DATA.selectedMedicalHistoryPositions.contains(DATA.pastHistoryBeans.get(i).getId())) {
						//DATA.print("bean removed with id "+it.next().getId());
						DATA.pastHistoryBeans.get(i).setSelected(false);
						DATA.pastHistoryBeans.remove(i);
					}else {

						DATA.pastHistoryBeans.get(i).setSelected(true);
						DATA.print("bean added with id "+DATA.pastHistoryBeans.get(i).getId());
					}
				}
				DATA.print("--after remove list size "+DATA.pastHistoryBeans.size());
					/*for (PastHistoryBean bean : DATA.pastHistoryBeans) {
						if (DATA.selectedMedicalHistoryPositions.contains(bean.getId())) {
							bean.setSelected(true);
						} else {
							bean.setSelected(false);
							DATA.pastHistoryBeans.remove(bean);
						}
					}//end loop
*/

				PastHistoryAdapter adapter = new PastHistoryAdapter(activity, DATA.pastHistoryBeans);
				lvMedicalHistory1.setAdapter(adapter);

				if (DATA.pastHistoryBeans.isEmpty()) {
					tvNoPastHistory.setVisibility(View.VISIBLE);
				} else {
					tvNoPastHistory.setVisibility(View.GONE);
				}
				/*medicalHistoryAdapter = new MedicalHistoryAdapter(activity, pastHistryArr);
				lvMedicalHistory1.setAdapter(medicalHistoryAdapter);*/
				//--------------------------medicalHistory-------------------------------------------


				//--------------------------relation_had-------------------------------------------
				if (relation_had.isEmpty()) {
					DATA.relativeHadBeans = new ArrayList<RelativeHadBean>();
				} else {
					DATA.relativeHadBeans = new ArrayList<RelativeHadBean>();
					String[] rh = relation_had.split("/");
					String[] rhNames = relation_had_name.split("/");

					for (int i = 0; i < rhNames.length; i++) {
						String relationHad = rh[i];
						String relationHadName = rhNames[i];
						DATA.relativeHadBeans.add(new RelativeHadBean(relationHad, relationHadName));
					}


					for (RelativeHadBean bean : DATA.relativeHadBeans) {
						if (bean.getRelation_had().equalsIgnoreCase("Inherited Disease")) {
							checkDisease.setChecked(true);
							etDisease.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Heart Trouble")) {
							checkHeart.setChecked(true);
							etHeart.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Mental Illness")) {
							checkMental.setChecked(true);
							etMental.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Cancer")) {
							checkCancer.setChecked(true);
							etCancer.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("High Blood Pressure")) {
							checkBp.setChecked(true);
							etBp.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Stroke")) {
							checkStroke.setChecked(true);
							etStroke.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Diabetes")) {
							checkDiabetes.setChecked(true);
							etDiabetes.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Kidney Problems")) {
							checkKidney.setChecked(true);
							etKidney.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Tuberculosis")) {
							checkTubr.setChecked(true);
							etTubr.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Epilespy")) {
							checkEpilespy.setChecked(true);
							etEpilespy.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}

						if (bean.getRelation_had().equalsIgnoreCase("Sickle Cell Disease")) {
							checksickle.setChecked(true);
							etsickle.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}
						if (bean.getRelation_had().equalsIgnoreCase("Bleeding Problems")) {
							checkBleeming.setChecked(true);
							etBleeming.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}
					}


				}

				//--------------------------relation_had-------------------------------------------

			} catch (JSONException e) {
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
				e.printStackTrace();
			}
		}
	}

	void showMediDialog(int flag){//1=medication, 2=allergies, 3=hospitalization
		Dialog medicationsDialog = new Dialog(activity, R.style.TransparentThemeH4B);
		medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		medicationsDialog.setContentView(R.layout.lay_view_med);
		ListView lvMedicationsDial = (ListView) medicationsDialog.findViewById(R.id.lvMedications);
		TextView tvNoData = medicationsDialog.findViewById(R.id.tvNoData);
		ImageView ivClose = medicationsDialog.findViewById(R.id.ivClose);

		TextView tvTittleD = medicationsDialog.findViewById(R.id.tvTittleD);
		ivClose.setOnClickListener(v -> medicationsDialog.dismiss());

		if(flag == 1){
			tvTittleD.setText("Medications");
			tvNoData.setText("No medication(s) added");

			if(medicationList!= null){
				lvMedicationsDial.setAdapter(new HistoryMediAdapter(activity,medicationList));
				int v = medicationList.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoData.setVisibility(v);
			}else {
				tvNoData.setVisibility(View.VISIBLE);
			}
		}else if(flag == 2){
			tvTittleD.setText("Allergies");
			tvNoData.setText("No allergies added");

			if(allergiesList != null){
				lvMedicationsDial.setAdapter(new HistoryMediAdapter(activity,allergiesList));
				//lvAllergiesDialog.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
				int v = allergiesList.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoData.setVisibility(v);
			}else {
				tvNoData.setVisibility(View.VISIBLE);
			}
		}else if(flag == 3){
			tvTittleD.setText("Hospitalizations");
			tvNoData.setText("No hospitalization(s) added");

			if(hosptalizationList != null){
				lvMedicationsDial.setAdapter(new HistoryMediAdapter(activity,hosptalizationList));
				//lvHosptDialog.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
				int v = hosptalizationList.isEmpty() ? View.VISIBLE : View.GONE;
				tvNoData.setVisibility(v);
			}else {
				tvNoData.setVisibility(View.VISIBLE);
			}
		}

		/*medicationsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
					lvMedications.setExpanded(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});*/
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(medicationsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		medicationsDialog.show();
		medicationsDialog.getWindow().setAttributes(lp);
	}

}
