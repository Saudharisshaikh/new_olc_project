package com.app.mhcsn_uc;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.app.mhcsn_uc.b_health2.GetLiveCareFormBhealth;
import com.app.mhcsn_uc.util.GloabalMethods;
import com.darsh.multipleimageselect.activities.AlbumSelectActivity;
import com.darsh.multipleimageselect.helpers.Constants;

import ss.com.bannerslider.views.BannerSlider;

public class ActivityAppntmtOptions extends BaseActivity{

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_apt_options);

		TextView tvBookApptmnt = findViewById(R.id.tvBookApptmnt);
		TextView tvMyAppointments = findViewById(R.id.tvMyAppointments);

		BannerSlider bannerSlider = (BannerSlider) findViewById(R.id.banner_slider1);
		GloabalMethods gloabalMethods = new GloabalMethods(appCompatActivity);
		gloabalMethods.setupBannerSlider(bannerSlider,false);

		OnClickListener onClickListener = view -> {

			//dialog.dismiss();

			switch (view.getId()){
				case R.id.tvBookApptmnt:
					showAskAptDialog();
					break;
				case R.id.tvMyAppointments:
					openActivity.open(MyAppointments.class, true);
					break;
				default:
					break;
			}
		};
		tvBookApptmnt.setOnClickListener(onClickListener);
		tvMyAppointments.setOnClickListener(onClickListener);

	}



	public void showAskAptDialog(){
		final Dialog dialogSupport = new Dialog(activity);
		dialogSupport.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSupport.setContentView(R.layout.dialog_ask_apt);

		TextView tvImmeAppt = (TextView) dialogSupport.findViewById(R.id.tvImmeAppt);
		TextView tvEmoWellApt = (TextView) dialogSupport.findViewById(R.id.tvEmoWellApt);
		TextView tvNotNow = (TextView) dialogSupport.findViewById(R.id.tvNotNow);



		tvImmeAppt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSupport.dismiss();

				openActivity.open(SearchADoctor.class, true);
			}
		});
		tvEmoWellApt.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSupport.dismiss();

				openActivity.open(GetLiveCareFormBhealth.class, true);
			}
		});
		tvNotNow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialogSupport.dismiss();

			}
		});
		dialogSupport.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogSupport.show();
	}

}
