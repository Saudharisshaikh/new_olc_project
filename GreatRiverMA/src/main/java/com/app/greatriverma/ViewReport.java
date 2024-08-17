package com.app.greatriverma;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.app.greatriverma.adapter.ViewReportsAdapter;
import com.app.greatriverma.util.DATA;
import com.app.greatriverma.util.OpenActivity;

public class ViewReport extends AppCompatActivity {
	
	Activity activity;
	
	ListView lvViewReports;
	ViewReportsAdapter viewReportsAdapter;
	TextView tvNoReports;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_reports);
		
		activity = ViewReport.this;
		
		lvViewReports = (ListView) findViewById(R.id.lvViewReports);
		tvNoReports = (TextView) findViewById(R.id.tvNoReports);

		viewReportsAdapter = new ViewReportsAdapter(activity);
		lvViewReports.setAdapter(viewReportsAdapter);
		
		lvViewReports.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				
				DATA.selectedPtReportUrl = DATA.allReportsFiltered.get(position).url;
				
				OpenActivity op = new OpenActivity(activity);
				op.open(ViewReportFull.class, false);
			}
		});
		
		
		if (DATA.allReportsFiltered.isEmpty()) {
			tvNoReports.setVisibility(View.VISIBLE);
			lvViewReports.setVisibility(View.GONE);
		}else {
			tvNoReports.setVisibility(View.GONE);
			lvViewReports.setVisibility(View.VISIBLE);
		}
		
	}

}
