package com.kits.brokerkowsar.viewholder;
;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;

public class GoodItemViewHolder extends RecyclerView.ViewHolder {

    public  LinearLayoutCompat mainline;
    public  ImageView img;
    public  MaterialCardView rltv;
    public  Button btnadd;

    public GoodItemViewHolder(View itemView) {
        super(itemView);

        mainline = itemView.findViewById(R.id.prosearch_mainline);
        img = itemView.findViewById(R.id.good_prosearch_img);
        rltv = itemView.findViewById(R.id.good_prosearch);
        btnadd = itemView.findViewById(R.id.good_prosearch_btn);
    }




}