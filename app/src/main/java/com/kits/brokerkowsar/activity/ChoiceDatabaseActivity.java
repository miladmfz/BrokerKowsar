package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.android.material.button.MaterialButton;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.Activation;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.webService.APIClient_kowsar;
import com.kits.brokerkowsar.webService.APIInterface;

import java.io.File;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ChoiceDatabaseActivity extends AppCompatActivity {

    APIInterface apiInterface = APIClient_kowsar.getCleint_log().create(APIInterface.class);
    CallMethod callMethod;
    Activation activation;
    boolean getdb=true;
    DatabaseHelper dbh;
    DatabaseHelper dbhbase;
    TextView tv_rep;
    TextView tv_step;
    Dialog dialog;
    ArrayList<Activation> activations;
    LinearLayoutCompat active_line;
    TextView active_edt;
    Button active_btn;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_database);
        Config();

        try {
            init();
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }

    }

    //*****************************************************************************************
    public void Config() {

        callMethod = new CallMethod(this);
        dialog = new Dialog(this);
        activation=new Activation();
        dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.brokerkowsar/databases/KowsarDb.sqlite");
        active_line = findViewById(R.id.activition_line);
        active_edt = findViewById(R.id.activition_edittext);
        active_btn = findViewById(R.id.activition_btn);
        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);


    }

    @SuppressLint("SdCardPath")
    public void init() {
        activations=dbhbase.getActivation();

        for(Activation singleactive:activations){
            CreateView(singleactive);
        }


        active_btn.setOnClickListener(v -> {

            Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", active_edt.getText().toString());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());
                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());
                }
            });

        });


    }

    public void DownloadRequesttest(String url,String dbname,File databasedir, File databasefile) {

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        // Setting timeout globally for the download network requests:
        PRDownloaderConfig config1 = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config1);

        PRDownloader.download(url, databasedir.getPath(), databasefile.getName())
                .build()
                .setOnStartOrResumeListener(() -> {
                    dialog.show();
                    dialog.setCancelable(false);
                })
                .setOnPauseListener(() -> {})
                .setOnCancelListener(() -> {})
                .setOnProgressListener(progress -> {
                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setText(NumberFunctions.PerisanNumber((((progress.currentBytes)*100)/progress.totalBytes)+"/100"));
                })
                .start(new OnDownloadListener() {
                    @SuppressLint("SdCardPath")
                    @Override
                    public void onDownloadComplete() {
                        dialog.dismiss();
                        callMethod.EditString("DatabaseName", "/data/data/com.kits.brokerkowsar/databases/" + dbname + "/KowsarDb.sqlite");
                        dbh = new DatabaseHelper(App.getContext(), callMethod.ReadString("DatabaseName"));
                        dbh.DatabaseCreate();
                        intent = new Intent(App.getContext(), SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        callMethod.ErrorLog(error.toString());
                    }

                });
    }
    @SuppressLint({"SetTextI18n", "SdCardPath"})
    public void CreateView(Activation singleactive){

        String serverip=singleactive.getServerURL().substring(singleactive.getServerURL().indexOf("//")+2,singleactive.getServerURL().indexOf("/login")-6);


        LinearLayoutCompat ll_main = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_tv = new LinearLayoutCompat(this);
        LinearLayoutCompat ll_btn = new LinearLayoutCompat(this);
        TextView tv_PersianCompanyName = new TextView(this);
        TextView tv_EnglishCompanyName = new TextView(this);
        TextView tv_ServerURL = new TextView(this);
        MaterialButton btn_login = new MaterialButton(this);
        MaterialButton btn_update = new MaterialButton(this);
        MaterialButton btn_gap = new MaterialButton(this);

        LinearLayoutCompat.LayoutParams margin_10 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);
        LinearLayoutCompat.LayoutParams margin_5 = new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT);

        ll_main.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        ll_tv.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT,(float) 0.3));
        ll_btn.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT,(float) 0.7));
        tv_PersianCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_EnglishCompanyName.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        tv_ServerURL.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.MATCH_PARENT));
        btn_login.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT,(float) 0.3));
        btn_update.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT,(float) 0.3));
        btn_gap.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT,LinearLayoutCompat.LayoutParams.WRAP_CONTENT,(float) 0.4));

        tv_PersianCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_EnglishCompanyName.setTextColor(getResources().getColor(R.color.grey_800));
        tv_ServerURL.setTextColor(getResources().getColor(R.color.grey_800));


        ll_main.setOrientation(LinearLayoutCompat.HORIZONTAL);
        ll_tv.setOrientation(LinearLayoutCompat.VERTICAL);
        ll_btn.setOrientation(LinearLayoutCompat.HORIZONTAL);


        margin_10.setMargins(10, 10, 10, 10);
        margin_5.setMargins(5, 5, 5, 5);


        ll_main.setBackgroundResource(R.color.grey_20);
        tv_PersianCompanyName.setBackgroundResource(R.color.grey_20);
        tv_EnglishCompanyName.setBackgroundResource(R.color.grey_20);
        tv_ServerURL.setBackgroundResource(R.color.grey_20);
        btn_login.setBackgroundResource(R.color.white);
        btn_update.setBackgroundResource(R.color.white);
        btn_gap.setBackgroundResource(R.color.white);



        tv_PersianCompanyName.setTextSize(26);
        tv_EnglishCompanyName.setTextSize(16);
        tv_ServerURL.setTextSize(16);
        btn_login.setTextSize(18);
        btn_update.setTextSize(18);

        ll_main.setPadding(20,20,20,20);
        ll_btn.setPadding(0,20,0,0);

        ll_main.setWeightSum(1);
        ll_btn.setWeightSum(1);

        btn_gap.setVisibility(View.INVISIBLE);


        tv_PersianCompanyName.setText(NumberFunctions.PerisanNumber(singleactive.getPersianCompanyName()));
        tv_EnglishCompanyName.setText("نام پوشه عکس : "+singleactive.getEnglishCompanyName());
        tv_ServerURL.setText( "آدرس سرور : "+serverip);
        btn_login.setText("ورود");
        btn_update.setText("اصلاح");


        btn_login.setOnClickListener(v -> {

            File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + singleactive.getEnglishCompanyName());
            File databasefile = new File(databasedir, "/KowsarDb.sqlite");
            callMethod.EditString("PersianCompanyNameUse", singleactive.getPersianCompanyName());
            callMethod.EditString("EnglishCompanyNameUse",singleactive.getEnglishCompanyName());
            callMethod.EditString("ServerURLUse", singleactive.getServerURL());
            if (!databasefile.exists()) {
                DownloadRequesttest(singleactive.getSQLiteURL(),singleactive.getEnglishCompanyName(),databasedir,databasefile);
            } else {
                callMethod.EditString("DatabaseName", "/data/data/com.kits.brokerkowsar/databases/" + singleactive.getEnglishCompanyName() + "/KowsarDb.sqlite");
                intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            }
        });



        btn_update.setOnClickListener(v -> {

            Call<RetrofitResponse> call1 = apiInterface.Activation("ActivationCode", singleactive.getActivationCode());
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NonNull Call<RetrofitResponse> call, @NonNull retrofit2.Response<RetrofitResponse> response) {
                    if (response.isSuccessful()) {
                        assert response.body() != null;
                        activation = response.body().getActivations().get(0);
                        dbhbase.InsertActivation(activation);
                        finish();
                        startActivity(getIntent());

                    }
                }
                @Override
                public void onFailure(@NonNull Call<RetrofitResponse> call, @NonNull Throwable t) {
                    callMethod.ErrorLog(t.getMessage());
                }
            });
        });



        ll_btn.addView(btn_login);
        ll_btn.addView(btn_gap);
        ll_btn.addView(btn_update);

        ll_tv.addView(tv_PersianCompanyName);
        ll_tv.addView(tv_EnglishCompanyName);
        ll_tv.addView(tv_ServerURL);

        ll_tv.addView(ll_btn,margin_5);

        ll_main.addView(ll_tv);


        active_line.addView(ll_main,margin_10);
    }



}