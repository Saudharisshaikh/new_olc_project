package com.app.emcurauc.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcurauc.R;
import com.app.emcurauc.api.ApiCallBack;
import com.app.emcurauc.model.MyInsuranceBean;
import com.app.emcurauc.model.Organization;
import com.app.emcurauc.model.PatientInsuranceModel;
import com.app.emcurauc.util.CustomToast;
import com.app.emcurauc.util.DATA;
import com.app.emcurauc.util.NewInsuranceListener;
import com.app.emcurauc.util.SelectedFormListener;

import java.util.ArrayList;

public class NewInsuranceAdapter  extends  RecyclerView.Adapter<NewInsuranceAdapter.ViewHolder>
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
        return new NewInsuranceAdapter.ViewHolder(view);
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
