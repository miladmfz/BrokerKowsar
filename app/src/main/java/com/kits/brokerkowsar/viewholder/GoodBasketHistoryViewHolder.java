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

public class GoodBasketHistoryViewHolder extends RecyclerView.ViewHolder {


    public TextView goodnameTextView;
    public TextView priceTextView;
    public TextView total;
    public TextView amount;
    public TextView code;
    public TextView maxsellpriceTextView;
    public TextView maxtotal;
    public ImageView img;
    public MaterialCardView rltv;


    public GoodBasketHistoryViewHolder(View itemView) {
        super(itemView);
        goodnameTextView = itemView.findViewById(R.id.good_buy_history_name);
        maxsellpriceTextView = itemView.findViewById(R.id.good_buy_history_maxprice);
        maxtotal = itemView.findViewById(R.id.good_buy_history_maxtotal);
        priceTextView = itemView.findViewById(R.id.good_buy_history_price);
        total = itemView.findViewById(R.id.good_buy_history_total);
        amount = itemView.findViewById(R.id.good_buy_history_amount);
        code = itemView.findViewById(R.id.good_buy_history_code);
        img = itemView.findViewById(R.id.good_buy_history_img);
        rltv = itemView.findViewById(R.id.good_buy_history);
    }



}