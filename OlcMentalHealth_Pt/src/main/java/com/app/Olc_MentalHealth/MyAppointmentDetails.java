package com.app.Olc_MentalHealth;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.Olc_MentalHealth.api.ApiManager;
import com.app.Olc_MentalHealth.util.CheckInternetConnection;
import com.app.Olc_MentalHealth.util.DATA;
import com.app.Olc_MentalHealth.util.GloabalMethods;
import com.app.Olc_MentalHealth.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class MyAppointmentDetails extends BaseActivity {

	ImageView imgSelDrImage;
	TextView tvSelDrName,tvApptSymp,tvApptCond,tvApptDesc,tvApptDate,tvApptTime,tvApptReffered,tvApptReason,tvSelDrDesg;
	Button btnApptCancel;

	Activity activity;
	CheckInternetConnection checkInternetConnection;
	FrameLayout imgCont;
	String aptID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_appontmt_details);

		activity = MyAppointmentDetails.this;
		checkInternetConnection = new CheckInternetConnection(activity);


		tvSelDrName = (TextView) findViewById(R.id.tvSelDrName);
		imgSelDrImage = (ImageView) findViewById(R.id.imgSelDrImage);
		tvApptSymp = (TextView) findViewById(R.id.tvApptSymp);
		tvApptCond = (TextView) findViewById(R.id.tvApptCond);
		tvApptDesc = (TextView) findViewById(R.id.tvApptDesc);
		tvApptDate = (TextView) findViewById(R.id.tvApptDate);
		tvApptTime = (TextView) findViewById(R.id.tvApptTime);
		//tvApptStatus = (TextView) findViewById(R.id.tvApptStatus);
		tvApptReffered = (TextView) findViewById(R.id.tvApptReffered);
		tvApptReason = (TextView) findViewById(R.id.tvApptReason);
		tvSelDrDesg = (TextView) findViewById(R.id.tvSelDrDesg);
		imgCont = (FrameLayout) findViewById(R.id.imgCont);

		btnApptCancel = (Button) findViewById(R.id.btnApptCancel);
		btnApptCancel.setVisibility(View.GONE);

		Intent i = getIntent();

		String aptDrName = i.getStringExtra("aptDrName");
		String aptDrUrl = i.getStringExtra("aptDrUrl");
		String aptSymp = i.getStringExtra("aptSymp");
		String aptCond = i.getStringExtra("aptCond");
		String aptdesc = i.getStringExtra("aptdesc");
		String aptStatus = i.getStringExtra("aptStatus");
		String aptDate = i.getStringExtra("aptDate");
		//String aptId = i.getStringExtra("aptId");
		String aptFromTime = i.getStringExtra("aptFromTime");
		String aptToTime = i.getStringExtra("aptToTime");
		aptID = i.getStringExtra("aptID");
		String aptSlotID = i.getStringExtra("aptSlotID");
		String aptReason = i.getStringExtra("aptReason");
		String aptDrCat = i.getStringExtra("aptDrCat");

		tvApptReason.setText(aptReason);
		tvSelDrDesg.setText(aptDrCat);

//		if(aptStatus.equals("0")) {
//			
//			btnApptCancel.setVisibility(View.VISIBLE);
//		}
//		else {
//			btnApptCancel.setVisibility(View.GONE);
//
//		}
		DATA.print("--slot id "+aptSlotID);
		if (aptSlotID.equals("0")) {
			imgCont.setVisibility(View.GONE);
			tvSelDrName.setVisibility(View.GONE);
			tvSelDrDesg.setVisibility(View.GONE);
			tvApptReffered.setVisibility(View.VISIBLE);
		} else {
			imgCont.setVisibility(View.VISIBLE);
			tvSelDrName.setVisibility(View.VISIBLE);
			tvSelDrDesg.setVisibility(View.VISIBLE);
			tvApptReffered.setVisibility(View.GONE);
			//UrlImageViewHelper.setUrlDrawable(imgSelDrImage, aptDrUrl, R.drawable.icon_call_screen);
			DATA.loadImageFromURL(aptDrUrl, R.drawable.icon_call_screen, imgSelDrImage);
			tvSelDrName.setText(aptDrName);//"Dr. "+
		}


		tvApptSymp.setText(aptSymp);
		tvApptCond.setText(aptCond);
		tvApptDesc.setText(aptdesc);
		tvApptDate.setText(aptDate);
		//tvApptTime.setText(aptFromTime + " To: "+aptToTime);
		tvApptTime.setText(aptFromTime);

		/*tvApptStatus.setText(aptStatus);
		if (aptStatus.equalsIgnoreCase("confirmed")) {
			tvApptStatus.setTextColor(Color.parseColor("#3A7D00"));
		}*/


		Button btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		Button btnCancelAppointment = (Button) findViewById(R.id.btnCancelAppointment);
		btnCancelAppointment.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				confirmDialog( "Confirm", "Are you sure? You want to cancel this appointment");
			}
		});

		if (aptStatus.equalsIgnoreCase("Completed") || aptStatus.equalsIgnoreCase("Cancelled")) {
			btnCancelAppointment.setVisibility(View.GONE);
		}
	}//oncreate


	public void cancelAppointment(String appointmentId) {

		AsyncHttpClient client = new AsyncHttpClient();
		ApiManager.addHeader(activity,client);
		RequestParams params = new RequestParams();
		params.put("app_id", appointmentId);

		DATA.print("--in cancelAppointment  params: "+params.toString());
		DATA.showLoaderDefault(activity,"");
		client.post(DATA.baseUrl+"/cancelAppointment", params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] response) {
				// called when response HTTP status is "200 OK"
				DATA.dismissLoaderDefault();
				try{
					String content = new String(response);

					DATA.print("--reaponce in cancelAppointment "+content);
					//--reaponce in cancelAppointment {"status":"success","msg":"Your appointment has been cancelled"}

					try {
						JSONObject jsonObject = new JSONObject(content);
						if (jsonObject.getString("status").equalsIgnoreCase("success")) {
							customToast.showToast("Your appointment has been cancelled",0,0);
							finish();

						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
					}

				}catch (Exception e){
					e.printStackTrace();
					DATA.print("-- responce onsuccess: cancelAppointment, http status code: "+statusCode+" Byte responce: "+response);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
				// called when response HTTP status is "4XX" (eg. 401, 403, 404)
				DATA.dismissLoaderDefault();
				try {
					String content = new String(errorResponse);
					DATA.print("--onFailure in cancelAppointment "+content);
					new GloabalMethods(activity).checkLogin(content,statusCode);
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);

				}catch (Exception e1){
					e1.printStackTrace();
					customToast.showToast(DATA.CMN_ERR_MSG,0,0);
				}
			}
		});

	}//end cancelAppointment


	//MaterialDialog mMaterialDialog;
	public void confirmDialog(String tittle , String content) {
		new AlertDialog.Builder(activity, R.style.CustomAlertDialogTheme)
				.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (checkInternetConnection.isConnectedToInternet()) {
							cancelAppointment(aptID);
						} else {
							customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
						}
					}
				})
				.setNegativeButton("No", null)
				.create()
				.show();

		/*mMaterialDialog = new MaterialDialog(context)
				//.setTitle(tittle)
				.setMessage(content)
				.setPositiveButton("Yes", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mMaterialDialog.dismiss();

						if (checkInternetConnection.isConnectedToInternet()) {
							cancelAppointment(aptID);
						} else {
							customToast.showToast(DATA.NO_NETWORK_MESSAGE,0,0);
						}
					}
				}).setNegativeButton("No", new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						mMaterialDialog.dismiss();
					}
				});
		mMaterialDialog.show();*/

	}
}
