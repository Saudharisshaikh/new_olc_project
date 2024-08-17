package com.app.msu_cp.reliance.idtnote;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.app.msu_cp.R;
import com.app.msu_cp.util.DATA;

import java.util.ArrayList;

public class LvIDTnoteAdapter extends ArrayAdapter<IDTnoteBean> {

	Activity activity;
	ArrayList<IDTnoteBean> idTnoteBeans;


	public LvIDTnoteAdapter(Activity activity, ArrayList<IDTnoteBean> idTnoteBeans) {
		super(activity, R.layout.lv_idt_note_row, idTnoteBeans);

		this.activity = activity;
		this.idTnoteBeans = idTnoteBeans;
	}

	static class ViewHolder {
		TextView tvIdtRowProvName,tvIdtRowNotes;
		CheckBox cbIdtIsLocked;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;

		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lv_idt_note_row, null);

			viewHolder = new ViewHolder();

			viewHolder.tvIdtRowProvName = convertView.findViewById(R.id.tvIdtRowProvName);
			viewHolder.tvIdtRowNotes = convertView.findViewById(R.id.tvIdtRowNotes);
			viewHolder.cbIdtIsLocked = convertView.findViewById(R.id.cbIdtIsLocked);

			convertView.setTag(viewHolder);
			convertView.setTag(R.id.tvIdtRowProvName, viewHolder.tvIdtRowProvName);
			convertView.setTag(R.id.tvIdtRowNotes, viewHolder.tvIdtRowNotes);
			convertView.setTag(R.id.cbIdtIsLocked, viewHolder.cbIdtIsLocked);

			//viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvIdtRowProvName.setText(idTnoteBeans.get(position).first_name+ " "+idTnoteBeans.get(position).last_name);

		String styledText = "<font color='"+ DATA.APP_THEME_RED_COLOR +"'>Notes : </font>"+idTnoteBeans.get(position).notes;
		viewHolder.tvIdtRowNotes.setText(Html.fromHtml(styledText));

		viewHolder.cbIdtIsLocked.setChecked((idTnoteBeans.get(position).is_lock != null && idTnoteBeans.get(position).is_lock.equalsIgnoreCase("1")) ? true : false);

		return convertView;
	}

}
