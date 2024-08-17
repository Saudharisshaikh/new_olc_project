package com.app.fivestaruc.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.app.fivestaruc.model.IcdCodeBean;

import java.util.ArrayList;
import java.util.List;

public class IcdCodesAdapter extends ArrayAdapter<IcdCodeBean> implements Filterable {
    private List<IcdCodeBean> mlistData;

    public IcdCodesAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mlistData = new ArrayList<>();
    }

    public void setData(List<IcdCodeBean> list) {
        mlistData.clear();
        mlistData.addAll(list);
    }

    @Override
    public int getCount() {
        return mlistData.size();
    }

    @Nullable
    @Override
    public IcdCodeBean getItem(int position) {
        return mlistData.get(position);
    }

    /**
     * Used to Return the full object directly from adapter.
     *
     * @param position
     * @return
     */
    public IcdCodeBean getObject(int position) {
        return mlistData.get(position);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mlistData;
                    filterResults.count = mlistData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}
