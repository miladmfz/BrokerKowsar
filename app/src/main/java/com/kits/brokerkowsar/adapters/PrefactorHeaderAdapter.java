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
import com.kits.brokerkowsar.activity.BuyHistoryActivity;
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
import com.kits.brokerkowsar.viewholder.PreFactorHeaderOpenViewHolder;
import com.kits.brokerkowsar.viewholder.PreFactorHeaderViewHolder;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class PrefactorHeaderAdapter extends RecyclerView.Adapter<PreFactorHeaderViewHolder> {

    private final Context mContext;
    CallMethod callMethod;
    private Intent intent;


    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private final ArrayList<PreFactor> PreFactors;
    private final DatabaseHelper dbh;
    private final Action action;


    public PrefactorHeaderAdapter(ArrayList<PreFactor> PreFactors, Context mContext) {
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


        holder.fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"))));
        holder.fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorDate"))));
        holder.fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorTime"))));
        holder.fac_kowsardate.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorkowsarDate"))));
        holder.fac_kowsarcode.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode"))));
        holder.fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorExplain"))));
        holder.fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("Customer"))));
        holder.fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("RowCount"))));
        holder.fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("SumAmount"))));
        holder.fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("SumPrice"))))));


        if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) > 0) {
            holder.fac_status.setVisibility(View.VISIBLE);
        } else {
            holder.fac_status.setVisibility(View.GONE);
        }

        holder.fac_history_good.setOnClickListener(view -> {
            callMethod.EditString("PreFactorGood", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
            intent = new Intent(mContext, BuyHistoryActivity.class);
            mContext.startActivity(intent);
        });


        holder.fac_excel.setOnClickListener(view -> {

            intent = new Intent(mContext, PrinterActivity.class);
            intent.putExtra("PreFac", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
            mContext.startActivity(intent);
        });


        holder.fac_dlt.setOnClickListener(view -> {

            if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));

                ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode")));
                if (goods.size() != 0) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("توجه")
                            .setMessage("فاکتور دارای کالا می باشد،کالاها حذف شود؟")
                            .setPositiveButton("بله", (dialogInterface, i) -> {
                                intent = new Intent(mContext, BuyActivity.class);
                                intent.putExtra("PreFac", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
                                mContext.startActivity(intent);
                            })
                            .setNegativeButton("خیر", (dialogInterface, i) -> {
                            })
                            .show();
                } else {
                    dbh.DeletePreFactor(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode")));
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

            if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));

                intent = new Intent(mContext, BuyActivity.class);
                intent.putExtra("PreFac", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));

                mContext.startActivity(intent);
            }
        });


        holder.fac_send.setOnClickListener(view -> {

            ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode")));
            if (goods.size() != 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا فاکتور ارسال گردد؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                callMethod.showToast( "فاکتور خالی می باشد");
                goods.size();
            }

        });


        holder.fac_customer_edit.setOnClickListener(view -> {
            if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح مشتری می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {

                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "1");
                            intent.putExtra("factor_code", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));
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
            if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح توضیحات می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.edit_explain(String.valueOf(PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });


        holder.fac_select.setOnClickListener(v -> {


            if (Integer.parseInt(PreFactors.get(position).getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast( "فاکتور بسته می باشد");
            } else {
                callMethod.EditString("PreFactorCode", PreFactors.get(position).getPreFactorFieldValue("PreFactorCode"));

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




}
