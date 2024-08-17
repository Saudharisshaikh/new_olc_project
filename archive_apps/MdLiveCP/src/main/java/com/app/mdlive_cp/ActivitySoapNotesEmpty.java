package com.app.mdlive_cp;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.ConditionsModel;
import com.app.mdlive_cp.model.SymptomsModel;
import com.app.mdlive_cp.util.ActionEditText;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.Database;
import com.app.mdlive_cp.util.DatePickerFragment;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.HideShowKeypad;
import com.app.mdlive_cp.util.OpenActivity;
import com.app.mdlive_cp.util.PrescriptionsPopup;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.app.mdlive_cp.ActivitySoapNotesNew.isDMEFormDone;
import static com.app.mdlive_cp.ActivitySoapNotesNew.isHomecareFormDone;
import static com.app.mdlive_cp.ActivitySoapNotesNew.isSkilledNursingFormDone;

public class ActivitySoapNotesEmpty extends BaseActivity implements OnClickListener{

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	HideShowKeypad hideShowKeypad;
	SharedPreferences prefs;

	ViewFlipper vfSoapNotes;
	TextView tvSubjective,tvObjective,tvASSESMENT,tvPlan;

	TextView tvSOAPPatientName,tvSOAPDOB;
	EditText etSOAPChiefComplaints,etSOAPSymptomExplanation,etSOAPConditionInfo,
			etSOAPHistoryMedical,etSOAPHistorySocial,etSOAPHistoryFamily,etSOAPHistoryMedications,etSOAPHistoryAllergies;
	Spinner spSOAPLevelOfPain;
	ImageView ivExpendExamLay;
	ExpandableRelativeLayout layExpandExam;
    EditText etSOAPExamHead,etSOAPExamHeent,etSOAPExamThroat,etSOAPExamHeart,etSOAPExamLungs,etSOAPExamChest,etSOAPExamExtremities,
            etSOAPExamNeurologic,etSOAPExamSkin,etSOAPExamGIGU,etSOAPExamOther;


	public static EditText etSOAPPrescription;//write value from PrescriptionsPopup
	EditText etSOAPSummaryofProblem,etSOAPPlanDescription,
			etSOAPCarePlan;

	Button btnSoapConfirm,btnAddOTNotes,btnSkilledNursing,btnDMEReferral,btnHomeCareReferral;



	ActionEditText etOTDate,etOTTimeIn,etOTTimeout,etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBmi;

	Button btnPharmacy,btnPrescription;
	TextView tvSelectedPharmacy;
	GloabalMethods gloabalMethods;
	BroadcastReceiver showSelectedPharmacyBroadcast;

	final String[] painSeverity = {"No Pain","Mild","Moderate","Severe","Very Severe","Worst pain possible"};
	String soapPainSeverity = "";

	Database db;
	AutoCompleteTextView autoLiveTvSymptoms;
	ArrayAdapter<String> symptomsAdapter, conditionsAdapter;
	Spinner spLiveSelectCondtn;
	String selectedCondition = "";
	String [] sympArr;
	boolean isSymptomSelectd;
	String selectedSympId = "0",selectedConditionId = "0";

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
	protected void onDestroy() {
		etSOAPPrescription = null;
		super.onDestroy();
	}

	/*public static boolean isSkilledNursingFormDone = false;
	public static boolean isDMEFormDone = false;
	public static boolean isHomecareFormDone = false;*/
	@Override
	protected void onResume() {
		/*if (checkInternetConnection.isConnectedToInternet()) {
			getNoteDataByPatientID(DATA.selectedUserCallId,"livecare");
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}*/
		if(isSkilledNursingFormDone){
			isSkilledNursingFormDone = false;
			btnSkilledNursing.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnSkilledNursing.setPadding(0,0,10,0);
		}
		if(isDMEFormDone){
			isDMEFormDone = false;
			btnDMEReferral.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnDMEReferral.setPadding(0,0,10,0);
		}
		if(isHomecareFormDone){
			isHomecareFormDone = false;
			btnHomeCareReferral.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnHomeCareReferral.setPadding(0,0,10,0);
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soap_notes_empty);

		activity = ActivitySoapNotesEmpty.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		openActivity = new OpenActivity(activity);

		vfSoapNotes = (ViewFlipper) findViewById(R.id.vfSoapNotes);
		tvSubjective = (TextView) findViewById(R.id.tvSubjective);
		tvObjective = (TextView) findViewById(R.id.tvObjective);
		tvASSESMENT = (TextView) findViewById(R.id.tvASSESMENT);
		tvPlan = (TextView) findViewById(R.id.tvPlan);
		btnSoapConfirm = (Button) findViewById(R.id.btnSoapConfirm);
		btnAddOTNotes = (Button) findViewById(R.id.btnAddOTNotes);
		btnSkilledNursing = (Button) findViewById(R.id.btnSkilledNursing);
		btnDMEReferral = (Button) findViewById(R.id.btnDMEReferral);
		btnHomeCareReferral = (Button) findViewById(R.id.btnHomeCareReferral);

		if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
			btnAddOTNotes.setVisibility(View.VISIBLE);
		}else{
			btnAddOTNotes.setVisibility(View.GONE);
		}
		tvSubjective.setOnClickListener(this);
		tvObjective.setOnClickListener(this);
		tvASSESMENT.setOnClickListener(this);
		tvPlan.setOnClickListener(this);
		btnSoapConfirm.setOnClickListener(this);
		btnAddOTNotes.setOnClickListener(this);

		//section 1 Subjective
		tvSOAPPatientName = (TextView) findViewById(R.id.tvSOAPPatientName);
		tvSOAPDOB = (TextView) findViewById(R.id.tvSOAPDOB);

		etSOAPChiefComplaints = (EditText) findViewById(R.id.etSOAPChiefComplaints);
		etSOAPSymptomExplanation = (EditText) findViewById(R.id.etSOAPSymptomExplanation);
		etSOAPConditionInfo = (EditText) findViewById(R.id.etSOAPConditionInfo);
		etSOAPHistoryMedical = (EditText) findViewById(R.id.etSOAPHistoryMedical);
		etSOAPHistorySocial = (EditText) findViewById(R.id.etSOAPHistorySocial);
		etSOAPHistoryFamily = (EditText) findViewById(R.id.etSOAPHistoryFamily);
		etSOAPHistoryMedications = (EditText) findViewById(R.id.etSOAPHistoryMedications);
		etSOAPHistoryAllergies = (EditText) findViewById(R.id.etSOAPHistoryAllergies);



		etSOAPPrescription = (EditText) findViewById(R.id.etSOAPPrescription);
		etSOAPSummaryofProblem = (EditText) findViewById(R.id.etSOAPSummaryofProblem);

		etSOAPPlanDescription = (EditText) findViewById(R.id.etSOAPPlanDescription);
		etSOAPCarePlan  = (EditText) findViewById(R.id.etSOAPCarePlan);

		spSOAPLevelOfPain = (Spinner) findViewById(R.id.spSOAPLevelOfPain);

		etOTDate = findViewById(R.id.etOTDate);
		etOTTimeIn = findViewById(R.id.etOTTimeIn);
		etOTTimeout = findViewById(R.id.etOTTimeout);
		etOTBP = findViewById(R.id.etOTBP);
		etOTHR = findViewById(R.id.etOTHR);
		etOTRespirations = findViewById(R.id.etOTRespirations);
		etOTO2Saturations = findViewById(R.id.etOTO2Saturations);
		etOTBloodSugar = findViewById(R.id.etOTBloodSugar);
		etOTTemperature = findViewById(R.id.etOTTemperature);
		etOTHeight = findViewById(R.id.etOTHeight);
		etOTWeight = findViewById(R.id.etOTWeight);
		etOTBmi = findViewById(R.id.etOTBmi);

        //Examination
		ivExpendExamLay = (ImageView) findViewById(R.id.ivExpendExamLay);
		layExpandExam = (ExpandableRelativeLayout) findViewById(R.id.layExpandExam);
        etSOAPExamHead = (EditText) findViewById(R.id.etSOAPExamHead);
        etSOAPExamHeent = (EditText) findViewById(R.id.etSOAPExamHeent);
        etSOAPExamThroat = (EditText) findViewById(R.id.etSOAPExamThroat);
        etSOAPExamHeart = (EditText) findViewById(R.id.etSOAPExamHeart);
        etSOAPExamLungs = (EditText) findViewById(R.id.etSOAPExamLungs);
        etSOAPExamChest = (EditText) findViewById(R.id.etSOAPExamChest);
        etSOAPExamExtremities = (EditText) findViewById(R.id.etSOAPExamExtremities);
        etSOAPExamNeurologic = (EditText) findViewById(R.id.etSOAPExamNeurologic);
        etSOAPExamSkin = (EditText) findViewById(R.id.etSOAPExamSkin);
        etSOAPExamGIGU = (EditText) findViewById(R.id.etSOAPExamGIGU);
        etSOAPExamOther = (EditText) findViewById(R.id.etSOAPExamOther);

		etOTDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		etOTTimeIn.setText(new SimpleDateFormat("hh:mm a").format(new Date()));
		//etOTTimeout.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
		etOTDate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment newFragment = new DatePickerFragment(etOTDate);
				newFragment.show(getSupportFragmentManager(), "datePicker");
			}
		});
		etOTTimeIn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.setTimeByTimePicker(activity,etOTTimeIn);
			}
		});
		etOTTimeout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.setTimeByTimePicker(activity,etOTTimeout);
			}
		});

		layExpandExam.collapse();
		ivExpendExamLay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(layExpandExam.isExpanded()){
					layExpandExam.collapse();
					ivExpendExamLay.setImageResource(R.drawable.ic_add_box_black_24dp);
				}else {
					layExpandExam.expand();
					ivExpendExamLay.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
				}
			}
		});

		spSOAPLevelOfPain.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				soapPainSeverity = painSeverity[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, painSeverity);
		spSOAPLevelOfPain.setAdapter(adapter);


		btnPharmacy = (Button) findViewById(R.id.btnPharmacy);
		btnPrescription = (Button) findViewById(R.id.btnPrescription);
		tvSelectedPharmacy = (TextView) findViewById(R.id.tvSelectedPharmacy);

		btnPrescription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new PrescriptionsPopup(ActivitySoapNotesEmpty.this).initDoubleVerficationDialog();
			}
		});

		gloabalMethods = new GloabalMethods(activity);

		if (checkInternetConnection.isConnectedToInternet()) {
			gloabalMethods.getPharmacy("",false);
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
					System.out.println("-- pharmacyBeans list is null");
					if (checkInternetConnection.isConnectedToInternet()) {
						gloabalMethods.getPharmacy("",true);
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
					if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
						btnPharmacy.setText("Select Pharmacy");
						tvSelectedPharmacy.setText("No pharmacy selected by the patient. Please select a phormacy for the patient in order to send prescription to the patient.");
						btnPrescription.setEnabled(false);
					}else {
						btnPharmacy.setText("Change Pharmacy");
						tvSelectedPharmacy.setText(GloabalMethods.selectedPharmacyBean.StoreName);
						btnPrescription.setEnabled(true);
					}
				}
			}
		};

		btnSkilledNursing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivitySkilledNursing.class,false);
			}
		});
		btnDMEReferral.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityDmeRefferal.class,false);
			}
		});
		btnHomeCareReferral.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityHomeCareForm.class,false);
			}
		});

		findViewById(R.id.btnEditServices).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityTelemedicineServices.class,false);
			}
		});


		if (checkInternetConnection.isConnectedToInternet()) {
			getNoteDataByPatientID(DATA.selectedUserCallId,"livecare");
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
		}


		findViewById(R.id.btnEnvironmentalAssesment).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityEnvirForm.class,false);
			}
		});
		findViewById(R.id.btnFallRiskAssesment).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(FallRiskForm.class,false);
			}
		});
		findViewById(R.id.btnBradenScale).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityBradenScale.class,false);
			}
		});
		findViewById(R.id.btnDepression).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityDepressionForm.class,false);
			}
		});



		autoLiveTvSymptoms = findViewById(R.id.autoLiveTvSymptoms);
		spLiveSelectCondtn =(Spinner) findViewById(R.id.spLiveSelectCondtn);

		//symptoms from db....
		db = new Database(activity);
		DATA.allSymptoms = new ArrayList<SymptomsModel>();
		DATA.allSymptoms = db.getAllSymptoms();

		sympArr = new String[DATA.allSymptoms.size()];
		for(int i = 0; i<DATA.allSymptoms.size(); i++) {

			sympArr[i] = DATA.allSymptoms.get(i).symptomName;

		}
		symptomsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, sympArr);
		autoLiveTvSymptoms.setAdapter(symptomsAdapter);


		autoLiveTvSymptoms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectedSympId = "0";
				//GetLiveCare.this.symptomsAdapter.getFilter().filter("");
				autoLiveTvSymptoms.setText("");

				//autoLiveTvSymptoms.showDropDown();  also can be used
			}
		});

		String condAr[] = {"possible condition"};
		conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condAr);
		spLiveSelectCondtn.setAdapter(conditionsAdapter);


		autoLiveTvSymptoms.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
									long arg3) {

				isSymptomSelectd = true;
				String selectedSymptomName = (String) arg0.getItemAtPosition(position);
				System.out.println("--online care selected symptom name: "+selectedSymptomName);

				getSelectedSymptomId(selectedSymptomName);

				DATA.allConditions = new ArrayList<ConditionsModel>();
				DATA.allConditions = db.getAllConditions(selectedSympId);

				if(DATA.allConditions != null) {

					System.out.println("--online care DATA.allConditions.size on mainscreen: "+DATA.allConditions.size());

					String condArr[] = new String[DATA.allConditions.size()];
					for(int i = 0; i<DATA.allConditions.size(); i++) {

						condArr[i] = DATA.allConditions.get(i).conditionName;
					}

					conditionsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, condArr);
					spLiveSelectCondtn.setAdapter(conditionsAdapter);
				}

			}
		});


		spLiveSelectCondtn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(isSymptomSelectd) {
					selectedConditionId = DATA.allConditions.get(arg2).conditionId;
					selectedCondition = DATA.allConditions.get(arg2).conditionName;
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				selectedConditionId = "0";
			}
		});

	}


	public void getSelectedSymptomId(String selectedSymptomName) {

		for(int i = 0; i<DATA.allSymptoms.size();i++) {
			if(DATA.allSymptoms.get(i).symptomName.equals(selectedSymptomName)) {
				selectedSympId = DATA.allSymptoms.get(i).symptomId;
			}
		}
	}


	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
			case R.id.tvSubjective:
				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(0);
				break;
			case R.id.tvObjective:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvObjective.setTextColor(Color.parseColor("#FFFFFF"));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(1);
				break;
			case R.id.tvASSESMENT:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				vfSoapNotes.setDisplayedChild(2);
				break;
			case R.id.tvPlan:

				tvSubjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvObjective.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
				tvASSESMENT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
				tvPlan.setBackgroundColor(getResources().getColor(R.color.theme_red));
				//tvPlan.setTextColor(Color.parseColor("#FFFFFF"));

				vfSoapNotes.setDisplayedChild(3);
				break;
			case R.id.btnSoapConfirm:
				submitSOAP_Notes();
				break;
			case R.id.btnAddOTNotes:
				openActivity.open(ActivityOTPT_Notes.class,false);
				break;
			default:
				break;
		}
	}


	public void saveDrNotes(boolean isTemprorySave) {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();

		if (isTemprorySave){
			params.put("submit_type","save");
		}else{
			params.put("submit_type","confirm");
		}

        params.put("notes[complain]", etSOAPChiefComplaints.getText().toString());
        params.put("notes[pain_where]", etSOAPSymptomExplanation.getText().toString());
        params.put("notes[pain_severity]", soapPainSeverity);
        params.put("notes[subjective]", etSOAPConditionInfo.getText().toString());
        params.put("notes[symptom]", autoLiveTvSymptoms.getText().toString());
        params.put("notes[condition]",selectedCondition);

        String history = "Medical: "+etSOAPHistoryMedical.getText().toString()+
                "\nSocial: "+etSOAPHistorySocial.getText().toString()+
                "\nFamily: "+etSOAPHistoryFamily.getText().toString()+
                "\nMedications: "+etSOAPHistoryMedications.getText().toString()+
                "\nAllergies: "+etSOAPHistoryAllergies.getText().toString();
		params.put("notes[history]", history);


        params.put("examination[head]",etSOAPExamHead.getText().toString());
        params.put("examination[heent]",etSOAPExamHeent.getText().toString());
        params.put("examination[throat]",etSOAPExamThroat.getText().toString());
        params.put("examination[heart]",etSOAPExamHeart.getText().toString());
        params.put("examination[lungs]",etSOAPExamLungs.getText().toString());
        params.put("examination[chest]",etSOAPExamChest.getText().toString());
        params.put("examination[extremities]",etSOAPExamExtremities.getText().toString());
        params.put("examination[neurologic]",etSOAPExamNeurologic.getText().toString());
        params.put("examination[skin]",etSOAPExamSkin.getText().toString());
        params.put("examination[gi]",etSOAPExamGIGU.getText().toString());
        params.put("examination[other]",etSOAPExamOther.getText().toString());

        params.put("notes[prescription]", etSOAPPrescription.getText().toString());

		//params.put("notes[objective]", prescription);
		params.put("notes[family]", etSOAPHistoryFamily.getText().toString());
		params.put("notes[assesment]", etSOAPSummaryofProblem.getText().toString());
		params.put("notes[care_plan]", etSOAPCarePlan.getText().toString());
        params.put("notes[plan]", etSOAPPlanDescription.getText().toString());
		params.put("treatment_codes", ActivityTelemedicineServices.tmsCodes);

		params.put("ot_date" , etOTDate.getText().toString());
		params.put("ot_timein" , etOTTimeIn.getText().toString());
		String timeOut = etOTTimeout.getText().toString();
		if (timeOut.isEmpty()){
			timeOut = new SimpleDateFormat("hh:mm a").format(new Date());
		}
		params.put("ot_timeout" , timeOut);
		params.put("ot_bp" , etOTBP.getText().toString());
		params.put("ot_hr" , etOTHR.getText().toString());
		params.put("ot_respirations" , etOTRespirations.getText().toString());
		params.put("ot_saturation" , etOTO2Saturations.getText().toString());
		params.put("ot_blood_sugar" , etOTBloodSugar.getText().toString());

		params.put("ot_temperature" , etOTTemperature.getText().toString());
		params.put("ot_height" , etOTHeight.getText().toString());
		params.put("ot_weight" , etOTWeight.getText().toString());
		params.put("ot_bmi",etOTBmi.getText().toString());
		//params.put("ot_additional_vitals" , etOTAdditionalSigns.getText().toString());

		params.put("patient_id", DATA.selectedUserCallId);
        params.put("doctor_id", prefs.getString("id",""));

		if(!DATA.incomingCall){
			params.put("i_am_nurse", "1");
			params.put("calling_doctor_id", DATA.selectedDrId);
		}
		if(!PrescriptionsPopup.prescription_id.isEmpty()){
			params.put("prescription_id",PrescriptionsPopup.prescription_id);
		}

        Iterator it = ActivitySkilledNursing.paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            params.put(pair.getKey()+"",pair.getValue()+"");
            it.remove(); // avoids a ConcurrentModificationException
        }
        Iterator it1 = ActivityDmeRefferal.paramsMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            //System.out.println(pair.getKey() + " = " + pair.getValue());
            params.put(pair.getKey()+"",pair.getValue()+"");
            it1.remove(); // avoids a ConcurrentModificationException
        }

		Iterator it2 = ActivityHomeCareForm.paramsMap.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry pair = (Map.Entry)it2.next();
			//System.out.println(pair.getKey() + " = " + pair.getValue());
			params.put(pair.getKey()+"",pair.getValue()+"");
			it2.remove(); // avoids a ConcurrentModificationException
		}

		System.out.println("--in saveNote params "+params.toString());
		client.post(DATA.baseUrl+"saveNotes", params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--reaponce in saveDrNotes"+content);

					//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							customToast.showToast("Your note saved successfully",0,0);
							DATA.isSOAP_NotesSent = true;
							PrescriptionsPopup.prescription_id = "";
							finish();
						} else {
							customToast.showToast(DATA.CMN_ERR_MSG,0,0);
						}
					} catch (JSONException e) {
						System.out.println("--saveNotes json exception");
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: saveDrNotes, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("--onfail saveDrNotes" +content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end saveDrNotes



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_soap_notes, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save_schedule) {
			submitSOAP_Notes();
		}else if(item.getItemId() == android.R.id.home){
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	private void submitSOAP_Notes() {
		// TODO Auto-generated method stub

		if (checkInternetConnection.isConnectedToInternet()) {

			final Dialog dialogSupport = new Dialog(activity);
			dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogSupport.setContentView(R.layout.dialog_savesoap_opt);

			TextView tvEditSoap = (TextView) dialogSupport.findViewById(R.id.tvEditSoap);
			TextView tvSaveSoap = (TextView) dialogSupport.findViewById(R.id.tvSaveSoap);
			TextView tvConfirmSoap = (TextView) dialogSupport.findViewById(R.id.tvConfirmSoap);

			tvEditSoap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
				}
			});
			tvSaveSoap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
					saveDrNotes(true);
				}
			});

			tvConfirmSoap.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialogSupport.dismiss();
					saveDrNotes(false);
				}
			});
			dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			dialogSupport.show();

			//==================================================================================================
			/*new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm").
					setMessage("Are you sure? Do you want to continue or edit").
					setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveDrNotes(false);

						}
					}).
					setNegativeButton("Save", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveDrNotes(true);

						}
					}).setNeutralButton("Edit",null).show();*/
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
		}

	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(activity,ActivityTelemedicineServices.class));
		super.onBackPressed();
	}


	//---------------------------------------------DOC TO DOC--------------------------------------------------------------------
	public void getNoteDataByPatientID(String patientId,String type) {


		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);

		System.out.println("-- url in getNoteDataByPatientID: "+DATA.baseUrl+"getNoteDataByPatientID/"+patientId+"/"+type);


		client.post(DATA.baseUrl+"getNoteDataByPatientID/"+patientId+"/"+type, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					System.out.println("--reaponce in getNoteDataByPatientID "+content);

					try {
						JSONObject data = new JSONObject(content).getJSONObject("data");
						String first_name = data.getString("first_name");
						String last_name = data.getString("last_name");
						String birthdate = data.getString("birthdate");
						String symptom_name = data.getString("symptom_name");
						String condition_name = data.getString("condition_name");
						String medical_history = data.getString("medical_history");
						String is_smoke = data.getString("is_smoke");
						String smoke_detail = data.getString("smoke_detail");
						String is_drink = data.getString("is_drink");
						String drink_detail = data.getString("drink_detail");
						String is_drug = data.getString("is_drug");
						String drug_detail = data.getString("drug_detail");
						String is_alergies = data.getString("is_alergies");
						String alergies_detail = data.getString("alergies_detail");
						String treatment = data.getString("treatment");
						String description = data.getString("description");
						String phistory = data.getString("phistory");
						String diagnosis_other = data.getString("diagnosis_other");
						phistory = phistory+"\n"+diagnosis_other;
						String familyhistory = data.getString("familyhistory");

						PrescriptionsPopup.dob = birthdate;
						PrescriptionsPopup.gender = data.getString("gender");
						PrescriptionsPopup.phone = data.getString("phone");
						PrescriptionsPopup.address = data.getString("residency");
						PrescriptionsPopup.pharmacyName = data.getString("StoreName");
						PrescriptionsPopup.pharmacyPhone = data.getString("PhonePrimary");


						tvSOAPPatientName.setText(first_name+" "+last_name);
						tvSOAPDOB.setText(birthdate);

						etSOAPPlanDescription.setText(ActivityTelemedicineServices.tmsCodesWithNames);

					/*etSOAPVitalSigns.setText(vitals);
					etSOAPSummaryofProblem.setText(diagnosis);
					String medicationsWithInstructions = "";
					if (DATA.drugBeans != null) {
						for (int i = 0; i < DATA.drugBeans.size(); i++) {
							medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
							medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
						}
					}

					etSOAPPrescription.setText(medicationsWithInstructions);*/

					/*if(data.has("virtual_ot_data")&& !data.getString("virtual_ot_data").isEmpty()){
						JSONObject virtual_ot_data = new JSONObject(data.getString("virtual_ot_data"));
						if(virtual_ot_data.has("ot_respirations")){
							String ot_respirations = virtual_ot_data.getString("ot_respirations");
							etOTRespirations.setText(ot_respirations);
						}
						if(virtual_ot_data.has("ot_blood_sugar")){
							String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
							etOTBloodSugar.setText(ot_blood_sugar);
						}
						if(virtual_ot_data.has("ot_hr")){
							String ot_hr = virtual_ot_data.getString("ot_hr");
							etOTHR.setText(ot_hr);
						}
						if(virtual_ot_data.has("ot_bp")){
							String ot_bp = virtual_ot_data.getString("ot_bp");
							etOTBP.setText(ot_bp);
						}
						if(virtual_ot_data.has("ot_saturation")){
							String ot_saturation = virtual_ot_data.getString("ot_saturation");
							etOTO2Saturations.setText(ot_saturation);
						}

						if(virtual_ot_data.has("ot_temperature")){
							String ot_temperature = virtual_ot_data.getString("ot_temperature");
							etOTTemperature.setText(ot_temperature);
						}
						if(virtual_ot_data.has("ot_height")){
							String ot_height = virtual_ot_data.getString("ot_height");
							etOTHeight.setText(ot_height);
						}
						if(virtual_ot_data.has("ot_weight")){
							String ot_weight = virtual_ot_data.getString("ot_weight");
							etOTWeight.setText(ot_weight);
						}
						if(virtual_ot_data.has("ot_bmi")){
							String ot_bmi = virtual_ot_data.getString("ot_bmi");
							etOTBmi.setText(ot_bmi);
						}
					}*/

						//if(!data.getString("notes").isEmpty()){
						/*JSONObject notes = new JSONObject(data.getString("notes"));

						if(notes.has("objective")){
							//etSOAPVitalSigns.setText(notes.getString("objective"));
						}
						if(notes.has("assesment")){
							etSOAPSummaryofProblem.setText(notes.getString("assesment"));
						}*/
						/*if(notes.has("prescription")){
							etSOAPPrescription.setText(notes.getString("prescription"));
						}else {
							etSOAPPrescription.setText(treatment);
						}*/
						//etSOAPPrescription.setText(treatment);

						/*if(notes.has("complain")){
							etSOAPComplain.setText(notes.getString("complain"));
						}else{
							etSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
						}*/
						//}

					/*String additional_data = data.getString("additional_data");
					if(!additional_data.isEmpty()){
						JSONObject additional_dataJSON = new JSONObject(additional_data);
						String pain_severity = additional_dataJSON.getString("pain_severity");
						for (int i = 0; i < painSeverity.length; i++) {
							if(pain_severity.equalsIgnoreCase(painSeverity[i])){
								spSOAPLevelOfPain.setSelection(i);
							}
						}
						if(additional_dataJSON.has("pain_where")){
							etSOAPSymptomExplanation.setText(additional_dataJSON.getString("pain_where"));
						}
					}*/

						//-------------------------New Code------------------------------------
						PrescriptionsPopup.prescription_id = data.getString("prescription_id");

						ActivityTcmDetails.ptPriHomeCareDiag = data.getString("symptom_details");//for homecare ref form
						ActivityTcmDetails.ptRefDr = data.getString("dfirst_name")+" "+data.getString("dlast_name");

						//etSOAPChiefComplaints.setText(data.getString("symptom_details"));
						//autoLiveTvSymptoms.setText(data.getString("symptom_name"));
						//etSOAPCondition.setText(data.getString("condition_name"));
						//etSOAPConditionInfo.setText(data.getString("virtual_visit_description"));

						etSOAPHistoryMedical.setText(phistory);

						/*StringBuilder socialHistory = new StringBuilder();
						if (is_smoke.equalsIgnoreCase("1")) {
							socialHistory.append("Smoke:Yes, ");
							if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
								String[] smokeArr = smoke_detail.split("/");
								if (smokeArr.length < 2) {

								}else {
									socialHistory.append("How long: "+smokeArr[0]+", How much: "+smokeArr[1]);
								}
							}
						}else{
							socialHistory.append("Smoke:No, ");
						}*/


						//new smoke start
						StringBuilder socialHistory = new StringBuilder();

						if(is_smoke.equalsIgnoreCase("0")){
							String arr[] = smoke_detail.split("\\|");
							String smokeType = "-", smokeAge = "-", smokeHowMuchPerDay = "-", smokeReadyToQuit = "-";
							try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
							try {smokeAge = arr[1]; }catch (Exception e){e.printStackTrace();}
							try {smokeHowMuchPerDay = arr[2];}catch (Exception e){e.printStackTrace();}
							try {smokeReadyToQuit = arr[3];}catch (Exception e){e.printStackTrace();}

							socialHistory.append("Smoking Status : Current Smoker");
							socialHistory.append(", Type : "+smokeType);
							socialHistory.append(", What age did you start : "+smokeAge);
							socialHistory.append(", How much per day : "+smokeHowMuchPerDay);
							socialHistory.append(", Are you ready to quit : "+smokeReadyToQuit);

						}else if(is_smoke.equalsIgnoreCase("1")){
							String arr[] = smoke_detail.split("\\|");
							String smokeType = "-", smokeHowLongDidU = "-", smokeQuitDate = "-";
							try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
							try {smokeHowLongDidU = arr[1]; }catch (Exception e){e.printStackTrace();}
							try {smokeQuitDate = arr[2];}catch (Exception e){e.printStackTrace();}
							socialHistory.append("Smoking Status : Former Smoker");
							socialHistory.append(", Type : "+smokeType);
							socialHistory.append(", How long did you smoke : "+smokeHowLongDidU);
							socialHistory.append(", Quit date : "+smokeQuitDate);
						}else if(is_smoke.equalsIgnoreCase("2")){
							socialHistory.append("Smoking Status : Non Smoker");
						}else{
							socialHistory.append("Smoke:No, ");
						}
						//new smoke end

						if (is_drink.equalsIgnoreCase("1")) {
							socialHistory.append("\nDrink alcohol:Yes, ");
							if ((!drink_detail.isEmpty()) || (!drink_detail.equalsIgnoreCase("null"))) {
								String[] drinkArr = drink_detail.split("/");
								if (drinkArr.length < 2) {

								}else {
									socialHistory.append("How long: "+drinkArr[0]+", How much: "+drinkArr[1]);
								}
							}
						}else{
							socialHistory.append("Drink alcohol:No, ");
						}

						if (is_drug.equalsIgnoreCase("1")) {
							socialHistory.append("\nUse Drug:Yes, ");
							if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
								socialHistory.append("Detail: "+drug_detail);
							}
						}else{
							socialHistory.append("Use Drug:No");
						}
						socialHistory.append("\nOther: "+data.getString("social_other"));

						etSOAPHistorySocial.setText(socialHistory.toString());

						String relation_had = data.getString("relation_had");
						String relation_had_name = data.getString("relation_had_name");
						String[] rhArr = relation_had.split("/");
						String[] rhNameArr = relation_had_name.split("/");
						StringBuilder sbFamilyHistory = new StringBuilder();
						for (int i = 0; i < rhNameArr.length; i++) {
							sbFamilyHistory.append(rhArr[i]+" : "+rhNameArr[i]);
							if(i < (rhNameArr.length-1)){
								sbFamilyHistory.append("\n");
							}
						}
						etSOAPHistoryFamily.setText(sbFamilyHistory.toString());


						etSOAPHistoryMedications.setText("Medication: "+data.getString("medication_detail")
								+"\n"+"Other: "+data.getString("medical_history_other"));
						if(alergies_detail.isEmpty()){
							alergies_detail = "NKDA";
						}
						etSOAPHistoryAllergies.setText(alergies_detail);

					/*examination": "{\"\":\"ci gu\",\"\":\"heart\",\"\":\"throat\"
					,\"\":\"chest\",\"\":\"skin\",\"\":\"other\",\"\":\"neurologic\",\
					"\":\"extremities\",\"\":\"heent\",\"\":\"head\",\"\":\"lungs\"}",*/
					/*String examination = data.getString("examination");
					if(! examination.isEmpty()){
						JSONObject examinationJSON = new JSONObject(examination);
						if(examinationJSON.has("gi")){
							etSOAPExamGIGU.setText(examinationJSON.getString("gi"));
						}
						if(examinationJSON.has("heart")){
							etSOAPExamHeart.setText(examinationJSON.getString("heart"));
						}
						if(examinationJSON.has("throat")){
							etSOAPExamThroat.setText(examinationJSON.getString("throat"));
						}
						if(examinationJSON.has("chest")){
							etSOAPExamChest.setText(examinationJSON.getString("chest"));
						}
						if(examinationJSON.has("skin")){
							etSOAPExamSkin.setText(examinationJSON.getString("skin"));
						}
						if(examinationJSON.has("other")){
							etSOAPExamOther.setText(examinationJSON.getString("other"));
						}
						if(examinationJSON.has("neurologic")){
							etSOAPExamNeurologic.setText(examinationJSON.getString("neurologic"));
						}
						if(examinationJSON.has("extremities")){
							etSOAPExamExtremities.setText(examinationJSON.getString("extremities"));
						}
						if(examinationJSON.has("heent")){
							etSOAPExamHeent.setText(examinationJSON.getString("heent"));
						}
						if(examinationJSON.has("head")){
							etSOAPExamHead.setText(examinationJSON.getString("head"));
						}
						if(examinationJSON.has("lungs")){
							etSOAPExamLungs.setText(examinationJSON.getString("lungs"));
						}
					}*/

					} catch (JSONException e) {
						//customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: getNoteDataByPatientID, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);

					System.out.println("--onFailure in getNoteDataByPatientID "+content);
					//Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();

					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getNoteData

	/*public void clearComplain(View v){
		AlertDialog alertDialog = new AlertDialog.Builder(activity,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Confirm")
				.setMessage("Complain will be cleared. Are you sure ?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						etSOAPComplain.setText("");
					}
				}).setNegativeButton("No",null).create();

		alertDialog.show();
	}*/

	/*public void appendNewPrescriptionInSOAP(){
		String medicationsWithInstructions = "";
		if (DATA.drugBeans != null) {
			for (int i = 0; i < DATA.drugBeans.size(); i++) {
				medicationsWithInstructions = medicationsWithInstructions+(i+1)+": "+ DATA.drugBeans.get(i).getDrug_name()+"\n";
				medicationsWithInstructions = medicationsWithInstructions + "Instructions: "+ DATA.drugBeans.get(i).instructions+"\n\n";
			}
		}
		etSOAPPrescription.append(medicationsWithInstructions);
	}*/
}
