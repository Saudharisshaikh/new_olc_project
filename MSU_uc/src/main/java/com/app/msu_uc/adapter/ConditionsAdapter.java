package com.app.msu_uc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;

import com.app.msu_uc.R;
import com.app.msu_uc.model.ConditionsModel;

import java.util.List;

public class ConditionsAdapter extends ArrayAdapter<ConditionsModel> {

    private LayoutInflater inflater;

    public ConditionsAdapter(Context context, List<ConditionsModel> conditionsModels) {
        super(context, R.layout.listitem_singlechoice, conditionsModels);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.listitem_singlechoice, parent, false);
        }

        CheckedTextView textView = view.findViewById(android.R.id.text1);
        ImageView imageView = view.findViewById(R.id.imageView);

        ConditionsModel conditionsModel = getItem(position);

        if (conditionsModel != null) {
            textView.setText("");
            if (conditionsModel.conditionName.equalsIgnoreCase("Anxiety Health")) {
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_anxiety));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("substance abuse")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_substance_abuse));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("eating disorder")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_eating_disorder));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("stress")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_stress));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("ocd")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ocd_form));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("depression analysis")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_depression));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("focus concentration")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_focus_conc));
            }else if (conditionsModel.conditionName.equalsIgnoreCase("panic attack")){
                imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_panic_attack));
            }
        }

        return view;
    }
}
