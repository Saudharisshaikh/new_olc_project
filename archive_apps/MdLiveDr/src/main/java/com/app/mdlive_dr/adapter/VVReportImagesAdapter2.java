package com.app.mdlive_dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.mdlive_dr.R;
import com.app.mdlive_dr.util.DATA;

import java.util.ArrayList;

public class VVReportImagesAdapter2 extends ArrayAdapter<String> {

	Activity activity;
	ArrayList<String> imagesArr;

	public VVReportImagesAdapter2(Activity activity, ArrayList<String> imagesArr) {
		super(activity, R.layout.vv_img_list_row, imagesArr);

		this.activity = activity;
		this.imagesArr = imagesArr;
	}

	static class ViewHolder {

		TextView tvCatName,tvImgNo;
		ImageView imgCatIcon,ivDeleteImg;
	}

	@Override
	public int getCount() {
		return imagesArr.size();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 convertView = layoutInflater.inflate(R.layout.vv_img_list_row, null);
			
			viewHolder = new ViewHolder();
			
			viewHolder.tvCatName = (TextView) convertView.findViewById(R.id.tvCatName);
			viewHolder.tvImgNo = (TextView) convertView.findViewById(R.id.tvImgNo);
			viewHolder.imgCatIcon = (ImageView) convertView.findViewById(R.id.imgCatIcon);
			viewHolder.ivDeleteImg = (ImageView) convertView.findViewById(R.id.ivDeleteImg);
			
			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvCatName, viewHolder.tvCatName);
			convertView.setTag(R.id.tvImgNo, viewHolder.tvImgNo);
			convertView.setTag(R.id.imgCatIcon, viewHolder.imgCatIcon);
			convertView.setTag(R.id.ivDeleteImg, viewHolder.ivDeleteImg);
			
		}
		else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

		//viewHolder.tvCatName.setText(images.get(position).name);
		viewHolder.imgCatIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
		viewHolder.tvImgNo.setVisibility(View.VISIBLE);
		viewHolder.tvImgNo.setText(String.valueOf(position+1));
		viewHolder.ivDeleteImg.setVisibility(View.GONE);
		/*Glide.with(activity)
				.load(imagesArr.get(position))
				.placeholder(R.drawable.ic_placeholder_2).into(viewHolder.imgCatIcon);*/

		DATA.loadImageFromURL(imagesArr.get(position),R.drawable.ic_placeholder_2,viewHolder.imgCatIcon);

		return convertView;
	}
}
