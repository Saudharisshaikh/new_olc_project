package com.app.Olc_MentalHealth.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import androidx.annotation.NonNull;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.core.content.ContextCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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
import android.widget.TextView;
import android.widget.Toast;

import com.app.Olc_MentalHealth.BuildConfig;
import com.app.Olc_MentalHealth.adapter.DialCountriesAdapter;
import com.app.Olc_MentalHealth.adapter.PharmacyAdapter;
import com.app.Olc_MentalHealth.adapter.SubUsersAdapter3;
import com.app.Olc_MentalHealth.b_health.ActivityBecksDepression;
import com.app.Olc_MentalHealth.b_health.ActivityDepInvList;
import com.app.Olc_MentalHealth.b_health.assessment.ActivityDAST_Form;
import com.app.Olc_MentalHealth.b_health.assessment.ActivityDastList;
import com.app.Olc_MentalHealth.b_health.assessment.ActivityPHQ_Form;
import com.app.Olc_MentalHealth.b_health.assessment.ActivityPhqList;
import com.app.Olc_MentalHealth.b_health.assessment.AssessSubmit;
import com.app.Olc_MentalHealth.b_health.assessment.new_assesment.ActivityOCD_Form;
import com.app.Olc_MentalHealth.b_health.assessment.new_assesment.ActivityOCD_List;
import com.app.Olc_MentalHealth.b_health.assessment.new_assesment.ActivityStressQues_Form;
import com.app.Olc_MentalHealth.b_health2.ActivityConnectTherapist;
import com.app.Olc_MentalHealth.b_health2.GetLiveCareFormBhealth;
import com.app.Olc_MentalHealth.model.ConditionsModel;
import com.app.Olc_MentalHealth.model.CountryBean;
import com.app.Olc_MentalHealth.model.EmoWelOptionBean;
import com.app.Olc_MentalHealth.model.PharmacyBean;
import com.app.Olc_MentalHealth.model.PharmacyCategoryBean;
import com.app.Olc_MentalHealth.model.SubUsersModel;
import com.app.Olc_MentalHealth.model.SymptomsModel;
import com.app.Olc_MentalHealth.paypal.PaymentLiveCare;
import com.app.Olc_MentalHealth.ActivityCovidFormList;
import com.app.Olc_MentalHealth.ActivityCovidTest;
import com.app.Olc_MentalHealth.ActivityPrimaryCareDoctors;

import com.app.Olc_MentalHealth.Login;
import com.app.Olc_MentalHealth.MainActivityNew;
import com.app.Olc_MentalHealth.MedicalHistory1;
import com.app.Olc_MentalHealth.R;
import com.app.Olc_MentalHealth.Signup;
import com.app.Olc_MentalHealth.SubUsersList;
import com.app.Olc_MentalHealth.api.ApiCallBack;
import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.api.Dialog_CustomProgress;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;
import ss.com.bannerslider.banners.Banner;
import ss.com.bannerslider.banners.DrawableBanner;
import ss.com.bannerslider.banners.RemoteBanner;
import ss.com.bannerslider.views.BannerSlider;
import ss.com.bannerslider.views.indicators.IndicatorShape;

import static com.app.Olc_MentalHealth.Login.HOSP_ID_PREFS_KEY;
import static com.app.Olc_MentalHealth.api.ApiManager.PREF_APP_LBL_KEY;

public class GloabalMethods implements ApiCallBack{

    Activity activity;
    CustomToast customToast;
    CheckInternetConnection checkInternetConnection;
    SharedPreferences prefs;
    OpenActivity openActivity;
    Dialog_CustomProgress dialog_customProgress;

    public static final String SHOW_PHARMACY_BROADCAST_ACTION = BuildConfig.APPLICATION_ID+".onlinecare_show_patient_selected_pharmacy";

    public GloabalMethods(Activity activity) {
        // TODO Auto-generated constructor stub
        this.activity = activity;
        customToast = new CustomToast(activity);
        checkInternetConnection = new CheckInternetConnection(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        openActivity = new OpenActivity(activity);
        dialog_customProgress = new Dialog_CustomProgress(activity);
    }



    //----------------------------------------------------------------PHARMACY----------------------------------------------------------------------

    public void updatePharmacy(String pharmacy_id, String pharmacyCat, final PharmacyBean selectedPharmacyBean) {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", ""));//prefs.getString("getLiveCareApptID", "")
        params.put("pharmacy_id", pharmacy_id);
        params.put("pharmacy_cat", pharmacyCat);

        DATA.print("-- params in updatePharmacy "+params.toString());

        dialog_customProgress.showProgressDialog();

        client.post(DATA.baseUrl+"updatePharmacy", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in updatePharmacy "+content);
                    // --reaponce in updatePharmacy {"msg":"","status":"success"}

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            customToast.showToast("Pharmacy selected", 0, 1);
								/*if (dialog!= null) {
									dialog.dismiss();
								}*/
                            GloabalMethods.selectedPharmacyBean = selectedPharmacyBean;
                            showPharmacyMap(selectedPharmacyBean);

                            //getPharmacy("",false, "");//GM1 commected in emcura
                            Intent intent = new Intent(SHOW_PHARMACY_BROADCAST_ACTION).setPackage(BuildConfig.APPLICATION_ID);//Send broadcast to update pharmacy in activity: GM2
                            activity.sendBroadcast(intent);

                            if(dialogPharmacySelection != null){
                                dialogPharmacySelection.dismiss();
                            }

                        } else {
                            customToast.showToast(jsonObject.getString("msg"), 0, 1);
                        }

                    } catch (JSONException e) {
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                        e.printStackTrace();
                    }

                    dialog_customProgress.dismissProgressDialog();

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: updatePharmacy, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- on fail updatePharmacy "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                    dialog_customProgress.dismissProgressDialog();

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

        });

    }//end updatePharmacy

    String selectedPharmacyCategory = "";
    String selectedPharmacyId = "";
    //boolean shouldLoadPharmacy = false;
    PharmacyAdapter pharmacyAdapter;
    ListView lvPharmacy;
    Dialog dialogPharmacySelection;

    public void showPharmacyDialog() {
        // TODO Auto-generated method stub
        if(dialogPharmacySelection != null){
            if(dialogPharmacySelection.isShowing()){
                return;
            }
        }
        dialogPharmacySelection = new Dialog(activity);
        dialogPharmacySelection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogPharmacySelection.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogPharmacySelection.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogPharmacySelection.setContentView(R.layout.dialog_pharmacy_selection);
        dialogPharmacySelection.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        SpinnerCustom spPharmacyCategory = dialogPharmacySelection.findViewById(R.id.spPharmacyCategory);
        EditText etPharmacyCity = dialogPharmacySelection.findViewById(R.id.etPharmacyCity);
        Button btnSearchPharmacy = dialogPharmacySelection.findViewById(R.id.btnSearchPharmacy);
        lvPharmacy = (ListView) dialogPharmacySelection.findViewById(R.id.lvPharmacy);

        ImageView ivClose = (ImageView) dialogPharmacySelection.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPharmacySelection.dismiss();
            }
        });

			 /*if (checkInternetConnection.isConnectedToInternet()) {
				 getPharmacy("", activity);
			 } else {
				 customToast.showToast(NO_NETWORK_MESSAGE, 0, 1);
			 }*/


        ArrayAdapter<PharmacyCategoryBean> dataAdapter2 = new ArrayAdapter<PharmacyCategoryBean>(activity, android.R.layout.simple_spinner_item, pharmacyCategoryBeans);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPharmacyCategory.setAdapter(dataAdapter2);

        lvPharmacy.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectedPharmacyId = pharmacyBeans.get(arg2).id;

                if (checkInternetConnection.isConnectedToInternet()) {
                    updatePharmacy(selectedPharmacyId, selectedPharmacyCategory,pharmacyBeans.get(arg2));
                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
                }
            }
        });

        pharmacyAdapter = new PharmacyAdapter(activity, pharmacyBeans);
        lvPharmacy.setAdapter(pharmacyAdapter);

        spPharmacyCategory.setOnItemSelectedListener(new SpinnerCustom.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3,boolean isUserSelect) {
                // TODO Auto-generated method stub
                if(isUserSelect){
                    if (arg2 == 0) {
                        selectedPharmacyCategory = "";
                    } else {
                        selectedPharmacyCategory = pharmacyCategoryBeans.get(arg2).key;
                    }
                    getPharmacy(selectedPharmacyCategory,false, etPharmacyCity.getText().toString().trim());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        etPharmacyCity.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    getPharmacy(selectedPharmacyCategory,false, etPharmacyCity.getText().toString().trim());
                    //return true; return true not closes keyboard
                    return false;
                }
                return false;
            }
        });
        btnSearchPharmacy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etPharmacyCity.getText().toString().trim().isEmpty()){
                    etPharmacyCity.setError("Please enter City/State/Zipcode");
                    return;
                }
                getPharmacy(selectedPharmacyCategory,false, etPharmacyCity.getText().toString().trim());
            }
        });

        dialogPharmacySelection.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                FragmentManager fm = activity.getFragmentManager();
                Fragment fragment = (fm.findFragmentById(R.id.map));
                FragmentTransaction ft = fm.beginTransaction();
                ft.remove(fragment);
                ft.commit();
            }
        });
        //dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogPharmacySelection.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogPharmacySelection.show();
        dialogPharmacySelection.getWindow().setAttributes(lp);

        if(!selectedPharmacyBean.id.isEmpty()){
            showPharmacyMap(selectedPharmacyBean);
        }
    }


    public static ArrayList<PharmacyCategoryBean>pharmacyCategoryBeans;
    public static ArrayList<PharmacyBean> pharmacyBeans;

    //public static PharmacyCategoryBean selectedPharmacyCategoryBean;
    public static PharmacyBean selectedPharmacyBean;
    public void getPharmacy(String pharmacyCat,final boolean shouldShowDialog, String search_keyword) {//search_keyword is city,state or zipcode

        String reqURL = DATA.baseUrl+"getPharmacy/"+prefs.getString("id", "")+"/"+pharmacyCat;

        if(!search_keyword.isEmpty()){
            reqURL = reqURL+"?search_keyword="+search_keyword;
        }

        DATA.print("-- url in getPharmacy: "+reqURL);

        dialog_customProgress.showProgressDialog();

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        client.get(reqURL, new AsyncHttpResponseHandler() {//prefs.getString("getLiveCareApptID", "")
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                dialog_customProgress.dismissProgressDialog();
                try{
                    String content = new String(response);

                    DATA.print("--reaponce in getPharmacy "+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);

                        JSONArray pharmacies = jsonObject.getJSONArray("pharmacies");
                        pharmacyBeans = new ArrayList<PharmacyBean>();
                        PharmacyBean bean = null;
                        for (int i = 0; i < pharmacies.length(); i++) {

                            String id = pharmacies.getJSONObject(i).getString("id");
                            String NPI = pharmacies.getJSONObject(i).getString("NPI");
                            String StoreName = pharmacies.getJSONObject(i).getString("StoreName");
                            String PhonePrimary = pharmacies.getJSONObject(i).getString("PhonePrimary");
                            String Latitude = pharmacies.getJSONObject(i).getString("Latitude");
                            String Longitude = pharmacies.getJSONObject(i).getString("Longitude");
                            String status = pharmacies.getJSONObject(i).getString("status");
                            String address = pharmacies.getJSONObject(i).getString("address");
                            String city = pharmacies.getJSONObject(i).getString("city");
                            String state = pharmacies.getJSONObject(i).getString("state");
                            String zipcode = pharmacies.getJSONObject(i).getString("zipcode");
                            String SpecialtyName = pharmacies.getJSONObject(i).getString("SpecialtyName");
                            String distance = pharmacies.getJSONObject(i).getString("distance");

                            bean = new PharmacyBean(id, NPI, StoreName, PhonePrimary, Latitude, Longitude, status, address, city, state, zipcode, SpecialtyName, distance);
                            pharmacyBeans.add(bean);
                            bean = null;
                        }


                        JSONArray pharmacy_categories = jsonObject.getJSONArray("pharmacy_categories");
                        PharmacyCategoryBean temp;
                        pharmacyCategoryBeans = new ArrayList<PharmacyCategoryBean>();

                        for (int i = 0; i < pharmacy_categories.length(); i++) {
                            String key = pharmacy_categories.getJSONObject(i).getString("key");
                            String value = pharmacy_categories.getJSONObject(i).getString("value");

                            temp = new PharmacyCategoryBean(key, value);
                            pharmacyCategoryBeans.add(temp);
                            temp = null;
                        }

                        String pharmacy_id = jsonObject.getString("pharmacy_id");
                        String StoreName = jsonObject.getString("StoreName");
                        String PhonePrimary = jsonObject.getString("PhonePrimary");
                        String Latitude = jsonObject.getString("Latitude");
                        String Longitude = jsonObject.getString("Longitude");
                        String address = jsonObject.getString("address");
                        String city = jsonObject.getString("city");
                        String state = jsonObject.getString("state");
                        String zipcode = jsonObject.getString("zipcode");
                        String SpecialtyName = jsonObject.getString("SpecialtyName");
                        String distance = jsonObject.getString("distance");
                        String pharmacy_cat = jsonObject.getString("pharmacy_cat");

                        selectedPharmacyBean = new PharmacyBean(pharmacy_id, "NPI", StoreName, PhonePrimary, Latitude, Longitude, "status",
                                address, city, state, zipcode, SpecialtyName, distance);

                        Intent intent = new Intent(SHOW_PHARMACY_BROADCAST_ACTION).setPackage(BuildConfig.APPLICATION_ID);
                        activity.sendBroadcast(intent);

                        if (lvPharmacy != null) {
                            pharmacyAdapter = new PharmacyAdapter(activity, pharmacyBeans);
                            lvPharmacy.setAdapter(pharmacyAdapter);
                        } else {
                            DATA.print("-- lvPharmacy is null in getPharmacy");
                        }

                        DATA.print("-- in getPharmacy shouldShowDialog: "+shouldShowDialog+" googleMap: "+googleMap);
                        if (shouldShowDialog) {
                            showPharmacyDialog();
                        }else {
                            plotAllMArkers();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: getPharmacy, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                dialog_customProgress.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- on fail getPharmacy "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end getPharmacy

    private GoogleMap googleMap;
    View mapView;
    public void showPharmacyMap(PharmacyBean pharmacyBean) {

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
                //boolean success = googleMap.setMapStyle(new MapStyleOptions(MapStyleJSON.MAP_STYLE_JSON));
                //getResources().getString(R.string.style_json)
                //String styleStatus = (success) ? "-- Style applied on map !" : "-- Style parsing failed.";
                //DATA.print(""+styleStatus);

                //=======================Start existing Code here================================================

                //googleMap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
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
                        View myContentView = activity.getLayoutInflater().inflate(R.layout.custom_marker_pharmacy, null);
                        TextView tvTitle = myContentView.findViewById(R.id.title);
                        tvTitle.setText(marker.getTitle());
                        TextView tvSnippet = myContentView.findViewById(R.id.snippet);
                        tvSnippet.setText(marker.getSnippet());
								/*ImageView ivMarker = myContentView.findViewById(R.id.ivMarker);
								//UrlImageViewHelper.setUrlDrawable(ivMarker, DATA.selectedUserCallImage, R.drawable.icon_call_screen);
								if(! DATA.selectedUserCallImage.isEmpty()){
									DATA.loadImageFromURL(DATA.selectedUserCallImage,R.drawable.icon_call_screen,ivMarker);
								}*/

                        Button btnSelPharmacyMap = myContentView.findViewById(R.id.btnSelPharmacyMap);

                        PharmacyBean pb = (PharmacyBean) marker.getTag();

                        DATA.print("-- pb : "+pb);

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
                });

                //add all pharmacies markers
                if(pharmacyBeans != null){
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();//LatLngBounds--1
                    for (int i = 0; i < pharmacyBeans.size(); i++) {

                        PharmacyBean pbn = pharmacyBeans.get(i);

                        Double pharmacyLat = 0.0;
                        Double pharmacyLng = 0.0;
                        //DATA.print("-- in showPharmacyMap "+pbn.StoreName);
                        String distance = pbn.distance;

                        try {
                            pharmacyLat = Double.parseDouble(pbn.Latitude);
                            pharmacyLng = Double.parseDouble(pbn.Longitude);
                            distance = String.format("%.2f", Double.parseDouble(distance));
                        } catch (Exception e) {
                            // TODO: handle exception
                            pharmacyLat = 0.0;
                            pharmacyLng = 0.0;
                            //return;
                        }

                        LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                        MarkerOptions marker = new MarkerOptions().position(cpos).title(pbn.StoreName).snippet("Distance: "+distance+" K.M");
                        // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                        // adding marker
                        Marker pharmacyMarker = googleMap.addMarker(marker);
                        pharmacyMarker.setTag(pbn);
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

                //add selected pharmacy marker
                Double pharmacyLat = 0.0;
                Double pharmacyLng = 0.0;
                DATA.print("-- in showPharmacyMap "+pharmacyBean.StoreName);
                String distance = pharmacyBean.distance;

                try {
                    pharmacyLat = Double.parseDouble(pharmacyBean.Latitude);
                    pharmacyLng = Double.parseDouble(pharmacyBean.Longitude);
                    distance = String.format("%.2f", Double.parseDouble(distance));
                } catch (Exception e) {
                    // TODO: handle exception
                    pharmacyLat = 0.0;
                    pharmacyLng = 0.0;
                    //return;
                }

                LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                MarkerOptions marker = new MarkerOptions().position(cpos).title(pharmacyBean.StoreName).snippet("Distance: "+distance+" K.M");
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                // adding marker
                Marker pharmacyMarker = googleMap.addMarker(marker);
                pharmacyMarker.setTag(pharmacyBean);
                pharmacyMarker.showInfoWindow();//important:showInfoWindow after settag  otherwise in infowindowadapter tag will null

                //Note: this is pharmacybean in method args. map not update on filter pharmacies from dropdown or et need to work
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 10);
                googleMap.animateCamera(update);
                //add selected pharmacy marker ends


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
        if(pharmacyBeans != null && googleMap != null){
            googleMap.clear();
            LatLngBounds.Builder builder = new LatLngBounds.Builder();//LatLngBounds--1
            for (int i = 0; i < pharmacyBeans.size(); i++) {

                PharmacyBean pbn = pharmacyBeans.get(i);

                Double pharmacyLat = 0.0;
                Double pharmacyLng = 0.0;
                //DATA.print("-- in showPharmacyMap "+pbn.StoreName);
                String distance = pbn.distance;

                try {
                    pharmacyLat = Double.parseDouble(pbn.Latitude);
                    pharmacyLng = Double.parseDouble(pbn.Longitude);
                    distance = String.format("%.2f", Double.parseDouble(distance));
                } catch (Exception e) {
                    // TODO: handle exception
                    pharmacyLat = 0.0;
                    pharmacyLng = 0.0;
                    //return;
                }

                LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);
                MarkerOptions marker = new MarkerOptions().position(cpos).title(pbn.StoreName).snippet("Distance: "+distance+" K.M");
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                // adding marker
                Marker pharmacyMarker = googleMap.addMarker(marker);
                pharmacyMarker.setTag(pbn);
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

    //----------------------------------------------------------------PHARMACY----------------------------------------------------------------------



    //------------------PRIMARY CARE--------------------------------------------Note: this primarycare code not used. shifted to ActivityPrimaryCareDoctors
    Dialog diloagAddPrimaryCare;
    public void pimaryCareDialog() {

        // TODO Auto-generated method stub
        diloagAddPrimaryCare = new Dialog(activity);
        diloagAddPrimaryCare.requestWindowFeature(Window.FEATURE_NO_TITLE);
        diloagAddPrimaryCare.setContentView(R.layout.dialog_add_primarycare);

        final EditText etAddPrimaryCareFName = (EditText) diloagAddPrimaryCare.findViewById(R.id.etAddPrimaryCareFName);
        final EditText etAddPrimaryCareLName = (EditText) diloagAddPrimaryCare.findViewById(R.id.etAddPrimaryCareLName);
        final EditText etAddPrimaryCareEmail = (EditText) diloagAddPrimaryCare.findViewById(R.id.etAddPrimaryCareEmail);
        final EditText etAddPrimaryCareAddress = (EditText) diloagAddPrimaryCare.findViewById(R.id.etAddPrimaryCareAddress);
        final EditText etAddPrimaryCareMobile = (EditText) diloagAddPrimaryCare.findViewById(R.id.etAddPrimaryCareMobile);

        Button btnAddPrimaryCareSbmt = (Button) diloagAddPrimaryCare.findViewById(R.id.btnAddPrimaryCareSbmt);


        diloagAddPrimaryCare.findViewById(R.id.btnAddPrimaryCareCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                diloagAddPrimaryCare.dismiss();
            }
        });

        btnAddPrimaryCareSbmt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (checkInternetConnection.isConnectedToInternet()) {
							/*if (etAddPrimaryCareFName.getText().toString().isEmpty() ||
									etAddPrimaryCareLName.getText().toString().isEmpty() ||
									etAddPrimaryCareEmail.getText().toString().isEmpty() ||
									etAddPrimaryCareAddress.getText().toString().isEmpty() ||
									etAddPrimaryCareMobile.getText().toString().isEmpty()) {
								Toast.makeText(activity, "All feilds are required", Toast.LENGTH_LONG).show();
							} else {
								addPrimaryCare(etAddPrimaryCareEmail.getText().toString(),
										etAddPrimaryCareFName.getText().toString(),
										etAddPrimaryCareLName.getText().toString(), 
										etAddPrimaryCareAddress.getText().toString(), 
										etAddPrimaryCareMobile.getText().toString());
							}*/
                    activity.startActivity(new Intent(activity, ActivityPrimaryCareDoctors.class));
                } else {
                    Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_LONG).show();
                }
            }
        });


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(diloagAddPrimaryCare.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        diloagAddPrimaryCare.show();
        diloagAddPrimaryCare.getWindow().setAttributes(lp);

        if (checkInternetConnection.isConnectedToInternet()) {
            EditText[] editTexts = {etAddPrimaryCareFName,etAddPrimaryCareLName,etAddPrimaryCareEmail,etAddPrimaryCareAddress,etAddPrimaryCareMobile};
            getPrimaryCare(activity,editTexts,diloagAddPrimaryCare);
        } else {
            customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
        }

    }

    //SharedPreferences prefs;
		 /*public void addPrimaryCare(String email,String first_name,String last_name,String address1,String mobile) {
			 
				DATA.showLoaderDefault(activity, "");
				AsyncHttpClient client = new AsyncHttpClient();
				RequestParams params = new RequestParams();

				params.put("patient_id", prefs.getString("id", "0"));
				params.put("email", email);
				params.put("first_name", first_name);
				params.put("last_name", last_name);
				params.put("address1", address1);
				params.put("mobile", mobile);

				client.post(DATA.baseUrl+"addPrimaryCare", params, new AsyncHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, String content) {
						DATA.dismissLoaderDefault();
						DATA.print("--reaponce in addPrimaryCare: "+content);

						try {
							JSONObject jsonObject = new JSONObject(content);
							if (jsonObject.getString("status").equalsIgnoreCase("success")) {
								customToast.showToast(jsonObject.getString("msg"), 0, 1);
								diloagAddPrimaryCare.dismiss();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
						}

					}

					@Override
					public void onFailure(Throwable error, String content) {
						DATA.dismissLoaderDefault();
						DATA.print("--onfail addPrimaryCare: " +content);
						Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_LONG).show();
					}
				});

			}*/

    public void getPrimaryCare(final Activity activity, final EditText[] editTexts, final Dialog diloagAddPrimaryCare) {

        DATA.showLoaderDefault(activity, "");

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();
        params.put("patient_id", prefs.getString("id", "0"));


        client.post(DATA.baseUrl+"getPrimaryCare", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try{
                    String content = new String(response);
                    DATA.print("--reaponce in getPrimaryCare: "+content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        int count = jsonObject.getInt("count");


                        if (count > 0) {
                            //diloagAddPrimaryCare.findViewById(R.id.layBtns).setVisibility(View.GONE);
                            diloagAddPrimaryCare.findViewById(R.id.contFields).setVisibility(View.VISIBLE);
                            ((TextView)diloagAddPrimaryCare.findViewById(R.id.tvPrimaryCareTxt)).
                                    setText("Primary Care Physician is added on your profile");
                            //addButton.setText("Cahnge Primary Care");

                            JSONObject data = jsonObject.getJSONObject("data");

                            String id = data.getString("id");
                            String doctor_id = data.getString("doctor_id");
                            String patient_id = data.getString("patient_id");
                            String date = data.getString("date");
                            String first_name = data.getString("first_name");
                            String last_name = data.getString("last_name");
                            String email = data.getString("email");
                            String mobile = data.getString("mobile");
                            String address1 = data.getString("address1");

                            editTexts[0].setText(first_name);
                            editTexts[1].setText(last_name);
                            editTexts[2].setText(email);
                            editTexts[3].setText(address1);
                            editTexts[4].setText(mobile);
                        }else {

                            diloagAddPrimaryCare.findViewById(R.id.layBtns).setVisibility(View.VISIBLE);
                            diloagAddPrimaryCare.findViewById(R.id.contFields).setVisibility(View.GONE);
                            ((TextView)diloagAddPrimaryCare.findViewById(R.id.tvPrimaryCareTxt)).
                                    setText("Do you want to add a Primary Care? Please select a primary care physician");

                            new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).
                                    setMessage("Primary care is not added on your profile. You can add the primary care here. Please select a doctor from doctor's list to add as primary care").
                                    setPositiveButton("Done", null).show();
                        }



                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                        e.printStackTrace();
                    }

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: getPrimaryCare, http status code: "+statusCode+" Byte responce: "+response);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail getPrimaryCare: " +content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }
    //------------------PRIMARY CARE--------------------------------------------


    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "-- googleplayservices";
    public boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(activity, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                activity.finish();
            }
            return false;
        }
        return true;
    }
	/*public boolean checkGooglePlayservices() {
		// Check status of Google Play Services
		int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);

		// Check Google Play Service Available
		try {
			if (status != ConnectionResult.SUCCESS) {
				GooglePlayServicesUtil.getErrorDialog(status, activity, 10).show();
				DATA.print("--not equal inside if");
				return false;
			}else {
				DATA.print("--inside else block");
				return true;
			}
		} catch (Exception e) {
			DATA.print("--inside exception");
			Log.e("GooglePlayServiceUtil", "" + e);
			return false;
		}
	}*/

    public void showWebviewDialog(final String webURL, String dialogTittle){
        DATA.print("-- showWebviewDialog url : "+webURL);
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lay_webview);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        ProgressBar pbWebview = dialog.findViewById(R.id.pbWebview);
        pbWebview.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);

        WebView webviewBill = (WebView) dialog.findViewById(R.id.webviewBill);
        webviewBill.getSettings().setJavaScriptEnabled(true);
        //webviewBill.getSettings().setPluginState(WebSettings.PluginState.ON);
        //webviewBill.getSettings().setLoadWithOverviewMode(true);
        //webviewBill.getSettings().setUseWideViewPort(true);
        //webviewBill.setWebViewClient(new Callback());
        //webviewBill.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //webviewBill.setHorizontalScrollBarEnabled(false);
        //webviewBill.getSettings().setLoadWithOverviewMode(true);
        //webviewBill.setWebChromeClient(new WebChromeClient());
        webviewBill.setInitialScale(100);
        webviewBill.getSettings().setUseWideViewPort(true);
        webviewBill.getSettings().setLoadWithOverviewMode(true);
        webviewBill.getSettings().setBuiltInZoomControls(true);

        TextView tvDialogTittle = (TextView) dialog.findViewById(R.id.dialogTittle);
        tvDialogTittle.setText(dialogTittle);

        webviewBill.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                //return super.shouldOverrideUrlLoading(view, request);
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                //DATA.dismissLoaderDefault();
                pbWebview.setVisibility(View.GONE);
            }
        });
        webviewBill.loadUrl(webURL);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(!(activity instanceof Signup)){
            dialog.findViewById(R.id.btnWebviewCancel).setVisibility(View.GONE);
            ((Button)dialog.findViewById(R.id.btnWebviewDone)).setText("Print");
        }




        //create object of print manager in your device
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        //create object of print adapter
        PrintDocumentAdapter printAdapter = webviewBill.createPrintDocumentAdapter();
        //provide name to your newly generated pdf file
        String jobName = activity.getResources().getString(R.string.app_name) + " Print Document";

        dialog.findViewById(R.id.btnWebviewDone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();

                try {
                    //open print dialog
                    printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
                }catch (Exception e){e.printStackTrace();}
            }
        });
        dialog.findViewById(R.id.btnWebviewCancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if(activity instanceof Signup){
                    if(webURL.equalsIgnoreCase(DATA.PRIVACY_POLICY_URL)){
                        ((Signup)activity).cbPrivacy.setChecked(false);
                    }else if(webURL.equalsIgnoreCase(DATA.USER_AGREEMENT_URL)){
                        ((Signup)activity).cbUserAgreement.setChecked(false);
                    }
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //DATA.showLoaderDefault(activity,"");
    }

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {

        if(apiName.equalsIgnoreCase(ApiManager.SYMP_COND)){
            if(! TextUtils.isEmpty(content)){
                prefs.edit().putString("symp_cond", content).commit();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.SYMP_COND_BHELTH)){
            if(! TextUtils.isEmpty(content)){
                prefs.edit().putString("symp_cond_bhealth", content).commit();
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.GET_COVID_SYMPTOMS)){
            if(! TextUtils.isEmpty(content)){
                prefs.edit().putString("symptoms_covid", content).commit();
            }
        } else if(apiName.equalsIgnoreCase(ApiManager.APP_LABELS_JSON)){
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray current_smoker_types = jsonObject.getJSONArray("current_smoker_types");
                prefs.edit().putString(PREF_APP_LBL_KEY,  content).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        else if(apiName.equalsIgnoreCase(ApiManager.GET_STATES)){
            if(! TextUtils.isEmpty(content)){
                prefs.edit().putString("states", content).commit();
            }
        }
        else if(apiName.equalsIgnoreCase(ApiManager.SUB_PATIENTS)){
            if(activity instanceof SubUsersList){
                ((SubUsersList) activity).displaySubUsersList(content);
            }else if(activity instanceof MainActivityNew){
                subUsersModels = parseSubUsers(content);
                if(lvSubusers != null){
                    lvSubusers.setAdapter(new SubUsersAdapter3(activity, subUsersModels));
                }
            }
        }else if(apiName.equalsIgnoreCase(ApiManager.HOSPITAL_DATA)){

			// {"status":"error","message":"Invalid Clinical Code"}
			//{"status":"success","hospital_data":{"id":"1","hospital_name":"OnlineCare Demo",
			// "folder_name":"onlinecare_newdesign","category_id":"2","hospital_zipcode":"48503","is_visible":"0"}}
			try {
				JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.getJSONObject("data").has("hospital_data")) {

                    JSONObject hospital_data = jsonObject.getJSONObject("data").getJSONObject("hospital_data");
                    String hospital_name = hospital_data.getString("hospital_name");
                    String id = hospital_data.getString("id");

                    String support_text = hospital_data.getString("support_text");
                    String hphones = hospital_data.getString("hphones");
                    String haddress = hospital_data.getString("haddress");

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(HOSP_ID_PREFS_KEY, id);

                    editor.putString("support_text", support_text);
                    editor.putString("hphones", hphones);
                    editor.putString("haddress", haddress);

                    editor.commit();

                    String paypal_clientid = hospital_data.getString("paypal_clientid");
                    if (TextUtils.isEmpty(paypal_clientid)) {
                        paypal_clientid = PaymentLiveCare.CONFIG_CLIENT_ID_JAMAL;
                    }
                    SharedPrefsHelper.getInstance().save(PaymentLiveCare.CONFIG_CLIENT_ID_PREFS_KEY, paypal_clientid);

                    String stripe_pub_key = hospital_data.getString("stripe_pub_key");
                    String stripe_secret_key = hospital_data.getString("stripe_pub_key");
                    if (TextUtils.isEmpty(stripe_pub_key)) {
                        stripe_pub_key = PaymentLiveCare.publishableKeyJamal;
                    }
                    SharedPrefsHelper.getInstance().save(PaymentLiveCare.PREFS_KEY_SRIPE_PK, stripe_pub_key);
                }
            } catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
    }


    public void loadAppLabels(){
		/*String savedJSON = prefs.getString("fcsas_form_json", "");
		if(!savedJSON.isEmpty()){
			parseFCSASFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}*/
        ApiManager.shouldShowLoader = false;
        ApiManager apiManager = new ApiManager(ApiManager.APP_LABELS_JSON,"get",null,this, activity);
        apiManager.loadURL();
    }

    public void loadStates()
    {
        ApiManager.shouldShowLoader = false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_STATES,"get",null,this, activity);
        apiManager.loadURL();
    }

    public void loadHospitalData(){
        ApiManager.shouldShowLoader = false;
        RequestParams params = new RequestParams("hospital_id", prefs.getString(HOSP_ID_PREFS_KEY, ""));
        ApiManager apiManager = new ApiManager(ApiManager.HOSPITAL_DATA,"post",params,this, activity);
        apiManager.loadURL();
    }


    //===========Check authtoken expiry======================
    public void checkLogin(String jsonStr, int status) {//Socket (logout) can't be emitted from here will work on this in future
        //{"error":"expired_token","error_description":"The access token provided has expired"}
        //{"error":"invalid_request","error_description":"Malformed auth header"}
        if (status == 401) {
            logout();
            SharedPreferences.Editor ed = prefs.edit();
            ed.putBoolean("HaveShownPrefs", false);
            ed.putBoolean("subUserSelected", false);
            ed.apply();

            Intent intent = new Intent(activity.getApplicationContext(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
        } else {
            try {
                JSONObject jsonObject = new JSONObject(jsonStr);
                if (jsonObject.optString("error").equalsIgnoreCase("expired_token") ||
                        jsonObject.optString("error").equalsIgnoreCase("invalid_request")) {
                    customToast.showToast("You session has been expired. Please login again", 0, 1);
                    logout();
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putBoolean("HaveShownPrefs", false);
                    ed.putBoolean("subUserSelected", false);
                    ed.apply();

                    Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                } else if (jsonObject.optString("error").equalsIgnoreCase("invalid_token")) {
                    customToast.showToast("You session has been expired. Please login again", 0, 1);
                    logout();
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putBoolean("HaveShownPrefs", false);
                    ed.putBoolean("subUserSelected", false);
                    ed.apply();

                    Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void logout(){
        RequestParams params = new RequestParams();
        params.put("id", prefs.getString("id", "0"));
        params.put("type", "patient");

        ApiManager apiManager = new ApiManager(ApiManager.LOGOUT,"post",params,GloabalMethods.this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }



    public void getFirebaseToken(){
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            DATA.print("-- getInstanceId failed"+ task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        saveToken(token);

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        //Log.d(TAG, msg);
                        //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void saveToken(String device_token) {

		/*pd.setMessage("Please wait...    ");
		pd.setCanceledOnTouchOutside(false);
		pd.show();*/

		/*if (device_token.contains("|ID|")) {
			device_token = device_token.substring(device_token.indexOf(":") +1);
		}*/
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity,client);
        RequestParams params = new RequestParams();

        params.put("patient_id", prefs.getString("id", "0"));
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("platform", "android");
        params.put("device_token", device_token);

        params.put("app_version", BuildConfig.VERSION_NAME);

        DATA.print("--params in saveToken : "+params.toString());

        client.post(DATA.baseUrl+"/saveToken", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                try{
                    String content = new String(response);
                    DATA.print("--reaponce in saveToken "+content);

                    JSONObject jsonObject = new JSONObject(content);
                    if(jsonObject.optString("is_online").equalsIgnoreCase("0")){

                        prefs.edit().clear().apply();
                        SharedPrefsHelper.getInstance().clearAllData();

                        Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        return;
                    }

                    compareVersions(content);

                }catch (Exception e){
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: saveToken, http status code: "+statusCode+" Byte responce: "+response);
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                try {
                    String content = new String(errorResponse);
                    DATA.print("--reaponce in onfailure saveToken: "+content);
                    new GloabalMethods(activity).checkLogin(content,statusCode);
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);

                }catch (Exception e1){
                    e1.printStackTrace();
                    //customToast.showToast(DATA.CMN_ERR_MSG,0,0);
                }
            }
        });

    }//end saveToken

    //===============Get Firebase Token Ends here


    //--------------------------------App Versions Check Starts--------------------------------------------------------------------
    public static boolean shouldUpdatePopAppear = true;
    public void compareVersions(String content){

        if(shouldUpdatePopAppear){
            shouldUpdatePopAppear = false;

            try {
                JSONObject jsonObject = new JSONObject(content);
                String app_version = jsonObject.getString("app_version");//"1.1.0"
                if(TextUtils.isEmpty(app_version)){
                    return;
                }
                String myAppVersion = BuildConfig.VERSION_NAME;//"1.0.19"
                String coloredVer = "<b><font color='"+ DATA.APP_THEME_RED_COLOR +"'>"+app_version+"</font></b>";

                String updateMsg = "New version "+coloredVer+" is available on the google play. Please keep your app up to date in order to get latest features and better performance.";
                try {

                    String myAppVerBeforeLastDecimal = myAppVersion.substring(0, myAppVersion.lastIndexOf("."));
                    String storeAppVerBeforeLastDecimal = app_version.substring(0, app_version.lastIndexOf("."));
                    DATA.print("-- substr BeforeLastDecimal myVer : "+myAppVerBeforeLastDecimal+" , Store Ver: "+ storeAppVerBeforeLastDecimal);

                    double myVerPre = Double.parseDouble(myAppVerBeforeLastDecimal);
                    double storeVerPre = Double.parseDouble(storeAppVerBeforeLastDecimal);

                    DATA.print("-- before last decimal after cast to doub myVer: "+myVerPre+" ** PlayStore ver: "+storeVerPre);

                    String myAppVerAfterLastDecimal = myAppVersion.substring(myAppVersion.lastIndexOf(".")+1);
                    String storeAppVerAfterLastDecimal = app_version.substring(app_version.lastIndexOf(".")+1);

                    DATA.print("-- substr AfterLastDecimal myVer : "+myAppVerAfterLastDecimal+" , Store Ver: "+ storeAppVerAfterLastDecimal);

                    int myVerPost = Integer.parseInt(myAppVerAfterLastDecimal);
                    int storeVerPost = Integer.parseInt(storeAppVerAfterLastDecimal);
                    DATA.print("-- after last decimal after cast to int myVer: "+myVerPost+" ** PlayStore ver: "+storeVerPost);


                    if(myVerPre < storeVerPre ||  myVerPost < storeVerPost){
                        showAppUpdateDialog(updateMsg);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    if(! myAppVersion.equalsIgnoreCase(app_version)){
                        showAppUpdateDialog(updateMsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showAppUpdateDialog(String updateMsg){

        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_app_update);
        dialogSupport.setCancelable(false);

        Button btnUpdateApp = dialogSupport.findViewById(R.id.btnUpdateApp);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        TextView tvUpdateMsg = dialogSupport.findViewById(R.id.tvUpdateMsg);

        tvUpdateMsg.setText(Html.fromHtml(updateMsg));

        btnUpdateApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialogSupport.dismiss();

                playStoreApp();
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

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }

    private void playStoreApp(){
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
        } catch (android.content.ActivityNotFoundException anfe) {
            anfe.printStackTrace();
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
        }
    }

    //--------------------------------App Versions Check Ends--------------------------------------------------------------------

    public void showAskMedHistoryDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_splash);
        dialogSupport.setCancelable(false);

        Button btnYesUptodate = dialogSupport.findViewById(R.id.btnYesUptodate);
        Button btnUpdateNow = dialogSupport.findViewById(R.id.btnUpdateNow);
        //Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        btnYesUptodate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //openActivity.open(MainActivityNew.class, true);
            }
        });
        btnUpdateNow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //openActivity.open(MainActivityNew.class, false);

                DATA.isFromFirstLogin = false;
                openActivity.open(MedicalHistory1.class, false);

                //openActivity.open(ActivityCareVisit.class,false);
                //activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
		/*btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

			}
		});*/
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }


    public void loadSymtomsConditions(){
        ApiManager apiManager = new ApiManager(ApiManager.SYMP_COND,"get",null,GloabalMethods.this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }

    public List<SymptomsModel> getAllSymptoms(){
        String symp_cond = prefs.getString("symp_cond","");
        List<SymptomsModel> symptomsModels = new ArrayList<>();
        if(!symp_cond.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(symp_cond);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String symptom_name = data.getJSONObject(i).getString("symptom_name");
                    List<ConditionsModel> conditionsModels = new ArrayList<>();
                    String conditionsStr = data.getJSONObject(i).getString("conditions");
                    if(! TextUtils.isEmpty(conditionsStr)){
                        JSONArray conditions = data.getJSONObject(i).getJSONArray("conditions");
                        for (int j = 0; j < conditions.length(); j++) {
                            String condition_id = conditions.getJSONObject(j).getString("id");
                            String symptom_id = conditions.getJSONObject(j).getString("symptom_id");
                            String condition_name = conditions.getJSONObject(j).getString("condition_name");

                            conditionsModels.add(new ConditionsModel(condition_id,symptom_id,condition_name));
                        }
                    }

                    symptomsModels.add(new SymptomsModel(id,symptom_name,conditionsModels));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
        return symptomsModels;
    }

    public void loadSymtomsConditionsBhealth(){
        ApiManager apiManager = new ApiManager(ApiManager.SYMP_COND_BHELTH,"get",null,GloabalMethods.this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }

    public List<SymptomsModel> getAllSymptomsBhealth(){
        String symp_cond = prefs.getString("symp_cond_bhealth","");
        List<SymptomsModel> symptomsModels = new ArrayList<>();
        if(!symp_cond.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(symp_cond);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String symptom_name = data.getJSONObject(i).getString("symptom_name");
                    List<ConditionsModel> conditionsModels = new ArrayList<>();
                    String conditionsStr = data.getJSONObject(i).getString("conditions");
                    if(! TextUtils.isEmpty(conditionsStr)){
                        JSONArray conditions = data.getJSONObject(i).getJSONArray("conditions");
                        for (int j = 0; j < conditions.length(); j++) {
                            String condition_id = conditions.getJSONObject(j).getString("id");
                            String symptom_id = conditions.getJSONObject(j).getString("symptom_id");
                            String condition_name = conditions.getJSONObject(j).getString("condition_name");

                            conditionsModels.add(new ConditionsModel(condition_id,symptom_id,condition_name));
                        }
                    }

                    symptomsModels.add(new SymptomsModel(id,symptom_name,conditionsModels));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
        return symptomsModels;
    }

    public void loadSymtomsCovid(){
        ApiManager apiManager = new ApiManager(ApiManager.GET_COVID_SYMPTOMS,"get",null,GloabalMethods.this, activity);
        ApiManager.shouldShowLoader = false;
        apiManager.loadURL();
    }

    public List<String> getCovidSymptoms(){
        String covidSymp = prefs.getString("symptoms_covid","");
        List<String> covidSymptoms = new ArrayList<>();
        if(!covidSymp.isEmpty()){
            try {
                JSONObject jsonObject = new JSONObject(covidSymp);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    covidSymptoms.add(data.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
        return covidSymptoms;
    }




    //===================BANNER Slider starts=============================================
    //BannerSlider bannerSlider;
    public void setupBannerSlider(BannerSlider bannerSlider, boolean isFromBHealth){

        //bannerSlider.setVisibility(View.GONE);//hide for onlinecare apps

        //bannerSlider = (BannerSlider) activity.findViewById(R.id.banner_slider1);
        bannerSlider.setMustAnimateIndicators(true);
        bannerSlider.setDefaultIndicator(IndicatorShape.ROUND_SQUARE);

        //bannerSlider.setInterval(5000);
        //bannerSlider.setLoopSlides(true);
        /*bannerSlider.setCustomIndicator(VectorDrawableCompat.create(getResources(),
                R.drawable.selected_slide_indicator, null),
                VectorDrawableCompat.create(getResources(),
                        R.drawable.unselected_slide_indicator, null));*/

       /* bannerSlider.setOnBannerClickListener(new OnBannerClickListener() {
            @Override
            public void onClick(int position) {
                //Toast.makeText(MainActivity.this, "Banner with position " + String.valueOf(position) + " clicked!", Toast.LENGTH_SHORT).show();
                DATA.addIntent(activity);
            }
        });*/

        new CountDownTimer(1000, 1000) {//total milis, interval b/w 2 in milis
            public void onTick(long millisUntilFinished) {
                DATA.print("seconds remaining: " + millisUntilFinished / 1000);
                //tvPlayingSeconds.setText("Playing video in:\n"+(millisUntilFinished / 1000)+" Seconds");
            }
            public void onFinish() {
                try{
                    bannerSlider.setInterval(4000);
                    bannerSlider.setLoopSlides(true);
                    bannerSlider.setCustomIndicator(
                            VectorDrawableCompat.create(activity.getResources(), R.drawable.selected_slide_indicator, null),
                            VectorDrawableCompat.create(activity.getResources(), R.drawable.unselected_slide_indicator, null));
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        addBanners(bannerSlider,isFromBHealth);

        //addRemoteBanners();
    }
    private void addBanners(BannerSlider bannerSlider, boolean isFromBHealth){

        List<Banner> drawableBanners=new ArrayList<>();
        if(isFromBHealth){
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_1).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_2).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_3).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_4).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_5).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_6).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_7).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_8).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_9).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_10).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_11).setScaleType(ImageView.ScaleType.FIT_CENTER));

        }else {

            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_1).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_2).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_3).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_4).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_5).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_6).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_7).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_8).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_9).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_10).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_bh_slider_11).setScaleType(ImageView.ScaleType.FIT_CENTER));

            //Add banners using image urls
         /*   drawableBanners.add(new DrawableBanner(R.drawable.ic_ad_emc).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_ad_emc1).setScaleType(ImageView.ScaleType.FIT_CENTER));
            drawableBanners.add(new DrawableBanner(R.drawable.ic_ad_emc2).setScaleType(ImageView.ScaleType.FIT_CENTER));*/
            //drawableBanners.add(new DrawableBanner(R.drawable.ic_ad_emc3).setScaleType(ImageView.ScaleType.FIT_XY));
        }

        bannerSlider.setBanners(drawableBanners);

    }
    private void addRemoteBanners(BannerSlider bannerSlider){
        List<Banner> remoteBanners=new ArrayList<>();
        //Add banners using image urls
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/4b88d2c1-9f95-4c51-867b-bf977b0caa8c/preview.gif"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/05e9b7d9-ade2-4aed-9cb4-9e24e5a3530d/preview.jpg"
        ));

        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/dcc07ea4-845a-463b-b5f0-4696574da5ed/preview.jpg"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/4b88d2c1-9f95-4c51-867b-bf977b0caa8c/preview.gif"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/76d63bbc-54a1-450a-a462-d90056be881b/preview.png"
        ));
        remoteBanners.add(new RemoteBanner(
                "https://assets.materialup.com/uploads/05e9b7d9-ade2-4aed-9cb4-9e24e5a3530d/preview.jpg"
        ));
        bannerSlider.setBanners(remoteBanners);

    }

    //===================BANNER Slider ends=============================================


    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    public static int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }



    public void showContrySelectionDialog(final EditText etCountryInput){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_countries);
        dialogSupport.setCanceledOnTouchOutside(false);

        EditText etSerchCountry = dialogSupport.findViewById(R.id.etSerchCountry);
        ListView lvCountry = dialogSupport.findViewById(R.id.lvCountry);

        dialogSupport.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSupport.dismiss();
            }
        });

        final ArrayList<CountryBean> countryBeans = DATA.loadCountries(activity);

        lvCountry.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                etCountryInput.setError(null);
                etCountryInput.setText(countryBeans.get(position).getName());

                new HideShowKeypad(activity).hidekeyboardOnDialog();

                dialogSupport.dismiss();
            }
        });

        final DialCountriesAdapter lvCountriesAdapter = new DialCountriesAdapter(activity, countryBeans);
        lvCountry.setAdapter(lvCountriesAdapter);

        etSerchCountry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(lvCountriesAdapter != null){
                    lvCountriesAdapter.filter(s.toString());
                }
            }
        });

        //dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogSupport.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);

        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);
    }





    //=========Behavioural Health/ Emotional Wellness Assessments helper methods starts=======================================



    //=============Behavioral Health Code

    private Dialog dialogWelnessOptions1;
    private void showWellnessOptionsDialog1() {
        // TODO Auto-generated method stub
		/*if(dialog != null){
			if(dialog.isShowing()){
				return;
			}
		}*/
        dialogWelnessOptions1 = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogWelnessOptions1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWelnessOptions1.setContentView(R.layout.dialog_home_options_1);

        ImageView ivConnectTherapist = dialogWelnessOptions1.findViewById(R.id.ivConnectTherapist);
        ImageView ivSelfHelpTools = dialogWelnessOptions1.findViewById(R.id.ivSelfHelpTools);

        ImageView ivClose = dialogWelnessOptions1.findViewById(R.id.ivClose);
        ImageView ivHome = dialogWelnessOptions1.findViewById(R.id.ivHome);
        ImageView imgMainAdDialog = dialogWelnessOptions1.findViewById(R.id.imgMainAd);


        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (view.getId()){

                    case R.id.ivConnectTherapist:
                        dialogWelnessOptions1.dismiss();
                        openActivity.open(ActivityConnectTherapist.class, false);
                        break;
                    case R.id.ivSelfHelpTools:
                        showWellnessOptionsDialog(false);
						/*new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle(getResources().getString(R.string.app_name))
								.setMessage("Comming Soon")
								.setPositiveButton("Done",null)
								.create().show();*/
                        break;
                    case R.id.imgMainAd:
                        DATA.addIntent(activity);
                        break;
                    case R.id.ivHome:
                        dialogWelnessOptions1.dismiss();
                        break;
                    case R.id.ivClose:
                        dialogWelnessOptions1.dismiss();
                        break;
                    default:
                        break;
                }
            }
        };

        ivClose.setOnClickListener(onClickListener);
        ivHome.setOnClickListener(onClickListener);
        imgMainAdDialog.setOnClickListener(onClickListener);

        ivConnectTherapist.setOnClickListener(onClickListener);
        ivSelfHelpTools.setOnClickListener(onClickListener);


        //dialog.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWelnessOptions1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogWelnessOptions1.show();
        dialogWelnessOptions1.getWindow().setAttributes(lp);
    }


    public static boolean isFromGetLiveCare;
    public void showWellnessOptionsDialog(boolean isFromGetLiveCare) {
        // TODO Auto-generated method stub
		/*if(dialog != null){
			if(dialog.isShowing()){
				return;
			}
		}*/

        GloabalMethods.isFromGetLiveCare = isFromGetLiveCare;

        Dialog dialogWellnessOptions = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogWellnessOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogWellnessOptions.setContentView(R.layout.dialog_home_options);

        TextView tvDepression = dialogWellnessOptions.findViewById(R.id.tvDepression);
        TextView tvAnxietyHealth = dialogWellnessOptions.findViewById(R.id.tvAnxietyHealth);
        TextView tvStress = dialogWellnessOptions.findViewById(R.id.tvStress);
        TextView tvPanicAttack = dialogWellnessOptions.findViewById(R.id.tvPanicAttack);
        TextView tvInsomnia = dialogWellnessOptions.findViewById(R.id.tvInsomnia);
        TextView tvFocusConc = dialogWellnessOptions.findViewById(R.id.tvFocusConc);

        TextView tvOCD = dialogWellnessOptions.findViewById(R.id.tvOCD);
        TextView tvScoff = dialogWellnessOptions.findViewById(R.id.tvScoff);
        TextView tvDAST = dialogWellnessOptions.findViewById(R.id.tvDAST);

        //TextView tvConnectTherapist = dialog.findViewById(R.id.tvConnectTherapist);
        //TextView tvSelfHelpTools = dialog.findViewById(R.id.tvSelfHelpTools);

        ImageView ivClose = dialogWellnessOptions.findViewById(R.id.ivClose);
        ImageView ivHome = dialogWellnessOptions.findViewById(R.id.ivHome);
        ImageView imgMainAdDialog = dialogWellnessOptions.findViewById(R.id.imgMainAd);


        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog.dismiss();

                switch (view.getId()){
                    case R.id.tvDepression:
                        //showAskDepDialog(1);
                        showAskDepDialog(7);
                        break;
                    case R.id.tvAnxietyHealth:
                        showAskDepDialog(8);
                        break;
                    case R.id.tvStress:
                        showAskDepDialog(6);
                        break;
                    case R.id.tvPanicAttack:
                        showAskDepDialog( 4);
                        break;
                    case R.id.tvInsomnia:
                        customToast.showToast("This section is under development",0,0);
                        break;
                    case R.id.tvFocusConc:
                        showAskDepDialog( 3);
                        break;
                    case R.id.tvOCD:
                        showAskDepDialog( 2);
                        break;
                    case R.id.tvScoff:
                        showAskDepDialog( 5);
                        break;
                    case R.id.tvDAST:
                        showAskDepDialog(9);
                        break;
                    case R.id.ivClose:
                        dialogWellnessOptions.dismiss();
                        break;
                    case R.id.ivHome:
                        dialogWellnessOptions.dismiss();
                        if(dialogWelnessOptions1 != null){
                            dialogWelnessOptions1.dismiss();
                        }
                        break;
                    case R.id.imgMainAd:
                        DATA.addIntent(activity);
                        break;
					/*case R.id.tvConnectTherapist:
						openActivity.open(ActivityConnectTherapist.class, false);
						break;
					case R.id.tvSelfHelpTools:
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle(getResources().getString(R.string.app_name))
								.setMessage("Comming Soon")
								.setPositiveButton("Done",null)
								.create().show();
						break;*/
                    default:
                        break;
                }
            }
        };

        tvDepression.setOnClickListener(onClickListener);
        tvAnxietyHealth.setOnClickListener(onClickListener);
        tvStress.setOnClickListener(onClickListener);
        tvPanicAttack.setOnClickListener(onClickListener);
        tvInsomnia.setOnClickListener(onClickListener);
        tvFocusConc.setOnClickListener(onClickListener);
        tvOCD.setOnClickListener(onClickListener);
        tvScoff.setOnClickListener(onClickListener);
        tvDAST.setOnClickListener(onClickListener);
        ivClose.setOnClickListener(onClickListener);
        ivHome.setOnClickListener(onClickListener);
        imgMainAdDialog.setOnClickListener(onClickListener);
        //tvConnectTherapist.setOnClickListener(onClickListener);
        //tvSelfHelpTools.setOnClickListener(onClickListener);


        //dialogWellnessOptions.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogWellnessOptions.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //dialogWellnessOptions.setCanceledOnTouchOutside(false);
        dialogWellnessOptions.show();
        dialogWellnessOptions.getWindow().setAttributes(lp);
    }

    private void showAskDepDialog(int formFlag){//Dialog wellnessOpDialog,
        //formFlag --- 1 = DepressionScale, 2 = OCD, 3 = fcsas , 4 = panic_attack, 5 = scoff, 6 = stress_questionaire,
        //7 = PHQ form, 8 = GAD form, 9 = DAST form
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_dep);
        dialogSupport.setCancelable(false);

        Button btnTakeNewSurvey = dialogSupport.findViewById(R.id.btnTakeNewSurvey);
        Button btnReviewPastSurvey = dialogSupport.findViewById(R.id.btnReviewPastSurvey);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        btnTakeNewSurvey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //wellnessOpDialog.dismiss();

                if(formFlag == 1){
                    openActivity.open(ActivityBecksDepression.class, false);
                }else if(formFlag == 2){
                    ActivityOCD_Form.formFlag = 1;
                    openActivity.open(ActivityOCD_Form.class, false);
                }else if(formFlag == 3){
                    ActivityOCD_Form.formFlag = 2;
                    openActivity.open(ActivityOCD_Form.class, false);
                }else if(formFlag == 4){
                    ActivityOCD_Form.formFlag = 3;
                    openActivity.open(ActivityOCD_Form.class, false);
                }else if(formFlag == 5){
                    ActivityOCD_Form.formFlag = 4;
                    openActivity.open(ActivityOCD_Form.class, false);
                }else if(formFlag == 6){
                    ActivityOCD_Form.formFlag = 5;//mo need to assign as activity seperite, but double check purpose
                    openActivity.open(ActivityStressQues_Form.class, false);
                }else if(formFlag == 7){
                    ActivityPHQ_Form.formFlag = 1;
                    openActivity.open(ActivityPHQ_Form.class, false);
                }else if(formFlag == 8){
                    ActivityPHQ_Form.formFlag = 2;
                    //dialogOptions.dismiss();
                    openActivity.open(ActivityPHQ_Form.class, false);
                }else if(formFlag == 9){
                    openActivity.open(ActivityDAST_Form.class, false);
                }
            }
        });
        btnReviewPastSurvey.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                //wellnessOpDialog.dismiss();


                if(formFlag == 1){
                    openActivity.open(ActivityDepInvList.class, false);
                }else if(formFlag == 2){
                    ActivityOCD_Form.formFlag = 1;
                    openActivity.open(ActivityOCD_List.class, false);
                }else if(formFlag == 3){
                    ActivityOCD_Form.formFlag = 2;
                    openActivity.open(ActivityOCD_List.class, false);
                }else if(formFlag == 4){
                    ActivityOCD_Form.formFlag = 3;
                    openActivity.open(ActivityOCD_List.class, false);
                }else if(formFlag == 5){
                    ActivityOCD_Form.formFlag = 4;
                    openActivity.open(ActivityOCD_List.class, false);
                }else if(formFlag == 6){
                    ActivityOCD_Form.formFlag = 5;
                    openActivity.open(ActivityOCD_List.class, false);
                }else if(formFlag == 7){
                    ActivityPHQ_Form.formFlag = 1;
                    openActivity.open(ActivityPhqList.class, false);
                }else if(formFlag == 8){
                    ActivityPHQ_Form.formFlag = 2;
                    //dialogOptions.dismiss();
                    openActivity.open(ActivityPhqList.class, false);
                }else if(formFlag == 9){
                    openActivity.open(ActivityDastList.class, false);
                }

                //openActivity.open(ActivityCareVisit.class,false);
                //activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
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

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }


    private EmoWelOptionBean emoWelOptionBean = null;
    public void showAskEmoWellDialog(ArrayList<EmoWelOptionBean> emoWelOptionBeans){
        emoWelOptionBean = null;
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_emowell);
        //dialogSupport.setCancelable(false);
        dialogSupport.setCanceledOnTouchOutside(false);

        Button btnEmoWellSubmit = dialogSupport.findViewById(R.id.btnEmoWellSubmit);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);

        RadioGroup rgEmoWell = dialogSupport.findViewById(R.id.rgEmoWell);

        rgEmoWell.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = dialogSupport.findViewById(checkedId);
                if(rb != null && rb.getTag() instanceof EmoWelOptionBean){
                    emoWelOptionBean = (EmoWelOptionBean) rb.getTag();
                    DATA.print("-- option selected : "+emoWelOptionBean.option_text);
                }
            }
        });

        for (int i = 0; i < emoWelOptionBeans.size(); i++) {
            RadioButton radioButton = new RadioButton(activity);

            if(i == 3){
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,dpToPx(activity, 40), 0,0);
                radioButton.setLayoutParams(lp);
            }else {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                lp.setMargins(0,dpToPx(activity, 5), 0,0);
                radioButton.setLayoutParams(lp);
            }
            radioButton.setText(emoWelOptionBeans.get(i).option_text);
            radioButton.setId(i);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(activity, R.color.theme_red)));
            }
            radioButton.setTag(emoWelOptionBeans.get(i));
            rgEmoWell.addView(radioButton);
        }


        btnEmoWellSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if(emoWelOptionBean != null){
                    dialogSupport.dismiss();
                    if(emoWelOptionBean.is_call.equalsIgnoreCase("1")){
                        showAskCall911Dialog();
                    }else {
                        //showWellnessOptionsDialog1();//this is old flow - New flow implemented
                        openActivity.open(GetLiveCareFormBhealth.class, false);//New Flow
                    }
                }else {
                    customToast.showToast("Please select an option", 0, 1);
                }

				/*int checkedRadioID = rgEmoWell.getCheckedRadioButtonId();
				if(checkedRadioID == R.id.rbEmoWell_1 || checkedRadioID == R.id.rbEmoWell_2 || checkedRadioID == R.id.rbEmoWell_3){

					dialogSupport.dismiss();
					showAskCall911Dialog();

				}else if(checkedRadioID == R.id.rbEmoWell_4 || checkedRadioID == R.id.rbEmoWell_5 || checkedRadioID == R.id.rbEmoWell_6){
					dialogSupport.dismiss();
					showWellnessOptionsDialog1();
				}else {
					customToast.showToast("Please select an option", 0, 1);
				}*/
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

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }


    private void showAskCall911Dialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_call911);
        //dialogSupport.setCancelable(false);
        dialogSupport.setCanceledOnTouchOutside(false);

        Button btnCall911 = dialogSupport.findViewById(R.id.btnCall911);
        Button btnCall2 = dialogSupport.findViewById(R.id.btnCall2);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);



        btnCall911.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(callIntent);
                }catch (Exception  e){e.printStackTrace();}
            }
        });

        btnCall2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                try {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+Uri.encode("1-844-886-4276")));//"1-800-273-8255" = kattring no
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    activity.startActivity(callIntent);
                }catch (Exception  e){e.printStackTrace();}
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

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }






    public void setAssesListHeader(){
        ImageView ivPatient = activity.findViewById(R.id.ivPatient);
        TextView tvPatientName = activity.findViewById(R.id.tvPatientName);
        Button btnPatientPage = activity.findViewById(R.id.btnPatientPage);
        if(tvPatientName != null){
            tvPatientName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
        }
        if(ivPatient != null){
            DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, ivPatient);
        }
        if(btnPatientPage!= null){
            btnPatientPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPatientDetail();
                }
            });
        }
    }

    public void showConfSaveAssesDialog(AssessSubmit assessSubmit, String assesTittle){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_saveassess_opt);

        TextView tvEditAssessDialog = (TextView) dialogSupport.findViewById(R.id.tvEditAssessDialog);
        TextView tvSaveAssesDialog = (TextView) dialogSupport.findViewById(R.id.tvSaveAssesDialog);
        TextView tvConfirmAssess = (TextView) dialogSupport.findViewById(R.id.tvConfirmAssess);
        TextView tvCancelAssess = dialogSupport.findViewById(R.id.tvCancelAssess);

        TextView tvAssesTittle = dialogSupport.findViewById(R.id.tvAssesTittle);

        tvAssesTittle.setText(assesTittle);


        tvCancelAssess.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSupport.dismiss();
            }
        });

        tvEditAssessDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
            }
        });
        tvSaveAssesDialog.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                assessSubmit.submitAssessment("0");
            }
        });

        tvConfirmAssess.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                assessSubmit.submitAssessment("1");
            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();
    }


    public static Activity activityAssesList, activityAssesForm;
    public static Dialog dialogAsses;
    public void goToPatientDetail(){
        if(activityAssesList != null){
            activityAssesList.finish();
        }
        if(activityAssesForm != null){
            activityAssesForm.finish();
        }
        if(dialogAsses != null){
            dialogAsses.dismiss();
        }
    }





    public void showAssesSavedDialog(int score, String severity, String self_text){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_asses_saved);
        dialogSupport.setCancelable(false);

        Button btnLiveCare = dialogSupport.findViewById(R.id.btnLiveCare);
        Button btnMakeApptmnt = dialogSupport.findViewById(R.id.btnMakeApptmnt);
        Button btnDone = dialogSupport.findViewById(R.id.btnDone);
        Button btnCancel = dialogSupport.findViewById(R.id.btnCancel);
        TextView tvScore = dialogSupport.findViewById(R.id.tvScore);
        TextView tvScoreCri = dialogSupport.findViewById(R.id.tvScoreCri);
        TextView tvEliveOrAppt = dialogSupport.findViewById(R.id.tvEliveOrAppt);
        TextView tvSelfText = dialogSupport.findViewById(R.id.tvSelfText);

		/*tvScore.setText("Total score : "+score);

		tvScoreCri.setText("Severity : "+severity);

		tvSelfText.setText("Self Care : \n"+self_text);*/

        tvScore.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Total score : </font>"+ score));

        tvScoreCri.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Severity : </font>"+ severity));

        tvSelfText.setText(Html.fromHtml("<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Self Care : \n</font>"+ self_text));

        if(activity instanceof ActivityOCD_Form && ActivityOCD_Form.formFlag == 4){
            if(score >= 2){
                tvScoreCri.append("\nAssessment needed for anorexia nervosa and bulimia.");
            }else {
                tvScoreCri.append("\nNo assessment needed for anorexia nervosa and bulimia.");
            }
        }


        if(isFromGetLiveCare){
            btnDone.setVisibility(View.VISIBLE);
            btnLiveCare.setVisibility(View.GONE);
            btnMakeApptmnt.setVisibility(View.GONE);
            btnCancel.setVisibility(View.GONE);
            tvEliveOrAppt.setVisibility(View.GONE);
        }else {
            btnDone.setVisibility(View.GONE);
            btnLiveCare.setVisibility(View.VISIBLE);
            btnMakeApptmnt.setVisibility(View.VISIBLE);
            btnCancel.setVisibility(View.VISIBLE);
            tvEliveOrAppt.setVisibility(View.VISIBLE);
        }

        btnLiveCare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                dialogSupport.dismiss();
                //openActivity.open(GetLiveCare.class, true);
                Intent intent = new Intent(activity, ActivityConnectTherapist.class);
                intent.putExtra("isForDirectLiveCare", true);
                activity.startActivity(intent);
                activity.finish();

				/*AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Info")
						.setMessage("Would you like to attach survey results ?")
						.setPositiveButton("Yes", null)
						.setNegativeButton("Not Now", null)
						.create();
				alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialogSupport.dismiss();
						//openActivity.open(GetLiveCare.class, true);
						Intent intent = new Intent(activity, ActivityConnectTherapist.class);
						intent.putExtra("isForDirectLiveCare", true);
						activity.startActivity(intent);
						activity.finish();
					}
				});
				alertDialog.show();*/
            }
        });

        btnMakeApptmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialogSupport.dismiss();
                //openActivity.open(ActivityAppntmtOptions.class, true);
                Intent intent = new Intent(activity, ActivityConnectTherapist.class);
                intent.putExtra("isForDirectApptmnt", true);
                activity.startActivity(intent);
                activity.finish();

				/*AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
						.setTitle("Info")
						.setMessage("Would you like to attach survey results ?")
						.setPositiveButton("Yes", null)
						.setNegativeButton("Not Now", null)
						.create();
				alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						dialogSupport.dismiss();
						//openActivity.open(ActivityAppntmtOptions.class, true);
						Intent intent = new Intent(activity, ActivityConnectTherapist.class);
						intent.putExtra("isForDirectApptmnt", true);
						activity.startActivity(intent);
						activity.finish();
					}
				});
				alertDialog.show();*/
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialogSupport.dismiss();
            activity.finish();
        });
        btnDone.setOnClickListener(v -> {
            dialogSupport.dismiss();
            activity.finish();
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);*/
    }

    //=========Behavioural Health/ Emotional Wellness Assessments helper methods ends=======================================



    public void showFullScreenAlertDialog(String tittle, String message){
        Dialog dialogFullScrAlert = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogFullScrAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogStudioInfo.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogFullScrAlert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogFullScrAlert.setContentView(R.layout.dialog_alert_fullscr);

        ImageView ivCancel = (ImageView) dialogFullScrAlert.findViewById(R.id.ivCancel);
        Button btnDone = (Button) dialogFullScrAlert.findViewById(R.id.btnDone);

        TextView tvTittle =  dialogFullScrAlert.findViewById(R.id.tvTittle);
        TextView tvMessage = dialogFullScrAlert.findViewById(R.id.tvMessage);

        tvTittle.setText(tittle);
        tvMessage.setText(message);


        btnDone.setOnClickListener(v -> {dialogFullScrAlert.dismiss();});
        ivCancel.setOnClickListener(v -> dialogFullScrAlert.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogFullScrAlert.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogFullScrAlert.setCanceledOnTouchOutside(false);
        dialogFullScrAlert.show();
        dialogFullScrAlert.getWindow().setAttributes(lp);

    }




    public void showAskCovidDialog(boolean isForPastList){
        final Dialog dialogAskCovid = new Dialog(activity);
        dialogAskCovid.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAskCovid.setContentView(R.layout.dialog_ask_covid);
        dialogAskCovid.setCancelable(false);

        Button btnSelf = dialogAskCovid.findViewById(R.id.btnSelf);
        Button btnFamilyMember = dialogAskCovid.findViewById(R.id.btnFamilyMember);
        Button btnCancel = dialogAskCovid.findViewById(R.id.btnCancel);


        btnSelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAskCovid.dismiss();

                if(isForPastList){
                    openActivity.open(ActivityCovidFormList.class, false);
                }else {
                    openActivity.open(ActivityCovidTest.class, false);
                }
            }
        });

        btnFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAskCovid.dismiss();

                showSubUsersDialog(isForPastList);
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialogAskCovid.dismiss();
            //activity.finish();
        });

        dialogAskCovid.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogAskCovid.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAskCovid.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogAskCovid.show();
        dialogAskCovid.getWindow().setAttributes(lp);*/
    }


    public static SubUsersModel subUsersModel;
    ListView lvSubusers;
    ArrayList<SubUsersModel> subUsersModels;
    public void showSubUsersDialog(boolean isForPastList){
        Dialog dialogSubUsers = new Dialog(activity,R.style.TransparentThemeH4B);
        dialogSubUsers.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogSubUsers.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogSubUsers.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogSubUsers.setContentView(R.layout.dialog_subusers);

        ImageView ivCancel =  dialogSubUsers.findViewById(R.id.ivCancel);
        lvSubusers =  dialogSubUsers.findViewById(R.id.lvSubusers);



        lvSubusers.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dialogSubUsers.dismiss();

                subUsersModel = subUsersModels.get(position);

                if(isForPastList){
                    ActivityCovidTest.isForFamilyMemberCovid = true;
                    openActivity.open(ActivityCovidFormList.class, false);
                }else {
                    Intent intent = new Intent(activity, ActivityCovidTest.class);
                    intent.putExtra("isForFamilyMemberCovid", true);
                    activity.startActivity(intent);
                }
            }
        });


        ivCancel.setOnClickListener(v -> dialogSubUsers.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSubUsers.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogSubUsers.setCanceledOnTouchOutside(false);
        dialogSubUsers.show();
        dialogSubUsers.getWindow().setAttributes(lp);


        subUsersModels = SharedPrefsHelper.getInstance().getAllSubUsers();
        if(subUsersModels.isEmpty()){
            loadSubPatients();
        }else {
            lvSubusers.setAdapter(new SubUsersAdapter3(activity, subUsersModels));
        }
    }



    public void loadSubPatients(){
        RequestParams params = new RequestParams();
        params.put("patient_id", SharedPrefsHelper.getInstance().getParentUser().id);

        ApiManager apiManager = new ApiManager(ApiManager.SUB_PATIENTS,"post",params,GloabalMethods.this, activity);
        apiManager.loadURL();
    }

    public ArrayList<SubUsersModel> parseSubUsers(String content){

        ArrayList<SubUsersModel> subUsersModels = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(content);
            JSONArray sub_users = jsonObject.getJSONArray("sub_users");

            SubUsersModel parentUser = SharedPrefsHelper.getInstance().getParentUser();

            subUsersModels.add(parentUser);

            SubUsersModel temp;

            for(int j = 0; j<sub_users.length(); j++) {

                temp = new SubUsersModel();

                temp.id = sub_users.getJSONObject(j).getString("id");
                temp.firstName = sub_users.getJSONObject(j).getString("first_name");
                temp.lastName = sub_users.getJSONObject(j).getString("last_name");
                temp.patient_id = sub_users.getJSONObject(j).getString("patient_id");
                temp.image = sub_users.getJSONObject(j).getString("image");
                temp.gender = sub_users.getJSONObject(j).getString("gender");
                temp.dob = sub_users.getJSONObject(j).getString("dob");
                temp.marital_status = sub_users.getJSONObject(j).getString("marital_status");
                //temp.relationship = "Family Member - "+sub_users.getJSONObject(j).getString("relationship");
                temp.relationship = sub_users.getJSONObject(j).getString("relationship");
                temp.occupation = sub_users.getJSONObject(j).getString("occupation");

                temp.insurance = sub_users.getJSONObject(j).getString("insurance");

                temp.is_primary = sub_users.getJSONObject(j).getString("is_primary");

                temp.phone = sub_users.getJSONObject(j).getString("phone");
                temp.residency = sub_users.getJSONObject(j).getString("residency");
                temp.city = sub_users.getJSONObject(j).getString("city");
                temp.state = sub_users.getJSONObject(j).getString("state");
                temp.country = sub_users.getJSONObject(j).getString("country");
                temp.zipcode = sub_users.getJSONObject(j).getString("zipcode");

                subUsersModels.add(temp);

                temp  = null;
            }

            SharedPrefsHelper.getInstance().saveAllSubUsers(subUsersModels);

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
        }

        return subUsersModels;
    }


    public void showDebugLogsDialog(){
        final Dialog dialogDebugLogs = new Dialog(activity);
        dialogDebugLogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDebugLogs.setContentView(R.layout.dialog_debug_logs);
        dialogDebugLogs.setCanceledOnTouchOutside(false);

        CheckBox cbDebugMode = dialogDebugLogs.findViewById(R.id.cbDebugMode);
        Button btnDone = dialogDebugLogs.findViewById(R.id.btnDone);

        cbDebugMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefsHelper.getInstance().save("debug_logs", isChecked);
            String msg = "Debug mode is set to "+ (isChecked ? "on" : "off");
            customToast.showToast(msg,0,0);
        });
        cbDebugMode.setChecked(SharedPrefsHelper.getInstance().get("debug_logs", false));

        btnDone.setOnClickListener(v -> dialogDebugLogs.dismiss());

        dialogDebugLogs.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogDebugLogs.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogDebugLogs.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogDebugLogs.show();
        dialogDebugLogs.getWindow().setAttributes(lp);*/
    }

}
