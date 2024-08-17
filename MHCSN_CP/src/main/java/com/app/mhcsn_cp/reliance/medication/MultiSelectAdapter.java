package com.app.mhcsn_cp.reliance.medication;

import android.content.Context;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.R;


import java.util.ArrayList;


/**
 * Created by Jaison on 08/10/16.
 */

public class MultiSelectAdapter extends RecyclerView.Adapter<MultiSelectAdapter.MyViewHolder> {

    public ArrayList<MedicationBean> usersList=new ArrayList<>();
    public ArrayList<MedicationBean> selected_usersList=new ArrayList<>();
    Context mContext;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvMedName, tvMedStrength,tvMedInstructions;
        public LinearLayout ll_listitem,  cellHeader, cellContent;
        TextView tvMedRoute,tvMedPurpose,tvMedPrescriber,tvMedStrtDate,tvMedStopDate,tvMedDirection,tvMedNotes;
        TextView btnRenew,btnStopMedication;
        Button btnEdit;

        public MyViewHolder(View view) {
            super(view);
            tvMedName = (TextView) view.findViewById(R.id.tvMedName);
            tvMedStrength = (TextView) view.findViewById(R.id.tvMedStrength);
            tvMedInstructions = (TextView) view.findViewById(R.id.tvMedInstructions);
            ll_listitem = (LinearLayout)view.findViewById(R.id.ll_listitem);
            cellHeader = (LinearLayout)view.findViewById(R.id.cellHeader);
            cellContent = (LinearLayout)view.findViewById(R.id.cellContent);

            tvMedRoute = (TextView) view.findViewById(R.id.tvMedRoute);
            tvMedPurpose = (TextView) view.findViewById(R.id.tvMedPurpose);
            tvMedPrescriber = (TextView) view.findViewById(R.id.tvMedPrescriber);
            tvMedStrtDate = (TextView) view.findViewById(R.id.tvMedStrtDate);
            tvMedStopDate = (TextView) view.findViewById(R.id.tvMedStopDate);
            tvMedDirection = (TextView) view.findViewById(R.id.tvMedDirection);
            tvMedNotes = (TextView) view.findViewById(R.id.tvMedNotes);

            btnEdit = (Button) view.findViewById(R.id.btnEdit);
            btnRenew = (TextView) view.findViewById(R.id.btnRenew);
            btnStopMedication = (TextView) view.findViewById(R.id.btnStopMedication);

        }
    }


    public MultiSelectAdapter(Context context, ArrayList<MedicationBean> userList, ArrayList<MedicationBean> selectedList) {
        this.mContext=context;
        this.usersList = userList;
        this.selected_usersList = selectedList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_medi_multiselect_row, parent, false);

        return new MyViewHolder(itemView);
    }


    public int expandPos = -1;
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MedicationBean movie = usersList.get(position);
        holder.tvMedName.setText(movie.name);
        holder.tvMedStrength.setText(movie.strength);
        holder.tvMedInstructions.setText(movie.directions);

        holder.tvMedRoute.setText(movie.route);
        holder.tvMedPurpose.setText(movie.purpose);
        holder.tvMedPrescriber.setText(movie.doctor_name);
        holder.tvMedStrtDate.setText(movie.start_date);
        holder.tvMedStopDate.setText(movie.stop_date);
        holder.tvMedDirection.setText(movie.directions);
        holder.tvMedNotes.setText(movie.notes);

        if(selected_usersList.contains(usersList.get(position)))
            //holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_selected_state));
            holder.ll_listitem.setBackgroundResource(R.drawable.cust_border_grey_outline2);
        else
            //holder.ll_listitem.setBackgroundColor(ContextCompat.getColor(mContext, R.color.list_item_normal_state));
            holder.ll_listitem.setBackgroundResource(R.drawable.cust_border_white_outline);



        if(expandPos == position){
            holder.cellHeader.setVisibility(View.GONE);
            holder.cellContent.setVisibility(View.VISIBLE);
        }else {
            holder.cellHeader.setVisibility(View.VISIBLE);
            holder.cellContent.setVisibility(View.GONE);
        }


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (v.getId()){

                    case R.id.btnEdit:
                        Toast.makeText(mContext, "Edit click", 0).show();
                        break;
                }
            }
        };
        holder.btnEdit.setOnClickListener(onClickListener);
        holder.btnRenew.setOnClickListener(onClickListener);
        holder.btnStopMedication.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}

