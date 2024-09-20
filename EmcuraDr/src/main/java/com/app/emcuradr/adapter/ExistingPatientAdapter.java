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
import com.app.emcuradr.model.ExistingPatient;
import com.app.emcuradr.util.ExistingPatientCall;

import java.util.ArrayList;

public class ExistingPatientAdapter extends RecyclerView.Adapter<ExistingPatientAdapter.ViewHolder> {

    ArrayList<ExistingPatient> existingPatientArrayList;
    Context context;
    ExistingPatientCall existingPatientCall;

    public ExistingPatientAdapter(ArrayList<ExistingPatient> existingPatientArrayList, Context context,ExistingPatientCall existingPatientCall) {
        this.existingPatientArrayList = existingPatientArrayList;
        this.context = context;
        this.existingPatientCall = existingPatientCall;
    }

    @NonNull
    @Override
    public ExistingPatientAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.existingpatientlayout, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ExistingPatientAdapter.ViewHolder holder, int position) {

        ExistingPatient existingPatient = existingPatientArrayList.get(position);
        holder.tvName.setText(existingPatient.firstName+" "+existingPatient.lastName);
        holder.tvPhone.setText("Phone: "+existingPatient.phone);
        holder.tvEmail.setText("Email: "+existingPatient.email);
        if(existingPatient.is_online.equals("1")){
            holder.onlineImage.setImageResource(R.drawable.icon_online);
        }
        else {
            holder.onlineImage.setImageResource(R.drawable.icon_notification);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                existingPatientCall.existingPatient(existingPatient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return existingPatientArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvName,tvEmail,tvPhone;
        ImageView onlineImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
            onlineImage = itemView.findViewById(R.id.ivIsonline);
        }
    }
}
