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

public class CustomerViewHolder extends RecyclerView.ViewHolder {

    public TextView cus_code;
    public TextView cus_name;
    public TextView cus_manage;
    public TextView cus_phone;
    public TextView cus_addres;
    public TextView cus_bes;
    public MaterialCardView fac_rltv;

    public CustomerViewHolder(View itemView) {
        super(itemView);
        cus_code = itemView.findViewById(R.id.customer_code);
        cus_name = itemView.findViewById(R.id.customer_name);
        cus_manage = itemView.findViewById(R.id.customer_manage);
        cus_phone = itemView.findViewById(R.id.customer_phone);
        cus_addres = itemView.findViewById(R.id.customer_addres);
        cus_bes = itemView.findViewById(R.id.customer_bes);
        fac_rltv = itemView.findViewById(R.id.customer);
    }




}