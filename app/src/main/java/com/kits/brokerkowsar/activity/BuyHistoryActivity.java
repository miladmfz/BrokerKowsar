package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.GoodBasketHistoryAdapter;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.databinding.ActivityBuyhistoryBinding;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.Good;
import com.kits.brokerkowsar.model.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;


public class BuyHistoryActivity extends AppCompatActivity {

    private String Itemposition = "0";
    private String srch = "";

    CallMethod callMethod;
    private ArrayList<Good> goods = new ArrayList<>();
    private DatabaseHelper dbh;
    DecimalFormat decimalFormat;
    Handler handler;
    GridLayoutManager gridLayoutManager;
    GoodBasketHistoryAdapter adapter;

    ActivityBuyhistoryBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyhistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.rep_prog);
        TextView repw = dialog.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog.show();


        try {
            Handler handler = new Handler();
            handler.postDelayed(this::init, 100);
            handler.postDelayed(dialog::dismiss, 1000);
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }

    //*****************************************************************
    public void init() {

        decimalFormat = new DecimalFormat("0,000");
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        handler = new Handler();


        binding.BuyHistoryActivityEdtsearch.addTextChangedListener(new TextWatcher() {
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
                    srch = NumberFunctions.EnglishNumber(editable.toString());
                    goods = dbh.getAllPreFactorRows(srch, callMethod.ReadString("PreFactorGood"));

                    if (Itemposition.equals("1")) {
                        binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(App.getContext(),
                                R.drawable.bg_round_green_history_line));
                    } else {
                        binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(App.getContext(),
                                R.drawable.bg_round_green_history));
                    }

                    adapter = new GoodBasketHistoryAdapter(goods, Itemposition, App.getContext());
                    gridLayoutManager = new GridLayoutManager(App.getContext(), 1);
                    binding.BuyHistoryActivityR1.setLayoutManager(gridLayoutManager);
                    binding.BuyHistoryActivityR1.setAdapter(adapter);
                    binding.BuyHistoryActivityR1.setItemAnimator(new DefaultItemAnimator());
                }, Integer.parseInt(callMethod.ReadString("Delay")));
            }
        });

        binding.BuyHistoryActivityRow.setOnClickListener(view -> {

            adapter = new GoodBasketHistoryAdapter(goods, Itemposition, this);
            gridLayoutManager = new GridLayoutManager(this, 1);
            binding.BuyHistoryActivityR1.setLayoutManager(gridLayoutManager);
            binding.BuyHistoryActivityR1.setAdapter(adapter);
            binding.BuyHistoryActivityR1.setItemAnimator(new DefaultItemAnimator());
            if (Itemposition.equals("1")) {
                Itemposition = "0";
                binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_round_green_history_line));
            } else {
                Itemposition = "1";
                binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_round_green_history));
            }
        });

        goods = dbh.getAllPreFactorRows(srch, callMethod.ReadString("PreFactorGood"));

        adapter = new GoodBasketHistoryAdapter(goods, Itemposition, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.BuyHistoryActivityR1.setLayoutManager(gridLayoutManager);
        binding.BuyHistoryActivityR1.setAdapter(adapter);
        binding.BuyHistoryActivityR1.setItemAnimator(new DefaultItemAnimator());

        binding.BuyHistoryActivityTotalPriceBuy.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorGood"))))));
        binding.BuyHistoryActivityTotalAmountBuy.setText(NumberFunctions.PerisanNumber(dbh.getFactorSumAmount(callMethod.ReadString("PreFactorGood"))));
        binding.BuyHistoryActivityTotalRowBuy.setText(NumberFunctions.PerisanNumber(String.valueOf(goods.size())));


    }


}
