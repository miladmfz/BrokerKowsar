package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
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

    Button btn_toreg;

    TextView tv_borker;
    TextView tv_grid;
    TextView tv_delay;
    TextView tv_titlesize;
    TextView tv_bodysize;
    TextView tv_itemamount;
    TextView tv_sum_factor;
    TextView tv_phonenumber;
    SwitchMaterial sw_regselloff;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);
        Config();

        try {
            init();
        }catch (Exception e){
            callMethod.showToast(e.getMessage());
        }


    }


    //****************************************************
    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);

        btn_toreg = findViewById(R.id.config_to_reg);

        tv_borker = findViewById(R.id.config_borker);
        tv_grid = findViewById(R.id.config_grid);
        tv_delay = findViewById(R.id.config_delay);
        tv_titlesize = findViewById(R.id.config_titlesize);
        tv_bodysize = findViewById(R.id.config_bodysize);
        tv_itemamount = findViewById(R.id.config_itemamount);
        tv_sum_factor = findViewById(R.id.config_sum_factor);
        tv_phonenumber = findViewById(R.id.config_phonenumber);
        sw_regselloff = findViewById(R.id.config_selloff);

        auser = dbh.LoadPersonalInfo();
    }


    public void init() {


        tv_sum_factor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        tv_borker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));
        tv_grid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        tv_delay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        tv_itemamount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("ItemAmount")));
        tv_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        tv_bodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        tv_phonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));

        sw_regselloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);

        btn_toreg.setOnClickListener(view -> {
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
