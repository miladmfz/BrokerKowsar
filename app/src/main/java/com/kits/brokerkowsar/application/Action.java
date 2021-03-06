package com.kits.brokerkowsar.application;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.brokerkowsar.BuildConfig;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.activity.BuyActivity;
import com.kits.brokerkowsar.activity.CustomerActivity;
import com.kits.brokerkowsar.activity.NavActivity;
import com.kits.brokerkowsar.activity.PrefactorActivity;
import com.kits.brokerkowsar.activity.SearchActivity;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.model.Utilities;
import com.kits.brokerkowsar.webService.APIClient_kowsar;
import com.kits.brokerkowsar.webService.APIInterface;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class Action {
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private final Context mContext;
    CallMethod callMethod;
    private final DatabaseHelper dbh;
    private Intent intent;
    Cursor cursor;
    private Integer il;
    String url;
    String TempString;

    public Action(Context mContext) {
        this.mContext = mContext;
        this.il = 0;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
        url = callMethod.ReadString("ServerURLUse");

    }


    public void buydialog(String goodcode, String Basketflag) {
        int DefaultUnitValue;
        final String[] NewPrice = {""};
        final String[] boxAmount = {""};

        Good good = dbh.getGoodBuyBox(goodcode);
        DefaultUnitValue=Integer.parseInt(good.getGoodFieldValue("DefaultUnitValue"));

        NewPrice[0]=good.getGoodFieldValue("SellPrice");



        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        if(good.getGoodFieldValue("SellPriceType").equals("0")){ // nerkh forosh motlagh

            dialog.setContentView(R.layout.box_buy_absolute);
            Button boxbuy = dialog.findViewById(R.id.boxbuy_absolute_btn);
            final EditText amount = dialog.findViewById(R.id.boxbuy_absolute_amount);
            final TextView factorname = dialog.findViewById(R.id.boxbuy_absolute_factorname);
            final EditText price = dialog.findViewById(R.id.boxbuy_absolute_price);
            final TextView sumprice = dialog.findViewById(R.id.boxbuy_absolute_sumprice);
            final TextView factoramount = dialog.findViewById(R.id.boxbuy_absolute_facamount);

            if (Basketflag.equals("0")) {
                amount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("UnitName")));
                factoramount.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("factoramount")));
            } else {
                amount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("amount")));
                factoramount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("factoramount")));
                boxbuy.setText("?????????? ?????????? ???????? ??????");
            }

            price.setEnabled(!callMethod.ReadString("SellOff").equals("0"));

            Log.e("test_",DefaultUnitValue+"");

            factorname.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));

            price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("SellPrice")))));

            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        boxAmount[0] =NumberFunctions.EnglishNumber(amount.getText().toString());

                        long sumpricevlue= (long) (Long.parseLong(NewPrice[0]) * Long.parseLong(boxAmount[0])*DefaultUnitValue);

                        sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sumpricevlue)));
                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }
            });


            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable editable) {
                    if (price.hasFocus()) {
                        try {
                            NewPrice[0] =NumberFunctions.EnglishNumber(price.getText().toString());

                            long sumpricevlue= (long) (Long.parseLong(NewPrice[0]) * Long.parseLong(boxAmount[0])*DefaultUnitValue);
                            sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sumpricevlue)));
                        } catch (Exception e) {
                            sumprice.setText("");
                        }
                    }
                }
            });

            dialog.show();
            amount.requestFocus();
            amount.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT);
            }, 500);
            boxbuy.setOnClickListener(view -> {
                try {

                    if (NewPrice[0].equals("")) NewPrice[0] = "-1";
                    if (!boxAmount[0].equals("")) {
                        if (Integer.parseInt(boxAmount[0]) != 0) {
                            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                                dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"), goodcode, boxAmount[0], NewPrice[0], Basketflag);

                                callMethod.showToast( "???? ?????? ???????? ?????????? ????");
                                if (!Basketflag.equals("0")) {
                                    intent = new Intent(mContext, BuyActivity.class);
                                    intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                                    ((Activity) mContext).finish();
                                    ((Activity) mContext).overridePendingTransition(0, 0);
                                    mContext.startActivity(intent);
                                    ((Activity) mContext).overridePendingTransition(0, 0);
                                }
                            } else {
                                intent = new Intent(mContext, CustomerActivity.class);
                                intent.putExtra("edit", "0");
                                intent.putExtra("factor_code", "0");
                                intent.putExtra("id", "0");
                                mContext.startActivity(intent);
                            }
                            dialog.dismiss();
                        } else {
                            callMethod.showToast( "?????????? ???????? ?????? ???????? ?????? ????????.");
                        }
                    } else {
                        callMethod.showToast( "?????????? ???????? ?????? ???????? ?????? ????????.");
                    }
                }catch (Exception e){
                    callMethod.ErrorLog(e.getMessage());

                }
            });


            if (price.hasFocusable()) {
                price.selectAll();
            }


        }else { // nerkh forosh nesbi

            dialog.setContentView(R.layout.box_buy_percent);
            Button boxbuy = dialog.findViewById(R.id.boxbuy_percent_btn);
            final EditText amount = dialog.findViewById(R.id.boxbuy_percent_amount);
            final TextView factorname = dialog.findViewById(R.id.boxbuy_percent_factorname);
            final TextView maxPrice = dialog.findViewById(R.id.boxbuy_percent_maxprice);
            final EditText percent = dialog.findViewById(R.id.boxbuy_percent_scale);
            final EditText price = dialog.findViewById(R.id.boxbuy_percent_price);
            final TextView sumprice = dialog.findViewById(R.id.boxbuy_percent_sumprice);
            final TextView factoramount = dialog.findViewById(R.id.boxbuy_percent_facamount);

            if (Basketflag.equals("0")) {
                amount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("UnitName")));
                factoramount.setText(NumberFunctions.PerisanNumber(good.getGoodFieldValue("factoramount")));
            } else {
                amount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("amount")));
                factoramount.setHint(NumberFunctions.PerisanNumber(good.getGoodFieldValue("factoramount")));
                boxbuy.setText("?????????? ?????????? ???????? ??????");
            }

            if (callMethod.ReadString("SellOff").equals("0")) {
                percent.setEnabled(false);
                price.setEnabled(false);
            } else {
                percent.setEnabled(true);
                price.setEnabled(true);
            }

            long percent_param= (long) (100 - (100 * Float.parseFloat(good.getGoodFieldValue("SellPrice")) / Integer.parseInt(good.getGoodFieldValue("MaxSellPrice"))));
            percent.setText(NumberFunctions.PerisanNumber(percent_param+""));

            factorname.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            maxPrice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")))));
            price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("SellPrice")))));


            amount.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    try {
                        boxAmount[0] =NumberFunctions.EnglishNumber(amount.getText().toString());

                        long sumpricevlue= (long) (Long.parseLong(NewPrice[0]) * Long.parseLong(boxAmount[0])*DefaultUnitValue);

                        sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sumpricevlue)));

                    } catch (Exception e) {
                        sumprice.setText("");
                    }
                }
            });

            percent.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable editable) {
                    if (percent.hasFocus()) {
                        long iPercent;
                        try {
                            iPercent = Integer.parseInt(NumberFunctions.EnglishNumber(percent.getText().toString()));
                            if (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) > 0) {
                                if (iPercent > 100) {
                                    iPercent = 100;
                                    percent.setText(NumberFunctions.PerisanNumber(String.valueOf(iPercent)));
                                    percent.setError("???????????? ??????????");
                                }
                                long sellpricenew= (long)(Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) - (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) * iPercent / 100));
                                price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sellpricenew)));
                                NewPrice[0]=String.valueOf(sellpricenew);
                            }

                        } catch (Exception e) {
                            price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")))));
                            NewPrice[0]=good.getGoodFieldValue("MaxSellPrice");

                        }
                        try {

                            long sumpricevlue= (long) (Long.parseLong(NewPrice[0]) * Long.parseLong(boxAmount[0])*DefaultUnitValue);
                            sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sumpricevlue)));

                        } catch (Exception e) {
                            sumprice.setText("");
                        }
                    }

                }
            });

            price.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                @SuppressLint("SetTextI18n")
                @Override
                public void afterTextChanged(Editable editable) {
                    if (price.hasFocus()) {
                        try {
                            NewPrice[0] =NumberFunctions.EnglishNumber(price.getText().toString());

                            if (Integer.parseInt(good.getGoodFieldValue("MaxSellPrice")) > 0) {
                                percent.setText(NumberFunctions.PerisanNumber("" + (100 - (100 * Long.parseLong(NewPrice[0]) / Integer.parseInt(good.getGoodFieldValue("MaxSellPrice"))))));
                            } else
                                percent.setText("");


                            long sumpricevlue= (long) (Long.parseLong(NewPrice[0]) * Long.parseLong(boxAmount[0])*DefaultUnitValue);

                            sumprice.setText(NumberFunctions.PerisanNumber(decimalFormat.format(sumpricevlue)));

                        } catch (Exception e) {
                            sumprice.setText("");
                        }
                    }
                }
            });

            dialog.show();
            amount.requestFocus();
            amount.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount, InputMethodManager.SHOW_IMPLICIT);
            }, 500);
            boxbuy.setOnClickListener(view -> {

                if (NewPrice[0].equals("")) NewPrice[0] = "-1";
                if (!boxAmount[0].equals("")) {
                    if (Integer.parseInt(boxAmount[0]) != 0) {
                        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                            dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"), goodcode, boxAmount[0], NewPrice[0], Basketflag);

                            callMethod.showToast( "???? ?????? ???????? ?????????? ????");
                            if (!Basketflag.equals("0")) {
                                intent = new Intent(mContext, BuyActivity.class);
                                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                                ((Activity) mContext).finish();
                                ((Activity) mContext).overridePendingTransition(0, 0);
                                mContext.startActivity(intent);
                                ((Activity) mContext).overridePendingTransition(0, 0);
                            }
                        } else {
                            intent = new Intent(mContext, CustomerActivity.class);
                            intent.putExtra("edit", "0");
                            intent.putExtra("factor_code", "0");
                            intent.putExtra("id", "0");
                            mContext.startActivity(intent);
                        }
                        dialog.dismiss();
                    } else {
                        callMethod.showToast( "?????????? ???????? ?????? ???????? ?????? ????????.");
                    }
                } else {
                    callMethod.showToast( "?????????? ???????? ?????? ???????? ?????? ????????.");
                }
            });


            if (percent.hasFocusable()) {
                percent.selectAll();
            }

            if (price.hasFocusable()) {
                price.selectAll();
            }

        }


    }


    public void sendfactor(String factor_code) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                int code = jo.getInt("GoodCode");
                if (code == 0) {
                    int kowsarcode = jo.getInt("PreFactorCode");
                    if (kowsarcode > 0) {
                        String factorDate = jo.getString("PreFactorDate");
                        dbh.UpdatePreFactor(factor_code, String.valueOf(kowsarcode), factorDate);
                        callMethod.EditString("PreFactorCode", "0");
                        lottieok();


                    } else {
                        callMethod.showToast( "?????? ???? ???????????? ???? ????????");
                    }

                } else {
                    SQLiteDatabase dtb = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
                    for (int i = 0; i < il; i++) {
                        jo = object.getJSONObject(i);
                        code = jo.getInt("GoodCode");
                        int flag = jo.getInt("Flag");
                        dtb.execSQL("Update PreFactorRow set Shortage = " + flag + " Where IfNull(PreFactorCode,0)=" + factor_code + " And GoodRef = " + code);
                    }
                    callMethod.showToast( "?????????????? ???????? ?????? ?????? ???????????? ??????????!");
                    intent = new Intent(mContext, BuyActivity.class);
                    intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                    ((Activity) mContext).finish();
                    ((Activity) mContext).overridePendingTransition(0, 0);
                    mContext.startActivity(intent);
                    ((Activity) mContext).overridePendingTransition(0, 0);
                }
            } catch (JSONException e) {
                callMethod.ErrorLog(e.getMessage());
                callMethod.showToast( "???????? ?????? ???? ??????????????");
            }
        }, volleyError -> {
            callMethod.ErrorLog(volleyError.getMessage());
            callMethod.showToast( "???????????? ???? ???????? ???????? ?????? ????????.");
        }) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "PFQASWED");
                SQLiteDatabase dtb = mContext.openOrCreateDatabase(callMethod.ReadString("DatabaseName"), Context.MODE_PRIVATE, null);
                cursor = dtb.rawQuery("Select PreFactorCode, PreFactorDate, PreFactorExplain, CustomerRef, BrokerRef, (Select sum(FactorAmount) From PreFactorRow r Where r.PrefactorRef=h.PrefactorCode) As rwCount From PreFactor h Where PreFactorCode = " + factor_code, null);
                String pr1 = CursorToJson(cursor);
                cursor.close();
                Log.e("bklog_reqqqq", pr1);
                params.put("PFHDQASW", pr1);
                cursor = dtb.rawQuery("Select GoodRef, FactorAmount, Price From PreFactorRow Where  GoodRef>0 and  Prefactorref = " + factor_code, null);
                String pr2 = CursorToJson(cursor);
                cursor.close();

                Log.e("bklog_reqqqq", pr2);
                params.put("PFDTQASW", pr2);
                return params;
            }

        };
        queue.add(stringrequste);
    }


    public void edit_explain(String factor_code) {

        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pf_detail);
        Button pf_detail_btn = dialog.findViewById(R.id.pf_detail_btn);
        pf_detail_btn.setText("?????? ??????????????");
        final EditText pf_detail_detail = dialog.findViewById(R.id.pf_detail_detail);
        dialog.show();
        pf_detail_detail.requestFocus();
        pf_detail_detail.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(pf_detail_detail, InputMethodManager.SHOW_IMPLICIT);
        }, 500);

        pf_detail_btn.setOnClickListener(view -> {

            String detail = NumberFunctions.EnglishNumber(pf_detail_detail.getText().toString());
            dbh.update_explain(factor_code, detail);
            intent = new Intent(mContext, PrefactorActivity.class);
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, 0);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(0, 0);

        });

    }


    public void addfactordialog(String customer_code) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pf_detail);
        Button pf_detail_btn = dialog.findViewById(R.id.pf_detail_btn);
        final EditText pf_detail_detail = dialog.findViewById(R.id.pf_detail_detail);
        dialog.show();
        pf_detail_detail.requestFocus();
        pf_detail_detail.postDelayed(() -> {
            InputMethodManager inputMethodManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(pf_detail_detail, InputMethodManager.SHOW_IMPLICIT);
        }, 500);

        pf_detail_btn.setOnClickListener(view -> {
            DatabaseHelper dbh = new DatabaseHelper(mContext, callMethod.ReadString("DatabaseName"));
            String detail = pf_detail_detail.getText().toString();
            dbh.InsertPreFactorHeader(detail, String.valueOf(customer_code));
            String prefactor_code = "PreFactorCode";
            callMethod.EditString(prefactor_code, dbh.GetLastPreFactorHeader().toString());
            lottiereceipt();
            intent = new Intent(mContext, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "???????????? ????????");
            ((Activity) mContext).finish();
            ((Activity) mContext).overridePendingTransition(0, 0);
            mContext.startActivity(intent);
            ((Activity) mContext).overridePendingTransition(0, 0);

        });

    }


    public void lottiereceipt() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.lottie_name);
        animationView.setAnimation(R.raw.receipt);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) { }
            @Override public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();

            }
            @Override public void onAnimationCancel(Animator animation) { }
            @Override public void onAnimationRepeat(Animator animation) { }
        });


    }

    public void lottieok() {

        Dialog dialog1 = new Dialog(mContext);
        dialog1.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.lottie);
        LottieAnimationView animationView = dialog1.findViewById(R.id.lottie_name);
        animationView.setAnimation(R.raw.oklottie);
        dialog1.show();
        animationView.setRepeatCount(0);

        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override public void onAnimationStart(Animator animation) { }
            @Override public void onAnimationEnd(Animator animation) {
                dialog1.dismiss();
                intent = new Intent(mContext, NavActivity.class);
                ((Activity) mContext).finish();
                ((Activity) mContext).overridePendingTransition(0, 0);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(0, 0);
            }
            @Override public void onAnimationCancel(Animator animation) { }
            @Override public void onAnimationRepeat(Animator animation) { }
        });


    }

    public void app_info() {

        @SuppressLint("HardwareIds") String android_id = Settings.Secure.getString(mContext
                .getContentResolver(), Settings.Secure.ANDROID_ID);


        PersianCalendar calendar1 = new PersianCalendar();
        String version=BuildConfig.VERSION_NAME;


        UserInfo auser = dbh.LoadPersonalInfo();

        APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
        Call<RetrofitResponse> cl = apiInterface.Kowsar_log("Log_report"
                , android_id
                , url
                , callMethod.ReadString("PersianCompanyNameUse")
                , callMethod.ReadString("PreFactorCode")
                , calendar1.getPersianShortDateTime()
                , auser.getBrokerCode()
                , version);

        cl.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                assert response.body() != null;
            }



            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                //callMethod.ErrorLog(t.getMessage());
            }
        });

    }




    public String CursorToJson(Cursor cursor) {
        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("CursorToJson_Error: ", Objects.requireNonNull(e.getMessage()));
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        cursor.close();
        return resultSet.toString();
    }

}
