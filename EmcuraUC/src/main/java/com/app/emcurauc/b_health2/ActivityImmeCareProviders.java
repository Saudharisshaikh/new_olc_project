package com.app.emcurauc.b_health2;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.app.emcurauc.BaseActivity;
import com.app.emcurauc.R;
import com.app.emcurauc.api.ApiManager;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.GloabalMethods;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivityImmeCareProviders extends BaseActivity {



    ListView lvMyProviders;
    EditText etSearchDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_imme_care_providers);

        if(getSupportActionBar() != null){
            getSupportActionBar().setTitle("Select a Provider");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        lvMyProviders = (ListView) findViewById(R.id.lvMyProviders);
        etSearchDoc = findViewById(R.id.etSearchDoc);

        etSearchDoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(lvImmeCareProvidersAdapter != null){
                    lvImmeCareProvidersAdapter.filter(s.toString());
                }
            }
        });

        lvMyProviders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateMap(position);
            }
        });


        RequestParams params = new RequestParams();
        params.put("is_online", "1");
        params.put("symptom_name", GetLiveCareFormBhealth.symptomName);
        ApiManager apiManager = new ApiManager(ApiManager.BHEALTH_GET_THERAPIST_DOC,"post",params,apiCallBack, activity);
        apiManager.loadURL();
    }

    ArrayList<DoctorBean> doctorBeans;
    LvImmeCareProvidersAdapter lvImmeCareProvidersAdapter;
    @Override
    public void fetchDataCallback(String httpstatus, String apiName, String content) {
        if(apiName.equalsIgnoreCase(ApiManager.BHEALTH_GET_THERAPIST_DOC)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray doctors = jsonObject.getJSONArray("doctors");
                if(doctors.length() == 0){
                    findViewById(R.id.tvNoProvider).setVisibility(View.VISIBLE);
                }else {
                    findViewById(R.id.tvNoProvider).setVisibility(View.GONE);
                }

                doctorBeans = gson.fromJson(doctors.toString(), new TypeToken<ArrayList<DoctorBean>>() {}.getType());
                if(doctorBeans != null){
                    lvImmeCareProvidersAdapter = new LvImmeCareProvidersAdapter(activity,doctorBeans);
                    lvMyProviders.setAdapter(lvImmeCareProvidersAdapter);

                    initilizeMap();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
            }
        }
    }





    private GoogleMap googleMap;
    private View mapView;
    private void initilizeMap() {


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

                    for (DoctorBean doctorBean : doctorBeans) {
                        double lati = Double.parseDouble(doctorBean.latitude);
                        double longi = Double.parseDouble(doctorBean.longitude);
                        String drName = doctorBean.first_name+" "+doctorBean.last_name;

                        // create marker
                        //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
                        MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(drName)//+"_"+latLongBean.getId()
                                .snippet(doctorBean.doctor_category)
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
                        /*MarkerOptions marker = new MarkerOptions().position(new LatLng(zipCodelat, zipCodelng)).title("Me");
                        //.snippet("Online Urgent Care")
                        // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                        // adding marker
                        googleMap.addMarker(marker).showInfoWindow();

                        //LatLng cpos = new LatLng(Double.parseDouble(latLongs.get(0).getLatitude()), Double.parseDouble(latLongs.get(0).getLongitude()));
                        LatLng cpos = new LatLng(zipCodelat, zipCodelng);
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                        googleMap.animateCamera(update);*/
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


    public void updateMap(int pos) {
        DATA.print("--update map called");
        if (new GloabalMethods(activity).checkPlayServices()) {//checkGooglePlayservices
            //reinitilizeMap(data);
            LatLng cpos = new LatLng(Double.parseDouble(doctorBeans.get(pos).latitude), Double.parseDouble(doctorBeans.get(pos).longitude));
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
            googleMap.animateCamera(update);
        }
    }
}
