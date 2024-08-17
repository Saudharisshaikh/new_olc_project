package com.app.onlinecare;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.onlinecare.util.DATA;
import com.app.onlinecare.util.OpenActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class SelectedDoctorsProfile extends BaseActivity{
	
	TextView tvSelDrName,tvSelDrPhone,tvSelDrDesignation,btnSelDrReqAppointment,btnSelDrViewSchedule;
	ImageView imgSelDrImage;
	LinearLayout mapCont;
	
	OpenActivity openActivity;
	AppCompatActivity activity;
	
	public static boolean shouldProceed = false;
	public static boolean goBack = false;
	public static boolean goBackToForm = false;
	
	@Override
	protected void onResume() {
		if (shouldProceed) {
			shouldProceed = false;
			openActivity.open(BookAppointment.class, true);
		}
		
		if (goBack) {
			goBack = false;
			onBackPressed();
		}
		
		if (goBackToForm) {
			goBackToForm = false;
			DoctorsList.goback = true;
			onBackPressed();
		}
		super.onResume();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selected_doctors_profile);
		
		activity = SelectedDoctorsProfile.this;
		
		mapCont = (LinearLayout) findViewById(R.id.mapCont);
		
		tvSelDrName = (TextView)findViewById(R.id.tvSelDrName);
		tvSelDrPhone = (TextView)findViewById(R.id.tvSelDrPhone);
		tvSelDrDesignation = (TextView)findViewById(R.id.tvSelDrDesignation);
		btnSelDrReqAppointment = (TextView) findViewById(R.id.btnSelDrReqAppointment);
		btnSelDrViewSchedule = (TextView) findViewById(R.id.btnSelDrViewSchedule);
		
		openActivity = new OpenActivity(activity);
		imgSelDrImage = (ImageView)findViewById(R.id.imgSelDrImage);
		
		String drName = DATA.doctorsModelForApptmnt.fName+" "+DATA.doctorsModelForApptmnt.lName;
		//tvSelDrName.setText(drName.toUpperCase(Locale.getDefault()));
		tvSelDrName.setText(drName);
		tvSelDrDesignation.setText(DATA.doctorsModelForApptmnt.designation);
		tvSelDrPhone.setText("Phone: "+DATA.doctorsModelForApptmnt.mobile);

		DATA.loadImageFromURL(DATA.doctorsModelForApptmnt.image,R.drawable.icon_call_screen, imgSelDrImage);
		
		//filter(DATA.doctorsModelForApptmnt.id);

		
		btnSelDrReqAppointment.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				openActivity.open(BookAppointment.class, true);

			}
		});
		
		btnSelDrViewSchedule.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				openActivity.open(DrSchedule.class, false);

			}
		});
		
		double latitude = 0.0;
		double longitude = 0.0;
		try {
			 latitude = Double.parseDouble(DATA.doctorsModelForApptmnt.latitude);
			 longitude = Double.parseDouble(DATA.doctorsModelForApptmnt.longitude);
		} catch (Exception e) {
			// TODO: handle exception
			mapCont.setVisibility(View.GONE);
		}
		
		if (latitude != 0.0  && longitude != 0.0) {
			initilizeMap(latitude, longitude, drName);
		}


		TextView tvQualification = (TextView) findViewById(R.id.tvQualification);
		TextView tvCareerData = (TextView) findViewById(R.id.tvCareerData);

		tvQualification.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(activity).setTitle("Qualification").setMessage(DATA.doctorsModelForApptmnt.qualification)
						.setPositiveButton("Done",null).show();
			}
		});
		tvCareerData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(activity).setTitle("Career Data").setMessage(DATA.doctorsModelForApptmnt.careerData)
						.setPositiveButton("Done",null).show();
			}
		});
	}

	/*public void filter(String filterText) {

		DATA.allScheduleFiltered = new ArrayList<DrSheduleModel>();
		DrSheduleModel temp;

		for(int i = 0; i<DATA.allSchedule.size(); i++) {

			temp = new DrSheduleModel();

			if(DATA.allSchedule.get(i).drId.equals(filterText)) {
				
				System.out.println("item added");

				temp.id = DATA.allSchedule.get(i).id;
				temp.drId = DATA.allSchedule.get(i).drId;
				temp.fromTime = DATA.allSchedule.get(i).fromTime;
				temp.toTime = DATA.allSchedule.get(i).toTime;
				temp.eveningFromTime = DATA.allSchedule.get(i).eveningFromTime;
				temp.eveningToTime = DATA.allSchedule.get(i).eveningToTime;
				temp.day = DATA.allSchedule.get(i).day;

				DATA.allScheduleFiltered.add(temp);	

				temp = null;
			}

		}

	}*///filter
	
	
	
	private GoogleMap googleMap;
	View mapView;
	private void initilizeMap(double latitude, double longitude, String drName) {

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
					//System.out.println(""+styleStatus);

					//=======================Start existing Code here================================================


					googleMap.clear();
					googleMap.setTrafficEnabled(true);
					if(!permissionsChecker.lacksPermission(Manifest.permission.ACCESS_FINE_LOCATION)){
						googleMap.setMyLocationEnabled(true);
					}
					googleMap.setBuildingsEnabled(true);

					MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title(drName)
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

					LatLng cpos = new LatLng(latitude, longitude);
					CameraUpdate update = CameraUpdateFactory.newLatLngZoom(cpos, 15);
					googleMap.animateCamera(update);
					// check if map is created successfully or not
					if (googleMap == null) {
						Toast.makeText(getApplicationContext(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
					}

				}
			};

			MapFragment mapFragment = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map));
			mapView = mapFragment.getView();
			mapFragment.getMapAsync(onMapReadyCallback);
		}

		//====================================GM===========================
	}
}
