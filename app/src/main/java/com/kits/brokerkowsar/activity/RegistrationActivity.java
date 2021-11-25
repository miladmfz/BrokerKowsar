package com.kits.brokerkowsar.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;


public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    CallMethod callMethod;

    private EditText regborker, reggrid, regdelay, regitemamount,regtitlesize,regbodysize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);


        init();

    }


    public void init() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));


        Button regbtn = findViewById(R.id.Registr_btn);

        regborker = findViewById(R.id.Registr_borker);
        reggrid = findViewById(R.id.Registr_grid);
        regdelay = findViewById(R.id.Registr_delay);
        regtitlesize = findViewById(R.id.Registr_titlesize);
        regbodysize = findViewById(R.id.Registr_bodysize);
        regitemamount = findViewById(R.id.Registr_itemamount);


        SwitchMaterial regselloff = findViewById(R.id.Registr_selloff);
        SwitchMaterial real_amount = findViewById(R.id.Registr_real_amount);
        SwitchMaterial auto_rep = findViewById(R.id.Registr_autorep);


        UserInfo auser = dbh.LoadPersonalInfo();

        regborker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));
        reggrid.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("Grid")))));
        regdelay.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("Delay")))));
        regitemamount.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("ItemAmount")))));
        regtitlesize.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("TitleSize")))));
        regbodysize.setText(NumberFunctions.PerisanNumber(String.valueOf(Integer.parseInt(callMethod.ReadString("BodySize")))));


        regselloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);

        real_amount.setChecked(callMethod.ReadBoolan("RealAmount"));

        auto_rep.setChecked(callMethod.ReadBoolan("auto_rep"));


        regselloff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Integer.parseInt(callMethod.ReadString("SellOff")) == 0) {
                Toast.makeText(RegistrationActivity.this, "بله", Toast.LENGTH_SHORT).show();
                callMethod.EditString("SellOff", "1");
            } else {
                callMethod.EditString("SellOff", "0");
                Toast.makeText(RegistrationActivity.this, "خیر", Toast.LENGTH_SHORT).show();


            }
        });


        real_amount.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                callMethod.EditBoolan("RealAmount", true);
                Toast.makeText(RegistrationActivity.this, "بله", Toast.LENGTH_SHORT).show();
            } else {
                callMethod.EditBoolan("RealAmount", false);
                Toast.makeText(RegistrationActivity.this, "خیر", Toast.LENGTH_SHORT).show();
            }
        });

        auto_rep.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                callMethod.EditBoolan("auto_rep", true);
                Toast.makeText(RegistrationActivity.this, "بله", Toast.LENGTH_SHORT).show();
            } else {
                callMethod.EditBoolan("auto_rep", false);
                Toast.makeText(RegistrationActivity.this, "خیر", Toast.LENGTH_SHORT).show();
            }
        });


        regbtn.setOnClickListener(view -> {
            Registration();
            callMethod.EditString("Grid", NumberFunctions.EnglishNumber(reggrid.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(regdelay.getText().toString()));
            callMethod.EditString("ItemAmount", NumberFunctions.EnglishNumber(regitemamount.getText().toString()));
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(regtitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(regbodysize.getText().toString()));
            finish();
        });


    }


    public void Registration() {
        UserInfo auser = new UserInfo();
        auser.setBrokerCode(NumberFunctions.EnglishNumber(regborker.getText().toString()));

        dbh.SavePersonalInfo(auser);
    }


}
