package com.app.msu_uc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.msu_uc.R;
import com.app.msu_uc.model.ConversationBean;
import com.app.msu_uc.util.DATA;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class ConversationAdapter extends ArrayAdapter<ConversationBean> {

	Activity activity;
	ArrayList<ConversationBean> conversationBeans;
	SharedPreferences prefs;

	public ConversationAdapter(Activity activity , ArrayList<ConversationBean> conversationBeans) {
		super(activity, R.layout.lv_conversation_row, conversationBeans);

		this.activity = activity;
		this.conversationBeans = conversationBeans;
		prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
//		filter(DATA.selectedDrId);
	}

	static class ViewHolder {

		TextView tvNameConv,tvDateConv,tvLastMsgConv,tvConvType;
		CircularImageView ivConv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_conversation_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvNameConv = (TextView) convertView.findViewById(R.id.tvNameConv);
			viewHolder.tvDateConv = (TextView) convertView.findViewById(R.id.tvDateConv);
			viewHolder.tvLastMsgConv = (TextView) convertView.findViewById(R.id.tvLastMsgConv);
			viewHolder.tvConvType = (TextView) convertView.findViewById(R.id.tvConvType);
			viewHolder.ivConv = (CircularImageView) convertView.findViewById(R.id.ivConv);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvNameConv, viewHolder.tvNameConv);
			convertView.setTag(R.id.tvDateConv, viewHolder.tvDateConv);
			convertView.setTag(R.id.tvLastMsgConv, viewHolder.tvLastMsgConv);
			convertView.setTag(R.id.tvConvType, viewHolder.tvConvType);
			convertView.setTag(R.id.ivConv, viewHolder.ivConv);
		}
		else {

			viewHolder = (ViewHolder) convertView.getTag();
		}
		

		viewHolder.tvNameConv.setText(conversationBeans.get(position).getFirst_name()+" "+conversationBeans.get(position).getLast_name());
		viewHolder.tvNameConv.setTag(conversationBeans.get(position).getFirst_name()+" "+conversationBeans.get(position).getLast_name());
		
		
		viewHolder.tvDateConv.setText(conversationBeans.get(position).getDateof());
		viewHolder.tvDateConv.setTag(conversationBeans.get(position).getDateof());
		
		String lastMsg = conversationBeans.get(position).getMessage_text();
		if (lastMsg.length() > 100) {
			lastMsg = lastMsg.substring(0, 100)+" . . .";
		}
		viewHolder.tvLastMsgConv.setText(lastMsg);
		viewHolder.tvLastMsgConv.setTag(lastMsg);
		
		//UrlImageViewHelper.setUrlDrawable(viewHolder.ivConv, conversationBeans.get(position).getImage(), R.drawable.doctor);
		/*if(! conversationBeans.get(position).getImage().isEmpty()){
			Picasso.with(activity).load(conversationBeans.get(position).getImage()).placeholder(R.drawable.icon_call_screen)
					.into(viewHolder.ivConv);
		}*/
		DATA.loadImageFromURL(conversationBeans.get(position).getImage(),R.drawable.icon_call_screen,viewHolder.ivConv);


		String desig = "";
		if (conversationBeans.get(position).getUser_type().equalsIgnoreCase("doctor")) {
			desig = "Doctor";
		}else if (conversationBeans.get(position).getUser_type().equalsIgnoreCase("specialist")){
			desig = "Specialist";

			if (!conversationBeans.get(position).doctor_category.isEmpty()) {
				desig = conversationBeans.get(position).doctor_category;
			}
		}else if (conversationBeans.get(position).getUser_type().equalsIgnoreCase("patient")){
			desig = "Patient";
		}

		viewHolder.tvConvType.setText(desig);
		
		return convertView;
	}

}
