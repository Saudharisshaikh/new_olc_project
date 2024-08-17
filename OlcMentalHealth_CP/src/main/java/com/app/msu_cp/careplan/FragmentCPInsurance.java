package com.app.msu_cp.careplan;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.app.msu_cp.R;

/**
 * Created by Engr G M on 7/10/2018.
 */

public class FragmentCPInsurance extends Fragment {

    //xml layout is same of medication fragment    === G.M

    Activity parentActivity;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (Activity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(parentActivity == null){
            parentActivity = getActivity();
        }
    }

    ListView lvCPMed;
    TextView tvNoData;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_cp_med, container, false);

        lvCPMed = (ListView) rootView.findViewById(R.id.lvCPMed);
        tvNoData = (TextView) rootView.findViewById(R.id.tvNoData);

        lvCPMed.setAdapter(new CPInsuranceAdapter(parentActivity,ActivityCarePlanDetail.cp_insuranceBeans));

        if(ActivityCarePlanDetail.cp_insuranceBeans.isEmpty()){
            tvNoData.setVisibility(View.VISIBLE);
        }else {
            tvNoData.setVisibility(View.GONE);
        }

        rootView.findViewById(R.id.layBottom1).setVisibility(View.GONE);


        return rootView;
    }
}
