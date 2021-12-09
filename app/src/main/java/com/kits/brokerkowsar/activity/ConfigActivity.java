package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
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
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        init();

    }


//****************************************************

    public void init() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        replication = new Replication(this);


        Button tohome = findViewById(R.id.config_to_home);
        Button toreg = findViewById(R.id.config_to_reg);
        Button repculomn = findViewById(R.id.configreplicationcolumn);


        TextView borker = findViewById(R.id.config_borker);
        TextView grid = findViewById(R.id.config_grid);
        TextView delay = findViewById(R.id.config_delay);
        TextView titlesize = findViewById(R.id.config_titlesize);
        TextView bodysize = findViewById(R.id.config_bodysize);
        TextView itemamount = findViewById(R.id.config_itemamount);
        TextView sum_factor = findViewById(R.id.config_sum_factor);

        SwitchMaterial regselloff = findViewById(R.id.config_selloff);
        SwitchMaterial real_amount = findViewById(R.id.config_real_amount);
        SwitchMaterial auto_rep = findViewById(R.id.config_autorep);


        sum_factor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));


        UserInfo auser = dbh.LoadPersonalInfo();

        borker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));


        grid.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("Grid")))));
        delay.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("Delay")))));
        itemamount.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("ItemAmount")))));
        titlesize.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("TitleSize")))));
        bodysize.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("BodySize")))));

        regselloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        real_amount.setChecked(callMethod.ReadBoolan("RealAmount"));
        auto_rep.setChecked(callMethod.ReadBoolan("auto_rep"));

        tohome.setOnClickListener(view -> {
            intent = new Intent(ConfigActivity.this, NavActivity.class);
            startActivity(intent);
        });


        toreg.setOnClickListener(view -> {
            intent = new Intent(ConfigActivity.this, RegistrationActivity.class);
            startActivity(intent);
        });

        repculomn.setOnClickListener(v -> {

            dbh.deleteColumn();
            replication.GoodTypeReplication();
            replication.MenuBroker();
            replication.BrokerStack();
            dbh.DatabaseCreate();
        });

    }


    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();

    }


}
