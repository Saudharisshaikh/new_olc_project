package com.app.emcuradr.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.emcuradr.R;
import com.app.emcuradr.model.DiscNoteBean;
import com.app.emcuradr.util.DATA;

import java.util.ArrayList;

public class DischargeNotesAdapter extends ArrayAdapter<DiscNoteBean> {

    Activity activity;
    ArrayList<DiscNoteBean> discNoteBeans;
    SharedPreferences prefs;

    public DischargeNotesAdapter(Activity activity, ArrayList<DiscNoteBean> discNoteBeans) {
        super(activity, R.layout.item_layout_disc_notes, discNoteBeans);

        this.activity = activity;
        this.discNoteBeans = discNoteBeans;
        prefs = activity.getSharedPreferences(DATA.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

    }

    static class ViewHolder {
        TextView tvBillingDrname,tvDischargeNotes;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        //if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.item_layout_disc_notes, null);

            viewHolder = new ViewHolder();


            viewHolder.tvBillingDrname = convertView.findViewById(R.id.tvBillingDrname);
            viewHolder.tvDischargeNotes = convertView.findViewById(R.id.tvDischargeNotes);

            convertView.setTag(R.id.tvBillingDrname, viewHolder.tvBillingDrname);
            convertView.setTag(R.id.tvDischargeNotes, viewHolder.tvDischargeNotes);

            viewHolder.tvBillingDrname.setText("Discharge Notes by: \n"+discNoteBeans.get(position).dr_name + " \non "+discNoteBeans.get(position).notes_date
            + " \nPatient: "+discNoteBeans.get(position).patient_name);

            viewHolder.tvDischargeNotes.setText(discNoteBeans.get(position).notes.note_text);
        //}

        return convertView;
    }
}
