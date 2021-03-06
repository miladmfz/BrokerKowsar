package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.adapters.Good_ProSearch_Adapter;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class Search_date_detailActivity extends AppCompatActivity {
    CallMethod callMethod;

    private final Integer conter = 0;
    private Integer grid;
    private String date;

    private String lastDate;
    ArrayList<String[]> Multi_buy = new ArrayList<>();
    private ArrayList<Good> goods = new ArrayList<>();
    DatabaseHelper dbh;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    FloatingActionButton fab;
    Good_ProSearch_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    int pastVisiblesItems = 0;
    Menu item_multi;
    String year;
    String mount;
    String day;
    PersianCalendar calendar1;

    public static String scan = "";
    public String title = "";
    Intent intent;

    Button btn_refresh;
    Toolbar toolbar;
    TextView tv_customer;
    TextView tv_sumfac;
    TextView tv_customer_code;
    SwitchMaterial sm_goodamount;
    Button btn_search;
    EditText ed_search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_date_detail);


        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("???? ?????? ???????????? ??????????????");
        dialog1.show();

        intent();
        Config();
        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog1::dismiss, 1000);
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }


    }

    //***************************************************


    public void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        tv_customer = findViewById(R.id.Search_date_detailActivity_customer);
        tv_sumfac = findViewById(R.id.Search_date_detailActivity_sum_factor);
        tv_customer_code = findViewById(R.id.Search_date_detailActivity_customer_code);

        toolbar = findViewById(R.id.search_date_toolbar);
        btn_refresh = findViewById(R.id.Search_date_detailActivity_refresh_fac);
        fab = findViewById(R.id.search_date_fab);
        calendar1 = new PersianCalendar();
        recyclerView = findViewById(R.id.search_date_recycler);
        btn_search = findViewById(R.id.search_date_btn);
        ed_search = findViewById(R.id.search_date);
        sm_goodamount = findViewById(R.id.search_date_switch_amount);
        setSupportActionBar(toolbar);

    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        date = data.getString("date");
    }

    public void init() {



        calendar1.setPersianDate(
                calendar1.getPersianYear(),
                calendar1.getPersianMonth(),
                calendar1.getPersianDay()  - Integer.parseInt(date)
        );

        year="";
        mount="0";
        day="0";

        year=year+calendar1.getPersianYear();

        if(String.valueOf(calendar1.getPersianMonth()).equals("11")){
            mount="12";
        }else if(String.valueOf(calendar1.getPersianMonth()).equals("00")){
            mount="01";
        }else{
            mount=mount+(calendar1.getPersianMonth()+1);
        }
        day=day+(calendar1.getPersianDay());
        lastDate = year+"/"+mount.substring(mount.length()-2)+"/"+day.substring(day.length()-2);


        grid = Integer.parseInt(callMethod.ReadString("Grid"));
        ed_search.setText(date);
        CallGoodView();



        btn_search.setOnClickListener(view -> {
            calendar1=new PersianCalendar();
            if (!ed_search.getText().toString().equals("")) {
                date = ed_search.getText().toString();
            } else {
                date = "7";
            }

            calendar1.setPersianDate(
                    calendar1.getPersianYear(),
                    calendar1.getPersianMonth(),
                    calendar1.getPersianDay()  - Integer.parseInt(date)
            );

            year="";
            mount="0";
            day="0";

            year=year+calendar1.getPersianYear();

            if(String.valueOf(calendar1.getPersianMonth()).equals("11")){
                mount="12";
            }else if(String.valueOf(calendar1.getPersianMonth()).equals("00")){
                mount="01";
            }else{
                mount=mount+(calendar1.getPersianMonth()+1);
            }
            day=day+(calendar1.getPersianDay());
            lastDate = year+"/"+mount.substring(mount.length()-2)+"/"+day.substring(day.length()-2);
            CallGoodView();

        });


        btn_refresh.setOnClickListener(view -> {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
                tv_customer.setText("?????????????? ???????????? ????????");
                tv_sumfac.setText("0");
            } else {
                tv_customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
                tv_sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getFactorSum(callMethod.ReadString("PreFactorCode")))));
                tv_customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
            }
        });

        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            tv_customer.setText("?????????????? ???????????? ????????");
            tv_sumfac.setText("0");
        } else {
            tv_customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            tv_sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            tv_customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }



        if (callMethod.ReadBoolan("GoodAmount")) {
            sm_goodamount.setChecked(true);
            sm_goodamount.setText("??????????");
        } else {
            sm_goodamount.setChecked(false);
            sm_goodamount.setText("????????");
        }

        sm_goodamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sm_goodamount.setText("??????????");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                sm_goodamount.setText("????????");
                callMethod.EditBoolan("GoodAmount", false);
            }
            if (conter == 0) {
                CallGoodView();
            }
        });

        fab.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.box_multi_buy);
            Button boxbuy = dialog.findViewById(R.id.box_multi_buy_btn);
            final EditText amount_mlti = dialog.findViewById(R.id.box_multi_buy_amount);
            final TextView tv = dialog.findViewById(R.id.box_multi_buy_factor);
            tv.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            dialog.show();
            amount_mlti.requestFocus();
            amount_mlti.postDelayed(() -> {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(amount_mlti, InputMethodManager.SHOW_IMPLICIT);
            }, 500);

            boxbuy.setOnClickListener(view -> {
                String amo = amount_mlti.getText().toString();
                if (!amo.equals("")) {
                    if (Integer.parseInt(amo) != 0) {
                        for (String[] singlebuy : Multi_buy) {
                            if (singlebuy[1].equals("")) singlebuy[1] = "-1";
                            String pf = callMethod.ReadString("PreFactorCode");
                            dbh.InsertPreFactor(pf, singlebuy[0], amo, "0", "0");
                        }
                        callMethod.showToast("???? ?????? ???????? ?????????? ????");

                        dialog.dismiss();
                        item_multi.findItem(R.id.menu_multi).setVisible(false);
                        for (Good good : goods) {
                            good.setCheck(false);
                        }
                        Multi_buy.clear();
                        adapter = new Good_ProSearch_Adapter(goods,this);
                        adapter.multi_select = false;
                        gridLayoutManager = new GridLayoutManager(this, grid);
                        gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                        recyclerView.setLayoutManager(gridLayoutManager);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                        fab.setVisibility(View.GONE);
                    } else {
                        callMethod.showToast("?????????? ???????? ?????? ???????? ?????? ????????.");
                    }
                } else {
                    callMethod.showToast("?????????? ???????? ?????? ???????? ?????? ????????.");
                }
            });
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        item_multi = menu;

        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);

            } else {
                callMethod.showToast( "?????????????? ???????????? ???????? ??????");
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_buy.clear();
            adapter = new Good_ProSearch_Adapter(goods,this);
            adapter.multi_select = false;

            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            recyclerView.setLayoutManager(gridLayoutManager);
            recyclerView.setAdapter(adapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            fab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void CallGoodView() {

        goods.clear();
        goods = dbh.getAllGood_ByDate(lastDate);
        adapter = new Good_ProSearch_Adapter(goods,this);
        gridLayoutManager = new GridLayoutManager(this, grid);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    public void good_select_function(String code_fun, String price_fun, String flag) {
        if (flag.equals("1")) {
            fab.setVisibility(View.VISIBLE);
            Multi_buy.add(new String[]{code_fun, price_fun});
            item_multi.findItem(R.id.menu_multi).setVisible(true);

        } else {
            int b = 0, c = 0;
            for (String[] s : Multi_buy) {
                if (s[0].equals(code_fun)) b = c;
                c++;
            }
            Multi_buy.remove(b);
            if (Multi_buy.size() < 1) {
                fab.setVisibility(View.GONE);
                adapter.multi_select = false;

                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }

}
