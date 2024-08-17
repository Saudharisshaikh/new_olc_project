package com.app.OnlineCareUS_Pt;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.app.OnlineCareUS_Pt.adapter.LvLivecareDoctorsAdapter;
import com.app.OnlineCareUS_Pt.adapter.NearbyHospitalsAdapter;
import com.app.OnlineCareUS_Pt.api.ApiManager;
import com.app.OnlineCareUS_Pt.model.HospitalsBean;
import com.app.OnlineCareUS_Pt.model.NearbyDoctorBean;
import com.app.OnlineCareUS_Pt.services.GPSTracker;
import com.app.OnlineCareUS_Pt.util.CheckInternetConnection;
import com.app.OnlineCareUS_Pt.util.CustomToast;
import com.app.OnlineCareUS_Pt.util.DATA;
import com.app.OnlineCareUS_Pt.util.GloabalMethods;
import com.app.OnlineCareUS_Pt.util.GloabalSocket;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;
import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class MapActivity extends AppCompatActivity implements GloabalSocket.SocketEmitterCallBack {

	public static Activity activity;
	CheckInternetConnection connection;
	ListView lvDoctors;
	EditText etSearchDoc;

	ArrayList<NearbyDoctorBean> latLongBeansList;
	LvLivecareDoctorsAdapter lvLivecareDoctorsAdapter;
	SharedPreferences prefs;
	CustomToast customToast;

	GloabalSocket gloabalSocket;

	@Override
	protected void onDestroy() {
		gloabalSocket.offSocket();
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId() == android.R.id.home){
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		activity = MapActivity.this;
		connection = new CheckInternetConnection(activity);
		prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		customToast = new CustomToast(activity);

		gloabalSocket = new GloabalSocket(activity,this);
		
		lvDoctors = (ListView) findViewById(R.id.lvDoctorsLivecare);
		etSearchDoc = findViewById(R.id.etSearchDoc);

		etSearchDoc.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}

			@Override
			public void afterTextChanged(Editable s) {
				if(lvLivecareDoctorsAdapter != null){
					lvLivecareDoctorsAdapter.filter(s.toString());
				}
			}
		});

		if (connection.isConnectedToInternet()) {
			//prefs.getString("state", "")
			getNearByDrs(DATA.stateFromLiveCare, true);
		} else {
			customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
		}
	
	}//oncreate

	private GoogleMap googleMap;
	private View mapView;
	private void initilizeMap(ArrayList<NearbyDoctorBean> latLongs) {


		if (googleMap == null) {
			OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
				@Override
				public void onMapReady(GoogleMap googleMapOMR) {
					googleMap = googleMapOMR;

					if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
						// Get the button view
						View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
						// and next place it, on bottom right (as Google Maps app)
                    /*RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);*/

						//ImageView ivLoc = (ImageView) locationButton;
						//ivLoc.setImageResource(R.drawable.ic_mylocation);
						//ivLoc.setVisibility(View.GONE);
					}
					//boolean success = googleMap.setMapStyle(new MapStyleOptions(MapStyleJSON.MAP_STYLE_JSON));
					//getResources().getString(R.string.style_json)
					//String styleStatus = (success) ? "-- Style applied on map !" : "-- Style parsing failed.";
					//DATA.print(""+styleStatus);

					//=======================Start existing Code here================================================


					//googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
					googleMap.clear();
					googleMap.setTrafficEnabled(true);
					if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
					googleMap.setBuildingsEnabled(true);

					for (NearbyDoctorBean latLongBean : latLongs) {
						double lati = Double.parseDouble(latLongBean.getLatitude());
						double longi = Double.parseDouble(latLongBean.getLongitude());
						String drName = latLongBean.getFirst_name()+" "+latLongBean.getLast_name();


						// create marker
						//  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
						MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(drName)//+"_"+latLongBean.getId()
								.snippet("Online Urgent Care")
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));



						// adding marker
						googleMap.addMarker(marker).showInfoWindow();


		        /*googleMap.setOnMarkerClickListener(new OnMarkerClickListener()
                {

                    @Override
                    public boolean onMarkerClick(Marker marker) {
                    	 marker.showInfoWindow();
                       // if(marker.getTitle().equals("MyHome")) // if marker source is clicked
                    	// DATA.doctorIdForLiveCare = latLongBean.getId();

                    	 String drId = marker.getTitle().split("_")[1];
                             Toast.makeText(MapActivity.this, marker.getTitle()+" id:"+ drId, Toast.LENGTH_SHORT).show();// display toast

                             DATA.doctorIdForLiveCare = drId;
                             finish();

                        return true;
                    }

                }); */
					}

					String userGpsLat = prefs.getString("userLatitude", "0.0");
					String userGpsLng = prefs.getString("userLongitude", "0.0");
					Double userGpsLatitude = Double.parseDouble(userGpsLat);
					Double userGpsLongitude = Double.parseDouble(userGpsLng);
					if (userGpsLatitude != 0.0 || userGpsLongitude != 0.0) {
						MarkerOptions marker = new MarkerOptions().position(new LatLng(userGpsLatitude, userGpsLongitude)).title("Me");
						//.snippet("Online Urgent Care")
						// .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
						// adding marker
						googleMap.addMarker(marker).showInfoWindow();

						//LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
						LatLng cpos = new LatLng(userGpsLatitude, userGpsLongitude);
						CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
						googleMap.animateCamera(update);
					} else {
						MarkerOptions marker = new MarkerOptions().position(new LatLng(zipCodelat, zipCodelng)).title("Me");
						//.snippet("Online Urgent Care")
						// .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
						// adding marker
						googleMap.addMarker(marker).showInfoWindow();

						//LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
						LatLng cpos = new LatLng(zipCodelat, zipCodelng);
						CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
						googleMap.animateCamera(update);
					}



					// check if map is created successfully or not
					if (googleMap == null) {
						Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
					}
				}
			};

			MapFragment mapFragment = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map));
			mapView = mapFragment.getView();
			mapFragment.getMapAsync(onMapReadyCallback);
		}

	}
	
	
	
	/*private void reinitilizeMap(ArrayList<NearbyDoctorBean> latLongs) {
	  
//	        googleMap = ((MapFragment) getFragmentManager().findFragmentById(
//	                R.id.map)).getMap();
	        googleMap.clear();
	        for (NearbyDoctorBean latLongBean : latLongs) {
				double lati = Double.parseDouble(latLongBean.getLatitude());
				double longi = Double.parseDouble(latLongBean.getLongitude());
				String drName = latLongBean.getFirst_name()+" "+latLongBean.getLast_name();
				
				 
				 // create marker
		      //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
	 MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(drName)//+"_"+latLongBean.getId()
	            .snippet("Online Urgent Care")
	            .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));	        
		         
	 
	
		        // adding marker
		        googleMap.addMarker(marker).showInfoWindow();
		       
			}

	        LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
	    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
			googleMap.animateCamera(update);
	        // check if map is created successfully or not
	        if (googleMap == null) {
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
	                    .show();
	        }
 
	}*/
	
	
	private void showMapForHospitals(ArrayList<HospitalsBean> hospitalsBeans) {

		if (googleMap == null) {
			OnMapReadyCallback onMapReadyCallback = new OnMapReadyCallback() {
			@Override
			public void onMapReady(GoogleMap googleMapOMR) {
				googleMap = googleMapOMR;

				if (mapView != null && mapView.findViewById(Integer.parseInt("1")) != null) {
					// Get the button view
					View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
					// and next place it, on bottom right (as Google Maps app)
                    /*RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);*/

					//ImageView ivLoc = (ImageView) locationButton;
					//ivLoc.setImageResource(R.drawable.ic_mylocation);
					//ivLoc.setVisibility(View.GONE);
				}
				//boolean success = googleMap.setMapStyle(new MapStyleOptions(MapStyleJSON.MAP_STYLE_JSON));
				//getResources().getString(R.string.style_json)
				//String styleStatus = (success) ? "-- Style applied on map !" : "-- Style parsing failed.";
				//DATA.print(""+styleStatus);

				//=======================Start existing Code here================================================


				//googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
				googleMap.clear();
				for (HospitalsBean hospitalsBean : hospitalsBeans) {
					double lati = hospitalsBean.getLat();
					double longi = hospitalsBean.getLng();
					String hospName = hospitalsBean.getName();


					// create marker
					//  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
					MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(hospName)
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_map_marker));
					// adding marker
					googleMap.addMarker(marker).showInfoWindow();
				}

				LatLng cpos = new LatLng(hospitalsBeans.get(0).getLat(), hospitalsBeans.get(0).getLng());
				CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
				googleMap.animateCamera(update);
				// check if map is created successfully or not
				if (googleMap == null) {
					Toast.makeText(getApplicationContext(), "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
				}


			}
		};

			MapFragment mapFragment = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map));
			mapView = mapFragment.getView();
			mapFragment.getMapAsync(onMapReadyCallback);
		}
	}
	
	
	private void showMapOneHospitals(ArrayList<HospitalsBean> hospitalsBeans) {
	   
	      
	        googleMap.clear();
	        for (HospitalsBean hospitalsBean : hospitalsBeans) {
				double lati = hospitalsBean.getLat();
				double longi = hospitalsBean.getLng();
				String hospName = hospitalsBean.getName();
				
				 
				 // create marker
		      //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
	 MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(hospName)
	            .icon(BitmapDescriptorFactory.fromResource(R.drawable.hospital_map_marker));	        
		        // adding marker
		        googleMap.addMarker(marker).showInfoWindow();
			}

	        LatLng cpos = new LatLng(hospitalsBeans.get(0).getLat(), hospitalsBeans.get(0).getLng());
	    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
			googleMap.animateCamera(update);
	        // check if map is created successfully or not
	        if (googleMap == null) {
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
	                    .show();
	        }
	 
	}

	
	double zipCodelat = 0.0, zipCodelng = 0.0;
	public void getNearByDrs(String state, boolean shouldShowLoader) {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);

		if(shouldShowLoader){
			DATA.showLoaderDefault(activity,"");
		}
		String requrl = DATA.baseUrl+"/doctor/searchDoctor/"+state;
		DATA.print("-- request " + requrl);
		client.get(requrl, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--responce in getNearByDrs: "+content);

					try {
						JSONObject jsonObject = new JSONObject(content);

						if (jsonObject.has("lat")) {
							zipCodelat = jsonObject.getDouble("lat");
						}
						if (jsonObject.has("lng")) {
							zipCodelng = jsonObject.getDouble("lng");
						}
						int success = jsonObject.getInt("success");
						latLongBeansList = new ArrayList<NearbyDoctorBean>();
						NearbyDoctorBean latLongBean = null;
						JSONArray data = jsonObject.getJSONArray("data");
						if (data.length() == 0) {
							//showMessageBoxForAllDrs(activity, "We are sorry", "Currently no doctors available near by you. Do you want to connect to other doctor ?");


							AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle(getResources().getString(R.string.app_name))
									.setMessage(DATA.NO_DOC_MESSAGE)
									.setPositiveButton("Done", null)
									.create();
							alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
								@Override
								public void onDismiss(DialogInterface dialog) {
									Intent intent1 = new Intent(getApplicationContext(), MainActivityNew.class);
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(intent1);
									finish();
								}
							});
							alertDialog.show();
						}else{
							for (int i = 0; i < data.length(); i++) {
								JSONObject object = data.getJSONObject(i);
								String id = object.getString("id");
								String latitude =object.getString("latitude");
								if (latitude.equals("")) {
									latitude = "0.0";
								}
								String longitude=object.getString("longitude");
								if (longitude.equals("")) {
									longitude = "0.0";
								}
								String zip_code=object.getString("zip_code");
								String title = object.optString("title");
								String first_name=object.getString("first_name");
								String last_name=object.getString("last_name");
								String is_online=object.getString("is_online");
								String image=object.getString("image");
								latLongBean = new NearbyDoctorBean(id, latitude, longitude, zip_code, title , first_name, last_name, is_online,image);
								latLongBeansList.add(latLongBean);
								latLongBean = null;
							}

							if (new GloabalMethods(activity).checkPlayServices()) {
								initilizeMap(latLongBeansList);
							}
							lvLivecareDoctorsAdapter = new LvLivecareDoctorsAdapter(activity, latLongBeansList);
							lvDoctors.setAdapter(lvLivecareDoctorsAdapter);

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: searchDoctor, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure searchDoctor: "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end searchDoctor
	
	public void getAllDrs() {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity, client);
		DATA.showLoaderDefault(activity,"");

		client.get(DATA.baseUrl+"/alldoctors/", new AsyncHttpResponseHandler() {
			//client.get("https://onlinecare.com/dev/index.php/app/alldoctors", new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--responce in alldoctors: "+content);

					try {
						JSONArray data = new JSONArray(content);
						latLongBeansList = new ArrayList<NearbyDoctorBean>();
						NearbyDoctorBean latLongBean = null;

						if (data.length() == 0) {
							showMessageBox(activity, "We are sorry", "Currently no doctors available");
						}else{
							for (int i = 0; i < data.length(); i++) {
								JSONObject object = data.getJSONObject(i);
								String id = object.getString("id");
								String latitude =object.getString("latitude");
								String longitude=object.getString("longitude");
								String zip_code=object.getString("zip_code");
								String first_name=object.getString("first_name");
								String last_name=object.getString("last_name");
								String is_online=object.getString("is_online");
								String image=object.getString("image");


								if (latitude.equalsIgnoreCase("null")) {
									latitude = "0.0";
								}
								if (longitude.equalsIgnoreCase("null")) {
									longitude = "0.0";
								}
								latLongBean = new NearbyDoctorBean(id, latitude, longitude, zip_code, first_name, last_name, is_online,image);
								latLongBeansList.add(latLongBean);
								latLongBean = null;
							}

							if (new GloabalMethods(activity).checkPlayServices()) {
								initilizeMap(latLongBeansList);
							}
							lvLivecareDoctorsAdapter = new LvLivecareDoctorsAdapter(activity, latLongBeansList);
							lvDoctors.setAdapter(lvLivecareDoctorsAdapter);

						}


					} catch (JSONException e) {
						// TODO Auto-generated catch block
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
						e.printStackTrace();
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: alldoctors, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure alldoctors: "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end alldoctors
	
	public void getNearByHospitals(double lat,double lng) {
		 
		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		DATA.showLoaderDefault(activity,"");

		client.get(DATA.baseUrl+"/getHospitals/"+lat+"/"+lng, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);
					DATA.print("--responce in getNearByHospitals: "+content);
					try {
						JSONObject jsonObject = new JSONObject(content);
						JSONArray jsonArray = jsonObject.getJSONArray("results");
						if (jsonArray.length() == 0) {
							customToast.showToast("No results found",0,0);
						}else{
							ArrayList<HospitalsBean> hospitalsBeans = new ArrayList<HospitalsBean>();
							HospitalsBean hospitalsBean = null;
							for (int i = 0; i < jsonArray.length(); i++) {
								double lat = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
								double lng = jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
								String icon = jsonArray.getJSONObject(i).getString("icon");
								String name = jsonArray.getJSONObject(i).getString("name");
								String vicinity = jsonArray.getJSONObject(i).getString("vicinity");
								hospitalsBean = new HospitalsBean(lat, lng, icon, name,vicinity);
								hospitalsBeans.add(hospitalsBean);
							}

							NearbyHospitalsAdapter adapter = new NearbyHospitalsAdapter(activity, hospitalsBeans);
							lvDoctors.setAdapter(adapter);
							if (new GloabalMethods(activity).checkPlayServices()) {
								showMapForHospitals(hospitalsBeans);
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}
				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: getNearByHospitals, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--responce in failure getNearByHospitals: "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});
		 
	}//end getNearByHospitals
	
	
	public void updateMap(ArrayList<NearbyDoctorBean> data) {
		DATA.print("--update map called");
		if (new GloabalMethods(activity).checkPlayServices()) {//checkGooglePlayservices
		//reinitilizeMap(data);
			LatLng cpos = new LatLng(Double.parseDouble(data.get(0).getLatitude()), Double.parseDouble(data.get(0).getLongitude()));
	    	CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
			googleMap.animateCamera(update);
		}
	}
	
	
	public void updateMapHospital(ArrayList<HospitalsBean> data) {
		DATA.print("--update map called");
		if (new GloabalMethods(activity).checkPlayServices()) {//checkGooglePlayservices
			showMapOneHospitals(data); 
		}
	}
	
	//MaterialDialog mMaterialDialog;
	public void showMessageBoxForAllDrs(Context context, String tittle , String content) {
		/*mMaterialDialog = new MaterialDialog(context)
	    .setTitle(tittle)
	    .setMessage(content)
	    .setPositiveButton("Find Other\nDoctors", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
				if (connection.isConnectedToInternet()) {
					getAllDrs();
				} else {
					Toast.makeText(activity, "Please check internet connection", 0).show();
				}  
	        }
	    }).setNegativeButton("Show nearby\nhospitals", new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				 GPSTracker gpsTracker = new GPSTracker(activity);
				 if (gpsTracker.canGetLocation()) {
					 mMaterialDialog.dismiss();
					double lat = gpsTracker.getLatitude();
					double lng = gpsTracker.getLongitude();
					getNearByHospitals(lat, lng);
				} else {
					gpsTracker.showSettingsAlert();
				}	
			}
		});
	
	mMaterialDialog.show();*/
		
		new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle(tittle)
		.setMessage(content)
		.setPositiveButton("Find Other Doctors", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				if (connection.isConnectedToInternet()) {
					getAllDrs();
				} else {
					Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
				} 
			}
		}).setNegativeButton("Show nearby hospitals", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				GPSTracker gpsTracker = new GPSTracker(activity);
				 if (gpsTracker.canGetLocation()) {
					
					double lat = gpsTracker.getLatitude();
					double lng = gpsTracker.getLongitude();
					getNearByHospitals(lat, lng);
				} else {
					gpsTracker.showSettingsAlert();
				}	
			}
		}).setNeutralButton("Call to coordinator", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				// TODO Auto-generated method stub
				Intent i = new Intent(activity,MainActivity.class);
				i.putExtra("isFromCallToCoordinator", true);
				startActivity(i);
				finish();
			}
		}).show();
	
	}
	public void showMessageBox(Context context, String tittle , String content) {

		new AlertDialog.Builder(context, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.setNegativeButton("Show nearby hospitals", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						GPSTracker gpsTracker = new GPSTracker(activity);
						if (gpsTracker.canGetLocation()) {
							double lat = gpsTracker.getLatitude();
							double lng = gpsTracker.getLongitude();
							getNearByHospitals(lat, lng);
						} else {
							gpsTracker.showSettingsAlert();
						}

					}
				})
				.create()
				.show();

		/*mMaterialDialog = new MaterialDialog(context)
	    .setTitle(tittle)
	    .setMessage(content)
	    .setPositiveButton("OK", new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	            mMaterialDialog.dismiss();
				finish();   
	        }
	    }).setNegativeButton("Show nearby hospitals", new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {

				 GPSTracker gpsTracker = new GPSTracker(activity);
				 if (gpsTracker.canGetLocation()) {
					 mMaterialDialog.dismiss();
					double lat = gpsTracker.getLatitude();
					double lng = gpsTracker.getLongitude();
					getNearByHospitals(lat, lng);
				} else {
					gpsTracker.showSettingsAlert();
				}	
			}
		});
	
	mMaterialDialog.show();*/
	
	}


	@Override
	public void onSocketCallBack(String emitterResponse) {

		try {
			JSONObject jsonObject = new JSONObject(emitterResponse);
			String id = jsonObject.getString("id");
			String usertype = jsonObject.getString("usertype");
			String status = jsonObject.getString("status");

			if(usertype.equalsIgnoreCase("doctor")){
				if(status.equalsIgnoreCase("logout") && latLongBeansList != null){
					/*for (int i = 0; i < latLongBeansList.size(); i++) {
						if(latLongBeansList.get(i).getId().equalsIgnoreCase(id)){
							latLongBeansList.remove(i);
						}
					}*/
                    for (Iterator<NearbyDoctorBean> iterator = latLongBeansList.iterator(); iterator.hasNext();) {
                        NearbyDoctorBean nearbyDoctorBean = iterator.next();
                        if (nearbyDoctorBean.getId().equals(id)) {
                            iterator.remove();
                        }
                        DATA.print("-- socket Call Back Doctor offline and removed : "+nearbyDoctorBean.getFirst_name()+" "+nearbyDoctorBean.getLast_name());
                    }
					if(lvLivecareDoctorsAdapter != null){
						lvLivecareDoctorsAdapter.notifyDataSetChanged();
					}
				}else if(status.equalsIgnoreCase("login")){
					if (connection.isConnectedToInternet()) {
						getNearByDrs(DATA.stateFromLiveCare, false);
					} else {
						customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
					}
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}
}
