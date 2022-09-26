package com.kits.brokerkowsar.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.UserInfo;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    CallMethod callMethod;
    Action action;
    Replication replication;
    SwitchMaterial sm_regselloff;
    SwitchMaterial sm_autorep;
    Button btn_register;
    UserInfo auser;


    private EditText ed_reg_borker;
    private EditText ed_reg_grid;
    private EditText ed_reg_delay;
    private EditText ed_reg_itemamount;
    private EditText ed_reg_titlesize;
    private EditText ed_reg_bodysize;
    private EditText ed_reg_phonenumber;
    private TextView tv_dbname;
    private Button btn_totaldelete;
    private Button btn_basedelete;
    private Button btn_repcol;
    private LinearLayoutCompat ll_dbname;
    boolean doubletouchdbanme = false;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Config();
        try {
            init();
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }


    }
//*******************************************************

    public void Config() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);
        action = new Action(this);

        btn_register = findViewById(R.id.registr_btn);
        btn_totaldelete = findViewById(R.id.registr_totaldelete);
        btn_basedelete = findViewById(R.id.registr_basedelete);
        btn_repcol = findViewById(R.id.registr_replicationcolumn);

        ed_reg_borker = findViewById(R.id.registr_borker);
        ed_reg_grid = findViewById(R.id.registr_grid);
        ed_reg_delay = findViewById(R.id.registr_delay);
        ed_reg_titlesize = findViewById(R.id.registr_titlesize);
        ed_reg_bodysize = findViewById(R.id.registr_bodysize);
        ed_reg_itemamount = findViewById(R.id.registr_itemamount);
        ed_reg_phonenumber = findViewById(R.id.registr_phonenumber);

        tv_dbname = findViewById(R.id.registr_dbname);
        ll_dbname = findViewById(R.id.registr_line_manage);

        sm_regselloff = findViewById(R.id.registr_selloff);
        sm_autorep = findViewById(R.id.registr_autorep);
    }

    public void init() {

        auser = dbh.LoadPersonalInfo();

        ed_reg_borker.setText(NumberFunctions.PerisanNumber(auser.getBrokerCode()));
        ed_reg_grid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        ed_reg_delay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
        ed_reg_itemamount.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("ItemAmount")));
        ed_reg_titlesize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("TitleSize")));
        ed_reg_bodysize.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("BodySize")));
        ed_reg_phonenumber.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PhoneNumber")));
        tv_dbname.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PersianCompanyNameUse")));

        tv_dbname.setOnClickListener(v -> {
            if(doubletouchdbanme){
                ll_dbname.setVisibility(View.VISIBLE);
            }
            doubletouchdbanme = true;
            new Handler().postDelayed(() -> doubletouchdbanme = false, 1000);
        });

        btn_totaldelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا اطلاعات نرم افزار به صورت کلی حذف شود؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {
                        File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                        deleteRecursive(databasedir);
                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {})
                    .show();
        });

        btn_basedelete.setOnClickListener(v -> {
            new android.app.AlertDialog.Builder(this)
                    .setTitle("توجه")
                    .setMessage("آیا نیازمند بارگیری مجدد اطلاعات هستید؟")
                    .setPositiveButton("بله", (dialogInterface, i) -> {

                        File currentFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse")+"/KowsarDb.sqlite");
                        File newFile = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse")+"/tempDb");

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
                    .setNegativeButton("خیر", (dialogInterface, i) -> {})
                    .show();
        });

        btn_repcol.setOnClickListener(v -> {
            dbh.deleteColumn();
            replication.GoodTypeReplication();
            replication.MenuBroker();
            replication.BrokerStack();
            dbh.DatabaseCreate();
        });


        sm_regselloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        sm_autorep.setChecked(callMethod.ReadBoolan("AutoReplication"));


        sm_regselloff.setOnCheckedChangeListener((compoundButton, b) -> {
            if (Integer.parseInt(callMethod.ReadString("SellOff")) == 0) {
                callMethod.EditString("SellOff", "1");
                callMethod.showToast( "بله");
            } else {
                callMethod.EditString("SellOff", "0");
                callMethod.showToast( "خیر");
            }
        });



        sm_autorep.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("AutoReplication")) {
                callMethod.EditBoolan("AutoReplication", false);
                callMethod.showToast( "خیر");

            } else {
                callMethod.EditBoolan("AutoReplication", true);
                callMethod.showToast( "بله");
            }
        });


        btn_register.setOnClickListener(view -> {
            callMethod.EditString("Grid", NumberFunctions.EnglishNumber(ed_reg_grid.getText().toString()));
            callMethod.EditString("Delay", NumberFunctions.EnglishNumber(ed_reg_delay.getText().toString()));
            callMethod.EditString("ItemAmount", NumberFunctions.EnglishNumber(ed_reg_itemamount.getText().toString()));
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(ed_reg_titlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(ed_reg_bodysize.getText().toString()));
            callMethod.EditString("PhoneNumber", NumberFunctions.EnglishNumber(ed_reg_phonenumber.getText().toString()));
            Registration();

        });


    }


    public void Registration() {


        if (!auser.getBrokerCode().equals(ed_reg_borker.getText().toString())) {
            UserInfo UserInfoNew = new UserInfo();
            UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(ed_reg_borker.getText().toString()));
            dbh.SavePersonalInfo(UserInfoNew);

            dbh.ExecQuery("delete from customer");
            dbh.ExecQuery("Update ReplicationTable Set LastRepLogCode = -1 Where ServerTable = 'Customer' ");

            replication.BrokerStack();
            action.app_info();
            replication.DoingReplicate();

        }else {
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
