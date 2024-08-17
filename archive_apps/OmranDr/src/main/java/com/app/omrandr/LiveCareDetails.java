package com.app.omrandr;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.omrandr.adapter.VVReportImagesAdapter2;
import com.app.omrandr.api.ApiManager;
import com.app.omrandr.util.DATA;
import com.app.omrandr.util.DialogPatientInfo;
import com.app.omrandr.util.ExpandableHeightGridView;
import com.app.omrandr.util.GloabalMethods;
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

import java.util.ArrayList;

import sg.com.temasys.skylink.sdk.sampleapp.MainActivity;

public class LiveCareDetails extends BaseActivity {

	ImageView imgSelPtImage;
	TextView tvSelPtName,tvPtSymptom,tvPtCondition,tvPtDescription,tvPain,tvPainSeverity,tvPainBodyPart,tvSymptomDetails,
	tvSelPtPhone,tvSelPtEmail,tvSelPtAddress,tvSelPtDOB;
	Button btnSelPtViewReports,btnSelPtStartCheckup,btnReferToSpecialist,btnSendMsg,btnReferToPCP,btnAddSOAP,btnConnectToEMS;
	TextView etSOAPHistoryMedical,etSOAPHistorySocial,etSOAPHistoryFamily,etSOAPHistoryMedications,etSOAPHistoryAllergies;
	ExpandableHeightGridView gvReportImages;
	EditText etOTBP,etOTHR,etOTRespirations,etOTO2Saturations,etOTBloodSugar,etOTTemperature,etOTHeight,etOTWeight,etOTBMI;
	TextView tvVitalsDate;

	LinearLayout layReports,layVideoCall,layMsg,layNEMT, layRemoveQueue,layInstantConnect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.livecare_details);

		imgSelPtImage = (ImageView)findViewById(R.id.imgSelPtImage);

		tvSelPtName = (TextView)findViewById(R.id.tvSelPtName);
		tvPtSymptom = (TextView)findViewById(R.id.tvPtSymptom);
		tvPtCondition = (TextView)findViewById(R.id.tvPtCondition);
		tvPtDescription = (TextView)findViewById(R.id.tvPtDescription);
		tvPain = (TextView)findViewById(R.id.tvPain);
		tvPainSeverity = (TextView)findViewById(R.id.tvPainSeverity);
		tvPainBodyPart = findViewById(R.id.tvPainBodyPart);
		tvSymptomDetails = (TextView)findViewById(R.id.tvSymptomDetails);

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

		tvSelPtPhone= (TextView)findViewById(R.id.tvSelPtPhone);
		tvSelPtEmail= (TextView)findViewById(R.id.tvSelPtEmail);
		tvSelPtAddress= (TextView)findViewById(R.id.tvSelPtAddress);
		tvSelPtDOB= (TextView)findViewById(R.id.tvSelPtDOB);

		btnSelPtViewReports = (Button) findViewById(R.id.btnSelPtViewReports);
		btnAddSOAP = (Button) findViewById(R.id.btnAddSOAP);
		btnConnectToEMS = (Button) findViewById(R.id.btnConnectToEMS);
		btnSelPtStartCheckup = (Button) findViewById(R.id.btnSelPtStartCheckup);
		btnReferToSpecialist = (Button) findViewById(R.id.btnReferToSpecialist);
		btnReferToPCP = (Button) findViewById(R.id.btnReferToPCP);
		btnSendMsg = (Button) findViewById(R.id.btnSendMsg);

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

		final ImageView ivExpendHistoryLay = (ImageView) findViewById(R.id.ivExpendHistoryLay);
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

		DATA.loadImageFromURL(DATA.selectedUserCallImage, R.drawable.icon_call_screen,imgSelPtImage);
		tvSelPtName.setText(DATA.selectedUserCallName);// DATA.selectedUserCallName
		tvPtSymptom.setText(DATA.selectedUserCallSympTom);
		tvPtCondition.setText(DATA.selectedUserCallCondition);
		tvPtDescription.setText(DATA.selectedUserCallDescription);
		tvPain.setText(DATA.selectedLiveCare.pain_where);
		tvPainSeverity.setText(DATA.selectedLiveCare.pain_severity);
		tvSymptomDetails.setText(DATA.selectedLiveCare.symptom_details);

		if (new GloabalMethods(activity).checkPlayServices()) {
			initilizeMap(DATA.selectedUserLatitude, DATA.selectedUserLongitude, DATA.selectedUserCallName);
		}

		getPetientDetails();

		btnConnectToEMS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//ActivityFollowUps_4.selectedFollowupBean = new FollowupBean("","","","",DATA.selectedUserCallId,"","","","");

				new AlertDialog.Builder(activity).setTitle("EMS").setMessage("Please select video call or meassage to the EMS Command Center")
						.setPositiveButton("Video Call", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DATA.incomingCall = false;
								DATA.isFromDocToDoc = true;
								Intent i = new Intent(activity,MainActivity.class);
								i.putExtra("isFromCallToEMS", true);
								startActivity(i);
								finish();
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

		btnAddSOAP.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DATA.isFromDocToDoc = true;
				//openActivity.open(ActivityTelemedicineServices.class, true);//finish activity to reset DATA.isFromDocToDoc
				new GloabalMethods(activity).showAddSOAPDialog();

			}
		});

		btnSelPtViewReports.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//openActivity.open(ActivityPinnedReports.class, false);//    //ViewReport.class
				showOptionsDialog();

			}
		});


		btnSelPtStartCheckup.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(DATA.selectedLiveCare.is_online.equals("1")){
					DATA.isVideoCallFromLiveCare = true;
					DATA.incomingCall = false;
					Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
					myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
					startActivity(myIntent);
					finish();
				}else {
					new AlertDialog.Builder(activity).setTitle("Patient offline")
							.setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
							.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									new GloabalMethods(activity).initMsgDialog();
								}
							}).setNegativeButton("Cancel",null).show();
				}

			}
		});


		btnSendMsg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				new GloabalMethods(activity).initMsgDialog();

			}
		});



		btnReferToSpecialist.setVisibility(View.GONE);
		btnReferToPCP.setVisibility(View.GONE);

		btnReferToSpecialist.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DATA.referToSpecialist = true;
				openActivity.open(DocToDoc.class, false);

			}
		});
		btnReferToPCP.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				/*CustomToast customToast = new CustomToast(activity);
				customToast.showToast("This section is under development", 0, 0);*/

				new GloabalMethods(activity).referToPCP(DATA.selectedUserAppntID);
			}
		});



		layReports = (LinearLayout) findViewById(R.id.layReports);
		layVideoCall = (LinearLayout) findViewById(R.id.layVideoCall);
		layMsg = (LinearLayout) findViewById(R.id.layMsg);
		layNEMT = (LinearLayout) findViewById(R.id.layNEMT);
		layRemoveQueue = findViewById(R.id.layRemoveQueue);
		layInstantConnect = findViewById(R.id.layInstantConnect);

		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				switch (v.getId()){
					case R.id.layReports:
						showOptionsDialog();
						break;
					case R.id.layVideoCall:
						if(DATA.selectedLiveCare.is_online.equals("1")){
							DATA.isVideoCallFromLiveCare = true;
							DATA.incomingCall = false;
							Intent myIntent = new Intent(getBaseContext(), MainActivity.class);//SampleActivity.class
							myIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
							myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
							startActivity(myIntent);
							finish();
						}else {
							new AlertDialog.Builder(activity).setTitle("Patient offline")
									.setMessage("Patient is offline and can't be connected right now. Leave a message instead ?")
									.setPositiveButton("Leave Message", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											new GloabalMethods(activity).initMsgDialog();
										}
									}).setNegativeButton("Cancel",null).show();
						}
						break;
					case R.id.layMsg:
						new GloabalMethods(activity).initMsgDialog();
						break;
					case R.id.layNEMT:

						if(true){
							customToast.showToast("Not available for right now",0,0);
							return;
						}
						//ActivityFollowUps_4.selectedFollowupBean = new FollowupBean("","","","",DATA.selectedUserCallId,"","","","");

						new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme).setTitle("NEMT").setMessage("Please select video call or message to the NEMT Command Center")
								.setPositiveButton("Call For NEMT", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										DATA.incomingCall = false;
										DATA.isFromDocToDoc = true;
										Intent i = new Intent(activity,MainActivity.class);
										i.putExtra("isFromCallToEMS", true);
										startActivity(i);
										finish();
									}
								}).setNegativeButton("Chat with NEMT", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								Intent intent = new Intent(activity,SupportMessagesActivity.class);
								intent.putExtra("isFromEmsMsg",true);
								startActivity(intent);
							}
						}).setNeutralButton("Not Now", null).show();
						break;
					case R.id.layRemoveQueue:
						new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
								.setTitle(getResources().getString(R.string.app_name))
								.setMessage("Are you sure ? Do you want to remove the patient from you live care queue ?")
								.setPositiveButton("Yes Remove", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent intent = new Intent(activity, AfterCallDialogEmcura.class);
										intent.putExtra("isForRemoveFromQueueFromUcDet", true);
										startActivity(intent);
										finish();
									}
								}).setNegativeButton("Not Now", null)
								.create().show();

						break;
					case R.id.layInstantConnect:
						Intent intent = new Intent(activity, ActivityInstatntConnect.class);
						intent.putExtra("isFromPatientProfile", true);
						startActivity(intent);
						break;
					default:
						break;
				}
			}
		};

		layReports.setOnClickListener(onClickListener);
		layVideoCall.setOnClickListener(onClickListener);
		layMsg.setOnClickListener(onClickListener);
		layNEMT.setOnClickListener(onClickListener);
		layRemoveQueue.setOnClickListener(onClickListener);
		layInstantConnect.setOnClickListener(onClickListener);
	}


	private GoogleMap googleMap;
	private View mapView;
	private void initilizeMap(Double latitude, double longitude , String patientName) {

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


					//googleMap = ((MapFragment) getFragmentManager().findFragmentById(map)).getMap();

					googleMap.setTrafficEnabled(true);
					if(ContextCompat.checkSelfPermission(activity, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
						googleMap.setMyLocationEnabled(true);
					}
					googleMap.setBuildingsEnabled(true);

					googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
						@Override
						public View getInfoWindow(Marker marker) {
							//return null;
							View myContentView = getLayoutInflater().inflate(
									R.layout.custom_marker, null);
							TextView tvTitle = ((TextView) myContentView.findViewById(R.id.title));
							tvTitle.setText(marker.getTitle());
							TextView tvSnippet = ((TextView) myContentView.findViewById(R.id.snippet));
							tvSnippet.setText(marker.getSnippet());
							ImageView ivMarker = (ImageView) myContentView.findViewById(R.id.ivMarker);
							//UrlImageViewHelper.setUrlDrawable(ivMarker, DATA.selectedUserCallImage, R.drawable.icon_call_screen);

							DATA.loadImageFromURL(DATA.selectedUserCallImage,R.drawable.icon_call_screen,ivMarker);

							return myContentView;
						}
						@Override
						public View getInfoContents(Marker marker) {
							return null;//a padding was shown on marker info window
						}
					});
					// create marker   Online Urgent Care
					//  MarkerOptions marker = new MarkerOptions().position(new LatLng(lati, longi)).title(address);
					MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(patientName)
							.snippet("Connect for Medical Transportation (NEMT)")
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.patient_icon_small));

					// adding marker
					googleMap.addMarker(marker).showInfoWindow();

					//Bus stop info window onClick event
					googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
						@Override
						public void onInfoWindowClick(Marker marker) {

							new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
									.setTitle(getResources().getString(R.string.app_name))
									.setMessage("This feature is not available right now.")
									.setPositiveButton("OK",null)
									.create().show();


							/*new AlertDialog.Builder(activity).setTitle("EMS").setMessage("Please select video call or meassage to the EMS Command Center")
									.setPositiveButton("Video Call", new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											DATA.incomingCall = false;
											DATA.isFromDocToDoc = true;
											Intent i = new Intent(activity,MainActivity.class);
											i.putExtra("isFromCallToEMS", true);
											startActivity(i);
											finish();
										}
									}).setNegativeButton("Message", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									Intent intent = new Intent(activity,SupportMessagesActivity.class);
									intent.putExtra("isFromEmsMsg",true);
									startActivity(intent);
								}
							}).show();*/
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
	}


	public void showOptionsDialog(){
		final Dialog dialogOptions = new Dialog(activity);
		dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogOptions.setCanceledOnTouchOutside(false);
		dialogOptions.setContentView(R.layout.dialog_patient_options);

		Button btnSelPtViewReports = (Button) dialogOptions.findViewById(R.id.btnSelPtViewReports);
		Button btnSelPtProblemList = (Button) dialogOptions.findViewById(R.id.btnSelPtProblemList);
		Button btnScreeningTools  = (Button) dialogOptions.findViewById(R.id.btnScreeningTools);
		Button btnSelPtViewMedications = (Button) dialogOptions.findViewById(R.id.btnSelPtViewMedications);
		Button btnSelPtViewMedicalHistory = (Button) dialogOptions.findViewById(R.id.btnSelPtViewMedicalHistory);
		Button btnSelPtViewPriDiag = dialogOptions.findViewById(R.id.btnSelPtViewPriDiag);
		ImageView ivClose = dialogOptions.findViewById(R.id.ivClose);

		ivClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogOptions.dismiss();
			}
		});

		btnSelPtViewPriDiag.setVisibility(View.GONE);

		btnSelPtViewReports.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogOptions.dismiss();
				openActivity.open(ActivityPinnedReports.class, false);//    //ViewReport.class

			}
		});
		btnSelPtViewMedications.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogOptions.dismiss();
				openActivity.open(PresscriptionActivity.class, false);

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
				//new GloabalMethods(activity).showWebviewDialog(DATA.baseUrl+ ApiManager.SCREENING_TOOL+"/"+DATA.selectedUserCallId,"Screening Tools Report");//temprorry
			}
		});

		btnSelPtViewMedicalHistory.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//dialogOptions.dismiss();
				openActivity.open(PatientMedicalHistory.class, false);
				//openActivity.open(PatientMedicalHistoryNew.class, false);//cp will edit medical history

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


	public void getPetientDetails(){
		ApiManager apiManager = new ApiManager(ApiManager.PATIENT_DETAIL_URGENTCARE+"/"+DATA.selectedUserCallId,"get",null,this, activity);
		//ApiManager.shouldShowPD = false;
		apiManager.loadURL();
	}


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.contains(ApiManager.PATIENT_DETAIL_URGENTCARE)){

			try {
				JSONObject jsonObject = new JSONObject(content).getJSONObject("data");

				JSONObject patient_data = jsonObject.getJSONObject("patient_data");

				ActivityTcmDetails.ptPolicyNo = patient_data.getString("policy_number");
				ActivityTcmDetails.ptDOB  = patient_data.getString("birthdate");
				ActivityTcmDetails.ptAddress = patient_data.getString("residency");
				ActivityTcmDetails.ptZipcode  = patient_data.getString("zipcode");
				ActivityTcmDetails.ptPhone = patient_data.getString("phone");
				ActivityTcmDetails.ptEmail = patient_data.getString("email");
				ActivityTcmDetails.ptFname = patient_data.getString("first_name");
				ActivityTcmDetails.ptLname = patient_data.getString("last_name");


				ActivityTcmDetails.primary_patient_id = patient_data.getString("primary_patient_id");
				ActivityTcmDetails.family_is_online = patient_data.getString("family_is_online");

				//ptRefDate  = patient_data.getString("phone");
				//ptRefDr  = patient_data.getString("phone")+" "
				//+patient_data.getString("phone");
				//ptPriHomeCareDiag  = patient_data.getString("phone");

				//tvSelPtPhone.setText("Mobile: "+patient_data.getString("phone"));
				//tvSelPtEmail.setText("Email: "+patient_data.getString("email"));
				//tvSelPtAddress.setText("Address: "+patient_data.getString("residency"));
				//tvSelPtDOB.setText("DOB: "+patient_data.getString("birthdate"));

				//DATA.selectedUserCallAge = patient_data.getString("age");//not comes in urgentcare api
				//String selectedUserCallAge = patient_data.getString("age");

				//tvSelPtEmail.setText(Html.fromHtml("<font color='"+DATA.APP_THEME_RED_COLOR+"'>Email : </font>"+patient_data.getString("email")));
				tvSelPtEmail.setText(Html.fromHtml("<font color='#2979ff'><u>"+ActivityTcmDetails.ptEmail+"</u></font>"));
				tvSelPtEmail.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						String subject = getString(R.string.app_name)+" clinical care";
						String[] addresses = {ActivityTcmDetails.ptEmail};

						Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
								"mailto",ActivityTcmDetails.ptEmail, null));
						emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses); // String[] addresses
						emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
						//emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
						startActivity(Intent.createChooser(emailIntent, "Send email..."));
					}
				});

				//DATA.selectedUserCallAge = patient_data.getString("age");

				String residency = patient_data.getString("residency");
				//String address2 = patient_data.getString("address2");
				String city = patient_data.optString("city");
				String state = patient_data.optString("state");
				String zipcode = patient_data.optString("zipcode");

				StringBuilder sbAddress = new StringBuilder();
				//sbAddress.append("<font color='"+DATA.APP_THEME_RED_COLOR+"'>Address : </font>");
				if(!TextUtils.isEmpty(residency)){
					sbAddress.append(residency);
					sbAddress.append("\n");
				}
				if(!TextUtils.isEmpty(city)){
					sbAddress.append(city);
					sbAddress.append(", ");
				}
				if(!TextUtils.isEmpty(state)){
					sbAddress.append(state);
					sbAddress.append(", ");
				}
				if(!TextUtils.isEmpty(zipcode)){
					sbAddress.append(zipcode);
				}
				//tvSelPtAddress.setText(Html.fromHtml(sbAddress.toString()));
				tvSelPtAddress.setText(sbAddress.toString());

				//tvSelPtDOB.setText(Html.fromHtml("<font color='"+DATA.APP_THEME_RED_COLOR+"'>DOB: </font>"+patient_data.getString("birthdate")+"  <b>|</b>  <font color='"+DATA.APP_THEME_RED_COLOR+"'>Age: </font>"+selectedUserCallAge));//DATA.selectedUserCallAge
				//tvSelPtDOB.setText(Html.fromHtml("<font color='"+DATA.APP_THEME_RED_COLOR+"'>DOB &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: </font>"+patient_data.getString("birthdate")));
				tvSelPtDOB.setText(patient_data.getString("birthdate"));


				String patientPhone = patient_data.getString("phone");
				//String styledText = "<font color='"+DATA.APP_THEME_RED_COLOR+"'>Mobile &nbsp;&nbsp;&nbsp;: </font>" + "<font color='#2979ff'><u>"+patientPhone+"</u></font>";
				//tvSelPtPhone.setText(Html.fromHtml(styledText));
				tvSelPtPhone.setText(Html.fromHtml("<font color='#2979ff'><u>"+patientPhone+"</u></font>"));
				tvSelPtPhone.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							Intent callIntent = new Intent(Intent.ACTION_CALL);
							callIntent.setData(Uri.parse("tel:"+Uri.encode(patientPhone)));
							callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(callIntent);
						}catch (Exception e){
							e.printStackTrace();
						}
					}
				});


				if(patient_data.has("StoreName")){
					DATA.selectedLiveCare.StoreName = patient_data.getString("StoreName");
				}
				if(patient_data.has("PhonePrimary")){
					DATA.selectedLiveCare.PhonePrimary = patient_data.getString("PhonePrimary");
				}
				if(patient_data.has("pharmacy_address")){
					DATA.selectedLiveCare.pharmacy_address = patient_data.getString("pharmacy_address");
				}



				JSONObject urgentcare_data = jsonObject.getJSONObject("urgentcare_data");
				JSONArray reports = urgentcare_data.getJSONArray("reports");
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
						DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
					}
				});

				if(urgentcare_data.has("symptom_name")){
					DATA.selectedUserCallSympTom = urgentcare_data.getString("symptom_name");
					tvPtSymptom.setText(DATA.selectedUserCallSympTom);
					DATA.selectedLiveCare.symptom_name = DATA.selectedUserCallSympTom;
				}
				if(urgentcare_data.has("condition_name")){
					DATA.selectedUserCallCondition = urgentcare_data.getString("condition_name");
					tvPtCondition.setText(DATA.selectedUserCallCondition);
					DATA.selectedLiveCare.condition_name = DATA.selectedUserCallCondition;
				}
				if(urgentcare_data.has("description")){
					DATA.selectedUserCallDescription = urgentcare_data.getString("description");
					tvPtDescription.setText(DATA.selectedUserCallDescription);
					DATA.selectedLiveCare.description = DATA.selectedUserCallDescription;
				}

				if(urgentcare_data.has("additional_data")){
					String additional_data = urgentcare_data.getString("additional_data");
					if(!additional_data.isEmpty()){
						JSONObject additional_dataJSON = new JSONObject(additional_data);
						if(additional_dataJSON.has("pain_where")){
							DATA.selectedLiveCare.pain_where = additional_dataJSON.getString("pain_where");
							tvPain.setText(DATA.selectedLiveCare.pain_where);
						}
						if(additional_dataJSON.has("pain_severity")){
							DATA.selectedLiveCare.pain_severity = additional_dataJSON.getString("pain_severity");
							tvPainSeverity.setText(DATA.selectedLiveCare.pain_severity);
						}
					}
				}

				//===================Medical History starts=================
				JSONObject medical_history = jsonObject.getJSONObject("medical_history");

				String phistory = medical_history.getString("phistory");
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

				StringBuilder socialHistory = new StringBuilder();
				/*if (is_smoke.equalsIgnoreCase("1")) {
					socialHistory.append("Smoke:Yes, ");
					if ((!smoke_detail.isEmpty()) || (!smoke_detail.equalsIgnoreCase("null"))) {
						String[] smokeArr = smoke_detail.split("/");
						if (smokeArr.length < 2) {

						}else {
							socialHistory.append("How long: "+smokeArr[0]+", How much: "+smokeArr[1]);
						}
					}
				}*/
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


				//===================Medical History Ends=================

				//==========this code is to show only vitals (bp,temp,hr,bmi etc) added in 9Nov2018 in elivecare
				if(!jsonObject.getString("virtual_visit").isEmpty()){
					JSONObject virtual_visit = jsonObject.getJSONObject("virtual_visit");
					if(virtual_visit.has("dateof")){
						tvVitalsDate.setText("Date: "+virtual_visit.getString("dateof"));
					}

					if(virtual_visit.has("pain_related")){
						DATA.selectedLiveCare.pain_related = virtual_visit.getString("pain_related");
						tvPainBodyPart.setText(DATA.selectedLiveCare.pain_related);
					}

					//No need====== copied code from tcmDetaisl Activity
					/*if(virtual_visit.has("symptom_name")){
						DATA.selectedUserCallSympTom = virtual_visit.getString("symptom_name");
						tvPtSymptom.setText(DATA.selectedUserCallSympTom);
						DATA.selectedLiveCare.symptom_name = DATA.selectedUserCallSympTom;
					}
					if(virtual_visit.has("condition_name")){
						DATA.selectedUserCallCondition = virtual_visit.getString("condition_name");
						tvPtCondition.setText(DATA.selectedUserCallCondition);
						DATA.selectedLiveCare.condition_name = DATA.selectedUserCallCondition;
					}
					if(virtual_visit.has("description")){
						DATA.selectedUserCallDescription = virtual_visit.getString("description");
						tvPtDescription.setText(DATA.selectedUserCallDescription);
						DATA.selectedLiveCare.description = DATA.selectedUserCallDescription;
					}

					if(virtual_visit.has("additional_data")){
						String additional_data = virtual_visit.getString("additional_data");
						if(!additional_data.isEmpty()){
							JSONObject additional_dataJSON = new JSONObject(additional_data);
							if(additional_dataJSON.has("pain_where")){
								DATA.selectedLiveCare.pain_where = additional_dataJSON.getString("pain_where");
								tvPain.setText(DATA.selectedLiveCare.pain_where);
							}
							if(additional_dataJSON.has("pain_severity")){
								DATA.selectedLiveCare.pain_severity = additional_dataJSON.getString("pain_severity");
								tvPainSeverity.setText(DATA.selectedLiveCare.pain_severity);
							}
						}
					}
					if(virtual_visit.has("symptom_details")){
						DATA.selectedLiveCare.symptom_details = virtual_visit.getString("symptom_details");
						tvSymptomDetails.setText(DATA.selectedLiveCare.symptom_details);
						ptPriHomeCareDiag = DATA.selectedLiveCare.symptom_details;
					}*/

					if(virtual_visit.has("ot_data")&& !virtual_visit.getString("ot_data").isEmpty()){
						JSONObject virtual_ot_data = new JSONObject(virtual_visit.getString("ot_data"));
						if(virtual_ot_data.has("ot_respirations")){
							String ot_respirations = virtual_ot_data.getString("ot_respirations");
							etOTRespirations.setText(ot_respirations);
						}
						if(virtual_ot_data.has("ot_blood_sugar")){
							String ot_blood_sugar = virtual_ot_data.getString("ot_blood_sugar");
							etOTBloodSugar.setText(ot_blood_sugar);
						}
						if(virtual_ot_data.has("ot_hr")){
							String ot_hr = virtual_ot_data.getString("ot_hr");
							etOTHR.setText(ot_hr);
						}
						if(virtual_ot_data.has("ot_bp")){
							String ot_bp = virtual_ot_data.getString("ot_bp");
							etOTBP.setText(ot_bp);
						}
						if(virtual_ot_data.has("ot_saturation")){
							String ot_saturation = virtual_ot_data.getString("ot_saturation");
							etOTO2Saturations.setText(ot_saturation);
						}

						if(virtual_ot_data.has("ot_height")){
							String ot_height = virtual_ot_data.getString("ot_height");
							etOTHeight.setText(ot_height);
						}
						if(virtual_ot_data.has("ot_temperature")){
							String ot_temperature = virtual_ot_data.getString("ot_temperature");
							etOTTemperature.setText(ot_temperature);
						}
						if(virtual_ot_data.has("ot_weight")){
							String ot_weight = virtual_ot_data.getString("ot_weight");
							etOTWeight.setText(ot_weight);
						}
						if(virtual_ot_data.has("ot_bmi")){
							String bmi = virtual_ot_data.getString("ot_bmi");
							etOTBMI.setText(bmi);
						}
					}

					//No need====== copied code from tcmDetaisl Activity
					/*JSONArray reports = virtual_visit.getJSONArray("reports");
					final ArrayList<String> vvImgs = new ArrayList<>();
					for (int i = 0; i < reports.length(); i++) {
						vvImgs.add(reports.getJSONObject(i).getString("report_name"));
					}
					gvReportImages.setAdapter(new VVReportImagesAdapter2(activity,vvImgs));
					gvReportImages.setExpanded(true);
					gvReportImages.setPadding(5,5,5,5);

					gvReportImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							DialogPatientInfo.showPicDialog(activity,vvImgs.get(position));
						}
					});*/
				}
			} catch (JSONException e) {
				e.printStackTrace();
				//customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}

		}
	}
}
