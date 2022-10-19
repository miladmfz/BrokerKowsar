package com.kits.brokerkowsar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.databinding.ActivityRegistrationBinding;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;

import java.io.File;
import java.util.Objects;


public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    CallMethod callMethod;
    Action action;
    Replication replication;
    UserInfo auser;
    boolean doubletouchdbanme = false;
    Intent intent;
    ActivityRegistrationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Config();
        try {
            init();
        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }


    }
//*******************************************************

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);
        action = new Action(this);

    }

    public void init() {

        auser = dbh.LoadPersonalInfo();

        binding.registrBroker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));
        binding.registrGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.registrDelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        binding.registrItemamount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("ItemAmount")));
        binding.registrTitlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        binding.registrBodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        binding.registrPhonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));
        binding.registrDbname.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PersianCompanyNameUse")));

        binding.registrDbname.setOnClickListener(v -> {
            if (doubletouchdbanme) {
                binding.registrLineManage.setVisibility(View.VISIBLE);
            }
            doubletouchdbanme = true;
            new Handler().postDelayed(() -> doubletouchdbanme = false, 1000);
        });

        binding.registrTotaldelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا اطلاعات نرم افزار به صورت کلی حذف شود؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {
                        File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                        deleteRecursive(databasedir);
                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();
        });

        binding.registrBasedelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا نیازمند بارگیری مجدد اطلاعات هستید؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {

                        File currentFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                        File newFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/tempDb");

                        if (rename(currentFile, newFile)) {
                            callMethod.EditString("PersianCompanyNameUse", "");
                            callMethod.EditString("EnglishCompanyNameUse", "");
                            callMethod.EditString("ServerURLUse", "");
                            callMethod.EditString("DatabaseName", "");
                            intent = new Intent(this, SplashActivity.class);
                            finish();
                            startActivity(intent);
                            Log.i("test", "Success");
                        }

                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();
        });

        binding.registrReplicationcolumn.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا تنظیمات پیش فرض مجددا گرفته شود ؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {
                        dbh.deleteColumn();
                        replication.BrokerStack();
                        dbh.DatabaseCreate();

                        dbh.ExecQuery("delete from customer");
                        dbh.ExecQuery("Update ReplicationTable Set LastRepLogCode = -1 Where ServerTable = 'Customer' ");

                        action.app_info();
                        replication.DoingReplicate();


                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();

        });


        binding.registrSelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.registrAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));


        binding.registrSelloff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Integer.parseInt(callMethod.ReadString("SellOff")) == 0) {
                callMethod.EditString("SellOff", "1");
                callMethod.showToast("بله");
            } else {
                callMethod.EditString("SellOff", "0");
                callMethod.showToast("خیر");
            }
        });


        binding.registrAutorep.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("AutoReplication")) {
                callMethod.EditBoolan("AutoReplication", false);
                callMethod.showToast("خیر");

            } else {
                callMethod.EditBoolan("AutoReplication", true);
                callMethod.showToast("بله");
            }
        });


        binding.registrBtn.setOnClickListener(view -> {
            callMethod.EditString("Grid", NumberFunctions.EnglishNumber(binding.registrGrid.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(binding.registrDelay.getText().toString()));
            callMethod.EditString("ItemAmount", NumberFunctions.EnglishNumber(binding.registrItemamount.getText().toString()));
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.registrTitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.registrBodysize.getText().toString()));
            callMethod.EditString("PhoneNumber", NumberFunctions.EnglishNumber(binding.registrPhonenumber.getText().toString()));
            Registration();


        });


    }


    public void Registration() {


        if (!auser.getBrokerCode().equals(binding.registrBroker.getText().toString())) {
            UserInfo UserInfoNew = new UserInfo();
            UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()));
            dbh.SavePersonalInfo(UserInfoNew);
            dbh.DatabaseCreate();
            dbh.ExecQuery("delete from customer");
            dbh.ExecQuery("Update ReplicationTable Set LastRepLogCode = -1 Where ServerTable = 'Customer' ");

            replication.BrokerStack();
            action.app_info();
            replication.DoingReplicate();

        } else {
            finish();
        }
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
        callMethod.EditString("PersianCompanyNameUse", "");
        callMethod.EditString("EnglishCompanyNameUse", "");
        callMethod.EditString("ServerURLUse", "");
        callMethod.EditString("DatabaseName", "");
        intent = new Intent(this, SplashActivity.class);
        finish();
        startActivity(intent);

    }

    private boolean rename(File from, File to) {
        return Objects.requireNonNull(from.getParentFile()).exists() && from.exists() && from.renameTo(to);
    }
}
