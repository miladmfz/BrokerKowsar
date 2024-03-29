package com.kits.brokerkowsar.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.Customer;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.viewholder.CustomerViewHolder;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder> {
    Context mContext;
    CallMethod callMethod;
    ArrayList<Customer> customers;
    String edit;
    String factor_target;
    DatabaseHelper dbh;
    Action action;


    public CustomerAdapter(ArrayList<Customer> customers, Context mContext, String edit, String factor_target) {
        this.mContext = mContext;
        this.customers = customers;
        this.edit = edit;
        this.factor_target = factor_target;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);

    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CustomerViewHolder holder, int position) {


        holder.bind(customers.get(position));

        holder.Action(customers.get(position)
                , dbh
                , callMethod
                , action
                , edit
                , factor_target
                , mContext
        );

        if (callMethod.ReadBoolan("ShowCustomerCredit")){
            holder.cus_ll.setVisibility(View.VISIBLE);
        }else {
            holder.cus_ll.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public int getItemCount() {
        return customers.size();
    }


}
