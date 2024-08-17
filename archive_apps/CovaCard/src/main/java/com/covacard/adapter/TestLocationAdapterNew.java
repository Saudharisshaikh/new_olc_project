package com.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.covacard.R;
import com.covacard.model.TestLocationBeanNew;
import com.covacard.util.DATA;

import java.util.ArrayList;
import java.util.Locale;

public class TestLocationAdapterNew extends ArrayAdapter<TestLocationBeanNew> {

	Activity activity;
	ArrayList<TestLocationBeanNew> testLocationBeanNews;
	public ArrayList<TestLocationBeanNew> testLocationBeanNewsOrig;
	SharedPreferences prefs;


	public TestLocationAdapterNew(Activity activity , ArrayList<TestLocationBeanNew> testLocationBeanNews) {
		super(activity, R.layout.lv_test_location_row_new, testLocationBeanNews);

		this.activity = activity;
		this.testLocationBeanNews = testLocationBeanNews;
		testLocationBeanNewsOrig = new ArrayList<>();
		testLocationBeanNewsOrig.addAll(testLocationBeanNews);
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		TextView tvLocName,tvPhoneName,tvAddress,tvCallNow,tvWeb,tvGetDirections;
		LinearLayout layPhoneCont,layBtnsCont;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_test_location_row_new, null);

			viewHolder = new ViewHolder();

			viewHolder.tvLocName = convertView.findViewById(R.id.tvLocName);
			viewHolder.tvPhoneName = convertView.findViewById(R.id.tvPhoneName);
			viewHolder.tvAddress = convertView.findViewById(R.id.tvAddress);
			viewHolder.tvCallNow = convertView.findViewById(R.id.tvCallNow);
			viewHolder.tvWeb = convertView.findViewById(R.id.tvWeb);
			viewHolder.tvGetDirections = convertView.findViewById(R.id.tvGetDirections);
			viewHolder.layPhoneCont  = convertView.findViewById(R.id.layPhoneCont);
			viewHolder.layBtnsCont = convertView.findViewById(R.id.layBtnsCont);


			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvLocName.setText(testLocationBeanNews.get(position).name);
		//viewHolder.tvPhoneName.setText(testCenterBeans.get(position).CtrPhoneNum);
		viewHolder.tvAddress.setText(testLocationBeanNews.get(position).formatted_address);

		/*if(activity instanceof ActivityTestLocations){
			viewHolder.layPhoneCont.setVisibility(((ActivityTestLocations) activity).isKeybordOpen ? View.GONE : View.VISIBLE);
			viewHolder.layBtnsCont.setVisibility(((ActivityTestLocations) activity).isKeybordOpen ? View.GONE : View.VISIBLE);
		}


		View.OnClickListener onClickListener = new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(v.getId() == R.id.tvCallNow){
					try {
						Intent callIntent = new Intent(Intent.ACTION_CALL);
						callIntent.setData(Uri.parse("tel:"+Uri.encode(testCenterBeans.get(position).CtrPhoneNum)));
						callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						activity.startActivity(callIntent);
					}catch (Exception  e){e.printStackTrace();}
				}else if(v.getId() == R.id.tvWeb){
					try {
						System.out.println("-- Site URL: "+testCenterBeans.get(position).SiteUrl);
						Intent i = new Intent(Intent.ACTION_VIEW);
						String site = testCenterBeans.get(position).SiteUrl;
						if(!site.startsWith("http")){
							site = "http://"+site;
						}
						i.setData(Uri.parse(site));
						activity.startActivity(i);
					}catch (Exception e){
						e.printStackTrace();
					}
				}else if(v.getId() == R.id.tvGetDirections){

					//"http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345"

					double userlat = 44.1251998340001, userlong = -84.1966975;
					try {
						userlat = Double.parseDouble(prefs.getString("userLatitude", "0.0"));
						userlong = Double.parseDouble(prefs.getString("userLongitude", "0.0"));
					}catch (Exception e){
						e.printStackTrace();
					}

					StringBuilder sb = new StringBuilder();
					sb.append("http://maps.google.com/maps?saddr").append(userlat).append(",").append(userlat).append("&daddr=")
							.append(testCenterBeans.get(position).Latitude).append(",").append(testCenterBeans.get(position).Longitude);
					String reqURL = sb.toString();
					System.out.println("-- Direction req: "+reqURL);
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(reqURL));
					activity.startActivity(intent);
				}
			}
		};

		viewHolder.tvCallNow.setOnClickListener(onClickListener);
		viewHolder.tvWeb.setOnClickListener(onClickListener);
		viewHolder.tvGetDirections.setOnClickListener(onClickListener);

		viewHolder.tvWeb.setEnabled(!TextUtils.isEmpty(testCenterBeans.get(position).SiteUrl));*/


		return convertView;
	}



	public void filter(String filterText) {
		testLocationBeanNews.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		System.out.println("-- testLocationBeanNewsOrig size: "+testLocationBeanNewsOrig.size());
		if(filterText.length() == 0) {
			testLocationBeanNews.addAll(testLocationBeanNewsOrig);
		} else {
			for(TestLocationBeanNew temp : testLocationBeanNewsOrig) {
				if(temp.name.toLowerCase(Locale.getDefault()).contains(filterText) ||
						temp.formatted_address.toLowerCase(Locale.getDefault()).contains(filterText)) {
					testLocationBeanNews.add(temp);
				}
			}
		}
		notifyDataSetChanged();
	}//end filter

}