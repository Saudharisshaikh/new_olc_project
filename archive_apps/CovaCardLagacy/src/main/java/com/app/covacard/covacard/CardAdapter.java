package com.app.covacard.covacard;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.covacard.R;
import com.app.covacard.util.DATA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CardAdapter extends ArrayAdapter<CardBean> {

	Activity activity;
	ArrayList<CardBean> cardBeans;


	public CardAdapter(Activity activity , ArrayList<CardBean> cardBeans) {
		super(activity, R.layout.lv_card_row, cardBeans);

		this.activity = activity;
		this.cardBeans = cardBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		TextView tvName,tvRelationShip,tvDate;
		ImageView ivDeleteCard, ivVCfrontImg,ivVCBackImg;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_card_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvName = convertView.findViewById(R.id.tvName);
			viewHolder.tvRelationShip = convertView.findViewById(R.id.tvRelationShip);
			viewHolder.ivDeleteCard = convertView.findViewById(R.id.ivDeleteCard);
			viewHolder.ivVCfrontImg = convertView.findViewById(R.id.ivVCfrontImg);
			viewHolder.ivVCBackImg = convertView.findViewById(R.id.ivVCBackImg);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);


			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvName.setText(cardBeans.get(position).name);
		viewHolder.tvRelationShip.setText(cardBeans.get(position).relation);


		//2020-02-26T16:01:34.000Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
		String formatedDate = cardBeans.get(position).date;//2021-04-12 09:17:46
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}

		viewHolder.tvDate.setText(formatedDate);

		DATA.loadImageFromURL(cardBeans.get(position).front_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCfrontImg);
		DATA.loadImageFromURL(cardBeans.get(position).back_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCBackImg);

		viewHolder.ivDeleteCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
								.setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this card ?")
								.setPositiveButton("Yes Delete", (dialogInterface, i) ->
										((ActivityCardsList)activity).deleteCard(position))
								.setNegativeButton("Not Now",null)
								.create();
				alertDialog.show();
			}
		});


		return convertView;
}






}
