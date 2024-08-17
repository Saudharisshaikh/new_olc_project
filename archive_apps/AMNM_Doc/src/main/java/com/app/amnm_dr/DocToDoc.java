package com.app.amnm_dr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.app.amnm_dr.api.ApiManager;
import com.app.amnm_dr.model.DoctorsModel;
import com.app.amnm_dr.model.SpecialityModel;
import com.app.amnm_dr.util.CallWebService;
import com.app.amnm_dr.util.CheckInternetConnection;
import com.app.amnm_dr.util.CustomToast;
import com.app.amnm_dr.util.DATA;
import com.app.amnm_dr.util.Database;
import com.app.amnm_dr.util.GloabalMethods;
import com.app.amnm_dr.util.OpenActivity;
import com.app.amnm_dr.util.SharedPrefsHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DocToDoc extends AppCompatActivity{

	
	//http://3melements.com/onlineclinic/app/getClinics
	//service for all clinic
	
	Activity activity;
	ImageView imcCelv,imgHenry,imgKaled,imgMayo,imgRoswell;
	OpenActivity openActivity;
	Spinner spinnerSpecialities;
	
	
	
	Database db;
	JSONObject jsonObject;
	JSONArray doctorsArray, drSheduleArray;

	String msg, status;

	//String specialitesNames[];
	
	CallWebService callWebService;
	AsyncHttpClient client;	

	CustomToast customToast;
	CheckInternetConnection checkInternetConnection;
	//ProgressDialog pd;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.doc_to_doc);
		
		activity = DocToDoc.this;
		
		
		db = new Database(activity);
		db.createDatabase();

		openActivity = new OpenActivity(activity);
		customToast = new CustomToast(activity);
		checkInternetConnection = new CheckInternetConnection(activity);
		
		/*if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...");
		pd.setCanceledOnTouchOutside(false);*/

		imcCelv = (ImageView) findViewById(R.id.imcCelv);
		imgHenry = (ImageView) findViewById(R.id.imgHenry);
		imgKaled = (ImageView) findViewById(R.id.imgKaled);
		imgMayo = (ImageView) findViewById(R.id.imgMayo);
		imgRoswell = (ImageView) findViewById(R.id.imgRoswell);

		openActivity = new OpenActivity(activity);

		imcCelv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DATA.drClinicId = "1";
				
				if(DATA.drSpecialityId.equals("0")){
					customToast.showToast("Please select speciality", 0, 0);
				}else{
					getDoctors();
					
				}

				 
			}
		});
		imgHenry.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				DATA.drClinicId = "2";
				if(DATA.drSpecialityId.equals("0")){
					customToast.showToast("Please select speciality", 0, 0);
				}else{
					getDoctors();
					
				}
			}
		});

		imgKaled.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				DATA.drClinicId = "3";
				if(DATA.drSpecialityId.equals("0")){
					customToast.showToast("Please select speciality", 0, 0);
				}else{
					getDoctors();
					
				}
			}
		});

		imgMayo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DATA.drClinicId = "4";
				if(DATA.drSpecialityId.equals("0")){
					customToast.showToast("Please select speciality", 0, 0);
				}else{
					getDoctors();
					
				}

			}
		});

		imgRoswell.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DATA.drClinicId = "5";
				if(DATA.drSpecialityId.equals("0")){
					customToast.showToast("Please select speciality", 0, 0);
				}else{
					getDoctors();
					
				}

			}
		});

		
		spinnerSpecialities = (Spinner) findViewById(R.id.spinnerSpecility);
		//DATA.allSpecialities = new ArrayList<SpecialityModel>();
		
		DATA.allSpecialities = SharedPrefsHelper.getInstance().getAllSpecialities();//db.getAllSpecialities();
		
//		specialitesNames = new String[DATA.allSpecialities.size()];
//		
//		for(int i = 0 ; i<specialitesNames.length; i++) {
//
//			specialitesNames[i] = DATA.allSpecialities.get(i).specialityName;
//		}
		
		ArrayAdapter<SpecialityModel> specialitiesArray = new ArrayAdapter<SpecialityModel>(activity, android.R.layout.simple_dropdown_item_1line, DATA.allSpecialities);
		spinnerSpecialities.setAdapter(specialitiesArray);
		
		spinnerSpecialities.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				//customToast.showToast(DATA.allSpecialities.get(position).specialityName, 0, 0);
				
				DATA.drSpecialityId = DATA.allSpecialities.get(position).specialityId;

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				 
				
			}
		});
		
		
	}
	
	
	
	
	
	public void getDoctors() {

		DATA.showLoaderDefault(activity,"");

		client = new AsyncHttpClient();

		ApiManager.addHeader(activity, client);
		
		RequestParams params = new RequestParams();
		//params.put("zipcode","123");
		
		params.put("clinic_id",DATA.drClinicId);
		params.put("speciality_id",DATA.drSpecialityId);


		//client.get(DATA.baseUrl+"searchDoctorByZipCode", params, new AsyncHttpResponseHandler() {

		String reqURL = DATA.baseUrl+"getDoctorsByClinic";

		System.out.println("-- Request : "+reqURL);
		System.out.println("-- params : "+params.toString());

		client.get(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					System.out.println("--Response in clinic drs: "+content);

					// remove the progress bar view
//				refreshMenuItem.setActionView(null);

					try {
						jsonObject = new JSONObject(content);

						String status = jsonObject.getString("status");

						if(status.equals("success")) {

							String drsStr = jsonObject.getString("doctors");

							doctorsArray = new JSONArray(drsStr);

							DATA.allDoctors = new ArrayList<DoctorsModel>();

							DoctorsModel temp;

							for(int i = 0; i<doctorsArray.length(); i++) {

								temp = new DoctorsModel();

								temp.id = doctorsArray.getJSONObject(i).getString("id");
								temp.qb_id = doctorsArray.getJSONObject(i).getString("qb_id");
								temp.fName = doctorsArray.getJSONObject(i).getString("first_name");
								temp.lName = doctorsArray.getJSONObject(i).getString("last_name");
								temp.email = doctorsArray.getJSONObject(i).getString("email");
								temp.qualification = doctorsArray.getJSONObject(i).getString("qualification");
								temp.image = doctorsArray.getJSONObject(i).getString("image");
								temp.careerData = doctorsArray.getJSONObject(i).getString("introduction");
								temp.designation = doctorsArray.getJSONObject(i).getString("designation");

								temp.current_app=doctorsArray.getJSONObject(i).getString("current_app");
								temp.zip_code=doctorsArray.getJSONObject(i).getString("zip_code");

								temp.is_online=doctorsArray.getJSONObject(i).getString("is_online");

								System.out.println("--online care callwebservice getOnline Doctors fname: "+temp.fName);

								DATA.allDoctors.add(temp);
								System.out.println("--size "+DATA.allDoctors.size()+"");
								temp = null;

							}


						}
						else {
							customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
						}
					} catch (JSONException e) {
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
					}

					Intent intent = new Intent(DocToDoc.this, DoctorsList.class);
					intent.putExtra("isFromReffer", true);
					startActivity(intent);


				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: "+reqURL+", http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					System.out.println("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

		});
	}//end getDoctors

}
