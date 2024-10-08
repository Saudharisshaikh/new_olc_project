package com.app.emcurama;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.emcurama.api.ApiCallBack;
import com.app.emcurama.api.ApiManager;
import com.app.emcurama.model.NotesBean;
import com.app.emcurama.util.ActionEditText;
import com.app.emcurama.util.CheckInternetConnection;
import com.app.emcurama.util.CustomToast;
import com.app.emcurama.util.DATA;
import com.app.emcurama.util.GloabalMethods;
import com.app.emcurama.util.HideShowKeypad;
import com.app.emcurama.util.OpenActivity;
import com.app.emcurama.util.PrescriptionModule;
//import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

import static com.app.emcurama.ActivitySoapNotesNew.isDMEFormDone;
import static com.app.emcurama.ActivitySoapNotesNew.isHomecareFormDone;
import static com.app.emcurama.ActivitySoapNotesNew.isSkilledNursingFormDone;

import net.cachapa.expandablelayout.ExpandableLayout;

public class ActivitySoapNotesEditNew extends AppCompatActivity implements OnClickListener,ApiCallBack{

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	OpenActivity openActivity;
	CustomToast customToast;
	HideShowKeypad hideShowKeypad;
	SharedPreferences prefs;

	ViewFlipper vfSoapNotes;
	TextView tvSubjective,tvObjective,tvASSESMENT,tvPlan;

	TextView tvSOAPPatientName,tvSOAPDOB;
	EditText etSOAPChiefComplaints,etSOAPSymptoms,etSOAPSymptomExplanation,etSOAPCondition,etSOAPConditionInfo,
			etSOAPHistoryMedical,etSOAPHistorySocial,etSOAPHistoryFamily,etSOAPHistoryMedications,etSOAPHistoryAllergies;
	Spinner spSOAPLevelOfPain;
	EditText etSOAP_PainBodyPart;
	ImageView ivExpendExamLay;
	ExpandableLayout layExpandExam;
    EditText etSOAPExamHead,etSOAPExamHeent,etSOAPExamThroat,etSOAPExamHeart,etSOAPExamLungs,etSOAPExamChest,etSOAPExamExtremities,
            etSOAPExamNeurologic,etSOAPExamSkin,etSOAPExamGIGU,etSOAPExamOther;


	EditText etSOAPSummaryofProblem,etSOAPPlanDescription,
			etSOAPCarePlan;
	public static EditText etSOAPPrescription;////to write value from prescription module

	Button btnSoapConfirm,btnAddOTNotes,btnSkilledNursing,btnDMEReferral,btnHomeCareReferral;



	ActionEditText etOTDate,etOTTimeIn,etOTTimeout,etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBmi;

	Button btnPharmacy,btnPrescription;
	GloabalMethods gloabalMethods;
	BroadcastReceiver showSelectedPharmacyBroadcast;

	final String[] painSeverity = {"No Pain","Mild","Moderate","Severe","Very Severe","Worst pain possible"};
	String soapPainSeverity = "";


	public static NotesBean selectedNotesBean;

	/*@Override
	protected void onStart() {
		registerReceiver(showSelectedPharmacyBroadcast, new IntentFilter(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION));
		super.onStart();
	}

	@Override
	protected void onStop() {
		unregisterReceiver(showSelectedPharmacyBroadcast);
		super.onStop();
	}*/

	@Override
	protected void onResume() {
		etSOAPPlanDescription.setText(ActivityTelemedicineServicesEdit.tmsCodesWithNames);
		/*if (checkInternetConnection.isConnectedToInternet()) {
			getNoteByNoteID(selectedNotesBean.id);
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
	protected void onDestroy() {
		etSOAPPrescription = null;
		super.onDestroy();
	}


	public static final String IS_FOR_EDIT_NOTE_KEY = "isForEditNote";
	boolean isForEditNote = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_soap_notes_new);

		isForEditNote = getIntent().getBooleanExtra(IS_FOR_EDIT_NOTE_KEY, false);

		activity = ActivitySoapNotesEditNew.this;
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
		etSOAPSymptoms = (EditText) findViewById(R.id.etSOAPSymptoms);
		etSOAPSymptomExplanation = (EditText) findViewById(R.id.etSOAPSymptomExplanation);
		etSOAPCondition = (EditText) findViewById(R.id.etSOAPCondition);
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

		etSOAP_PainBodyPart = findViewById(R.id.etSOAP_PainBodyPart);

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
		layExpandExam = (ExpandableLayout) findViewById(R.id.layExpandExam);
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

		/*etOTDate.setText(new SimpleDateFormat("MM/dd/yyyy").format(new Date()));
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
		});*/

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

		btnPharmacy.setVisibility(View.GONE);
		//btnPrescription.setVisibility(View.GONE);

		btnPrescription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//new PrescriptionsPopup(ActivitySoapNotesEditNew.this).initDoubleVerficationDialog();
				new PrescriptionModule(ActivitySoapNotesEditNew.this).initDoubleVerficationDialog();
			}
		});

		gloabalMethods = new GloabalMethods(activity);

		/*if (checkInternetConnection.isConnectedToInternet()) {
			gloabalMethods.getPharmacy("",false);
		} else {
			customToast.showToast("Network not connected", 0, 0);
		}*/

		/*btnPharmacy.setOnClickListener(new OnClickListener() {

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
		});*/

		/*showSelectedPharmacyBroadcast = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				if (intent.getAction().equals(GloabalMethods.SHOW_PHARMACY_BROADCAST_ACTION)) {
					if (GloabalMethods.selectedPharmacyBean.id.isEmpty()) {
						btnPharmacy.setText("Select Pharmacy");
					}else {
						btnPharmacy.setText("Change Pharmacy");
					}
				}
			}
		};*/

		if(!selectedNotesBean.dme_referral.isEmpty()){
			btnDMEReferral.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnDMEReferral.setPadding(0,0,10,0);
		}
		if(!selectedNotesBean.skilled_nursing.isEmpty()){
			btnSkilledNursing.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnSkilledNursing.setPadding(0,0,10,0);
		}
		if(!selectedNotesBean.homecare_referral.isEmpty()){
			btnHomeCareReferral.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.ic_check_white_24dp), null);
			btnHomeCareReferral.setPadding(0,0,10,0);
		}

		btnSkilledNursing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivitySkilledNursingEdit.class,false);
			}
		});
		btnDMEReferral.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityDmeRefferalEdit.class,false);
			}
		});
		btnHomeCareReferral.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityHomeCareFormEdit.class,false);
			}
		});

		findViewById(R.id.btnEditServices).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityTelemedicineServicesEdit.class,false);
			}
		});


		if (checkInternetConnection.isConnectedToInternet()) {
			getNoteByNoteID(selectedNotesBean.id);
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



		findViewById(R.id.btnProgressNotes).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				openActivity.open(ActivityProgressNotesView.class,false);
			}
		});
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
				//openActivity.open(ActivityOTPT_Notes.class,false);
				break;
			default:
				break;
		}
	}


	public void saveDrNotes() {

		DATA.showLoaderDefault(activity, "");

		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();

		params.put("note_id", selectedNotesBean.id);
        params.put("notes[complain]", etSOAPChiefComplaints.getText().toString());
        params.put("notes[pain_where]", etSOAPSymptomExplanation.getText().toString());
        params.put("notes[pain_severity]", soapPainSeverity);
		params.put("notes[pain_related]", etSOAP_PainBodyPart.getText().toString());
        params.put("notes[subjective]", etSOAPConditionInfo.getText().toString());
        params.put("notes[symptom]", etSOAPSymptoms.getText().toString());
        params.put("notes[condition]",etSOAPCondition.getText().toString());

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
		params.put("treatment_codes", ActivityTelemedicineServicesEdit.tmsCodes);

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

		params.put("patient_id", DATA.selectedUserCallId);
        params.put("doctor_id", prefs.getString("id",""));

		/*if(!DATA.incomingCall){
			params.put("i_am_nurse", "1");
			params.put("calling_doctor_id", DATA.selectedDrId);
		}
		if(!PrescriptionsPopup.prescription_id.isEmpty()){
			params.put("prescription_id",PrescriptionsPopup.prescription_id);
		}*/

        Iterator it = ActivitySkilledNursingEdit.paramsMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            //DATA.print(pair.getKey() + " = " + pair.getValue());
            params.put(pair.getKey()+"",pair.getValue()+"");
            it.remove(); // avoids a ConcurrentModificationException
        }
        Iterator it1 = ActivityDmeRefferalEdit.paramsMap.entrySet().iterator();
        while (it1.hasNext()) {
            Map.Entry pair = (Map.Entry)it1.next();
            //DATA.print(pair.getKey() + " = " + pair.getValue());
            params.put(pair.getKey()+"",pair.getValue()+"");
            it1.remove(); // avoids a ConcurrentModificationException
        }

		Iterator it2 = ActivityHomeCareFormEdit.paramsMap.entrySet().iterator();
		while (it2.hasNext()) {
			Map.Entry pair = (Map.Entry)it2.next();
			//DATA.print(pair.getKey() + " = " + pair.getValue());
			params.put(pair.getKey()+"",pair.getValue()+"");
			it2.remove(); // avoids a ConcurrentModificationException
		}

		String editNoteURL = "";
		if(isForEditNote){
			editNoteURL = DATA.baseUrl+"doctor/updateNotes";
		}else{
			editNoteURL = DATA.baseUrl+"saveNotes";
		}

		final String reqURL = editNoteURL;

		DATA.print("--in saveNote URL "+editNoteURL);
		DATA.print("--in saveNote params "+params.toString());

		client.post(editNoteURL, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in saveDrNotes"+content);

					//--reaponce in saveDrNotes{"success":1,"message":"Saved."}
					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.has("success")) {
							Toast.makeText(activity, "Your note saved successfully", Toast.LENGTH_LONG).show();
							DATA.isSOAP_NotesSent = true;
							//PrescriptionsPopup.prescription_id = "";
							finish();
						} else {
							Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
						}
					} catch (JSONException e) {
						DATA.print("--saveNotes json exception");
						Toast.makeText(activity, DATA.JSON_ERROR_MSG, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}


				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
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
		return true;
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

			new AlertDialog.Builder(activity).setTitle("Confirm").
					setMessage("Are you sure? Do you want to continue or edit").
					setPositiveButton("Continue", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							saveDrNotes();

						}
					}).setNegativeButton("Edit",null).show();
		} else {
			customToast.showToast("No internet connection. Can not save notes. Please check internet connection and try again", 0, 1);
		}

	}

	@Override
	public void onBackPressed() {
		//startActivity(new Intent(activity,ActivityTelemedicineServices.class));
		super.onBackPressed();
	}


	//---------------------------------------------DOC TO DOC--------------------------------------------------------------------
	public void getNoteByNoteID(String noteId) {


		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);

		String reqURL = DATA.baseUrl+"getNoteByNoteID/"+noteId;

		DATA.print("-- Request : "+reqURL);
		//DATA.print("-- params : "+params.toString());

		client.post(reqURL, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				//customProgressDialog.dismissProgressDialog();
				try{
					String content = new String(response);

					DATA.print("--reaponce in getNoteDataByPatientID "+content);

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

					/*PrescriptionsPopup.dob = birthdate;
					PrescriptionsPopup.gender = data.getString("gender");
					PrescriptionsPopup.phone = data.getString("phone");
					PrescriptionsPopup.address = data.getString("residency");
					PrescriptionsPopup.pharmacyName = data.getString("StoreName");
					PrescriptionsPopup.pharmacyPhone = data.getString("PhonePrimary");*/


						tvSOAPPatientName.setText(first_name+" "+last_name);
						tvSOAPDOB.setText(birthdate);


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
					}*/

						if(data.has("ot_data")&& !data.getString("ot_data").isEmpty()){
							JSONObject ot_data = new JSONObject(data.getString("ot_data"));
							if(ot_data.has("ot_date")){
								String ot_date = ot_data.getString("ot_date");
								etOTDate.setText(ot_date);
							}
							if(ot_data.has("ot_timein")){
								String ot_timein = ot_data.getString("ot_timein");
								etOTTimeIn.setText(ot_timein);
							}
							if(ot_data.has("ot_timeout")){
								String ot_timeout = ot_data.getString("ot_timeout");
								etOTTimeout.setText(ot_timeout);
							}
							if(ot_data.has("ot_respirations")){
								String ot_respirations = ot_data.getString("ot_respirations");
								etOTRespirations.setText(ot_respirations);
							}
							if(ot_data.has("ot_blood_sugar")){
								String ot_blood_sugar = ot_data.getString("ot_blood_sugar");
								etOTBloodSugar.setText(ot_blood_sugar);
							}
							if(ot_data.has("ot_hr")){
								String ot_hr = ot_data.getString("ot_hr");
								etOTHR.setText(ot_hr);
							}
							if(ot_data.has("ot_bp")){
								String ot_bp = ot_data.getString("ot_bp");
								etOTBP.setText(ot_bp);
							}
							if(ot_data.has("ot_saturation")){
								String ot_saturation = ot_data.getString("ot_saturation");
								etOTO2Saturations.setText(ot_saturation);
							}

							if(ot_data.has("ot_temperature")){
								String ot_temperature = ot_data.getString("ot_temperature");
								etOTTemperature.setText(ot_temperature);
							}
							if(ot_data.has("ot_height")){
								String ot_height = ot_data.getString("ot_height");
								etOTHeight.setText(ot_height);
							}
							if(ot_data.has("ot_weight")){
								String ot_weight = ot_data.getString("ot_weight");
								etOTWeight.setText(ot_weight);
							}

							if(ot_data.has("ot_bmi")){
								String ot_bmi = ot_data.getString("ot_bmi");
								etOTBmi.setText(ot_bmi);
							}
						}

						//etSOAPPrescription.setText(treatment);

						if(!data.getString("notes").isEmpty()){
							JSONObject notes = new JSONObject(data.getString("notes"));

							if(notes.has("objective")){
								//etSOAPVitalSigns.setText(notes.getString("objective"));
							}
							if(notes.has("assesment")){
								etSOAPSummaryofProblem.setText(notes.getString("assesment"));
							}
							if(notes.has("prescription")){
								etSOAPPrescription.setText(notes.getString("prescription"));
							}else {
								etSOAPPrescription.setText(treatment);
							}

							if(notes.has("plan")){
								etSOAPPlanDescription.setText(notes.getString("plan"));
							}


							etSOAPChiefComplaints.setText(notes.getString("complain"));
							etSOAPSymptoms.setText(notes.getString("symptom"));
							etSOAPCondition.setText(notes.getString("condition"));
							etSOAPConditionInfo.setText(notes.getString("subjective"));
							etSOAPSymptomExplanation.setText(notes.getString("pain_where"));
							String pain_severity = notes.getString("pain_severity");
							for (int i = 0; i < painSeverity.length; i++) {
								if(pain_severity.equalsIgnoreCase(painSeverity[i])){
									spSOAPLevelOfPain.setSelection(i);
								}
							}
							etSOAPCarePlan.setText(notes.getString("care_plan"));

							if(notes.has("pain_related")){
								etSOAP_PainBodyPart.setText(notes.getString("pain_related"));
							}

						/*if(notes.has("complain")){
							etSOAPComplain.setText(notes.getString("complain"));
						}else{
							etSOAPComplain.setText("Symptoms: "+symptom_name+"\n\nConditions: "+condition_name);
						}*/
						}

						//-------------------------New Code------------------------------------
						//PrescriptionsPopup.prescription_id = data.getString("prescription_id");

						//SelectedDoctorsProfile.ptPriHomeCareDiag = data.getJSONObject("notes").getString("complain");//for homecare ref form
						//SelectedDoctorsProfile.ptRefDr = data.getString("dfirst_name")+" "+data.getString("dlast_name");

						//etSOAPChiefComplaints.setText(data.getString("symptom_details"));


						etSOAPHistoryMedical.setText(phistory);


						StringBuilder socialHistory = new StringBuilder();
						/*if (is_smoke.equalsIgnoreCase("1")) {
							socialHistory.append("Smoke:Yes, ");
							if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
								String[] smokeArr = smoke_detail.split("/");
								if (smokeArr.length < 2) {

								}else {
									socialHistory.append("How long: "+smokeArr[0]+", How much: "+smokeArr[1]);
								}
							}
						}*/
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
						} else{
							socialHistory.append("Smoke:No, ");
						}

						if (is_drink.equalsIgnoreCase("1")) {
							socialHistory.append("Drink alcohol:Yes, ");
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
							socialHistory.append("Use Drug:Yes, ");
							if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
								socialHistory.append("Detail: "+drug_detail);
							}
						}else{
							socialHistory.append("Use Drug:No");
						}
						socialHistory.append("Other: "+data.getString("social_other"));

						etSOAPHistorySocial.setText(socialHistory.toString());

						String relation_had = data.getString("relation_had");
						String relation_had_name = data.getString("relation_had_name");
						String[] rhArr = relation_had.split("/");
						String[] rhNameArr = relation_had_name.split("/");
						StringBuilder sbFamilyHistory = new StringBuilder();
						for (int i = 0; i < rhNameArr.length; i++) {
							sbFamilyHistory.append(rhArr[i]+" : "+rhNameArr[i]);
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
						String examination = data.getString("examination");
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
						}

					} catch (JSONException e) {
						//customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				//customProgressDialog.dismissProgressDialog();
				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content , statusCode);
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					//customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end getNoteData

	public void getPetientDetails(String patientId){
		ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+patientId,"get",null,this, activity);//DATA.selectedUserCallId
		ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		if(apiName.contains(ApiManager.PATIENT_DETAIL)){
			try {
				JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

				ActivityTcmDetails.ptPolicyNo = jsonObject.getJSONObject("patient_data").getString("policy_number");
				ActivityTcmDetails.ptDOB  = jsonObject.getJSONObject("patient_data").getString("birthdate");
				ActivityTcmDetails.ptAddress = jsonObject.getJSONObject("patient_data").getString("residency");
				ActivityTcmDetails.ptZipcode  = jsonObject.getJSONObject("patient_data").getString("zipcode");
				ActivityTcmDetails.ptPhone = jsonObject.getJSONObject("patient_data").getString("phone");
				//ptRefDate  = jsonObject.getJSONObject("patient_data").getString("phone");
				//ptRefDr  = jsonObject.getJSONObject("patient_data").getString("phone")+" "
				//+jsonObject.getJSONObject("patient_data").getString("phone");
				//ptPriHomeCareDiag  = jsonObject.getJSONObject("patient_data").getString("phone");

			} catch (JSONException e) {
				e.printStackTrace();
				//customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}

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
