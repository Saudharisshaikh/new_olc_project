package com.app.onlinecare_pk.dental;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.onlinecare_pk.R;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Jaison on 08/10/16.
 */

public class RvDentalPainLevelAdapter extends RecyclerView.Adapter<RvDentalPainLevelAdapter.MyViewHolder> {

    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivDentalPainLevel;
        public TextView tvDentalPainLevel;
        public LinearLayout rvPainItem;

        public MyViewHolder(View view) {
            super(view);
            ivDentalPainLevel = view.findViewById(R.id.ivDentalPainLevel);
            tvDentalPainLevel = view.findViewById(R.id.tvDentalPainLevel);
            rvPainItem = view.findViewById(R.id.rvPainItem);
        }
    }


    List<String> painLevelOptions;
    public RvDentalPainLevelAdapter(Context context, List<String> painLevelOptions) {
        this.mContext=context;

        this.painLevelOptions = painLevelOptions;

        selectedPos = RecyclerView.NO_POSITION;//important to reset when const called
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_dental_painlevel_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public static int selectedPos = RecyclerView.NO_POSITION;

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.ivDentalPainLevel.setImageResource(selectedPos == position ? R.drawable.cust_cir_red_dpl : R.drawable.cust_cir_grey_dpl);
        holder.tvDentalPainLevel.setText(painLevelOptions.get(position));

        //holder.rvPainItem.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return painLevelOptions.size();
    }
}

