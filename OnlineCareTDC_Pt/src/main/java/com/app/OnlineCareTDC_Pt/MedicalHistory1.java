package com.app.OnlineCareTDC_Pt;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.OnlineCareTDC_Pt.adapter.HistoryAllergyAdapter;
import com.app.OnlineCareTDC_Pt.adapter.HistoryHosptAdapter;
import com.app.OnlineCareTDC_Pt.adapter.HistoryMediAdapter;
import com.app.OnlineCareTDC_Pt.adapter.IcdCodesAdapter;
import com.app.OnlineCareTDC_Pt.adapter.PastHistoryAdapter;
import com.app.OnlineCareTDC_Pt.api.ApiCallBack;
import com.app.OnlineCareTDC_Pt.api.ApiManager;
import com.app.OnlineCareTDC_Pt.model.DrugBean;
import com.app.OnlineCareTDC_Pt.model.IcdCodeBean;
import com.app.OnlineCareTDC_Pt.model.PastHistoryBean;
import com.app.OnlineCareTDC_Pt.model.RelativeHadBean;
import com.app.OnlineCareTDC_Pt.util.CheckInternetConnection;
import com.app.OnlineCareTDC_Pt.util.CustomToast;
import com.app.OnlineCareTDC_Pt.util.DATA;
import com.app.OnlineCareTDC_Pt.util.DatePickerFragment;
import com.app.OnlineCareTDC_Pt.util.ExpandableHeightListView;
import com.app.OnlineCareTDC_Pt.util.GloabalMethods;
import com.app.OnlineCareTDC_Pt.util.HideShowKeypad;
import com.app.OnlineCareTDC_Pt.util.OpenActivity;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static com.app.OnlineCareTDC_Pt.api.ApiManager.PREF_APP_LBL_KEY;

public class MedicalHistory1 extends BaseActivity{

	Activity activity;
	public HideShowKeypad hideShowKeypad;
	SharedPreferences prefs;
	Button btnFlipPrev,btnFlipNext;

	//past history layout...
	ListView lvMedicalHistory1;
	EditText etPastOther;
	//MedicalHistoryAdapter medicalHistoryAdapter;

	PastHistoryAdapter adapter;
	StringBuilder s;
	OpenActivity openActivity;
	ApiCallBack apiCallBack;

	//social history layout.,.
	RadioGroup rgMedicalSmoke, rgMedicalAlcohol, rgStreetDrug;
	EditText etAlcoholHowMuch, etAlcoholHowLong, etSocialOther;//etStreetDrugList , etSmokeHowMuch,etSmokeHowLong,

	RadioButton radioMedicalAlcoholYes,radioMedicalAlcoholNo,radioStreetDrugYes,radioStreetDrugNo;//radioMedicalSmokeYes,radioMedicalSmokeNo,

	LinearLayout layStreetDrugs;
	CheckBox cbLSD, cbMarijuana, cbCocaine, cbMethAmphetamines, cbHeroine ,cbCarfentanyl;
	CheckBox[] checkBoxesStreetDrugs;
	//family history layout
	CheckBox checkDisease, checkHeart,checkMental, checkCancer, checkBp, checkStroke, checkDiabetes,
			checkKidney, checkTubr, checkEpilespy, checksickle, checkBleeming,checkFamilyOther;

	EditText etDisease,etHeart, etMental, etCancer, etBp, etStroke, etDiabetes,
			etKidney, etTubr, etEpilespy, etsickle, etBleeming, etFamilyOther;

	//final layout
	RadioGroup rgMedications,rgAllergies;
	EditText etMedicalHistoryOther;
	Button btnUpdateMedical;
	RadioButton radioMedicationsYes,radioMedicationsNo,radioAllergiesYes,radioAllergiesNo;

	EditText etMedicationsAddMedication;
	ExpandableHeightListView lvMedications;
	ImageView ivAddMedication, ivSearchMedication;
	TextView tvAddMed;
	public TextView tvViewMed;
	ArrayList<String> medicationList;

	ExpandableHeightListView lvAllergies;
	ImageView ivAddAllergies;
	EditText etAddAllergies;
	TextView tvAddAllergy;
	public TextView tvViewAllergy;
	ArrayList<String> allergiesList;

	ExpandableHeightListView lvHosptalization;
	ImageView ivAddHosptalization;
	EditText etAddHosptalization;
	TextView tvAddHosp;
	public TextView tvViewHosp;
	ArrayList<String> hosptalizationList;

	LinearLayout layNoNetwork;

	//tabs
	TextView tvPast,tvSocial,tvRelatives,tvMedications;

	//underline tabs
	View underLineDiagnosis_0,underLineDiagnosis_1,underLineDiagnosis_2,underLineDiagnosis_3;


	//new Diagnosis ILC_CODES field
	LinearLayout layDiagnosis;
	ProgressBar pbAutoComplete;
	private static final int TRIGGER_AUTO_COMPLETE = 100;
	private static final long AUTO_COMPLETE_DELAY = 500; //300 - > orig value
	private Handler handler;
	private IcdCodesAdapter icdCodesAdapter;
	EditText etMedHistrDiagnosis;
	//new Diagnosis ILC_CODES field


	//New Smoke Section code
	RadioButton radioSmokeCurrentSmoker,radioSmokeFormerSmoker,radioSmokeNonSmoker;
	LinearLayout laySmokeDetail,laySmokeCurrentSmoker,laySmokeFormerSmoker;
	Spinner spSmokeType,spSmokeHowMuchPerDay,spSmokeReadyToQuit;
	EditText etSmokeWhatAge,etSmokeHowLongDidUsmoke,etSmokeQuitDate;
	TextView tvSmokeTypeLbl,tvSmokeAgeLbl,tvSmokeHowMuchPerDayLbl,tvSmokeReadyToQuitLbl,tvSmokeHowLongDidULbl,tvSmokeQuitDateLbl;//labels

	//EditText etSmokeHowMuch,etSmokeHowLong;
	//RadioButton radioMedicalSmokeYes,radioMedicalSmokeNo;

	//Current Smoker=0 , Former Smoker=1, Non Smoker=2

	ArrayList<String> smokeTypeList;
	ArrayList<String> smokeHowMuchPerDayList;
	ArrayList<String> smokeReadyToQuitList;
	//New Smoke Section code
	
	/*String pastHistryArr[] = {"Anemia","Heart Disease","Rheumetic Fever","Mitral Valve Prolapse Thyroid","Cancer","Gall Blader Disease","Blood Transfusion",
			"Pelvic Infection","Bladder Infections","Genital Herpes","Seizurs","Migrains","Liver Disease","Depression/Anxiety",
			"Drug or Alcohol Problem","Gonorrhea/Syphillis/Chlamydia","Asthma","Pneumonia","Diabetes","Sickle Cell Trait","Blood clot in legs/lungs",
			"Osteopenia",};*/

	ViewFlipper viewFlipper;
	CheckInternetConnection connection;
	CustomToast toast;
	boolean shouldUpdate = true;

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
		if (hosptalizationList != null){
			if(hosptalizationList.isEmpty()){
				DATA.isHospitalized = 0;
			}else {
				DATA.isHospitalized = 1;
			}
		}else {
			DATA.isHospitalized = 0;
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.medical_history_new);

		activity = MedicalHistory1.this;
		apiCallBack = this;
		connection = new CheckInternetConnection(activity);
		toast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper1);

		btnFlipNext = (Button) findViewById(R.id.btnFlipNext);
		btnFlipPrev = (Button) findViewById(R.id.btnFlipPrev);

		setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Medical History");

		findViewById(R.id.btnToolbar).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				final Dialog verDialog = new Dialog(activity);
				verDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				verDialog.setContentView(R.layout.dialog_verification);
				verDialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

				final TextView tvMessage = (TextView) verDialog.findViewById(R.id.tvMessage);
				final EditText etPincode = (EditText) verDialog.findViewById(R.id.etPincode);
				Button btnEnterPincode = (Button) verDialog.findViewById(R.id.btnEnterPincode);
				Button btnForgotPincode = (Button) verDialog.findViewById(R.id.btnForgotPincode);

				btnEnterPincode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						if (etPincode.getText().toString().isEmpty()) {
							Toast.makeText(activity, "Please enter your prescription pincode", Toast.LENGTH_SHORT).show();
						} else {
							if (prefs.getString("pincode", "1234").equals(etPincode.getText().toString())) {
								verDialog.dismiss();
								openActivity.open(ReportFolders.class, false);
							} else {
								tvMessage.setText("Incorrect pincode. Possible typing mistake?");
							}
						}
					}
				});

				btnForgotPincode.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						verDialog.dismiss();
						initForgotPincodeDialogDialog();
					}
				});

				verDialog.show();
			}
		});

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
		DATA.pastHistoryBeans.add(new PastHistoryBean(21, "Osteopenia", false));

		adapter = new PastHistoryAdapter(activity, DATA.pastHistoryBeans);
		lvMedicalHistory1.setAdapter(adapter);*/

		lvMedicalHistory1 = (ListView) findViewById(R.id.lvMedicalHistory1);
		etPastOther = (EditText) findViewById(R.id.etPastOther);
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
		//etStreetDrugList = (EditText) findViewById(R.id.etStreetDrugList);
		etSocialOther = (EditText) findViewById(R.id.etSocialOther);
		//radioMedicalSmokeYes=(RadioButton) findViewById(R.id.radioSmokeYes);
		//radioMedicalSmokeNo=(RadioButton) findViewById(R.id.radioSmokeNo);
		radioMedicalAlcoholYes=(RadioButton) findViewById(R.id.rdioAlcoholYes);
		radioMedicalAlcoholNo=(RadioButton) findViewById(R.id.radioAlcoholNo);
		radioStreetDrugYes=(RadioButton) findViewById(R.id.radioStreetYes);
		radioStreetDrugNo=(RadioButton) findViewById(R.id.radioStreetNo);
		layStreetDrugs = findViewById(R.id.layStreetDrugs);
		cbLSD = findViewById(R.id.cbLSD);
		cbMarijuana = findViewById(R.id.cbMarijuana);
		cbCocaine = findViewById(R.id.cbCocaine);
		cbMethAmphetamines = findViewById(R.id.cbMethAmphetamines);
		cbHeroine = findViewById(R.id.cbHeroine);
		cbCarfentanyl = findViewById(R.id.cbCarfentanyl);
		checkBoxesStreetDrugs = new CheckBox[]{cbLSD, cbMarijuana, cbCocaine, cbMethAmphetamines, cbHeroine, cbCarfentanyl};
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
		checkFamilyOther = (CheckBox) findViewById(R.id.checkFamilyOther);

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
		etFamilyOther = (EditText) findViewById(R.id.etFamilyOther);
		//Family History ends========================================================

		//Final History==============================================================
		rgMedications = (RadioGroup) findViewById(R.id.rgMedications);
		rgAllergies = (RadioGroup) findViewById(R.id.rgAllergies);
		etMedicalHistoryOther = (EditText) findViewById(R.id.etMedicalHistoryOther);
		btnUpdateMedical = (Button) findViewById(R.id.btnUpdateMedical);
		radioMedicationsYes=(RadioButton) findViewById(R.id.radioMedicationYes);
		radioMedicationsNo=(RadioButton) findViewById(R.id.radioMedicationNo);
		radioAllergiesYes=(RadioButton) findViewById(R.id.radioAllergiesYes);
		radioAllergiesNo=(RadioButton) findViewById(R.id.radioAllergiesNo);

		//Final History ends=========================================================

		layNoNetwork = findViewById(R.id.layNoNetwork);

		btnFlipNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DATA.print("--viewFlipper "+ viewFlipper.getDisplayedChild());
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
				DATA.print("--viewFlipper "+ viewFlipper.getDisplayedChild());
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
				
				/*if(DATA.isFromFirstLogin) {
					
					openActivity.open(FreeCare.class, true);
				}
				else {*/
				StringBuilder s = new StringBuilder(200);
				DATA.desaesenamesFromHistory = "";
				for (PastHistoryBean bean : DATA.pastHistoryBeans) {
					if (bean.isSelected()) {
						s.append(DATA.desaesenamesFromHistory+bean.getId());
						s.append(",");
						//DATA.desaesenamesFromHistory=DATA.desaesenamesFromHistory+bean.getId()+",";
					}
				}
				DATA.desaesenamesFromHistory = s.toString();
				if (!DATA.desaesenamesFromHistory.isEmpty()) {
					DATA.desaesenamesFromHistory=DATA.desaesenamesFromHistory.substring(0, DATA.desaesenamesFromHistory.length()-1);
				}
				DATA.print("--selected positions "+DATA.desaesenamesFromHistory);

				String patient_id = prefs.getString("id", "");
				String medical_history=DATA.desaesenamesFromHistory;
				String is_smoke=DATA.isSmoke+"";
				String smoke_detail = "";
				/*if (DATA.isSmoke == 1) {
					if (etSmokeHowLong.getText().toString().isEmpty() || etSmokeHowMuch.getText().toString().isEmpty()) {
						shouldUpdate = false;
						Toast.makeText(activity, "Please enter smoke detail", Toast.LENGTH_SHORT).show();
					}else {
						shouldUpdate = true;
						smoke_detail= etSmokeHowLong.getText().toString()+"/"+etSmokeHowMuch.getText().toString();
					}
				}*/

				if(DATA.isSmoke == 0){
					String smokeType = "";
					try {smokeType = smokeTypeList.get(spSmokeType.getSelectedItemPosition());}catch (Exception e){e.printStackTrace();}
					String smokeAge = etSmokeWhatAge.getText().toString().trim();
					String smokeHowMuchPerDay = "";
					try {smokeHowMuchPerDay = smokeHowMuchPerDayList.get(spSmokeHowMuchPerDay.getSelectedItemPosition());}catch (Exception e){e.printStackTrace();}
					String smokeReadyToQuit = "";
					try {smokeReadyToQuit = smokeReadyToQuitList.get(spSmokeReadyToQuit.getSelectedItemPosition());}catch (Exception e){e.printStackTrace();}

					smoke_detail = smokeType + "|" +smokeAge + "|" + smokeHowMuchPerDay + "|" + smokeReadyToQuit;
				}else if(DATA.isSmoke == 1){
					String smokeType = "";
					try {smokeType = smokeTypeList.get(spSmokeType.getSelectedItemPosition());}catch (Exception e){e.printStackTrace();}
					String smokeHowLong = etSmokeHowLongDidUsmoke.getText().toString().trim();
					String smokeQuitDate = etSmokeQuitDate.getText().toString().trim();

					smoke_detail = smokeType + "|" + smokeHowLong + "|" + smokeQuitDate;
				}else if(DATA.isSmoke == 2){
					smoke_detail = "";
				}

				String is_drink = DATA.isDrunk+"";
				String drink_detail = "";
				if (DATA.isDrunk == 1) {
					if (etAlcoholHowLong.getText().toString().isEmpty() || etAlcoholHowMuch.getText().toString().isEmpty()) {
						shouldUpdate = false;
						Toast.makeText(activity, "Please enter drink detail", Toast.LENGTH_SHORT).show();
					} else {
						shouldUpdate = true;
						drink_detail=etAlcoholHowLong.getText().toString()+"/"+etAlcoholHowMuch.getText().toString();
					}
				}

				String is_drug = DATA.isDrug+"";
				String drug_detail = "";
				if (DATA.isDrug == 1) {
					StringBuilder sbDrugDetail = new StringBuilder();
					for (int i = 0; i < checkBoxesStreetDrugs.length; i++) {
						if(checkBoxesStreetDrugs[i].isChecked()){
							sbDrugDetail.append(checkBoxesStreetDrugs[i].getText().toString());
							sbDrugDetail.append("\n");
						}
					}
					if (sbDrugDetail.toString().isEmpty()) {//etStreetDrugList.getText()
						shouldUpdate = false;
						Toast.makeText(activity, "Please enter drugs detail", Toast.LENGTH_SHORT).show();
					} else {
						shouldUpdate = true;
						drug_detail = sbDrugDetail.substring(0, sbDrugDetail.length()-1);//etStreetDrugList.getText()
					}
				}


				String is_medication=DATA.isMedication+"";
				String medication_detail = "";
				if (DATA.isMedication == 1) {
					if(medicationList != null){
						if (medicationList.isEmpty()) {
							shouldUpdate = false;
							Toast.makeText(activity, "Please enter medication detail", Toast.LENGTH_SHORT).show();
						} else {
							shouldUpdate = true;
							for (int i = 0; i < medicationList.size(); i++) {
								medication_detail = medication_detail+medicationList.get(i);
								if(i < (medicationList.size()-1)){
									medication_detail = medication_detail+"\n";
								}
							}
						}
					}else{
						shouldUpdate = false;
						Toast.makeText(activity, "Please enter medication detail", Toast.LENGTH_SHORT).show();
					}
				}

				String is_alergies=DATA.isAlergies+"";
				String alergies_detail = "";
				if (DATA.isAlergies == 1) {
					if(allergiesList != null){
						if(allergiesList.isEmpty()){
							shouldUpdate = false;
							Toast.makeText(activity, "Please enter Allergies detail", Toast.LENGTH_SHORT).show();
						}else{
							shouldUpdate = true;
							for (int i = 0; i < allergiesList.size(); i++) {
								alergies_detail = alergies_detail+allergiesList.get(i)+"\n";
							}
						}
					}else{
						shouldUpdate = false;
						Toast.makeText(activity, "Please enter Allergies detail", Toast.LENGTH_SHORT).show();
					}
				}


				String hospitalize_detail = "";
				if(hosptalizationList != null){
					if(hosptalizationList.isEmpty()){
						DATA.isHospitalized = 0;
						hospitalize_detail= "";
					}else{
						DATA.isHospitalized = 1;
						for (int i = 0; i < hosptalizationList.size(); i++) {
							hospitalize_detail = hospitalize_detail + hosptalizationList.get(i)+"\n";
						}
					}
				}else{
					DATA.isHospitalized = 0;
					hospitalize_detail= "";
				}
				String is_hospitalize = DATA.isHospitalized+"";

//			CheckBox checkDisease, checkHeart,checkMental, checkCancer, checkBp, checkStroke, checkDiabetes,
//				checkKidney, checkTubr, checkEpilespy, checksickle, checkBleeming,checkFamilyOther;
//
//			EditText etDisease,etHeart, etMental, etCancer, etBp, etStroke, etDiabetes,
//					 etKidney, etTubr, etEpilespy, etsickle, etBleeming, etFamilyOther;
				String relation_had="";
				String relation_had_name="";
				String relation_had_id="";
				if (checkDisease.isChecked()) {
					if (etDisease.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Inherited Disease detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etDisease.setError("Please enter Inherited Disease detail");
					} else {
						relation_had =relation_had+checkDisease.getText().toString();
						relation_had_name = relation_had_name+etDisease.getText().toString();
						relation_had_id = relation_had_id+checkDisease.getTag().toString();
					}
				}
				if (checkHeart.isChecked()) {
					if (etHeart.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Heart Trouble detail", Toast.LENGTH_SHORT).show();

						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etHeart.setError("Please enter Heart Trouble detail");
					} else {
						relation_had = relation_had+"/"+checkHeart.getText().toString();
						relation_had_name = relation_had_name+"/"+etHeart.getText().toString();
						relation_had_id = relation_had_id+"/"+checkHeart.getTag().toString();
					}
				}
				if (checkMental.isChecked()) {
					if (etMental.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Mental Illness detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etMental.setError("Please enter Mental Illness detail");
					} else {
						relation_had = relation_had+"/"+checkMental.getText().toString();
						relation_had_name = relation_had_name+"/"+etMental.getText().toString();
						relation_had_id = relation_had_id+"/"+checkMental.getTag().toString();
					}
				}
				if (checkCancer.isChecked()) {
					if (etCancer.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Cancer detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etCancer.setError("Please enter Cancer detail");
					} else {
						relation_had = relation_had+"/"+checkCancer.getText().toString();
						relation_had_name = relation_had_name+"/"+etCancer.getText().toString();
						relation_had_id = relation_had_id+"/"+checkCancer.getTag().toString();
					}
				}
				if (checkBp.isChecked()) {
					if (etBp.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter High Blood Pressure detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etBp.setError("Please enter High Blood Pressure detail");
					} else {
						relation_had = relation_had+"/"+checkBp.getText().toString();
						relation_had_name = relation_had_name+"/"+etBp.getText().toString();
						relation_had_id = relation_had_id+"/"+checkBp.getTag().toString();
					}
				}
				if (checkStroke.isChecked()) {
					if (etStroke.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Stroke detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etStroke.setError("Please enter Stroke detail");
					} else {
						relation_had = relation_had+"/"+checkStroke.getText().toString();
						relation_had_name = relation_had_name+"/"+etStroke.getText().toString();
						relation_had_id = relation_had_id+"/"+checkStroke.getTag().toString();
					}
				}
				if (checkDiabetes.isChecked()) {
					if (etDiabetes.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Diabetes detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etDiabetes.setError("Please enter Diabetes detail");
					} else {
						relation_had = relation_had+"/"+checkDiabetes.getText().toString();
						relation_had_name = relation_had_name+"/"+etDiabetes.getText().toString();
						relation_had_id = relation_had_id+"/"+checkDiabetes.getTag().toString();
					}
				}
				if (checkKidney.isChecked()) {
					if (etKidney.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Kidney Problem detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etKidney.setError("Please enter Kidney Problem detail");
					} else {
						relation_had = relation_had+"/"+checkKidney.getText().toString();
						relation_had_name = relation_had_name+"/"+etKidney.getText().toString();
						relation_had_id = relation_had_id+"/"+checkKidney.getTag().toString();
					}
				}
				if (checkTubr.isChecked()) {
					if (etTubr.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Tuberculosis detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etTubr.setError("Please enter Tuberculosis detail");
					} else {
						relation_had = relation_had+"/"+checkTubr.getText().toString();
						relation_had_name = relation_had_name+"/"+etTubr.getText().toString();
						relation_had_id = relation_had_id+"/"+checkTubr.getTag().toString();
					}
				}
				if (checkEpilespy.isChecked()) {
					if (etEpilespy.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Epilespy detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etEpilespy.setError("Please enter Epilespy detail");
					} else {
						relation_had = relation_had+"/"+checkEpilespy.getText().toString();
						relation_had_name = relation_had_name+"/"+etEpilespy.getText().toString();
						relation_had_id = relation_had_id+"/"+checkEpilespy.getTag().toString();
					}
				}
				if (checksickle.isChecked()) {
					if (etsickle.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Sickle Cell Disease detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etsickle.setError("Please enter Sickle Cell Disease detail");
					} else {
						relation_had = relation_had+"/"+checksickle.getText().toString();
						relation_had_name = relation_had_name+"/"+etsickle.getText().toString();
						relation_had_id = relation_had_id+"/"+checksickle.getTag().toString();
					}
				}
				if (checkBleeming.isChecked()) {
					if (etBleeming.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Bleeming Problems detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etBleeming.setError("Please enter Bleeding Problems detail");
					} else {
						relation_had = relation_had+"/"+checkBleeming.getText().toString();
						relation_had_name = relation_had_name+"/"+etBleeming.getText().toString();
						relation_had_id = relation_had_id+"/"+checkBleeming.getTag().toString();
					}
				}
				if (checkFamilyOther.isChecked()) {
					if (etFamilyOther.getText().toString().isEmpty()) {
						shouldUpdate = false;
						//Toast.makeText(activity, "Please enter Bleeming Problems detail", Toast.LENGTH_SHORT).show();
						viewFlipper.setDisplayedChild(2);
						btnFlipNext.setEnabled(true);
						etFamilyOther.setError("Please enter problem detail");
					} else {
						relation_had = relation_had+"/"+checkFamilyOther.getText().toString();
						relation_had_name = relation_had_name+"/"+etFamilyOther.getText().toString();
						relation_had_id = relation_had_id+"/"+checkFamilyOther.getTag().toString();
					}
				}

				String icd_codes = etMedHistrDiagnosis.getText().toString();

				if (connection.isConnectedToInternet()) {
					if(shouldUpdate){
						sendMedicalHistory(patient_id, medical_history, is_smoke, smoke_detail, is_drink, drink_detail, is_drug, drug_detail,
								relation_had, relation_had_name, relation_had_id, is_medication, medication_detail, is_alergies, alergies_detail,
								is_hospitalize, hospitalize_detail, icd_codes);
					}
				}else {
					toast.showToast("Please check internet connection and try again", 0, Toast.LENGTH_SHORT);
				}

				//finish();
				//}

//				Toast.makeText(activity, "Upload and save on the server is under construction", 1).show();
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

		etSmokeQuitDate.setOnClickListener(v -> {
			DialogFragment newFragment = new DatePickerFragment(etSmokeQuitDate);
			newFragment.show(appCompatActivity.getSupportFragmentManager(), "datePicker");
		});

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

					/*case R.id.radioSmokeYes:
						etSmokeHowLong.setVisibility(View.VISIBLE);
						etSmokeHowMuch.setVisibility(View.VISIBLE);
						DATA.isSmoke = 1;
						//shouldUpdate = true;
						break;
					case R.id.radioSmokeNo:
						etSmokeHowLong.setVisibility(View.GONE);
						etSmokeHowMuch.setVisibility(View.GONE);
						DATA.isSmoke = 0;
						shouldUpdate = true;

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
						//shouldUpdate = true;
						break;
					case R.id.radioAlcoholNo:
						etAlcoholHowLong.setVisibility(View.GONE);
						etAlcoholHowMuch.setVisibility(View.GONE);
						DATA.isDrunk = 0;
						shouldUpdate = true;

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
						layStreetDrugs.setVisibility(View.VISIBLE);//etStreetDrugList
						DATA.isDrug = 1;
						//shouldUpdate = true;

						break;
					case R.id.radioStreetNo:
						layStreetDrugs.setVisibility(View.GONE);//etStreetDrugList
						DATA.isDrug = 0;
						shouldUpdate = true;
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
						findViewById(R.id.medicationsCont).setVisibility(View.VISIBLE);
						DATA.isMedication=1;
						//shouldUpdate = true;
						break;
					case R.id.radioMedicationNo:
						findViewById(R.id.medicationsCont).setVisibility(View.GONE);
						DATA.isMedication=0;
						shouldUpdate = true;
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
						findViewById(R.id.contAllergies).setVisibility(View.VISIBLE);
						DATA.isAlergies = 1;
						//shouldUpdate = true;
						break;
					case R.id.radioAllergiesNo:
						findViewById(R.id.contAllergies).setVisibility(View.GONE);
						DATA.isAlergies = 0;
						shouldUpdate = true;
						break;

					default:
						Toast.makeText(getApplicationContext(), id+"", Toast.LENGTH_SHORT).show();

						break;
				}

			}
		});

		//=====================Medications========================================================================
		etMedicationsAddMedication = (EditText) findViewById(R.id.etMedicationsAddMedication);
		lvMedications = (ExpandableHeightListView) findViewById(R.id.lvMedications);
		ivAddMedication= (ImageView) findViewById(R.id.ivAddMedication);
        ivSearchMedication = (ImageView) findViewById(R.id.ivSearchMedication);
		tvAddMed = findViewById(R.id.tvAddMed);
		tvViewMed = findViewById(R.id.tvViewMed);

		if(medicationList == null){
			medicationList = new ArrayList<>();
		}
		tvAddMed.setOnClickListener(new OnClickListener() {//new code emcura iphone
			@Override
			public void onClick(View v) {
				addMedDialog();
			}
		});
		tvViewMed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showMediDialog();
			}
		});
		ivAddMedication.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etMedicationsAddMedication.getText().toString().isEmpty()){
					hideShowKeypad.hideSoftKeyboard();
					medicationList.add(etMedicationsAddMedication.getText().toString());
					lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
					lvMedications.setExpanded(true);
					etMedicationsAddMedication.setText("");
				}else {
					etMedicationsAddMedication.setError("Please enter medication name");
				}
			}
		});
        ivSearchMedication.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!etMedicationsAddMedication.getText().toString().trim().isEmpty()){
                    showDrugDialog(etMedicationsAddMedication.getText().toString().trim(),false,0);
                }else {
                    etMedicationsAddMedication.setError("Please enter medication name");
                }
            }
        });
		etMedicationsAddMedication.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(!etMedicationsAddMedication.getText().toString().trim().isEmpty()){
						//hideShowKeypad.hideSoftKeyboard();
						showAskMedicationDialog(etMedicationsAddMedication.getText().toString().trim());

					}else {
						etMedicationsAddMedication.setError("Please enter medication name");
					}
					//return true; return true not closes keyboard
					return false;
				}
				return false;
			}
		});
		//=====================Allergies========================================================================
		lvAllergies = (ExpandableHeightListView) findViewById(R.id.lvAllergies);
		ivAddAllergies = (ImageView) findViewById(R.id.ivAddAllergies);;
		etAddAllergies = (EditText) findViewById(R.id.etAddAllergies);
		tvAddAllergy = findViewById(R.id.tvAddAllergy);
		tvViewAllergy = findViewById(R.id.tvViewAllergy);
		if(allergiesList == null){
			allergiesList = new ArrayList<>();
		}
		//new code emcura iphone app like
		tvAddAllergy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addAllergiesDialog();
			}
		});
		tvViewAllergy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAllergiesDialog();
			}
		});
		//new code emcura iphone app like ends
		ivAddAllergies.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etAddAllergies.getText().toString().isEmpty()){
					hideShowKeypad.hideSoftKeyboard();
					allergiesList.add(etAddAllergies.getText().toString());
					lvAllergies.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
					lvAllergies.setExpanded(true);
					etAddAllergies.setText("");
				}else {
					etAddAllergies.setError("Please enter allergy name");
				}
			}
		});
		etAddAllergies.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(!etAddAllergies.getText().toString().isEmpty()){
						hideShowKeypad.hideSoftKeyboard();
						allergiesList.add(etAddAllergies.getText().toString());
						lvAllergies.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
						lvAllergies.setExpanded(true);
						etAddAllergies.setText("");
					}else {
						etAddAllergies.setError("Please enter allergy name");
					}
					//return true; return true not closes keyboard
					return false;
				}
				return false;
			}
		});

		//=====================Hospitalizations========================================================================
		lvHosptalization = (ExpandableHeightListView) findViewById(R.id.lvHosptalization);
		ivAddHosptalization = (ImageView) findViewById(R.id.ivAddHosptalization);;
		etAddHosptalization = (EditText) findViewById(R.id.etAddHosptalization);
		tvAddHosp = findViewById(R.id.tvAddHosp);
		tvViewHosp = findViewById(R.id.tvViewHosp);

		if(hosptalizationList == null){
			hosptalizationList = new ArrayList<>();
		}

		//new code emcura iphone app
		tvAddHosp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				addHospDialog();
			}
		});
		tvViewHosp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showHospitalizationDialog();
			}
		});
		//new code emcura iphone app ends
		ivAddHosptalization.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!etAddHosptalization.getText().toString().isEmpty()){
					hideShowKeypad.hideSoftKeyboard();
					hosptalizationList.add(etAddHosptalization.getText().toString());
					lvHosptalization.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
					lvHosptalization.setExpanded(true);
					etAddHosptalization.setText("");
				}else {
					etAddHosptalization.setError("Please enter Hospitalization/Surgery name");
				}
			}
		});
		etAddHosptalization.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					if(!etAddHosptalization.getText().toString().isEmpty()){
						hideShowKeypad.hideSoftKeyboard();
						hosptalizationList.add(etAddHosptalization.getText().toString());
						lvHosptalization.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
						lvHosptalization.setExpanded(true);
						etAddHosptalization.setText("");
					}else {
						etAddHosptalization.setError("Please enter Hospitalization/Surgery name");
					}
					//return true; return true not closes keyboard
					return false;
				}
				return false;
			}
		});
		//========================Tabs================================================================================
		tvPast = (TextView) findViewById(R.id.tvPast);
		underLineDiagnosis_0 = findViewById(R.id.underLineDiagnosis_0);
		underLineDiagnosis_1 = findViewById(R.id.underLineDiagnosis_1);
		underLineDiagnosis_2 = findViewById(R.id.underLineDiagnosis_2);
		underLineDiagnosis_3 = findViewById(R.id.underLineDiagnosis_3);
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


		ScrollView scroll = (ScrollView) findViewById(R.id.nb);
		preventScrollViewFromScrollingToEdiText(scroll);
		/*scroll.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				View focussedView = getCurrentFocus();
				if( focussedView != null ) focussedView.clearFocus();

				return false;
			}
		});*/



		//new Diagnosis ILC_CODES field
		etMedHistrDiagnosis = findViewById(R.id.etMedHistrDiagnosis);
		layDiagnosis = findViewById(R.id.layDiagnosis);
		//===============AutoComplete========================
		pbAutoComplete = (ProgressBar) findViewById(R.id.pbAutoComplete);
		pbAutoComplete.getIndeterminateDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), android.graphics.PorterDuff.Mode.MULTIPLY);

		final AppCompatAutoCompleteTextView autoCompleteTextView = (AppCompatAutoCompleteTextView) findViewById(R.id.auto_complete_edit_text);

		//Setting up the adapter for AutoSuggest
		icdCodesAdapter = new IcdCodesAdapter(activity, android.R.layout.simple_spinner_dropdown_item);
		autoCompleteTextView.setThreshold(2);
		autoCompleteTextView.setAdapter(icdCodesAdapter);
		autoCompleteTextView.setOnItemClickListener(
				new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						//selectedText.setText(icdCodesAdapter.getObject(position).toString());
						String diagnosis = icdCodesAdapter.getObject(position).desc+ " ("+icdCodesAdapter.getObject(position).code+")";

						String diag = etMedHistrDiagnosis.getText().toString();
						if(TextUtils.isEmpty(diag)){
							diag = diagnosis;
						}else {
							diag = diag + "\n"+diagnosis;//,
						}
						etMedHistrDiagnosis.setText(diag);

						autoCompleteTextView.setText("");

						hideShowKeypad.hideSoftKeyboard();

					}
				});

		autoCompleteTextView.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				handler.removeMessages(TRIGGER_AUTO_COMPLETE);
				handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE, AUTO_COMPLETE_DELAY);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		handler = new Handler(new Handler.Callback() {
			@Override
			public boolean handleMessage(Message msg) {
				if (msg.what == TRIGGER_AUTO_COMPLETE) {
					if (!TextUtils.isEmpty(autoCompleteTextView.getText())) {
						pbAutoComplete.setVisibility(View.VISIBLE);
						RequestParams params = new RequestParams();
						params.put("keyword", autoCompleteTextView.getText().toString());
						ApiManager apiManager = new ApiManager(ApiManager.GET_ICD_CODES,"post",params, apiCallBack, activity);
						ApiManager.shouldShowLoader = false;
						apiManager.loadURL();
					}
				}
				return false;
			}
		});
		//new Diagnosis ILC_CODES field Ends


		getMedicalHistory(prefs.getString("id", "0"));
		layNoNetwork.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getMedicalHistory(prefs.getString("id", "0"));
			}
		});

	}//end oncreate

	public static void preventScrollViewFromScrollingToEdiText(ScrollView view) {
		view.setDescendantFocusability(ViewGroup.FOCUS_BEFORE_DESCENDANTS);
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		view.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				v.requestFocusFromTouch();
				return false;
			}
		});
	}

	void setupViewFiliperAndTabs(int pos){
		switch (pos){
			case 0:
				//tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red));

				underLineDiagnosis_0.setBackgroundColor(getResources().getColor(R.color.theme_red));
				underLineDiagnosis_1.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_2.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_3.setBackgroundColor(getResources().getColor(R.color.gray_light));


				//tvSubjective.setTextColor(Color.parseColor("#FFFFFF"));
//				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
//				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(0);
				break;
			case 1:
				//tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				underLineDiagnosis_0.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_1.setBackgroundColor(getResources().getColor(R.color.theme_red));
				underLineDiagnosis_2.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_3.setBackgroundColor(getResources().getColor(R.color.gray_light));
				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red));
//				//tvObjective.setTextColor(Color.parseColor("#FFFFFF"));
//				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
//				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(1);
				break;
			case 2:
				//tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

				underLineDiagnosis_0.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_1.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_2.setBackgroundColor(getResources().getColor(R.color.theme_red));
				underLineDiagnosis_3.setBackgroundColor(getResources().getColor(R.color.gray_light));

				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red));
//				//tvASSESMENT.setTextColor(Color.parseColor("#FFFFFF"));
//				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
				//tvPlan.setTextColor(getResources().getColor(R.color.theme_red));

				viewFlipper.setDisplayedChild(2);
				break;
			case 3:
			//	tvPast.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

				underLineDiagnosis_0.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_1.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_2.setBackgroundColor(getResources().getColor(R.color.gray_light));
				underLineDiagnosis_3.setBackgroundColor(getResources().getColor(R.color.theme_red));

				//tvSubjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvSocial.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvObjective.setTextColor(getResources().getColor(R.color.theme_red));
//				tvRelatives.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
//				//tvASSESMENT.setTextColor(getResources().getColor(R.color.theme_red));
//				tvMedications.setBackgroundColor(getResources().getColor(R.color.theme_red));
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


//	api: saveMedicalHistory (post)
//	Params:
//	patient_id
//	medical_history (comma sepereated)
//	is_smoke (1/0)
//  smoke_detail(text)
//  is_drink (1/0)
//   drink_detail(text)
//   is_drug (1/0)	
//  drug_detail (text)
//  relation_had (comma seprated ids that you saved in local db)
//  relation_had_name (comma seperated text)
//  is_medication (1/0)
//  medication_detail (txt)
//  is_alergies (1/0)
//  alergies_detail (text)
//  is_hospitalize (1/0)
//  hospitalize_detail (text)


	public void sendMedicalHistory(String patient_id,String medical_history,String is_smoke,String smoke_detail,
								   String is_drink,String drink_detail,String is_drug,String drug_detail,
								   String relation_had,String relation_had_name,String relation_had_id,String is_medication,String medication_detail,
								   String is_alergies,String alergies_detail,String is_hospitalize,String hospitalize_detail, String icd_codes) {

		RequestParams params = new RequestParams();
		params.put("patient_id", patient_id);
		params.put("medical_history", medical_history);
		params.put("is_smoke", is_smoke);
		params.put("smoke_detail", smoke_detail);
		params.put("is_drink", is_drink);
		params.put("drink_detail", drink_detail);
		params.put("is_drug", is_drug);
		params.put("drug_detail", drug_detail);
		params.put("relation_had", relation_had);
		params.put("relation_had_name", relation_had_name);
		params.put("relation_had_id", relation_had_id);
		params.put("is_medication", is_medication);
		params.put("medication_detail", medication_detail);
		params.put("is_alergies", is_alergies);
		params.put("alergies_detail", alergies_detail);
		params.put("is_hospitalize", is_hospitalize);
		params.put("hospitalize_detail", hospitalize_detail);
		params.put("other",etMedicalHistoryOther.getText().toString());
		params.put("social_other",etSocialOther.getText().toString());
		params.put("medical_history_other",etPastOther.getText().toString());
		params.put("icd_codes", icd_codes);

		ApiManager apiManager = new ApiManager(ApiManager.SAVE_MEDICAL_HISTORY,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	public void getMedicalHistory(String patient_id) {

		int vis = connection.isConnectedToInternet() ? View.GONE : View.VISIBLE;
		layNoNetwork.setVisibility(vis);

		ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_HISTORY+"/"+patient_id,"get",null,apiCallBack, activity);
		apiManager.loadURL();
	}

	@Override
	public void fetchDataCallback(String httpstatus, String apiName, String content) {

		if(apiName.equalsIgnoreCase(ApiManager.SAVE_MEDICAL_HISTORY)){

			try {
				JSONObject jsonObject = new JSONObject(content);
				if (jsonObject.has("success") && jsonObject.getInt("success") == 1) {
					if(DATA.isFromFirstLogin) {
						openActivity.open(FreeCare.class, true);
					}else

						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle(getResources().getString(R.string.app_name))
								.setMessage("Your medical history has been saved successfully")
								.setPositiveButton("Done", null)
						.setOnDismissListener(new DialogInterface.OnDismissListener() {@Override public void onDismiss(DialogInterface dialog) {finish();}})
								//.setNegativeButton("Not Now", null)
								.create()
								.show();


						/*new SweetAlertDialog(activity, SweetAlertDialog.SUCCESS_TYPE)
								.setTitleText("Success")
								.setContentText("Your medical history has been saved")
								.setConfirmText("OK").setConfirmClickListener(new OnSweetClickListener() {

							@Override
							public void onClick(SweetAlertDialog sweetAlertDialog) {
								sweetAlertDialog.dismiss();

								if (connection.isConnectedToInternet()) {
									getMedicalHistory(prefs.getString("id", "0"));
								} else {
									toast.showToast("No internet connection", 0, 0);
								}
							}
						})
								.show();*/
				} else {
					customToast.showToast(DATA.CMN_ERR_MSG,0,1);
					/*new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
							.setTitleText("Oopss")
							.setContentText("Something went wrong. Please try again.")
							.setConfirmText("OK")
							.show();*/
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
				/*new SweetAlertDialog(activity, SweetAlertDialog.ERROR_TYPE)
						.setTitleText("Oopss")
						.setContentText("Something went wrong. Please try again.")
						.setConfirmText("OK")
						.show();*/
			}
		}else if(apiName.contains(ApiManager.GET_MEDICAL_HISTORY)){

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
					DATA.pastHistoryBeans.add(new PastHistoryBean(Integer.parseInt(diseases.getJSONObject(i).getString("id")),
							dName, false));
				}
				adapter = new PastHistoryAdapter(activity, DATA.pastHistoryBeans);
				lvMedicalHistory1.setAdapter(adapter);
				//==============================================================================

				int success = jsonObject.getInt("success");
				String message = jsonObject.getString("message");
				JSONObject data = jsonObject.getJSONObject("data");

				if(! data.has("id")){
					AlertDialog alertDialog =
							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle(getResources().getString(R.string.app_name))
									.setMessage("Medical history not added on your profile. Please add your medical history now.")
									.setPositiveButton("Done",null).create();
					alertDialog.setCanceledOnTouchOutside(false);
					alertDialog.show();

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

				String icd_codes = data.optString("icd_codes");

				etMedicalHistoryOther.setText(other);
				etSocialOther.setText(social_other);
				etPastOther.setText(medical_history_other);

				etMedHistrDiagnosis.setText(icd_codes);

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
					//etStreetDrugList.setText(drug_detail);
					String[] drugDetailArr = drug_detail.split("\n");
					List<String> drugDetailList = Arrays.asList(drugDetailArr);
					for (int i = 0; i < checkBoxesStreetDrugs.length; i++) {
						if(drugDetailList.contains(checkBoxesStreetDrugs[i].getText().toString())){
							checkBoxesStreetDrugs[i].setChecked(true);
						}else {
							checkBoxesStreetDrugs[i].setChecked(false);
						}
					}
				} else {
					radioStreetDrugNo.setChecked(true);
				}
				if (is_alergies.equals("1")) {
					radioAllergiesYes.setChecked(true);

					allergiesList = new ArrayList<>();
					if(alergies_detail.isEmpty()){

					}else {
						String[] allergyArr = alergies_detail.split("\n");
						for (int i = 0; i < allergyArr.length; i++) {
							allergiesList.add(allergyArr[i]);
						}
					}
					lvAllergies.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
					lvAllergies.setExpanded(true);

				} else {
					radioAllergiesNo.setChecked(true);
				}
				if (is_medication.equals("1")) {
					radioMedicationsYes.setChecked(true);

					medicationList = new ArrayList<>();
					if(medication_detail.isEmpty()){

					}else {
						String[] mediArr = medication_detail.split("\n");
						for (int i = 0; i < mediArr.length; i++) {
							medicationList.add(mediArr[i]);
						}
					}
					lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
					lvMedications.setExpanded(true);

				} else {
					radioMedicationsNo.setChecked(true);
				}
				if (is_hospitalize.equals("1")) {
					hosptalizationList = new ArrayList<>();
					if(hospitalize_detail.isEmpty()){

					}else {
						String[] hospArr = hospitalize_detail.split("\n");
						for (int i = 0; i < hospArr.length; i++) {
							hosptalizationList.add(hospArr[i]);
						}
					}
					lvHosptalization.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
					lvHosptalization.setExpanded(true);
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

						DATA.print("--positinselected "+pos);
					}
					//	medicalHistoryAdapter.notifyDataSetChanged();
					for (PastHistoryBean bean : DATA.pastHistoryBeans) {
						if (DATA.selectedMedicalHistoryPositions.contains(bean.getId())) {
							bean.setSelected(true);
						} else {
							bean.setSelected(false);
						}
					}//end loop
				}
				adapter = new PastHistoryAdapter(activity, DATA.pastHistoryBeans);
				lvMedicalHistory1.setAdapter(adapter);
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
						if (bean.getRelation_had().equalsIgnoreCase("Other")) {
							checkFamilyOther.setChecked(true);
							etFamilyOther.setText(bean.getRelation_had_name());
							DATA.print("--inside true"+bean.getRelation_had_name());
						}
					}


				}

				//--------------------------relation_had-------------------------------------------

				//viewFlipper.setDisplayedChild(0);
				setupViewFiliperAndTabs(0);
				btnFlipNext.setEnabled(true);
				btnFlipPrev.setEnabled(false);

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.GET_DRUGS)){
			try {
				drugBeans = new ArrayList<DrugBean>();
				DrugBean temp;

				JSONArray jsonArray = new JSONObject(content).getJSONArray("data");
				if(jsonArray.length() == 0){
					toast.showToast("No drug found",0,1);
				}
				for (int i = 0; i < jsonArray.length(); i++) {
					String drug_descriptor_id = jsonArray.getJSONObject(i).getString("drug_descriptor_id");
					String route_of_administration = jsonArray.getJSONObject(i).getString("route_of_administration");
					String drug_name = jsonArray.getJSONObject(i).getString("drug_name");
					String code = jsonArray.getJSONObject(i).getString("code");
					String route = jsonArray.getJSONObject(i).getString("route");
					String strength = jsonArray.getJSONObject(i).getString("strength");
					String strength_unit_of_measure = jsonArray.getJSONObject(i).getString("strength_unit_of_measure");
					String dosage_form = jsonArray.getJSONObject(i).getString("dosage_form");
					String dfcode = jsonArray.getJSONObject(i).getString("dfcode");
					String dfdesc = jsonArray.getJSONObject(i).getString("dfdesc");

					String potency_unit = jsonArray.getJSONObject(i).getString("potency_unit");
					String potency_code = jsonArray.getJSONObject(i).getString("potency_code");

					temp = new DrugBean(drug_descriptor_id, route_of_administration, drug_name, code, route, strength, strength_unit_of_measure, dosage_form, dfcode, dfdesc,potency_unit,potency_code);
					drugBeans.add(temp);
					temp = null;
				}

				//setData here

				ArrayAdapter<DrugBean> spDrugNameAdapter = new ArrayAdapter<DrugBean>(
						activity,
						android.R.layout.simple_list_item_1,
						drugBeans
				);
				//spDrugNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				lvDrugs.setAdapter(spDrugNameAdapter);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else if(apiName.equalsIgnoreCase(ApiManager.GET_ICD_CODES)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("data");

				Type listType = new TypeToken<ArrayList<IcdCodeBean>>() {}.getType();
				List<IcdCodeBean> icdCodeBeans = gson.fromJson(data.toString(), listType);

                /*List<IcdCodeBean> icdCodeBeans = new ArrayList<>();
                Gson gson = new Gson();
                for (int i = 0; i < data.length(); i++) {
                    icdCodeBeans.add(gson.fromJson(data.getJSONObject(i)+"", IcdCodeBean.class));
                }*/

				pbAutoComplete.setVisibility(View.GONE);
				if(icdCodeBeans != null){
					//IMPORTANT: set data here and notify
					icdCodesAdapter.setData(icdCodeBeans);
					icdCodesAdapter.notifyDataSetChanged();
				}

			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}


	//-------------------------Forgot PIN---------------------
	Dialog forgotPincodeDialog;
	public void initForgotPincodeDialogDialog() {
		forgotPincodeDialog = new Dialog(activity);
		forgotPincodeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		forgotPincodeDialog.setContentView(R.layout.dialog_forgot_pincode);


		final EditText etEmailForgotPincode = (EditText) forgotPincodeDialog.findViewById(R.id.etEmailForgotPincode);
		Button btnEnterForgotPincode = (Button) forgotPincodeDialog.findViewById(R.id.btnEnterForgotPincode);
		Button btnCancelForgotPincode = (Button) forgotPincodeDialog.findViewById(R.id.btnCancelForgotPincode);

		btnEnterForgotPincode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (etEmailForgotPincode.getText().toString().isEmpty()) {
					Toast.makeText(activity, "Please enter your email used for OnlineCare account", Toast.LENGTH_SHORT).show();
				} else {

					if(connection.isConnectedToInternet()) {
						forgotPincode(etEmailForgotPincode.getText().toString());
					}
					else {

						Toast.makeText(activity, "Not connected to Internet", Toast.LENGTH_SHORT).show();
					}


				}
			}
		});

		btnCancelForgotPincode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				forgotPincodeDialog.dismiss();
			}
		});

		forgotPincodeDialog.show();

	}


	public void forgotPincode(String email) {
		DATA.showLoaderDefault(activity,  "Please wait...    ");
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		RequestParams params = new RequestParams();

		params.put("email", email);

		client.post(DATA.baseUrl+"/forgotPincode/patient", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);
					DATA.print("--reaponce in forgotPincode "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						String status = jsonObject.getString("status");
						String msg = jsonObject.getString("msg");

						new CustomToast(activity).showToast(msg, 0, 1);

						forgotPincodeDialog.dismiss();

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: forgotPincode/patient, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onfail forgotPincode " +content);
					new GloabalMethods(activity).checkLogin(content, statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end forgotPincode


	void showMediDialog(){
		Dialog medicationsDialog = new Dialog(activity, R.style.TransparentThemeH4B);
		medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		medicationsDialog.setContentView(R.layout.lay_view_med);
		ListView lvMedicationsDial = (ListView) medicationsDialog.findViewById(R.id.lvMedications);
		TextView tvNoData = medicationsDialog.findViewById(R.id.tvNoData);
		ImageView ivClose = medicationsDialog.findViewById(R.id.ivClose);

		ivClose.setOnClickListener(v -> medicationsDialog.dismiss());

		if(medicationList!= null){
			lvMedicationsDial.setAdapter(new HistoryMediAdapter(activity,medicationList));
			int v = medicationList.isEmpty() ? View.VISIBLE : View.GONE;
			tvNoData.setVisibility(v);
		}else {
			tvNoData.setVisibility(View.VISIBLE);
		}

		medicationsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
                    lvMedications.setExpanded(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(medicationsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		medicationsDialog.show();
		medicationsDialog.getWindow().setAttributes(lp);
	}

	public void addMedDialog(){
		final Dialog medicationsDialog = new Dialog(activity);
		medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		medicationsDialog.setContentView(R.layout.dialog_edit_medi);
		medicationsDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = medicationsDialog.findViewById(R.id.tvDTittle);
		final EditText etEditMed = (EditText) medicationsDialog.findViewById(R.id.etEditMed);
		Button btnSaveMed = medicationsDialog.findViewById(R.id.btnSaveMed);
		btnSaveMed.setText("Search");
		tvDTittle.setText("Add Medication");

		btnSaveMed.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String med = etEditMed.getText().toString().trim();
				if(med.isEmpty()){
					etEditMed.setError("Please enter the medication name");
					return;
				}
				medicationsDialog.dismiss();
				showDrugDialog(med,false,0);
			}
		});

		medicationsDialog.findViewById(R.id.btnCancelMed).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				medicationsDialog.dismiss();
			}
		});
		medicationsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				medicationsDialog.dismiss();
			}
		});



		medicationsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(medicationsDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		medicationsDialog.show();
		medicationsDialog.getWindow().setAttributes(lp);
	}


	ListView lvDrugs;
	ArrayList<DrugBean> drugBeans;
	public void showDrugDialog(String drugName, final boolean isEdit, final int listPos){
		final Dialog medicationsDialog = new Dialog(activity);
		medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		medicationsDialog.setContentView(R.layout.dialog_drug_list);

		final EditText etSearchQuery = (EditText) medicationsDialog.findViewById(R.id.etSearchQuery);
		ImageView ivSearchQuery = (ImageView) medicationsDialog.findViewById(R.id.ivSearchQuery);
		lvDrugs = (ListView) medicationsDialog.findViewById(R.id.lvDrugs);

		medicationsDialog.findViewById(R.id.ivClose).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				medicationsDialog.dismiss();
			}
		});


		ivSearchQuery.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				searchMedication(etSearchQuery);
			}
		});
		etSearchQuery.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					searchMedication(etSearchQuery);
					//return true; return true not closes keyboard
					return false;
				}
				return false;
			}
		});

		medicationsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
					lvMedications.setExpanded(true);
					etMedicationsAddMedication.setText("");
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});

		lvDrugs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if(medicationList == null){
					medicationList = new ArrayList<>();
				}
				if(isEdit){
					medicationList.remove(listPos);
					medicationList.add(listPos,drugBeans.get(position).getDrug_name());
				}else {
					medicationList.add(drugBeans.get(position).getDrug_name());
				}
				medicationsDialog.dismiss();
			}
		});

		medicationsDialog.show();

		etSearchQuery.setText(drugName);
		etSearchQuery.setSelection(etSearchQuery.getText().toString().length());
		RequestParams params = new RequestParams();
		params.put("keyword", drugName);
		ApiManager apiManager = new ApiManager(ApiManager.GET_DRUGS,"post",params,apiCallBack, activity);
		apiManager.loadURL();
	}

	private void searchMedication(EditText etSearchQuery){
		if (etSearchQuery.getText().toString().trim().isEmpty() || etSearchQuery.getText().toString().trim().length()==1) {
			Toast.makeText(activity, "Please enter at least 2 characters of a medication name to search the medication", Toast.LENGTH_LONG).show();
			etSearchQuery.setError("Please enter at least 2 characters of a medication name to search the medication");
		} else {
			RequestParams params = new RequestParams();
			params.put("keyword", etSearchQuery.getText().toString().trim());
			ApiManager apiManager = new ApiManager(ApiManager.GET_DRUGS,"post",params,apiCallBack, activity);
			apiManager.loadURL();
		}
	}


	public void showAskMedicationDialog(String medicationName){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_ask_medic);
		dialogSupport.setCancelable(false);

		Button btnSearchMed = dialogSupport.findViewById(R.id.btnSearchMed);
		Button btnAddMed = dialogSupport.findViewById(R.id.btnAddMed);
		Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

		TextView tvMedName = dialogSupport.findViewById(R.id.tvMedName);
		tvMedName.setText(medicationName);

		btnSearchMed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				showDrugDialog(medicationName,false,0);
			}
		});
		btnAddMed.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();
				medicationList.add(medicationName);
				lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
				lvMedications.setExpanded(true);
				etMedicationsAddMedication.setText("");
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogSupport.show();
	}

	//====================Medications End========================================================

	//===========Allergies view/add dialogs==================================================
	void showAllergiesDialog(){
		Dialog allergiesDialog = new Dialog(activity, R.style.TransparentThemeH4B);
		allergiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		allergiesDialog.setContentView(R.layout.lay_view_allergies);
		ListView lvAllergiesDialog = (ListView) allergiesDialog.findViewById(R.id.lvAllergiesDialog);
		TextView tvNoData = allergiesDialog.findViewById(R.id.tvNoData);
		ImageView ivClose = allergiesDialog.findViewById(R.id.ivClose);

		ivClose.setOnClickListener(v -> allergiesDialog.dismiss());

		if(allergiesList != null){
			lvAllergiesDialog.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
			int v = allergiesList.isEmpty() ? View.VISIBLE : View.GONE;
			tvNoData.setVisibility(v);
		}else {
			tvNoData.setVisibility(View.VISIBLE);
		}

		allergiesDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					lvAllergies.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
					lvAllergies.setExpanded(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(allergiesDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		allergiesDialog.show();
		allergiesDialog.getWindow().setAttributes(lp);
	}

	public void addAllergiesDialog(){
		final Dialog allergiesDialog = new Dialog(activity);
		allergiesDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		allergiesDialog.setContentView(R.layout.dialog_add_allergy);
		allergiesDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = allergiesDialog.findViewById(R.id.tvDTittle);
		final EditText etEditAllergy = (EditText) allergiesDialog.findViewById(R.id.etEditAllergy);
		Button btnSaveAllergy = allergiesDialog.findViewById(R.id.btnSaveAllergy);

		tvDTittle.setText("Add Allergies");

		btnSaveAllergy.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String allegy = etEditAllergy.getText().toString();
				if(allegy.isEmpty()){
					etEditAllergy.setError("Please enter the allergies");
					return;
				}
				allergiesDialog.dismiss();

				allergiesList.add(allegy);
				lvAllergies.setAdapter(new HistoryAllergyAdapter(activity,allergiesList));
				lvAllergies.setExpanded(true);
			}
		});

		allergiesDialog.findViewById(R.id.btnCancelAllergy).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				allergiesDialog.dismiss();
			}
		});
		allergiesDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				allergiesDialog.dismiss();
			}
		});



		allergiesDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(allergiesDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		allergiesDialog.show();
		allergiesDialog.getWindow().setAttributes(lp);
	}

	//===========Allergies view/add dialogs Ends==================================================


	//=====================Hospitalization view/add===============================================

	void showHospitalizationDialog(){
		Dialog hosptDialog = new Dialog(activity, R.style.TransparentThemeH4B);
		hosptDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		hosptDialog.setContentView(R.layout.lay_view_hospitalization);
		ListView lvHosptDialog = (ListView) hosptDialog.findViewById(R.id.lvHosptDialog);
		TextView tvNoData = hosptDialog.findViewById(R.id.tvNoData);
		ImageView ivClose = hosptDialog.findViewById(R.id.ivClose);

		ivClose.setOnClickListener(v -> hosptDialog.dismiss());

		if(hosptalizationList != null){
			lvHosptDialog.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
			int v = hosptalizationList.isEmpty() ? View.VISIBLE : View.GONE;
			tvNoData.setVisibility(v);
		}else {
			tvNoData.setVisibility(View.VISIBLE);
		}

		hosptDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				try {
					lvHosptalization.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
					lvHosptalization.setExpanded(true);
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		});
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(hosptDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		hosptDialog.show();
		hosptDialog.getWindow().setAttributes(lp);
	}

	public void addHospDialog(){
		final Dialog hospDialog = new Dialog(activity);
		hospDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		hospDialog.setContentView(R.layout.dialog_edit_hosp);
		hospDialog.setCanceledOnTouchOutside(false);

		TextView tvDTittle = hospDialog.findViewById(R.id.tvDTittle);
		final EditText etAddHosp = (EditText) hospDialog.findViewById(R.id.etAddHosp);
		Button btnSaveHosp = hospDialog.findViewById(R.id.btnSaveHosp);

		tvDTittle.setText("Add Hospitalization");

		btnSaveHosp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String hosp = etAddHosp.getText().toString();
				if(hosp.isEmpty()){
					etAddHosp.setError("Please enter the hospitalization");
					return;
				}

				hospDialog.dismiss();

				hosptalizationList.add(hosp);
				lvHosptalization.setAdapter(new HistoryHosptAdapter(activity,hosptalizationList));
				lvHosptalization.setExpanded(true);

			}
		});

		hospDialog.findViewById(R.id.btnCancelHosp).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hospDialog.dismiss();
			}
		});
		hospDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				hospDialog.dismiss();
			}
		});



		hospDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		//medicationsDialog.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(hospDialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		hospDialog.show();
		hospDialog.getWindow().setAttributes(lp);
	}

	//=====================Hospitalization view/add Ends==================
}
