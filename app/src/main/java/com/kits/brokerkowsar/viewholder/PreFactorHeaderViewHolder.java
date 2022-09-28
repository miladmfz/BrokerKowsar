package com.kits.brokerkowsar.viewholder;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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

import java.text.DecimalFormat;
import java.util.ArrayList;

;

public class PreFactorHeaderViewHolder extends RecyclerView.ViewHolder {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    Intent intent;
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


    public void bind(PreFactor preFactor) {

        fac_code.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode"))));
        fac_date.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorDate"))));
        fac_time.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorTime"))));
        fac_kowsardate.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorkowsarDate"))));
        fac_kowsarcode.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorKowsarCode"))));
        fac_detail.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorExplain"))));
        fac_customer.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("Customer"))));
        fac_row.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("RowCount"))));
        fac_count.setText(NumberFunctions.PerisanNumber(String.valueOf(preFactor.getPreFactorFieldValue("SumAmount"))));
        fac_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(String.valueOf(preFactor.getPreFactorFieldValue("SumPrice"))))));


        if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) > 0) {
            fac_status.setVisibility(View.VISIBLE);
        } else {
            fac_status.setVisibility(View.GONE);
        }

    }


    public void Action(
            PreFactor preFactor
            , Context mContext
            , DatabaseHelper dbh
            , CallMethod callMethod
            , Action action
    ) {
        fac_history_good.setOnClickListener(view -> {
            callMethod.EditString("PreFactorGood", preFactor.getPreFactorFieldValue("PreFactorCode"));
            intent = new Intent(mContext, BuyHistoryActivity.class);
            mContext.startActivity(intent);
        });


        fac_excel.setOnClickListener(view -> {

            intent = new Intent(mContext, PrinterActivity.class);
            intent.putExtra("PreFac", preFactor.getPreFactorFieldValue("PreFactorCode"));
            mContext.startActivity(intent);
        });


        fac_dlt.setOnClickListener(view -> {

            if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast("فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", preFactor.getPreFactorFieldValue("PreFactorCode"));

                ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode")));
                if (goods.size() != 0) {
                    new AlertDialog.Builder(mContext)
                            .setTitle("توجه")
                            .setMessage("فاکتور دارای کالا می باشد،کالاها حذف شود؟")
                            .setPositiveButton("بله", (dialogInterface, i) -> {
                                intent = new Intent(mContext, BuyActivity.class);
                                intent.putExtra("PreFac", preFactor.getPreFactorFieldValue("PreFactorCode"));
                                mContext.startActivity(intent);
                            })
                            .setNegativeButton("خیر", (dialogInterface, i) -> {
                            })
                            .show();
                } else {
                    dbh.DeletePreFactor(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode")));
                    callMethod.showToast("فاکتور حذف گردید");
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


        fac_good_edit.setOnClickListener(view -> {

            if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast("فاکتور بسته می باشد");
            } else {

                callMethod.EditString("PreFactorCode", preFactor.getPreFactorFieldValue("PreFactorCode"));

                intent = new Intent(mContext, BuyActivity.class);
                intent.putExtra("PreFac", preFactor.getPreFactorFieldValue("PreFactorCode"));

                mContext.startActivity(intent);
            }
        });


        fac_send.setOnClickListener(view -> {

            ArrayList<Good> goods = dbh.getAllPreFactorRows("", String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode")));
            if (goods.size() != 0) {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا فاکتور ارسال گردد؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            } else {
                callMethod.showToast("فاکتور خالی می باشد");
                goods.size();
            }

        });


        fac_customer_edit.setOnClickListener(view -> {
            if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast("فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح مشتری می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {

                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "1");
                            intent.putExtra("factor_code", preFactor.getPreFactorFieldValue("PreFactorCode"));
                            intent.putExtra("id", "0");

                            ((Activity) mContext).finish();
                            mContext.startActivity(intent);


                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });


        fac_explain_edit.setOnClickListener(view -> {
            if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast("فاکتور بسته می باشد");
            } else {
                new AlertDialog.Builder(mContext)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به اصلاح توضیحات می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.edit_explain(String.valueOf(preFactor.getPreFactorFieldValue("PreFactorCode"))))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show();
            }
        });


        fac_select.setOnClickListener(v -> {


            if (Integer.parseInt(preFactor.getPreFactorFieldValue("PreFactorKowsarCode")) != 0) {
                callMethod.showToast("فاکتور بسته می باشد");
            } else {
                callMethod.EditString("PreFactorCode", preFactor.getPreFactorFieldValue("PreFactorCode"));

                callMethod.showToast("فاکتور مورد نظر انتخاب شد");
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


}