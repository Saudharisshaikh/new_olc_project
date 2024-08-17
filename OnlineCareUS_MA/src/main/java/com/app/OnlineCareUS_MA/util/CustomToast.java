package com.app.OnlineCareUS_MA.util;

import com.app.OnlineCareUS_MA.R;

import android.app.Activity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class CustomToast {

	Activity a;
	TextView text;
	ImageView img;
	LayoutInflater inflater;
	View layout;

	public CustomToast(Activity a) {
		this.a = a;

	}


	public void showToast(String message, int imageId, int length) {

		inflater = a.getLayoutInflater();
		layout = inflater.inflate(R.layout.custom_toast,null);

		text = (TextView) layout.findViewById(R.id.tvCustomToast);
		text.setText(message);

		img = (ImageView) layout.findViewById(R.id.imgCustomToast);

		if(imageId != 0) {
			img.setVisibility(View.VISIBLE);
			img.setImageResource(imageId);
		}
		else {
			img.setVisibility(View.GONE);
		}


		Toast toast = new Toast(a);
		toast.setGravity(Gravity.BOTTOM, 0, 150);
		if (length == 1) {
			toast.setDuration(Toast.LENGTH_LONG);
		} else {
			toast.setDuration(Toast.LENGTH_SHORT);
		}
		toast.setView(layout);
		toast.show();

	}
}

