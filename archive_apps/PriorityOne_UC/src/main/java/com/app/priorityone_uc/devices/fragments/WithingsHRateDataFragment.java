package com.app.priorityone_uc.devices.fragments;

/**
 * Created by aftab on 09/06/2016.
 */

import android.content.Context;
import android.content.res.Resources;
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

import com.app.priorityone_uc.R;
import com.app.priorityone_uc.devices.beanclasses.BpHeartRateValuesBean;
import com.app.priorityone_uc.devices.interfaces.GetWithingsBpDataInterface;



public class WithingsHRateDataFragment extends Fragment {

    BarChart heartRateChart;
    GetWithingsBpDataInterface sGetDataInterface;

    ArrayList<BpHeartRateValuesBean> heartRateValuesList;


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
            heartRateValuesList = sGetDataInterface.getDataList();
        }
    }



    public WithingsHRateDataFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_withings_hrate, container, false);


        if(sGetDataInterface != null){
            heartRateValuesList = sGetDataInterface.getDataList();
        }

        heartRateChart = (BarChart) view.findViewById(R.id.heartRateChart);

        heartRateChart.setDescription("");
        heartRateChart.setDrawGridBackground(false);
        heartRateChart.setDrawBarShadow(false);
        heartRateChart.setDoubleTapToZoomEnabled(false);
        heartRateChart.setPinchZoom(false);

        XAxis xAxis = heartRateChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelRotationAngle(90);

        YAxis leftAxis = heartRateChart.getAxisLeft();
        leftAxis.setLabelCount(5, true);
        leftAxis.setSpaceTop(20f);


        YAxis rightAxis = heartRateChart.getAxisRight();
        rightAxis.setLabelCount(5,true);
        rightAxis.setSpaceTop(20f);

        if (heartRateValuesList.size() > 0) {

            ArrayList<String> dates = new ArrayList<String>();
            for (int j=0; j<heartRateValuesList.size(); j++) {

                dates.add(heartRateValuesList.get(j).getDate());
            }

            heartRateChart.setData(generateDataBar());
            heartRateChart.invalidate(); // refresh
            heartRateChart.animateY(800);

        }

        return view;
    }


    private BarData generateDataBar() {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();


//        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/GeosansLight.ttf");
        for (int i = 0; i < heartRateValuesList.size(); i++)
        {
            BpHeartRateValuesBean bd = heartRateValuesList.get(i);
            entries.add(new BarEntry(bd.getHeartRate(), i));
        }

        ArrayList<String> dates = new ArrayList<String>();
        for (int j=0; j<heartRateValuesList.size(); j++) {

            dates.add(heartRateValuesList.get(j).getDate());


        }

        BarDataSet d = new BarDataSet(entries, "Heart Rate Values");
//        d.setValueTypeface(typeface);
        d.setBarSpacePercent(25f);
        Resources r = this.getResources();
        d.setColors(new int[]{Color.rgb(209, 90, 56)
                ,Color.rgb(117, 117, 117)});
        d.setHighLightAlpha(255);

        return new BarData(dates,d);
    }

}