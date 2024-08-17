package com.app.OnlineCareUS_Pt.devices.fragments;

/**
 * Created by aftab on 09/06/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

import com.app.OnlineCareUS_Pt.R;
import com.app.OnlineCareUS_Pt.devices.beanclasses.BpHeartRateValuesBean;
import com.app.OnlineCareUS_Pt.devices.interfaces.GetWithingsBpDataInterface;



public class WithingsBpDataFragment extends Fragment {

    BarChart bpChart;

    ArrayList<BpHeartRateValuesBean> bpHeartRateList;

    GetWithingsBpDataInterface sGetDataInterface;


    public WithingsBpDataFragment() {
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        try {
            sGetDataInterface = (GetWithingsBpDataInterface) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement GetWithingsBpDataInterface Interface");
        }
    }


        @Override
        public void onResume() {
            super.onResume();
            if(sGetDataInterface != null){
                bpHeartRateList = sGetDataInterface.getDataList();
            }
        }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withings_bp, container, false);

        if(sGetDataInterface != null){
            bpHeartRateList = sGetDataInterface.getDataList();
        }

        bpChart = (BarChart) view.findViewById(R.id.bpChart);

        bpChart.setDescription("");
        bpChart.setDrawGridBackground(false);
        bpChart.setDrawBarShadow(false);
        bpChart.setDoubleTapToZoomEnabled(false);
        bpChart.setPinchZoom(false);

        XAxis xAxis = bpChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelRotationAngle(90);


        YAxis leftAxis = bpChart.getAxisLeft();
        leftAxis.setLabelCount(5, true);
        leftAxis.setSpaceTop(20f);


        YAxis rightAxis = bpChart.getAxisRight();
        rightAxis.setLabelCount(5,true);
        rightAxis.setSpaceTop(20f);



        if (bpHeartRateList.size() > 0) {

            ArrayList<String> dates = new ArrayList<String>();
            for (int j=0; j<bpHeartRateList.size(); j++) {

                dates.add(bpHeartRateList.get(j).getDate());

            }

            BarData data = new BarData(dates, getDataSet());
            bpChart.setData(data);
            bpChart.invalidate(); // refresh
            bpChart.animateY(800);

        }

        return view;
    }


    private ArrayList<BarDataSet> getDataSet() {

        ArrayList<BarDataSet> dataSets = null;
        ArrayList<BarEntry> systolicValues = new ArrayList<BarEntry>();
        ArrayList<BarEntry> dystolicValues = new ArrayList<BarEntry>();

        for (int i = 0; i<bpHeartRateList.size(); i++) {

            BarEntry v1e1 = new BarEntry(bpHeartRateList.get(i).getSystolic(), i); // Jan
            systolicValues.add(v1e1);

            BarEntry v2e1 = new BarEntry(bpHeartRateList.get(i).getDystolic(), i); // Jan
            dystolicValues.add(v2e1);
        }


        BarDataSet barDataSet1 = new BarDataSet(systolicValues, "Systolic");
        barDataSet1.setColor(Color.rgb(209, 90, 56));
        BarDataSet barDataSet2 = new BarDataSet(dystolicValues, "Dystolic");
//        barDataSet2.setColors(ColorTemplate.COLORFUL_COLORS);
        barDataSet2.setColor(Color.rgb(117, 117, 117
        ));

        dataSets = new ArrayList<BarDataSet>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);
        return dataSets;
    }


}