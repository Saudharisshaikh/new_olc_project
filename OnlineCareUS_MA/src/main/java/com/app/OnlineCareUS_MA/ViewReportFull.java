package com.app.OnlineCareUS_MA;

import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.app.OnlineCareUS_MA.util.DATA;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewReportFull extends AppCompatActivity {
	
	PhotoView imgViewReportFull;
	WebView wvReport;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.view_report_full);
		
		imgViewReportFull = (PhotoView) findViewById(R.id.imgViewReportFull);
		
		//UrlImageViewHelper.setUrlDrawable(imgViewReportFull, DATA.selectedPtReportUrl, R.drawable.icon_loading);


		wvReport= (WebView) findViewById(R.id.wvReport);
		wvReport.getSettings().setJavaScriptEnabled(true);

		if (DATA.selectedPtReportUrl.endsWith("pdf") || DATA.selectedPtReportUrl.endsWith("doc") || DATA.selectedPtReportUrl.endsWith("docx")) {
			wvReport.setVisibility(View.VISIBLE);
			imgViewReportFull.setVisibility(View.GONE);
			DATA.showLoaderDefault(ViewReportFull.this,"");
			wvReport.setWebViewClient(new WebViewClient(){
				@Override
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return super.shouldOverrideUrlLoading(view, url);
				}
				@Override
				public void onPageFinished(WebView view, String url) {
					DATA.dismissLoaderDefault();
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

			DATA.loadImageFromURL(DATA.selectedPtReportUrl,R.drawable.ic_placeholder_2,imgViewReportFull);
		}
		
	}

}
