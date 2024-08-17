package com.app.covacard.covacard;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.print.PrintHelper;

import com.app.covacard.BaseActivity;
import com.app.covacard.R;
import com.app.covacard.util.DATA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityCardDetail extends BaseActivity{


	ImageView ivUserImage,ivQRcodeImg,ivVCfrontImg,ivVCBackImg;
	TextView tvUserFullName,tvDate,tvName,tvRelation;


	public static CardBean cardBeanSelected;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_card_detail);

		if(getSupportActionBar() != null){
			getSupportActionBar().setTitle(cardBeanSelected.name);
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		}

		ivUserImage = findViewById(R.id.ivUserImage);
		ivQRcodeImg = findViewById(R.id.ivQRcodeImg);
		ivVCfrontImg = findViewById(R.id.ivVCfrontImg);
		ivVCBackImg = findViewById(R.id.ivVCBackImg);
		tvUserFullName = findViewById(R.id.tvUserFullName);
		tvDate = findViewById(R.id.tvDate);
		tvName = findViewById(R.id.tvName);
		tvRelation = findViewById(R.id.tvRelation);

		DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, ivUserImage);
		DATA.loadImageFromURL(cardBeanSelected.qrcode, R.drawable.ic_qrcode, ivQRcodeImg);
		DATA.loadImageFromURL(cardBeanSelected.front_pic, R.drawable.icon_call_screen, ivVCfrontImg);
		DATA.loadImageFromURL(cardBeanSelected.back_pic, R.drawable.icon_call_screen, ivVCBackImg);


		tvUserFullName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
		//tvDate.setText(cardBeanSelected.);
		tvName.setText(cardBeanSelected.name);
		tvRelation.setText(cardBeanSelected.relation);

		String formatedDate = cardBeanSelected.date;//2021-04-12 09:17:46
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}

		tvDate.setText(formatedDate);


		Button btnPrint = findViewById(R.id.btnPrint);
		btnPrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//createPDFWithIText();
				PrintHelper printHelper = new PrintHelper(activity);
				printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
				Bitmap bitmap = getScreenShotFromView(findViewById(R.id.cardViewDetail));//((BitmapDrawable) mImageView.getDrawable()).getBitmap();
				printHelper.printBitmap(getResources().getString(R.string.app_name)+" print out", bitmap);
			}
		});

	}




	private Bitmap getScreenShotFromView(View v){
		// create a bitmap object
		 Bitmap screenshot = null;
		try {
			// inflate screenshot object
			// with Bitmap.createBitmap it
			// requires three parameters
			// width and height of the view and
			// the background color
			screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
			// Now draw this bitmap on a canvas
			Canvas canvas = new Canvas(screenshot);
			v.draw(canvas);
		} catch (Exception e) {
			e.printStackTrace();
			//Log.e("GFG", "Failed to capture screenshot because:" + e.message)
		}
		// return the bitmap
		return screenshot;
	}

}
