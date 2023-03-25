package com.kits.brokerkowsar.activity;


import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.adapters.GoodBasketAdapter;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.databinding.ActivityBuyBinding;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class BasketActivity extends AppCompatActivity {

    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");


    private Action action;
    private String PreFac = "0";
    private DatabaseHelper dbh;
    private ArrayList<Good> goods;
    private GoodBasketAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private CallMethod callMethod;

    private ActivityBuyBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBuyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent();
        config();
        init();
    }

    private void config() {
        action = new Action(this);
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        setSupportActionBar(binding.BuyActivityToolbar);
    }
    public void init() {
        // Initialize adapter and layout manager
        goods = dbh.getAllPreFactorRows("", PreFac);
        adapter = new GoodBasketAdapter(goods, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.BuyActivityR1.setLayoutManager(gridLayoutManager);
        binding.BuyActivityR1.setAdapter(adapter);
        binding.BuyActivityR1.setItemAnimator(new DefaultItemAnimator());
        binding.BuyActivityR1.setVisibility(View.VISIBLE);

        // Set scroll position
        int position = callMethod.readInt("BasketItemView");
        if (position >= 0 && position < adapter.getItemCount()) {
            binding.BuyActivityR1.scrollToPosition(position);
        } else {
            binding.BuyActivityR1.scrollToPosition(0);
            callMethod.EditString("BasketItemView", "0");
        }
        // Set scroll listener
        binding.BuyActivityR1.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NotNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    callMethod.EditString("BasketItemView", String.valueOf(gridLayoutManager.findFirstVisibleItemPosition()));
                }
            }
        });

        // Set total price, amount, customer, and row count
        String factorSum = dbh.getFactorSum(PreFac);
        String factorSumAmount = dbh.getFactorSumAmount(PreFac);
        String factorCustomer = dbh.getFactorCustomer(PreFac);
        String goodsCount = String.valueOf(goods.size());
        binding.BuyActivityTotalPriceBuy.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(factorSum))));
        binding.BuyActivityTotalAmountBuy.setText(NumberFunctions.PerisanNumber(factorSumAmount));
        binding.BuyActivityTotalCustomerBuy.setText(NumberFunctions.PerisanNumber(factorCustomer));
        binding.BuyActivityTotalRowBuy.setText(NumberFunctions.PerisanNumber(goodsCount));

        // Set total delete button listener
        binding.BuyActivityTotalDelete.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا مایل به خالی کردن سبد خرید می باشید؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {
                        dbh.DeletePreFactorRow(PreFac, "0");
                        finish();
                        callMethod.showToast("سبد خرید با موفقیت حذف گردید!");
                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {})
                    .show();
        });

        // Set test button listener
        binding.BuyActivityTest.setOnClickListener(view -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا فاکتور ارسال گردد؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> action.sendfactor(PreFac))
                    .setNegativeButton("خیر", (dialogInterface, i) -> {})
                    .show();
        });
    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        PreFac = data.getString("PreFac");
    }


}
