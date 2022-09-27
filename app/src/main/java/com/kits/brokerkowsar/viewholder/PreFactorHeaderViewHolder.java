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

public class PreFactorHeaderViewHolder extends RecyclerView.ViewHolder {

    public TextView fac_code;
    public TextView fac_date;
    public TextView fac_time;
    public TextView fac_kowsardate;
    public TextView fac_kowsarcode;
    public TextView fac_detail;
    public TextView fac_row;
    public TextView fac_count;
    public TextView fac_price;
    public TextView fac_customer;
    public TextView fac_status;
    public Button fac_history_good;
    public Button fac_send;
    public Button fac_dlt;
    public Button fac_customer_edit;
    public Button fac_explain_edit;
    public Button fac_excel;
    public Button fac_select;
    public Button fac_good_edit;
    public MaterialCardView fac_rltv;

    public PreFactorHeaderViewHolder(View itemView) {
        super(itemView);

        fac_code = itemView.findViewById(R.id.pf_header_code);
        fac_date = itemView.findViewById(R.id.pf_header_date);
        fac_time = itemView.findViewById(R.id.pf_header_time);
        fac_kowsardate = itemView.findViewById(R.id.pf_header_kowsardate);
        fac_row = itemView.findViewById(R.id.pf_header_row);
        fac_count = itemView.findViewById(R.id.pf_header_count);
        fac_price = itemView.findViewById(R.id.pf_header_price);
        fac_kowsarcode = itemView.findViewById(R.id.pf_header_kowsarcode);
        fac_detail = itemView.findViewById(R.id.pf_header_detail);
        fac_customer = itemView.findViewById(R.id.pf_header_customer);
        fac_history_good = itemView.findViewById(R.id.pf_header_histoy_good);
        fac_send = itemView.findViewById(R.id.pf_header_send);
        fac_dlt = itemView.findViewById(R.id.pf_header_dlt);
        fac_customer_edit = itemView.findViewById(R.id.pf_header_customer_edit);
        fac_explain_edit = itemView.findViewById(R.id.pf_header_explain_edit);
        fac_excel = itemView.findViewById(R.id.pf_header__xls);
        fac_select = itemView.findViewById(R.id.pf_header_select);
        fac_status = itemView.findViewById(R.id.pf_header_status);
        fac_good_edit = itemView.findViewById(R.id.pf_header_good_edit);

        fac_rltv = itemView.findViewById(R.id.pf_header);
    }




}