package com.app.mhcsn_cp.reliance.idtnote;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.mhcsn_cp.R;

import java.util.ArrayList;

public class IDTnoteListAdapter extends ArrayAdapter<IDTnoteListBean> {

	Activity activity;
	ArrayList<IDTnoteListBean> idTnoteListBeans;

	public IDTnoteListAdapter(Activity activity , ArrayList<IDTnoteListBean> idTnoteListBeans) {
		super(activity, R.layout.lv_idtnotelist_row, idTnoteListBeans);
		this.activity = activity;
		this.idTnoteListBeans = idTnoteListBeans;
	}

	static class ViewHolder {
		TextView tvDate;

	}
	ViewHolder viewHolder = null;
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_idtnotelist_row, null);

			viewHolder = new ViewHolder();
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);

			convertView.setTag(viewHolder);

			convertView.setTag(R.id.tvDate, viewHolder.tvDate);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvDate.setText(idTnoteListBeans.get(position).dateof);

		return convertView;
	}

}
