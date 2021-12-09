package com.kits.brokerkowsar.application;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.activity.NavActivity;
import com.kits.brokerkowsar.model.Column;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;


public class Replication {

    private final Context mContext;
    CallMethod callMethod;
    APIInterface apiInterface;
    Intent intent;
    Image_info image_info ;

    private SQLiteDatabase database;
    private final Integer RepRowCount = 200;
    private Integer FinalStep = 0;
    private final String RepType = "1";
    private String LastRepCode = "0";
    private String RepTable = "";
    private final Dialog dialog;
    private final DatabaseHelper dbh;
    String url;
    Cursor cursor;

    TextView tv_rep, tv_step;

    public Replication(Context context) {
        this.mContext = context;
        this.callMethod = new CallMethod(mContext);
        this.dbh = new DatabaseHelper(mContext, callMethod.ReadString("UseSQLiteURL"));
        this.dialog = new Dialog(mContext);
        this.image_info = new Image_info(mContext);
        url = callMethod.ReadString("ServerURLUse");
        database = mContext.openOrCreateDatabase(callMethod.ReadString("UseSQLiteURL"), Context.MODE_PRIVATE, null);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

    }


    public void dialog() {
        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);
        dialog.show();
    }


    public void BrokerStack() {
        UserInfo userInfo = dbh.LoadPersonalInfo();
        Call<RetrofitResponse> call1 = apiInterface.BrokerStack("BrokerStack", userInfo.getBrokerCode());
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(dbh.ReadConfig("BrokerStack"))) {
                        dbh.SaveConfig("BrokerStack",response.body().getText());
                    }
                }
            }
            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });
        MenuBroker();
    }

    public void MenuBroker() {
        Call<RetrofitResponse> call1 = apiInterface.MenuBroker("GetMenuBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    if (!response.body().getText().equals(dbh.ReadConfig("MenuBroker"))) {
                        dbh.SaveConfig("MenuBroker",response.body().getText());
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
            }
        });

    }


    public void GoodTypeReplication() {

        Call<RetrofitResponse> call1 = apiInterface.GetGoodType("GetGoodType");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    ArrayList<Column> columns = response.body().getColumns();
                    for (Column column : columns) {
                        dbh.ReplicateGoodtype(column);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                Log.e("onFailure", t.toString());
            }
        });
        final Dialog dialog1;
        dialog1 = new Dialog(mContext);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        columnReplication(0);

    }

    public void columnReplication(Integer i) {
        if (i < 4) {
            Call<RetrofitResponse> call2 = apiInterface.GetColumnList("GetColumnList", "" + i, "1", "1");
            call2.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        ArrayList<Column> columns = response.body().getColumns();
                        Log.e("columns_size", columns.size() + "");
                        int j = 0;
                        for (Column column : columns) {
                            dbh.ReplicateColumn(column, i);
                            j++;
                        }
                        if (j == columns.size()) {
                            columnReplication(i + 1);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    Log.e("onFailure", t.toString());
                }
            });
        } else {
            intent = new Intent(mContext, NavActivity.class);
            mContext.startActivity(intent);
            ((Activity) mContext).finish();
        }
    }


    public void replicate_all() {
        replicateCentralChange();
    }


    public void replicate_customer() {
        replicateCentralChange_customer();
    }

    public void replicateCentralChange() {

        dialog();
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/1"));
        RepTable = "Central";

        if (LastRepCode.equals("0")) {

            cursor = database.rawQuery("Select DataValue From Config Where KeyValue = 'Central_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {

            FinalStep = 0;
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");

                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:

                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CentralCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":

                                    String CentralPrivateCode = jo.getString("CentralPrivateCode");
                                    String CentralName = (jo.getString("Title") + jo.getString("FName") + jo.getString("Name")).trim();
                                    CentralName = CentralName.replaceAll("'", "''");

                                    String Manager = jo.getString("Manager");
                                    String Delegacy = jo.getString("Delegacy");
                                    String D_CodeMelli = jo.getString("D_CodeMelli");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Central Where CentralCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Central(CentralCode, CentralPrivateCode, CentralName, Manager, Delegacy,D_CodeMelli) Select " + code + "," + CentralPrivateCode + ",'" + CentralName + "','" + Manager + "','" + Delegacy + "','" + D_CodeMelli + "'";
                                    } else {
                                        qCol = "Update Central Set CentralPrivateCode=" + CentralPrivateCode + ", CentralName='" + CentralName + "', Manager='" + Manager + "', Delegacy='" + Delegacy + "', D_CodeMelli='" + D_CodeMelli + "' Where CentralCode=" + code;
                                    }

                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }

                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);
                            LastRepCode = repcode;

                        }

                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Central_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
            if (il >= RepRowCount) {

                replicateCentralChange();
            } else {

                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateCityChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };

        queue.add(stringrequste);

    }

    public void replicateCityChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/2"));
        FinalStep = 0;
        RepTable = "City";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='City_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CityCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CityName = jo.getString("Name");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From City Where CityCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO City(CityCode, CityName) Select " + code + ",'" + CityName + "'";
                                    } else {
                                        qCol = "Update City Set CityName='" + CityName + "' Where CityCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);
                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'City_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCityChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateCacheGroupChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateCacheGroupChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/2"));
        FinalStep = 0;
        RepTable = "CacheGroup";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='CacheGroup_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);

            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CacheGoodGroupCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String GroupsWhitoutCode = jo.getString("GroupsWhitoutCode");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From CacheGoodGroup Where CacheGoodGroupCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO CacheGoodGroup(CacheGoodGroupCode, GroupsWhitoutCode) Select " + code + ",'" + GroupsWhitoutCode + "'";
                                    } else {
                                        qCol = "Update CacheGoodGroup Set GroupsWhitoutCode='" + GroupsWhitoutCode + "' Where CacheGoodGroupCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);
                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'CacheGroup_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCacheGroupChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateAddressChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }


    public void replicateAddressChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/3"));
        FinalStep = 0;
        RepTable = "Address";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='Address_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("AddressCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CentralRef = jo.getString("CentralRef");
                                    String CityCode = jo.getString("CityCode");
                                    String Address = jo.getString("Address");
                                    Address = Address.replaceAll("'", "''");

                                    String Phone = jo.getString("Phone");
                                    String Mobile = jo.getString("Mobile");
                                    String MobileName = jo.getString("MobileName");
                                    String Email = jo.getString("Email");
                                    String Fax = jo.getString("Fax");
                                    String ZipCode = jo.getString("ZipCode");
                                    String PostCode = jo.getString("PostCode");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Address Where AddressCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Address(AddressCode, CentralRef, CityCode, Address, Phone, Mobile, MobileName, Email, Fax, ZipCode, PostCode) Select " + code + "," + CentralRef + "," + CityCode + ",'" + Address + "','" + Phone + "','" + Mobile + "','" + MobileName + "','" + Email + "','" + Fax + "','" + ZipCode + "','" + PostCode + "'";
                                    } else {
                                        qCol = "Update Address Set CentralRef=" + CentralRef + ", CityCode=" + CityCode + ", Address='" + Address + "', Phone='" + Phone + "', Mobile='" + Mobile + "', MobileName='" + MobileName + "', Email='" + Email + "', Fax='" + Fax + "', ZipCode='" + ZipCode + "', PostCode='" + PostCode + "' Where AddressCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);
                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Address_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateAddressChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateCustomerChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateCustomerChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/4"));
        FinalStep = 0;

        RepTable = "Customer";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='Customer_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CustomerCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CentralRef = jo.getString("CentralRef");
                                    String AddressRef = jo.getString("AddressRef");
                                    String Bestankar = String.valueOf((jo.getDouble("CustomerBestankar") - jo.getDouble("CustomerBedehkar")));
                                    String Active = jo.getString("Active");
                                    String EtebarNaghd = jo.getString("EtebarNaghd");
                                    String EtebarCheck = jo.getString("EtebarCheck");
                                    String Takhfif = jo.getString("Takhfif");
                                    String PriceTip = jo.getString("PriceTip");
                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Customer Where CustomerCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Customer(CustomerCode, CentralRef, AddressRef, Bestankar, Active, EtebarNaghd, EtebarCheck, Takhfif, PriceTip) Select " + code + "," + CentralRef + "," + AddressRef + "," + Bestankar + "," + Active + "," + EtebarNaghd + "," + EtebarCheck + "," + Takhfif + "," + PriceTip;
                                    } else {
                                        qCol = "Update Customer Set CentralRef=" + CentralRef + ", AddressRef=" + AddressRef + ", Bestankar=" + Bestankar + ", Active=" + Active + ", EtebarNaghd=" + EtebarNaghd + ", EtebarCheck=" + EtebarCheck + ", Takhfif=" + Takhfif + ", PriceTip=" + PriceTip + " Where CustomerCode=" + code;
                                    }

                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }
                            Log.e("bklog_repstrQuery", qCol);
                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Customer_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCustomerChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/5"));
        FinalStep = 0;
        RepTable = "Good";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery(" Select DataValue From Config Where KeyValue = 'Good_LastRepCode' ", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(cursor.getColumnIndex("DataValue"));
            cursor.close();
        }

        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("GoodCode");
                            StringBuilder qCol = new StringBuilder();
                            StringBuilder qVal = new StringBuilder();
                            StringBuilder qUpd = new StringBuilder();
                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    Iterator<String> iter = jo.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GoodCode"))) {
                                            try {
                                                Object value = jo.get(key);

                                                value = value.toString().replaceAll("'", "''");
                                                if (!key.equals("RLClassName")) {
                                                    if (!key.equals("RowsCount")) {
                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");

                                                        if (qUpd.toString().equals("")) {
                                                            qUpd = new StringBuilder("Update Good Set " + key + "='" + value + "'");
                                                        } else {
                                                            qUpd.append(",").append(key).append("='").append(value).append("'");
                                                        }
                                                    }
                                                }
                                            } catch (JSONException ignored) {
                                            }
                                        }
                                    }
                                    Cursor d = database.rawQuery("Select Count(*) As cntRec From Good Where GoodCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = new StringBuilder("INSERT INTO Good( GoodCode " + qCol + ") VALUES(" + code + qVal + ")");
                                    } else {
                                        qCol = new StringBuilder(qUpd + " Where GoodCode=" + code);
                                    }
                                    try {
                                        database.execSQL(qCol.toString());
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                                case "D":
                                case "d":
                                    try {
                                        database.execSQL("delete from good where goodcode = " + code + " and not exists (select 1 From PreFactorRow Where GoodRef =" + code + ")");
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    break;
                            }
                            Log.e("bklog_repstrQuery", qCol.toString());
                            Log.e("bklog_repstrQuery", LastRepCode);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Good_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {

                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateGoodChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodStackChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodStackChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/6"));
        FinalStep = 0;
        RepTable = "GoodStack";
        if (LastRepCode.equals("0")) {

            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodStack_LastRepCode'", null);
            cursor.moveToFirst();

            LastRepCode = cursor.getString(0);

            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {

            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {
                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("GoodRef");
                            String qCol = "";
                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":


                                    String Amount = jo.getString("Amount");
                                    String ReservedAmount = jo.getString("ReservedAmount");
                                    String StackRef = jo.getString("StackRef");
                                    String ActiveStack = jo.getString("ActiveStack");


                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From GoodStack Where GoodRef =" + code + " And StackRef=" + StackRef, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO GoodStack( GoodRef,StackRef,Amount,ReservedAmount,ActiveStack)  VALUES ( " + code + "," + StackRef + "," + Amount + "," + ReservedAmount + "," + ActiveStack + ")";
                                    } else {
                                        qCol = "Update GoodStack Set Amount = " + Amount + ", ActiveStack=" + ActiveStack + ", ReservedAmount=" + ReservedAmount + " Where GoodRef=" + code + " And StackRef=" + StackRef;
                                    }

                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();

                                    break;
                            }


                            Log.e("bklog_repstrQuery", qCol);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodStack_LastRepCode'");
                        break;
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateGoodStackChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodsGrpChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodsGrpChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/7"));
        FinalStep = 0;
        RepTable = "GoodsGrp";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodsGrp_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:

                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("GroupCode");
                            StringBuilder qCol = new StringBuilder();
                            StringBuilder qVal = new StringBuilder();
                            StringBuilder qUpd = new StringBuilder();

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    Iterator<String> iter = jo.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GroupCode"))) {
                                            try {
                                                Object value = jo.get(key);
                                                value = value.toString().replaceAll("'", "''");
                                                if (!key.equals("RLClassName")) {
                                                    if (!key.equals("RowsCount")) {
                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");
                                                        if (qUpd.toString().equals("")) {
                                                            qUpd = new StringBuilder("Update GoodsGrp Set " + key + "='" + value + "'");
                                                        } else {
                                                            qUpd.append(",").append(key).append("='").append(value).append("'");
                                                        }
                                                    }
                                                }
                                            } catch (JSONException ignored) {
                                            }
                                        }
                                    }
                                    Cursor d = database.rawQuery("Select Count(*) As cntRec From GoodsGrp Where GroupCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = new StringBuilder("INSERT INTO GoodsGrp( GroupCode " + qCol + ") VALUES(" + code + qVal + ")");
                                    } else {
                                        qCol = new StringBuilder(qUpd + " Where GroupCode=" + code);
                                    }
                                    database.execSQL(qCol.toString());
                                    d.close();
                                    break;

                            }
                            Log.e("bklog_repstrQuery", qCol.toString());

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodsGrp_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateGoodsGrpChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodGroupChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateGoodGroupChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/8"));
        FinalStep = 0;
        RepTable = "GoodGroup";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='GoodGroup_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;

            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:

                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("GoodGroupCode");
                            StringBuilder qCol = new StringBuilder();
                            StringBuilder qVal = new StringBuilder();
                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    Iterator<String> iter = jo.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("GoodGroupCode"))) {
                                            try {
                                                Object value = jo.get(key);
                                                if (!key.equals("RLClassName")) {
                                                    if (!key.equals("RowsCount")) {
                                                        qCol.append(",").append(key);
                                                        qVal.append(",'").append(value).append("'");
                                                    }
                                                }
                                            } catch (JSONException ignored) {
                                            }
                                        }
                                    }
                                    qCol = new StringBuilder("INSERT OR REPLACE INTO GoodGroup( GoodGroupCode " + qCol + ") VALUES(" + code + qVal + ")");
                                    database.execSQL(qCol.toString());
                                    try {
                                        database.execSQL(qCol.toString());
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }

                                    break;
                                case "D":
                                case "d":

                                    try {
                                        database.execSQL("delete from GoodGroup where GoodGroupCode = " + code);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }

                                    break;
                            }
                            Log.e("bklog_repstrQuery", qCol.toString());

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'GoodGroup_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateGoodGroupChange();
            } else {
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodImageChange();

            }

        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }


    public void replicateGoodImageChange() {
        Log.e("test_Rep_e=","1");
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/9"));
        Log.e("test_Rep_e=","2");
        FinalStep = 0;
        RepTable = "KsrImage";
        if (LastRepCode.equals("0")) {
            Log.e("test_Rep_e=","3");
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='KsrImage_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
            Log.e("test_Rep_e=","4");
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);
        Log.e("test_Rep_e=","5");
        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                Log.e("test_Rep_e=","6");
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                Log.e("test_Rep_e=","7");
                switch (state) {
                    case "n":
                    case "N":
                        Log.e("test", "1+"+state);

                        break;
                    default:
                        Log.e("test_Rep_e=","8");
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        Log.e("test_Rep_e=","9");
                        for (int i = 0; i < il; i++) {
                            Log.e("test_Rep_e=","10");
                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("KsrImageCode");
                            String qCol = "";
                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                case "D":
                                case "d":
                                    Log.e("test_Rep_e=","11");
                                    String ObjectRef = jo.getString("ObjectRef");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From KsrImage Where KsrImageCode =" + code, null);

                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));

                                    if (nc == 0) {

                                        qCol = "INSERT INTO KsrImage(KsrImageCode, ObjectRef,IsDefaultImage) Select " + code + "," + ObjectRef + ",'false'";
                                    } else {
                                        qCol = "Delete from KsrImage Where KsrImageCode= " + code ;
                                        image_info.DeleteImage(code);
                                    }

                                    Log.e("test_Rep_e=","12");
                                    try {
                                        database.execSQL(qCol);
                                        Log.e("test_Rep_e=",qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                        Log.e("test_Rep_e=","13");
                                    }
                                    d.close();
                                    break;
                            }
                            Log.e("test_Rep_e=","14");
                            LastRepCode = repcode;
                        }
                        Log.e("test_Rep_e=","15");
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'KsrImage_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                Log.e("test_Rep_e=","16");
                Log.e("test_Rep_e=",e.getMessage());
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                Log.e("test_Rep_e=","17");
                replicateGoodImageChange();
            } else {
                Log.e("test_Rep_e=","18");
                tv_step.setVisibility(View.GONE);
                LastRepCode = "0";
                replicateGoodPropertyValueChange();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "getImageInfo");
                params.put("code", LastRepCode);

                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateGoodPropertyValueChange() {
        tv_rep.setText(NumberFunctions.PerisanNumber("در حال بروز رسانی 10/10"));
        FinalStep = 0;
        RepTable = "PropertyValue";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='PropertyValue_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        tv_step.setVisibility(View.VISIBLE);
                        FinalStep = Integer.parseInt(object.getJSONObject(0).getString("RowsCount"));
                        for (int i = 0; i < il; i++) {

                            tv_step.setText(NumberFunctions.PerisanNumber(FinalStep + "تعداد"));
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("ObjectRef");
                            StringBuilder qCol = new StringBuilder();
//                                String qVal = "";
                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    Iterator<String> iter = jo.keys();
                                    while (iter.hasNext()) {
                                        String key = iter.next();
                                        if ((!key.equals("RLOpType")) & (!key.equals("RepLogDataCode")) & (!key.equals("ObjectRef"))) {
                                            try {
                                                Object value = jo.get(key);
                                                value = value.toString().replaceAll("'", "''");
                                                if (!key.equals("RLClassName")) {
                                                    if (!key.equals("RowsCount")) {
                                                        if (qCol.toString().equals("")) {
                                                            qCol = new StringBuilder(key + "='" + value + "'");
                                                        } else {
                                                            qCol.append(",").append(key).append("='").append(value).append("'");
                                                        }
                                                    }
                                                }
                                            } catch (JSONException ignored) {
                                            }
                                        }
                                    }

                                    qCol = new StringBuilder("Update Good Set " + qCol + " Where GoodCode=" + code);
                                    Log.e("bklog_repstrQuery", qCol.toString());

                                    try {
                                        database.execSQL(qCol.toString());
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    break;
                            }
                            Log.e("bklog_repstrQuery", qCol.toString());

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'PropertyValue_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateGoodPropertyValueChange();
            } else {
                tv_step.setVisibility(View.GONE);
                try {
                    if(dbh.GetColumnscount().equals("0")){
                        MenuBroker();
                        GoodTypeReplication();
                    }else
                        dialog.dismiss();

                }catch (Exception e){

                }
                Toast.makeText(mContext, "بروز رسانی انجام شد", Toast.LENGTH_SHORT).show();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }


    public void replicateCentralChange_customer() {

        dialog();
        database = mContext.openOrCreateDatabase("KowsarDb.sqlite", Context.MODE_PRIVATE, null);

        RepTable = "Central";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue = 'Central_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CentralCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CentralPrivateCode = jo.getString("CentralPrivateCode");
                                    String CentralName = (jo.getString("Title") + jo.getString("FName") + jo.getString("Name")).trim();
                                    CentralName = CentralName.replaceAll("'", "''");

                                    String Manager = jo.getString("Manager");
                                    String Delegacy = jo.getString("Delegacy");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Central Where CentralCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Central(CentralCode, CentralPrivateCode, CentralName, Manager, Delegacy) Select " + code + "," + CentralPrivateCode + ",'" + CentralName + "','" + Manager + "','" + Delegacy + "'";
                                    } else {
                                        qCol = "Update Central Set CentralPrivateCode=" + CentralPrivateCode + ", CentralName='" + CentralName + "', Manager='" + Manager + "', Delegacy='" + Delegacy + "' Where CentralCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Central_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCentralChange_customer();
            } else {
                LastRepCode = "0";
                replicateCityChange_customer();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateCityChange_customer() {

        RepTable = "City";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='City_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CityCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CityName = jo.getString("Name");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From City Where CityCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO City(CityCode, CityName) Select " + code + ",'" + CityName + "'";
                                    } else {
                                        qCol = "Update City Set CityName='" + CityName + "' Where CityCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'City_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCityChange_customer();
            } else {
                LastRepCode = "0";
                replicateAddressChange_customer();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);

    }

    public void replicateAddressChange_customer() {

        RepTable = "Address";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='Address_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);

        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("AddressCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CentralRef = jo.getString("CentralRef");
                                    String CityCode = jo.getString("CityCode");
                                    String Address = jo.getString("Address");
                                    Address = Address.replaceAll("'", "''");

                                    String Phone = jo.getString("Phone");
                                    String Mobile = jo.getString("Mobile");
                                    String MobileName = jo.getString("MobileName");
                                    String Email = jo.getString("Email");
                                    String Fax = jo.getString("Fax");
                                    String ZipCode = jo.getString("ZipCode");
                                    String PostCode = jo.getString("PostCode");

                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Address Where AddressCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Address(AddressCode, CentralRef, CityCode, Address, Phone, Mobile, MobileName, Email, Fax, ZipCode, PostCode) Select " + code + "," + CentralRef + "," + CityCode + ",'" + Address + "','" + Phone + "','" + Mobile + "','" + MobileName + "','" + Email + "','" + Fax + "','" + ZipCode + "','" + PostCode + "'";
                                    } else {
                                        qCol = "Update Address Set CentralRef=" + CentralRef + ", CityCode=" + CityCode + ", Address='" + Address + "', Phone='" + Phone + "', Mobile='" + Mobile + "', MobileName='" + MobileName + "', Email='" + Email + "', Fax='" + Fax + "', ZipCode='" + ZipCode + "', PostCode='" + PostCode + "' Where AddressCode=" + code;
                                    }


                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }

                            Log.e("bklog_repstrQuery", qCol);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Address_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateAddressChange_customer();
            } else {
                LastRepCode = "0";
                replicateCustomerChange_customer();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }

    public void replicateCustomerChange_customer() {

        RepTable = "Customer";
        if (LastRepCode.equals("0")) {
            cursor = database.rawQuery("Select DataValue From Config Where KeyValue ='Customer_LastRepCode'", null);
            cursor.moveToFirst();
            LastRepCode = cursor.getString(0);
            cursor.close();
        }
        RequestQueue queue = Volley.newRequestQueue(mContext);
        StringRequest stringrequste = new StringRequest(Request.Method.POST, url, response -> {
            int il = 0;
            try {
                JSONArray object = new JSONArray(response);
                JSONObject jo = object.getJSONObject(0);
                il = object.length();
                String state = jo.getString("RLOpType");
                switch (state) {
                    case "n":
                    case "N":
                        break;
                    default:
                        for (int i = 0; i < il; i++) {
                            jo = object.getJSONObject(i);
                            String optype = jo.getString("RLOpType");
                            String repcode = jo.getString("RepLogDataCode");
                            String code = jo.getString("CustomerCode");
                            String qCol = "";

                            switch (optype) {
                                case "U":
                                case "u":
                                case "I":
                                case "i":
                                    String CentralRef = jo.getString("CentralRef");
                                    String AddressRef = jo.getString("AddressRef");
                                    String Bestankar = String.valueOf((jo.getDouble("CustomerBestankar") - jo.getDouble("CustomerBedehkar")));
                                    String Active = jo.getString("Active");
                                    String EtebarNaghd = jo.getString("EtebarNaghd");
                                    String EtebarCheck = jo.getString("EtebarCheck");
                                    String Takhfif = jo.getString("Takhfif");
                                    String PriceTip = jo.getString("PriceTip");
                                    Cursor d = database.rawQuery("Select Count(*) AS cntRec From Customer Where CustomerCode =" + code, null);
                                    d.moveToFirst();
                                    int nc = d.getInt(d.getColumnIndex("cntRec"));
                                    if (nc == 0) {
                                        qCol = "INSERT INTO Customer(CustomerCode, CentralRef, AddressRef, Bestankar, Active, EtebarNaghd, EtebarCheck, Takhfif, PriceTip) Select " + code + "," + CentralRef + "," + AddressRef + "," + Bestankar + "," + Active + "," + EtebarNaghd + "," + EtebarCheck + "," + Takhfif + "," + PriceTip;
                                    } else {
                                        qCol = "Update Customer Set CentralRef=" + CentralRef + ", AddressRef=" + AddressRef + ", Bestankar=" + Bestankar + ", Active=" + Active + ", EtebarNaghd=" + EtebarNaghd + ", EtebarCheck=" + EtebarCheck + ", Takhfif=" + Takhfif + ", PriceTip=" + PriceTip + " Where CustomerCode=" + code;
                                    }

                                    try {
                                        database.execSQL(qCol);
                                    }catch (Exception e){
                                        Log.e("test_Rep_e=",e.getMessage());
                                    }
                                    d.close();
                                    break;
                            }
                            Log.e("bklog_repstrQuery", qCol);

                            LastRepCode = repcode;
                        }
                        database.execSQL("Update Config Set DataValue = " + LastRepCode + " Where KeyValue = 'Customer_LastRepCode'");
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (il >= RepRowCount) {
                replicateCustomerChange_customer();
            } else {
                dialog.dismiss();
            }
        }, Throwable::printStackTrace) {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("tag", "repinfo");
                params.put("code", LastRepCode);
                params.put("table", RepTable);
                params.put("reptype", RepType);
                return params;
            }
        };
        queue.add(stringrequste);
    }


}
