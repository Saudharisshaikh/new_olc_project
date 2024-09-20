package com.app.emcuradr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.R;
import com.app.emcuradr.model.AllScriptPatient;
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.util.AllScriptPatientListener;
import com.app.emcuradr.util.ExistingPatientCall;

import java.util.ArrayList;

public class AllScriptsPatientAdapter extends RecyclerView.Adapter<AllScriptsPatientAdapter.ViewHolder> {

    ArrayList<AllScriptPatient> existingPatientArrayList;
    Context context;
    AllScriptPatientListener allScriptPatientListener;

    public AllScriptsPatientAdapter(ArrayList<AllScriptPatient> existingPatientArrayList,
                                    Context context, AllScriptPatientListener allScriptPatientListener) {
        this.existingPatientArrayList = existingPatientArrayList;
        this.context = context;
        this.allScriptPatientListener = allScriptPatientListener;
    }

    @NonNull
    @Override
    public AllScriptsPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.allscriptpatientlayout, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull AllScriptsPatientAdapter.ViewHolder holder, int position) {

        AllScriptPatient allScriptPatient = existingPatientArrayList.get(position);
        holder.tvName.setText(allScriptPatient.firstName+" "+allScriptPatient.lastName);
        holder.tvPhone.setText("Mobile: "+allScriptPatient.cellPhone);
        holder.tvEmail.setText("Email: "+allScriptPatient.emailAddress);
        holder.tvAddress.setText("Address: "+allScriptPatient.addressLine1);
        holder.tvGender.setText("Gender: "+allScriptPatient.gender);
        holder.tvHome.setText("Home: "+allScriptPatient.homePhone);
        holder.tvDOB.setText("DOB: "+allScriptPatient.dateOfBirth);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allScriptPatientListener.selectedPatient(allScriptPatient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return existingPatientArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvEmail,tvPhone,tvHome,tvDOB,tvAddress,tvGender;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvMobile);
            tvGender = itemView.findViewById(R.id.tvGender);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvHome = itemView.findViewById(R.id.tvHome);
            tvDOB = itemView.findViewById(R.id.tvdob);

                  }
    }
}
