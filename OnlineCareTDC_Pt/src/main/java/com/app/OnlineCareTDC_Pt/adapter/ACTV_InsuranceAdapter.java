package com.app.OnlineCareTDC_Pt.adapter;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import com.app.OnlineCareTDC_Pt.model.PayerBean;

import java.util.ArrayList;
import java.util.List;


public class ACTV_InsuranceAdapter extends ArrayAdapter<PayerBean> {

    // Note: this adapter is used to display suggessions from local storage (recieved in GetAllSubmissionsForLookups API) (offline mode. app is offline).
    Context context;
    //int resource, textViewResourceId;
    List<PayerBean> items, tempItems, suggestions;

    public ACTV_InsuranceAdapter(Context context, int resource, List<PayerBean> items) {
        //super(context, resource, textViewResourceId, items);
        super(context, resource);
        this.context = context;
        //this.resource = resource;
        //this.textViewResourceId = textViewResourceId;
        this.items = items;
        tempItems = new ArrayList<PayerBean>(items); // this makes the difference.
        suggestions = new ArrayList<PayerBean>();
    }

    /*public void setData(List<LookupACTV_DataBean> list) {
        this.items.clear();
        this.items.addAll(list);
    }*/

    public PayerBean getObject(int position) {
        //return items.get(position);//this was rtical bugg. wrong item was returmed : GM
        return suggestions.get(position);
    }

    /*@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.row_people, parent, false);
        }
        People people = items.get(position);
        if (people != null) {
            TextView lblName = (TextView) view.findViewById(R.id.lbl_name);
            if (lblName != null)
                lblName.setText(people.getName());
        }
        return view;
    }*/

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    /**
     * Custom Filter implementation for custom suggestions we provide.
     */
    Filter nameFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            String str = ((PayerBean) resultValue).payer_name;
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (PayerBean lookupACTV_dataBean : tempItems) {
                    if (lookupACTV_dataBean.payer_name.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        suggestions.add(lookupACTV_dataBean);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<PayerBean> filterList = (ArrayList<PayerBean>) results.values;
            if (results != null && results.count > 0) {
                clear();
                /*for (LookupACTV_DataBean people : filterList) {
                    add(people);
                    notifyDataSetChanged();
                }*/
                addAll(filterList);
                notifyDataSetChanged();
            }
        }
    };
}
