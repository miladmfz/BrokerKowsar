package com.kits.brokerkowsar.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.BuyActivity;
import com.kits.brokerkowsar.activity.BuyhistoryActivity;
import com.kits.brokerkowsar.activity.CustomerActivity;
import com.kits.brokerkowsar.activity.PrefactorActivity;
import com.kits.brokerkowsar.activity.PrinterActivity;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.PreFactor;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class Prefactor_Header_adapter extends RecyclerView.Adapter<Prefactor_Header_adapter.facViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private Intent intent;


    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;
    private final DatabaseHelper dbh;
    private final Action action;


    public Prefactor_Header_adapter(ArrayList<PreFactor> PreFactors, Context mContext) {
        this.mContext = mContext;
        this.PreFactors = PreFactors;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        this.action = new Action(mContext);

    }

    @NonNull
    @Override
    public facViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.prefactor_header, parent, false);
        return new facViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final facViewHolder holder, final int position) {

        final PreFactor preFactordetail = PreFactors.get(position);

        holder.fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode"))));
        holder.fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorDate"))));
        holder.fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorTime"))));
        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorkowsarDate"))));
        holder.fac_kowsarcode.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode"))));
        holder.fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorExplain"))));
        holder.fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("Customer"))));
        holder.fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("RowCount"))));
        holder.fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactordetail.getPreFactorFieldValue("SumAmount"))));
        holder.fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(preFactordetail.getPreFactorFieldValue("SumPrice"))))));


        if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) > 0) {
            holder.fac_status.setVisibility(View.VISIBLE);
        } else {
            holder.fac_status.setVisibility(View.GONE);
        }

        holder.fac_history_good.setOnClickListener(view -> {
            callMethod.EditString("PreFactorGood", preFactordetail.getPreFactorFieldValue("PreFactorCode"));
            intent = new Intent(mContext, BuyhistoryActivity.class);
            mContext.startActivity(intent);
        });


        holder.fac_excel.setOnClickListener(view -> {

            intent = new Intent(mContext, PrinterActivity.class);
            intent.putExtra("PreFac", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
            mContext.startActivity(intent);
        });


        holder.fac_dlt.setOnClickListener(view -> {

            if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", preFactordetail.getPreFactorFieldValue("PreFactorCode"));

                ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode")));
                if (goods.size() != 0) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("توجه")
                            .setMessage("فاکتور دارای کالا می باشد،کالاها حذف شود؟")
                            .setPositiveButton("بله", (dialogInterface, i) -> {
                                intent = new Intent(mContext, BuyActivity.class);
                                intent.putExtra("PreFac", preFactordetail.getPreFactorFieldValue("PreFactorCode"));
                                mContext.startActivity(intent);
                            })
                            .setNegativeButton("خیر", (dialogInterface, i) -> {
                            })
                            .show();
                } else {
                    dbh.DeletePreFactor(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode")));
                    callMethod.showToast( "فاکتور حذف گردید");
                    goods.size();

                    callMethod.EditString("PreFactorCode", "0");

                    intent = new Intent(mContext, PrefactorActivity.class);
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);


                }


            }
        });


        holder.fac_good_edit.setOnClickListener(view -> {

            if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", preFactordetail.getPreFactorFieldValue("PreFactorCode"));

                intent = new Intent(mContext, BuyActivity.class);
                intent.putExtra("PreFac", preFactordetail.getPreFactorFieldValue("PreFactorCode"));

                mContext.startActivity(intent);
            }
        });


        holder.fac_send.setOnClickListener(view -> {

            ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode")));
            if (goods.size() != 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا فاکتور ارسال گردد؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                callMethod.showToast( "فاکتور خالی می باشد");
                goods.size();
            }

        });


        holder.fac_customer_edit.setOnClickListener(view -> {
            if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح مشتری می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {

                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "1");
                            intent.putExtra("factor_code", preFactordetail.getPreFactorFieldValue("PreFactorCode"));
                            intent.putExtra("id", "0");

                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);


                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });


        holder.fac_explain_edit.setOnClickListener(view -> {
            if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح توضیحات می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.edit_explain(String.valueOf(preFactordetail.getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });


        holder.fac_select.setOnClickListener(v -> {


            if (Integer.parseInt(preFactordetail.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                callMethod.EditString("PreFactorCode", preFactordetail.getPreFactorFieldValue("PreFactorCode"));

                callMethod.showToast( "فاکتور مورد نظر انتخاب شد");
                intent = new Intent(mContext, SearchActivity.class);
                intent.putExtra("scan", "");
                intent.putExtra("id", "0");
                intent.putExtra("title", "جستجوی کالا");

                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);

            }
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
        private final TextView fac_kowsardate;
        private final TextView fac_kowsarcode;
        private final TextView fac_detail;
        private final TextView fac_row;
        private final TextView fac_count;
        private final TextView fac_price;
        private final TextView fac_customer;
        private final TextView fac_status;
        private final Button fac_history_good;
        private final Button fac_send;
        private final Button fac_dlt;
        private final Button fac_customer_edit;
        private final Button fac_explain_edit;
        private final Button fac_excel;
        private final Button fac_select;
        private final Button fac_good_edit;
        MaterialCardView fac_rltv;

        facViewHolder(View itemView) {
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


}
