package com.kits.brokerkowsar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.databinding.ActivityRegistrationBinding;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.model.SellBroker;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Response;


public class RegistrationActivity extends AppCompatActivity {

    DatabaseHelper dbh;
    CallMethod callMethod;
    Action action;
    Replication replication;
    boolean doubletouchdbanme = false;
    Intent intent;
    ActivityRegistrationBinding binding;
    APIInterface apiInterface;
    ArrayList<String> SellBroker_Names = new ArrayList<>();
    ArrayList<SellBroker> SellBrokers = new ArrayList<>();
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
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        Call<RetrofitResponse> call1 = apiInterface.GetSellBroker("GetSellBroker");
        call1.enqueue(new Callback<RetrofitResponse>() {
            @Override
            public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    SellBrokers.clear();
                    SellBrokers = response.body().getSellBrokers();
                    SellBroker sellBroker= new SellBroker();
                    sellBroker.setBrokerCode("0");
                    sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                    SellBrokers.add(sellBroker);
                    for (SellBroker sb : SellBrokers) {
                        SellBroker_Names.add(sb.getBrokerNameWithoutType());
                    }
                    brokerViewConfig();
                }
            }

            @Override
            public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                SellBroker sellBroker= new SellBroker();
                sellBroker.setBrokerCode("0");
                sellBroker.setBrokerNameWithoutType("بازاریاب تعریف نشده");
                SellBrokers.add(sellBroker);
                brokerViewConfig();

            }
        });

    }

    public void brokerViewConfig() {
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SellBroker_Names);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.registrSpinnerbroker.setAdapter(spinner_adapter);
        int possellbroker=0;
        for (SellBroker sellBroker:SellBrokers){
            if (sellBroker.getBrokerCode().equals(dbh.ReadConfig("BrokerCode"))){
                possellbroker=SellBrokers.indexOf(sellBroker);
            }
        }

        binding.registrSpinnerbroker.setSelection(possellbroker);
        binding.registrSpinnerbroker.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                dbh.SaveConfig("BrokerCode",SellBrokers.get(position).getBrokerCode());
                binding.registrBroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }
    public void init() {


        binding.registrBroker.setText(NumberFunctions.PerisanNumber(dbh.ReadConfig("BrokerCode")));
        binding.registrGrid.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Grid")));
        binding.registrDelay.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("Delay")));
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
                        action.app_info();
                        replication.DoingReplicate();


                    })
                    .setNegativeButton("خیر", (dialogInterface, i) -> {
                    })
                    .show();

        });


        binding.registrSelloff.setChecked(Integer.parseInt(callMethod.ReadString("SellOff")) != 0);
        binding.registrAutorep.setChecked(callMethod.ReadBoolan("AutoReplication"));
        binding.registrCustomercredit.setChecked(callMethod.ReadBoolan("ShowCustomerCredit"));

        binding.registrCustomercredit.setOnCheckedChangeListener((compoundButton, b) -> {
            if (callMethod.ReadBoolan("ShowCustomerCredit")) {
                callMethod.EditBoolan("ShowCustomerCredit", false);
                callMethod.showToast("خیر");
            } else {
                callMethod.EditBoolan("ShowCustomerCredit", true);
                callMethod.showToast("بله");
            }
        });

        binding.registrBroker.setOnClickListener(v -> binding.registrBroker.selectAll());



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
            callMethod.EditString("TitleSize", NumberFunctions.EnglishNumber(binding.registrTitlesize.getText().toString()));
            callMethod.EditString("BodySize", NumberFunctions.EnglishNumber(binding.registrBodysize.getText().toString()));
            callMethod.EditString("PhoneNumber", NumberFunctions.EnglishNumber(binding.registrPhonenumber.getText().toString()));
            if(!dbh.ReadConfig("BrokerCode").equals(NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()))){
                Registration();
            }else {
                finish();
            }

        });


    }


    public void Registration() {



            UserInfo UserInfoNew = new UserInfo();
            UserInfoNew.setBrokerCode(NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()));
            callMethod.EditString("BrokerCode", NumberFunctions.EnglishNumber(binding.registrBroker.getText().toString()));
            dbh.SavePersonalInfo(UserInfoNew);
            dbh.DatabaseCreate();

            replication.BrokerStack();
            action.app_info();
            replication.DoingReplicate();

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
