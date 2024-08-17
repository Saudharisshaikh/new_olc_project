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


/**
 * Created by Jaison on 08/10/16.
 */

public class RvPainAdapter extends RecyclerView.Adapter<RvPainAdapter.MyViewHolder> {

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


    public RvPainAdapter(Context context) {
        this.mContext=context;

        selectedPos = RecyclerView.NO_POSITION;//important to reset when const called
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_pain_row, parent, false);

        return new MyViewHolder(itemView);
    }

    public static int selectedPos = RecyclerView.NO_POSITION;

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.ivPSEmoji.setImageResource(GetLiveCare.painSevEmojies[position]);
        holder.tvPSLbl.setText(GetLiveCare.painSeverity[position]);

        holder.rvPainItem.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {
        return GetLiveCare.painSeverity.length;
    }
}

