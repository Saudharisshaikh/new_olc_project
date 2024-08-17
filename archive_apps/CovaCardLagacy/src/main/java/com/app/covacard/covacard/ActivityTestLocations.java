package com.app.covacard.covacard;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.covacard.BaseActivity;
import com.app.covacard.R;
import com.app.covacard.api.Dialog_CustomProgress;
import com.app.covacard.util.DATA;
import com.app.covacard.util.MapStyleJSON;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityTestLocations extends BaseActivity{


    ListView lvTestLoc;
    TextView tvNoData;
    Spinner spRadius;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_locations);

        lvTestLoc = findViewById(R.id.lvTestLoc);
        tvNoData = findViewById(R.id.tvNoData);
        spRadius = findViewById(R.id.spRadius);

        lvTestLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);
                // selectedCardBean = cardBeans.get(position);

                showPharmacyMap(testCenterBeans.get(position));
            }
        });


        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("COVID Test Locations");
        }


        String[] milesArray  = {"5 Miles", "10 Miles", "20 Miles", "30 Miles", "40 Miles", "50 Miles", "75 Miles", "100 Miles", "250 Miles"};
        ArrayAdapter<String> spRadiusAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, milesArray);
        spRadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                double userlat = 44.1251998340001, userlong = -84.1966975;
                try {
                    userlat = Double.parseDouble(prefs.getString("userLatitude", "0.0"));
                    userlong = Double.parseDouble(prefs.getString("userLongitude", "0.0"));
                }catch (Exception e){
                    e.printStackTrace();
                }

                loadListData(userlat, userlong, milesArray[position].split("\\s+")[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spRadius.setAdapter(spRadiusAdapter);


        EditText etSearchLoc = findViewById(R.id.etSearchLoc);

        etSearchLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(testLocationAdapter != null){
                    testLocationAdapter.filter(s.toString());
                }
            }
        });


        customProgressDialog = new Dialog_CustomProgress(activity);


    }

    Dialog_CustomProgress customProgressDialog;
    public void loadListData(double userLatitude, double userLongitude, String radiusInMiles){
        customProgressDialog.showProgressDialog();
        String URL = "https://findahealthcenter.hrsa.gov//healthcenters/find?lon="+userLongitude+"&lat="+userLatitude+"&radius="+radiusInMiles;
        System.out.println("-- URL in test locations request : "+URL);
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(URL,null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                customProgressDialog.dismissProgressDialog();
                try{
                    String content = new String(response);
                    System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+URL+"\nresult: "+content);

                    try {
                        JSONArray data = new JSONArray(content);
                        //JSONArray data = jsonObject.getJSONArray("data");

                        if(data.length() == 0){
                            tvNoData.setVisibility(View.VISIBLE);
                        }else {
                            tvNoData.setVisibility(View.GONE);
                        }

                        Type listType = new TypeToken<ArrayList<TestCenterBean>>() {}.getType();
                        testCenterBeans = new Gson().fromJson(data.toString(), listType);

                        testLocationAdapter = new TestLocationAdapter(activity,  testCenterBeans);
                        lvTestLoc.setAdapter(testLocationAdapter);

                        if(testCenterBeans.size() > 0){
                            showPharmacyMap(null);//testCenterBeans.get(0)
                        }else {
                            customToast.showToast("No test locations found near by you. Please try again OR change search radius.",0,0);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("-- responce onsuccess: "+URL+", http status code: "+statusCode+" Byte responce: "+response);
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+URL+"\nresult: "+content);
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }
        });
    }

    ArrayList<TestCenterBean> testCenterBeans;
    TestLocationAdapter testLocationAdapter;






    private GoogleMap googleMap;
    View mapView;
    public void showPharmacyMap(TestCenterBean testCenterBean) {

        //if (googleMap == null) {}

        //if (googleMap == null) {   to call the  code each time

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
                boolean success = googleMap.setMapStyle(new MapStyleOptions(MapStyleJSON.MAP_STYLE_JSON));
                //getResources().getString(R.string.style_json)
                //String styleStatus = (success) ? "-- Style applied on map !" : "-- Style parsing failed.";
                //System.out.println(""+styleStatus);

                //=======================Start existing Code here================================================

                //googleMap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.clear();
                googleMap.setTrafficEnabled(true);
                if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
                googleMap.setBuildingsEnabled(true);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        TestCenterBean markerTag = (TestCenterBean) marker.getTag();
                        for (int i = 0; i < testCenterBeans.size(); i++) {
                            if(testCenterBeans.get(i).equals(markerTag)){

                                lvTestLoc.setItemChecked(i, true);
                                lvTestLoc.setSelection(i);
                                lvTestLoc.smoothScrollToPosition(i);
                            }
                        }

                        return false;
                    }
                });


                /*googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        //return null;
                        View myContentView = activity.getLayoutInflater().inflate(R.layout.custom_marker_pharmacy, null);
                        TextView tvTitle = myContentView.findViewById(R.id.title);
                        tvTitle.setText(marker.getTitle());
                        TextView tvSnippet = myContentView.findViewById(R.id.snippet);
                        tvSnippet.setText(marker.getSnippet());

                        Button btnSelPharmacyMap = myContentView.findViewById(R.id.btnSelPharmacyMap);

                        PharmacyBean pb = (PharmacyBean) marker.getTag();

                        System.out.println("-- pb : "+pb);

                        if (pb != null && selectedPharmacyBean.id.equalsIgnoreCase(pb.id)) {
                            btnSelPharmacyMap.setText("Your Selected Pharmacy");
                            btnSelPharmacyMap.setBackgroundResource(R.drawable.apptmnt_cancel_drawable);
                        }else {
                            btnSelPharmacyMap.setText("Select this Pharmacy");
                            btnSelPharmacyMap.setBackgroundResource(R.drawable.btn_selector);
                        }

                        return myContentView;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;//a padding was shown on marker info window
                    }
                });
                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        try {
                            PharmacyBean pb = (PharmacyBean) marker.getTag();
                            if(!pb.id.equalsIgnoreCase(selectedPharmacyBean.id)){
                                selectedPharmacyId = pb.id;
                                if (checkInternetConnection.isConnectedToInternet()) {
                                    updatePharmacy(selectedPharmacyId, selectedPharmacyCategory,pb);
                                } else {
                                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });*/

                //add all pharmacies markers
                if(testCenterBeans != null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();//LatLngBounds--1
                    for (int i = 0; i < testCenterBeans.size(); i++) {

                        TestCenterBean tcb = testCenterBeans.get(i);

                        Double pharmacyLat = 0.0;
                        Double pharmacyLng = 0.0;
                        //System.out.println("-- in showPharmacyMap "+pbn.StoreName);
                        String distance = tcb.Distance+"";

                        try {
                            pharmacyLat = tcb.Latitude;
                            pharmacyLng = tcb.Longitude;
                            distance = String.format("%.2f", Double.parseDouble(distance));
                        } catch (Exception e) {
                            // TODO: handle exception
                            pharmacyLat = 0.0;
                            pharmacyLng = 0.0;
                            //return;
                        }

                        LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                        MarkerOptions marker = new MarkerOptions().position(cpos).title(tcb.CtrNm).snippet("Distance: "+distance+" Miles")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_null));
                        // adding marker
                        Marker pharmacyMarker = googleMap.addMarker(marker);
                        pharmacyMarker.setTag(tcb);
                        //pharmacyMarker.showInfoWindow();//important:showInfoWindow after settag  otherwise in infowindowadapter tag will null

                        builder.include(marker.getPosition());//LatLngBounds --2
                    }
                    try {
                        LatLngBounds bounds = builder.build();//LatLngBounds--3
                        int padding = 50; // offset from edges of the map in pixels
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                        //googleMap.moveCamera(cu);
                        googleMap.animateCamera(cu);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //add all pharmacies markers ends

                if(testCenterBean != null){
                    //add selected pharmacy marker
                    Double pharmacyLat = 0.0;
                    Double pharmacyLng = 0.0;
                    System.out.println("-- in showPharmacyMap "+testCenterBean.CountyNm);
                    String distance = testCenterBean.Distance+"";

                    try {
                        pharmacyLat = testCenterBean.Latitude;
                        pharmacyLng = testCenterBean.Longitude;
                        distance = String.format("%.2f", Double.parseDouble(distance));
                    } catch (Exception e) {
                        // TODO: handle exception
                        pharmacyLat = 0.0;
                        pharmacyLng = 0.0;
                        //return;
                    }

                    LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                /*MarkerOptions marker = new MarkerOptions().position(cpos).title(testCenterBean.CtrNm).snippet("Distance: "+distance+" K.M")
                 .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_null));
                // adding marker
                Marker pharmacyMarker = googleMap.addMarker(marker);
                pharmacyMarker.setTag(testCenterBean);
                pharmacyMarker.showInfoWindow();//important:showInfoWindow after settag  otherwise in infowindowadapter tag will null
                 */
                    //Note: this is pharmacybean in method args. map not update on filter pharmacies from dropdown or et need to work
                    CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);//10
                    googleMap.animateCamera(update);
                    //add selected pharmacy marker ends
                }


                // check if map is created successfully or not
                if (googleMap == null) {
                    Toast.makeText(activity, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
                }
            }
        };

        MapFragment mapFragment = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map));
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(onMapReadyCallback);

        //}
    }

    public void plotAllMArkers(){
        //add all pharmacies markers
        if(testCenterBeans != null && googleMap != null){
            googleMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();//LatLngBounds--1
            for (int i = 0; i < testCenterBeans.size(); i++) {

                TestCenterBean tcb = testCenterBeans.get(i);

                Double pharmacyLat = 0.0;
                Double pharmacyLng = 0.0;
                //System.out.println("-- in showPharmacyMap "+pbn.StoreName);
                String distance = tcb.Distance+"";

                try {
                    pharmacyLat = tcb.Latitude;
                    pharmacyLng = tcb.Longitude;
                    distance = String.format("%.2f", Double.parseDouble(distance));
                } catch (Exception e) {
                    // TODO: handle exception
                    pharmacyLat = 0.0;
                    pharmacyLng = 0.0;
                    //return;
                }

                LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                MarkerOptions marker = new MarkerOptions().position(cpos).title(tcb.CtrNm).snippet("Distance: "+distance+" K.M")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_null));
                // adding marker
                Marker pharmacyMarker = googleMap.addMarker(marker);
                pharmacyMarker.setTag(tcb);
                //pharmacyMarker.showInfoWindow();//important:showInfoWindow after settag  otherwise in infowindowadapter tag will null

                builder.include(marker.getPosition());//LatLngBounds --2
            }
            try {
                LatLngBounds bounds = builder.build();//LatLngBounds--3
                int padding = 50; // offset from edges of the map in pixels
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                //googleMap.moveCamera(cu);
                googleMap.animateCamera(cu);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //add all pharmacies markers ends
    }


}
