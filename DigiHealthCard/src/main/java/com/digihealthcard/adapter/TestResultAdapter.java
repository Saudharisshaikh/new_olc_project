package com.digihealthcard.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.digihealthcard.R;
import com.digihealthcard.ActivityTestResultsList;
import com.digihealthcard.model.TestResultBean;
import com.digihealthcard.util.DATA;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class TestResultAdapter extends ArrayAdapter<TestResultBean> {// implements Html.ImageGetter

	Activity activity;
	ArrayList<TestResultBean> testResultBeans;


	public TestResultAdapter(Activity activity , ArrayList<TestResultBean> testResultBeans) {
		super(activity, R.layout.lv_test_result_row, testResultBeans);

		this.activity = activity;
		this.testResultBeans = testResultBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);

		//textViewsList.clear();
	}


	static class ViewHolder {
		TextView tvName,tvRelationShip,tvResult,tvDate,tvReportBody, tvOpenEmail;
		ImageView ivDeleteCard, ivVCfrontImg;
		LinearLayout layEmailReport,layUserAddedReport,layReport;
		//WebView wvReport;
		//NestedScrollView nsvWebView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_test_result_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvName = convertView.findViewById(R.id.tvName);
			viewHolder.tvRelationShip = convertView.findViewById(R.id.tvRelationShip);
			viewHolder.ivDeleteCard = convertView.findViewById(R.id.ivDeleteCard);
			viewHolder.ivVCfrontImg = convertView.findViewById(R.id.ivVCfrontImg);
			viewHolder.tvResult = convertView.findViewById(R.id.tvResult);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvReportBody = convertView.findViewById(R.id.tvReportBody);
			viewHolder.layEmailReport = convertView.findViewById(R.id.layEmailReport);
			viewHolder.layUserAddedReport = convertView.findViewById(R.id.layUserAddedReport);
			viewHolder.layReport = convertView.findViewById(R.id.layReport);
			//viewHolder.wvReport = convertView.findViewById(R.id.wvReport);
			//viewHolder.nsvWebView = convertView.findViewById(R.id.nsvWebView);
			viewHolder.tvOpenEmail = convertView.findViewById(R.id.tvOpenEmail);

			//textViewsList.add(viewHolder.tvReportBody);

			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvName.setText(testResultBeans.get(position).name);
		viewHolder.tvRelationShip.setText(testResultBeans.get(position).relation);
		viewHolder.tvResult.setText(testResultBeans.get(position).result);

		String formatedDate = testResultBeans.get(position).dateof;//2021-04-12 09:17:46
		/*try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}*/

		viewHolder.tvDate.setText(formatedDate);

		DATA.loadImageFromURL(testResultBeans.get(position).frontpic, R.drawable.ic_placeholder_2, viewHolder.ivVCfrontImg);
		//DATA.loadImageFromURL(cardBeans.get(position).back_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCBackImg);

		viewHolder.ivDeleteCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
								.setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this test result ?")
								.setPositiveButton("Yes Delete", (dialogInterface, i) ->
										((ActivityTestResultsList)activity).deleteCard(position))
								.setNegativeButton("Not Now",null)
								.create();
				alertDialog.show();
			}
		});


		if(testResultBeans.get(position).result_type.equalsIgnoreCase("email")){
			viewHolder.layUserAddedReport.setVisibility(View.GONE);
			viewHolder.layEmailReport.setVisibility(View.VISIBLE);
			viewHolder.tvReportBody.setText(Html.fromHtml(testResultBeans.get(position).bodyhtml));
			/*Spanned spanned = Html.fromHtml(testResultBeans.get(position).bodyhtml, this, null);
			viewHolder.tvReportBody.setText(spanned,  TextView.BufferType.SPANNABLE);
			//viewHolder.tvReportBody.setAutoLinkMask(Linkify.ALL);//Linkify.WEB_URLS
			viewHolder.tvReportBody.setMovementMethod(LinkMovementMethod.getInstance());*/

			/*viewHolder.nsvWebView.setVisibility(View.VISIBLE);
			viewHolder.layReport.setVisibility(View.GONE);

			viewHolder.wvReport.loadData(testResultBeans.get(position).bodyhtml, "text/html", "UTF-8");*/

		}else {
			viewHolder.layUserAddedReport.setVisibility(View.VISIBLE);
			viewHolder.layEmailReport.setVisibility(View.GONE);

			/*viewHolder.nsvWebView.setVisibility(View.GONE);
			viewHolder.layReport.setVisibility(View.VISIBLE);*/
		}

		viewHolder.tvOpenEmail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(testResultBeans.get(position).result_type.equalsIgnoreCase("email")){
					//showWebviewWithHTMLDialog(testResultBeans.get(position).bodyhtml, "Report Preview");
					String filePath = writeFileOnInternalStorage(activity, testResultBeans.get(position).bodyhtml);
					if(filePath != null){
						File htmlFile = new File(filePath);
						System.out.println("-- file exist "+htmlFile.exists()+ " file size: "+htmlFile.length());
						Uri fileURI =  FileProvider.getUriForFile(activity.getApplicationContext(), activity.getPackageName()+".fileprovider", htmlFile);
						//Uri fileURI = Uri.fromFile(new File(filePath));
						System.out.println("-- file Path: "+filePath+" |||| File URI : "+fileURI);
						Intent browserIntent = new Intent(Intent.ACTION_VIEW);
						browserIntent.setDataAndType(fileURI, "text/html");
						//browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
						browserIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
						activity.startActivity(browserIntent);
					}
				}
			}
		});


		return convertView;
	}



	private String writeFileOnInternalStorage(Context context, String fileBody){
		System.out.println("-- showWebviewDialog htmlString : "+fileBody);
		String filePath = null;
		//File dir = new File(context.getFilesDir(), "tempHTML");
		File dir = new File(context.getCacheDir(), "tempHTML");
		if(!dir.exists()){
			dir.mkdir();
		}
		try {
			File gpxfile = new File(dir, "testReport.html");
			FileWriter writer = new FileWriter(gpxfile);
			writer.append(fileBody);
			writer.flush();
			writer.close();
			filePath = gpxfile.getAbsolutePath();
		} catch (Exception e){
			e.printStackTrace();
		}
		return filePath;
	}


	public void showWebviewWithHTMLDialog(final String htmlString, String dialogTittle){
		System.out.println("-- showWebviewDialog htmlString : "+htmlString);
		final Dialog dialog = new Dialog(activity);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.lay_webview_html);
		//dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
		dialog.getWindow().setBackgroundDrawableResource(R.drawable.cust_border_white_outline);

		ProgressBar pbWebview = dialog.findViewById(R.id.pbWebview);
		pbWebview.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.app_blue_color), android.graphics.PorterDuff.Mode.MULTIPLY);

		WebView webviewBill = (WebView) dialog.findViewById(R.id.webviewBill);
		//webviewBill.getSettings().setJavaScriptEnabled(true);
		//webviewBill.getSettings().setPluginState(WebSettings.PluginState.ON);
		//webviewBill.getSettings().setLoadWithOverviewMode(true);
		//webviewBill.getSettings().setUseWideViewPort(true);
		//webviewBill.setWebViewClient(new Callback());
		//webviewBill.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		//webviewBill.setHorizontalScrollBarEnabled(false);
		//webviewBill.getSettings().setLoadWithOverviewMode(true);
		//webviewBill.setWebChromeClient(new WebChromeClient());

		/*webviewBill.setInitialScale(100);
		webviewBill.getSettings().setUseWideViewPort(true);
		webviewBill.getSettings().setLoadWithOverviewMode(true);*/
		//webviewBill.getSettings().setBuiltInZoomControls(true);

		TextView tvDialogTittle = (TextView) dialog.findViewById(R.id.dialogTittle);
		tvDialogTittle.setText(dialogTittle);

		/*webviewBill.setWebViewClient(new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				//return super.shouldOverrideUrlLoading(view, request);
				return false;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				// do your stuff here
				//DATA.dismissLoaderDefault();
				pbWebview.setVisibility(View.GONE);
			}
		});*/
		//webviewBill.loadUrl(webURL);

		//webviewBill.loadData(htmlString, "text/html", "UTF-8");

		webviewBill.getSettings().setJavaScriptEnabled(true);
		webviewBill.loadData(htmlString, "text/html; charset=utf-8", "UTF-8");

		ImageView ivClose = (ImageView) dialog.findViewById(R.id.ivClose);
		ivClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		/*if(!(activity instanceof Signup)){
			dialog.findViewById(R.id.btnWebviewCancel).setVisibility(View.GONE);
			((Button)dialog.findViewById(R.id.btnWebviewDone)).setText("Print");
		}




		//create object of print manager in your device
		PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
		//create object of print adapter
		PrintDocumentAdapter printAdapter = webviewBill.createPrintDocumentAdapter();
		//provide name to your newly generated pdf file
		String jobName = activity.getResources().getString(R.string.app_name) + " Print Document";

		dialog.findViewById(R.id.btnWebviewDone).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dialog.dismiss();

				try {
					//open print dialog
					printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
				}catch (Exception e){e.printStackTrace();}
			}
		});
		dialog.findViewById(R.id.btnWebviewCancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();

				if(activity instanceof Signup){
					if(webURL.equalsIgnoreCase(DATA.PRIVACY_POLICY_URL)){
						((Signup)activity).cbPrivacy.setChecked(false);
					}else if(webURL.equalsIgnoreCase(DATA.USER_AGREEMENT_URL)){
						((Signup)activity).cbUserAgreement.setChecked(false);
					}
				}
			}
		});*/

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(dialog.getWindow().getAttributes());
		lp.width = WindowManager.LayoutParams.MATCH_PARENT;
		lp.height = WindowManager.LayoutParams.MATCH_PARENT;
		dialog.show();
		dialog.getWindow().setAttributes(lp);
		//DATA.showLoaderDefault(activity,"");
	}



	/*ArrayList<TextView> textViewsList = new ArrayList<>();
	@Override
	public Drawable getDrawable(String source) {
		LevelListDrawable d = new LevelListDrawable();
		Drawable empty = activity.getResources().getDrawable(R.drawable.ic_launcher);
		d.addLevel(0, 0, empty);
		d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

		new LoadImage(textViewsList).execute(source, d);

		return d;
	}*/



}

/*class LoadImage extends AsyncTask<Object, Void, Bitmap> {

	private LevelListDrawable mDrawable;
	//TextView mTv;

	ArrayList<TextView> textViews;
	public LoadImage(ArrayList<TextView> textViews) {
		this.textViews = textViews;
	}

	@Override
	protected Bitmap doInBackground(Object... params) {
		String source = (String) params[0];
		mDrawable = (LevelListDrawable) params[1];
		//Log.d(TAG, "doInBackground " + source);
		try {
			InputStream is = new URL(source).openStream();
			return BitmapFactory.decodeStream(is);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		Log.d("", "onPostExecute drawable " + mDrawable);
		Log.d("", "onPostExecute bitmap " + bitmap);
		if (bitmap != null) {
			BitmapDrawable d = new BitmapDrawable(bitmap);
			mDrawable.addLevel(1, 1, d);
			mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
			mDrawable.setLevel(1);
			// i don't know yet a better way to refresh TextView
			// mTv.invalidate() doesn't work as expected
			*//*CharSequence t = mTv.getText();
			mTv.setText(t);*//*

			for (int i = 0; i < textViews.size(); i++) {
				CharSequence charSequence = textViews.get(i).getText();
				textViews.get(i).setText(charSequence);
			}
		}
	}
}*/
