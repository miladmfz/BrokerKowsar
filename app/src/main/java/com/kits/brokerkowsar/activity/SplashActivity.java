package com.kits.brokerkowsar.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.WorkManager;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;

import java.io.File;

public class SplashActivity extends AppCompatActivity {


    Intent intent;
    CallMethod callMethod;
    Handler handler;
    final int PERMISSION_CODE = 1;
    DatabaseHelper dbh, dbhbase;
    final int PERMISSION_REQUEST_CODE = 1;
    WorkManager workManager;
        int i=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        Config();
        try {
            init();

        } catch (Exception e) {
            callMethod.ErrorLog(e.getMessage());
        }

    }


    //***************************************************************************************


    public void Config() {

    }


    @SuppressLint("SdCardPath")
    public void init() {
        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        if (callMethod.ReadBoolan("AutoReplication")) {
            try {
                workManager.cancelAllWork();
            } catch (Exception ignored) {
            }
        }

        if (callMethod.ReadString("ServerURLUse").equals("")) {
            callMethod.EditString("DatabaseName", "");
        }
        if (callMethod.firstStart()) {
            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditString("SellOff", "1");
            callMethod.EditString("Grid", "3");
            callMethod.EditString("Delay", "500");
            callMethod.EditString("ItemAmount", "200");
            callMethod.EditString("TitleSize", "18");
            callMethod.EditString("BodySize", "18");
            callMethod.EditString("PhoneNumber", "");
            callMethod.EditString("Theme", "Green");
            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);
            callMethod.EditBoolan("AutoReplication", false);
            callMethod.EditBoolan("SellPriceTypeDeactivate", true);

            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.brokerkowsar/databases/KowsarDb.sqlite");
            dbhbase.CreateActivationDb();


            try {
                dbh.SaveConfig("BrokerStack", "0");
                dbh.SaveConfig("MenuBroker", "");
            } catch (Exception e) {
                callMethod.ErrorLog(e.getMessage());
            }

        }

        callMethod.EditString("Filter", "0");
        callMethod.EditString("PreFactorCode", "0");
        callMethod.EditString("PreFactorGood", "0");
        callMethod.EditString("BasketItemView", "0");
        requestPermission();


    }

    private void Startapplication() {
        Log.e("test_","34");

        File databasedir = new File(getApplicationInfo().dataDir + "/databases/" + callMethod.ReadString("EnglishCompanyNameUse"));
        File temp = new File(databasedir, "/tempDb");
        if (!temp.exists()) {
            if (callMethod.ReadString("DatabaseName").equals("")) {
                handler = new Handler();
                handler.postDelayed(() -> {
                    intent = new Intent(this, ChoiceDatabaseActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
            } else {

                handler = new Handler();
                handler.postDelayed(() -> {


                    intent = new Intent(this, NavActivity.class);
                    startActivity(intent);
                    finish();
                }, 2000);
            }
        } else {
            deleteRecursive(databasedir);
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("DatabaseName", "");
            startActivity(getIntent());
            finish();
        }


    }

    private void requestPermission() {
        Log.e("test_","0");
        if (Build.VERSION_CODES.R <= Build.VERSION.SDK_INT) {
            Log.e("test_","1");

            if (!Environment.isExternalStorageManager()) {
                Log.e("test_","2");

                try {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else {
                Log.e("test_","3");

                runtimePermission();
            }

        } else {
            Log.e("test_","4");

            runtimePermission();
        }
    }

    private void runtimePermission() {
        try {

            Log.e("test_","31");

            if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {

                    Log.e("test_","33");
                    Startapplication();

                } else {
                    androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
                }
            } else {
                Log.e("test_","32");

                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
            }
        } catch (Exception e) {
            Log.e("test", e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (Environment.isExternalStorageManager()) {
                    runtimePermission();
                    callMethod.showToast("مجوز صادر شد");

                } else {
                    handler = new Handler();
                    handler.postDelayed(() -> {
                        intent = new Intent(this, SplashActivity.class);
                        finish();
                        startActivity(intent);
                    }, 2000);
                    callMethod.showToast("مجوز مربوطه را فعال نمایید");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod.showToast("permission granted");
            } else {
                callMethod.showToast("permission denied");
            }
            requestPermission();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
        callMethod.EditString("PersianCompanyNameUse", "");
        callMethod.EditString("EnglishCompanyNameUse", "");
        callMethod.EditString("ServerURLUse", "");
        //callMethod.EditString("DatabaseName", "");
        callMethod.EditString("DatabaseName", "");
        intent = new Intent(this, SplashActivity.class);
        finish();
        startActivity(intent);

    }
}
