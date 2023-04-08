package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.databinding.ActivityConfigBinding;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;

import java.text.DecimalFormat;


public class ConfigActivity extends AppCompatActivity {

    private DatabaseHelper dbh;
    private CallMethod callMethod;
    private ActivityConfigBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize variables and configurations
        Config();

        // Populate views with data
        populateViews();

        // Set listeners for buttons
        setButtonListeners();
    }

    private void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        Replication replication = new Replication(this);

    }

    private void populateViews() {
        DecimalFormat decimalFormat = new DecimalFormat("0,000");
        binding.configSumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        binding.configBorker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.configGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.configDelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.configTitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.configBodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.configPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));

        binding.configSelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.configAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));
    }

    private void setButtonListeners() {
        binding.configBtnToReg.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegistrationActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}

