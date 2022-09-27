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

public class GoodBasketViewHolder extends RecyclerView.ViewHolder {

    public TextView goodnameTextView;
    public TextView maxsellpriceTextView;
    public TextView priceTextView;
    public TextView total;
    public TextView maxtotal;
    public TextView amount;
    public TextView good_buy_shortage_f1;
    public TextView good_buy_shortage_f2;
    public TextView offer;
    public Button btndlt;
    public ImageView img;
    public MaterialCardView rltv;


    public GoodBasketViewHolder(View itemView) {
        super(itemView);

        goodnameTextView = itemView.findViewById(R.id.good_buy_name);
        maxsellpriceTextView = itemView.findViewById(R.id.good_buy_maxprice);
        priceTextView = itemView.findViewById(R.id.good_buy_price);
        amount = itemView.findViewById(R.id.good_buy_amount);
        good_buy_shortage_f1 = itemView.findViewById(R.id.good_buy_shortage_false1);
        good_buy_shortage_f2 = itemView.findViewById(R.id.good_buy_shortage_false2);
        total = itemView.findViewById(R.id.good_buy_total);
        maxtotal = itemView.findViewById(R.id.good_buy_maxtotal);
        img = itemView.findViewById(R.id.good_buy_img);
        btndlt = itemView.findViewById(R.id.good_buy_btndlt);
        offer = itemView.findViewById(R.id.good_buy_offer);


        rltv = itemView.findViewById(R.id.good_buy);
    }




}