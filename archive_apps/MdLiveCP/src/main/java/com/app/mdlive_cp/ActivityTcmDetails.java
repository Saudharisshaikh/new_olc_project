package com.app.mdlive_cp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.app.mdlive_cp.adapters.DocToDocAdapter;
import com.app.mdlive_cp.adapters.VVReportImagesAdapter2;
import com.app.mdlive_cp.api.ApiManager;
import com.app.mdlive_cp.careplan.ActivityCarePlan;
import com.app.mdlive_cp.model.FollowupBean;
import com.app.mdlive_cp.reliance.assessment.ActivityAdlForm;
import com.app.mdlive_cp.reliance.assessment.ActivityAdlList;
import com.app.mdlive_cp.reliance.assessment.ActivityAssessmentsSummary;
import com.app.mdlive_cp.reliance.assessment.ActivityDAST_Form;
import com.app.mdlive_cp.reliance.assessment.ActivityDastList;
import com.app.mdlive_cp.reliance.assessment.ActivityDietAssesForm;
import com.app.mdlive_cp.reliance.assessment.ActivityDietAssesList;
import com.app.mdlive_cp.reliance.assessment.ActivityOtEvaForm;
import com.app.mdlive_cp.reliance.assessment.ActivityOtEvaList;
import com.app.mdlive_cp.reliance.assessment.ActivityOtProfileForm;
import com.app.mdlive_cp.reliance.assessment.ActivityOtProfileList;
import com.app.mdlive_cp.reliance.assessment.ActivityPHQ_Form;
import com.app.mdlive_cp.reliance.assessment.ActivityPhqList;
import com.app.mdlive_cp.reliance.assessment.ActivitySDHSList;
import com.app.mdlive_cp.reliance.assessment.ActivitySDHS_Form;
import com.app.mdlive_cp.reliance.assessment.ActivitySelfMgtForm;
import com.app.mdlive_cp.reliance.assessment.ActivitySelfMgtList;
import com.app.mdlive_cp.reliance.assessment.new_assesment.ActivityOCD_Form;
import com.app.mdlive_cp.reliance.assessment.new_assesment.ActivityOCD_List;
import com.app.mdlive_cp.reliance.assessment.new_assesment.ActivityStressQues_Form;
import com.app.mdlive_cp.reliance.careteam.ActivityCareTeam;
import com.app.mdlive_cp.reliance.counter.ActivityCounter;
import com.app.mdlive_cp.reliance.idtnote.ActivityIDTnoteList;
import com.app.mdlive_cp.reliance.preschistory.ActivityPrescHistory;
import com.app.mdlive_cp.reliance.pt_reports.ReportFolders;
import com.app.mdlive_cp.util.DATA;
import com.app.mdlive_cp.util.DialogPatientInfo;
import com.app.mdlive_cp.util.ExpandableHeightGridView;
import com.app.mdlive_cp.util.GloabalMethods;
import com.app.mdlive_cp.util.ReportsDialog;
import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class ActivityTcmDetails extends BaseActivity {

    ImageView imgSelPtImage;
    TextView tvSelPtName,tvPtSymptom,tvPtCondition,tvPtDescription,tvPain,tvPainSeverity,tvPainBodyPart,tvSymptomDetails,tvSelPtPhone,tvSelPtEmail,tvSelPtAddress,tvSelPtDOB,tvSelPtCompanies,
            tvSelPtProviderName,tvSelPtProviderPhone;
    View divSepProvider;
    TextView etSOAPHistoryMedical,etSOAPHistorySocial,etSOAPHistoryFamily,etSOAPHistoryMedications,etSOAPHistoryAllergies;
    LinearLayout layReports,layVideoCall,layCareReview,laySoapNotes,layProgressNotes,layAssesments,layOtNotes,layMsg,layNEMT,
            layAddOTNotes,layCarePlan, layCounter,layIDTnotes,layCareTeam;

    EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBMI;
    TextView tvVitalsDate;
    ExpandableHeightGridView gvReportImages;

    ImageView ivExpendHistoryLay;
    TextView tvMedHistoryLabel;

    Button btnEditVV;


    @Override
    protected void onResume() {
        /*if(prefs.getString("doctor_category","").equalsIgnoreCase("ot") || prefs.getString("doctor_category","").equalsIgnoreCase("pt")){
            //btnAddOTNotes.setVisibility(View.VISIBLE);
        }else{
            //btnAddOTNotes.setVisibility(View.GONE);
        }*/

        //===================================Set Fields==============================================
        tvSelPtName.setText(DATA.selectedUserCallName);
        tvPtSymptom.setText(DATA.selectedUserCallSympTom);
        tvPtCondition.setText(DATA.selectedUserCallCondition);
        tvPtDescription.setText(DATA.selectedUserCallDescription);
        tvPain.setText(DATA.selectedRefferedLiveCare.pain_where);
        tvPainSeverity.setText(DATA.selectedRefferedLiveCare.pain_severity);
        tvSymptomDetails.setText(DATA.selectedRefferedLiveCare.symptom_details);
        DATA.loadImageFromURL(DATA.selectedUserCallImage, R.drawable.ic_default_user_squire,imgSelPtImage);
        if (new GloabalMethods(activity).checkPlayServices()) {
            initilizeMap(DATA.selectedUserLatitude, DATA.selectedUserLongitude, DATA.selectedUserCallName);
        }

        ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL+"/"+DATA.selectedUserCallId,"get",null,apiCallBack, activity);
        ApiManager.shouldShowPD = false;
        apiManager.loadURL();
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcm_details);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Patient Details");

        Button btnToolbar = (Button) findViewById(R.id.btnToolbar);
        if(DATA.isFromDocToDoc){
            btnToolbar.setVisibility(View.GONE);
        }
        btnToolbar.setText("Virtual Home Visit");
        btnToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.visit_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                openActivity.open(GetLiveCareForm.class,false);
            }
        });

        btnEditVV = findViewById(R.id.btnEditVV);
        btnEditVV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DATA.visit_start_time = new SimpleDateFormat("HH:mm:ss").format(new Date());
                Intent intent = new Intent(activity, GetLiveCareForm.class);
                intent.putExtra("isForEdit", true);
                startActivity(intent);
            }
        });

        imgSelPtImage = (ImageView) findViewById(R.id.imgSelPtImage);
        tvSelPtName = (TextView)findViewById(R.id.tvSelPtName);
        tvPtSymptom = (TextView)findViewById(R.id.tvPtSymptom);
        tvPtCondition = (TextView)findViewById(R.id.tvPtCondition);
        tvPtDescription = (TextView)findViewById(R.id.tvPtDescription);
        tvPain = (TextView)findViewById(R.id.tvPain);
        tvPainSeverity = (TextView)findViewById(R.id.tvPainSeverity);
        tvPainBodyPart = findViewById(R.id.tvPainBodyPart);
        tvSymptomDetails = (TextView)findViewById(R.id.tvSymptomDetails);
        tvSelPtPhone= (TextView)findViewById(R.id.tvSelPtPhone);
        tvSelPtEmail= (TextView)findViewById(R.id.tvSelPtEmail);
        tvSelPtAddress= (TextView)findViewById(R.id.tvSelPtAddress);
        tvSelPtDOB= (TextView)findViewById(R.id.tvSelPtDOB);
        tvSelPtCompanies = findViewById(R.id.tvSelPtCompanies);
        tvSelPtProviderName= (TextView)findViewById(R.id.tvSelPtProviderName);
        tvSelPtProviderPhone= (TextView)findViewById(R.id.tvSelPtProviderPhone);
        divSepProvider = findViewById(R.id.divSepProvider);

        etOTBP = findViewById(R.id.etOTBP);
        etOTHR = findViewById(R.id.etOTHR);
        etOTRespirations = findViewById(R.id.etOTRespirations);
        etOTO2Saturations = findViewById(R.id.etOTO2Saturations);
        etOTBloodSugar = findViewById(R.id.etOTBloodSugar);
        etOTTemperature = findViewById(R.id.etOTTemperature);
        etOTHeight = findViewById(R.id.etOTHeight);
        etOTWeight = findViewById(R.id.etOTWeight);
        etOTBMI = findViewById(R.id.etOTBMI);
        tvVitalsDate = findViewById(R.id.tvVitalsDate);
        gvReportImages = findViewById(R.id.gvReportImages);

        etSOAPHistoryMedical = (TextView) findViewById(R.id.etSOAPHistoryMedical);
        etSOAPHistorySocial = (TextView) findViewById(R.id.etSOAPHistorySocial);
        etSOAPHistoryFamily = (TextView) findViewById(R.id.etSOAPHistoryFamily);
        etSOAPHistoryMedications = (TextView) findViewById(R.id.etSOAPHistoryMedications);
        etSOAPHistoryAllergies = (TextView) findViewById(R.id.etSOAPHistoryAllergies);


        final ImageView ivExpendExamLay = (ImageView) findViewById(R.id.ivExpendExamLay);
        final ExpandableRelativeLayout layExpandExam = (ExpandableRelativeLayout) findViewById(R.id.layExpandExam);
        layExpandExam.collapse();
        ivExpendExamLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layExpandExam.isExpanded()){
                    layExpandExam.collapse();
                    ivExpendExamLay.setImageResource(R.drawable.ic_add_box_black_24dp);
                }else {
                    layExpandExam.expand();
                    ivExpendExamLay.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
                }
            }
        });

        tvMedHistoryLabel = findViewById(R.id.tvMedHistoryLabel);
        ivExpendHistoryLay = (ImageView) findViewById(R.id.ivExpendHistoryLay);
        final ExpandableRelativeLayout layExpandHistory = (ExpandableRelativeLayout) findViewById(R.id.layExpandHistory);
        layExpandHistory.collapse();
        layExpandHistory.toggle();
        ivExpendHistoryLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layExpandHistory.isExpanded()){
                    layExpandHistory.collapse();
                    ivExpendHistoryLay.setImageResource(R.drawable.ic_add_box_black_24dp);
                }else {
                    //layExpandHistory.expand();
                    layExpandHistory.expand(1000, new FastOutSlowInInterpolator());
                    ivExpendHistoryLay.setImageResource(R.drawable.ic_indeterminate_check_box_black_24dp);
                }
            }
        });


        layReports = (LinearLayout) findViewById(R.id.layReports);
        layVideoCall = (LinearLayout) findViewById(R.id.layVideoCall);
        layCareReview = (LinearLayout) findViewById(R.id.layCareReview);
        laySoapNotes = (LinearLayout) findViewById(R.id.laySoapNotes);
        layProgressNotes = (LinearLayout) findViewById(R.id.layProgressNotes);
        layAssesments = (LinearLayout) findViewById(R.id.layAssesments);
        layOtNotes = (LinearLayout) findViewById(R.id.layOtNotes);
        layMsg = (LinearLayout) findViewById(R.id.layMsg);
        layNEMT = (LinearLayout) findViewById(R.id.layNEMT);
        layCarePlan = (LinearLayout) findViewById(R.id.layCarePlan);
        layCounter = (LinearLayout) findViewById(R.id.layCounter);
        layIDTnotes = (LinearLayout) findViewById(R.id.layIDTnotes);
        layCareTeam = (LinearLayout) findViewById(R.id.layCareTeam);


        //openActivity.open(ActivityOTPT_Notes.class,false);   add OT
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.layReports:
                        showOptionsDialog();
                        break;
                    case R.id.layVideoCall:
                        if(DATA.selectedRefferedLiveCare.is_online.equals("1")){
                            DATA.isFromDocToDoc = false;
                            DATA.incomingCall = false;
                            Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                            myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(myIntent);
                            finish();
                        }else {
                            new AlertDialog.Builder(activity).setTitle("Patient Offline")
                                    .setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
                                    .setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            new GloabalMethods(activity).initMsgDialog();
                                        }
                                    }).setNegativeButton("Cancel",null).show();
                        }
                        break;
                    case R.id.layCareReview:
                        showCCRDialog();
                        break;
                    case R.id.laySoapNotes:
                        openActivity.open(ActivitySOAP.class, false);
                        break;
                    case R.id.layProgressNotes:
                        openActivity.open(ActivityProgressNotesView.class, false);
                        break;
                    case R.id.layAssesments:
                        //showAssesmentDialog();
                        showAssesmentDialogRoleBased();
                        break;
                    case R.id.layOtNotes:
                        openActivity.open(ActivityViewOTNotes.class, false);
                        break;
                    case R.id.layMsg:
                        new GloabalMethods(activity).initMsgDialog();
                        break;
                    case R.id.layNEMT:
                        ActivityFollowUps_4.selectedFollowupBean = new FollowupBean("","","","",DATA.selectedUserCallId,"","","","");

                        new AlertDialog.Builder(activity).setTitle("NEMT").setMessage("Please select video call or meassage to the NEMT Command Center")
                                .setPositiveButton("Video Call", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DATA.incomingCall = false;
                                        Intent i = new Intent(activity,MainActivity.class);
                                        i.putExtra("isFromCallToEMS", true);
                                        startActivity(i);
                                    }
                                }).setNegativeButton("Message", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(activity,SupportMessagesActivity.class);
                                intent.putExtra("isFromEmsMsg",true);
                                startActivity(intent);
                            }
                        }).show();

                        break;
                    case R.id.layCarePlan:
                        openActivity.open(ActivityCarePlan.class,false);
                        break;
                    case R.id.layCounter:
                        //openActivity.open(ActivityCounter.class,false);
                        showEncounterDialog();
                        break;
                    case R.id.layIDTnotes:
                        openActivity.open(ActivityIDTnoteList.class,false);
                        break;
                    case R.id.layCareTeam:
                        openActivity.open(ActivityCareTeam.class,false);
                        break;
                    default:
                        break;
                }
            }
        };

        layReports.setOnClickListener(onClickListener);
        layVideoCall.setOnClickListener(onClickListener);
        layCareReview.setOnClickListener(onClickListener);
        laySoapNotes.setOnClickListener(onClickListener);
        layProgressNotes.setOnClickListener(onClickListener);
        layAssesments.setOnClickListener(onClickListener);
        layOtNotes.setOnClickListener(onClickListener);
        layMsg.setOnClickListener(onClickListener);
        layNEMT.setOnClickListener(onClickListener);
        layCarePlan.setOnClickListener(onClickListener);
        layCounter.setOnClickListener(onClickListener);
        layIDTnotes.setOnClickListener(onClickListener);
        layCareTeam.setOnClickListener(onClickListener);


        new GloabalMethods(activity).getPatientMedicalHistory();
    }


    private GoogleMap googleMap;
    View mapView;
    private void initilizeMap(Double latitude, double longitude , String patientName) {
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

                googleMap.setTrafficEnabled(true);
                googleMap.setMyLocationEnabled(true);
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
                        ImageView ivMarker = (ImageView) myContentView.findViewById(R.id.ivMarker);
                        //UrlImageViewHelper.setUrlDrawable(ivMarker, DATA.selectedUserCallImage, R.drawable.icon_call_screen);
                        if(! DATA.selectedUserCallImage.isEmpty()){
                            DATA.loadImageFromURL(DATA.selectedUserCallImage,R.drawable.icon_call_screen,ivMarker);
                        }
                        return myContentView;
                    }
                    @Override
                    public View getInfoContents(Marker marker) {
                        return null;//a padding was shown on marker info window
                    }
                });

                // create marker
                //  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(patientName)
                        .snippet("Connect for Medical Transportation (NEMT)")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.patient_icon_small));

                // adding marker
                googleMap.addMarker(marker).showInfoWindow();


                googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        ActivityFollowUps_4.selectedFollowupBean = new FollowupBean("","","","",DATA.selectedUserCallId,"","","","");

                        new AlertDialog.Builder(activity).setTitle("NEMT").setMessage("Please select video call or meassage to the NEMT Command Center")
                                .setPositiveButton("Video Call", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        DATA.incomingCall = false;
                                        Intent i = new Intent(activity,MainActivity.class);
                                        i.putExtra("isFromCallToEMS", true);
                                        startActivity(i);
                                    }
                                }).setNegativeButton("Message", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(activity,SupportMessagesActivity.class);
                                intent.putExtra("isFromEmsMsg",true);
                                startActivity(intent);
                            }
                        }).show();
                    }
                });


                LatLng cpos = new LatLng(latitude, longitude);
                CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
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


    public static String ptPolicyNo = "",ptDOB = "", ptAddress = "", pt_city = "", pt_state = "",  ptZipcode = "",ptPhone = "", ptEmail = "",ptFname = "",ptLname = "",ptInsurancePayerName = "", phistory= "",
            ptRefDate = "",ptRefDr = "",ptPriHomeCareDiag = "",
            primary_patient_id = "",family_is_online = "";
    //ptRefDr not used DATA.selectedDoctorName used insted in dme skn and homecare forms
    @Override
    public void fetchDataCallback(String status, String apiName, String content) {
        if(apiName.contains(ApiManager.PATIENT_DETAIL)){
            try {
                JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

                JSONObject patient_data = jsonObject.getJSONObject("patient_data");

                DATA.selectedUserCallAge = patient_data.getString("age");

                ptPolicyNo = patient_data.getString("policy_number");
                ptDOB  = patient_data.getString("birthdate");
                ptAddress = patient_data.getString("residency");
                //String address2 = patient_data.getString("address2");
                pt_city = patient_data.optString("city");
                pt_state = patient_data.optString("state");
                ptZipcode  = patient_data.getString("zipcode");
                ptPhone = patient_data.getString("phone");
                ptEmail = patient_data.getString("email");
                ptFname = patient_data.getString("first_name");
                ptLname = patient_data.getString("last_name");

                primary_patient_id = patient_data.getString("primary_patient_id");
                family_is_online = patient_data.getString("family_is_online");

                ptInsurancePayerName = patient_data.optString("payer_name", "-");

                DATA.selectedRefferedLiveCare.StoreName = patient_data.getString("StoreName");
                DATA.selectedRefferedLiveCare.PhonePrimary = patient_data.getString("PhonePrimary");

                DATA.selectedRefferedLiveCare.is_online = patient_data.getString("is_online");

                try{
                    DATA.selectedUserLatitude = Double.parseDouble(patient_data.getString("latitude"));//zlat = patient zipcode latlong
                    DATA.selectedUserLongitude = Double.parseDouble(patient_data.getString("longitude"));//zlong

                    if (new GloabalMethods(activity).checkPlayServices()) {
                        initilizeMap(DATA.selectedUserLatitude, DATA.selectedUserLongitude, DATA.selectedUserCallName);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }




                StringBuilder sbAddress = new StringBuilder();
                sbAddress.append("Address: ");
                if(!TextUtils.isEmpty(ptAddress)){
                    sbAddress.append(ptAddress);
                    sbAddress.append(", ");
                }
                if(!TextUtils.isEmpty(pt_city)){
                    sbAddress.append(pt_city);
                    sbAddress.append(", ");
                }
                if(!TextUtils.isEmpty(pt_state)){
                    sbAddress.append(pt_state);
                    sbAddress.append(", ");
                }
                if(!TextUtils.isEmpty(ptZipcode)){
                    sbAddress.append(ptZipcode);
                }
                tvSelPtAddress.setText(sbAddress.toString());

                tvSelPtDOB.setText("DOB: "+ptDOB+"  |  Age: "+DATA.selectedUserCallAge);
                tvSelPtEmail.setText("Email: "+ptEmail);


                String styledText = "Mobile: " + "<font color='#2979ff'><u>"+ptPhone+"</u></font>";
                tvSelPtPhone.setText(Html.fromHtml(styledText));
                tvSelPtPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:"+Uri.encode(ptPhone)));
                            callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(callIntent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });

                if(!jsonObject.optString("primary_care").isEmpty()){
                    JSONObject primary_care = jsonObject.getJSONObject("primary_care");
                    tvSelPtProviderName.setText("Provider Name: "+primary_care.getString("first_name")+ " " +primary_care.getString("last_name"));
                    String provederPhone = primary_care.getString("mobile");
                    //tvSelPtProviderPhone.setText(provederPhone);
                    String styledText2 = "Provider Mobile: " + "<font color='#2979ff'><u>"+provederPhone+"</u></font>";
                    tvSelPtProviderPhone.setText(Html.fromHtml(styledText2));

                    //tvSelPtProviderPhone.setPaintFlags(tvSelPtProviderPhone.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

                    tvSelPtProviderPhone.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            try {
                                Intent callIntent = new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+Uri.encode(provederPhone)));
                                callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(callIntent);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });
                }else {
                    tvSelPtProviderName.setVisibility(View.GONE);
                    tvSelPtProviderPhone.setVisibility(View.GONE);
                    divSepProvider.setVisibility(View.GONE);
                }

                JSONArray companies = jsonObject.optJSONArray("companies");
                StringBuilder sbCompanies = new StringBuilder();
                if(companies != null){
                    for (int i = 0; i < companies.length(); i++) {
                        sbCompanies.append(companies.getJSONObject(i).getString("company_name"));
                        if(i < (companies.length() - 1)){
                            sbCompanies.append(", ");
                        }
                    }
                }
                tvSelPtCompanies.setText("Nursing Home: "+sbCompanies.toString());


                if(!jsonObject.getString("virtual_visit").isEmpty()){
                    JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");

                    DATA.selectedRefferedLiveCare.virtualVisitId = virtual_visit.optString("id");

                    if(virtual_visit.has("dateof")){
                        tvVitalsDate.setText("Date: "+virtual_visit.getString("dateof"));
                    }

                    if(virtual_visit.has("symptom_id")){
                        ActivityBookAptmtCP.symptom_id = virtual_visit.getString("symptom_id");
                    }
                    if(virtual_visit.has("condition_id")){
                        ActivityBookAptmtCP.condition_id = virtual_visit.getString("condition_id");
                    }

                    if(virtual_visit.has("symptom_name")){
                        DATA.selectedUserCallSympTom = virtual_visit.getString("symptom_name");
                        tvPtSymptom.setText(DATA.selectedUserCallSympTom);
                        DATA.selectedRefferedLiveCare.symptom_name = DATA.selectedUserCallSympTom;
                    }
                    if(virtual_visit.has("condition_name")){
                        DATA.selectedUserCallCondition = virtual_visit.getString("condition_name");
                        tvPtCondition.setText(DATA.selectedUserCallCondition);
                        DATA.selectedRefferedLiveCare.condition_name = DATA.selectedUserCallCondition;
                    }
                    if(virtual_visit.has("description")){
                        DATA.selectedUserCallDescription = virtual_visit.getString("description");
                        tvPtDescription.setText(DATA.selectedUserCallDescription);
                        DATA.selectedRefferedLiveCare.description = DATA.selectedUserCallDescription;
                    }

                    if(virtual_visit.has("additional_data")){
                        String additional_data = virtual_visit.getString("additional_data");
                        if(!additional_data.isEmpty()){
                            JSONObject additional_dataJSON = new JSONObject(additional_data);
                            if(additional_dataJSON.has("pain_where")){
                                DATA.selectedRefferedLiveCare.pain_where = additional_dataJSON.getString("pain_where");
                                tvPain.setText(DATA.selectedRefferedLiveCare.pain_where);
                            }
                            if(additional_dataJSON.has("pain_severity")){
                                DATA.selectedRefferedLiveCare.pain_severity = additional_dataJSON.getString("pain_severity");
                                tvPainSeverity.setText(DATA.selectedRefferedLiveCare.pain_severity);
                            }
                        }
                    }
                    if(virtual_visit.has("symptom_details")){
                        DATA.selectedRefferedLiveCare.symptom_details = virtual_visit.getString("symptom_details");
                        tvSymptomDetails.setText(DATA.selectedRefferedLiveCare.symptom_details);
                        ptPriHomeCareDiag = DATA.selectedRefferedLiveCare.symptom_details;
                    }

                    if(virtual_visit.has("pain_related")){
                        DATA.selectedRefferedLiveCare.pain_related = virtual_visit.getString("pain_related");
                        tvPainBodyPart.setText(DATA.selectedRefferedLiveCare.pain_related);
                    }

                    if(virtual_visit.has("ot_data")&& !virtual_visit.getString("ot_data").isEmpty()){
                        JSONObject virtual_ot_data = new JSONObject(virtual_visit.getString("ot_data"));
                        if(virtual_ot_data.has("ot_respirations")){
                            DATA.selectedRefferedLiveCare.ot_respirations = virtual_ot_data.getString("ot_respirations");
                            etOTRespirations.setText(DATA.selectedRefferedLiveCare.ot_respirations);
                        }
                        if(virtual_ot_data.has("ot_blood_sugar")){
                            DATA.selectedRefferedLiveCare.ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
                            etOTBloodSugar.setText(DATA.selectedRefferedLiveCare.ot_blood_sugar);
                        }
                        if(virtual_ot_data.has("ot_hr")){
                            DATA.selectedRefferedLiveCare.ot_hr = virtual_ot_data.getString("ot_hr");
                            etOTHR.setText(DATA.selectedRefferedLiveCare.ot_hr);
                        }
                        if(virtual_ot_data.has("ot_bp")){
                            DATA.selectedRefferedLiveCare.ot_bp = virtual_ot_data.getString("ot_bp");
                            etOTBP.setText(DATA.selectedRefferedLiveCare.ot_bp);
                        }
                        if(virtual_ot_data.has("ot_saturation")){
                            DATA.selectedRefferedLiveCare.ot_saturation = virtual_ot_data.getString("ot_saturation");
                            etOTO2Saturations.setText(DATA.selectedRefferedLiveCare.ot_saturation);
                        }

                        if(virtual_ot_data.has("ot_height")){
                            DATA.selectedRefferedLiveCare.ot_height = virtual_ot_data.getString("ot_height");
                            etOTHeight.setText(DATA.selectedRefferedLiveCare.ot_height);
                            DATA.selectedUserCallHeight = DATA.selectedRefferedLiveCare.ot_height;
                        }
                        if(virtual_ot_data.has("ot_temperature")){
                            DATA.selectedRefferedLiveCare.ot_temperature = virtual_ot_data.getString("ot_temperature");
                            etOTTemperature.setText(DATA.selectedRefferedLiveCare.ot_temperature);
                        }
                        if(virtual_ot_data.has("ot_weight")){
                            DATA.selectedRefferedLiveCare.ot_weight = virtual_ot_data.getString("ot_weight");
                            etOTWeight.setText(DATA.selectedRefferedLiveCare.ot_weight);
                            DATA.selectedUserCallWeight = DATA.selectedRefferedLiveCare.ot_weight;
                        }

                        if(virtual_ot_data.has("ot_bmi")){
                            DATA.selectedRefferedLiveCare.bmi = virtual_ot_data.getString("ot_bmi");
                            etOTBMI.setText(DATA.selectedRefferedLiveCare.bmi);
                            DATA.selectedUserCallBMI = DATA.selectedRefferedLiveCare.bmi;
                        }
                    }


                    JSONArray reports = virtual_visit.getJSONArray("reports");
                    final ArrayList<String> vvImgs = new ArrayList<>();
                    for (int i = 0; i < reports.length(); i++) {
                        vvImgs.add(reports.getJSONObject(i).getString("report_name"));
                    }
                    gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
                    gvReportImages.setExpanded(true);
                    gvReportImages.setPadding(5,5,5,5);
					/*TextView gvHeader = new TextView(activity);
					gvHeader.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
					gvHeader.setText("Virtual Visit Medical Reports");
					gvHeader.setGravity(Gravity.CENTER);

					gvReportImages.addHeaderView(footerView);*/

                    gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            if(vvImgs.get(position).endsWith(".pdf")){
                                DATA.selectedPtReportUrl = vvImgs.get(position);
                                //openActivity.open(ViewReportFull.class, false);
                                new ReportsDialog(appCompatActivity).showReportDialog();
                            }else {
                                DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
                            }
                        }
                    });

                    btnEditVV.setVisibility(View.VISIBLE);
                }else {
                    btnEditVV.setVisibility(View.GONE);
                }


                if(jsonObject.has("medical_history")){

                    JSONObject medical_history = jsonObject.getJSONObject("medical_history");

                    phistory = medical_history.getString("phistory");
                    etSOAPHistoryMedical.setText(phistory);

                    String is_smoke = "0";
                    String smoke_detail = "";
                    String is_drink = "0";
                    String drink_detail = "";
                    String is_drug = "0";
                    String drug_detail = "";

                    if(medical_history.has("is_smoke")){
                        is_smoke = medical_history.getString("is_smoke");
                    }
                    if(medical_history.has("smoke_detail")){
                        smoke_detail = medical_history.getString("smoke_detail");
                    }
                    if(medical_history.has("is_drink")){
                        is_drink = medical_history.getString("is_drink");
                    }
                    if(medical_history.has("drink_detail")){
                        drink_detail = medical_history.getString("drink_detail");
                    }
                    if(medical_history.has("is_drug")){
                        is_drug = medical_history.getString("is_drug");
                    }
                    if(medical_history.has("drug_detail")){
                        drug_detail = medical_history.getString("drug_detail");
                    }




                    /*StringBuilder socialHistory = new StringBuilder();
                    if (is_smoke.equalsIgnoreCase("1")) {
                        socialHistory.append("Smoke:Yes, ");
                        if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
                            String[] smokeArr = smoke_detail.split("/");
                            if (smokeArr.length < 2) {

                            }else {
                                socialHistory.append("How long: "+smokeArr[0]+", How much: "+smokeArr[1]);
                            }
                        }
                    }else{
                        socialHistory.append("Smoke:No, ");
                    }*/

                    //new smoke start
                    StringBuilder socialHistory = new StringBuilder();

                    if(is_smoke.equalsIgnoreCase("0")){
                        String arr[] = smoke_detail.split("\\|");
                        String smokeType = "-", smokeAge = "-", smokeHowMuchPerDay = "-", smokeReadyToQuit = "-";
                        try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
                        try {smokeAge = arr[1]; }catch (Exception e){e.printStackTrace();}
                        try {smokeHowMuchPerDay = arr[2];}catch (Exception e){e.printStackTrace();}
                        try {smokeReadyToQuit = arr[3];}catch (Exception e){e.printStackTrace();}

                        socialHistory.append("Smoking Status : Current Smoker");
                        socialHistory.append(", Type : "+smokeType);
                        socialHistory.append(", What age did you start : "+smokeAge);
                        socialHistory.append(", How much per day : "+smokeHowMuchPerDay);
                        socialHistory.append(", Are you ready to quit : "+smokeReadyToQuit);

                    }else if(is_smoke.equalsIgnoreCase("1")){
                        String arr[] = smoke_detail.split("\\|");
                        String smokeType = "-", smokeHowLongDidU = "-", smokeQuitDate = "-";
                        try {smokeType = arr[0]; }catch (Exception e){e.printStackTrace();}
                        try {smokeHowLongDidU = arr[1]; }catch (Exception e){e.printStackTrace();}
                        try {smokeQuitDate = arr[2];}catch (Exception e){e.printStackTrace();}
                        socialHistory.append("Smoking Status : Former Smoker");
                        socialHistory.append(", Type : "+smokeType);
                        socialHistory.append(", How long did you smoke : "+smokeHowLongDidU);
                        socialHistory.append(", Quit date : "+smokeQuitDate);
                    }else if(is_smoke.equalsIgnoreCase("2")){
                        socialHistory.append("Smoking Status : Non Smoker");
                    }else{
                        socialHistory.append("Smoke:No, ");
                    }
                    //new smoke end




                    if (is_drink.equalsIgnoreCase("1")) {
                        socialHistory.append("\nDrink alcohol:Yes, ");
                        if ((!drink_detail.isEmpty()) || (!drink_detail.equalsIgnoreCase("null"))) {
                            String[] drinkArr = drink_detail.split("/");
                            if (drinkArr.length < 2) {

                            }else {
                                socialHistory.append("How long: "+drinkArr[0]+", How much: "+drinkArr[1]);
                            }
                        }
                    }else{
                        socialHistory.append("Drink alcohol:No, ");
                    }

                    if (is_drug.equalsIgnoreCase("1")) {
                        socialHistory.append("\nUse Drug:Yes, ");
                        if ((!drug_detail.isEmpty()) || (!drug_detail.equalsIgnoreCase("null"))) {
                            socialHistory.append("Detail: "+drug_detail);
                        }
                    }else{
                        socialHistory.append("Use Drug:No");
                    }

                    if(medical_history.has("social_other")){
                        socialHistory.append("\nOther: "+medical_history.getString("social_other"));
                    }
                    etSOAPHistorySocial.setText(socialHistory.toString());

                    String relation_had = "";
                    if(medical_history.has("relation_had")){
                        relation_had = medical_history.getString("relation_had");
                    }
                    String relation_had_name = "";
                    if(medical_history.has("relation_had_name")){
                        relation_had_name = medical_history.getString("relation_had_name");
                    }

                    String[] rhArr = relation_had.split("/");
                    String[] rhNameArr = relation_had_name.split("/");
                    StringBuilder sbFamilyHistory = new StringBuilder();
                    for (int i = 0; i < rhNameArr.length; i++) {
                        sbFamilyHistory.append(rhArr[i]+" : "+rhNameArr[i]);
                        if(i < (rhNameArr.length-1)){
                            sbFamilyHistory.append("\n");
                        }
                    }
                    etSOAPHistoryFamily.setText(sbFamilyHistory.toString());


                    StringBuilder mediDetail = new StringBuilder();
                    if(medical_history.has("medication_detail")){
                        mediDetail.append("Medication: "+medical_history.getString("medication_detail"));
                        DATA.selectedUserCallHistoryMed = medical_history.getString("medication_detail");
                    }
                    if(medical_history.has("other")){
                        mediDetail.append("\n"+"Other: "+medical_history.getString("other"));
                    }
                    etSOAPHistoryMedications.setText(mediDetail.toString());

                    String allergyDetail = "";
                    if(medical_history.has("alergies_detail")){
                        allergyDetail = medical_history.getString("alergies_detail");
                    }
                    if(allergyDetail.isEmpty()){
                        allergyDetail = "NKDA";
                    }
                    etSOAPHistoryAllergies.setText(allergyDetail);
                }else {
                    tvMedHistoryLabel.setText("Medical history not found");
                    ivExpendHistoryLay.setEnabled(false);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                //customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
            }
        }
    }


    public void showOptionsDialog(){
        final Dialog dialogOptions = new Dialog(activity);
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOptions.setContentView(R.layout.dialog_patient_options);

        dialogOptions.setCanceledOnTouchOutside(false);

        Button btnSelPtViewPriDiag = (Button) dialogOptions.findViewById(R.id.btnSelPtViewPriDiag);
        Button btnSelPtViewReports = (Button) dialogOptions.findViewById(R.id.btnSelPtViewReports);
        Button btnSelPtProblemList = (Button) dialogOptions.findViewById(R.id.btnSelPtProblemList);
        Button btnScreeningTools  = (Button) dialogOptions.findViewById(R.id.btnScreeningTools);
        Button btnSelPtViewMedications = (Button) dialogOptions.findViewById(R.id.btnSelPtViewMedications);
        Button btnSelPtViewMedicalHistory = (Button) dialogOptions.findViewById(R.id.btnSelPtViewMedicalHistory);
        Button btnAssesReports = dialogOptions.findViewById(R.id.btnAssesReports);
        Button btnSelPtManageReports = dialogOptions.findViewById(R.id.btnSelPtManageReports);


        btnSelPtManageReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivity.open(ReportFolders.class, false);
            }
        });

        btnSelPtViewPriDiag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogPatientInfo.patientIdGCM = DATA.selectedUserCallId;
                //dialogOptions.dismiss();
                new DialogPatientInfo(appCompatActivity).showDialog();
            }
        });

        btnSelPtViewReports.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityPinnedReports.class, false);//  ViewReport.class

            }
        });
        btnSelPtViewMedications.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityPrescHistory.class, false);//PresscriptionActivity.class -- this is patient presc hist sent by olc docs -- later changed to surescript presc
            }
        });
        btnSelPtProblemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityProbelemList.class, false);
            }
        });
        btnScreeningTools.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityScreeningToolReport.class,false);
            }
        });

        btnSelPtViewMedicalHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(PatientMedicalHistory.class, false);
                openActivity.open(PatientMedicalHistoryNew.class, false);//cp will edit medical history

            }
        });

        btnAssesReports.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityAssessmentsSummary.class, false);

            }
        });

        dialogOptions.findViewById(R.id.btnNotNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
            }
        });
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptions.show();
    }


    public void showAssesmentDialog(){
        final Dialog dialogOptions = new Dialog(activity);
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOptions.setContentView(R.layout.dialog_assesments);

        dialogOptions.setCanceledOnTouchOutside(false);

        Button btnEnvironmentalAssesment = (Button) dialogOptions.findViewById(R.id.btnEnvironmentalAssesment);
        Button btnFallRiskAssesment = (Button) dialogOptions.findViewById(R.id.btnFallRiskAssesment);
        Button btnBradenScale  = (Button) dialogOptions.findViewById(R.id.btnBradenScale);
        Button btnDepression = (Button) dialogOptions.findViewById(R.id.btnDepression);
        Button btnBeckDepression = dialogOptions.findViewById(R.id.btnBeckDepression);

        Button btnPHQ = dialogOptions.findViewById(R.id.btnPHQ);
        Button btnGAD = dialogOptions.findViewById(R.id.btnGAD);
        Button btnDAST = dialogOptions.findViewById(R.id.btnDAST);
        Button btnSDHS = dialogOptions.findViewById(R.id.btnSDHS);
        Button btnDietAsses = dialogOptions.findViewById(R.id.btnDietAsses);

        btnPHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //ActivityPHQ_Form.formFlag = 1;
                //openActivity.open(ActivityPhqList.class, false);
                showAskAssesDialog(7);
            }
        });
        btnGAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //ActivityPHQ_Form.formFlag = 2;
                //openActivity.open(ActivityPhqList.class, false);
                showAskAssesDialog(8);
            }
        });
        btnDAST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityDastList.class, false);
                showAskAssesDialog(9);
            }
        });
        btnSDHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivitySDHSList.class, false);
            }
        });
        btnDietAsses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityDietAssesForm.class, false);
            }
        });


        btnEnvironmentalAssesment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityAllEnvir.class, false);

            }
        });

        btnFallRiskAssesment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityAllFallRisk.class, false);//FallRiskForm

            }
        });
        btnBradenScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityAllBradenScale.class, false);//ActivityBradenScale
            }
        });
        btnDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityAllDepression.class,false);//ActivityDepressionForm
            }
        });

        btnBeckDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogOptions.dismiss();
                openActivity.open(ActivityDepInvList.class,false);
            }
        });



        dialogOptions.findViewById(R.id.btnNotNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
            }
        });
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptions.show();
    }


    public void showEncounterDialog(){
        final Dialog dialogOptions = new Dialog(activity);
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOptions.setContentView(R.layout.dialog_encounter);

        dialogOptions.setCanceledOnTouchOutside(false);


        Button btnFalls = (Button) dialogOptions.findViewById(R.id.btnFalls);
        Button btnHospAdm = (Button) dialogOptions.findViewById(R.id.btnHospAdm);
        Button btnNHAdm  = (Button) dialogOptions.findViewById(R.id.btnNHAdm);
        Button btnERoom = (Button) dialogOptions.findViewById(R.id.btnERoom);


        btnFalls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                ActivityCounter.counterFlag = 1;
                openActivity.open(ActivityCounter.class,false);
            }
        });
        btnHospAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                ActivityCounter.counterFlag = 2;
                openActivity.open(ActivityCounter.class,false);
            }
        });
        btnNHAdm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                ActivityCounter.counterFlag = 3;
                openActivity.open(ActivityCounter.class,false);
            }
        });
        btnERoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                ActivityCounter.counterFlag = 4;
                openActivity.open(ActivityCounter.class,false);
            }
        });




        dialogOptions.findViewById(R.id.btnNotNow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogOptions.dismiss();
            }
        });
        dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogOptions.show();
    }



    //note : is_lock function is not implemented on BradenScale, Geriatic Depression and beck's depression forms.
    int selectedChild = 0;
    public void showAssesmentDialogRoleBased(){
        final Dialog dialogOptions = new Dialog(activity, R.style.TransparentThemeH4B);
        dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogOptions.setContentView(R.layout.dialog_assesments2);

        dialogOptions.setCanceledOnTouchOutside(false);

        Button btnEnvironmentalAssesment = (Button) dialogOptions.findViewById(R.id.btnEnvironmentalAssesment);
        Button btnFallRiskAssesment = (Button) dialogOptions.findViewById(R.id.btnFallRiskAssesment);
        Button btnBradenScale  = (Button) dialogOptions.findViewById(R.id.btnBradenScale);
        Button btnDepression = (Button) dialogOptions.findViewById(R.id.btnDepression);
        Button btnBeckDepression = dialogOptions.findViewById(R.id.btnBeckDepression);

        Button btnPHQ = dialogOptions.findViewById(R.id.btnPHQ);
        Button btnGAD = dialogOptions.findViewById(R.id.btnGAD);
        Button btnDAST = dialogOptions.findViewById(R.id.btnDAST);
        Button btnSDHS = dialogOptions.findViewById(R.id.btnSDHS);
        Button btnDietAsses = dialogOptions.findViewById(R.id.btnDietAsses);
        Button btnADLform = dialogOptions.findViewById(R.id.btnADLform);
        Button btnIADLform = dialogOptions.findViewById(R.id.btnIADLform);

        btnPHQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //ActivityPHQ_Form.formFlag = 1;
                //openActivity.open(ActivityPhqList.class, false);
                showAskAssesDialog(7);
            }
        });
        btnGAD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //ActivityPHQ_Form.formFlag = 2;
                //openActivity.open(ActivityPhqList.class, false);
                showAskAssesDialog(8);
            }
        });
        btnDAST.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityDastList.class, false);
                showAskAssesDialog(9);
            }
        });
        btnSDHS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivitySDHSList.class, false);
                showAskAssesDialog(10);
            }
        });
        btnDietAsses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityDietAssesList.class, false);
                showAskAssesDialog(20);
            }
        });
        btnADLform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityAdlForm.formFlagA = 1;
                //openActivity.open(ActivityAdlList.class,false);
                showAskAssesDialog(18);
            }
        });
        btnIADLform.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ActivityAdlForm.formFlagA = 2;
                //openActivity.open(ActivityAdlList.class,false);
                showAskAssesDialog(19);
            }
        });


        btnEnvironmentalAssesment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityAllEnvir.class, false);
                showAskAssesDialog(16);
            }
        });

        btnFallRiskAssesment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityAllFallRisk.class, false);//FallRiskForm
                showAskAssesDialog(17);

            }
        });
        btnBradenScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityAllBradenScale.class, false);//ActivityBradenScale
                showAskAssesDialog(12);
            }
        });
        btnDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityAllDepression.class,false);//ActivityDepressionForm

                showAskAssesDialog(11);

            }
        });

        btnBeckDepression.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //dialogOptions.dismiss();
                //openActivity.open(ActivityDepInvList.class,false);
                showAskAssesDialog(1);
            }
        });






        TextView tvTabSW = dialogOptions.findViewById(R.id.tvTabSW);
        TextView tvTabRN = dialogOptions.findViewById(R.id.tvTabRN);
        TextView tvTabOT = dialogOptions.findViewById(R.id.tvTabOT);
        TextView tvTabDietitian = dialogOptions.findViewById(R.id.tvTabDietitian);

        Button btnSMG = dialogOptions.findViewById(R.id.btnSMG);
        Button btnMedications = dialogOptions.findViewById(R.id.btnMedications);
        Button btnMedHistory = dialogOptions.findViewById(R.id.btnMedHistory);
        Button btnNotNow = dialogOptions.findViewById(R.id.btnNotNow);
        Button btnNotNow2 = dialogOptions.findViewById(R.id.btnNotNow2);
        Button btnOTprofile = dialogOptions.findViewById(R.id.btnOTprofile);
        Button btnOTevaluation = dialogOptions.findViewById(R.id.btnOTevaluation);
        Button btnNotNow3 = dialogOptions.findViewById(R.id.btnNotNow3);
        Button btnNotNow4 = dialogOptions.findViewById(R.id.btnNotNow4);

        Button btnOCDAsses = dialogOptions.findViewById(R.id.btnOCDAsses);
        Button btnFCSASAsses = dialogOptions.findViewById(R.id.btnFCSASAsses);
        Button btnPanicAttachAsses = dialogOptions.findViewById(R.id.btnPanicAttachAsses);
        Button btnSCOFFAsses = dialogOptions.findViewById(R.id.btnSCOFFAsses);
        Button btnStressQeueAsses = dialogOptions.findViewById(R.id.btnStressQeueAsses);

        ViewFlipper vfAsses = dialogOptions.findViewById(R.id.vfAsses);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){

                    case R.id.tvTabSW:
                        tvTabSW.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabRN.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        selectedChild = 0;
                        if (selectedChild > vfAsses.getDisplayedChild()) {

                            vfAsses.setInAnimation(activity, R.anim.in_right);
                            vfAsses.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfAsses.setInAnimation(activity, R.anim.in_left);
                            vfAsses.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfAsses.getDisplayedChild() != selectedChild) {
                            vfAsses.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.tvTabRN:
                        tvTabRN.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabSW.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        selectedChild = 1;
                        if (selectedChild > vfAsses.getDisplayedChild()) {

                            vfAsses.setInAnimation(activity, R.anim.in_right);
                            vfAsses.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfAsses.setInAnimation(activity, R.anim.in_left);
                            vfAsses.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfAsses.getDisplayedChild() != selectedChild) {
                            vfAsses.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.tvTabOT:
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabRN.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSW.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        selectedChild = 2;
                        if (selectedChild > vfAsses.getDisplayedChild()) {

                            vfAsses.setInAnimation(activity, R.anim.in_right);
                            vfAsses.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfAsses.setInAnimation(activity, R.anim.in_left);
                            vfAsses.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfAsses.getDisplayedChild() != selectedChild) {
                            vfAsses.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.tvTabDietitian:
                        tvTabDietitian.setBackgroundColor(getResources().getColor(R.color.theme_red));
                        tvTabRN.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabOT.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));
                        tvTabSW.setBackgroundColor(getResources().getColor(R.color.theme_red_opaque_60));

                        selectedChild = 3;
                        if (selectedChild > vfAsses.getDisplayedChild()) {

                            vfAsses.setInAnimation(activity, R.anim.in_right);
                            vfAsses.setOutAnimation(activity, R.anim.out_left);
                        } else {
                            vfAsses.setInAnimation(activity, R.anim.in_left);
                            vfAsses.setOutAnimation(activity, R.anim.out_right);
                        }
                        if (vfAsses.getDisplayedChild() != selectedChild) {
                            vfAsses.setDisplayedChild(selectedChild);
                        }
                        break;
                    case R.id.btnNotNow:
                        dialogOptions.dismiss();
                        break;
                    case R.id.btnNotNow2:
                        dialogOptions.dismiss();
                        break;
                    case R.id.btnNotNow3:
                        dialogOptions.dismiss();
                        break;
                    case R.id.btnNotNow4:
                        dialogOptions.dismiss();
                        break;
                    case R.id.btnMedications:
                        //openActivity.open(PresscriptionActivity.class, false);
                        openActivity.open(ActivityPrescHistory.class, false);//PresscriptionActivity.class -- this is patient presc hist sent by olc docs -- later changed to surescript presc
                        break;
                    case R.id.btnMedHistory:
                        //openActivity.open(PatientMedicalHistory.class, false);
                        openActivity.open(PatientMedicalHistoryNew.class, false);//cp will edit medical history
                        break;
                    case R.id.btnOTprofile:
                        //openActivity.open(ActivityOtProfileList.class, false);
                        showAskAssesDialog(14);
                        break;
                    case R.id.btnOTevaluation:
                        //openActivity.open(ActivityOtEvaList.class, false);
                        showAskAssesDialog(15);
                        break;
                    case R.id.btnSMG:
                        //openActivity.open(ActivitySelfMgtList.class, false);
                        showAskAssesDialog(13);
                        break;
                    case R.id.btnOCDAsses:
                        ActivityOCD_Form.formFlag = 1;
                        openActivity.open(ActivityOCD_List.class, false);
                        break;
                    case R.id.btnFCSASAsses:
                        ActivityOCD_Form.formFlag = 2;
                        openActivity.open(ActivityOCD_List.class, false);
                        break;
                    case R.id.btnPanicAttachAsses:
                        ActivityOCD_Form.formFlag = 3;
                        openActivity.open(ActivityOCD_List.class, false);
                        break;
                    case R.id.btnSCOFFAsses:
                        ActivityOCD_Form.formFlag = 4;
                        openActivity.open(ActivityOCD_List.class, false);
                        break;
                    case R.id.btnStressQeueAsses:
                        ActivityOCD_Form.formFlag = 5;
                        openActivity.open(ActivityOCD_List.class, false);
                        break;
                    default:
                        break;

                }
            }
        };
        tvTabSW.setOnClickListener(onClickListener);
        tvTabRN.setOnClickListener(onClickListener);
        tvTabOT.setOnClickListener(onClickListener);
        tvTabDietitian.setOnClickListener(onClickListener);
        btnNotNow.setOnClickListener(onClickListener);
        btnNotNow2.setOnClickListener(onClickListener);
        btnNotNow3.setOnClickListener(onClickListener);
        btnNotNow4.setOnClickListener(onClickListener);
        btnMedications.setOnClickListener(onClickListener);
        btnMedHistory.setOnClickListener(onClickListener);
        btnSMG.setOnClickListener(onClickListener);
        btnOTprofile.setOnClickListener(onClickListener);
        btnOTevaluation.setOnClickListener(onClickListener);

        btnOCDAsses.setOnClickListener(onClickListener);
        btnFCSASAsses.setOnClickListener(onClickListener);
        btnPanicAttachAsses.setOnClickListener(onClickListener);
        btnSCOFFAsses.setOnClickListener(onClickListener);
        btnStressQeueAsses.setOnClickListener(onClickListener);



        //dialogOptions.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogOptions.show();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogOptions.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        dialogOptions.setCanceledOnTouchOutside(false);
        dialogOptions.show();
        dialogOptions.getWindow().setAttributes(lp);

        GloabalMethods.dialogAsses = dialogOptions;
    }


    public void showAskAssesDialog(int formFlag){//Dialog wellnessOpDialog,
        //formFlag ---
        // 1 = DepressionScale,-- Beck's depression, DepInvList activity
        // 2 = OCD,
        // 3 = fcsas ,
        // 4 = panic_attack,
        // 5 = scoff,
        // 6 = stress_questionaire,
        //7 = PHQ form,
        // 8 = GAD form,
        // 9 = DAST form

        // 10 = SDHS form
        // 11 = Geriatric deperession scale form
        // 12 = Braden Scale form
        // 13 = Self Mgt Goals form
        // 14 = OT Profile form
        // 15 = OT Evaluation form
        // 16 = Environmental Asses form
        // 17 = Fall Risk Asses Tool form
        // 18 = ADL Asses form
        // 19 = IADL Asses form
        // 20 = Dietitary Asses form




        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ask_asses);
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
                }else if(formFlag == 10){
                    openActivity.open(ActivitySDHS_Form.class, false);
                }else if(formFlag == 11){
                    openActivity.open(ActivityDepressionForm.class, false);
                }else if(formFlag == 12){
                    openActivity.open(ActivityBradenScale.class, false);
                }else if(formFlag == 13){
                    openActivity.open(ActivitySelfMgtForm.class, false);
                }else if(formFlag == 14){
                    openActivity.open(ActivityOtProfileForm.class, false);
                }else if(formFlag == 15){
                    openActivity.open(ActivityOtEvaForm.class, false);
                }else if(formFlag == 16){
                    openActivity.open(ActivityEnvirForm.class, false);
                }else if(formFlag == 17){
                    openActivity.open(FallRiskForm.class, false);
                }else if(formFlag == 18){
                    ActivityAdlForm.formFlagA = 1;
                    openActivity.open(ActivityAdlForm.class, false);
                }else if(formFlag == 19){
                    ActivityAdlForm.formFlagA = 2;
                    openActivity.open(ActivityAdlForm.class, false);
                }else if(formFlag == 20){
                    openActivity.open(ActivityDietAssesForm.class, false);
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
                }else if(formFlag == 10){
                    openActivity.open(ActivitySDHSList.class, false);
                }else if(formFlag == 11){
                    openActivity.open(ActivityAllDepression.class,false);//ActivityDepressionForm
                }else if(formFlag == 12){
                    openActivity.open(ActivityAllBradenScale.class, false);
                }else if(formFlag == 13){
                    openActivity.open(ActivitySelfMgtList.class, false);
                }else if(formFlag == 14){
                    openActivity.open(ActivityOtProfileList.class, false);
                }else if(formFlag == 15){
                    openActivity.open(ActivityOtEvaList.class, false);
                }else if(formFlag == 16){
                    openActivity.open(ActivityAllEnvir.class, false);
                }else if(formFlag == 17){
                    openActivity.open(ActivityAllFallRisk.class, false);
                }else if(formFlag == 18){
                    ActivityAdlForm.formFlagA = 1;
                    openActivity.open(ActivityAdlList.class,false);
                }else if(formFlag == 19){
                    ActivityAdlForm.formFlagA = 2;
                    openActivity.open(ActivityAdlList.class,false);
                }else if(formFlag == 20){
                    openActivity.open(ActivityDietAssesList.class, false);
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



    public void showCCRDialog(){
        final Dialog dialogSupport = new Dialog(activity);
        dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSupport.setContentView(R.layout.dialog_ccr);
        //dialogSupport.setCancelable(false);
        dialogSupport.setCanceledOnTouchOutside(false);

        TextView tvDocToDoc = (TextView) dialogSupport.findViewById(R.id.tvDocToDoc);
        TextView tvDocToCp = (TextView) dialogSupport.findViewById(R.id.tvDocToCp);
        TextView tvFamily = (TextView) dialogSupport.findViewById(R.id.tvFamily);
        TextView tvReqCare = (TextView) dialogSupport.findViewById(R.id.tvReqCare);
        TextView tvCancel = (TextView) dialogSupport.findViewById(R.id.tvCancel);

        if(ActivityTcmDetails.primary_patient_id.isEmpty()){
            tvFamily.setEnabled(false);
        }else{
            tvFamily.setEnabled(true);
        }

        tvDocToDoc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();
                DocToDocAdapter.reqCareVisibility = View.GONE;
                openActivity.open(ActivityDocToDocNew.class, true);
            }
        });
        tvDocToCp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                openActivity.open(ActivityCareProviders.class, false);
            }
        });
        tvFamily.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

                if(family_is_online.equalsIgnoreCase("1")){
                    DATA.incomingCall = false;
                    Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    myIntent.putExtra("isFromCallToFamily",true);

                    startActivity(myIntent);
                    finish();
                }else{
                    new AlertDialog.Builder(activity).setTitle("Offline")
                            .setMessage("Patient's primary family member is offline and can't be connected right now. Leave a message instead ?")
                            .setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new GloabalMethods(activity).initMsgDialog();
                                }
                            }).setNegativeButton("Cancel",null).show();
                }
            }
        });
        tvReqCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSupport.dismiss();
                DocToDocAdapter.reqCareVisibility = View.VISIBLE;
                openActivity.open(ActivityDocToDocNew.class, true);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogSupport.dismiss();

            }
        });
        dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //dialogSupport.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSupport.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogSupport.show();
        dialogSupport.getWindow().setAttributes(lp);
    }
}
