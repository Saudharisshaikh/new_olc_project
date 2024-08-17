package com.digihealthcard;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.print.PrintHelper;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.loopj.android.http.RequestParams;
import com.digihealthcard.R;
import com.digihealthcard.api.ApiManager;
import com.digihealthcard.model.CardBean;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;
import com.digihealthcard.util.ImageOverlayView;
import com.digihealthcard.util.ShareScreenshot;
import com.digihealthcard.util.StylingOptions;
import com.stfalcon.frescoimageviewer.ImageViewer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Random;

import cz.msebera.android.httpclient.util.TextUtils;
import jp.wasabeef.fresco.processors.GrayscalePostprocessor;

public class ActivityCardDetail extends ActivityBaseDrawer {


	ImageView ivUserImage,ivQRcodeImg,ivVCfrontImg,ivVCBackImg;
	TextView tvUserFullName,tvDate,tvName,tvRelation,tvTypeOfCard;
	SwipeRefreshLayout swiperefresh;


	public static CardBean cardBeanSelected;

	boolean isFromIdCard;
	String cardDetailsAPI = ApiManager.GET_CARD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_card_detail);
		getLayoutInflater().inflate(R.layout.activity_card_detail, container_frame);

		isFromIdCard = getIntent().getBooleanExtra("isFromIdCard", false);

		if(getSupportActionBar() != null){
			/*getSupportActionBar().setTitle(cardBeanSelected.name);
			getSupportActionBar().setLogo(R.drawable.ic_launcher);
			getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
			getSupportActionBar().hide();
		}

		tvToolbarTitle.setText(cardBeanSelected.name);
		ivToolbarBack.setVisibility(View.VISIBLE);
		ivToolbarHome.setVisibility(View.VISIBLE);
		btnToolbarAdd.setVisibility(View.GONE);
		/*btnToolbarAdd.setText("Add New");
		btnToolbarAdd.setOnClickListener(v -> {
			openActivity.open(ActivityAddCard.class, false);
		});*/

		ivUserImage = findViewById(R.id.ivUserImage);
		ivQRcodeImg = findViewById(R.id.ivQRcodeImg);
		ivVCfrontImg = findViewById(R.id.ivVCfrontImg);
		ivVCBackImg = findViewById(R.id.ivVCBackImg);
		tvUserFullName = findViewById(R.id.tvUserFullName);
		tvDate = findViewById(R.id.tvDate);
		tvName = findViewById(R.id.tvName);
		tvRelation = findViewById(R.id.tvRelation);
		tvTypeOfCard = findViewById(R.id.tvTypeOfCard);


		showCard();

		ivVCfrontImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPicker(0);
			}
		});
		ivVCBackImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showPicker(1);
			}
		});


		Button btnPrint = findViewById(R.id.btnPrint);
		Button btnView = findViewById(R.id.btnView);
		btnPrint.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//createPDFWithIText();

				/*PrintHelper printHelper = new PrintHelper(activity);
				printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
				Bitmap bitmap = getScreenShotFromView(findViewById(R.id.cardViewDetail));//((BitmapDrawable) mImageView.getDrawable()).getBitmap();
				printHelper.printBitmap(getResources().getString(R.string.app_name)+" print out", bitmap);*/

				showAskPrintDialog();
			}
		});

		btnView.setEnabled(!TextUtils.isEmpty(cardBeanSelected.code_url));
		btnView.setOnClickListener(v -> {
			new GloabalMethods(activity).showWebviewDialog(cardBeanSelected.code_url, "Card Preview");
		});


		getSingleCardFromAPI();


		//======================swip to refresh==================================
		swiperefresh = findViewById(R.id.swiperefresh);
		int colorsArr[] = {Color.parseColor("#3cba54"), Color.parseColor("#f4c20d"), Color.parseColor("#db3236"), Color.parseColor("#4885ed")};
		swiperefresh.setColorSchemeColors(colorsArr);
		swiperefresh.setOnRefreshListener(
				new SwipeRefreshLayout.OnRefreshListener() {
					@Override
					public void onRefresh() {
						if(!checkInternetConnection.isConnectedToInternet()){
							swiperefresh.setRefreshing(false);
						}
						ApiManager.shouldShowLoader = false;
						getSingleCardFromAPI();
					}
				}
		);
		//======================swip to refresh ends=============================


		super.lockApp(sharedPrefsHelper.get("isAppLocked", false));
	}


	private void getSingleCardFromAPI(){
		if(isFromIdCard){
			cardDetailsAPI = ApiManager.GET_ID_CARD;
		}else {
			cardDetailsAPI = ApiManager.GET_CARD;
		}
		RequestParams params = new RequestParams();
		params.put("id", cardBeanSelected.id);
		ApiManager apiManager = new ApiManager(cardDetailsAPI, "post", params, apiCallBack, activity);
		apiManager.loadURL();
	}


	private void showCard(){
		/*DATA.loadImageFromURL(prefs.getString("image", ""), R.drawable.icon_call_screen, ivUserImage);
		DATA.loadImageFromURL(cardBeanSelected.qrcode, R.drawable.ic_qrcode, ivQRcodeImg);
		DATA.loadImageFromURL(cardBeanSelected.front_pic, R.drawable.ic_placeholder_3, ivVCfrontImg);
		DATA.loadImageFromURL(cardBeanSelected.back_pic, R.drawable.ic_placeholder_3, ivVCBackImg);*/

		try {
			Glide.with(activity).setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_default_user_squire).error(R.drawable.ic_default_user_squire)).load(prefs.getString("image", "")).into(ivUserImage);
		}catch (Exception e){e.printStackTrace();}
		try {
			Glide.with(activity).setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_qrcode).error(R.drawable.ic_qrcode)).load(cardBeanSelected.qrcode).into(ivQRcodeImg);
		}catch (Exception e){e.printStackTrace();}
		try {
			Glide.with(activity).setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_placeholder_3).error(R.drawable.ic_placeholder_3)).load(cardBeanSelected.front_pic).into(ivVCfrontImg);
		}catch (Exception e){e.printStackTrace();}
		try {
			Glide.with(activity).setDefaultRequestOptions(new RequestOptions().placeholder(R.drawable.ic_placeholder_3).error(R.drawable.ic_placeholder_3)).load(cardBeanSelected.back_pic).into(ivVCBackImg);
		}catch (Exception e){e.printStackTrace();}


		tvUserFullName.setText(prefs.getString("first_name", "")+" "+prefs.getString("last_name", ""));
		//tvDate.setText(cardBeanSelected.);
		tvName.setText(cardBeanSelected.name);
		tvRelation.setText(cardBeanSelected.relation);
		tvTypeOfCard.setText(cardBeanSelected.card_type);
		if(!android.text.TextUtils.isEmpty(cardBeanSelected.additional_card_type)){
			tvTypeOfCard.append(" - "+cardBeanSelected.additional_card_type);
		}

		String formatedDate = cardBeanSelected.date;//2021-04-12 09:17:46
		/*try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}*/

		tvDate.setText(formatedDate);


		//Fresco
		options = new StylingOptions();
		//List<Image> images = productSelected.getImages();
		posters = new String[2];
		descriptions = new String[2];
		posters[0] = cardBeanSelected.front_pic;
		descriptions[0] = "Front Image";//cardBeanSelected.front_pic;
		posters[1] = cardBeanSelected.back_pic;
		descriptions[1] = "Back Image";//cardBeanSelected.back_pic;
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




	//---------------------Fresco Image Viewer starts--------------------------------------------------
	protected String[] posters, descriptions;

	private ImageOverlayView overlayView;
	private StylingOptions options;
	protected void showPicker(int startPosition) {
		ImageViewer.Builder builder = new ImageViewer.Builder<>(this, posters)
				.setStartPosition(startPosition)
				.setOnDismissListener(getDismissListener());

		builder.hideStatusBar(options.get(StylingOptions.Property.HIDE_STATUS_BAR));

		if (options.get(StylingOptions.Property.IMAGE_MARGIN)) {
			builder.setImageMargin(this, R.dimen.image_margin);
		}

		if (options.get(StylingOptions.Property.CONTAINER_PADDING)) {
			builder.setContainerPadding(this, R.dimen.image_margin);
		}

		if (options.get(StylingOptions.Property.IMAGES_ROUNDING)) {
			builder.setCustomDraweeHierarchyBuilder(getRoundedHierarchyBuilder());
		}

		builder.allowSwipeToDismiss(options.get(StylingOptions.Property.SWIPE_TO_DISMISS));

		builder.allowZooming(options.get(StylingOptions.Property.ZOOMING));

		if (options.get(StylingOptions.Property.SHOW_OVERLAY)) {
			overlayView = new ImageOverlayView(this);
			builder.setOverlayView(overlayView);
			builder.setImageChangeListener(getImageChangeListener());
		}

		if (options.get(StylingOptions.Property.RANDOM_BACKGROUND)) {
			builder.setBackgroundColor(getRandomColor());
		}

		if (options.get(StylingOptions.Property.POST_PROCESSING)) {
			builder.setCustomImageRequestBuilder(
					ImageViewer.createImageRequestBuilder()
							.setPostprocessor(new GrayscalePostprocessor()));
		}

		builder.show();
	}

	private ImageViewer.OnImageChangeListener getImageChangeListener() {
		return new ImageViewer.OnImageChangeListener() {
			@Override
			public void onImageChange(int position) {
				String url = posters[position];
				overlayView.setShareText(url);
				overlayView.setDescription(descriptions[position]);
			}
		};
	}

	private ImageViewer.OnDismissListener getDismissListener() {
		return new ImageViewer.OnDismissListener() {
			@Override
			public void onDismiss() {
				//AppUtils.showInfoSnackbar(findViewById(R.id.coordinator), R.string.message_on_dismiss, false);
			}
		};
	}

	private GenericDraweeHierarchyBuilder getRoundedHierarchyBuilder() {
		RoundingParams roundingParams = new RoundingParams();
		roundingParams.setRoundAsCircle(true);

		return GenericDraweeHierarchyBuilder.newInstance(getResources())
				.setRoundingParams(roundingParams);
	}

	private int getRandomColor() {
		Random random = new Random();
		return Color.argb(255, random.nextInt(156), random.nextInt(156), random.nextInt(156));
	}

	//---------------------Fresco Image Viewer ends--------------------------------------------------


	@Override
	public void fetchDataCallback(String httpStatus, String apiName, String content) {
		super.fetchDataCallback(httpStatus, apiName, content);
		if(apiName.equalsIgnoreCase(cardDetailsAPI)){
			swiperefresh.setRefreshing(false);
			try {
				JSONObject jsonObject = new JSONObject(content);
				cardBeanSelected = gson.fromJson(jsonObject.getJSONObject("data").toString(), CardBean.class);
				showCard();
			} catch (JSONException e) {
				e.printStackTrace();
				customToast.showToast(DATA.JSON_ERROR_MSG,0,0);
			}
		}
	}


	Dialog dialogAskPrint;
	public void showAskPrintDialog() {
		if(dialogAskPrint != null){
			dialogAskPrint.dismiss();
		}
		dialogAskPrint = new Dialog(activity);
		dialogAskPrint.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogAskPrint.setContentView(R.layout.dialog_ask_print);
		dialogAskPrint.setCanceledOnTouchOutside(false);

		Button btnPrintPDF = dialogAskPrint.findViewById(R.id.btnPrintPDF);
		Button btnPrintImg = dialogAskPrint.findViewById(R.id.btnPrintImg);
		Button btnCancel = dialogAskPrint.findViewById(R.id.btnCancel);


		btnPrintPDF.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogAskPrint.dismiss();

				PrintHelper printHelper = new PrintHelper(activity);
				printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
				Bitmap bitmap = getScreenShotFromView(findViewById(R.id.cardViewDetail));//((BitmapDrawable) mImageView.getDrawable()).getBitmap();
				printHelper.printBitmap(getResources().getString(R.string.app_name)+" print out", bitmap);
			}
		});
		btnPrintImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogAskPrint.dismiss();

				ShareScreenshot shareScreenshot = new ShareScreenshot(activity);
				Bitmap bitmap = shareScreenshot.getScreenShot(findViewById(R.id.cardViewDetail));
				String path = shareScreenshot.store(bitmap);
				if(path != null){
					shareScreenshot.shareImage(new File(path));
				}
			}
		});

		btnCancel.setOnClickListener(v -> {
			dialogAskPrint.dismiss();
		});

		dialogAskPrint.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		dialogAskPrint.show();

        /*WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogAskPrint.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;//+500
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        //lp.gravity = Gravity.BOTTOM;
        //lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;

        //askDialog.setCanceledOnTouchOutside(false);
        dialogAskPrint.show();
        dialogAskPrint.getWindow().setAttributes(lp);*/
	}
}
