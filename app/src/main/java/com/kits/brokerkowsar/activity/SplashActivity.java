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

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {


    final int PERMISSION_CODE = 1;
    final int PERMISSION_REQUEST_CODE = 1;
    Intent intent;
    CallMethod callMethod;
    Handler handler;
    DatabaseHelper dbh, dbhbase;
    WorkManager workManager;
    int i = 0;

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

        if (callMethod.ReadString("ServerURLUse").equals("")) {
            callMethod.EditString("DatabaseName", "");
        }
        if (callMethod.firstStart()) {
            callMethod.EditBoolan("FirstStart", false);
            callMethod.EditString("SellOff", "1");
            callMethod.EditString("Grid", "3");
            callMethod.EditString("Delay", "1000");
            callMethod.EditString("TitleSize", "18");
            callMethod.EditString("BodySize", "18");
            callMethod.EditString("PhoneNumber", "");
            callMethod.EditString("Theme", "Green");
            callMethod.EditBoolan("RealAmount", false);
            callMethod.EditBoolan("ActiveStack", false);
            callMethod.EditBoolan("GoodAmount", false);
            callMethod.EditBoolan("AutoReplication", false);
            callMethod.EditBoolan("SellPriceTypeDeactivate", true);
            callMethod.EditBoolan("ShowDetail", true);
            callMethod.EditBoolan("LineView", false);


            callMethod.EditBoolan("keyboardRunnable", false);
            callMethod.EditBoolan("kowsarService", false);

            callMethod.EditBoolan("ShowCustomerCredit", true);

            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("SQLiteURLUse", "");
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ActivationCode", "");
            dbhbase = new DatabaseHelper(App.getContext(), "/data/data/com.kits.brokerkowsar/databases/KowsarDb.sqlite");
            dbhbase.CreateActivationDb();


        }

        if (callMethod.ReadBoolan("AutoReplication")) {
            try {
                workManager.cancelAllWork();
            } catch (Exception ignored) {
            }
        }
        callMethod.EditString("Filter", "0");
        callMethod.EditString("PreFactorCode", "0");
        callMethod.EditString("PreFactorGood", "0");
        callMethod.EditString("BasketItemView", "0");
        requestPermission();


    }

    private void Startapplication() {

        if (androidx.core.content.ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
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

                callMethod.EditString("ServerURLUse", "");
                callMethod.EditString("SQLiteURLUse", "");
                callMethod.EditString("PersianCompanyNameUse", "");
                callMethod.EditString("EnglishCompanyNameUse", "");
                callMethod.EditString("DatabaseName", "");
                callMethod.EditString("ActivationCode", "");
                startActivity(getIntent());
                finish();
            }

        } else {
            androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        }


    }


    // Check if Bluetooth permissions are granted

    private void runtimePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_CODE);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PERMISSION_CODE);
        } else {
            Startapplication();
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                    startActivityForResult(intent, 2296);
                } catch (Exception e) {
                    intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, 2296);
                }
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_CODE);
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_CODE);
            } else {
                Startapplication();
            }
        } else {
            runtimePermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2296 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                requestPermission();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);


        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callMethod.showToast("Permission granted");
            } else {
                callMethod.showToast("Permission denied");
            }
            requestPermission();
        } else {
            throw new IllegalStateException("Unexpected value: " + requestCode);
        }
    }





}
