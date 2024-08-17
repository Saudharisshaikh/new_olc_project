package com.app.mdlive_cp.reliance.pt_reports;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_cp.BaseActivity;
import com.app.mdlive_cp.R;
import com.app.mdlive_cp.api.Dialog_CustomProgress;
import com.app.mdlive_cp.util.DATA;

import java.io.File;

public class ViewReportImage extends BaseActivity {

	Activity activity;
	
	ImageView imgViewReport;
	TextView btnSaveReportImage;
	WebView wvReport;

	//Bitmap bmp;
	//Drawable d;
	//AsyncHttpClient client;
	//ProgressDialog pd;
	Dialog_CustomProgress dialog_customProgress;
	String extention = "";
	
	@Override
	protected void onResume() {
		super.onResume();
		
		/*if(extention.equals(".png" ) || extention.equals(".jpg" ) || extention.equals(".jpeg" )) {
			DATA.loadImageFromURL(DATA.selctdReprtURLForVu, R.drawable.ic_placeholder_2, imgViewReport);
		} else if(extention.equals(".pdf")) {
			imgViewReport.setImageResource(R.drawable.icon_pdf);
		} else if(extention.equals(".docx") || extention.equals(".doc")) {
			imgViewReport.setImageResource(R.drawable.icon_doc);
		} else if(extention.equals(".xls") || extention.equals(".xlsx")) {
			imgViewReport.setImageResource(R.drawable.icon_exel);
		}*/

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_report_image);
		
		activity = ViewReportImage.this;

		dialog_customProgress = new Dialog_CustomProgress(activity);

		//pd = new ProgressDialog(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
		//pd.setMessage("Downloading your report...");
		//pd.setCanceledOnTouchOutside(false);

		imgViewReport = (ImageView) findViewById(R.id.imgViewReport);		
		btnSaveReportImage = (TextView) findViewById(R.id.btnSaveReportImage);
		wvReport= (WebView) findViewById(R.id.wvReport);
		wvReport.getSettings().setJavaScriptEnabled(true);
		//wvReport.getSettings().setPluginState(WebSettings.PluginState.ON);
		//wvReport.getSettings().setLoadWithOverviewMode(true);
		//wvReport.getSettings().setUseWideViewPort(true);
		//wvReport.setWebViewClient(new Callback());

		extention = DATA.selctdReprtURLForVu.substring(DATA.selctdReprtURLForVu.lastIndexOf("."));
		System.out.println("-- extention: "+extention+" url: "+DATA.selctdReprtURLForVu);
		if(extention.equals(".png" ) || extention.equals(".jpg" ) || extention.equals(".jpeg" )) {

			wvReport.setVisibility(View.GONE);
			imgViewReport.setVisibility(View.VISIBLE);
			//UrlImageViewHelper.setUrlDrawable(imgViewReport, DATA.selctdReprtURLForVu, R.drawable.icon_loading);
			//Picasso.with(activity).load(DATA.selctdReprtURLForVu).placeholder(R.drawable.icon_loading).into(imgViewReport);
			DATA.loadImageFromURL(DATA.selctdReprtURLForVu, R.drawable.ic_placeholder_2, imgViewReport);

		} else if(extention.equals(".pdf")  || extention.equals(".docx") || extention.equals(".doc")
				|| extention.equals(".xls") || extention.equals(".xlsx")  || extention.equals(".pptx") || extention.equals(".ppt")
				||extention.equals(".txt")) {
			
			imgViewReport.setImageResource(R.drawable.ic_pickfile_pdf);
			wvReport.setVisibility(View.VISIBLE);
			imgViewReport.setVisibility(View.GONE);

			//pd.show();
			dialog_customProgress.showProgressDialog();

			wvReport.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return super.shouldOverrideUrlLoading(view, url);
				}
				@Override
				public void onPageFinished(WebView view, String url) {

					dialog_customProgress.dismissProgressDialog();

					super.onPageFinished(view, url);
				}
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);

				}

				@Override
				public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
					super.onReceivedError(view, request, error);
					System.out.println("-- web view onReceivedError Request : "+request.toString()+ " . Error : "+error.toString());
				}

				@Override
				public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
					super.onReceivedHttpError(view, request, errorResponse);

					System.out.println("-- web view onReceivedHttpError Request : "+request.toString()+ " . Error : "+errorResponse.toString());
				}
			});

			//DATA.selctdReprtURLForVu = DATA.selctdReprtURLForVu.replace("onlinecare_newdesign","odev");//Must remove

			wvReport.loadUrl("https://docs.google.com/gview?embedded=true&url="+DATA.selctdReprtURLForVu);
			System.out.println("-- in extention.equals(\".pdf\"): "+extention.equals(".pdf"));
			System.out.println("-- pdf URL: "+"https://docs.google.com/gview?embedded=true&url="+DATA.selctdReprtURLForVu);
		}
		/*else if(extention.equals(".docx") || extention.equals(".doc")) {
			imgViewReport.setImageResource(R.drawable.icon_doc);
		} else if(extention.equals(".xls") || extention.equals(".xlsx")) {
			imgViewReport.setImageResource(R.drawable.icon_exel);
		}*/


		btnSaveReportImage.setVisibility(View.GONE);
		/*btnSaveReportImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

//				DownloadImage(DATA.selctdReprtURLForVu, extention);
				
				Download downloadFile = new Download(activity, DATA.selctdReprtURLForVu, DATA.selctdReprtName);
				downloadFile.execute("");
				
			}
		});*/
		
		/*pd.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				client.cancelRequests(activity, true);
			}
		});*/
		
	}
	
//	private void DownloadImage(String img_link, final String ext) {
//
//
//		pd.show();
//
//		client = new AsyncHttpClient();
//		String[] allowedContentTypes = new String[] { "image/png", "image/jpeg", "image/jpg", "application/pdf" };
//		client.get(img_link, new BinaryHttpResponseHandler(allowedContentTypes) {
//
//			@Override
//			@Deprecated
//			public void onFailure(int statusCode, Throwable error,
//					String content) {
//				super.onFailure(statusCode, error, content);
//
//				CustomToast c = new CustomToast(activity);
//				c.showToast("Something went wrong, please try again. "+content, 0, 0);
//
//
//				pd.dismiss();
//			}
//
//			@Override
//			public void onSuccess(byte[] fileData) {
//
//
//
//				BitmapFactory.Options options=new BitmapFactory.Options();// Create object of bitmapfactory's option method for further option use
//				
//				options.inPurgeable = true; // inPurgeable is used to free up memory while required
//				
//				bmp = BitmapFactory.decodeByteArray(fileData, 0, fileData.length, options);
//
//				File mydir = new File(Environment.getExternalStorageDirectory() + "/Online care reports/");
//
//				if(!(mydir.exists())) {
//
//					mydir.mkdirs();
//				}
//
//				OutputStream fOut = null;
//				File file = new File(Environment.getExternalStorageDirectory().toString(), "/Online care reports/"+SystemClock.currentThreadTimeMillis()+"_image"+ext);
//
//				try {
//					fOut = new FileOutputStream(file);
//
//					bmp.compress(Bitmap.CompressFormat.PNG, 90, fOut);
//					fOut.flush();
//					fOut.close();
//
//					sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
//
//
//				} catch (FileNotFoundException e) {
//
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				
//				MediaScannerConnection.scanFile(activity,new String[] { file.toString() }, null,
//				          new MediaScannerConnection.OnScanCompletedListener() {
//				      public void onScanCompleted(String path, Uri uri) {
//
//				    	  Log.i("ExternalStorage", "Scanned " + path + ":");
//				          Log.i("ExternalStorage", "-> uri=" + uri);
//				      }
//				 });
//				pd.dismiss();
//				
//				CustomToast c = new CustomToast(activity);
//				c.showToast("Report saved on sdcard.", 0, 0);
//
//
//			}
//		});
//
//	}

	
	public void setResults(String filename) {
		
		String extention = filename.substring(filename.lastIndexOf("."));
		
		if(extention.equals(".png" ) || extention.equals(".jpg" ) || extention.equals(".jpeg" )) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse(filename), "image/*");
			startActivity(intent);

		} else {
			File targetFile = new File(filename);
            Uri targetUri = Uri.fromFile(targetFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(targetUri, "application/*");
            startActivity(intent);
		}

	}


	/*class Callback extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			return(false);
		}
	}*/

}
