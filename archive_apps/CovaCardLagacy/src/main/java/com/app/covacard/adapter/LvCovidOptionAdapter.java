package com.app.covacard.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.app.covacard.ActivityCovidTest2;
import com.app.covacard.R;
import com.app.covacard.model.CovidFormItemBean;

import java.util.List;

public class LvCovidOptionAdapter extends ArrayAdapter<CovidFormItemBean.CovidOptionBean> {

    Activity activity;
    CovidFormItemBean covidFormItemBean;//from parent listview
    List<CovidFormItemBean.CovidOptionBean> options;

    //public static boolean validateFlag = false;

    public LvCovidOptionAdapter(Activity activity, CovidFormItemBean covidFormItemBean) {
        super(activity, R.layout.lv_covid_option_row, covidFormItemBean.options);

        this.activity = activity;
        this.options = covidFormItemBean.options;
        this.covidFormItemBean = covidFormItemBean;
    }

    static class ViewHolder {
        LinearLayout rootCell;
        RadioButton rbOption;

        //TextView tvOptionDesc;

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
            convertView = layoutInflater.inflate(R.layout.lv_covid_option_row, null);

            viewHolder = new ViewHolder();

            viewHolder.rbOption = convertView.findViewById(R.id.rbOption);
            viewHolder.rootCell = convertView.findViewById(R.id.rootCell);

            convertView.setTag(viewHolder);
            convertView.setTag(R.id.rbOption, viewHolder.rbOption);
            convertView.setTag(R.id.rootCell, viewHolder.rootCell);


            //viewHolder.radios = new RadioButton[]{viewHolder.rb0, viewHolder.rb1, viewHolder.rb2, viewHolder.rb3};

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.rbOption.setText(options.get(position).optionTxt);
        viewHolder.rbOption.setChecked(options.get(position).isSelected);

        viewHolder.rbOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetAllAndSelect(position);
            }
        });

		/*int vis = ActivityAdlForm.formFlagA == 1 ? View.VISIBLE : View.GONE;
		viewHolder.tvOptionDesc.setVisibility(vis);
		String txt = position == 0 ? "DEPENDENCE: (0 POINTS) WITH supervision, direction, personal assistance or total care" :
				"INDEPENDENCE: (1 POINT) NO supervision, direction or personal assistance";
		viewHolder.tvOptionDesc.setText(txt);

		if(activity instanceof ActivityAdlForm){
			boolean clickAble = !((ActivityAdlForm) activity).isReadOnly;
			viewHolder.rbOption.setClickable(clickAble);
			viewHolder.rbOption.setLongClickable(clickAble);
		}*/


        return convertView;
    }


    public void resetAllAndSelect(int pos){
        for (int i = 0; i < options.size(); i++) {
            options.get(i).isSelected = false;
        }
        options.get(pos).isSelected = true;
        covidFormItemBean.isAnswered = true;

        covidFormItemBean.selectedAns = options.get(pos).optionTxt;

		/*if(ActivityAdlForm.formFlagA == 1){
			adlFormBean.score = pos;
		}else if(ActivityAdlForm.formFlagA == 2){
			try {
				adlFormBean.score = adlFormBean.scores.get(pos);
				adlFormBean.selectedAns = options.get(pos).optionTxt;
			}catch (Exception e){
				e.printStackTrace();
				adlFormBean.score = pos;
			}
		}*/
        notifyDataSetChanged();

		/*if(activity instanceof ActivityAdlForm){
			((ActivityAdlForm) activity).sumUpScore();
		}*/

		if(covidFormItemBean.label.contains("Symptomatic as defined by CDC?")){
		    if(options.get(pos).optionTxt.contains("Yes")){
                ((ActivityCovidTest2) activity).covidFormItemBeans.get(5).isCellVisible = true;
            }else {
                ((ActivityCovidTest2) activity).covidFormItemBeans.get(5).isCellVisible = false;
            }
            ((ActivityCovidTest2) activity).lvCovidFormAdapter.notifyDataSetChanged();
        }
    }
}
