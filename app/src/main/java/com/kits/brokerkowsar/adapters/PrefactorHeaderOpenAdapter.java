package com.kits.brokerkowsar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.PreFactor;
import com.kits.brokerkowsar.viewholder.PreFactorHeaderOpenViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class PrefactorHeaderOpenAdapter extends RecyclerView.Adapter<PreFactorHeaderOpenViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;


    public PrefactorHeaderOpenAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
    }

    @NonNull
    @Override
    public PreFactorHeaderOpenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header_open, parent, false);
        return new PreFactorHeaderOpenViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PreFactorHeaderOpenViewHolder holder, final int position) {



        holder.fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorDate"))));
        holder.fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorTime"))));
        holder.fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"))));
        holder.fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorExplain"))));
        holder.fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("Customer"))));
        holder.fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("RowCount"))));
        holder.fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("SumAmount"))));
        holder.fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("SumPrice"))))));


        holder.fac_rltv.setOnClickListener(v -> {

            final String prefactor_code = "PreFactorCode";
            callMethod.EditString(prefactor_code, PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
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




}
