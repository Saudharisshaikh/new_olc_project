package com.app.covacard.covacard;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.app.covacard.R;
import com.app.covacard.util.DATA;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TestResultAdapter extends ArrayAdapter<TestResultBean> {

	Activity activity;
	ArrayList<TestResultBean> testResultBeans;


	public TestResultAdapter(Activity activity , ArrayList<TestResultBean> testResultBeans) {
		super(activity, R.layout.lv_test_result_row, testResultBeans);

		this.activity = activity;
		this.testResultBeans = testResultBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);
	}


	static class ViewHolder {
		TextView tvName,tvRelationShip,tvResult,tvDate,tvReportBody;
		ImageView ivDeleteCard, ivVCfrontImg;
		LinearLayout layEmailReport,layUserAddedReport;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_test_result_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvName = convertView.findViewById(R.id.tvName);
			viewHolder.tvRelationShip = convertView.findViewById(R.id.tvRelationShip);
			viewHolder.ivDeleteCard = convertView.findViewById(R.id.ivDeleteCard);
			viewHolder.ivVCfrontImg = convertView.findViewById(R.id.ivVCfrontImg);
			viewHolder.tvResult = convertView.findViewById(R.id.tvResult);
			viewHolder.tvDate = convertView.findViewById(R.id.tvDate);
			viewHolder.tvReportBody = convertView.findViewById(R.id.tvReportBody);
			viewHolder.layEmailReport = convertView.findViewById(R.id.layEmailReport);
			viewHolder.layUserAddedReport = convertView.findViewById(R.id.layUserAddedReport);


			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvName.setText(testResultBeans.get(position).name);
		viewHolder.tvRelationShip.setText(testResultBeans.get(position).relation);
		viewHolder.tvResult.setText(testResultBeans.get(position).result);

		String formatedDate = testResultBeans.get(position).dateof;//2021-04-12 09:17:46
		try {
			Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(formatedDate);//"2020-2-31 11:30:19"
			formatedDate = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a").format(d);
			//commenceDate = commenceDate.replace(" ","\n");
		}catch (Exception e){e.printStackTrace();}

		viewHolder.tvDate.setText(formatedDate);

		DATA.loadImageFromURL(testResultBeans.get(position).frontpic, R.drawable.ic_placeholder_2, viewHolder.ivVCfrontImg);
		//DATA.loadImageFromURL(cardBeans.get(position).back_pic, R.drawable.ic_placeholder_2, viewHolder.ivVCBackImg);

		viewHolder.ivDeleteCard.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				AlertDialog alertDialog =
						new AlertDialog.Builder(activity,R.style.CustomAlertDialogTheme)
								.setTitle("Confirm")
								.setMessage("Are you sure ? Do you want to delete this card ?")
								.setPositiveButton("Yes Delete", (dialogInterface, i) ->
										((ActivityTestResultsList)activity).deleteCard(position))
								.setNegativeButton("Not Now",null)
								.create();
				alertDialog.show();
			}
		});


		if(testResultBeans.get(position).result_type.equalsIgnoreCase("email")){
			viewHolder.layUserAddedReport.setVisibility(View.GONE);
			viewHolder.layEmailReport.setVisibility(View.VISIBLE);
			viewHolder.tvReportBody.setText(Html.fromHtml(testResultBeans.get(position).bodyhtml));
		}else {
			viewHolder.layUserAddedReport.setVisibility(View.VISIBLE);
			viewHolder.layEmailReport.setVisibility(View.GONE);
		}


		return convertView;
}






}
