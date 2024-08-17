package com.app.omranpatient.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.omranpatient.R;
import com.app.omranpatient.model.PayerBean;

import java.util.ArrayList;
import java.util.Locale;

public class DialogInsuranceAdapter extends ArrayAdapter<PayerBean> {
	Context activity;
	ArrayList<PayerBean> payerBeans;
	ArrayList<PayerBean> payerBeansOriginal;

	public DialogInsuranceAdapter(Context activity, ArrayList<PayerBean> payerBeans) {
		super(activity, R.layout.lv_dial_insurance_row);

		this.activity = activity;
		this.payerBeans = payerBeans;

		this.payerBeansOriginal = new ArrayList<>();
		this.payerBeansOriginal.addAll(payerBeans);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return payerBeans.size();
	}

	@Override
	public PayerBean getItem(int pos) {
		// TODO Auto-generated method stub
		return payerBeans.get(pos);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	static class ViewHolder {
		TextView tvPayerName,tvPayerId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_dial_insurance_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvPayerName = convertView.findViewById(R.id.tvPayerName);
			viewHolder.tvPayerId = convertView.findViewById(R.id.tvPayerId);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvPayerName, viewHolder.tvPayerName);
			convertView.setTag(R.id.tvPayerId, viewHolder.tvPayerId);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvPayerName.setText(payerBeans.get(position).payer_name);
		viewHolder.tvPayerId.setText(payerBeans.get(position).payer_id);
		 
		
		return convertView;
	}
	
	
public void filter(String filterText) {
		payerBeans.clear();
		filterText = filterText.toLowerCase(Locale.getDefault());
		System.out.println("---contrycodeBeansOriginal size: "+payerBeansOriginal.size());
		if(filterText.length() == 0) {
			payerBeans.addAll(payerBeansOriginal);
		} else {
			for(PayerBean temp : payerBeansOriginal) {
				if(temp.payer_name.toLowerCase(Locale.getDefault()).contains(filterText) ||
						temp.payer_id.toLowerCase(Locale.getDefault()).contains(filterText)) {
					payerBeans.add(temp);
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
