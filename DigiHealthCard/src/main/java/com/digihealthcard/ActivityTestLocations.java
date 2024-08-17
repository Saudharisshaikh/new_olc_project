package com.digihealthcard;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.digihealthcard.api.ApiManager;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.digihealthcard.R;
import com.digihealthcard.api.Dialog_CustomProgress;
import com.digihealthcard.adapter.TestLocationAdapterNew;
import com.digihealthcard.model.TestLocationBeanNew;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.MapStyleJSON;
import com.paging.listview.PagingListView;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ActivityTestLocations extends ActivityBaseDrawer{


    PagingListView lvTestLoc;
    TextView tvNoData;
    Spinner spRadius;
    CardView mapCont;

    /*static boolean shoulRefresh = false;
    @Override
    protected void onResume() {
        if(shoulRefresh){
            shoulRefresh = false;

            loadListData();
        }
        super.onResume();
    }*/

    String placeSearchURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_test_locations);
        getLayoutInflater().inflate(R.layout.activity_test_locations, container_frame);

        lvTestLoc = findViewById(R.id.lvTestLoc);
        tvNoData = findViewById(R.id.tvNoData);
        spRadius = findViewById(R.id.spRadius);
        mapCont = findViewById(R.id.mapCont);

        //attachKeyboardListeners();

        placeSearchURL = sharedPrefsHelper.get("testLocSearchURL", "https://maps.googleapis.com/maps/api/place/textsearch/json?query=covid+vaccine+near+me&key=AIzaSyDcXui6S-AQcgRmHdZtB4rwsPR_Yo-s2f4");

        lvTestLoc.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //openActivity.open(ActivityBecksDepression.class, false);
                //viewOrEditForm(position, true);
                // selectedCardBean = cardBeans.get(position);

                try {
                    showPharmacyMap(testLocationBeanNews.get(position));
                    testLocationBeanNew = testLocationBeanNews.get(position);
                    getLocationDetail();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        if(getSupportActionBar() != null){
            /*getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Test Locations");*/
            getSupportActionBar().hide();
        }

        tvToolbarTitle.setText("Test Locations");
        ivToolbarBack.setVisibility(View.VISIBLE);
        ivToolbarHome.setVisibility(View.VISIBLE);
        btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/



        String[] milesArray  = {"5 Miles", "10 Miles", "20 Miles", "30 Miles", "40 Miles", "50 Miles", "75 Miles", "100 Miles", "250 Miles"};
        ArrayAdapter<String> spRadiusAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, milesArray);
        spRadiusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spRadius.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getTestLocations(milesArray[position].split("\\s+")[0]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spRadius.setAdapter(spRadiusAdapter);

        getTestLocations(milesArray[0].split("\\s+")[0]);


        testLocationBeanNews.clear();
        testLocationAdapterNew = new TestLocationAdapterNew(activity,testLocationBeanNews);
        lvTestLoc.setAdapter(testLocationAdapterNew);

        lvTestLoc.setPagingableListener(new PagingListView.Pagingable() {
            @Override
            public void onLoadMoreItems() {
                getTestLocations(milesArray[spRadius.getSelectedItemPosition()].split("\\s+")[0]);
            }
        });


        EditText etSearchLoc = findViewById(R.id.etSearchLoc);

        etSearchLoc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(testLocationAdapterNew != null){
                    testLocationAdapterNew.filter(s.toString());
                }
            }
        });

        etSearchLoc.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    String searchKeyword = etSearchLoc.getText().toString().trim();

                    if(TextUtils.isEmpty(searchKeyword)){
                        placeSearchURL = sharedPrefsHelper.get("testLocSearchURL", "https://maps.googleapis.com/maps/api/place/textsearch/json?query=covid+vaccine+near+me&key=AIzaSyDcXui6S-AQcgRmHdZtB4rwsPR_Yo-s2f4");
                    }else {
                        searchKeyword = searchKeyword.replaceAll("\\s+", "+");
                        placeSearchURL = sharedPrefsHelper.get("city_search", "https://maps.googleapis.com/maps/api/place/textsearch/json?key=AIzaSyDcXui6S-AQcgRmHdZtB4rwsPR_Yo-s2f4&query=covid+vaccine+near+") +searchKeyword;
                    }

                    next_page_token = null;
                    testLocationBeanNews.clear();
                    testLocationAdapterNew.notifyDataSetChanged();

                    getTestLocations(milesArray[spRadius.getSelectedItemPosition()].split("\\s+")[0]);

                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                activity,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        isKeybordOpen = isOpen;
                        //mapCont.setVisibility(isOpen ? View.GONE : View.VISIBLE);
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mapCont.getLayoutParams();
                        layoutParams.height = GloabalMethods.dpToPx(activity, isOpen ? 150 : 250);
                        System.out.println("-- onVisibilityChanged event isOpen = "+isOpen);
                        if(getSupportActionBar() != null){
                            if(isOpen){
                                //getSupportActionBar().hide();
                                cvToolbar.setVisibility(View.GONE);
                            }else {
                                //getSupportActionBar().show();
                                cvToolbar.setVisibility(View.VISIBLE);
                            }
                        }
                        /*if(testLocationAdapterNew != null){
                            testLocationAdapterNew.notifyDataSetChanged();
                        }*/
                    }
                });


        customProgressDialog = new Dialog_CustomProgress(activity);


        super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
    }
    public boolean isKeybordOpen = false;


    private void getTestLocations(String miles){
        double userlat = 44.1251998340001, userlong = -84.1966975;
        try {
            userlat = Double.parseDouble(prefs.getString("userLatitude", "0.0"));
            userlong = Double.parseDouble(prefs.getString("userLongitude", "0.0"));
        }catch (Exception e){
            e.printStackTrace();
        }

        loadListData(userlat, userlong, miles, next_page_token);
    }


    ArrayList<TestLocationBeanNew> testLocationBeanNews = new ArrayList<>();
    TestLocationAdapterNew testLocationAdapterNew;
    String next_page_token;

    Dialog_CustomProgress customProgressDialog;
    public void loadListData(double userLatitude, double userLongitude, String radiusInMiles, String next_page_tokenArg){
        //customProgressDialog.showProgressDialog();
        //String URL = sharedPrefsHelper.get("testLocSearchURL", "https://maps.googleapis.com/maps/api/place/textsearch/json?query=covid+vaccine+near+me&key=AIzaSyDcXui6S-AQcgRmHdZtB4rwsPR_Yo-s2f4");//"https://findahealthcenter.hrsa.gov//healthcenters/find?lon="+userLongitude+"&lat="+userLatitude+"&radius="+radiusInMiles;

        String URL = placeSearchURL;

        if(!TextUtils.isEmpty(next_page_tokenArg)){
            URL = URL + "&pagetoken=" + next_page_tokenArg;
        }

        final String urlPrnt = URL;

        if(sharedPrefsHelper.get("debug_logs", false)){
            System.out.println("-- URL in test locations request : "+URL);
        }
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.setupKeystores(client);
        client.post(URL,null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                try{
                    String content = new String(response);
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+urlPrnt+"\nresult: "+content);
                    }

                    try {

                        JSONObject jsonObject = new JSONObject(content);
                        if(jsonObject.optString("status").equalsIgnoreCase("OK")){

                            ArrayList <TestLocationBeanNew> testLocationBeanNewsAPI = new ArrayList<>();
                            TestLocationBeanNew testLocationBeanNew;
                            JSONArray results = jsonObject.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                testLocationBeanNew = new TestLocationBeanNew();
                                testLocationBeanNew.placeObject = results.getJSONObject(i);

                                testLocationBeanNew.name = testLocationBeanNew.placeObject.optString("name");
                                testLocationBeanNew.place_id = testLocationBeanNew.placeObject.optString("place_id");
                                testLocationBeanNew.lat = testLocationBeanNew.placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                testLocationBeanNew.lng = testLocationBeanNew.placeObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                                testLocationBeanNew.formatted_address = testLocationBeanNew.placeObject.optString("formatted_address");

                                testLocationBeanNewsAPI.add(testLocationBeanNew);
                                testLocationBeanNew = null;
                            }

                            testLocationBeanNews.addAll(testLocationBeanNewsAPI);

                            testLocationAdapterNew.notifyDataSetChanged();

                            testLocationAdapterNew.testLocationBeanNewsOrig.clear();
                            testLocationAdapterNew.testLocationBeanNewsOrig.addAll(testLocationBeanNews);

                            boolean viewMore = jsonObject.has("next_page_token");
                            lvTestLoc.setHasMoreItems(viewMore);
                            lvTestLoc.onFinishLoading(viewMore, testLocationBeanNews);

                            next_page_token = jsonObject.optString("next_page_token","");

                            int vis = testLocationBeanNews.isEmpty() ? View.VISIBLE : View.GONE;
                            tvNoData.setVisibility(vis);

                            if(testLocationBeanNews.size() > 0){
                                showPharmacyMap(null);//testCenterBeans.get(0)
                            }else {
                                customToast.showToast("No test locations found near by you. Please try again OR change search radius.",0,0);
                            }
                        }else {
                            boolean viewMore = false;//jsonObject.has("next_page_token");
                            lvTestLoc.setHasMoreItems(viewMore);
                            lvTestLoc.onFinishLoading(viewMore, testLocationBeanNews);

                           // next_page_token = jsonObject.optString("next_page_token","");

                            int vis = View.VISIBLE;//testLocationBeanNews.isEmpty() ? View.VISIBLE : View.GONE;
                            tvNoData.setVisibility(vis);

                        }



//                        else if(jsonObject.optString("status").equalsIgnoreCase("ZERO_RESULTS")){
//
//                            JSONArray results = jsonObject.getJSONArray("results");
//                            int vis = testLocationBeanNews.isEmpty() ? View.VISIBLE : View.GONE;
//                            tvNoData.setVisibility(vis);
//                        }


                        /*JSONArray data = new JSONArray(content);
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
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                        customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- responce onsuccess: "+urlPrnt+", http status code: "+statusCode+" Byte responce: "+response);
                    }
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+urlPrnt+"\nresult: "+content);
                    }
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }
        });
    }

    public void getLocationDetail(){
        customProgressDialog.showProgressDialog();
        String URL = "https://maps.googleapis.com/maps/api/place/details/json?fields=name,rating,formatted_phone_number,website&key=AIzaSyDcXui6S-AQcgRmHdZtB4rwsPR_Yo-s2f4&place_id="+testLocationBeanNew.place_id;

        final String urlPrnt = URL;
        if(sharedPrefsHelper.get("debug_logs", false)){
            System.out.println("-- URL in test location details request : "+URL);
        }
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.setupKeystores(client);
        client.post(URL,null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                customProgressDialog.dismissProgressDialog();
                try{
                    String content = new String(response);
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+urlPrnt+"\nresult: "+content);
                    }

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        if(jsonObject.optString("status").equalsIgnoreCase("OK")){
                            JSONObject result = jsonObject.getJSONObject("result");
                            testLocationBeanNew.website = result.optString("website");
                            testLocationBeanNew.formatted_phone_number = result.optString("formatted_phone_number");

                            showLocDetailDialog();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customSnakeBar.showToast(DATA.JSON_ERROR_MSG);
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- responce onsuccess: "+urlPrnt+", http status code: "+statusCode+" Byte responce: "+response);
                    }
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- in activity "+apiCallBack.getClass().getSimpleName()+"\napiName: "+urlPrnt+"\nresult: "+content);
                    }
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }catch (Exception e1){
                    e1.printStackTrace();
                    customSnakeBar.showToast(DATA.CMN_ERR_MSG);
                }
            }
        });
    }


    /*
    ArrayList<TestCenterBean> testCenterBeans;
    TestLocationAdapter testLocationAdapter;

    public void loadListData2(double userLatitude, double userLongitude, String radiusInMiles){
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
      */



    private GoogleMap googleMap;
    View mapView;
    public void showPharmacyMap(TestLocationBeanNew testLocationBeanNew) {

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
                if(!permissionsChecker.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
                    googleMap.setMyLocationEnabled(true);
                }
                googleMap.setBuildingsEnabled(true);

                googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        TestLocationBeanNew markerTag = (TestLocationBeanNew) marker.getTag();
                        for (int i = 0; i < testLocationBeanNews.size(); i++) {
                            if(testLocationBeanNews.get(i).equals(markerTag)){

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
                if(testLocationBeanNews != null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();//LatLngBounds--1
                    for (int i = 0; i < testLocationBeanNews.size(); i++) {

                        TestLocationBeanNew tcb = testLocationBeanNews.get(i);

                        Double pharmacyLat = 0.0;
                        Double pharmacyLng = 0.0;
                        //System.out.println("-- in showPharmacyMap "+pbn.StoreName);
                        //String distance = tcb.Distance+"";

                        try {
                            pharmacyLat = tcb.lat;
                            pharmacyLng = tcb.lng;
                            //distance = String.format("%.2f", Double.parseDouble(distance));
                        } catch (Exception e) {
                            // TODO: handle exception
                            pharmacyLat = 0.0;
                            pharmacyLng = 0.0;
                            //return;
                        }

                        LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                        //MarkerOptions marker = new MarkerOptions().position(cpos).title(tcb.CtrNm).snippet("Distance: "+distance+" Miles");
                        MarkerOptions marker = new MarkerOptions().position(cpos).title(tcb.name).snippet(tcb.formatted_address);
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_null));
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

                if(testLocationBeanNew != null){
                    //add selected pharmacy marker
                    Double pharmacyLat = 0.0;
                    Double pharmacyLng = 0.0;
                    System.out.println("-- in showPharmacyMap "+testLocationBeanNew.name);
                    //String distance = testCenterBean.Distance+"";

                    try {
                        pharmacyLat = testLocationBeanNew.lat;
                        pharmacyLng = testLocationBeanNew.lng;
                        //distance = String.format("%.2f", Double.parseDouble(distance));
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




    Dialog dialogLocationDetail;
    TestLocationBeanNew testLocationBeanNew;
    public void showLocDetailDialog() {
        if(dialogLocationDetail != null){
            dialogLocationDetail.dismiss();
        }
        dialogLocationDetail = new Dialog(activity);
        dialogLocationDetail.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogLocationDetail.setContentView(R.layout.dialog_tl_details);
        dialogLocationDetail.setCanceledOnTouchOutside(false);

        TextView tvLocName = dialogLocationDetail.findViewById(R.id.tvLocName);
        TextView tvPhoneName = dialogLocationDetail.findViewById(R.id.tvPhoneName);
        TextView tvAddress = dialogLocationDetail.findViewById(R.id.tvAddress);
        TextView tvCallNow = dialogLocationDetail.findViewById(R.id.tvCallNow);
        TextView tvWeb = dialogLocationDetail.findViewById(R.id.tvWeb);
        TextView tvGetDirections = dialogLocationDetail.findViewById(R.id.tvGetDirections);
        Button btnDone = dialogLocationDetail.findViewById(R.id.btnDone);

        tvLocName.setText(testLocationBeanNew.name);
        tvPhoneName.setText(testLocationBeanNew.formatted_phone_number);
        tvAddress.setText(testLocationBeanNew.formatted_address);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.tvCallNow){

                    checkPermission();

                }else if(v.getId() == R.id.tvWeb){
                    try {
                        if(sharedPrefsHelper.get("debug_logs", false)){
                            System.out.println("-- Site URL: "+testLocationBeanNew.website);
                        }
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        String site = testLocationBeanNew.website;
                        if(!site.startsWith("http")){
                            site = "http://"+site;
                        }
                        i.setData(Uri.parse(site));
                        activity.startActivity(i);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else if(v.getId() == R.id.tvGetDirections){

                    //"http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"

                    double userlat = 44.1251998340001, userlong = -84.1966975;
                    try {
                        userlat = Double.parseDouble(prefs.getString("userLatitude", "0.0"));
                        userlong = Double.parseDouble(prefs.getString("userLongitude", "0.0"));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("http://maps.google.com/maps?saddr").append(userlat).append(",").append(userlong).append("&daddr=")
                            .append(testLocationBeanNew.lat).append(",").append(testLocationBeanNew.lng);
                    String reqURL = sb.toString();
                    if(sharedPrefsHelper.get("debug_logs", false)){
                        System.out.println("-- Direction req: "+reqURL);
                    }
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse(reqURL));
                    activity.startActivity(intent);
                }else if(v.getId() == R.id.btnDone){
                    dialogLocationDetail.dismiss();
                }
            }
        };
        tvCallNow.setOnClickListener(onClickListener);
        tvWeb.setOnClickListener(onClickListener);
        tvGetDirections.setOnClickListener(onClickListener);
        btnDone.setOnClickListener(onClickListener);


        tvWeb.setEnabled(!TextUtils.isEmpty(testLocationBeanNew.website));
        tvCallNow.setEnabled(!TextUtils.isEmpty(testLocationBeanNew.formatted_phone_number));

        dialogLocationDetail.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogLocationDetail.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogLocationDetail.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogLocationDetail.show();
        dialogLocationDetail.getWindow().setAttributes(lp);*/
    }


    private String[] PERMISSIONS = {
            Manifest.permission.CALL_PHONE
    };
    private static final int PERMISSION_REQ_CODE = 1 << 4;
    private void checkPermission() {
        boolean granted = true;
        for (String per : PERMISSIONS) {
            if (!permissionGranted(per)) {
                granted = false;
                break;
            }
        }
        if (granted) {
            resetLayoutAndForward();
        } else {
            requestPermissions();
        }
    }

    private boolean permissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_REQ_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQ_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                granted = (result == PackageManager.PERMISSION_GRANTED);
                if (!granted) break;
            }

            if (granted) {
                resetLayoutAndForward();
            } else {
                customToast.showToast(getResources().getString(R.string.need_necessary_permissions), 0, 0);
            }
        }
    }

    private void resetLayoutAndForward() {
        try {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:"+Uri.encode(testLocationBeanNew.formatted_phone_number)));
            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(callIntent);
        }catch (Exception  e){e.printStackTrace();}
    }








    /*public void plotAllMArkers(){
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
                MarkerOptions marker = new MarkerOptions().position(cpos).title(tcb.CtrNm).snippet("Distance: "+distance+" K.M");
                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_null));
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
    }*/



    /*@Override
    protected void onShowKeyboard(int keyboardHeight) {
        mapCont.setVisibility(View.GONE);
        System.out.println("-- on show keyborad call keybored height : "+keyboardHeight);
    }
    @Override
    protected void onHideKeyboard() {
        mapCont.setVisibility(View.VISIBLE);
        System.out.println("-- on hide keyborad called");
    }*/

}
