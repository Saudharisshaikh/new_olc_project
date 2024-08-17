/*
 * Copyright 2014 Magnus Woxblom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.mhcsn_cp.reliance.medication;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.mhcsn_cp.R;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.ArrayList;

class ItemAdapter extends DragItemAdapter<Pair<Long, MedicationBean>, ItemAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;

    Activity activity;

    ItemAdapter(ArrayList<Pair<Long, MedicationBean>> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Activity activity) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        setItemList(list);

        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.tvMedName.setText(mItemList.get(position).second.name);
        holder.tvMedStrength.setText(mItemList.get(position).second.strength);
        holder.tvMedInstructions.setText(mItemList.get(position).second.directions);

        holder.itemView.setTag(mItemList.get(position));

        int resId = mItemList.get(position).second.isSelected ? R.drawable.cust_border_grey_outline2:R.drawable.cust_border_white_outline;
        holder.item_layout.setBackgroundResource(resId);


        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnView:
                        ((ActivityMedicationList) activity).showMedicationDetailDialog(mItemList.get(position).second,1);
                        break;
                    case R.id.btnEdit:
                        ((ActivityMedicationList) activity).showMedicationDetailDialog(mItemList.get(position).second,2);
                        break;
                    case R.id.btnRenew:
                        ((ActivityMedicationList) activity).showMedicationDetailDialog(mItemList.get(position).second,3);
                        break;
                }
            }
        };
        holder.btnView.setOnClickListener(onClickListener);
        holder.btnEdit.setOnClickListener(onClickListener);
        holder.btnRenew.setOnClickListener(onClickListener);


        holder.cbCheckMed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mItemList.get(position).second.isSelected = isChecked;
                ((ActivityMedicationList) activity).enableStopBtn();
                notifyDataSetChanged();
            }
        });
        holder.cbCheckMed.setChecked(mItemList.get(position).second.isSelected);
    }

    @Override
    public long getUniqueItemId(int position) {
        return mItemList.get(position).first;
    }

    class ViewHolder extends DragItemAdapter.ViewHolder {
        TextView tvMedName, tvMedStrength,tvMedInstructions;
        TextView btnView,btnEdit,btnRenew;
        CheckBox cbCheckMed;
        LinearLayout item_layout;

        ViewHolder(final View itemView) {
            super(itemView, mGrabHandleId, mDragOnLongPress);
            tvMedName = (TextView) itemView.findViewById(R.id.tvMedName);
            tvMedStrength = (TextView) itemView.findViewById(R.id.tvMedStrength);
            tvMedInstructions = (TextView) itemView.findViewById(R.id.tvMedInstructions);

            btnView = (TextView) itemView.findViewById(R.id.btnView);
            btnEdit = (TextView) itemView.findViewById(R.id.btnEdit);
            btnRenew = (TextView) itemView.findViewById(R.id.btnRenew);

            cbCheckMed = itemView.findViewById(R.id.cbCheckMed);

            item_layout = itemView.findViewById(R.id.item_layout);
        }

        @Override
        public void onItemClicked(View view) {
            Toast.makeText(view.getContext(), "Item clicked", Toast.LENGTH_SHORT).show();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            Toast.makeText(view.getContext(), "Item long clicked", Toast.LENGTH_SHORT).show();
            return true;
        }
    }
}
