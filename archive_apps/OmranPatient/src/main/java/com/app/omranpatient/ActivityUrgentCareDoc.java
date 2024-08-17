package com.app.omranpatient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.omranpatient.adapter.LvLivecareDoctorsAdapter;
import com.app.omranpatient.adapter.UrgentCareAdapter;
import com.app.omranpatient.api.ApiCallBack;
import com.app.omranpatient.api.ApiManager;
import com.app.omranpatient.api.CustomSnakeBar;
import com.app.omranpatient.model.NearbyDoctorBean;
import com.app.omranpatient.model.UrgentCareBean;
import com.app.omranpatient.paypal.PaymentLiveCare;
import com.app.omranpatient.util.CheckInternetConnection;
import com.app.omranpatient.util.CustomToast;
import com.app.omranpatient.util.DATA;
import com.app.omranpatient.util.GloabalMethods;
import com.app.omranpatient.util.HideShowKeypad;
import com.app.omranpatient.util.OpenActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ActivityUrgentCareDoc extends BaseActivity{

    public static AppCompatActivity activity;
    CheckInternetConnection checkInternetConnection;
    OpenActivity openActivity;
    CustomToast customToast;
    CustomSnakeBar customSnakeBar;
    HideShowKeypad hideShowKeypad;
    SharedPreferences prefs;
    ApiCallBack apiCallBack;

    //Spinner spUrgentCareCenter;
    ListView lvDoctorsUrgentCare;
    Button btnUrgentCareContinue;
    public static String urgentCareCenterId = "";

    String apiGetDoctorsWithId = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urgent_care_doc);

        activity = ActivityUrgentCareDoc.this;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        customSnakeBar = CustomSnakeBar.getCustomSnakeBarInstance(activity);
        hideShowKeypad = new HideShowKeypad(activity);
        prefs = getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        apiCallBack = this;

        //spUrgentCareCenter = (Spinner) findViewById(R.id.spUrgentCareCenter);
        lvDoctorsUrgentCare = (ListView) findViewById(R.id.lvDoctorsUrgentCare);
        btnUrgentCareContinue = (Button) findViewById(R.id.btnUrgentCareContinue);

        /*spUrgentCareCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                apiGetDoctorsWithId = ApiManager.DOCTORS_BY_URGENTCARE+"/"+urgentCareBeans.get(position).id;
                ApiManager apiManager = new ApiManager(apiGetDoctorsWithId,"post",null,apiCallBack, activity);
                apiManager.loadURL();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/

        lvDoctorsUrgentCare.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(urgentCareBeans.get(position).id.equalsIgnoreCase("a1")){
                    backFlag = 1;
                    getSupportActionBar().setTitle("OnlineCare Doctors");
                    apiGetDoctorsWithId = ApiManager.DOCTORS_BY_URGENTCARE+"/"+urgentCareBeans.get(position).id;
                    ApiManager apiManager = new ApiManager(apiGetDoctorsWithId,"post",null,apiCallBack, activity);
                    apiManager.loadURL();

                    btnUrgentCareContinue.setVisibility(View.GONE);
                }else {
                    updateMapForUrgentCare(urgentCareBeans.get(position),position);

                    btnUrgentCareContinue.setVisibility(View.VISIBLE);
                }
                urgentCareCenterId = urgentCareBeans.get(position).id;
                btnUrgentCareContinue.setEnabled(true);
                btnUrgentCareContinue.setBackgroundResource(R.drawable.btn_green);

                urgentCareAdapter.ucAdapterSelPos = position;
                urgentCareAdapter.notifyDataSetChanged();
            }
        });

        btnUrgentCareContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.doctorIdForLiveCare = "0";
                activity.startActivity(new Intent(activity,PaymentLiveCare.class));
                activity.finish();
            }
        });

        backFlag = 0;
        //getSupportActionBar().setTitle("Select an Urgent Care Center near you");
        getSupportActionBar().setTitle("Select the provider near you");
        RequestParams params = new RequestParams();
        params.put("zipcode",DATA.zipCodeFromLiveCare);
        params.put("patient_id", prefs.getString("id",""));
        ApiManager apiManager = new ApiManager(ApiManager.GET_URGENT_CARES,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    static int backFlag = 0;//0=urgentcare, 1 = doctors
    ArrayList<UrgentCareBean> urgentCareBeans;
    ArrayList<NearbyDoctorBean> latLongBeansList;
    double zipCodelat = 0.0, zipCodelng = 0.0;//not for use
    UrgentCareAdapter urgentCareAdapter;
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.equals(ApiManager.GET_URGENT_CARES)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                urgentCareBeans = new ArrayList<>();
                UrgentCareBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String center_name = data.getJSONObject(i).getString("center_name");
                    String is_deleted = data.getJSONObject(i).getString("is_deleted");
                    String total_doctors = data.getJSONObject(i).getString("total_doctors");

                    String latitude = "0";
                    String longitude = "0";
                    if (data.getJSONObject(i).has("latitude")){
                        if((!data.getJSONObject(i).getString("latitude").isEmpty())){
                            latitude = data.getJSONObject(i).getString("latitude");
                        }
                    }
                    if (data.getJSONObject(i).has("longitude")){
                        if(! data.getJSONObject(i).getString("longitude").isEmpty()){
                            longitude = data.getJSONObject(i).getString("longitude");
                        }
                    }

                    bean = new UrgentCareBean(id,center_name,is_deleted,latitude,longitude,total_doctors);
                    urgentCareBeans.add(bean);
                    bean = null;
                }
                //ArrayAdapter<UrgentCareBean> hospitalAdapter = new ArrayAdapter<UrgentCareBean>(activity, android.R.layout.simple_selectable_list_item, urgentCareBeans);//simple_dropdown_item_1line
                //spUrgentCareCenter.setAdapter(hospitalAdapter);
                urgentCareAdapter = new UrgentCareAdapter(activity,urgentCareBeans);
                lvDoctorsUrgentCare.setAdapter(urgentCareAdapter);
                initilizeMapForUrgentCare(urgentCareBeans);
            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }else if(apiName.equals(apiGetDoctorsWithId)){
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
                customSnakeBar.showToast("No doctors available in the urgent care center");
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
                        String first_name=object.getString("first_name");
                        String last_name=object.getString("last_name");
                        String is_online=object.getString("is_online");
                        String image=object.getString("image");
                        latLongBean = new NearbyDoctorBean(id, latitude, longitude, zip_code, first_name, last_name, is_online,image);
                        latLongBeansList.add(latLongBean);
                        latLongBean = null;
                    }

                    if (new GloabalMethods(activity).checkPlayServices()) {//checkGooglePlayservices
                        initilizeMap(latLongBeansList);
                    }
                    lvDoctorsUrgentCare.setAdapter(new LvLivecareDoctorsAdapter(activity, latLongBeansList));

                }

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }



    private GoogleMap googleMap;
    View mapView;
    private void initilizeMap(ArrayList<NearbyDoctorBean> latLongs) {
       // if (googleMap == null) { bc map is already initialized for urgent cares

        //if (googleMap == null) {}
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
                //System.out.println(""+styleStatus);

                //=======================Start existing Code here================================================


                //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.clear();
                googleMap.setTrafficEnabled(true);
                if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
                googleMap.setBuildingsEnabled(true);

                googleMap.setInfoWindowAdapter(null);
                googleMap.setOnInfoWindowClickListener(null);

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
                    googleMap.addMarker(marker);

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
                    googleMap.addMarker(marker);
                    //LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
                    LatLng cpos = new LatLng(userGpsLatitude, userGpsLongitude);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                    googleMap.animateCamera(update);
                } else {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(zipCodelat, zipCodelng)).title("Me");
                    //.snippet("Online Urgent Care")
                    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                    // adding marker
                    googleMap.addMarker(marker);
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

        //}
    }

    public void updateMap(ArrayList<NearbyDoctorBean> data) {
        System.out.println("--update map called");
        if (new GloabalMethods(activity).checkPlayServices()) {
            //reinitilizeMap(data);
            LatLng cpos = new LatLng(Double.parseDouble(data.get(0).getLatitude()), Double.parseDouble(data.get(0).getLongitude()));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
            googleMap.animateCamera(update);
        }
    }

    private void initilizeMapForUrgentCare(final ArrayList<UrgentCareBean> urgentCareBeans) {
        //if (googleMap == null) {map reload probelem on backpressed

        if (googleMap == null) {}
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
                //System.out.println(""+styleStatus);

                //=======================Start existing Code here================================================

                //googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.clear();
                googleMap.setTrafficEnabled(true);
                if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
                googleMap.setBuildingsEnabled(true);

                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        //return null;
                        View myContentView = getLayoutInflater().inflate(R.layout.custom_marker, null);
                        TextView tvTitle = ((TextView) myContentView.findViewById(R.id.title));
                        tvTitle.setText(marker.getTitle());
                        TextView tvSnippet = ((TextView) myContentView.findViewById(R.id.snippet));
                        tvSnippet.setText(marker.getSnippet());
                    /*ImageView ivMarker = (ImageView) myContentView.findViewById(R.id.ivMarker);
                    //UrlImageViewHelper.setUrlDrawable(ivMarker, DATA.selectedUserCallImage, R.drawable.icon_call_screen);
                    if(! DATA.selectedUserCallImage.isEmpty()){
                        Picasso.with(activity).load(DATA.selectedUserCallImage).placeholder(R.drawable.icon_call_screen).into(ivMarker);
                    }*/
                        return myContentView;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;//a padding was shown on marker info window
                    }
                });

            /*googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    int listPosition = 0;
                    try {
                        listPosition = Integer.parseInt(marker.getSnippet());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(!urgentCareBeans.get(listPosition).id.equalsIgnoreCase("a1")){
                        getSupportActionBar().setTitle("Urgent Care Doctors");
                        backFlag = 1;
                        apiGetDoctorsWithId = ApiManager.DOCTORS_BY_URGENTCARE+"/"+urgentCareBeans.get(listPosition).id;
                        ApiManager apiManager = new ApiManager(apiGetDoctorsWithId,"post",null,apiCallBack, activity);
                        apiManager.loadURL();
                    }
                }
            });*/

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        int listPosition = 0;
                        try {
                            listPosition = Integer.parseInt(marker.getSnippet());
                            lvDoctorsUrgentCare.performItemClick(
                                    lvDoctorsUrgentCare.getAdapter().getView(listPosition, null, null),
                                    listPosition,
                                    lvDoctorsUrgentCare.getAdapter().getItemId(listPosition));
                            lvDoctorsUrgentCare.setSelection(listPosition);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                });


                for (int i = 0; i < urgentCareBeans.size(); i++) {
                    UrgentCareBean urgentCareBean = urgentCareBeans.get(i);
                    double lati = 0,longi = 0;
                    try{
                        lati = Double.parseDouble(urgentCareBean.latitude);
                        longi = Double.parseDouble(urgentCareBean.longitude);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    String drName = urgentCareBean.center_name;
                    // create marker
                    //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(drName)//+"_"+latLongBean.getId()
                            .snippet(i+"")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));

                    // adding marker
                    googleMap.addMarker(marker);

                    if(i == 0){
                        LatLng cpos = marker.getPosition();
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                        googleMap.animateCamera(update);
                    }


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
                    googleMap.addMarker(marker);

                    //LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
                    LatLng cpos = new LatLng(userGpsLatitude, userGpsLongitude);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                    //googleMap.animateCamera(update);//Removed by Jamal... Emcura updates
                } else {
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(zipCodelat, zipCodelng)).title("Me");
                    //.snippet("Online Urgent Care")
                    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                    // adding marker
                    googleMap.addMarker(marker);

                    //LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
                    LatLng cpos = new LatLng(zipCodelat, zipCodelng);
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                    //googleMap.animateCamera(update);//Removed by Jamal ... Emcura updates
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

        //}
    }

    public void updateMapForUrgentCare(UrgentCareBean bean,int listPos) {
        System.out.println("--update map called");
        if (new GloabalMethods(activity).checkPlayServices()) {
            //reinitilizeMap(data);
            LatLng cpos = new LatLng(Double.parseDouble(bean.latitude), Double.parseDouble(bean.longitude));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
            googleMap.animateCamera(update);
            MarkerOptions marker = new MarkerOptions().position(cpos).title(bean.center_name)//+"_"+latLongBean.getId()
                    .snippet(listPos+"")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
            googleMap.addMarker(marker).showInfoWindow();
        }
    }


    @Override
    public void onBackPressed() {
        if(backFlag == 1){
            backFlag = 0;
            getSupportActionBar().setTitle("Select an Urgent Care Center near you");
            if(urgentCareBeans != null){
                //ArrayAdapter<UrgentCareBean> hospitalAdapter = new ArrayAdapter<UrgentCareBean>(activity, android.R.layout.simple_dropdown_item_1line, urgentCareBeans);
                //spUrgentCareCenter.setAdapter(hospitalAdapter);
                //lvDoctorsUrgentCare.setAdapter(hospitalAdapter);
                urgentCareAdapter = new UrgentCareAdapter(activity,urgentCareBeans);
                lvDoctorsUrgentCare.setAdapter(urgentCareAdapter);
                initilizeMapForUrgentCare(urgentCareBeans);
            }else{
                RequestParams params = new RequestParams("zipcode",DATA.zipCodeFromLiveCare);
                params.put("patient_id", prefs.getString("id",""));
                ApiManager apiManager = new ApiManager(ApiManager.GET_URGENT_CARES,"post",params,apiCallBack, activity);
                apiManager.loadURL();
            }
            return;
        }
        super.onBackPressed();
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_add_new, menu);
            menu.getItem(0).setTitle("Continue");
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*if (item.getItemId() == R.id.action_save_schedule) {

            DATA.doctorIdForLiveCare = "0";
            activity.startActivity(new Intent(activity,PaymentLiveCare.class));
            activity.finish();

        }else */
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;//return true back activity state maintains if false back activity oncreate called
        }
        return super.onOptionsItemSelected(item);
    }
}
