package com.app.emcurauc.devices;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.emcurauc.R;
import com.app.emcurauc.devices.beanclasses.BpHeartRateValuesBean;
import com.app.emcurauc.devices.fragments.WithingsBpDataFragment;
import com.app.emcurauc.devices.fragments.WithingsHRateDataFragment;
import com.app.emcurauc.devices.interfaces.GetWithingsBpDataInterface;

import static com.app.emcurauc.devices.MyDevices.endTime;
import static com.app.emcurauc.devices.MyDevices.showWithingsDateDialog;
import static com.app.emcurauc.devices.MyDevices.startTime;

public class WithingsBpDataActivity1 extends AppCompatActivity implements GetWithingsBpDataInterface {
	
	AppCompatActivity activity;
	LinearLayout layBP,layHeartBeat,mainCont;
	TextView tvBp,tvHb;
	
	public static ArrayList<BpHeartRateValuesBean> withingsBpDataList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_withings_bp_data1);
		
		activity = WithingsBpDataActivity1.this;
		
		layBP = (LinearLayout) findViewById(R.id.layBP);
		layHeartBeat = (LinearLayout) findViewById(R.id.layHeartBeat);
		mainCont = (LinearLayout) findViewById(R.id.mainCont);
		
		tvBp = (TextView) findViewById(R.id.tvBp);
		tvHb = (TextView) findViewById(R.id.tvHb);
		
		
		Fragment f = new WithingsBpDataFragment();
		openFragment(f);
		
		layBP.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tvBp.setTextColor(Color.parseColor("#FFFFFF"));
				tvHb.setTextColor(Color.parseColor("#66FFFFFF"));
				Fragment f = new WithingsBpDataFragment();
				openFragment(f);
			}
		});
		layHeartBeat.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				tvBp.setTextColor(Color.parseColor("#66FFFFFF"));
				tvHb.setTextColor(Color.parseColor("#FFFFFF"));
				Fragment f = new WithingsHRateDataFragment();
				openFragment(f);
			}
		});
		
	}

	
	
	public void openFragment(Fragment fragment) {
		
		FragmentManager fragmentManager = activity.getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.mainCont, fragment).commit();
	}
	
	
	@Override
    public ArrayList<BpHeartRateValuesBean> getDataList() {

        return withingsBpDataList;
    }
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.item_filter_date) {

            showWithingsDateDialog = true;
            startTime = 0;
            endTime = 0;

            finish();
//            Intent intent = new Intent(WithingsBpDataActivity.this,MyDevices.class);
//            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filter_date, menu);

        return true;
    }
}
