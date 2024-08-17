package com.digihealthcard.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.digihealthcard.R;
import com.digihealthcard.ActivityBuyPackage;
import com.digihealthcard.ActivityPackages;
import com.digihealthcard.model.PackageBean;
import com.digihealthcard.model.SubscriptionPlanBean;
import com.digihealthcard.util.SharedPrefsHelper;

import java.util.ArrayList;

public class PackageAdapter extends ArrayAdapter<PackageBean> {

	Activity activity;
	ArrayList<PackageBean> packageBeans;
	SubscriptionPlanBean subscriptionPlanBean;


	public PackageAdapter(Activity activity , ArrayList<PackageBean> packageBeans) {
		super(activity, R.layout.lv_package_row, packageBeans);

		this.activity = activity;
		this.packageBeans = packageBeans;
		//prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);
		//customToast = new CustomToast(activity);

		subscriptionPlanBean = SharedPrefsHelper.getInstance().getSubscriptionPlan();
	}


	static class ViewHolder {
		TextView tvPkgTitle,tvPkgDes,tvPkgPrice,
				tvPlanTitle,tvPkgType,tvPkgMode;
		Button btnBuyPkg;
		//LinearLayout layPriceCont;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_package_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvPkgTitle = convertView.findViewById(R.id.tvPkgTitle);
			viewHolder.tvPkgDes = convertView.findViewById(R.id.tvPkgDes);
			viewHolder.tvPkgPrice = convertView.findViewById(R.id.tvPkgPrice);
			viewHolder.btnBuyPkg = convertView.findViewById(R.id.btnBuyPkg);
			//viewHolder.layPriceCont = convertView.findViewById(R.id.layPriceCont);
			viewHolder.tvPlanTitle = convertView.findViewById(R.id.tvPlanTitle);
			viewHolder.tvPkgType = convertView.findViewById(R.id.tvPkgType);
			viewHolder.tvPkgMode = convertView.findViewById(R.id.tvPkgMode);

			convertView.setTag(viewHolder);

			//convertView.setTag(R.id.tvInsurance, viewHolder.tvInsurance);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}



		viewHolder.tvPkgTitle.setText(packageBeans.get(position).package_name);
		viewHolder.tvPkgPrice.setText("US$ "+packageBeans.get(position).amount);
		viewHolder.tvPlanTitle.setText(packageBeans.get(position).package_name+" Plan");
		viewHolder.tvPkgType.setText(packageBeans.get(position).pkg_type);
		viewHolder.tvPkgMode.setText(packageBeans.get(position).pkg_mode);


		if(packageBeans.get(position).isCustomAdded){
			//viewHolder.layPriceCont.setVisibility(View.GONE);
			viewHolder.tvPkgDes.setText("Subscription code provided by your employer.");
			viewHolder.btnBuyPkg.setText("Subscribe With Code");

			viewHolder.btnBuyPkg.setOnClickListener(v -> {
				((ActivityPackages) activity).showBuyWithCodeDialog();
			});
		}else {
			//viewHolder.layPriceCont.setVisibility(View.VISIBLE);
			viewHolder.btnBuyPkg.setText("Subscribe Now");
			String desc = "This subscription package will renew after "+packageBeans.get(position).duration_month+" month(s)";
			viewHolder.tvPkgDes.setText(desc);

			viewHolder.btnBuyPkg.setOnClickListener(v -> {
				ActivityPackages.selectedPackageBean = packageBeans.get(position);
				activity.startActivity(new Intent(activity, ActivityBuyPackage.class));
			});


			if(subscriptionPlanBean != null){
				if(subscriptionPlanBean.package_id.equalsIgnoreCase(packageBeans.get(position).id)){
					viewHolder.btnBuyPkg.setText("Subscribed");
					viewHolder.btnBuyPkg.setEnabled(false);
				}else {
					viewHolder.btnBuyPkg.setText("Subscribe Now");
					viewHolder.btnBuyPkg.setEnabled(true);
				}
			}
		}


		return convertView;
	}



}

