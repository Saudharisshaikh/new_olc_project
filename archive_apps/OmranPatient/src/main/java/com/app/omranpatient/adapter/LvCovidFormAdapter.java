package com.app.omranpatient.adapter;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.omranpatient.R;
import com.app.omranpatient.model.CovidFormItemBean;
import com.app.omranpatient.util.DatePickerFragment;
import com.app.omranpatient.util.ExpandableHeightListView;

import java.util.ArrayList;

public class LvCovidFormAdapter extends ArrayAdapter<CovidFormItemBean> {

	Activity activity;
	ArrayList<CovidFormItemBean> covidFormItemBeans;

	public static boolean validateFlag = false;

	public LvCovidFormAdapter(Activity activity, ArrayList<CovidFormItemBean> covidFormItemBeans) {
		super(activity, R.layout.lv_covid_form_row, covidFormItemBeans);

		this.activity = activity;
		this.covidFormItemBeans = covidFormItemBeans;
	}

	static class ViewHolder {
		LinearLayout rootCell;
		TextView tvQuestion; //, tvQuesDesc;
		ExpandableHeightListView lvOptions;
        EditText etDateInput;

		/*TextView tvQuestion;
		RadioGroup rgPHQOptions;
		RadioButton rb0,rb1,rb2,rb3;
		RadioButton [] radios;*/
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_covid_form_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvQuestion = convertView.findViewById(R.id.tvQuestion);
			//viewHolder.tvQuesDesc = convertView.findViewById(R.id.tvQuesDesc);
			viewHolder.lvOptions = convertView.findViewById(R.id.lvOptions);
			viewHolder.rootCell = convertView.findViewById(R.id.rootCell);
            viewHolder.etDateInput = convertView.findViewById(R.id.etDateInput);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvQuestion, viewHolder.tvQuestion);
			//convertView.setTag(R.id.tvQuesDesc, viewHolder.tvQuesDesc);
			convertView.setTag(R.id.lvOptions, viewHolder.lvOptions);
			convertView.setTag(R.id.rootCell, viewHolder.rootCell);
            convertView.setTag(R.id.etDateInput, viewHolder.etDateInput);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}


		viewHolder.tvQuestion.setText(covidFormItemBeans.get(position).label);
		viewHolder.lvOptions.setAdapter(new LvCovidOptionAdapter(activity, covidFormItemBeans.get(position)));
		viewHolder.lvOptions.setExpanded(true);

		//int vis = ActivityAdlForm.formFlagA == 1 ? View.VISIBLE : View.GONE;
		//viewHolder.tvQuesDesc.setVisibility(vis);


		if(validateFlag){
			int drawableId = covidFormItemBeans.get(position).isAnswered ? R.drawable.cust_border_white_outline : R.drawable.cust_border_white_outline_red;
			//viewHolder.rootCell.setBackgroundColor(activity.getResources().getColor(colorId));
			viewHolder.rootCell.setBackgroundResource(drawableId);
		}




		LvCovidFormAdapter.ViewHolder finalViewHolder = viewHolder;
        viewHolder.etDateInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment(finalViewHolder.etDateInput);
                newFragment.show(((AppCompatActivity)activity).getSupportFragmentManager(), "datePicker");
            }
        });


        int vis = position == 5 ? View.VISIBLE : View.GONE;
        viewHolder.etDateInput.setVisibility(vis);

        int vis2 = covidFormItemBeans.get(position).isCellVisible ? View.VISIBLE : View.GONE;
        viewHolder.rootCell.setVisibility(vis2);

		return convertView;
	}
}
