package com.app.covacard.covacard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.covacard.BaseActivity;
import com.app.covacard.Login;
import com.app.covacard.R;
import com.app.covacard.UpdateProfile;
import com.app.covacard.services.GPSTracker;
import com.app.covacard.util.DATA;
import com.app.covacard.util.SharedPrefsHelper;

public class ActivityCovacardHome extends BaseActivity{


	TextView tvAddCard,tvShowCard,tvTestLocations,tvTestResults,tvIdCard,tvCovidTestPast,tvTeleHealth,tvProfile,tvLogout;


	@Override
	protected void onResume() {
		super.onResume();
		GPSTracker gpsTracker = new GPSTracker(activity);
		if (gpsTracker.canGetLocation()) {
			prefs.edit().putString("userLatitude", gpsTracker.getLatitude()+"").commit();
			prefs.edit().putString("userLongitude", gpsTracker.getLongitude()+"").commit();
			System.out.println("-- mainActivity on resume : lat: "+gpsTracker.getLatitude()+" lng: "+gpsTracker.getLongitude());
			gpsTracker.stopUsingGPS();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_covacard_home);


		setSupportActionBar(findViewById(R.id.toolbar));
		Button btnToolbar = findViewById(R.id.btnToolbar);
		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}
		btnToolbar.setVisibility(View.GONE);

		tvAddCard = findViewById(R.id.tvAddCard);
		tvShowCard = findViewById(R.id.tvShowCard);
		tvTestLocations = findViewById(R.id.tvTestLocations);
		tvTestResults = findViewById(R.id.tvTestResults);
		tvIdCard = findViewById(R.id.tvIdCard);
		tvCovidTestPast = findViewById(R.id.tvCovidTestPast);
		tvTeleHealth = findViewById(R.id.tvTeleHealth);
		tvProfile = findViewById(R.id.tvProfile);
		tvLogout = findViewById(R.id.tvLogout);

		ImageView ivAdView = findViewById(R.id.ivAdView);


		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.tvAddCard:
					openActivity.open(ActivityAddCard.class, false);
					break;

				case R.id.tvShowCard:
					openActivity.open(ActivityCardsList.class, false);
					break;

				case R.id.tvTestLocations:
					openActivity.open(ActivityTestLocations.class, false);
					break;

				case R.id.tvTestResults:
					openActivity.open(ActivityTestResultsList.class, false);//ActivityAddTestResults
					break;
				case R.id.tvProfile:
					openActivity.open(UpdateProfile.class, false);
					break;
				case R.id.tvLogout:

					new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Confirm")
							.setMessage("Are you sure ? Do you want to logout ?")
							.setPositiveButton("Yes Logout", new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

									SharedPreferences.Editor editor = prefs.edit();
									editor.clear();
									editor.apply();
									SharedPrefsHelper.getInstance().clearAllData();

									Intent intent1 = new Intent(getApplicationContext(), Login.class);
									intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(intent1);
								}
							})
							.setNegativeButton("Not Now", null).create().show();
					break;
				case R.id.ivAdView:
					DATA.addIntent(activity);
					break;
				default:
					AlertDialog alertDialog = new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme).setTitle("Comming soon")
							.setMessage("This feature will be available in the near future")
							.setPositiveButton("Done",null).create();
					alertDialog.show();
					break;
			}
		};
		tvAddCard.setOnClickListener(onClickListener);
		tvShowCard.setOnClickListener(onClickListener);
		tvTestLocations.setOnClickListener(onClickListener);
		tvTestResults.setOnClickListener(onClickListener);
		tvIdCard.setOnClickListener(onClickListener);
		tvCovidTestPast.setOnClickListener(onClickListener);
		tvTeleHealth.setOnClickListener(onClickListener);
		tvLogout.setOnClickListener(onClickListener);
		tvProfile.setOnClickListener(onClickListener);
		ivAdView.setOnClickListener(onClickListener);

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//return super.onCreateOptionsMenu(menu);
		return false;
	}
}
