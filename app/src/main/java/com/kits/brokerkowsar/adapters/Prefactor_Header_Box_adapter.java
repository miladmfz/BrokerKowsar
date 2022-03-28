package com.kits.brokerkowsar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.PreFactor;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Prefactor_Header_Box_adapter extends RecyclerView.Adapter<Prefactor_Header_Box_adapter.facViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;


    public Prefactor_Header_Box_adapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header_box, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {

        PreFactor preFactordetail = PreFactors.get(position);


        holder.fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorDate"))));
        holder.fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorTime"))));
        holder.fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode"))));
        holder.fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorExplain"))));
        holder.fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("Customer"))));
        holder.fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("RowCount"))));
        holder.fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("SumAmount"))));
        holder.fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(preFactordetail.getPreFactorFieldValue("SumPrice"))))));


        holder.fac_rltv.setOnClickListener(v -> {

            final String prefactor_code = "PreFactorCode";
            callMethod.EditString(prefactor_code, preFactordetail.getPreFactorFieldValue("PreFactorCode"));
            callMethod.showToast( "فاکتور مورد نظر انتخاب شد");
            ((Activity) mContext).overridePendingTransition(0, 0);
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, 0);


        });


    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }

    static class facViewHolder extends RecyclerView.ViewHolder {
        private final TextView fac_code;
        private final TextView fac_date;
        private final TextView fac_time;
        private final TextView fac_detail;
        private final TextView fac_row;
        private final TextView fac_count;
        private final TextView fac_price;
        private final TextView fac_customer;

        MaterialCardView fac_rltv;

        facViewHolder(View itemView) {
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


}
