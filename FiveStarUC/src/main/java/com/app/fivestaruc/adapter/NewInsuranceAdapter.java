package com.app.fivestaruc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fivestaruc.R;
import com.app.fivestaruc.model.PatientInsuranceModel;
import com.app.fivestaruc.util.CustomToast;
import com.app.fivestaruc.util.NewInsuranceListener;

import java.util.ArrayList;

public class NewInsuranceAdapter extends  RecyclerView.Adapter<NewInsuranceAdapter.ViewHolder>
{

    Activity activity;
    ArrayList<PatientInsuranceModel> patientInsuranceModels;
    SharedPreferences prefs;
    CustomToast customToast;
    private final Context context;

    int selectedPosition = -1;

    Handler handler = new Handler();


    NewInsuranceListener newInsuranceListener;

    public NewInsuranceAdapter(Context context, ArrayList<PatientInsuranceModel> patientInsuranceModels,
                               NewInsuranceListener newInsuranceListener) {
        this.patientInsuranceModels = patientInsuranceModels;
        this.context = context;
        this.newInsuranceListener= newInsuranceListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.new_insurance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PatientInsuranceModel patientInsuranceModel = patientInsuranceModels.get(position);
        holder.insNameTextView.setText("Insurance : "+patientInsuranceModel.getPayer_name());
        holder.insCodeTextView.setText("Insurance Code : "+patientInsuranceModel.getGetInsurance_code()); // Assuming PatientInfo has first_name and last_name fields
        holder.insGroupTextView.setText("Insurance Group : "+patientInsuranceModel.getInsurance_group());
        holder.policyNoTextView.setText("Policy Number : "+patientInsuranceModel.getPolicy_number());

        holder.radioCheck.setVisibility(View.VISIBLE);
        holder.radioCheck.setChecked(position == selectedPosition);





        holder.radioCheck.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedPosition = position;
                notifyDataSetChanged(); // Refresh the list
                if (newInsuranceListener != null) {
                    newInsuranceListener.selectedInsurance(patientInsuranceModel);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                   holder.radioCheck.setChecked(true);

            }
        });



    }

    @Override
    public int getItemCount() {
        return patientInsuranceModels.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView insNameTextView;
        private final TextView policyNoTextView;
        private final TextView insGroupTextView;
        private final TextView insCodeTextView;


        private final RelativeLayout cardLayout;
        private final RadioButton radioCheck;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            insNameTextView = itemView.findViewById(R.id.ins_name);
            policyNoTextView = itemView.findViewById(R.id.policy_no);
            insGroupTextView = itemView.findViewById(R.id.ins_group);
            insCodeTextView = itemView.findViewById(R.id.ins_code);
            cardLayout = itemView.findViewById(R.id.card_layout);
            radioCheck = itemView.findViewById(R.id.radioCheck);
        }
    }
}
