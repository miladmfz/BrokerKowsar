package com.kits.brokerkowsar.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.google.android.material.button.MaterialButton;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.DownloadTask;
import com.kits.brokerkowsar.model.Activation;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
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
    int finalI;
    ArrayList<String> servers;
    ArrayList<String> sqlsurl;
    ArrayList<String> persiancompanynames;
    ArrayList<String> englishcompanynames;
    private ProgressBar pgsBar;
    private int i = 0;
    private TextView txtView;
    private Handler hdlr = new Handler();

    TextView tv_rep;
    TextView tv_step;
    Dialog dialog;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_database);
        init();

    }

    //*****************************************************************************************
    public void init12(String url,File databasedir, File databasefile) {

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
                .setOnPauseListener(() -> {

                })
                .setOnCancelListener(() -> {

                })
                .setOnProgressListener(progress -> {

                    tv_rep.setText("در حال بارگیری...");
                    tv_step.setText(NumberFunctions.PerisanNumber((((progress.currentBytes)*100)/progress.totalBytes)+"/100"));

                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        dialog.dismiss();
                        callMethod.EditString("UseSQLiteURL", "/data/data/com.kits.brokerkowsar/databases/" + callMethod.ReadString("EnglishCompanyNameUse") + "/KowsarDb.sqlite");
                        DatabaseHelper dbh = new DatabaseHelper(ChoiceDatabaseActivity.this, callMethod.ReadString("UseSQLiteURL"));
                        dbh.DatabaseCreate();
                        Intent intent = new Intent(ChoiceDatabaseActivity.this, SplashActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        Log.e("test",error.toString());
                        Log.e("test",error.isConnectionError()+"");
                        Log.e("test",error.isServerError()+"");
                    }

                });
    }

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
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.rep_prog);
        tv_rep = dialog.findViewById(R.id.rep_prog_text);
        tv_step = dialog.findViewById(R.id.rep_prog_step);
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
                finalI = i;
                Button.setOnClickListener(v -> {
                    int count=persiancompanynames.indexOf(string);

                    callMethod.EditString("PersianCompanyNameUse", persiancompanynames.get(count));
                    callMethod.EditString("EnglishCompanyNameUse", englishcompanynames.get(count));
                    callMethod.EditString("ServerURLUse", servers.get(count));

                    File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
                    File databasefile = new File(databasedir, "/KowsarDb.sqlite");//Create Output file in Main File

                    if (!databasefile.exists()) {
                        Log.e("test","true");
                        //new DownloadTask(this, sqlsurl.get(finalI));
                        init12(sqlsurl.get(finalI),databasedir,databasefile);
                    } else {
                        Log.e("test","false");
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
                        //Log.e("test",activation.getActivationCode());

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

                    Log.e("test",t.getMessage());
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