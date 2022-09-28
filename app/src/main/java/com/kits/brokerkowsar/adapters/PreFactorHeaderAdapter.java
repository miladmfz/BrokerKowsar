package com.kits.brokerkowsar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.PreFactor;
import com.kits.brokerkowsar.viewholder.PreFactorHeaderViewHolder;

import java.util.ArrayList;


public class PreFactorHeaderAdapter extends RecyclerView.Adapter<PreFactorHeaderViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private final ArrayList<PreFactor> PreFactors;
    private final DatabaseHelper dbh;
    private final Action action;


    public PreFactorHeaderAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);

    }

    @NonNull
    @Override
    public PreFactorHeaderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header, parent, false);
        return new PreFactorHeaderViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final PreFactorHeaderViewHolder holder, final int position) {


        holder.bind(PreFactors.get(position));
        holder.Action(PreFactors.get(position), mContext, dbh, callMethod, action);


    }

    @Override
    public int getItemCount() {
        return PreFactors.size();
    }


}
