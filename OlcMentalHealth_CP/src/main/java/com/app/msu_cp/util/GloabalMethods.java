package com.app.msu_cp.util;

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
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.app.msu_cp.ActivityBecksDepression;
import com.app.msu_cp.ActivityDepInvList;
import com.app.msu_cp.ActivityFollowUps_2;
import com.app.msu_cp.ActivityOTPT_Notes;
import com.app.msu_cp.ActivitySOAP;
import com.app.msu_cp.ActivityTcmDetails;
import com.app.msu_cp.ActivityTelemedicineServices;
import com.app.msu_cp.BuildConfig;
import com.app.msu_cp.Login;
import com.app.msu_cp.R;
import com.app.msu_cp.adapters.DMEFaxAdapter;
import com.app.msu_cp.adapters.DialCountriesAdapter;
import com.app.msu_cp.adapters.GvAttachedSurveyAdapter;
import com.app.msu_cp.adapters.PharmacyAdapter;
import com.app.msu_cp.api.ApiCallBack;
import com.app.msu_cp.api.ApiManager;
import com.app.msu_cp.model.AttachSurveyBean;
import com.app.msu_cp.model.ConditionsModel;
import com.app.msu_cp.model.CountryBean;
import com.app.msu_cp.model.FaxHistBean;
import com.app.msu_cp.model.PastHistoryBean;
import com.app.msu_cp.model.PharmacyBean;
import com.app.msu_cp.model.PharmacyCategoryBean;
import com.app.msu_cp.model.SpecialityModel;
import com.app.msu_cp.model.SymptomsModel;
import com.app.msu_cp.reliance.ActivityCompany;
import com.app.msu_cp.reliance.CompanyBean;
import com.app.msu_cp.reliance.assessment.ActivityDAST_Form;
import com.app.msu_cp.reliance.assessment.ActivityDastList;
import com.app.msu_cp.reliance.assessment.ActivityPHQ_Form;
import com.app.msu_cp.reliance.assessment.ActivityPhqList;
import com.app.msu_cp.reliance.assessment.AssessSubmit;
import com.app.msu_cp.reliance.assessment.new_assesment.ActivityOCD_Form;
import com.app.msu_cp.reliance.assessment.new_assesment.ActivityOCD_List;
import com.app.msu_cp.reliance.assessment.new_assesment.ActivityStressQues_Form;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cz.msebera.android.httpclient.Header;

/**
 * Created by Engr G M on 7/24/2017.
 */

public class GloabalMethods implements ApiCallBack {

    Activity activity;
    CheckInternetConnection checkInternetConnection;
    CustomToast customToast;
    SharedPreferences prefs;
    SharedPrefsHelper sharedPrefsHelper;
    OpenActivity openActivity;

    public GloabalMethods(Activity activity) {
        this.activity = activity;
        checkInternetConnection = new CheckInternetConnection(activity);
        customToast = new CustomToast(activity);
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        sharedPrefsHelper = SharedPrefsHelper.getInstance();
        openActivity = new OpenActivity(activity);
    }


    Dialog dialogSendMessage;

    public void initMsgDialogEMS() {
        dialogSendMessage = new Dialog(activity);
        dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSendMessage.setContentView(R.layout.dialog_send_msg);
        final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
        TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);

        TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);
        /*if (DATA.isFromDocToDoc) {
            tvMsgPatientName.setText("To: "+DATA.selectedDrName);
        } else {
            tvMsgPatientName.setText("To: "+DATA.selectedUserCallName);
        }*/

        tvMsgPatientName.setText("Currently command center is busy. Please try again latter or you can send text message for EMS. If it is an emergency call 911.");

        btnSendMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (etMsg.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {
                        sendEMSmessage(etMsg.getText().toString());
                    } else {
                        Toast.makeText(activity, DATA.NO_NETWORK_MESSAGE, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSendMessage.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogSendMessage.show();
        dialogSendMessage.getWindow().setAttributes(lp);
    }


    public void sendEMSmessage(final String message_text) {

        DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();

        final String api = "ems/sendmessage";
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("message_text", message_text);
        //params.put("patient_id", ActivityFollowUps_4.selectedFollowupBean.patient_id);
        params.put("patient_id", "0");
        params.put("from", "doctor");
        params.put("to", "ems");

        DATA.print("-- api: " + api);
        DATA.print("-- params: " + params.toString());

        client.post(DATA.baseUrl + api, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(response);
                    DATA.print("--reaponce in ems/sendmessage: " + content);

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        int success = jsonObject.getInt("success");
                        String message = jsonObject.getString("message");
                        //customToast.showToast(message, 0, 1);
                        if (dialogSendMessage != null) {
                            dialogSendMessage.dismiss();
                        }

                        AlertDialog alertDialog = new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
                                .setMessage(message)
                                .setPositiveButton("Done", null).create();
                        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                activity.finish();
                            }
                        });
                        alertDialog.show();

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 1);
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: " + api + ", http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail in API: " + api + " : " + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }


    //----------------------------------------------------------------PHARMACY----------------------------------------------------------------------

    public static final String SHOW_PHARMACY_BROADCAST_ACTION = BuildConfig.APPLICATION_ID + ".onlinecare_show_patient_selected_pharmacy";

    public void updatePharmacy(String pharmacy_id, String pharmacyCat, final PharmacyBean selectedPharmacyBean) {

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();
        params.put("patient_id", DATA.selectedUserCallId);//prefs.getString("getLiveCareApptID", "")
        params.put("pharmacy_id", pharmacy_id);
        params.put("pharmacy_cat", pharmacyCat);

        DATA.print("-- params in updatePharmacy " + params.toString());


        client.post(DATA.baseUrl + "updatePharmacy", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in updatePharmacy " + content);
                    // --reaponce in updatePharmacy {"msg":"","status":"success"}

                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        String status = jsonObject.getString("status");

                        if (status.equalsIgnoreCase("success")) {
                            customToast.showToast("Pharmacy selected", 0, 1);
								/*if (dialog!= null) {
									dialog.dismiss();
								}*/
                            showPharmacyMap(selectedPharmacyBean);

                            getPharmacy("", false);
                        } else {
                            customToast.showToast(jsonObject.getString("msg"), 0, 1);
                        }

                    } catch (JSONException e) {
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                        e.printStackTrace();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: updatePharmacy, http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- on fail updatePharmacy " + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }//end updatePharmacy

    String selectedPharmacyCategory = "";
    String selectedPharmacyId = "";
    //boolean shouldLoadPharmacy = false;
    PharmacyAdapter pharmacyAdapter;
    ListView lvPharmacy;
    Dialog dialog;

    public void showPharmacyDialog() {
        // TODO Auto-generated method stub
        if (dialog != null) {
            if (dialog.isShowing()) {
                return;
            }
        }
        dialog = new Dialog(activity, R.style.TransparentThemeH4B);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setContentView(R.layout.dialog_pharmacy_selection);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

        Spinner spPharmacyCategory = (Spinner) dialog.findViewById(R.id.spPharmacyCategory);
        lvPharmacy = (ListView) dialog.findViewById(R.id.lvPharmacy);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

			 /*if (checkInternetConnection.isConnectedToInternet()) {
				 getPharmacy("", activity);
			 } else {
				 customToast.showToast(NO_NETWORK_MESSAGE, 0, 1);
			 }*/


        ArrayAdapter<PharmacyCategoryBean> dataAdapter2 = new ArrayAdapter<PharmacyCategoryBean>(activity,
                android.R.layout.simple_spinner_item, pharmacyCategoryBeans);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPharmacyCategory.setAdapter(dataAdapter2);

        lvPharmacy.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                selectedPharmacyId = pharmacyBeans.get(arg2).id;

                if (checkInternetConnection.isConnectedToInternet()) {
                    updatePharmacy(selectedPharmacyId, selectedPharmacyCategory, pharmacyBeans.get(arg2));
                } else {
                    customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 1);
                }
            }
        });

        pharmacyAdapter = new PharmacyAdapter(activity, pharmacyBeans);
        lvPharmacy.setAdapter(pharmacyAdapter);

        spPharmacyCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                if (arg2 == 0) {
                    selectedPharmacyCategory = "";
                } else {
                    selectedPharmacyCategory = pharmacyCategoryBeans.get(arg2).key;
                }

                getPharmacy(selectedPharmacyCategory, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
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
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        if (!selectedPharmacyBean.id.isEmpty()) {
            showPharmacyMap(selectedPharmacyBean);
        }
    }


    public static ArrayList<PharmacyCategoryBean> pharmacyCategoryBeans;
    public static ArrayList<PharmacyBean> pharmacyBeans;

    //public static PharmacyCategoryBean selectedPharmacyCategoryBean;
    public static PharmacyBean selectedPharmacyBean;

    public void getPharmacy(String pharmacyCat, final boolean shouldShowDialog) {

        DATA.print("-- url in getPharmacy: " + DATA.baseUrl + "getPharmacy/" + DATA.selectedUserCallId + "/" + pharmacyCat);

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        client.get(DATA.baseUrl + "getPharmacy/" + DATA.selectedUserCallId + "/" + pharmacyCat, new AsyncHttpResponseHandler() {//prefs.getString("getLiveCareApptID", "")
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in getPharmacy " + content);

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

                        if (PrescriptionsPopup.tvPrescPharmacy != null) {
                            PrescriptionsPopup.tvPrescPharmacy.setText(StoreName);
                        }
                        if (PrescriptionsPopup.tvPrescPharmacyPhone != null) {
                            PrescriptionsPopup.tvPrescPharmacyPhone.setText(PhonePrimary);
                        }

                        if (lvPharmacy != null) {
                            pharmacyAdapter = new PharmacyAdapter(activity, pharmacyBeans);
                            lvPharmacy.setAdapter(pharmacyAdapter);
                        } else {
                            DATA.print("-- lvPharmacy is null in getPharmacy");
                        }

                        if (shouldShowDialog) {
                            showPharmacyDialog();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: getPharmacy, http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("-- on fail getPharmacy " + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }//end getPharmacy

    private GoogleMap googleMap;
    View mapView;

    public void showPharmacyMap(PharmacyBean pharmacyBean) {
        //if (googleMap == null) {   to call the  code each time
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
                //DATA.print(""+styleStatus);

                //=======================Start existing Code here================================================

                //googleMap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();
                googleMap.clear();
                googleMap.setTrafficEnabled(true);
                if (ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    googleMap.setMyLocationEnabled(true);
                }
                googleMap.setBuildingsEnabled(true);

                Double pharmacyLat = 0.0;
                Double pharmacyLng = 0.0;

                DATA.print("-- in showPharmacyMap " + pharmacyBean.StoreName);
                String distance = pharmacyBean.distance;

                try {
                    pharmacyLat = Double.parseDouble(pharmacyBean.Latitude);
                    pharmacyLng = Double.parseDouble(pharmacyBean.Longitude);
                    distance = String.format("%.2f", Double.parseDouble(distance));
                } catch (Exception e) {
                    // TODO: handle exception
                    pharmacyLat = 0.0;
                    pharmacyLng = 0.0;
                    return;
                }

                LatLng cpos = new LatLng(pharmacyLat, pharmacyLng);

                MarkerOptions marker = new MarkerOptions().position(cpos).title(pharmacyBean.StoreName)
                        .snippet("Distance: " + distance + " K.M");
                // .icon(BitmapDescriptorFactory.fromResource(R.drawable.doctor_icon_small));
                // adding marker
                googleMap.addMarker(marker).showInfoWindow();


                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 12);
                googleMap.animateCamera(update);


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

    //----------------------------------------------------------------PHARMACY----------------------------------------------------------------------


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


    //-------------------------addSOAP notes dialog----------------------------------------------
    public void showAddSOAPDialog() {
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.my_alert_dialog);

        TextView tvAddSOAPVV = (TextView) dialogSupport.findViewById(R.id.tvAddSOAPVV);
        TextView tvAddSOAPEmpty = (TextView) dialogSupport.findViewById(R.id.tvAddSOAPEmpty);
        TextView tvAddSOAPNOTNOW = (TextView) dialogSupport.findViewById(R.id.tvAddSOAPNOTNOW);

        TextView tvAddOTNotes = (TextView) dialogSupport.findViewById(R.id.tvAddOTNotes);
        SharedPreferences prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
        if (prefs.getString("doctor_category", "").equalsIgnoreCase("ot") || prefs.getString("doctor_category", "").equalsIgnoreCase("pt")) {
            tvAddOTNotes.setVisibility(View.VISIBLE);
            tvAddOTNotes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogSupport.dismiss();
                    activity.startActivity(new Intent(activity, ActivityOTPT_Notes.class));
                    activity.finish();
                }
            });
        } else {
            tvAddOTNotes.setVisibility(View.GONE);
        }

        tvAddSOAPVV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                ActivitySOAP.addSoapFlag = 1;
                activity.startActivity(new Intent(activity, ActivityTelemedicineServices.class));
                if (activity instanceof ActivityFollowUps_2) {
                    activity.finish();
                }
            }
        });
        tvAddSOAPEmpty.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                ActivitySOAP.addSoapFlag = 2;
                activity.startActivity(new Intent(activity, ActivityTelemedicineServices.class));

                if (activity instanceof ActivityFollowUps_2) {
                    activity.finish();
                }
            }
        });
        tvAddSOAPNOTNOW.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogSupport.show();
    }
    //-------------------------addSOAP notes dialog----------------------------------------------


    //-------------------------DME Fax dialog for Service referral forms----------------------------------------------
    ListView lvDmeFax;
    public static String faxId = "";

    public void showDmeFaxDialog(final EditText etName, final EditText etEmail, final EditText etFax) {
        final Dialog medicationsDialog = new Dialog(activity);
        medicationsDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        medicationsDialog.setContentView(R.layout.dialog_dme_fax);

        final EditText etSearchQuery = (EditText) medicationsDialog.findViewById(R.id.etSearchQuery);
        ImageView ivSearchQuery = (ImageView) medicationsDialog.findViewById(R.id.ivSearchQuery);
        lvDmeFax = (ListView) medicationsDialog.findViewById(R.id.lvDmeFax);


        ivSearchQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                RequestParams params = new RequestParams();
                //params.put("user_id", prefs.getString("id", ""));
                params.put("keyword", etSearchQuery.getText().toString());
                ApiManager apiManager = new ApiManager(ApiManager.GET_FAX_HISTORY, "post", params, GloabalMethods.this, activity);
                apiManager.loadURL();
            }
        });

        /*medicationsDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                try {
                    lvMedications.setAdapter(new HistoryMediAdapter(activity,medicationList));
                    etMedicationsAddMedication.setText("");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/

        lvDmeFax.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                etName.setText(faxHistBeens.get(position).name);
                etEmail.setText(faxHistBeens.get(position).email);
                etFax.setText(faxHistBeens.get(position).fax_number);

                faxId = faxHistBeens.get(position).id;

                medicationsDialog.dismiss();
            }
        });

        medicationsDialog.findViewById(R.id.ivClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                medicationsDialog.dismiss();
            }
        });

        medicationsDialog.show();

        etSearchQuery.setText(etName.getText().toString());
        etSearchQuery.setSelection(etSearchQuery.getText().toString().length());

        RequestParams params = new RequestParams();
        //params.put("user_id", prefs.getString("id", ""));
        params.put("keyword", etSearchQuery.getText().toString());
        ApiManager apiManager = new ApiManager(ApiManager.GET_FAX_HISTORY, "post", params, GloabalMethods.this, activity);
        apiManager.loadURL();
    }

    ArrayList<FaxHistBean> faxHistBeens;

    @Override
    public void fetchDataCallback(String httpStatus, String apiName, String content) {
        if (apiName.equalsIgnoreCase(ApiManager.GET_FAX_HISTORY)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray data = jsonObject.getJSONArray("data");
                if (data.length() == 0) {
                    customToast.showToast("No results found for the given name", 0, 1);
                }
                faxHistBeens = new ArrayList<>();
                FaxHistBean bean;
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String name = data.getJSONObject(i).getString("name");
                    String email = data.getJSONObject(i).getString("email");
                    String fax_number = data.getJSONObject(i).getString("fax_number");

                    bean = new FaxHistBean(id, name, email, fax_number);
                    faxHistBeens.add(bean);
                    bean = null;
                }

                DMEFaxAdapter dmeFaxAdapter = new DMEFaxAdapter(activity, faxHistBeens);
                lvDmeFax.setAdapter(dmeFaxAdapter);

            } catch (Exception e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.contains(ApiManager.PATIENT_DETAIL)) {
            try {
                JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

                ActivityTcmDetails.ptPolicyNo = jsonObject.getJSONObject("patient_data").getString("policy_number");
                ActivityTcmDetails.ptDOB = jsonObject.getJSONObject("patient_data").getString("birthdate");
                ActivityTcmDetails.ptAddress = jsonObject.getJSONObject("patient_data").getString("residency");
                ActivityTcmDetails.ptZipcode = jsonObject.getJSONObject("patient_data").getString("zipcode");
                ActivityTcmDetails.ptPhone = jsonObject.getJSONObject("patient_data").getString("phone");

                DATA.selectedUserCallName = jsonObject.getJSONObject("patient_data").getString("first_name") + " " +
                        jsonObject.getJSONObject("patient_data").getString("last_name");
                //ptRefDate  = jsonObject.getJSONObject("patient_data").getString("phone");
                //ptRefDr  = jsonObject.getJSONObject("patient_data").getString("phone")+" "
                //+jsonObject.getJSONObject("patient_data").getString("phone");
                //ptPriHomeCareDiag  = jsonObject.getJSONObject("patient_data").getString("phone");


            } catch (Exception e) {
                e.printStackTrace();
                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SEND_MESSAGE)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                if (jsonObject.has("success")) {
                    dialogSendMessage.dismiss();
                    customToast.showToast("Your message has been sent", 0, 1);
                } else if (jsonObject.has("error")) {
                    dialogSendMessage.dismiss();
                    new AlertDialog.Builder(activity).setTitle("Info").setMessage(jsonObject.getString("message")).
                            setPositiveButton("OK", null).show();
                } else {
                    Toast.makeText(activity, "Opps! Some thing went wrong please try again", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                DATA.print("--json eception sendMsg" + content);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.SYMP_COND)) {
            if (!TextUtils.isEmpty(content)) {
                prefs.edit().putString("symp_cond", content).commit();
            }
        } else if (apiName.contains(ApiManager.GET_MEDICAL_HISTORY)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray diseases = jsonObject.getJSONArray("diseases");
                Type listType = new TypeToken<ArrayList<PastHistoryBean>>() {
                }.getType();
                ArrayList<PastHistoryBean> pastHistoryBeans = new Gson().fromJson(diseases.toString(), listType);
                if (pastHistoryBeans != null) {
                    sharedPrefsHelper.savePastHistoryDiag(pastHistoryBeans);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GET_ALL_COMPANIES)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray companies = jsonObject.getJSONArray("companies");
                Type listType = new TypeToken<ArrayList<CompanyBean.AllCompanyBean>>() {
                }.getType();
                List<CompanyBean.AllCompanyBean> allCompanyBeans = new Gson().fromJson(companies + "", listType);
                //lvCompanyPatients.setAdapter(new CompanyPatientAdapter(activity, companyPatientBeans));
                if (allCompanyBeans != null) {
                    allCompanyBeans.add(0, new CompanyBean().new AllCompanyBean("", "", "All", "0"));
                    sharedPrefsHelper.saveAllCompanies(allCompanyBeans);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.GET_COMPANIES)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray companies = jsonObject.getJSONArray("companies");
                sharedPrefsHelper.save(ActivityCompany.COMPANY_PREFS_KEY, content);
                /*Type listType = new TypeToken<ArrayList<CompanyBean>>() {}.getType();
                List<CompanyBean> companyBeans = new Gson().fromJson(companies+"", listType);
                if(companyBeans != null){
                    sharedPrefsHelper.saveCompanies(companyBeans);
                }*/

            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        } else if (apiName.equalsIgnoreCase(ApiManager.APP_LABELS_JSON)) {
            try {
                JSONObject jsonObject = new JSONObject(content);
                JSONArray current_smoker_types = jsonObject.getJSONArray("current_smoker_types");
                prefs.edit().putString(ApiManager.PREF_APP_LBL_KEY, content).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
    //-------------------------DME Fax dialog for Service referral forms----------------------------------------------

    public void getPatientDetails() {
        if (!DATA.selectedUserCallId.isEmpty()) {
            ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL + "/" + DATA.selectedUserCallId, "get", null, GloabalMethods.this, activity);
            ApiManager.shouldShowPD = false;
            apiManager.loadURL();
        }
    }

    public void getPatientMedicalHistory() {
        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_MEDICAL_HISTORY + "/" + DATA.selectedUserCallId, "get", null, GloabalMethods.this, activity);
        apiManager.loadURL();
    }


    //---------------------------------SEND MESSAGE MODULE-------------------------------------------------

    //Dialog dialogSendMessage;
    public void initMsgDialog() {
        dialogSendMessage = new Dialog(activity);
        dialogSendMessage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSendMessage.setContentView(R.layout.dialog_send_msg);
        final EditText etMsg = (EditText) dialogSendMessage.findViewById(R.id.etMessage);
        TextView btnSendMsg = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsg);
        TextView btnSendMsgToFamily = (TextView) dialogSendMessage.findViewById(R.id.btnSendMsgToFamily);

        TextView tvMsgPatientName = (TextView) dialogSendMessage.findViewById(R.id.tvMsgPatientName);
        if (DATA.isFromDocToDoc) {
            tvMsgPatientName.setText("To: " + DATA.selectedDrName);
            btnSendMsgToFamily.setVisibility(View.GONE);
        } else {
            tvMsgPatientName.setText("To: " + DATA.selectedUserCallName);
            btnSendMsgToFamily.setVisibility(View.VISIBLE);
            if (ActivityTcmDetails.primary_patient_id.isEmpty()) {
                btnSendMsgToFamily.setEnabled(false);
            } else {
                btnSendMsgToFamily.setEnabled(true);
            }
        }


        btnSendMsg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (etMsg.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {
                        if (DATA.isFromDocToDoc) {
                            sendMsgSP(DATA.selectedDrId, EasyAES.encryptString(etMsg.getText().toString()));
                        } else {
                            sendMsg(DATA.selectedUserCallId, EasyAES.encryptString(etMsg.getText().toString()));
                        }
                        //sendMsg(DATA.selectedUserCallId, etMsg.getText().toString());
                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        btnSendMsgToFamily.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (etMsg.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please type your message", Toast.LENGTH_SHORT).show();
                } else {
                    if (checkInternetConnection.isConnectedToInternet()) {
                        sendMsg(ActivityTcmDetails.primary_patient_id, EasyAES.encryptString(etMsg.getText().toString()));
                    } else {
                        customToast.showToast(DATA.NO_NETWORK_MESSAGE, 0, 0);
                    }
                }

            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSendMessage.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogSendMessage.show();
        dialogSendMessage.getWindow().setAttributes(lp);
    }

    public void sendMsg(String patientId, String msgText) {

        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("patient_id", patientId);
        params.put("message_text", msgText);
        params.put("from", "specialist");
        params.put("to", "patient");

        ApiManager apiManager = new ApiManager(ApiManager.SEND_MESSAGE, "post", params, GloabalMethods.this, activity);
        apiManager.loadURL();
    }//end sendMsg


    public void sendMsgSP(String doctorId, String msgText) {

        RequestParams params = new RequestParams();

        params.put("doctor_id", doctorId);
        params.put("patient_id", prefs.getString("id", "0"));
        params.put("message_text", msgText);
        params.put("from", "specialist");
        params.put("to", DATA.selectedDoctorsModel.current_app);//"doctor"

        ApiManager apiManager = new ApiManager(ApiManager.SEND_MESSAGE, "post", params, GloabalMethods.this, activity);
        apiManager.loadURL();
    }//end sendMsgSP

    //---------------------------------SEND MESSAGE MODULE-------------------------------------------------------------------------------------


    //===========Check authtoken expiry======================
    public void checkLogin(String jsonStr, int status) {//Socket (logout) can't be emitted from here will work on this in future
        //{"error":"expired_token","error_description":"The access token provided has expired"}
        //{"error":"invalid_request","error_description":"Malformed auth header"}
        //DATA.print("-- checkLogin json "+ jsonStr);
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
                    customToast.showToast("Your session has been expired. Please login again", 0, 1);
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

    public void logout() {
        RequestParams params = new RequestParams();
        params.put("id", prefs.getString("id", "0"));
        params.put("type", "doctor");

        ApiManager apiManager = new ApiManager(ApiManager.LOGOUT, "post", params, GloabalMethods.this, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }


    public void getFirebaseToken() {
        // Get token
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            DATA.print("-- getInstanceId failed" + task.getException());
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

        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        RequestParams params = new RequestParams();

        params.put("doctor_id", prefs.getString("id", "0"));
        params.put("timezone", TimeZone.getDefault().getID());
        params.put("platform", "android");
        params.put("device_token", device_token);

        params.put("app_version", BuildConfig.VERSION_NAME);

        DATA.print("-- params in saveDoctorToken : " + params.toString());

        client.post(DATA.baseUrl + "/saveDoctorToken", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(response);
                    DATA.print("--reaponce in saveDoctorToken: " + content);

                    //--reaponce in saveDoctorToken {"success":1,"message":"Updated.","app_version":"1.0.13","app_link":"https:\/\/play.google.com\/store\/apps\/details?id=com.app.emcuradr"}

                    JSONObject jsonObject = new JSONObject(content);
                    if (jsonObject.optString("is_online").equalsIgnoreCase("0")) {

                        prefs.edit().clear().apply();
                        SharedPrefsHelper.getInstance().clearAllData();

                        Intent intent = new Intent(activity.getApplicationContext(), Login.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        activity.startActivity(intent);
                        activity.finish();
                        return;
                    }

                    compareVersions(content);

                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: saveDoctorToken, http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //customProgressDialog.dismissProgressDialog();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onFailure in saveDoctorToken " + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }// end saveDoctorToken


    //--------------------------------App Versions Check Starts--------------------------------------------------------------------
    public static boolean shouldUpdatePopAppear = true;

    public void compareVersions(String content) {
        if (shouldUpdatePopAppear) {
            shouldUpdatePopAppear = false;

            try {
                JSONObject jsonObject = new JSONObject(content);
                String app_version = jsonObject.getString("app_version");//"1.1.0"
                if (TextUtils.isEmpty(app_version)) {
                    return;
                }
                String myAppVersion = BuildConfig.VERSION_NAME;//"1.0.14"
                String coloredVer = "<b><font color='" + DATA.APP_THEME_RED_COLOR + "'>" + app_version + "</font></b>";

                String updateMsg = "New version " + coloredVer + " is available on the google play. Please keep your app up to date in order to get latest features and better performance.";
                try {

                    String myAppVerBeforeLastDecimal = myAppVersion.substring(0, myAppVersion.lastIndexOf("."));
                    String storeAppVerBeforeLastDecimal = app_version.substring(0, app_version.lastIndexOf("."));
                    DATA.print("-- substr BeforeLastDecimal myVer : " + myAppVerBeforeLastDecimal + " , Store Ver: " + storeAppVerBeforeLastDecimal);

                    double myVerPre = Double.parseDouble(myAppVerBeforeLastDecimal);
                    double storeVerPre = Double.parseDouble(storeAppVerBeforeLastDecimal);

                    DATA.print("-- before last decimal after cast to doub myVer: " + myVerPre + " ** PlayStore ver: " + storeVerPre);

                    String myAppVerAfterLastDecimal = myAppVersion.substring(myAppVersion.lastIndexOf(".") + 1);
                    String storeAppVerAfterLastDecimal = app_version.substring(app_version.lastIndexOf(".") + 1);

                    DATA.print("-- substr AfterLastDecimal myVer : " + myAppVerAfterLastDecimal + " , Store Ver: " + storeAppVerAfterLastDecimal);

                    int myVerPost = Integer.parseInt(myAppVerAfterLastDecimal);
                    int storeVerPost = Integer.parseInt(storeAppVerAfterLastDecimal);
                    DATA.print("-- after last decimal after cast to int myVer: " + myVerPost + " ** PlayStore ver: " + storeVerPost);


                    if (myVerPre < storeVerPre || myVerPost < storeVerPost) {
                        showAppUpdateDialog(updateMsg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (!myAppVersion.equalsIgnoreCase(app_version)) {
                        showAppUpdateDialog(updateMsg);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showAppUpdateDialog(String updateMsg) {
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

    private void playStoreApp() {
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


    public void loadSymtomsConditions() {
        ApiManager apiManager = new ApiManager(ApiManager.SYMP_COND, "get", null, GloabalMethods.this, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
    }

    public List<SymptomsModel> getAllSymptoms() {
        String symp_cond = prefs.getString("symp_cond", "");
        List<SymptomsModel> symptomsModels = new ArrayList<>();
        if (!symp_cond.isEmpty()) {
            try {
                JSONObject jsonObject = new JSONObject(symp_cond);
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    String id = data.getJSONObject(i).getString("id");
                    String symptom_name = data.getJSONObject(i).getString("symptom_name");
                    List<ConditionsModel> conditionsModels = new ArrayList<>();
                    String conditionsStr = data.getJSONObject(i).getString("conditions");
                    if (!TextUtils.isEmpty(conditionsStr)) {
                        JSONArray conditions = data.getJSONObject(i).getJSONArray("conditions");
                        for (int j = 0; j < conditions.length(); j++) {
                            String condition_id = conditions.getJSONObject(j).getString("id");
                            String symptom_id = conditions.getJSONObject(j).getString("symptom_id");
                            String condition_name = conditions.getJSONObject(j).getString("condition_name");

                            conditionsModels.add(new ConditionsModel(condition_id, symptom_id, condition_name));
                        }
                    }

                    symptomsModels.add(new SymptomsModel(id, symptom_name, conditionsModels));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
            }
        }
        return symptomsModels;
    }


    public void showWebviewDialog(final String webURL, String dialogTittle) {
        DATA.print("-- showWebviewDialog url : " + webURL);
        final Dialog dialog = new Dialog(activity, R.style.TransparentThemeH4B);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lay_webview);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

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
        ProgressBar pbWebview = dialog.findViewById(R.id.pbWebview);
        tvDialogTittle.setText(dialogTittle);

        pbWebview.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);

        webviewBill.getSettings().setJavaScriptEnabled(true);

        webviewBill.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                //DATA.dismissLoaderDefault();
                pbWebview.setVisibility(View.GONE);
            }
        });

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Oauthtoken","Bearer "+prefs.getString("access_token",""));
        webviewBill.loadUrl(webURL,extraHeaders);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /*if(!(activity instanceof Signup)){
            dialog.findViewById(R.id.btnWebviewCancel).setVisibility(View.GONE);
            ((Button)dialog.findViewById(R.id.btnWebviewDone)).setText("Done");
        }*/

        dialog.findViewById(R.id.btnWebviewDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        /*dialog.findViewById(R.id.btnWebviewCancel).setOnClickListener(new View.OnClickListener() {
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
        });*/

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //DATA.showLoaderDefault(activity,"");
    }


    public void showWebviewDialog2(final String webURL, String dialogTittle) {//for beHealth Assess view with view past btn
        DATA.print("-- showWebviewDialog url : " + webURL);
        final Dialog dialog = new Dialog(activity, R.style.TransparentThemeH4B);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.lay_webview2);
        //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

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
        ProgressBar pbWebview = dialog.findViewById(R.id.pbWebview);
        tvDialogTittle.setText(dialogTittle);

        pbWebview.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);

        webviewBill.getSettings().setJavaScriptEnabled(true);

        webviewBill.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                //DATA.dismissLoaderDefault();
                pbWebview.setVisibility(View.GONE);
            }
        });

        Map<String, String> extraHeaders = new HashMap<String, String>();
        extraHeaders.put("Oauthtoken","Bearer "+prefs.getString("access_token",""));
        webviewBill.loadUrl(webURL,extraHeaders);

        ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        /*if(!(activity instanceof Signup)){
            dialog.findViewById(R.id.btnWebviewCancel).setVisibility(View.GONE);
            ((Button)dialog.findViewById(R.id.btnWebviewDone)).setText("Done");
        }*/

        dialog.findViewById(R.id.btnWebviewDone).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

                if (dialogTittle.equalsIgnoreCase(ActivityOCD_Form.OCD_FORM_NAME)) {
                    ActivityOCD_Form.formFlag = 1;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityOCD_Form.FCSAS_FORM_NAME)) {
                    ActivityOCD_Form.formFlag = 2;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityOCD_Form.PANIC_ATACK_FORM_NAME)) {
                    ActivityOCD_Form.formFlag = 3;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityOCD_Form.SCOFF_FORM_NAME)) {
                    ActivityOCD_Form.formFlag = 4;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityOCD_Form.STRESS_QUES_FORM_NAME)) {
                    ActivityOCD_Form.formFlag = 5;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityPhqList.PHQ_FORM_TITLE)) {
                    ActivityPHQ_Form.formFlag = 1;
                    openActivity.open(ActivityPhqList.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityPhqList.GAD_FORM_TITLE)) {
                    ActivityPHQ_Form.formFlag = 2;
                    openActivity.open(ActivityPhqList.class, false);
                } else if (dialogTittle.equalsIgnoreCase(ActivityDastList.DAST_FORM_TITLE)) {
                    openActivity.open(ActivityDastList.class, false);
                }
            }
        });
        /*dialog.findViewById(R.id.btnWebviewCancel).setOnClickListener(new View.OnClickListener() {
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
        });*/

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        //DATA.showLoaderDefault(activity,"");
    }


    public void setAssesListHeader() {
        ImageView ivPatient = activity.findViewById(R.id.ivPatient);
        TextView tvPatientName = activity.findViewById(R.id.tvPatientName);
        Button btnPatientPage = activity.findViewById(R.id.btnPatientPage);
        if (tvPatientName != null) {
            tvPatientName.setText(DATA.selectedUserCallName);
        }
        if (ivPatient != null) {
            DATA.loadImageFromURL(DATA.selectedUserCallImage, R.drawable.icon_call_screen, ivPatient);
        }
        if (btnPatientPage != null) {
            btnPatientPage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToPatientDetail();
                }
            });
        }
    }


    public void showConfSaveAssesDialog(AssessSubmit assessSubmit, String assesTittle) {
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_saveassess_opt);

        TextView tvEditAssessDialog = (TextView) dialogSupport.findViewById(R.id.tvEditAssessDialog);
        TextView tvSaveAssesDialog = (TextView) dialogSupport.findViewById(R.id.tvSaveAssesDialog);
        TextView tvConfirmAssess = (TextView) dialogSupport.findViewById(R.id.tvConfirmAssess);

        TextView tvAssesTittle = dialogSupport.findViewById(R.id.tvAssesTittle);

        tvAssesTittle.setText(assesTittle);

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

    public void goToPatientDetail() {
        if (activityAssesList != null) {
            activityAssesList.finish();
        }
        if (activityAssesForm != null) {
            activityAssesForm.finish();
        }
        if (dialogAsses != null) {
            dialogAsses.dismiss();
        }
    }


    //====================assessments code ends here


    public static void openKeyboardOnEditText(Activity activity, EditText editText) {
        editText.setSelection(editText.getText().length());

        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }


    //====get All Companies -- Reliance Apps ---- called from home
    public void getAllCompany() {
        RequestParams params = new RequestParams();
        //params.put("doctor_id", prefs.getString("id", ""));
        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_ALL_COMPANIES, "post", params, GloabalMethods.this, activity);
        apiManager.loadURL();
    }

    public void getCompany() {
        RequestParams params = new RequestParams();
        params.put("doctor_id", prefs.getString("id", ""));

        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.GET_COMPANIES, "post", params, GloabalMethods.this, activity);
        apiManager.loadURL();
    }

    public void getSpecialties() {
        //DATA.showLoaderDefault(activity, "");
        AsyncHttpClient client = new AsyncHttpClient();
        ApiManager.addHeader(activity, client);
        client.get(DATA.baseUrl + "getSpecialties", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // called when response HTTP status is "200 OK"
                //DATA.dismissLoaderDefault();
                try {
                    String content = new String(response);

                    DATA.print("--reaponce in getSpecialties: " + content);
                    try {
                        JSONArray jsonArray = new JSONArray(content);
                        Type listType = new TypeToken<ArrayList<SpecialityModel>>() {
                        }.getType();
                        ArrayList<SpecialityModel> specialityModels = new Gson().fromJson(jsonArray.toString(), listType);
                        if (specialityModels != null) {
                            sharedPrefsHelper.saveAllSpecialities(specialityModels);
                        }
                        //GM commented
						/*db.deleteSpecialities();
						for (int i = 0; i < jsonArray.length(); i++) {
							String id = jsonArray.getJSONObject(i).getString("id");
							String speciality_name = jsonArray.getJSONObject(i).getString("speciality_name");
							db.insertSpeciality(id,speciality_name);
						}*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                        customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    DATA.print("-- responce onsuccess: getSpecialties, http status code: " + statusCode + " Byte responce: " + response);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //DATA.dismissLoaderDefault();
                try {
                    String content = new String(errorResponse);
                    DATA.print("--onfail getSpecialties: " + content);
                    new GloabalMethods(activity).checkLogin(content, statusCode);
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);

                } catch (Exception e1) {
                    e1.printStackTrace();
                    customToast.showToast(DATA.CMN_ERR_MSG, 0, 0);
                }
            }
        });

    }


    public void loadAppLabels() {
		/*String savedJSON = prefs.getString("fcsas_form_json", "");
		if(!savedJSON.isEmpty()){
			parseFCSASFormData(savedJSON);
			ApiManager.shouldShowLoader = false;
		}*/
        ApiManager.shouldShowPD = false;
        ApiManager apiManager = new ApiManager(ApiManager.APP_LABELS_JSON, "get", null, this, activity);
        apiManager.loadURL();
    }


    public void showContrySelectionDialog(final EditText etCountryInput) {
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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (lvCountriesAdapter != null) {
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


    //=======================Emotional Wellness Assessments starts========================================

    public void showWellnessOptionsDialog() {
        // TODO Auto-generated method stub
		/*if(dialog != null){
			if(dialog.isShowing()){
				return;
			}
		}*/

        //GloabalMethods.isFromGetLiveCare = isFromGetLiveCare;

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


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //dialog.dismiss();

                switch (view.getId()) {
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
                        showAskDepDialog(4);
                        break;
                    case R.id.tvInsomnia:
                        customToast.showToast("This section is under development", 0, 0);
                        break;
                    case R.id.tvFocusConc:
                        showAskDepDialog(3);
                        break;
                    case R.id.tvOCD:
                        showAskDepDialog(2);
                        break;
                    case R.id.tvScoff:
                        showAskDepDialog(5);
                        break;
                    case R.id.tvDAST:
                        showAskDepDialog(9);
                        break;
                    case R.id.ivClose:
                        dialogWellnessOptions.dismiss();
                        break;
                    case R.id.ivHome:
                        dialogWellnessOptions.dismiss();
						/*if(dialogWelnessOptions1 != null){
							dialogWelnessOptions1.dismiss();
						}*/
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

        GloabalMethods.dialogAsses = dialogWellnessOptions;
    }


    private void showAskDepDialog(int formFlag) {//Dialog wellnessOpDialog,
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

                if (formFlag == 1) {
                    openActivity.open(ActivityBecksDepression.class, false);
                } else if (formFlag == 2) {
                    ActivityOCD_Form.formFlag = 1;
                    openActivity.open(ActivityOCD_Form.class, false);
                } else if (formFlag == 3) {
                    ActivityOCD_Form.formFlag = 2;
                    openActivity.open(ActivityOCD_Form.class, false);
                } else if (formFlag == 4) {
                    ActivityOCD_Form.formFlag = 3;
                    openActivity.open(ActivityOCD_Form.class, false);
                } else if (formFlag == 5) {
                    ActivityOCD_Form.formFlag = 4;
                    openActivity.open(ActivityOCD_Form.class, false);
                } else if (formFlag == 6) {
                    ActivityOCD_Form.formFlag = 5;//mo need to assign as activity seperite, but double check purpose
                    openActivity.open(ActivityStressQues_Form.class, false);
                } else if (formFlag == 7) {
                    ActivityPHQ_Form.formFlag = 1;
                    openActivity.open(ActivityPHQ_Form.class, false);
                } else if (formFlag == 8) {
                    ActivityPHQ_Form.formFlag = 2;
                    //dialogOptions.dismiss();
                    openActivity.open(ActivityPHQ_Form.class, false);
                } else if (formFlag == 9) {
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


                if (formFlag == 1) {
                    openActivity.open(ActivityDepInvList.class, false);
                } else if (formFlag == 2) {
                    ActivityOCD_Form.formFlag = 1;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (formFlag == 3) {
                    ActivityOCD_Form.formFlag = 2;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (formFlag == 4) {
                    ActivityOCD_Form.formFlag = 3;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (formFlag == 5) {
                    ActivityOCD_Form.formFlag = 4;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (formFlag == 6) {
                    ActivityOCD_Form.formFlag = 5;
                    openActivity.open(ActivityOCD_List.class, false);
                } else if (formFlag == 7) {
                    ActivityPHQ_Form.formFlag = 1;
                    openActivity.open(ActivityPhqList.class, false);
                } else if (formFlag == 8) {
                    ActivityPHQ_Form.formFlag = 2;
                    //dialogOptions.dismiss();
                    openActivity.open(ActivityPhqList.class, false);
                } else if (formFlag == 9) {
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

        try {
            btnReviewPastSurvey.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    public void parseAttachedSurveyResponce(String content) {

        try {

            if (new JSONObject(content).get("data") instanceof JSONObject) {
                JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

                String id = jsonObject.getString("id");
                String livecare_id = jsonObject.getString("livecare_id");

                String ocd_form = jsonObject.getString("ocd_form");
                String fcsas_form = jsonObject.getString("fcsas_form");
                String panic_attack_form = jsonObject.getString("panic_attack_form");
                String scoff_form = jsonObject.getString("scoff_form");
                String stress_form = jsonObject.getString("stress_form");
                String phq_form = jsonObject.getString("phq_form");
                String gad_form = jsonObject.getString("gad_form");
                String dast_form = jsonObject.getString("dast_form");

                ArrayList<AttachSurveyBean> attachSurveyBeans = new ArrayList<>();


                if (!TextUtils.isEmpty(ocd_form) && !ocd_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(ocd_form, ActivityOCD_Form.OCD_FORM_NAME, R.drawable.ic_ocd_form, DATA.baseUrl + "assessments/view_ocd/" + ocd_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(fcsas_form) && !fcsas_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(fcsas_form, ActivityOCD_Form.FCSAS_FORM_NAME, R.drawable.ic_focus_conc, DATA.baseUrl + "assessments/view_fcsas/" + fcsas_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(panic_attack_form) && !panic_attack_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(panic_attack_form, ActivityOCD_Form.PANIC_ATACK_FORM_NAME, R.drawable.ic_panic_attack, DATA.baseUrl + "assessments/panic_attack_view/" + panic_attack_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(scoff_form) && !scoff_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(scoff_form, ActivityOCD_Form.SCOFF_FORM_NAME, R.drawable.ic_eating_disorder, DATA.baseUrl + "assessments/scoff_view/" + scoff_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(stress_form) && !stress_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(stress_form, ActivityOCD_Form.STRESS_QUES_FORM_NAME, R.drawable.ic_stress, DATA.baseUrl + "assessments/stress_view/" + stress_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(phq_form) && !phq_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(phq_form, ActivityPhqList.PHQ_FORM_TITLE, R.drawable.ic_depression, DATA.baseUrl + "assessments/phq_view/" + phq_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(gad_form) && !gad_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(gad_form, ActivityPhqList.GAD_FORM_TITLE, R.drawable.ic_anxiety, DATA.baseUrl + "assessments/gad_view/" + gad_form + "?platform=mobile"));
                }
                if (!TextUtils.isEmpty(dast_form) && !dast_form.equalsIgnoreCase("0")) {
                    attachSurveyBeans.add(new AttachSurveyBean(dast_form, ActivityDastList.DAST_FORM_TITLE, R.drawable.ic_substance_abuse, DATA.baseUrl + "assessments/dast_view/" + dast_form + "?platform=mobile"));
                }

                showSelfAssessListDialog(attachSurveyBeans);
            } else {
                String msgforNoSurvey = new JSONObject(content).getString("message");
                customToast.showToast(msgforNoSurvey, 0, 1);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            customToast.showToast(DATA.JSON_ERROR_MSG, 0, 0);
        }
    }


    public void showSelfAssessListDialog(ArrayList<AttachSurveyBean> attachSurveyBeans) {
        Dialog dialogAssessList = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogAssessList.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialogAssessList.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialogAssessList.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialogAssessList.setContentView(R.layout.dialog_self_assess_list);

        ImageView ivCancel = (ImageView) dialogAssessList.findViewById(R.id.ivCancel);
        Button btnDone = (Button) dialogAssessList.findViewById(R.id.btnDone);

        TextView tvTittle = dialogAssessList.findViewById(R.id.tvTittle);
        //tvTittle.setText(tittle);

        GridView gvSelfAssessList = dialogAssessList.findViewById(R.id.gvSelfAssessList);
        TextView tvNoData = dialogAssessList.findViewById(R.id.tvNoData);

        gvSelfAssessList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new GloabalMethods(activity).showWebviewDialog2(attachSurveyBeans.get(position).surveyWebviewURL, attachSurveyBeans.get(position).surveyName);
            }
        });

        gvSelfAssessList.setAdapter(new GvAttachedSurveyAdapter(activity, attachSurveyBeans));

        int visi = attachSurveyBeans.isEmpty() ? View.VISIBLE : View.GONE;
        tvNoData.setVisibility(visi);


        btnDone.setOnClickListener(v -> {
            dialogAssessList.dismiss();
        });
        ivCancel.setOnClickListener(v -> dialogAssessList.dismiss());

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAssessList.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogAssessList.setCanceledOnTouchOutside(false);
        dialogAssessList.show();
        dialogAssessList.getWindow().setAttributes(lp);

    }


    //=======================Emotional Wellness Assessments ends========================================


    public void showDebugLogsDialog() {
        final Dialog dialogDebugLogs = new Dialog(activity);
        dialogDebugLogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDebugLogs.setContentView(R.layout.dialog_debug_logs);
        dialogDebugLogs.setCanceledOnTouchOutside(false);

        CheckBox cbDebugMode = dialogDebugLogs.findViewById(R.id.cbDebugMode);
        Button btnDone = dialogDebugLogs.findViewById(R.id.btnDone);

        cbDebugMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPrefsHelper.getInstance().save("debug_logs", isChecked);
            String msg = "Debug mode is set to " + (isChecked ? "on" : "off");
            customToast.showToast(msg, 0, 0);
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
