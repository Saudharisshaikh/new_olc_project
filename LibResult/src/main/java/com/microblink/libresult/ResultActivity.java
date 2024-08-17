package com.microblink.libresult;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.microblink.activity.ScanActivity;
import com.microblink.recognizers.RecognitionResults;

/**
 * Created by igor on 12/2/14.
 */
public class ResultActivity extends FragmentActivity {

    FragmentPagerAdapter adapterViewPager;

    //public static JSONArray resultJsonArray;


    @SuppressLint("InlinedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.result_menu);
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new ResultFragmentAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
    }

    public void buttonClickHandler(View view) {
        final int id = view.getId();
        if (id == R.id.btnYesPay) {
            finish();
        } else if (id == R.id.btnNoBack) {
            finish();
        }
    }

    class ResultFragmentAdapter extends FragmentPagerAdapter {

        RecognitionResults mResults = getIntent().getExtras().getParcelable(
                ScanActivity.EXTRAS_RECOGNITION_RESULTS);

        public ResultFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ResultFragment.newInstance(mResults.getRecognitionResults()[position]);
        }

        @Override
        public int getCount() {
            if (mResults.getRecognitionResults() == null) {
                return 0;
            } else {
                return mResults.getRecognitionResults().length;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mResults.getRecognitionResults()[position].getTitle();
        }
    }
}
