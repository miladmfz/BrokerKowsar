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

public class PreFactorHeaderOpenAdapter extends RecyclerView.Adapter<PreFactorHeaderOpenViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<PreFactor> PreFactors;


    public PreFactorHeaderOpenAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
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


        holder.bind( PreFactors.get(position),  mContext,  callMethod);

    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }




}
