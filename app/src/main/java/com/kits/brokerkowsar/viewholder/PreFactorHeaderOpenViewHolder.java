package com.kits.brokerkowsar.viewholder;
;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;

public class PreFactorHeaderOpenViewHolder extends RecyclerView.ViewHolder {

    public TextView fac_code;
    public TextView fac_date;
    public TextView fac_time;
    public TextView fac_detail;
    public TextView fac_row;
    public TextView fac_count;
    public TextView fac_price;
    public TextView fac_customer;

    public MaterialCardView fac_rltv;

    public PreFactorHeaderOpenViewHolder(View itemView) {
        super(itemView);
        fac_code = itemView.findViewById(R.id.pf_header_box_code);
        fac_date = itemView.findViewById(R.id.pf_header_box_date);
        fac_time = itemView.findViewById(R.id.pf_header_box_time);
        fac_row = itemView.findViewById(R.id.pf_header_box_row);
        fac_count = itemView.findViewById(R.id.pf_header_box_count);
        fac_price = itemView.findViewById(R.id.pf_header_box_price);
        fac_detail = itemView.findViewById(R.id.pf_header_box_detail);
        fac_customer = itemView.findViewById(R.id.pf_header_box_customer);


        fac_rltv = itemView.findViewById(R.id.pf_header_box);

    }




}