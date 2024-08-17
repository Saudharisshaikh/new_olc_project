package com.paging.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


public class LoadingView extends LinearLayout {

    public LoadingView(Context context) {
        super(context);
        init();
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
       View loadingCellView = inflate(getContext(), R.layout.loading_view, this);
        ProgressBar progressBar2 = loadingCellView.findViewById(R.id.progressBar2);
        progressBar2.getIndeterminateDrawable().setColorFilter(getContext().getResources().getColor(R.color.paginglistview_primary), android.graphics.PorterDuff.Mode.MULTIPLY);
    }


}
