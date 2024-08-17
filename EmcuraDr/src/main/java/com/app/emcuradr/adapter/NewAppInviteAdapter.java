package com.app.emcuradr.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.emcuradr.ActivityAppInvite;
import com.app.emcuradr.R;
import com.app.emcuradr.model.AppBean2;
import com.app.emcuradr.util.AppInviteListener;
import com.app.emcuradr.util.DATA;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class NewAppInviteAdapter extends RecyclerView.Adapter<NewAppInviteAdapter.ViewHolder> {


    ArrayList<AppBean2> arrayList;
    Context context;

    public AppInviteListener appInviteListener;





    public NewAppInviteAdapter(ArrayList<AppBean2> arrayList, Context context,AppInviteListener appInviteListener) {
        this.appInviteListener = appInviteListener;
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public NewAppInviteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.app_invite_item, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull NewAppInviteAdapter.ViewHolder holder, int position) {

        AppBean2 appBean2 = arrayList.get(position);

        holder.appText.setText(appBean2.appName);
        Glide.with(context)
                .load(appBean2.getDrawableSelecterID())
                //.placeholder(com.darsh.multipleimageselect.R.drawable.image_placeholder)
                .into(holder.appImage);


        if(DATA.appBean_GM == appBean2){
            holder.cardView.setCardBackgroundColor(context.getResources().getColor(R.color.theme_red));
            holder.appText.setTextColor(Color.WHITE);
            holder.appImage.setColorFilter(ContextCompat.getColor(context, android.R.color.white), android.graphics.PorterDuff.Mode.SRC_IN);

        }else {
            holder.cardView.setCardBackgroundColor(Color.WHITE);
            holder.appText.setTextColor(Color.BLACK);
            holder.appImage.setColorFilter(ContextCompat.getColor(context, android.R.color.black), PorterDuff.Mode.SRC_IN);
        }


        /*holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedPosition = -1;

                if(context instanceof ActivityAppInvite){
                    ((ActivityAppInvite) context).androidAdapter.notifyDataSetChanged();
                    ((ActivityAppInvite) context).iosAdapter.notifyDataSetChanged();
                    ((ActivityAppInvite) context).webAdapter.notifyDataSetChanged();
                }

                selectedPosition = position;
                //notifyDataSetChanged();
                appInviteListener.selectedAppInvite(appBean2);

                if(selectedPosition == position){
                    holder.cardView.setCardBackgroundColor(Color.GREEN);
                    holder.appText.setTextColor(Color.WHITE);
                }else {
                    holder.cardView.setCardBackgroundColor(Color.WHITE);
                    holder.appText.setTextColor(Color.BLACK);
                }

            }
        });*/

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView appText;
        ImageView appImage;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            appImage = itemView.findViewById(R.id.app_image);
            appText = itemView.findViewById(R.id.app_text);
            cardView = itemView.findViewById(R.id.cardlayout);

        }
    }
}
