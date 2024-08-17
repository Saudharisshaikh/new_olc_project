package com.app.mhcsn_cp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.app.mhcsn_cp.R;
import com.app.mhcsn_cp.model.HomeCareFieldBean;
import com.app.mhcsn_cp.util.DATA;

import java.util.ArrayList;

public class HomecareFieldAdapter extends ArrayAdapter<HomeCareFieldBean> {

	Activity activity;
	ArrayList<HomeCareFieldBean> homeCareFieldBeens;
	boolean shouldShowTxtField = false;

	public HomecareFieldAdapter(Activity activity , ArrayList<HomeCareFieldBean> homeCareFieldBeens,boolean shouldShowTxtField) {
		super(activity, R.layout.homecare_field_row,homeCareFieldBeens);
		this.activity = activity;
		this.homeCareFieldBeens = homeCareFieldBeens;
		this.shouldShowTxtField = shouldShowTxtField;
	}

	static class ViewHolder {
		//EditText etHomeCareFrm;
		CheckBox cbHomeCareFrm;
	}

	ViewHolder viewHolder = null;
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {


		if(convertView == null) {

			LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.homecare_field_row, null);

			viewHolder = new ViewHolder();
			//viewHolder.etHomeCareFrm= (EditText) convertView.findViewById(R.id.etHomeCareFrm);
			viewHolder.cbHomeCareFrm = (CheckBox) convertView.findViewById(R.id.cbHomeCareFrm);

			convertView.setTag(viewHolder);
			//convertView.setTag(R.id.etHomeCareFrm, viewHolder.etHomeCareFrm);
			convertView.setTag(R.id.cbHomeCareFrm,viewHolder.cbHomeCareFrm);


		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		/*viewHolder.cbSkilledNursing.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				DATA.print("--checkchange listener ");
				skilledNursingCheckBeen.get(position).isChecked = isChecked;
			}
		});*/

		final EditText etHomeCareFrm = (EditText) convertView.findViewById(R.id.etHomeCareFrm);

		if(shouldShowTxtField){
			etHomeCareFrm.setVisibility(View.VISIBLE);

			etHomeCareFrm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(! hasFocus){
						String txtVal = etHomeCareFrm.getText().toString();
						homeCareFieldBeens.get(position).textField = txtVal;
						if(txtVal.isEmpty()){
							homeCareFieldBeens.get(position).isChecked = false;
							//viewHolder.cbHomeCareFrm.setChecked(false);
						}else{
							homeCareFieldBeens.get(position).isChecked = true;
							//viewHolder.cbHomeCareFrm.setChecked(true);
						}

						DATA.print("-- position on textchange: "+position);
						DATA.print("-- txtVal: "+txtVal);
						DATA.print("-- from list val : "+homeCareFieldBeens.get(position).textField+" isChecked: "+homeCareFieldBeens.get(position).isChecked);
						//notifyDataSetChanged();//commented this b/c it creates problem in some devices like in nexus tab and grandprime
					}
				}
			});

			etHomeCareFrm.setOnEditorActionListener(
					new EditText.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
							if (actionId == EditorInfo.IME_ACTION_SEARCH ||
									actionId == EditorInfo.IME_ACTION_DONE ||
									event.getAction() == KeyEvent.ACTION_DOWN &&
											event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
								//if (!event.isShiftPressed()) {
									// the user is done typing.
								    String txtVal = etHomeCareFrm.getText().toString();
									homeCareFieldBeens.get(position).textField = txtVal;
									if(txtVal.isEmpty()){
										homeCareFieldBeens.get(position).isChecked = false;
										//viewHolder.cbHomeCareFrm.setChecked(false);
									}else{
										homeCareFieldBeens.get(position).isChecked = true;
										//viewHolder.cbHomeCareFrm.setChecked(true);
									}

								DATA.print("-- position on textchange: "+position);
								DATA.print("-- txtVal: "+txtVal);
								DATA.print("-- from list val : "+homeCareFieldBeens.get(position).textField+" isChecked: "+homeCareFieldBeens.get(position).isChecked);
									//notifyDataSetChanged();//commented this b/c it creates problem in some devices like in nexus tab and grandprime
									return false; // consume.
								//}
							}
							return false; // pass on to other listeners.
						}
					});

		}else{
			etHomeCareFrm.setVisibility(View.GONE);
		}

		viewHolder.cbHomeCareFrm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				homeCareFieldBeens.get(position).isChecked = isChecked;
				if(!shouldShowTxtField){
					//notifyDataSetChanged();//commented this b/c it creates problem in some devices like in nexus tab and grandprime
				}
				DATA.print("--list rec ischecked : "+homeCareFieldBeens.get(position).isChecked);
			}
		});

		viewHolder.cbHomeCareFrm.setChecked(homeCareFieldBeens.get(position).isChecked);
		viewHolder.cbHomeCareFrm.setText(homeCareFieldBeens.get(position).value);
		etHomeCareFrm.setText(homeCareFieldBeens.get(position).textField);

		return convertView;
	}

}
