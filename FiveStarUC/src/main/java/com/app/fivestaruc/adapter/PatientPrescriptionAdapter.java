package com.app.fivestaruc.adapter;

import static android.os.Build.VERSION_CODES.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.app.fivestaruc.ActivityPrescriptionDetails;
import com.app.fivestaruc.R;
import com.app.fivestaruc.model.MyPrescriptionModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PatientPrescriptionAdapter extends RecyclerView.Adapter<PatientPrescriptionAdapter.ViewHolder> {

    private final Context context;
    ArrayList<MyPrescriptionModel> myPrescriptionModelArrayList;

    public PatientPrescriptionAdapter(Context context, ArrayList<MyPrescriptionModel> myPrescriptionModelArrayList) {
        this.context = context;
        this.myPrescriptionModelArrayList = myPrescriptionModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(com.app.fivestaruc.R.layout.my_prescription_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MyPrescriptionModel myPrescriptionModel = myPrescriptionModelArrayList.get(position);


        String date = convertDateFormat(myPrescriptionModel.getDateof(),context);
        holder.date1.setText(date);
        holder.date2.setText(date);
        holder.treatment.setText(myPrescriptionModel.getTreatment());
        holder.diagnosis.setText(myPrescriptionModel.getDiagnosis());
        holder.doctor.setText(myPrescriptionModel.getFirstName()+" "+myPrescriptionModel.getLastName());

        holder.btnViewDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(new Intent(context, ActivityPrescriptionDetails.class)
                        .putExtra("prescription_id",myPrescriptionModel.getId())
                        .putExtra("send_education",myPrescriptionModel.getSendEducationToPatient()));
            }
        });

    }

    public static String convertDateFormat(String inputDate,Context context) {
        // Define the input and output date formats
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("MM/dd/yyyy");

        String formattedDate = null;
        try {
            // Parse the input date string to a Date object
            Date date = inputFormat.parse(inputDate);
            // Format the Date object to the desired output format
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        return formattedDate;
    }

    @Override
    public int getItemCount() {
        return myPrescriptionModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date1,date2,treatment,diagnosis,doctor;
        Button btnViewDetails;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date1 = itemView.findViewById(com.app.fivestaruc.R.id.presp_date);
            date2 = itemView.findViewById(com.app.fivestaruc.R.id.presp_date2);
            treatment = itemView.findViewById(com.app.fivestaruc.R.id.presp_treatment);
            diagnosis = itemView.findViewById(com.app.fivestaruc.R.id.presp_diagnosis);
            doctor = itemView.findViewById(com.app.fivestaruc.R.id.presp_doctor);
            btnViewDetails = itemView.findViewById(com.app.fivestaruc.R.id.presp_btn_viewdetails);
        }
    }
}
