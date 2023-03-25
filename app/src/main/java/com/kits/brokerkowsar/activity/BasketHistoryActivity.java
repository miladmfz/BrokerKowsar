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


public class BasketHistoryActivity extends AppCompatActivity {

    private String itemPosition = "0";
    private String searchQuery = "";

    private CallMethod callMethod;
    private ArrayList<Good> goods = new ArrayList<>();
    private DatabaseHelper dbh;
    private DecimalFormat decimalFormat;
    private Handler handler;
    private GridLayoutManager gridLayoutManager;
    private GoodBasketHistoryAdapter adapter;

    private ActivityBuyhistoryBinding binding;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBuyhistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // create a dialog to show progress while loading data
        progressDialog = new Dialog(this);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setContentView(R.layout.rep_prog);
        TextView repw = progressDialog.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        progressDialog.show();

        handler = new Handler();
        handler.postDelayed(this::init, 100);
    }



    // initialize the activity
    public void init() {
        decimalFormat = new DecimalFormat("0,000");
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

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
                    searchQuery = NumberFunctions.EnglishNumber(editable.toString());
                    goods = dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

                    int backgroundResourceId = itemPosition.equals("1") ?
                            R.drawable.bg_round_green_history_line : R.drawable.bg_round_green_history;

                    binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(
                            App.getContext(), backgroundResourceId));
                    // update the list view with the new data
                    adapter.updateList(goods, itemPosition);
                }, Integer.parseInt(callMethod.ReadString("Delay")));
            }
        });
        // set listener for the list item
        binding.BuyHistoryActivityRow.setOnClickListener(view -> {
            int backgroundResourceId;
            if (itemPosition.equals("1")) {
                itemPosition = "0";
                backgroundResourceId = R.drawable.bg_round_green_history_line;
            } else {
                itemPosition = "1";
                backgroundResourceId = R.drawable.bg_round_green_history;
            }
            binding.BuyHistoryActivityRow.setBackground(ContextCompat.getDrawable(
                    this, backgroundResourceId));

            adapter.updateList(goods, itemPosition);
        });

        goods = dbh.getAllPreFactorRows(searchQuery, callMethod.ReadString("PreFactorGood"));

        adapter = new GoodBasketHistoryAdapter(goods, itemPosition, this);
        gridLayoutManager = new GridLayoutManager(this, 1);
        binding.BuyHistoryActivityR1.setLayoutManager(gridLayoutManager);
        binding.BuyHistoryActivityR1.setAdapter(adapter);
        binding.BuyHistoryActivityR1.setItemAnimator(new DefaultItemAnimator());

        binding.BuyHistoryActivityTotalPriceBuy.setText(NumberFunctions.PerisanNumber(
                decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorGood"))))));
        binding.BuyHistoryActivityTotalAmountBuy.setText(NumberFunctions.PerisanNumber(
                dbh.getFactorSumAmount(callMethod.ReadString("PreFactorGood"))));
        binding.BuyHistoryActivityTotalRowBuy.setText(NumberFunctions.PerisanNumber(
                String.valueOf(goods.size())));

        progressDialog.dismiss();
    }
}