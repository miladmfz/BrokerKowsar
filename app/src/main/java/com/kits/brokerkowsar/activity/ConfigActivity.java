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

    DatabaseHelper dbh;
    DecimalFormat decimalFormat = new DecimalFormat("0,000");
    CallMethod callMethod;
    Intent intent;
    Replication replication;
    UserInfo auser;

    ActivityConfigBinding binding;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();

        try {
            init();
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }


    //****************************************************
    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);
        auser = dbh.LoadPersonalInfo();
    }


    public void init() {


        binding.configSumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        binding.configBorker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));
        binding.configGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.configDelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.configItemamount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("ItemAmount")));
        binding.configTitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.configBodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.configPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));

        binding.configSelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.configAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));

        binding.configBtnToReg.setOnClickListener(view -> {
            intent = new Intent(this, RegistrationActivity.class);
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
