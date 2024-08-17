package com.app.emcurauc.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcurauc.R;
import com.app.emcurauc.model.Organization;
import com.app.emcurauc.util.SelectedFormListener;

import java.util.ArrayList;

public class PatientInsuranceAdapter extends RecyclerView.Adapter<PatientInsuranceAdapter.ViewHolder> {

    private final ArrayList<Organization> organizations;
    private final Context context;
    SelectedFormListener selectedFormListener;
    boolean isCheck = false;

    public PatientInsuranceAdapter(Context context, ArrayList<Organization> organizations,SelectedFormListener selectedFormListener) {
        this.organizations = organizations;
        this.context = context;
        this.selectedFormListener= selectedFormListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.patient_insurance_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Organization organization = organizations.get(position);
        holder.orgNameTextView.setText("Organization Name:\n"+organization.org_name);
        holder.patNameTextView.setText("Patient Name:\n"+organization.patient_info.first_name + " " + organization.patient_info.last_name); // Assuming PatientInfo has first_name and last_name fields
        holder.patMemberIdTextView.setText("Member Id:\n"+organization.getMember_id());
        holder.patIdCodeTextView.setText("ID code:\n"+organization.getIdentification_code());
        holder.patCovTextView.setText("Cov:\n"+organization.getCov());
        holder.patPlansTextView.setText("Plans:\n"+organization.getPlans());

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                 isCheck = isChecked;
                if (isChecked) {
                    // Checkbox is checked, call the adapter method through the listener
                    selectedFormListener.selectedForm(organization); // Pass the selected organization
                } else {
                     selectedFormListener.unSelectedForm(organization);
                    Log.d("--notCheck", "onCheckedChanged: ");
                    // Checkbox is unchecked, optional: handle unchecking (e.g., deselect the organization)
                }
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.checkBox.setChecked(!isCheck);
            }
        });
    }





    @Override
    public int getItemCount() {
        return organizations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView orgNameTextView;
        private final TextView patNameTextView;
        private final TextView patMemberIdTextView;
        private final TextView patIdCodeTextView;
        private final TextView patCovTextView;
        private final TextView patPlansTextView;

        private final CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            orgNameTextView = itemView.findViewById(R.id.org_name);
            patNameTextView = itemView.findViewById(R.id.pat_name);
            patMemberIdTextView = itemView.findViewById(R.id.pat_member_id);
            patIdCodeTextView = itemView.findViewById(R.id.pat_id_code);
            patCovTextView = itemView.findViewById(R.id.pat_cov);
            patPlansTextView = itemView.findViewById(R.id.pat_plans);
            checkBox = itemView.findViewById(R.id.cbInsurance);
        }
    }
}
