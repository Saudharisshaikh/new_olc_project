package com.app.OnlineCareTDC_MA.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.app.OnlineCareTDC_MA.R;
import com.app.OnlineCareTDC_MA.model.AppBean2;

import java.util.ArrayList;

public class AppsExpAdapter extends ArrayAdapter<AppBean2> {
	Context activity;
	ArrayList<AppBean2> appBeans;
	ArrayList<AppBean2> appBeansOriginal;

	public static int selectedPos = -1;

	public AppsExpAdapter(Context activity, ArrayList<AppBean2> appBeans) {
		super(activity, R.layout.gv_exp_apps_row);

		this.activity = activity;
		this.appBeans = appBeans;

		this.appBeansOriginal = new ArrayList<>();
		this.appBeansOriginal.addAll(appBeans);

		selectedPos = -1;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return appBeans.size();
	}

	@Override
	public AppBean2 getItem(int pos) {
		// TODO Auto-generated method stub
		return appBeans.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	static class ViewHolder {
		CheckBox cbApp;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.gv_exp_apps_row, null);

			viewHolder = new ViewHolder();

			viewHolder.cbApp = convertView.findViewById(R.id.cbApp);
			//viewHolder.tvCountryCode = convertView.findViewById(R.id.tvCountryCode);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.cbApp, viewHolder.cbApp);
			//convertView.setTag(R.id.tvCountryCode, viewHolder.tvCountryCode);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.cbApp.setText(appBeans.get(position).appName);
		//viewHolder.tvCountryCode.setText(appBeans.get(position).getCode());

		viewHolder.cbApp.setCompoundDrawablesWithIntrinsicBounds(appBeans.get(position).drawableSelecterID , 0, 0, 0);

		viewHolder.cbApp.setChecked(selectedPos == position);

		return convertView;
	}


//	public void filter(String filterText) {
//		appBeans.clear();
//		filterText = filterText.toLowerCase(Locale.getDefault());
//		DATA.print("---appBeansOriginal size: "+appBeansOriginal.size());
//		if(filterText.length() == 0) {
//			appBeans.addAll(appBeansOriginal);
//		} else {
//			for(AppBean temp :appBeansOriginal) {
//				if(temp.app_name.toLowerCase(Locale.getDefault()).contains(filterText) ) {//|| temp.getCode().toLowerCase(Locale.getDefault()).contains(filterText)
//					appBeans.add(temp);
//				}
//			}
//		}
//		/*if (allNotes.size()==0) {
//			DATA.noNotesFound=true;
//		} else {
//			DATA.noNotesFound=false;
//		}*/
//		notifyDataSetChanged();
//	}//end filter

}
