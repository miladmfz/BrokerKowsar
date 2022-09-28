package com.kits.brokerkowsar.viewholder;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.ConfigActivity;
import com.kits.brokerkowsar.activity.PrefactorActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.Customer;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;

import java.text.DecimalFormat;

;

public class CustomerViewHolder extends RecyclerView.ViewHolder {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final TextView cus_code;
    private final TextView cus_name;
    private final TextView cus_manage;
    private final TextView cus_phone;
    private final TextView cus_addres;
    private final TextView cus_bes;
    private final MaterialCardView fac_rltv;

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

    public void bind(Customer customer) {
        cus_code.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("CustomerCode")));
        cus_name.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("CustomerName")));
        cus_manage.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Manager")));

        if (customer.getCustomerFieldValue("Address").equals("null")) {
            cus_addres.setText("");
        } else {
            cus_addres.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Address")));
        }

        if (customer.getCustomerFieldValue("Phone").equals("null")) {
            cus_phone.setText("");
        } else {
            cus_phone.setText(NumberFunctions.PerisanNumber(customer.getCustomerFieldValue("Phone")));
        }


        if (Integer.parseInt(customer.getCustomerFieldValue("Bestankar")) > -1) {
            cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(customer.getCustomerFieldValue("Bestankar")))));
            cus_bes.setTextColor(ContextCompat.getColor(App.getContext(), R.color.green_900));
        } else {
            int a = (Integer.parseInt(customer.getCustomerFieldValue("Bestankar"))) * (-1);
            cus_bes.setText(NumberFunctions.PerisanNumber(decimalFormat.format(a)));
            cus_bes.setTextColor(ContextCompat.getColor(App.getContext(), R.color.red_900));
        }


    }


    public void Action(Customer customer
            , DatabaseHelper dbh
            , CallMethod callMethod
            , Action action
            , String edit
            , String factor_target
            , Context mContext) {


        fac_rltv.setOnClickListener(v -> {
            if (edit.equals("0")) {

                UserInfo auser = dbh.LoadPersonalInfo();
                if (Integer.parseInt(auser.getBrokerCode()) > 0) {
                    action.addfactordialog(customer.getCustomerFieldValue("CustomerCode"));
                } else {
                    Intent intent = new Intent(mContext, ConfigActivity.class);
                    callMethod.showToast("کد بازاریاب را وارد کنید");

                    mContext.startActivity(intent);
                }

            } else {
                dbh.UpdatePreFactorHeader_Customer(factor_target, customer.getCustomerFieldValue("CustomerCode"));
                Intent intent = new Intent(mContext, PrefactorActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
        });


    }


}