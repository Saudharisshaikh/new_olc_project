package com.app.emcurauc.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.emcurauc.GetLiveCare;
import com.app.emcurauc.R;
import com.app.emcurauc.SearchADoctor;
import com.app.emcurauc.b_health2.GetLiveCareFormBhealth;
import com.bumptech.glide.Glide;
import com.darsh.multipleimageselect.models.Image;

import java.util.ArrayList;

public class VVReportImagesAdapter extends ArrayAdapter<Image> {

	Activity activity;
	ArrayList<Image> images;

	public VVReportImagesAdapter(Activity activity, ArrayList<Image> images) {
		super(activity, R.layout.vv_img_list_row, images);

		this.activity = activity;
		this.images = images;
	}

	static class ViewHolder {

		TextView tvCatName,tvImgNo;
		ImageView imgCatIcon,ivDeleteImg;
	}

	@Override
	public int getCount() {
		return images.size()+1;
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

		if(position == images.size()){
			viewHolder.imgCatIcon.setScaleType(ImageView.ScaleType.CENTER);
			viewHolder.imgCatIcon.setImageResource(R.drawable.ic_add_a_photo_black_24dp);
			viewHolder.ivDeleteImg.setVisibility(View.GONE);
			viewHolder.tvImgNo.setVisibility(View.GONE);
		}else {
			viewHolder.imgCatIcon.setScaleType(ImageView.ScaleType.CENTER_CROP);
			viewHolder.ivDeleteImg.setVisibility(View.VISIBLE);
			Glide.with(activity)
					.load(images.get(position).path)
					//.placeholder(com.darsh.multipleimageselect.R.drawable.image_placeholder)
					.into(viewHolder.imgCatIcon);

			viewHolder.tvImgNo.setVisibility(View.VISIBLE);
			viewHolder.tvImgNo.setText(String.valueOf(position+1));
		}

		viewHolder.imgCatIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(position == images.size()){
					try {
						if(activity instanceof GetLiveCare){
							((GetLiveCare) activity).btnSelectImages.performClick();
						}else if(activity instanceof SearchADoctor){
							((SearchADoctor)activity).btnSelectImages.performClick();
						}else if(activity instanceof GetLiveCareFormBhealth){
							((GetLiveCareFormBhealth) activity).btnSelectImages.performClick();
						}
					}catch (Exception e){
						e.printStackTrace();
					}
				}
			}
		});

		viewHolder.ivDeleteImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(activity, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
						.setTitle("Confirm").setMessage("Are you sure ? Do you want to delete image ?")
						.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								images.remove(position);
								notifyDataSetChanged();
							}
						})
						.setNegativeButton("Cancel",null).create().show();
			}
		});


		  /*int sCorner = 15;
		  int sMargin = 2;
		Glide.with(activity)
				.load(images.get(position).path)
				.bitmapTransform(new RoundedCornersTransformation( activity,sCorner, sMargin))
				.into(viewHolder.imgCatIcon);*/

		/*Glide.with(activity).load(images.get(position).path).asBitmap().centerCrop().into(new BitmapImageViewTarget(imgCatIcon) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable =
						RoundedBitmapDrawableFactory.create(activity.getResources(), resource);
				circularBitmapDrawable.setCircular(true);
				imgCatIcon.setImageDrawable(circularBitmapDrawable);
			}
		});*/
		//viewHolder.tvCatName.setTag(DATA.allCategories.get(position).catName);
		//viewHolder.imgCatIcon.setTag(DATA.allCategories.get(position).catIcon);


		return convertView;
	}
}
