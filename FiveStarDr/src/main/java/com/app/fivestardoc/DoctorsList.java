package com.app.fivestardoc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.fivestardoc.adapter.UsersAdapter;
import com.app.fivestardoc.api.ApiManager;
import com.app.fivestardoc.model.DoctorsModel;
import com.app.fivestardoc.util.CheckInternetConnection;
import com.app.fivestardoc.util.DATA;
import com.app.fivestardoc.util.GloabalMethods;
import com.app.fivestardoc.util.GloabalSocket;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class DoctorsList  extends BaseActivity implements GloabalSocket.SocketEmitterCallBack{
	
	ListView lvUsersList;TextView tvNoDoctors,tvNoData;
	Button btnDoctors,btnSpecialist;//btnAll
	EditText etZipcode;
	
	Activity activity;
	SharedPreferences prefs;
	CheckInternetConnection checkInternetConnection;
	JSONObject jsonObject, userInfoObject, adminObject;
	JSONArray clientAgentArray;
	String msg, status;
	ActionBar ab;
    DoctorsModel temp;

	AsyncHttpClient client;
	AlertDialog dialog;
	//Database db;

	UsersAdapter usersAdapter;
	//ProgressDialog pd;
	LinearLayout tabOptions;
	boolean isFromReffer = false;
	public static boolean isFromPCPRefer = false;

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

        activity = DoctorsList.this;
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        checkInternetConnection = new CheckInternetConnection(activity);

		gloabalSocket = new GloabalSocket(activity,this);

        //db = new Database(activity);
        /*if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB){
			pd = new ProgressDialog(this,ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT );
		}else{
			pd = new ProgressDialog(this);
		}
		pd.setMessage("Please wait...");
		pd.setCanceledOnTouchOutside(false);*/
		//prefs = getSharedPreferences("onlinecaredrPrefs", Context.MODE_PRIVATE);
		client = new AsyncHttpClient();

//		ab = getSupportActionBar(); 
//		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//		ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4b6162"));     
//		ab.setBackgroundDrawable(colorDrawable);
		        
        lvUsersList = (ListView) findViewById(R.id.lvUsersList);
        tvNoDoctors = (TextView) findViewById(R.id.tvNoDoctors);
		tvNoData = (TextView) findViewById(R.id.tvNoData);

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
     
        
        isFromReffer = getIntent().getBooleanExtra("isFromReffer", false);
        isFromPCPRefer = getIntent().getBooleanExtra("isFromPCPRefer", false);
        
        tabOptions = (LinearLayout) findViewById(R.id.tabOptions);
        etZipcode = (EditText) findViewById(R.id.etZipcode);
        
        if (isFromReffer) {
        	tabOptions.setVisibility(View.GONE);
            etZipcode.setVisibility(View.GONE);
            
            if (DATA.allDoctors != null) {
            	if (DATA.allDoctors.size() == 0) {
        			tvNoDoctors.setVisibility(View.VISIBLE);
        		} else {
        			tvNoDoctors.setVisibility(View.GONE);
        		}
                usersAdapter = new UsersAdapter(activity);
                lvUsersList.setAdapter(usersAdapter);
    		} else {
    			tvNoDoctors.setVisibility(View.VISIBLE);
    		}
            
		} else {
			tabOptions.setVisibility(View.VISIBLE);
            etZipcode.setVisibility(View.VISIBLE);
            
            if (checkInternetConnection.isConnectedToInternet()) {
    			//getAllDrs();
            	getAllDrs("doctor");
    		} else {
    			Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE , Toast.LENGTH_SHORT).show();
    		}
		}
        
        
        
        btnDoctors = (Button) findViewById(R.id.btnDoctors);
        btnSpecialist = (Button) findViewById(R.id.btnSpecialist);
        //btnAll = (Button) findViewById(R.id.btnAll);
        
        btnDoctors.setOnClickListener(new OnClickListener() {
			
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
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
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
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});
        
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
        
        
       /* if (checkInternetConnection.isConnectedToInternet()) {
			getAllDrs("doctor");
		} else {
			Toast.makeText(activity, "Please check internet connection", 0).show();
		}*/
        
        etZipcode.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {}
			@Override
			public void afterTextChanged(Editable arg0) {
				if (usersAdapter != null) {
					usersAdapter.filter(arg0.toString());
				}
			}
		});

        lvUsersList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

				//Toast.makeText(activity, "Sorry, Further section is under development",  1).show();
				
				if(DATA.referToSpecialist){
					DATA.selectedDrName = DATA.allDoctors.get(position).fName + " " + DATA.allDoctors.get(position).lName;
					final String specialistId = DATA.allDoctors.get(position).id;
					
					
					
					AlertDialog.Builder b = new Builder(activity);
					b.setTitle("Refer to specialist");
					b.setMessage("Are you sure? You want to refer the patient "+DATA.selectedUserCallName+" to specialist "+DATA.selectedDrName);
					b.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							referToSpecialist(DATA.selectedUserAppntID, specialistId);
						}
					});
					b.setNegativeButton("No", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							
						}
					});
					AlertDialog d = b.create();
					d.show();
					
				}else {
					DATA.selectedDrId = DATA.allDoctors.get(position).id;
					
					//DATA.selectedUserCallId = DATA.allDoctors.get(position).id;
					
					DATA.selectedDrName = DATA.allDoctors.get(position).fName + " " + DATA.allDoctors.get(position).lName;
					DATA.selectedDrQbId = DATA.allDoctors.get(position).qb_id;
					DATA.selectedDrImage = DATA.allDoctors.get(position).image;
					DATA.selectedDrQualification = DATA.allDoctors.get(position).qualification;
					
					DATA.isFromDocToDoc = true;
					
					DATA.selectedDoctorsModel = DATA.allDoctors.get(position);
					
					Intent intent = new Intent(activity,SelectedDoctorsProfile.class);
					startActivity(intent);
					//finish();

				}

			}
		});

    }// end oncreate
    
    private void referToSpecialist(String liveCheckupId , String specialistId) {

		//pd.show();
		DATA.showLoaderDefault(activity,"");

		RequestParams params = new RequestParams();
		params.put("live_checkup_id", liveCheckupId);
		params.put("ref_doctor_id", specialistId);
		if(DATA.onlyReports.equals("1")){
			params.put("only_reports", "1");
			
		}else{
		params.put("only_reports", "0");
		}

		String reqURL = DATA.baseUrl+"referToSpecialist";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		ApiManager.addHeader(activity, client);

		client.get(reqURL, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--online care response in refer patients to secialist: "+content);

					if(response.equals(null)){
						Toast.makeText(activity, DATA.CMN_ERR_MSG, Toast.LENGTH_SHORT).show();
					}else {
						try {
							JSONObject jobj = new JSONObject(content);
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
										//startActivity(new Intent(activity , MainActivityNew.class));
										DATA.referToSpecialist = false;
										finish();
									}
								});
								dialog = builder.create();
								dialog.show();

								dialog.setOnDismissListener(new OnDismissListener() {

									@Override
									public void onDismiss(DialogInterface arg0) {
										// TODO Auto-generated method stub
										DATA.referToSpecialist = false;
										finish();
									}
								});


							}else{
								AlertDialog alertDialog =
										new AlertDialog.Builder(activity).setTitle("Info")
												.setMessage(msg)
												.setPositiveButton("Done",null).create();
								alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
									@Override
									public void onDismiss(DialogInterface dialog) {

									}
								});
								alertDialog.show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						}
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
				DATA.referToSpecialist = false;

				DATA.dismissLoaderDefault();

				try {
					String content = new String(errorResponse);
					DATA.print("-- onfailure : "+reqURL+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
	}
    
    
    public void getAllDrs() {
		DATA.showLoaderDefault(activity,"");
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);

		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));

		String reqURL = DATA.baseUrl+"/searchAllDoctors/";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());

		client.post(reqURL,params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--responce in getAllDrs: "+content);

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
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end getAllDrs
    
    public void getAllDrs(String user_type) {
		 
		AsyncHttpClient client = new AsyncHttpClient();

		ApiManager.addHeader(activity,client);


		DATA.showLoaderDefault(activity,"");
		
		RequestParams params = new RequestParams();
		params.put("doctor_id", prefs.getString("id", ""));
		params.put("user_type", user_type);
		
		/*if (etZipcode.getText().toString().isEmpty()) {
			
		} else {
			params.put("zipcode", etZipcode.getText().toString());
		}*/

		String reqURL = DATA.baseUrl+"/searchAllDoctors_/";

		DATA.print("-- Request : "+reqURL);
		DATA.print("-- params : "+params.toString());
		
		client.post(reqURL, params, new AsyncHttpResponseHandler() {


			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--responce in getAllDrs: "+content);

					try {
						JSONArray data = new JSONArray(content);
						DATA.allDoctors = new ArrayList<DoctorsModel>();
						DoctorsModel temp = null;

						if (data.length() == 0) {
							//showMessageBox(activity, "We are sorry", "Currently no doctors available");
							tvNoData.setVisibility(View.VISIBLE);
							usersAdapter = new UsersAdapter(activity);
							lvUsersList.setAdapter(usersAdapter);
						}else{
							tvNoData.setVisibility(View.GONE);

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
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end getAllDrs

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
 }