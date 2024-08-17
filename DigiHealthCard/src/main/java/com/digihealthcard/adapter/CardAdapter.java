package com.digihealthcard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.digihealthcard.ActivityCardDetail;
import com.digihealthcard.R;
import com.digihealthcard.ActivityCardsList;
import com.digihealthcard.model.CardBean;
import com.digihealthcard.ActivityIdCardsList;
import com.digihealthcard.util.DATA;
import com.digihealthcard.util.GloabalMethods;

import java.util.ArrayList;

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
		TextView tvName,tvRelationShip,tvDate, tvTypeOfCard;
		ImageView ivDeleteCard, ivVCfrontImg,ivVCBackImg;
		Button btnPrint , btnPreview;
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
			viewHolder.tvTypeOfCard = convertView.findViewById(R.id.tvTypeOfCard);

			viewHolder.btnPrint = convertView.findViewById(R.id.btnPrintcard);
			viewHolder.btnPreview = convertView.findViewById(R.id.btnPreviewCard);


			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvName.setText(cardBeans.get(position).name);
		viewHolder.tvRelationShip.setText(cardBeans.get(position).relation);


		//2020-02-26T16:01:34.000Z = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
		String formatedDate = cardBeans.get(position).date;//2021-04-12 09:17:46
		/*try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}*/

		viewHolder.tvDate.setText(formatedDate);

		String cardType = cardBeans.get(position).card_type;
		if(!TextUtils.isEmpty(cardBeans.get(position).additional_card_type)){
			cardType = cardType + " - "+cardBeans.get(position).additional_card_type;
		}
		viewHolder.tvTypeOfCard.setText(cardType);

		DATA.loadImageFromURL(cardBeans.get(position).front_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCfrontImg);
		DATA.loadImageFromURL(cardBeans.get(position).back_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCBackImg);
		/*ViewHolder finalViewHolder = viewHolder;
		viewHolder.ivVCfrontImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				finalViewHolder.ivVCfrontImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				DATA.loadImageFromURL(cardBeans.get(position).front_pic, R.drawable.ic_placeholder_2, finalViewHolder.ivVCfrontImg);
			}
		});
		viewHolder.ivVCBackImg.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				finalViewHolder.ivVCBackImg.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				DATA.loadImageFromURL(cardBeans.get(position).front_pic, R.drawable.ic_placeholder_2, finalViewHolder.ivVCBackImg);
			}
		});*/

		viewHolder.ivDeleteCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
								.setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this card ?")
								.setPositiveButton("Yes Delete", (dialogInterface, i) -> {
									if(activity instanceof ActivityCardsList){
										((ActivityCardsList)activity).deleteCard(position);
									}else if(activity instanceof ActivityIdCardsList){
										((ActivityIdCardsList)activity).deleteCard(position);
									}
								})
								.setNegativeButton("Not Now",null)
								.create();
				alertDialog.show();
			}
		});


		viewHolder.btnPrint.setOnClickListener(view ->
		{
			if (activity instanceof ActivityCardsList)
			{
				ActivityCardDetail.cardBeanSelected = cardBeans.get(position);
				activity.startActivity(new Intent(activity , ActivityCardDetail.class));
			}
			else if(activity instanceof ActivityIdCardsList){
				ActivityCardDetail.cardBeanSelected = cardBeans.get(position);
				Intent intent = new Intent(activity, ActivityCardDetail.class);
				intent.putExtra("isFromIdCard", true);
				activity.startActivity(intent);
			}
		});

		viewHolder.btnPreview.setOnClickListener(view ->
		{
			if (activity instanceof ActivityCardsList)
			{
				new GloabalMethods(activity).showWebviewDialog(cardBeans.get(position).code_url, "Card Preview");
			}
			else if(activity instanceof ActivityIdCardsList){
				new GloabalMethods(activity).showWebviewDialog(cardBeans.get(position).code_url, "Card Preview");
			}
		});

		return convertView;
}






}
