package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.kits.brokerkowsar.adapters.Grp_Vlist_detail_Adapter;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Search_box;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity {


    private ArrayList<Good> goods = new ArrayList<>();
    private Integer grid;
    public static String scan = "";
    public String id = "";
    public String title = "";
    Dialog dialog1;
    private RecyclerView recyclerView_good;
    private EditText edtsearch;
    Intent intent;
    DatabaseHelper dbh;
    Handler handler;
    ArrayList<String[]> Multi_buy = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    FloatingActionButton fab;
    Good_ProSearch_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Menu item_multi;
    CallMethod callMethod;
    public String proSearchCondition;
    TextView tv_customer;
    TextView tv_sumfac;
    TextView tv_customer_code;
    ArrayList<GoodGroup> goodGroups;
    SwitchMaterial sm_activestack;
    SwitchMaterial sm_goodamount;
    Button btn_pro_search;
    Button btn_grp_button;
    Button btn_ref_fac;
    Button btn_scan;
    Toolbar toolbar;
    RecyclerView recyclerView_grp;
    Grp_Vlist_detail_Adapter grp_adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
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


    //*************************************************


    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        handler = new Handler();
        grid = Integer.parseInt(callMethod.ReadString("Grid"));

        sm_activestack = findViewById(R.id.SearchActivityswitch);
        sm_goodamount = findViewById(R.id.SearchActivityswitch_amount);
        btn_pro_search = findViewById(R.id.SearchActivity_pro_search);
        btn_grp_button = findViewById(R.id.SearchActivity_grp);
        btn_ref_fac = findViewById(R.id.SearchActivity_refresh_fac);
        toolbar = findViewById(R.id.SearchActivity_toolbar);
        recyclerView_grp = findViewById(R.id.SearchActivity_grp_recy);
        btn_scan = findViewById(R.id.SearchActivity_scan);


        tv_customer = findViewById(R.id.SearchActivity_customer);
        tv_sumfac = findViewById(R.id.SearchActivity_sum_factor);
        tv_customer_code = findViewById(R.id.SearchActivity_customer_code);
        recyclerView_good = findViewById(R.id.SearchActivity_allgood);
        fab = findViewById(R.id.SearchActivity_fab);
        edtsearch = findViewById(R.id.SearchActivity_edtsearch);

    }

    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        scan = data.getString("scan");
        id = data.getString("id");
        title = data.getString("title");

    }


    public void factorState() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            tv_customer.setText("فاکتوری انتخاب نشده");
            tv_sumfac.setText("0");
        } else {
            tv_customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            tv_sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            tv_customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }
    }


    public void init() {



        toolbar.setTitle(title);

        factorState();

        btn_ref_fac.setOnClickListener(view -> {
            factorState();
        });

        goodGroups = dbh.getAllGroups(id);
        grp_adapter = new Grp_Vlist_detail_Adapter(goodGroups, this);
        recyclerView_grp.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView_grp.setAdapter(grp_adapter);
        if (goodGroups.size() == 0) {
            recyclerView_grp.getLayoutParams().height = 0;
            btn_grp_button.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);

        edtsearch.setOnClickListener(view -> edtsearch.selectAll());
        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
                    @Override
                    public void afterTextChanged(final Editable editable) {
                        handler.removeCallbacksAndMessages(null);
                        handler.postDelayed(() -> {
                            goods.clear();
                            goods = dbh.getAllGood(NumberFunctions.EnglishNumber(editable.toString()), id);
                            CallGoodView();
                        }, Integer.parseInt(callMethod.ReadString("Delay")));

                        handler.postDelayed(() -> edtsearch.selectAll(), 5000);
                    }
                });


        try {
            goods = dbh.getAllGood(scan, id);
        }catch (Exception e ){
            callMethod.showToast(  "تنظیم۱۲۳ جدول مشکل دارد");
            dialog1.dismiss();
            finish();
        }

        CallGoodView();

        btn_scan.setOnClickListener(view -> {
            intent = new Intent(this, ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });

        btn_grp_button.setOnClickListener(view -> {
            if (recyclerView_grp.getVisibility() == View.GONE) {
                recyclerView_grp.setVisibility(View.VISIBLE);
            } else {
                recyclerView_grp.setVisibility(View.GONE);
            }
        });


        btn_pro_search.setOnClickListener(view -> {
            Search_box search_box = new Search_box(this);
            search_box.search_pro();
        });


        if (callMethod.ReadBoolan("ActiveStack")) {
            sm_activestack.setChecked(true);
            sm_activestack.setText("فعال");
        } else {
            sm_activestack.setChecked(false);
            sm_activestack.setText("فعال -غیرفعال");
        }

        if (callMethod.ReadBoolan("GoodAmount")) {
            sm_goodamount.setChecked(true);
            sm_goodamount.setText("موجود");
        } else {
            sm_goodamount.setChecked(false);
            sm_goodamount.setText("هردو");
        }


        sm_activestack.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sm_activestack.setText("فعال");
                callMethod.EditBoolan("ActiveStack", true);
            } else {

                sm_activestack.setText("فعال -غیرفعال");
                callMethod.EditBoolan("ActiveStack", false);
            }
            goods.clear();
            goods = dbh.getAllGood(NumberFunctions.EnglishNumber(edtsearch.getText().toString()), id);
            CallGoodView();
        });

        sm_goodamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                sm_goodamount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                sm_goodamount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }
            goods.clear();
            goods = dbh.getAllGood(NumberFunctions.EnglishNumber(edtsearch.getText().toString()), id);
            CallGoodView();
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
                String AmountMulti = amount_mlti.getText().toString();
                if (!AmountMulti.equals("")) {

                    if (Integer.parseInt(AmountMulti) != 0) {
                        for (String[] singlebuy : Multi_buy) {
                            dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"),
                                    singlebuy[0],
                                    AmountMulti,
                                    "0",
                                    "0");
                        }
                        callMethod.showToast( "به سبد خرید اضافه شد");

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
                        recyclerView_good.setLayoutManager(gridLayoutManager);
                        recyclerView_good.setAdapter(adapter);
                        recyclerView_good.setItemAnimator(new DefaultItemAnimator());
                        fab.setVisibility(View.GONE);
                    } else {
                        callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                    }
                } else {
                    callMethod.showToast("تعداد مورد نظر صحیح نمی باشد.");
                }
            });


        });

        recyclerView_good.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    visibleItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                }
            }
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
                startActivity(intent);
            } else {
                callMethod.showToast( "فاکتوری انتخاب نشده است");
            }
            return true;
        }
        if (item.getItemId() == R.id.menu_multi) {
            item_multi.findItem(R.id.menu_multi).setVisible(false);
            for (Good good : goods) {
                good.setCheck(false);
            }
            Multi_buy.clear();
            adapter.multi_select = false;

            adapter = new Good_ProSearch_Adapter(goods,this);
            gridLayoutManager = new GridLayoutManager(this, grid);
            gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
            recyclerView_good.setLayoutManager(gridLayoutManager);
            recyclerView_good.setAdapter(adapter);
            recyclerView_good.setItemAnimator(new DefaultItemAnimator());
            fab.setVisibility(View.GONE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void proSearchResult() {
        goods.clear();
        goods = dbh.getAllGood_Extended(proSearchCondition, id);
        CallGoodView();
    }

    public void CallGoodView() {
        adapter = new Good_ProSearch_Adapter(goods,this);
        adapter.notifyDataSetChanged();
        gridLayoutManager = new GridLayoutManager(this, grid);
        recyclerView_good.setLayoutManager(gridLayoutManager);
        recyclerView_good.setAdapter(adapter);
        recyclerView_good.setItemAnimator(new DefaultItemAnimator());
    }


    public void good_select_function(String code_fun, String price_fun, String flag) {

        if (flag.equals("1")) {
            fab.setVisibility(View.VISIBLE);
            Multi_buy.add(new String[]{code_fun, price_fun});

            item_multi.findItem(R.id.menu_multi).setVisible(true);

        } else {
            int objectrow = 0, conter = 0;
            for (String[] singlebuy : Multi_buy) {
                if (singlebuy[0].equals(code_fun))
                    objectrow = conter;
                conter++;
            }
            Multi_buy.remove(objectrow);
            if (Multi_buy.size() < 1) {
                fab.setVisibility(View.GONE);
                adapter.multi_select = false;

                item_multi.findItem(R.id.menu_multi).setVisible(false);
            }
        }
    }


}




