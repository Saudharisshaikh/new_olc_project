package com.app.msu_cp;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.app.msu_cp.util.DATA;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewReportFull extends AppCompatActivity {

	PhotoView imgViewReportFull;
	ProgressBar pb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_report_full);
		getSupportActionBar().setTitle("Patient Medical Report");
		pb = (ProgressBar) findViewById(R.id.progressBar);
		pb.setVisibility(View.VISIBLE);
		//ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(ViewReportFull.this));
		imgViewReportFull = (PhotoView) findViewById(R.id.imgViewReportFull);


		WebView wvReport= (WebView) findViewById(R.id.wvReport);
		wvReport.getSettings().setJavaScriptEnabled(true);

		if (DATA.selectedPtReportUrl.endsWith("pdf") || DATA.selectedPtReportUrl.endsWith("doc") || DATA.selectedPtReportUrl.endsWith("docx")) {
			wvReport.setVisibility(View.VISIBLE);
			imgViewReportFull.setVisibility(View.GONE);

			wvReport.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return super.shouldOverrideUrlLoading(view, url);
				}
				@Override
				public void onPageFinished(WebView view, String url) {
					pb.setVisibility(View.INVISIBLE);
					super.onPageFinished(view, url);
				}
				@Override
				public void onPageStarted(WebView view, String url, Bitmap favicon) {
					super.onPageStarted(view, url, favicon);

				}
			});
			wvReport.loadUrl("https://docs.google.com/gview?embedded=true&url="+DATA.selectedPtReportUrl);
		}else{
			wvReport.setVisibility(View.GONE);
			imgViewReportFull.setVisibility(View.VISIBLE);
			//Toast.makeText(ViewReportFull.this, DATA.selectedPtReportUrl, Toast.LENGTH_SHORT).show();
			Log.i("image--", DATA.selectedPtReportUrl);
			///UrlImageViewHelper.setUrlDrawable(imgViewReportFull, DATA.selectedPtReportUrl, R.drawable.icon_loading);
			/*ImageLoader imageLoader = ImageLoader.getInstance();
			//imageLoader.displayImage(DATA.selectedPtReportUrl, imgViewReportFull);
			imageLoader.loadImage(DATA.selectedPtReportUrl, new SimpleImageLoadingListener() {
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					// Do whatever you want with Bitmap
					imgViewReportFull.setImageBitmap(loadedImage);
					pb.setVisibility(View.INVISIBLE);
				}
			});*/
			pb.setVisibility(View.INVISIBLE);
			DATA.loadImageFromURL(DATA.selectedPtReportUrl, R.drawable.ic_placeholder_2, imgViewReportFull);
		}

	}
}