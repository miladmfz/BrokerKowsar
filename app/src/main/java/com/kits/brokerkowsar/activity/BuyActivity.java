package com.kits.brokerkowsar.activity;


import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.Good_buy_Adapter;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class BuyActivity extends AppCompatActivity {


    private Action action;
    private String PreFac = "0";
    private DatabaseHelper dbh;
    ArrayList<Good> goods;
    Good_buy_Adapter adapter;
    GridLayoutManager gridLayoutManager;
    RecyclerView recyclerView;
    CallMethod callMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);


        intent();

        Handler handler = new Handler();
        handler.postDelayed(this::init, 100);


    }

    //*****************************************************************
    public void init() {

        action = new Action(this);
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));


        Toolbar toolbar = findViewById(R.id.BuyActivity_toolbar);
        TextView row = findViewById(R.id.BuyActivity_total_row_buy);
        TextView price = findViewById(R.id.BuyActivity_total_price_buy);
        TextView customer = findViewById(R.id.BuyActivity_total_customer_buy);
        TextView amount = findViewById(R.id.BuyActivity_total_amount_buy);
        Button total_delete = findViewById(R.id.BuyActivity_total_delete);
        Button final_buy_test = findViewById(R.id.BuyActivity_test);
        recyclerView = findViewById(R.id.BuyActivity_R1);


        setSupportActionBar(toolbar);


        goods = dbh.getAllPreFactorRows("", PreFac);

        adapter = new Good_buy_Adapter(goods, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        gridLayoutManager.scrollToPosition(Integer.parseInt(callMethod.ReadString("BasketItemView"))-1);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    callMethod.EditString("BasketItemView", String.valueOf(gridLayoutManager.findFirstVisibleItemPosition()));
                }
            }
        });
        price.setText(NumberFunctions.PerisanNumber(dbh.getFactorSum(PreFac)));
        amount.setText(NumberFunctions.PerisanNumber(dbh.getFactorSumAmount(PreFac)));
        customer.setText(NumberFunctions.PerisanNumber(dbh.getFactorCustomer(PreFac)));
        row.setText(NumberFunctions.PerisanNumber(String.valueOf(goods.size())));


        final_buy_test.setOnClickListener(view -> new android.app.AlertDialog.Builder(this)
                .setTitle("توجه")
                .setMessage("آیا فاکتور ارسال گردد؟")
                .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(PreFac))
                .setNegativeButton("خیر", (dialogInterface, i) -> {
                })
                .show());


        total_delete.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("توجه")
                .setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟")
                .setPositiveButton("بله", (dialogInterface, i) -> {
                    dbh.DeletePreFactorRow(PreFac, "0");
                    finish();
                    Toast.makeText(this, "سبد خرید با موفقیت حذف گردید!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("خیر", (dialogInterface, i) -> {
                })
                .show());

        if (dbh.getAllGood_pfcode().size() < 1) {
            Toast.makeText(this, "کالای برای اصلاح موجود نمی باشد", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getString("PreFac");
    }



}
