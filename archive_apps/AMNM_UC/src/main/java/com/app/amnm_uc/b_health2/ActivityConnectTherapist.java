package com.app.amnm_uc.b_health2;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.app.amnm_uc.BaseActivity;
import com.app.amnm_uc.R;

public class ActivityConnectTherapist extends BaseActivity{


	Button btn_call911,btnImmCare,btnAppointment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_therapist);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle("Connect Therapist");
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		btn_call911 = findViewById(R.id.btn_call911);
		btnImmCare = findViewById(R.id.btnImmCare);
		btnAppointment = findViewById(R.id.btnAppointment);

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.btn_call911:
					try {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+Uri.encode("911")));
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(callIntent);
					}catch (Exception  e){e.printStackTrace();}
					break;
				case R.id.btnImmCare:
					if(sharedPrefsHelper.get(ActivityIcWaitingRoom.ISIN_ICROOM_PREFS_KEY, false)){
						openActivity.open(ActivityIcWaitingRoom.class, false);
					}else {
						ActivityConsent.flagNav = 1;
						openActivity.open(ActivityConsent.class, false);
					}
					break;
				case R.id.btnAppointment:
					ActivityConsent.flagNav = 2;
					openActivity.open(ActivityConsent.class, false);
					break;
				default:
					break;
			}
		};
		btn_call911.setOnClickListener(onClickListener);
		btnImmCare.setOnClickListener(onClickListener);
		btnAppointment.setOnClickListener(onClickListener);

	}

}
