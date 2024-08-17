package com.app.OnlineCareUS_MA;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.OnlineCareUS_MA.adapter.CategoriesAdapter;
import com.app.OnlineCareUS_MA.model.ConditionsModel;
import com.app.OnlineCareUS_MA.model.SymptomsModel;
import com.app.OnlineCareUS_MA.util.CallWebService;
import com.app.OnlineCareUS_MA.util.CheckInternetConnection;
import com.app.OnlineCareUS_MA.util.CustomToast;
import com.app.OnlineCareUS_MA.util.DATA;
import com.app.OnlineCareUS_MA.util.Database;
import com.app.OnlineCareUS_MA.util.HideShowKeypad;
import com.app.OnlineCareUS_MA.util.OpenActivity;
import com.loopj.android.http.RequestParams;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

public class SearchADoctor extends BaseActivity {
	
	String sympArr[];
	CustomToast customToast;
	OpenActivity openActivity;
	CheckInternetConnection checkInternetConnection;
	CallWebService callWebService;
	HideShowKeypad hideShowKeypad;
	private String selectedSympId = "";
	private String selectedSpecialityId = "";
	
	ProgressDialog pd;
	
	ArrayAdapter<String> symptomsAdapter, conditionsAdapter, specialitiesAdapter;

	CategoriesAdapter categoriesAdapter;
	SharedPreferences prefs;
	
	Activity activity;
	
	Spinner spSelectSymptm,spSelectCondtn;
	Button btnSbmtSymptom;
	EditText etExtraInfo,etZipCode;
	AutoCompleteTextView autoTvSymptoms,autoTvDrSpeciality;
	
	ListView lvSymptoms;
	
	Database db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_a_doctor);
		activity = SearchADoctor.this;
		
		exportDB();
		
		if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(activity,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(activity );
		}

		pd.setMessage("Loading...");
		pd.setCanceledOnTouchOutside(false);
		
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		hideShowKeypad = new HideShowKeypad(activity);
		db = new Database(activity);
		
//		callWebService = new CallWebService(activity, pd);
				
		btnSbmtSymptom = (Button) findViewById(R.id.btnSbmtSymptom);
		
		autoTvSymptoms = (AutoCompleteTextView) findViewById(R.id.autoTvSymptoms);
		autoTvDrSpeciality = (AutoCompleteTextView) findViewById(R.id.autoTvDrSpeciality);
		spSelectCondtn =(Spinner) findViewById(R.id.spSelectCondtn);
		spSelectSymptm =(Spinner) findViewById(R.id.spSelectSymptm);
		
		etExtraInfo = (EditText) findViewById(R.id.etExtraInfo);
		etZipCode = (EditText) findViewById(R.id.etZipCode);

		//symptoms from db....
		DATA.allSymptoms = new ArrayList<SymptomsModel>();
		DATA.allSymptoms = db.getAllSymptoms();
				
		 sympArr = new String[DATA.allSymptoms.size()];
		for(int i = 0; i<DATA.allSymptoms.size(); i++) {
			
			sympArr[i] = DATA.allSymptoms.get(i).symptomName;
			
		}
		symptomsAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, sympArr);
		autoTvSymptoms.setAdapter(symptomsAdapter);
		autoTvSymptoms.setThreshold(1);
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
		});
		//....................................................
		
		//specialities from db................................		
		//DATA.allSpecialities = new ArrayList<SpecialityModel>();
		DATA.allSpecialities = sharedPrefsHelper.getAllSpecialities();//db.getAllSpecialities();
		
		String specialityArr[] = new String[DATA.allSpecialities.size()];
		for(int i = 0; i<DATA.allSpecialities.size(); i++) {
			
			specialityArr[i] = DATA.allSpecialities.get(i).specialityName;
			
		}
		specialitiesAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_dropdown_item_1line, specialityArr);
		autoTvDrSpeciality.setAdapter(specialitiesAdapter);
		autoTvDrSpeciality.setThreshold(1);

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
		
		String condAr[] = {"possible condition"};
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
		
		autoTvDrSpeciality.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
			
					
			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// getActionBar().setHomeButtonEnabled(true);
	

		btnSbmtSymptom.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				pd.show();
				
				RequestParams params = new RequestParams();
				params.put("dummy","");

				callWebService = new CallWebService(activity, pd);
				callWebService.postData("getOnlineDoctors", params);
				
			}
		});
	}

	private void exportDB() {
		try {
		    File sd = Environment.getExternalStorageDirectory();
		    File data = Environment.getDataDirectory();

		    if (sd.canWrite()) {
		        String currentDBPath = "//data//com.app.onlinecare//databases//onlinecareDB.sqlite";
		        String backupDBPath = "onlinecareDB.sqlite";
		        File currentDB = new File(data, currentDBPath);
		        File backupDB = new File(sd, backupDBPath);

		        FileChannel src = new FileInputStream(currentDB).getChannel();
		        FileChannel dst = new FileOutputStream(backupDB).getChannel();
		        dst.transferFrom(src, 0, src.size());
		        src.close();
		        dst.close();
//		        Toast.makeText(activity, backupDB.toString(), Toast.LENGTH_LONG).show();
		    }
		} catch (Exception e) {
		    Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show();
		}
	}

	public void getSelectedSymptomId(String selectedSymptomName) {

			for(int i = 0; i<DATA.allSymptoms.size();i++) {
				if(DATA.allSymptoms.get(i).symptomName.equals(selectedSymptomName)) {					
					selectedSympId = DATA.allSymptoms.get(i).symptomId;
				}
			}
	}
	
	public void getSelectedSpecialityId(String selectedSpecialityName) {

		for(int i = 0; i<DATA.allSymptoms.size();i++) {
			if(DATA.allSpecialities.get(i).specialityName.equals(selectedSpecialityName)) {					
				selectedSympId = DATA.allSymptoms.get(i).symptomId;
			}
		}
}

}
