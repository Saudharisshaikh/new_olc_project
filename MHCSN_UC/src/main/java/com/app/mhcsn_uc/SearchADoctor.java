package com.app.mhcsn_uc;

import static com.app.mhcsn_uc.GetLiveCare.bodyParts;
import static com.app.mhcsn_uc.GetLiveCare.painSeverity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Html;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.mhcsn_uc.adapter.RvBodyPartAdapter;
import com.app.mhcsn_uc.adapter.RvPainAdapter;
import com.app.mhcsn_uc.adapter.VVReportImagesAdapter;
import com.app.mhcsn_uc.b_health2.GetLiveCareFormBhealth;
import com.app.mhcsn_uc.model.ConditionsModel;
import com.app.mhcsn_uc.model.SymptomsModel;
import com.app.mhcsn_uc.util.ActionSheetPopup;
import com.app.mhcsn_uc.util.CheckInternetConnection;
import com.app.mhcsn_uc.util.CustomToast;
import com.app.mhcsn_uc.util.DATA;
import com.app.mhcsn_uc.util.DatePickerFragment2;
import com.app.mhcsn_uc.util.ExpandableHeightGridView;
import com.app.mhcsn_uc.util.GloabalMethods;
import com.app.mhcsn_uc.util.HideShowKeypad;
import com.app.mhcsn_uc.util.OpenActivity;
import com.app.mhcsn_uc.util.RecyclerItemClickListener;
import com.darsh.multipleimageselect.helpers.Constants;
import com.darsh.multipleimageselect.models.Image;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchADoctor extends BaseActivity{

	Activity activity;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;
	CheckInternetConnection checkInternetConnection;
	HideShowKeypad hideShowKeypad;
	//Database db;

	//Spinner spSelectSymptm,spSelectCondtn;
	Button btnSbmtSymptom;
	EditText etExtraInfo,etZipCode,etApptmntDate;
	//SingleDateAndTimePicker dateTimePicer;// note : etApptmntDate is  hidden and not used
	//AutoCompleteTextView autoTvSymptoms,autoTvDrSpeciality;
	ImageView imgAdSearchadr;

	Spinner spSymptomNew;//,spConditionNew;

	//String sympArr[];
	private String selectedSympId = "",selectedConditionId = "";
	//ArrayAdapter<String> symptomsAdapter, conditionsAdapter, specialitiesAdapter;

	GloabalMethods gloabalMethods;
	BroadcastReceiver showSelectedPharmacyBroadcast;

	/*LinearLayout layPharmacyInfo;
	TextView tvNoPharmacyAdded;
	TextView tvPharmacyName,tvPharmacyStreetAddress,tvPharmacyCity,tvPharmacyState,tvPharmacyZipcode,tvPharmacyPhone,tvPharmacyCategories;
	Button btnSelectPharmacy;*/

	Button btnPharmacy;
	TextView tvSelPharmacy;


	EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight;
	ImageView ic_mike_LiveExtraInfo;
	ExpandableHeightGridView gvConditionNew;//gvSymptomNew
	RecyclerView rvPainSeverity;
	//public static final String[] painSeverity = {"No Pain","Mild","Moderate","Severe","Very Severe","Worst pain possible"};
	String selectedPainSeverity = "";
	//public static final int[] painSevEmojies = {R.drawable.ic_nopain,R.drawable.ic_mild,R.drawable.ic_moderate,R.drawable.ic_severe,R.drawable.ic_verysevere,R.drawable.ic_worstpainpossible};
	public Button btnSelectImages;
	ExpandableHeightGridView gvReportImages;

	RecyclerView rvBodyPart;


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
		setContentView(R.layout.search_a_doctor);

		activity = SearchADoctor.this;
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		hideShowKeypad = new HideShowKeypad(activity);


		btnSbmtSymptom = (Button) findViewById(R.id.btnSbmtSymptom);

		/*db = new Database(activity);
		autoTvSymptoms = (AutoCompleteTextView) findViewById(R.id.autoTvSymptoms);
		autoTvDrSpeciality = (AutoCompleteTextView) findViewById(R.id.autoTvDrSpeciality);
		spSelectCondtn =(Spinner) findViewById(R.id.spSelectCondtn);
		spSelectSymptm =(Spinner) findViewById(R.id.spSelectSymptm);*/

		etExtraInfo = (EditText) findViewById(R.id.etExtraInfo);
		etZipCode = (EditText) findViewById(R.id.etZipCode);
		etApptmntDate = (EditText) findViewById(R.id.etApptmntDate);


		etZipCode.setText(prefs.getString("zipcode", ""));

		etApptmntDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				DialogFragment newFragment = new DatePickerFragment2(etApptmntDate,activity);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});

		//--------------------------------------------SingleDateAndTimePicker--------------------------------------------------
		/*dateTimePicer = (SingleDateAndTimePicker) findViewById(R.id.dateTimePicer);
		dateTimePicer.setDisplayHours(true);
		dateTimePicer.setDisplayMinutes(true);
		//dateTimePicer.setDayFormatter(new SimpleDateFormat("dd MMM yyyy"));//MMMM
		dateTimePicer.setDisplayDays(true);
		dateTimePicer.setStepMinutes(15);
		dateTimePicer.setMustBeOnFuture(true);
		dateTimePicer.setSelectedTextColor(getResources().getColor(R.color.theme_red));
		dateTimePicer.setTodayText("Today");
		//dateTimePicer.setTextSize(20);

		Date todayDate = new Date();

		dateTimePicer.setMinDate(todayDate);
		dateTimePicer.setDefaultDate(todayDate);

		int noOfDays = 100; //i.e 100 days later
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(todayDate);
		calendar.add(Calendar.DAY_OF_YEAR, noOfDays);
		Date afterDate = calendar.getTime();

		dateTimePicer.setMaxDate(afterDate);*/
		//--------------------------------------------SingleDateAndTimePicker--------------------------------------------------

		//symptoms from db....
		/*DATA.allSymptoms = new ArrayList<SymptomsModel>();
		DATA.allSymptoms = db.getAllSymptoms();
				
		 sympArr = new String[DATA.allSymptoms.size()];
		for(int i = 0; i<DATA.allSymptoms.size(); i++) {
			
			sympArr[i] = DATA.allSymptoms.get(i).symptomName;
			
		}
		symptomsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, sympArr);
		autoTvSymptoms.setAdapter(symptomsAdapter);*/
		/*autoTvSymptoms.setThreshold(1);
		autoTvSymptoms.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				selectedSympId = "0";
				SearchADoctor.this.symptomsAdapter.getFilter().filter(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
			}
		});*/

		/*autoTvSymptoms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedSympId = "0";
				autoTvSymptoms.setText("");
				//autoTvSymptoms.showDropDown(); // also can be used
			}
		});
		//....................................................
		
		//specialities from db................................		
		DATA.allSpecialities = new ArrayList<SpecialityModel>();
		DATA.allSpecialities = db.getAllSpecialities();
		
		String specialityArr[] = new String[DATA.allSpecialities.size()];
		for(int i = 0; i<DATA.allSpecialities.size(); i++) {
			
			specialityArr[i] = DATA.allSpecialities.get(i).specialityName;
			
		}
		specialitiesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, specialityArr);
		autoTvDrSpeciality.setAdapter(specialitiesAdapter);
		autoTvDrSpeciality.setThreshold(1);*/

		//....................................................

//		spSelectSymptm.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				
//				DATA.allConditions = new ArrayList<ConditionsModel>();
//				DATA.allConditions = db.getAllConditions(DATA.allSymptoms.get(position).symptomId);	
//				
//				if(DATA.allConditions != null) {
//				
//					String condArr[] = new String[DATA.allConditions.size()];
//					for(int i = 0; i<DATA.allConditions.size(); i++) {
//						
//						condArr[i] = DATA.allConditions.get(i).conditionName;						
//					}
//					
//					conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condArr);
//					spSelectCondtn.setAdapter(conditionsAdapter);
//				}
//				
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
		
		/*String condAr[] = {"Possible condition"};
		conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condAr);
		spSelectCondtn.setAdapter(conditionsAdapter);

		
		autoTvSymptoms.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				String selectedSymptomName = (String) arg0.getItemAtPosition(position);
				DATA.print("--online care selected symptom name: "+selectedSymptomName);

				getSelectedSymptomId(selectedSymptomName);
				
				DATA.allConditions = new ArrayList<ConditionsModel>();
				DATA.allConditions = db.getAllConditions(selectedSympId);	
				
				if(DATA.allConditions != null) {
					
					DATA.print("--online care DATA.allConditions.size on mainscreen: "+DATA.allConditions.size());
				
					String condArr[] = new String[DATA.allConditions.size()];
					for(int i = 0; i<DATA.allConditions.size(); i++) {
						
						condArr[i] = DATA.allConditions.get(i).conditionName;						
					}
					
					conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condArr);
					spSelectCondtn.setAdapter(conditionsAdapter);
				}
				
			}
		});
		
		
		spSelectCondtn.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {

				if(DATA.allConditions != null) {
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("selectedCondId", DATA.allConditions.get(position).conditionId);
					ed.commit();
					
				}
				else {
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("selectedCondId", "0");
					ed.commit();
					
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				SharedPreferences.Editor ed = prefs.edit();
				ed.putString("selectedCondId", "0");
				ed.commit();
			}
		});
		
		
		
		autoTvDrSpeciality.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			
					
			}
		});*/

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);


		btnSbmtSymptom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideShowKeypad.hideSoftKeyboard();

				String extraInfo = etExtraInfo.getText().toString().trim();
				String zipcode = etZipCode.getText().toString().trim();
				String apptmntDate = etApptmntDate.getText().toString();


				SparseBooleanArray checked = gvConditionNew.getCheckedItemPositions();
				DATA.print("--checked: "+checked);
				DATA.print("--checked size: "+checked.size());
				int c = 0;

				selectedConditionId = "";

				for (int i = 0; i < checked.size(); i++) {
					// Item position in adapter
					int position1 = checked.keyAt(i);
					// Add sport if it is checked i.e.) == TRUE!
					if (checked.valueAt(i)){
						DATA.print("--pos checked "+position1);
						c++;

						selectedConditionId = selectedConditionId+((List<ConditionsModel>)gvConditionNew.getTag()).get(position1).conditionName+", ";
					}
				}
				if (c == 0) {
					selectedConditionId = "";
				}else {
					selectedConditionId = selectedConditionId.substring(0, selectedConditionId.length()-2);
				}

				if(selectedSympId.equalsIgnoreCase("907")){//Other
					selectedConditionId = "0";
				}


				if(zipcode.isEmpty()) {
					customToast.showToast("Please enter the zip code", 0, 0);
				}else if(apptmntDate.isEmpty()) {
					customToast.showToast("Please enter the date for the appointment", 0, 0);
				}else if(selectedConditionId.isEmpty()) {
					customToast.showToast("Please enter your symptoms and conditions", 0, 0);
				} else {


					//body part starts
					String bodyPart = "";
					DATA.print("-- RvBodyPartAdapter.selectedPositons: "+RvBodyPartAdapter.selectedPositons);
					if(!RvBodyPartAdapter.selectedPositons.isEmpty()){

						for (int i = 0; i < bodyParts.length; i++) {
							if(RvBodyPartAdapter.selectedPositons.contains(i)){
								bodyPart = bodyPart + bodyParts[i]+", ";
							}
						}
						try {
							bodyPart = bodyPart.substring(0, bodyPart.length()-2);
							DATA.print("-- bodyPart: "+bodyPart);
							//DATA.selectedPainBodyPart = bodyPart;//DATA values are for eLiveCare
						}catch (Exception e){e.printStackTrace();}
					}
					//body part ends

					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("apptmntDate",etApptmntDate.getText().toString());
					ed.putString("apptmntDateOriginal",etApptmntDate.getText().toString());

					ed.putString("selectedCondId", selectedConditionId);
					ed.putString("extraInfo",extraInfo);
					ed.putString("selectedPainSeverity", selectedPainSeverity);
					ed.putString("selectedPainBodyPart", bodyPart);

					ed.commit();

					DoctorsList.zipcodeForApptmnt = etZipCode.getText().toString();
					DoctorsList.apptmntDate = etApptmntDate.getText().toString();

					DATA.ot_bp = etOTBP.getText().toString();
					DATA.ot_hr = etOTHR.getText().toString();
					DATA.ot_respirations = etOTRespirations.getText().toString();
					DATA.ot_saturation = etOTO2Saturations.getText().toString();
					DATA.ot_blood_sugar = etOTBloodSugar.getText().toString();
					DATA.ot_temperature = etOTTemperature.getText().toString();
					DATA.ot_height = etOTHeight.getText().toString();
					DATA.ot_weight = etOTWeight.getText().toString();

					openActivity.open(DoctorsList.class, false);
				}
			}
		});

		imgAdSearchadr = (ImageView) findViewById(R.id.imgAdSearchadr);
		imgAdSearchadr.setOnClickListener(new OnClickListener() {

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
			gloabalMethods.getPharmacy("",false, "");
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}

		btnPharmacy.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (GloabalMethods.pharmacyBeans != null) {
					gloabalMethods.showPharmacyDialog();
				} else {
					DATA.print("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true, "");
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
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
						tvSelPharmacy.setText(Html.fromHtml("Selected Pharmacy: <b>"+GloabalMethods.selectedPharmacyBean.StoreName+"</b>"));
					}
				}
			}
		};




		//symtoms new
		List<SymptomsModel> symptomsModels = gloabalMethods.getAllSymptoms();
		spSymptomNew = findViewById(R.id.spSymptomNew);
		//spConditionNew = findViewById(R.id.spConditionNew);
		gvConditionNew = findViewById(R.id.gvConditionNew);

		/*spConditionNew.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
				String selectedConditionId = ((List<ConditionsModel>)spConditionNew.getTag()).get(pos).conditionId;
				prefs.edit().putString("selectedCondId", selectedConditionId).commit();
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});*/

		spSymptomNew.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

				selectedSympId = symptomsModels.get(i).symptomId;
				prefs.edit().putString("selectedSympId", selectedSympId).commit();

				/*List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;
				ArrayAdapter<ConditionsModel> spConditionAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, conditionsModels);
				spConditionNew.setAdapter(spConditionAdapter);
				spConditionNew.setTag(conditionsModels);//important*/

				List<ConditionsModel> conditionsModels = symptomsModels.get(i).conditionsModelList;
				ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, R.layout.listitem_singlechoice, android.R.id.text1, conditionsModels);
				//ArrayAdapter<ConditionsModel> conditionsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_single_choice, android.R.id.text1, conditionsModels);
				gvConditionNew.setAdapter(conditionsAdapter);
				gvConditionNew.setExpanded(true);
				gvConditionNew.setTag(conditionsModels);//important

				if(symptomsModels.get(i).symptomName.equalsIgnoreCase("other")){
					openKeyboardOnetExraInfo();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		ArrayAdapter<SymptomsModel> spSymptomsAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_dropdown_item_1line, symptomsModels);
		spSymptomNew.setAdapter(spSymptomsAdapter);



		//from GetLiveCare code
		etOTBP = findViewById(R.id.etOTBP);
		etOTHR = findViewById(R.id.etOTHR);
		etOTRespirations = findViewById(R.id.etOTRespirations);
		etOTO2Saturations = findViewById(R.id.etOTO2Saturations);
		etOTBloodSugar = findViewById(R.id.etOTBloodSugar);
		etOTTemperature = findViewById(R.id.etOTTemperature);
		etOTHeight = findViewById(R.id.etOTHeight);
		etOTWeight = findViewById(R.id.etOTWeight);
		ic_mike_LiveExtraInfo = findViewById(R.id.ic_mike_LiveExtraInfo);

		ic_mike_LiveExtraInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startVoiceRecognitionActivity(etExtraInfo);
			}
		});


		gvReportImages = findViewById(R.id.gvReportImages);
		btnSelectImages = findViewById(R.id.btnSelectImages);
		btnSelectImages.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new ActionSheetPopup(appCompatActivity).showActionSheet();
			}
		});
		vvReportImagesAdapter = new VVReportImagesAdapter(activity,new ArrayList<Image>());
		gvReportImages.setAdapter(vvReportImagesAdapter);
		gvReportImages.setExpanded(true);
		gvReportImages.setPadding(5,5,5,5);



		//pain severity emojies new
		rvPainSeverity = findViewById(R.id.rvPainSeverity);
		RvPainAdapter rvPainAdapter = new RvPainAdapter(activity);
		RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
		rvPainSeverity.setLayoutManager(mLayoutManager);
		rvPainSeverity.setItemAnimator(new DefaultItemAnimator());
		rvPainSeverity.setAdapter(rvPainAdapter);

		rvPainSeverity.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvPainSeverity, new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

				selectedPainSeverity = painSeverity[position];

				//spPainSeverity.setSelection(position);

				RvPainAdapter.selectedPos = position;

				rvPainAdapter.notifyDataSetChanged();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				DATA.print("-- item long press pos: "+position);
			}
		}));

		//===========BodyPart=========
		rvBodyPart = findViewById(R.id.rvBodyPart);
		RvBodyPartAdapter rvBodyPartAdapter = new RvBodyPartAdapter(activity);
		//RecyclerView.LayoutManager mLayoutManagerRVBP = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

		int numberOfColumns = 5;
		RecyclerView.LayoutManager mLayoutManagerRVBP = new GridLayoutManager(this, numberOfColumns);

		rvBodyPart.setLayoutManager(mLayoutManagerRVBP);

		//rvBodyPart.setHasFixedSize(true);//this line was for com.app.emcurauc.util.AutofitRecyclerView

		rvBodyPart.setItemAnimator(new DefaultItemAnimator());
		rvBodyPart.setAdapter(rvBodyPartAdapter);

		rvBodyPart.addOnItemTouchListener(new RecyclerItemClickListener(activity, rvBodyPart, new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {

				//selectedPainSeverity = painSeverity[position];

				if(RvBodyPartAdapter.selectedPositons.contains(position)){
					boolean isremoved = RvBodyPartAdapter.selectedPositons.remove(Integer.valueOf(position));
					DATA.print("-- removed pos: "+position+" isremoved: "+isremoved);
				}else {
					RvBodyPartAdapter.selectedPositons.add(position);
				}

				rvBodyPartAdapter.notifyDataSetChanged();

				if(bodyParts[position].equalsIgnoreCase("Other")){
					openKeyboardOnetExraInfo();
				}
			}

			@Override
			public void onItemLongClick(View view, int position) {
				DATA.print("-- item long press pos: "+position);
			}
		}));
		//=====================BodyPart RV ends================


		//============tab layout apptmnt========================================
		TextView tvBookAppt = findViewById(R.id.tvBookAppt);
		TextView tvMyAppt = findViewById(R.id.tvMyAppt);
		tvMyAppt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				openActivity.open(MyAppointments.class, true);
			}
		});




		LinearLayout tabOptions = findViewById(R.id.tabOptions);
		TextView tvUrgentCareVisits = findViewById(R.id.tvUrgentCareVisits);
		TextView tvCounsling = findViewById(R.id.tvCounsling);

		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.tvUrgentCareVisits:
						openActivity.open(SearchADoctor.class, true);
						break;
					case R.id.tvCounsling:
						openActivity.open(GetLiveCareFormBhealth.class, true);
						break;
				}
			}
		};
		tvUrgentCareVisits.setOnClickListener(onClickListener);
		tvCounsling.setOnClickListener(onClickListener);
	}


	private void openKeyboardOnetExraInfo(){
		etExtraInfo.setSelection(etExtraInfo.getText().length());

		etExtraInfo.requestFocus();
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(etExtraInfo, InputMethodManager.SHOW_IMPLICIT);
	}

	/*public void getSelectedSymptomId(String selectedSymptomName) {

			for(int i = 0; i<DATA.allSymptoms.size();i++) {
				if(DATA.allSymptoms.get(i).symptomName.equals(selectedSymptomName)) {					
					selectedSympId = DATA.allSymptoms.get(i).symptomId;
					
					SharedPreferences.Editor ed = prefs.edit();
					ed.putString("selectedSympId", selectedSympId);
					ed.commit();

				}
			}
	}*/


	static ArrayList<Image> images;
	VVReportImagesAdapter vvReportImagesAdapter;
	final int REQUEST_IMAGE_CAPTURE = 1;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == Constants.REQUEST_CODE && resultCode == RESULT_OK && data != null) {

			if(images == null){
				images = new ArrayList<>();
			}
			ArrayList<Image> imagesFromGal = data.getParcelableArrayListExtra(Constants.INTENT_EXTRA_IMAGES);
			images.addAll(imagesFromGal);

			vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
			gvReportImages.setAdapter(vvReportImagesAdapter);
			gvReportImages.setExpanded(true);
			gvReportImages.setPadding(5,5,5,5);
            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/
		}else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

			//String imgPath = ImageFilePath.getPath(activity, ActionSheetPopup.outputFileUriVV);//Android O issue, file provider,  GM
			String imgPath = ActionSheetPopup.capturedImagePath;
			if(images == null){
				images = new ArrayList<>();
			}
			images.add(new Image(0,"",imgPath,true));

			vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
			gvReportImages.setAdapter(vvReportImagesAdapter);
			gvReportImages.setExpanded(true);
			gvReportImages.setPadding(5,5,5,5);

            /*if(vvReportImagesAdapter != null){
                vvReportImagesAdapter.notifyDataSetChanged();
            }else {
                vvReportImagesAdapter = new VVReportImagesAdapter(activity,images);
                gvReportImages.setAdapter(vvReportImagesAdapter);
                gvReportImages.setExpanded(true);
            }*/

		}else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
			ArrayList<String> matches = data.getStringArrayListExtra(
					RecognizerIntent.EXTRA_RESULTS);
			// do whatever you want with the results
			if(this.editText != null){
				this.editText.setText(matches.get(0));
			}
		}
	}


	EditText editText;
	private static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
	private void startVoiceRecognitionActivity(EditText editText) {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		// identifying your application to the Google service
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
		// hint in the dialog
		intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.app_name));
		// hint to the recognizer about what the user is going to say
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		// number of results
		intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
		// recognition language
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
		startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		this.editText = editText;
	}

	AlertDialog dialog;
	public static boolean correttime;
	public void validateDate(String time){

		//Ahmer's code for validate user can not select past time of the day 05-01-2023
		System.out.println("-- DATA " + DATA.date+" "+time);
		String pattern = "MM/dd/yyyy hh:mm a";
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		Date callTime = null;//"2016-01-28 19:44:31"
		try {
			callTime = format.parse(DATA.date+" "+time);
			System.out.println("call date parsed : "+callTime);
			correttime = callTime.after(new Date());
			DATA.print("-- "+callTime.after(new Date()));

			if (correttime == false)
			{
				AlertDialog.Builder b = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle(getString(R.string.app_name))
						.setMessage("You can not select past time of the day please select current or future time Thanks")
						.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								dialog.dismiss();
							}
						});
				dialog = b.create();
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}


		} catch (ParseException e) {
			e.printStackTrace();
		}
		//Ahmer's code end

	}
}
