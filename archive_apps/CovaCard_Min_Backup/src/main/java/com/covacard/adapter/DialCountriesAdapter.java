package com.covacard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.covacard.R;
import com.covacard.model.CountryBean;


import java.util.ArrayList;
import java.util.Locale;

public class DialCountriesAdapter extends ArrayAdapter<CountryBean> {
	Context activity;
	ArrayList<CountryBean>contrycodeBeans;
	ArrayList<CountryBean>contrycodeBeansOriginal;

	public DialCountriesAdapter(Context activity, ArrayList<CountryBean> contrycodeBeans) {
		super(activity, R.layout.lv_dial_countries_row);

		this.activity = activity;
		this.contrycodeBeans = contrycodeBeans;

		this.contrycodeBeansOriginal = new ArrayList<CountryBean>();
		this.contrycodeBeansOriginal.addAll(contrycodeBeans);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contrycodeBeans.size();
	}

	@Override
	public CountryBean getItem(int pos) {
		// TODO Auto-generated method stub
		return contrycodeBeans.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	static class ViewHolder {
		TextView tvCountryName,tvCountryCode;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dial_countries_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvCountryName = convertView.findViewById(R.id.tvCountryName);
			viewHolder.tvCountryCode = convertView.findViewById(R.id.tvCountryCode);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvCountryName, viewHolder.tvCountryName);
			convertView.setTag(R.id.tvCountryCode, viewHolder.tvCountryCode);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvCountryName.setText(contrycodeBeans.get(position).getName());
		viewHolder.tvCountryCode.setText(contrycodeBeans.get(position).getCode());
		 
		
		return convertView;
	}
	
	
public void filter(String filterText) {
		contrycodeBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		System.out.println("---contrycodeBeansOriginal size: "+contrycodeBeansOriginal.size());
		if(filterText.length() == 0) {
			contrycodeBeans.addAll(contrycodeBeansOriginal);
		} else {
			for(CountryBean temp :contrycodeBeansOriginal) {
				if(temp.getName().toLowerCase(Locale.getDefault()).contains(filterText) ||
						temp.getCode().toLowerCase(Locale.getDefault()).contains(filterText)) {
					contrycodeBeans.add(temp);				
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
