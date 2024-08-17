package com.app.emcuradr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.PatientMedicalHistory;
import com.app.emcuradr.R;
import com.app.emcuradr.model.PatientMedicalHistoryBean;

import java.util.ArrayList;

public class PatientMedicalHistoryAdapter extends RecyclerView.Adapter<PatientMedicalHistoryAdapter.PatientMedicalHistoryViewHolder>{


    private ArrayList<PatientMedicalHistoryBean> patientMedicalHistoryList;


    public PatientMedicalHistoryAdapter(ArrayList<PatientMedicalHistoryBean> patientMedicalHistoryList) {
        this.patientMedicalHistoryList = patientMedicalHistoryList;
    }


    @NonNull
    @Override
    public PatientMedicalHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.med_history_item, parent, false);
        return new PatientMedicalHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientMedicalHistoryViewHolder holder, int position) {

        PatientMedicalHistoryBean patientMedicalHistory = patientMedicalHistoryList.get(position);

        holder.drugTextView.setText(patientMedicalHistory.getDrug());
        holder.prescriberNameTextView.setText(patientMedicalHistory.getPrescriberName());
        holder.quantityTextView.setText(patientMedicalHistory.getQuantity());
        holder.daysSupplyTextView.setText(patientMedicalHistory.getDaysSupply());
        holder.lastFillDateTextView.setText(patientMedicalHistory.getLastFillDate());
        holder.pharmacyTextView.setText(patientMedicalHistory.getPharmacy());

    }

    @Override
    public int getItemCount() {
        return patientMedicalHistoryList.size();
    }

    class PatientMedicalHistoryViewHolder extends RecyclerView.ViewHolder{

        TextView drugTextView;
        TextView prescriberNameTextView;
        TextView quantityTextView;
        TextView daysSupplyTextView;
        TextView lastFillDateTextView;
        TextView pharmacyTextView;

        public PatientMedicalHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            drugTextView = itemView.findViewById(R.id.med_name);
            prescriberNameTextView = itemView.findViewById(R.id.prescriber);
            quantityTextView = itemView.findViewById(R.id.quantity);
            daysSupplyTextView = itemView.findViewById(R.id.date_supply);
            lastFillDateTextView = itemView.findViewById(R.id.last_fill_date);
            pharmacyTextView = itemView.findViewById(R.id.pharmacies);



        }
    }
}