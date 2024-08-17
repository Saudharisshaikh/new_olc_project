package com.app.greatriverma;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.app.greatriverma.util.CheckInternetConnection;
import com.app.greatriverma.util.CustomToast;
import com.app.greatriverma.util.DATA;
import com.app.greatriverma.util.DialogPatientInfo;
import com.app.greatriverma.util.GloabalMethods;

public class CustomDialogActivity extends Activity {

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	CustomToast customToast;

	@Override
	protected void onResume() {
		if(DATA.isSOAP_NotesSent){
			DATA.isSOAP_NotesSent = false;
			AlertDialog alertDialog =
					new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT).setTitle("Info")
					.setMessage("Information saved successfully.")
					.setPositiveButton("Done",null).create();
			alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					finish();
				}
			});
			alertDialog.show();
		}
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_custom_dialog);
		this.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

		activity = CustomDialogActivity.this;
		checkInternetConnection = new CheckInternetConnection(activity);
		customToast = new CustomToast(activity);

		Button btnBackToHome = (Button) findViewById(R.id.btnBackToHome);
		Button btnAdditionalDetails = (Button) findViewById(R.id.btnAdditionalDetails);
		Button btnAddPrNotes = (Button) findViewById(R.id.btnAddPrNotes);

		btnBackToHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), MainActivityNew.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

				finish();
			}
		});

		btnAdditionalDetails.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInternetConnection.isConnectedToInternet()) {
					if(DialogPatientInfo.patientIdGCM.isEmpty()){
						startActivity(new Intent(activity,ActivityTCM_2.class));
					}else{
						DATA.selectedUserCallId = DialogPatientInfo.patientIdGCM;
						new GloabalMethods(activity).showAddSOAPDialog();
					}
				}
				else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});


		btnAddPrNotes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(checkInternetConnection.isConnectedToInternet()) {
					if(DATA.selectedUserCallId.isEmpty()){
						if(DialogPatientInfo.patientIdGCM.isEmpty()){
							Intent intent = new Intent(activity,ActivityTCM_2.class);
							intent.putExtra("isForProgrssNotes",true);
							startActivity(intent);
						}else{
							DATA.selectedUserCallId = DialogPatientInfo.patientIdGCM;
							//new GloabalMethods(activity).showAddSOAPDialog();
							startActivity(new Intent(activity,ActivityProgressNotes.class));
						}
					}else {
						startActivity(new Intent(activity,ActivityProgressNotes.class));
					}

				}
				else {
					customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
				}
			}
		});

		/*if(DATA.incomingCall){
			btnAdditionalDetails.setVisibility(View.INVISIBLE);
		}else {
			btnAdditionalDetails.setVisibility(View.VISIBLE);
		}*/

		/*TextView tvDialogSubmit = (TextView) findViewById(R.id.tvDialogSubmit);
		TextView tvDialogNoThanks = (TextView) findViewById(R.id.tvDialogNoThanks);
		
		
		tvDialogSubmit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Toast.makeText(CustomDialogActivity.this, "Thank you", 0).show();
				finish();
			}
		});
		
		tvDialogNoThanks.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});*/
	}
	
}
