package com.kits.brokerkowsar.viewholder;


import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;

public class GroupLabelViewHolder extends RecyclerView.ViewHolder {

    public TextView grpname;
    public ImageView img;
    public FrameLayout rltv;

    public GroupLabelViewHolder(View itemView) {
        super(itemView);

        grpname = itemView.findViewById(R.id.grp_vlist_detail_name);
        rltv = itemView.findViewById(R.id.grp_vlist_detail);
        img = itemView.findViewById(R.id.grp_vlist_detail_image);
    }




}