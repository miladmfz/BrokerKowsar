package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.material.button.MaterialButton;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.DownloadTask;
import com.kits.brokerkowsar.model.Activation;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIClient_kowsar;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ChoiceDatabaseActivity extends AppCompatActivity {
    APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
    APIInterface apiInterface1;
    CallMethod callMethod;
    Activation activation;
    boolean getdb=true;

    ArrayList<String> servers;
    ArrayList<String> sqlsurl;
    ArrayList<String> persiancompanynames;
    ArrayList<String> englishcompanynames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_database);
        init();

    }

    //*****************************************************************************************


    @SuppressLint("SdCardPath")
    public void init() {
        callMethod = new CallMethod(this);
        LinearLayoutCompat active_line = findViewById(R.id.activition_line);
        TextView active_edt = findViewById(R.id.activition_edittext);
        Button active_btn = findViewById(R.id.activition_btn);
        servers = callMethod.getArrayList("ServerURLs");
        sqlsurl = callMethod.getArrayList("SQLiteURLs");
        persiancompanynames = callMethod.getArrayList("PersianCompanyNames");
        englishcompanynames = callMethod.getArrayList("EnglishCompanyNames");


        int i = 0;
        //if (companynames.size()>0){
        if (!callMethod.ReadString("PersianCompanyNames").equals("[]")) {

            for (String string : callMethod.getArrayList("PersianCompanyNames")) {
                MaterialButton Button = new MaterialButton(this);
                Button.setText(string);
                Button.setBackgroundResource(R.color.white);
                LinearLayoutCompat.LayoutParams layoutParams = new LinearLayoutCompat.LayoutParams(
                        LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(30, 10, 30, 10);

                Button.setTextSize(30);
                Button.setGravity(Gravity.CENTER);
                int finalI = i;
                Button.setOnClickListener(v -> {
                    callMethod.EditString("PersianCompanyNameUse", persiancompanynames.get(finalI));
                    callMethod.EditString("EnglishCompanyNameUse", englishcompanynames.get(finalI));
                    callMethod.EditString("ServerURLUse", servers.get(finalI));


                    File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                    File databasefile = new File(databasedir, "/KowsarDb.sqlite");//Create Output file in Main File

                    if (!databasefile.exists()) {
                        new DownloadTask(this, sqlsurl.get(finalI));
                    } else {

                        callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.brokerkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                        Intent intent = new Intent(this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    //APIClient.getCleint(callMethod.ReadString("ServerURLUse"));

                });
                active_line.addView(Button, layoutParams);
                i++;

            }
        }


        active_btn.setOnClickListener(v -> {

                Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", active_edt.getText().toString());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        if (callMethod.ReadString("PersianCompanyNames").equals("")) {
                            servers = new ArrayList<>();
                            sqlsurl = new ArrayList<>();
                            persiancompanynames = new ArrayList<>();
                            englishcompanynames = new ArrayList<>();
                        }else {
                            for (String string : englishcompanynames) {
                                if (string.equals(activation.getEnglishCompanyName())) {
                                    Toast.makeText(ChoiceDatabaseActivity.this, "این کد ثبت شده است", Toast.LENGTH_SHORT).show();
                                    getdb = false;
                                    break;
                                }
                            }
                        }


                        if(getdb){
                            saveactivation();

                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {

                }
            });

        });


    }



    public void saveactivation() {

        servers.add(activation.getServerURL());
        sqlsurl.add(activation.getSQLiteURL());
        persiancompanynames.add(activation.getPersianCompanyName());
        englishcompanynames.add(activation.getEnglishCompanyName());
        callMethod.saveArrayList(servers, "ServerURLs");
        callMethod.saveArrayList(sqlsurl, "SQLiteURLs");
        callMethod.saveArrayList(persiancompanynames, "PersianCompanyNames");
        callMethod.saveArrayList(englishcompanynames, "EnglishCompanyNames");
        finish();
        startActivity(getIntent());

    }


}