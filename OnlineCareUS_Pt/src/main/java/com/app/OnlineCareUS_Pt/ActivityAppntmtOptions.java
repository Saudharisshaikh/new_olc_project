package com.app.OnlineCareUS_Pt;

import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.app.OnlineCareUS_Pt.util.GloabalMethods;

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
					openActivity.open(SearchADoctor.class, true);
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

}
