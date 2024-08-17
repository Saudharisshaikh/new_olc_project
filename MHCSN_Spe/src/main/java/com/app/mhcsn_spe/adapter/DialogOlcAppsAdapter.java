package com.app.mhcsn_spe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_spe.R;
import com.app.mhcsn_spe.model.AppBean;

import java.util.ArrayList;
import java.util.Locale;

public class DialogOlcAppsAdapter extends ArrayAdapter<AppBean> {
	Context activity;
	ArrayList<AppBean> appBeans;
	ArrayList<AppBean> appBeansOriginal;

	public DialogOlcAppsAdapter(Context activity, ArrayList<AppBean> appBeans) {
		super(activity, R.layout.lv_dial_olcapps_row);

		this.activity = activity;
		this.appBeans = appBeans;

		this.appBeansOriginal = new ArrayList<>();
		this.appBeansOriginal.addAll(appBeans);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appBeans.size();
	}

	@Override
	public AppBean getItem(int pos) {
		// TODO Auto-generated method stub
		return appBeans.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	static class ViewHolder {
		TextView tvAppName;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dial_olcapps_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvAppName = convertView.findViewById(R.id.tvAppName);
			//viewHolder.tvCountryCode = convertView.findViewById(R.id.tvCountryCode);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvAppName, viewHolder.tvAppName);
			//convertView.setTag(R.id.tvCountryCode, viewHolder.tvCountryCode);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvAppName.setText(appBeans.get(position).app_name);
		//viewHolder.tvCountryCode.setText(appBeans.get(position).getCode());


		return convertView;
	}


	public void filter(String filterText) {
		appBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		System.out.println("---appBeansOriginal size: "+appBeansOriginal.size());
		if(filterText.length() == 0) {
			appBeans.addAll(appBeansOriginal);
		} else {
			for(AppBean temp :appBeansOriginal) {
				if(temp.app_name.toLowerCase(Locale.getDefault()).contains(filterText) ) {//|| temp.getCode().toLowerCase(Locale.getDefault()).contains(filterText)
					appBeans.add(temp);
				}
			}
		}
		/*if (allNotes.size()==0) {
			DATA.noNotesFound=true;
		} else {
			DATA.noNotesFound=false;
		}*/
		notifyDataSetChanged();
	}//end filter

}
