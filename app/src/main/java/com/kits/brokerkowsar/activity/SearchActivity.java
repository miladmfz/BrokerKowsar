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
    String srch = "";
    ArrayList<String[]> Multi_buy = new ArrayList<>();
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    FloatingActionButton fab;
    Good_ProSearch_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    int pastVisiblesItems = 0, visibleItemCount, totalItemCount;
    Menu item_multi;
    CallMethod callMethod;
    public String proSearchCondition;
    TextView customer;
    TextView sumfac;
    TextView customer_code;

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

        Handler handler = new Handler();
        handler.postDelayed(this::init, 100);
        handler.postDelayed(dialog1::dismiss, 1000);


    }


    //*************************************************


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        scan = data.getString("scan");
        id = data.getString("id");
        title = data.getString("title");

    }


    public void factorState() {
        if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) == 0) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");
        } else {
            customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }
    }


    public void init() {


        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        handler = new Handler();
        grid = Integer.parseInt(callMethod.ReadString("Grid"));

        final SwitchMaterial mySwitch_activestack = findViewById(R.id.SearchActivityswitch);
        final SwitchMaterial mySwitch_goodamount = findViewById(R.id.SearchActivityswitch_amount);
        final Button pro_search = findViewById(R.id.SearchActivity_pro_search);
        final Button grp_button = findViewById(R.id.SearchActivity_grp);
        final Button ref_fac = findViewById(R.id.SearchActivity_refresh_fac);
        Toolbar toolbar = findViewById(R.id.SearchActivity_toolbar);
        RecyclerView recyclerView_grp = findViewById(R.id.SearchActivity_grp_recy);
        Button btn_scan = findViewById(R.id.SearchActivity_scan);


        customer = findViewById(R.id.SearchActivity_customer);
        sumfac = findViewById(R.id.SearchActivity_sum_factor);
        customer_code = findViewById(R.id.SearchActivity_customer_code);
        recyclerView_good = findViewById(R.id.SearchActivity_allgood);
        fab = findViewById(R.id.SearchActivity_fab);
        edtsearch = findViewById(R.id.SearchActivity_edtsearch);


        toolbar.setTitle(title);

        factorState();

        ref_fac.setOnClickListener(view -> {
            factorState();
        });

        ArrayList<GoodGroup> goodGroups = dbh.getAllGroups(id);
        Grp_Vlist_detail_Adapter adapter4 = new Grp_Vlist_detail_Adapter(goodGroups, this);
        recyclerView_grp.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false));
        recyclerView_grp.setAdapter(adapter4);
        if (goodGroups.size() == 0) {
            recyclerView_grp.getLayoutParams().height = 0;
            grp_button.setVisibility(View.GONE);
        }

        setSupportActionBar(toolbar);

        edtsearch.setOnClickListener(view -> edtsearch.selectAll());
        edtsearch.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

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


        //try {

            goods = dbh.getAllGood(scan, id);

//        }catch (Exception e ){
//            Toast.makeText(this,  "تنظیم جدول مشکل دارد", Toast.LENGTH_SHORT).show();
//            dialog1.dismiss();
//            finish();
//        }
        CallGoodView();

        btn_scan.setOnClickListener(view -> {
            intent = new Intent(SearchActivity.this, ScanCodeActivity.class);
            startActivity(intent);
            finish();
        });

        grp_button.setOnClickListener(view -> {
            if (recyclerView_grp.getVisibility() == View.GONE) {
                recyclerView_grp.setVisibility(View.VISIBLE);
            } else {
                recyclerView_grp.setVisibility(View.GONE);
            }
        });


        pro_search.setOnClickListener(view -> {
            Search_box search_box = new Search_box(this);
            search_box.search_pro();
        });


        if (callMethod.ReadBoolan("ActiveStack")) {
            mySwitch_activestack.setChecked(true);
            mySwitch_activestack.setText("فعال");
        } else {
            mySwitch_activestack.setChecked(false);
            mySwitch_activestack.setText("فعال -غیرفعال");
        }

        if (callMethod.ReadBoolan("GoodAmount")) {
            mySwitch_goodamount.setChecked(true);
            mySwitch_goodamount.setText("موجود");
        } else {
            mySwitch_goodamount.setChecked(false);
            mySwitch_goodamount.setText("هردو");
        }

        mySwitch_activestack.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mySwitch_activestack.setText("فعال");
                callMethod.EditBoolan("ActiveStack", true);
            } else {

                mySwitch_activestack.setText("فعال -غیرفعال");
                callMethod.EditBoolan("ActiveStack", false);
            }
            goods.clear();
            goods = dbh.getAllGood(NumberFunctions.EnglishNumber(edtsearch.getText().toString()), id);
            CallGoodView();
        });

        mySwitch_goodamount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                mySwitch_goodamount.setText("موجود");
                callMethod.EditBoolan("GoodAmount", true);
            } else {
                mySwitch_goodamount.setText("هردو");
                callMethod.EditBoolan("GoodAmount", false);
            }
            goods.clear();
            goods = dbh.getAllGood(NumberFunctions.EnglishNumber(edtsearch.getText().toString()), id);
            CallGoodView();
        });

        fab.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SearchActivity.this);
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
                        for (String[] GoodMulti : Multi_buy) {
                            dbh.InsertPreFactor(callMethod.ReadString("PreFactorCode"),
                                    GoodMulti[0],
                                    AmountMulti,
                                    GoodMulti[1],
                                    "0");
                        }
                        Toast.makeText(this, "به سبد خرید اضافه شد", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                        item_multi.findItem(R.id.menu_multi).setVisible(false);
                        for (Good good : goods) {
                            good.setCheck(false);
                        }
                        Multi_buy.clear();
                        adapter = new Good_ProSearch_Adapter(goods, SearchActivity.this);
                        adapter.multi_select = false;
                        gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);
                        gridLayoutManager.scrollToPosition(pastVisiblesItems + 2);
                        recyclerView_good.setLayoutManager(gridLayoutManager);
                        recyclerView_good.setAdapter(adapter);
                        recyclerView_good.setItemAnimator(new DefaultItemAnimator());
                        fab.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(SearchActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                    }



                } else {
                    Toast.makeText(SearchActivity.this, "تعداد مورد نظر صحیح نمی باشد.", Toast.LENGTH_SHORT).show();
                }
            });


        });

        recyclerView_good.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();
                }
            }
        });


    }


    //
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
                intent = new Intent(SearchActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                startActivity(intent);
            } else {
                Toast.makeText(this, "فاکتوری انتخاب نشده است", Toast.LENGTH_SHORT).show();
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

            adapter = new Good_ProSearch_Adapter(goods, SearchActivity.this);
            gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);
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
        adapter = new Good_ProSearch_Adapter(goods, SearchActivity.this);
        adapter.notifyDataSetChanged();
        gridLayoutManager = new GridLayoutManager(SearchActivity.this, grid);
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
            int b = 0, c = 0;
            for (String[] s : Multi_buy) {
                if (s[0].equals(code_fun))
                    b = c;
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




