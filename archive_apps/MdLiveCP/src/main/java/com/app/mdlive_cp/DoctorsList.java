package com.app.mdlive_cp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mdlive_cp.adapters.UsersAdapter;
import com.app.mdlive_cp.api.ApiCallBack;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.model.DoctorsModel;
import com.app.mdlive_cp.util.CheckInternetConnection;
import com.app.mdlive_cp.util.CustomToast;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.Database;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.GloabalSocket;
import com.app.mdlive_cp.util.OpenActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DoctorsList  extends BaseActivity implements ApiCallBack, GloabalSocket.SocketEmitterCallBack{
	
	ListView lvUsersList;TextView tvNoDoctors;
	Button btnDoctors,btnSpecialist;//btnAll
	EditText etZipcode;
	ImageView ivSearchDoc;
	RelativeLayout searchBar;
	
	Activity activity;
	CheckInternetConnection checkInternetConnection;
	SharedPreferences prefs;
	CustomToast customToast;
	OpenActivity openActivity;
	JSONObject jsonObject, userInfoObject, adminObject;
	JSONArray clientAgentArray;
	String msg, status;
	ActionBar ab;
    DoctorsModel temp;

	AsyncHttpClient client;
	AlertDialog dialog;
	//Database db;

	UsersAdapter usersAdapter;
	ProgressDialog pd;

	boolean isFromMyDoctors = false;

	public static final String SELCTED_DR_ID_PREFS_KEY = "selected_dr_id_from_mydoc";


	@Override
	protected void onResume() {
		try {//because if back from ActivityDocToDocNew DATA.allDoctors changed its a bug will not use DATA.allDoctors in ActivityDocToDocNew
			if(DATA.allDoctors!=null){
				usersAdapter = new UsersAdapter(activity);
				lvUsersList.setAdapter(usersAdapter);
			}
		}catch (Exception e){
			e.printStackTrace();
		}

		System.out.println("-- onresume call DoctorList");
		gloabalSocket = new GloabalSocket(activity,this);

		super.onResume();
	}

	GloabalSocket gloabalSocket;

	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctors_list);

		isFromMyDoctors = getIntent().getBooleanExtra("isFromMyDoctors",false);

        activity = DoctorsList.this;

		new Database(activity).deleteNotif(DATA.NOTIF_TYPE_NEW_PATIENT);

        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);
		openActivity = new OpenActivity(activity);
        //db = new Database(activity);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...");
		pd.setCanceledOnTouchOutside(false);
		//prefs = getSharedPreferences("onlinecaredrPrefs", Context.MODE_PRIVATE);
		client = new AsyncHttpClient();

//		ab = getSupportActionBar(); 
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4b6162"));     
//		ab.setBackgroundDrawable(colorDrawable);
		        
        lvUsersList = (ListView) findViewById(R.id.lvUsersList);
        tvNoDoctors = (TextView) findViewById(R.id.tvNoDoctors);
		ivSearchDoc = (ImageView) findViewById(R.id.ivSearchDoc);
		searchBar = (RelativeLayout) findViewById(R.id.searchBar);

		//       DATA.allDoctors = new ArrayList<DoctorsModel>();
        
//            temp = new DoctorsModel();
//        	temp.fName = "Dr. Henry";
//        	temp.lName = "Jacobs";        	
//        	DATA.allDoctors.add(temp);        	
//        	temp = null;
//
//            temp = new DoctorsModel();
//        	temp.fName = "Dr. Victor";
//        	temp.lName = "Foster";        	
//        	DATA.allDoctors.add(temp);        	
//        	temp = null;
//
//            temp = new DoctorsModel();
//        	temp.fName = "Dr. Side";
//        	temp.lName = "Johnsan";        	
//        	DATA.allDoctors.add(temp);        	
//        	temp = null;
        /*if (DATA.allDoctors != null) {
        	if (DATA.allDoctors.size() == 0) {
    			tvNoDoctors.setVisibility(View.VISIBLE);
    		} else {
    			tvNoDoctors.setVisibility(View.GONE);
    		}
            usersAdapter = new UsersAdapter(activity);
            lvUsersList.setAdapter(usersAdapter);
		} else {
			tvNoDoctors.setVisibility(View.VISIBLE);
		}*/
        etZipcode = (EditText) findViewById(R.id.etZipcode);

        if (checkInternetConnection.isConnectedToInternet()) {
			//getAllDrs();

			if (isFromMyDoctors){
				getSupportActionBar().setTitle("My Doctors");
				//searchBar.setVisibility(View.GONE);
				mydoctors();
			}else {
				//searchBar.setVisibility(View.VISIBLE);
				nurseDoctorsSearch("doctor","");
			}
		} else {
			Toast.makeText(activity, "Please check internet connection", Toast.LENGTH_SHORT).show();
		}
        
        btnDoctors = (Button) findViewById(R.id.btnDoctors);
        btnSpecialist = (Button) findViewById(R.id.btnSpecialist);
        //btnAll = (Button) findViewById(R.id.btnAll);
        
        /*btnDoctors.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					btnDoctors.setBackgroundColor(getResources().getColor(R.color.theme_red));
					btnDoctors.setTextColor(getResources().getColor(android.R.color.white));
					btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
					btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));
					//btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
					//btnAll.setTextColor(getResources().getColor(R.color.theme_red));
					getAllDrs("doctor");
				} else {
					Toast.makeText(activity, "Please check internet connection", 0).show();
				}
			}
		});
        
        btnSpecialist.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
					btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
					btnSpecialist.setBackgroundColor(getResources().getColor(R.color.theme_red));
					btnSpecialist.setTextColor(getResources().getColor(android.R.color.white));
					//btnAll.setBackgroundColor(getResources().getColor(android.R.color.white));
					//btnAll.setTextColor(getResources().getColor(R.color.theme_red));
					getAllDrs("specialist");
				} else {
					Toast.makeText(activity, "Please check internet connection", 0).show();
				}
			}
		});*/
        
        /*btnAll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (checkInternetConnection.isConnectedToInternet()) {
					btnAll.setBackgroundColor(getResources().getColor(R.color.theme_red));
					btnAll.setTextColor(getResources().getColor(android.R.color.white));
					btnDoctors.setBackgroundColor(getResources().getColor(android.R.color.white));
					btnDoctors.setTextColor(getResources().getColor(R.color.theme_red));
					btnSpecialist.setBackgroundColor(getResources().getColor(android.R.color.white));
					btnSpecialist.setTextColor(getResources().getColor(R.color.theme_red));
					
					getAllDrs();
				} else {
					Toast.makeText(activity, "Please check internet connection", 0).show();
				}
			}
		});*/
        
        etZipcode.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (usersAdapter != null) {
					usersAdapter.filter(arg0.toString());
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		/*ivSearchDoc.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (etZipcode.getText().toString().isEmpty()){
					etZipcode.setError("Please enter a doctor name or zipcode");
				}else {
					if (checkInternetConnection.isConnectedToInternet()){
						nurseDoctorsSearch("doctor",etZipcode.getText().toString());
					}else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,1);
					}
				}
			}
		});*/

        lvUsersList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {

				//Toast.makeText(activity, "Sorry, Further section is under development",  1).show();
				
				if(false){//DATA.referToSpecialist
					DATA.selectedDrName = DATA.allDoctors.get(position).fName + " " + DATA.allDoctors.get(position).lName;
					String specialistId = DATA.allDoctors.get(position).id;
					
					//referToSpecialist(DATA.selectedUserAppntID, specialistId);
					
				}else {
					DATA.selectedDrIdForNurse = DATA.allDoctors.get(position).id;
					DATA.selectedDrId = DATA.allDoctors.get(position).id;

					prefs.edit().putString(SELCTED_DR_ID_PREFS_KEY, DATA.allDoctors.get(position).id).commit();
					
					//DATA.selectedUserCallId = DATA.allDoctors.get(position).id;
					
					DATA.selectedDrName = DATA.allDoctors.get(position).fName + " " + DATA.allDoctors.get(position).lName;
					DATA.selectedDrQbId = DATA.allDoctors.get(position).qb_id;
					DATA.selectedDrImage = DATA.allDoctors.get(position).image;
					DATA.selectedDrQualification = DATA.allDoctors.get(position).qualification;
					
					DATA.selectedDoctorsModel = DATA.allDoctors.get(position);
					
					DATA.isFromDocToDoc = true;
					
					Intent intent = new Intent(activity,SelectedDoctorsProfile.class);
					intent.putExtra("isFromMyDoctors",isFromMyDoctors);
					startActivity(intent);
					//finish();

				}

			}
		});

    }// end oncreate
    
    //ArrayList<DoctorsModel> doctorsModels;
    public void getAllDrs() {
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		pd.show();
		client.post(DATA.baseUrl+"/searchAllDoctors/", params, new AsyncHttpResponseHandler() {
			//client.get("https://onlinecare.com/dev/index.php/app/alldoctors", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--responce in searchAllDoctors: "+content);

					try {
						JSONArray data = new JSONArray(content);
						DATA.allDoctors = new ArrayList<DoctorsModel>();
						DoctorsModel temp = null;

						if (data.length() == 0) {
							//showMessageBox(activity, "We are sorry", "Currently no doctors available");
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

								temp.listdoc=object.getString("listdoc");

								if (!temp.id.equalsIgnoreCase(prefs.getString("id", ""))) {
									DATA.allDoctors.add(temp);
								}
								temp = null;
							}

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
							usersAdapter = new UsersAdapter(activity);
							lvUsersList.setAdapter(usersAdapter);

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: searchAllDoctors, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure searchAllDoctors: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end getAllDrs
    
    
    
    
    public void nurseDoctorsSearch(String user_type,String zipcode) {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		pd.show();
		
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("user_type", user_type);
		if (!zipcode.isEmpty()){
			params.put("zipcode", zipcode);
		}
		System.out.println("-- params in nurseDoctorsSearch: "+params.toString());
		/*if (etZipcode.getText().toString().isEmpty()) {
			
		} else {
			params.put("zipcode", etZipcode.getText().toString());
		}*/
		
		client.post(DATA.baseUrl+"/nurseDoctorsSearch/", params, new AsyncHttpResponseHandler() {
			//client.get("https://onlinecare.com/dev/index.php/app/alldoctors", new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				pd.dismiss();
				try{
					String content = new String(response);

					System.out.println("--responce in nurseDoctorsSearch: "+content);

					try {
						JSONArray data = new JSONArray(content);
						DATA.allDoctors = new ArrayList<DoctorsModel>();
						DoctorsModel temp = null;

						if (data.length() == 0) {
							//showMessageBox(activity, "We are sorry", "Currently no doctors available");
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
								//temp.speciality_name=object.getString("speciality_name");

								temp.listdoc=object.getString("listdoc");
								DATA.allDoctors.add(temp);
								temp = null;
							}

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
							usersAdapter = new UsersAdapter(activity);
							lvUsersList.setAdapter(usersAdapter);

						}


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					System.out.println("-- responce onsuccess: nurseDoctorsSearch, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				pd.dismiss();
				try {
					String content = new String(errorResponse);
					System.out.println("--responce in failure nurseDoctorsSearch: "+content);
					new GloabalMethods(activity).checkLogin(content);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end nurseDoctorsSearch

	public void mydoctors() {
		RequestParams params = new RequestParams();
		params.put("my_id", prefs.getString("id", ""));


		ApiManager apiManager = new ApiManager(ApiManager.MY_DOCTORS,"post",params,this, activity);
		apiManager.loadURL();

	}//end mydoctors

	@Override
	public void fetchDataCallback(String status, String apiName, String content) {
		if(apiName.equalsIgnoreCase(ApiManager.MY_DOCTORS)){
			try {
				JSONObject jsonObject = new JSONObject(content);
				JSONArray data = jsonObject.getJSONArray("doctors");
				DATA.allDoctors = new ArrayList<DoctorsModel>();
				DoctorsModel temp = null;

				if (data.length() == 0) {
					//showMessageBox(activity, "We are sorry", "Currently no doctors available");
				}else{
					for (int i = 0; i < data.length(); i++) {
						temp = new DoctorsModel();
						JSONObject object = data.getJSONObject(i);
						temp.id = object.getString("id");
						temp.latitude = "0.0";//object.getString("latitude");
						temp.longitude = "0.0";//=object.getString("longitude");
						temp.zip_code="";//object.getString("zip_code");
						temp.fName=object.getString("first_name");
						temp.lName=object.getString("last_name");
						temp.is_online= object.getString("is_online");
						temp.image=object.getString("image");
						temp.designation="";//object.getString("designation");


						if (temp.latitude.equalsIgnoreCase("null")) {
							temp.latitude = "0.0";
						}
						if (temp.longitude.equalsIgnoreCase("null")) {
							temp.longitude = "0.0";
						}

						temp.speciality_id="";//object.getString("speciality_id");
						temp.current_app=object.getString("current_app");
						temp.speciality_name="";//object.getString("speciality_name");

						temp.listdoc=object.getString("listdoc");
						DATA.allDoctors.add(temp);
						temp = null;
					}

					/*if (checkGooglePlayservices()) {
						initilizeMap(latLongBeansList);
					}*/
					usersAdapter = new UsersAdapter(activity);
					lvUsersList.setAdapter(usersAdapter);

				}


			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public void onSocketCallBack(String emitterResponse) {

		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("doctor")){
				for (int i = 0; i < DATA.allDoctors.size(); i++) {
					if(DATA.allDoctors.get(i).id.equalsIgnoreCase(id)){
						if(status.equalsIgnoreCase("login")){
							DATA.allDoctors.get(i).is_online = "1";
						}else if(status.equalsIgnoreCase("logout")){
							DATA.allDoctors.get(i).is_online = "0";
						}
					}
				}
				usersAdapter.notifyDataSetChanged();
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
    
    /*private void referToSpecialist(String liveCheckupId , String specialistId) {

		pd.show();

		RequestParams params = new RequestParams();
		params.put("live_checkup_id", liveCheckupId);
		params.put("ref_doctor_id", specialistId);
		if(DATA.onlyReports.equals("1")){
			params.put("only_reports", "1");
			
		}else{
		params.put("only_reports", "0");
		}

		client.get(DATA.baseUrl+"referToSpecialist", params, new AsyncHttpResponseHandler() {
			
			@Override
			@Deprecated
			public void onFailure(int statusCode, Throwable error,
					String content) {
				super.onFailure(statusCode, error, content);
				System.out.println("--on fail referToSpecialist: "+content);
				DATA.referToSpecialist = false;
				pd.dismiss();

			}
			@Override
			public void onSuccess(String response) {
				System.out.println("--online care response in refer patients to secialist: "+response);

				pd.dismiss();
				
				if(response.equals(null)){
					Toast.makeText(activity, "Internal server error", Toast.LENGTH_SHORT).show();
				}else {
					try {
						JSONObject jobj = new JSONObject(response);
						String status = jobj.getString("status");
						String msg = jobj.getString("msg");
						if(status.equals("success")){
							AlertDialog.Builder builder = new Builder(activity);
							builder.setTitle(status);
							builder.setMessage(msg+" "+DATA.selectedDrName);
							builder.setPositiveButton("OK",new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									dialog.dismiss();
									startActivity(new Intent(activity , MainActivityNew.class));
									DATA.referToSpecialist = false;
								}
							});
							  dialog = builder.create();
							  dialog.show();
							
							
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				
			}
		});
	}*/

 }