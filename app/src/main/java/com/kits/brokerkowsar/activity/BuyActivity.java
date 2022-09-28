package com.kits.brokerkowsar.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.GoodBasketAdapter;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BuyActivity extends AppCompatActivity {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");

    private Action action;
    private String PreFac = "0";
    private DatabaseHelper dbh;
    ArrayList<Good> goods;
    GoodBasketAdapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    CallMethod callMethod;

    Toolbar toolbar;
    TextView tv_row;
    TextView tv_price;
    TextView tv_customer;
    TextView tv_amount;
    Button btn_total_delete;
    Button btn_final_buy_test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);


        intent();
        Config();

        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }

    //*****************************************************************


    public void Config() {
        action = new Action(this);
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

        toolbar = findViewById(R.id.BuyActivity_toolbar);
        tv_row = findViewById(R.id.BuyActivity_total_row_buy);
        tv_price = findViewById(R.id.BuyActivity_total_price_buy);
        tv_customer = findViewById(R.id.BuyActivity_total_customer_buy);
        tv_amount = findViewById(R.id.BuyActivity_total_amount_buy);
        btn_total_delete = findViewById(R.id.BuyActivity_total_delete);
        btn_final_buy_test = findViewById(R.id.BuyActivity_test);
        recyclerView = findViewById(R.id.BuyActivity_R1);
        setSupportActionBar(toolbar);

    }

    public void init() {


        goods = dbh.getAllPreFactorRows("", PreFac);
        adapter = new GoodBasketAdapter(goods, this);
        if (adapter.getItemCount() == 0) {
            callMethod.showToast("سبد خرید خالی می باشد");
        }
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setVisibility(View.VISIBLE);

        try {
            recyclerView.scrollToPosition(Integer.parseInt(callMethod.ReadString("BasketItemView")) - 1);
        } catch (Exception e) {
            recyclerView.scrollToPosition(0);
            callMethod.EditString("BasketItemView", "0");

        }


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    callMethod.EditString("BasketItemView", String.valueOf(gridLayoutManager.findFirstVisibleItemPosition()));
                }
            }
        });

        tv_price.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(PreFac)))));
        tv_amount.setText(NumberFunctions.PerisanNumber(dbh.getFactorSumAmount(PreFac)));
        tv_customer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(PreFac)));
        tv_row.setText(NumberFunctions.PerisanNumber(String.valueOf(goods.size())));


        btn_total_delete.setOnClickListener(view ->
                new AlertDialog.Builder(this)
                        .setTitle("توجه")
                        .setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {
                            dbh.DeletePreFactorRow(PreFac, "0");
                            finish();
                            callMethod.showToast("سبد خرید با موفقیت حذف گردید!");
                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show()
        );


        btn_final_buy_test.setOnClickListener(view ->
                new android.app.AlertDialog.Builder(this)
                        .setTitle("توجه")
                        .setMessage("آیا فاکتور ارسال گردد؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(PreFac))
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                        })
                        .show()
        );


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getString("PreFac");
    }


}
