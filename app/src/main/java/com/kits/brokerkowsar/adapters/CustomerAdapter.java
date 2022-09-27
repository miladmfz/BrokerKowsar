package com.kits.brokerkowsar.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.ConfigActivity;
import com.kits.brokerkowsar.activity.PrefactorActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.Customer;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.viewholder.CustomerViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerViewHolder> {
    private final Context mContext;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<Customer> customers;
    private final String edit;
    private final String factor_target;
    private final DatabaseHelper dbh;
    private final Action action;
    private Intent intent;


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


        holder.cus_code.setText(NumberFunctions.PerisanNumber(customers.get(position).getCustomerFieldValue("CustomerCode")));
        holder.cus_name.setText(NumberFunctions.PerisanNumber(customers.get(position).getCustomerFieldValue("CustomerName")));
        holder.cus_manage.setText(NumberFunctions.PerisanNumber(customers.get(position).getCustomerFieldValue("Manager")));


        if (customers.get(position).getCustomerFieldValue("Address").equals("null")) {
            holder.cus_addres.setText("");
        } else {
            holder.cus_addres.setText(NumberFunctions.PerisanNumber(customers.get(position).getCustomerFieldValue("Address")));
        }

        if (customers.get(position).getCustomerFieldValue("Phone").equals("null")) {
            holder.cus_phone.setText("");
        } else {
            holder.cus_phone.setText(NumberFunctions.PerisanNumber(customers.get(position).getCustomerFieldValue("Phone")));
        }


        if (Integer.parseInt(customers.get(position).getCustomerFieldValue("Bestankar")) > -1) {
            holder.cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(customers.get(position).getCustomerFieldValue("Bestankar")))));
            holder.cus_bes.setTextColor(ContextCompat.getColor(mContext, R.color.green_900));
        } else {
            int a = (Integer.parseInt(customers.get(position).getCustomerFieldValue("Bestankar"))) * (-1);
            holder.cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(a)));
            holder.cus_bes.setTextColor(ContextCompat.getColor(mContext, R.color.red_900));
        }

        holder.fac_rltv.setOnClickListener(v -> {
            if (edit.equals("0")) {

                UserInfo auser = dbh.LoadPersonalInfo();
                if (Integer.parseInt(auser.getBrokerCode()) > 0) {
                    action.addfactordialog(customers.get(position).getCustomerFieldValue("CustomerCode"));
                } else {
                    intent = new Intent(mContext, ConfigActivity.class);
                    callMethod.showToast( "کد بازاریاب را وارد کنید");

                    mContext.startActivity(intent);
                }

            } else {
                dbh.UpdatePreFactorHeader_Customer(factor_target, customers.get(position).getCustomerFieldValue("CustomerCode"));
                intent = new Intent(mContext, PrefactorActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }



}
