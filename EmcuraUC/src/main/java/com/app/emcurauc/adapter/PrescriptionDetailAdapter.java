package com.app.emcurauc.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcurauc.ActivityPrescriptionDetails;
import com.app.emcurauc.R;
import com.app.emcurauc.model.MyPrescriptionModel;
import com.app.emcurauc.model.PrescriptionDetailModel;
import com.app.emcurauc.util.PrescriptionDetailListener;

import java.util.ArrayList;

public class PrescriptionDetailAdapter extends RecyclerView.Adapter<PrescriptionDetailAdapter.ViewHolder> {

    private final Context context;
    ArrayList<PrescriptionDetailModel> prescriptionDetailModelArrayList;
    PrescriptionDetailListener prescriptionDetailListener;

    public PrescriptionDetailAdapter(Context context,
                                     ArrayList<PrescriptionDetailModel> prescriptionDetailModelArrayList,PrescriptionDetailListener prescriptionDetailListener) {
        this.context = context;
        this.prescriptionDetailModelArrayList = prescriptionDetailModelArrayList;
        this.prescriptionDetailListener = prescriptionDetailListener;
    }

    @NonNull
    @Override
    public PrescriptionDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.prescription_detail_item, parent, false);
        return new PrescriptionDetailAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionDetailAdapter.ViewHolder holder, int position) {

        PrescriptionDetailModel prescriptionDetailModel = prescriptionDetailModelArrayList.get(position);
        holder.drugName.setText(prescriptionDetailModel.getDrug_name());
        holder.drugDirection.setText(prescriptionDetailModel.getDirections());
        holder.drugQuantity.setText(prescriptionDetailModel.getQuantity());
        holder.drugRefill.setText(prescriptionDetailModel.getRefill());
        holder.drugNote.setText(prescriptionDetailModel.getNotes());
        holder.btnSendEducation.setVisibility(prescriptionDetailModel.getSend_education().equals("0")?View.GONE:View.VISIBLE);

        holder.btnSendEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                prescriptionDetailListener.prescriptionDetail(prescriptionDetailModel.getDrug_name());
            }
        });

    }

    @Override
    public int getItemCount() {
        return prescriptionDetailModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView drugName,drugDirection,drugQuantity,drugRefill,drugNote;
        Button btnSendEducation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            drugName = itemView.findViewById(R.id.drug_name);
            drugDirection = itemView.findViewById(R.id.drug_directions);
            drugQuantity = itemView.findViewById(R.id.drug_quantity);
            drugRefill = itemView.findViewById(R.id.drug_refill);
            drugNote = itemView.findViewById(R.id.drug_note);
            btnSendEducation = itemView.findViewById(R.id.btn_drug_education);
        }
    }
}
