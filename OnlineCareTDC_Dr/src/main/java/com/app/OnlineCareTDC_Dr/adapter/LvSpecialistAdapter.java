package com.app.OnlineCareTDC_Dr.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.app.OnlineCareTDC_Dr.R;
import com.app.OnlineCareTDC_Dr.model.PrescriptionBean;
import com.app.OnlineCareTDC_Dr.model.SpecialityModel;
import com.app.OnlineCareTDC_Dr.util.DATA;

import java.util.ArrayList;

public class LvSpecialistAdapter extends ArrayAdapter<SpecialityModel> {

    Activity activity;

    public LvSpecialistAdapter(Activity activity) {
        super(activity, R.layout.lv_specialistrow, DATA.allSpecialities);

        this.activity = activity;
    }
    static class ViewHolder {

        TextView tvSpecialistName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if(convertView == null) {

            LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.lv_specialistrow, null);

            viewHolder = new ViewHolder();

            viewHolder.tvSpecialistName = (TextView) convertView.findViewById(R.id.tvSpecialistName);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.tvSpecialistName, viewHolder.tvSpecialistName);

        }
        else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.tvSpecialistName.setText(DATA.allSpecialities.get(position).specialityName);
        return convertView;
    }
}
