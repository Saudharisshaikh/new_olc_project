package com.app.msu_uc.adapter;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.msu_uc.R;
import com.app.msu_uc.GetLiveCare;

import java.util.ArrayList;


/**
 * Created by Jaison on 08/10/16.
 */

public class RvBodyPartAdapter extends RecyclerView.Adapter<RvBodyPartAdapter.MyViewHolder> {

    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivPSEmoji;
        public TextView tvPSLbl;
        public LinearLayout rvPainItem;

        public MyViewHolder(View view) {
            super(view);
            ivPSEmoji = view.findViewById(R.id.ivPSEmoji);
            tvPSLbl = view.findViewById(R.id.tvPSLbl);
            rvPainItem = view.findViewById(R.id.rvPainItem);
        }
    }


    public RvBodyPartAdapter(Context context) {
        this.mContext=context;

        selectedPositons = new ArrayList<>();//important to reset when const called
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_bodypart_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public static ArrayList<Integer> selectedPositons = new ArrayList<>();

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.ivPSEmoji.setImageResource(GetLiveCare.bodyPartIcons[position]);
        holder.tvPSLbl.setText(GetLiveCare.bodyParts[position]);

        holder.rvPainItem.setSelected(selectedPositons.contains(position));
    }

    @Override
    public int getItemCount() {
        return GetLiveCare.bodyParts.length;
    }
}

