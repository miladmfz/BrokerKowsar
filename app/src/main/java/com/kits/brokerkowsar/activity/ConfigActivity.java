package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.kits.brokerkowsar.R;
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

     void Config() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));

    }

     void populateViews() {
        DecimalFormat decimalFormat = new DecimalFormat("0,000");
        //binding.configSumFactor.setText(NumberFunctions.PerisanNumber(decimalFormat.format(dbh.getsum_sumfactor())));
        binding.configBorker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.configGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.configDelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.configTitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.configBodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.configPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));
        binding.configPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));

        binding.configSelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.configAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));
        binding.configKeyboardrunnable.setChecked(callMethod.ReadBoolan("keyboardRunnable"));
        binding.configDetailshow.setChecked(callMethod.ReadBoolan("ShowDetail"));
        binding.configLineview.setChecked(callMethod.ReadBoolan("LineView"));
    }

     void setButtonListeners() {
        binding.configBtnToReg.setOnClickListener(view -> {


            if (callMethod.ReadString("ActivationCode").equals("111111")) {
                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
            }else {
                LoginSetting();
            }
        });
    }
    public void LoginSetting() {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.loginconfig);
        EditText ed_password = dialog.findViewById(R.id.edloginconfig);
        MaterialButton btn_login = dialog.findViewById(R.id.btnloginconfig);


        btn_login.setOnClickListener(v -> {

            if (NumberFunctions.EnglishNumber(ed_password.getText().toString()).equals(callMethod.ReadString("ActivationCode"))) {

                Intent intent = new Intent(this, RegistrationActivity.class);
                startActivity(intent);
            }else {
                callMethod.showToast("رمز عبور صیحیح نیست");
            }


        });
        dialog.show();
    }

    @Override
    public void onRestart() {
        finish();
        startActivity(getIntent());
        super.onRestart();
    }

}

