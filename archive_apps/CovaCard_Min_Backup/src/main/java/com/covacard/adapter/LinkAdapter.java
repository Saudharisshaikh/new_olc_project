package com.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.covacard.R;
import com.covacard.model.LinkBean;
import com.covacard.util.GloabalMethods;

import java.util.ArrayList;

public class LinkAdapter extends ArrayAdapter<LinkBean> {

	Activity activity;
	ArrayList<LinkBean> linkBeans;
	GloabalMethods gloabalMethods;


	public LinkAdapter(Activity activity , ArrayList<LinkBean> linkBeans) {
		super(activity, R.layout.lv_link_row, linkBeans);

		this.activity = activity;
		this.linkBeans = linkBeans;
		gloabalMethods = new GloabalMethods(activity);
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		CheckBox cbAccept;
		TextView tvLink;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_link_row, null);

			viewHolder = new ViewHolder();

			viewHolder.cbAccept = convertView.findViewById(R.id.cbAccept);
			viewHolder.tvLink = convertView.findViewById(R.id.tvLink);

			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}




		String styledText =  "<font color='#2979ff'><u>"+linkBeans.get(position).header+"</u></font>";
		viewHolder.tvLink.setText(Html.fromHtml(styledText));
		viewHolder.tvLink.setOnClickListener(v -> {
			//gloabalMethods.showWebviewDialog(linkBeans.get(position).url, linkBeans.get(position).header);

			Intent i = new Intent(Intent.ACTION_VIEW);
			i.setData(Uri.parse(linkBeans.get(position).url));
			activity.startActivity(i);
		});

		viewHolder.cbAccept.setOnCheckedChangeListener((buttonView, isChecked) -> {
			linkBeans.get(position).isChecked = isChecked;
		});


		return convertView;
}






}
