package com.kits.brokerkowsar.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.navigation.NavigationView;
import com.kits.brokerkowsar.BuildConfig;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.application.WManager;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.Location;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.RetrofitResponse;
import com.kits.brokerkowsar.model.UserInfo;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;


import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    APIInterface apiInterface;

    private Action action;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private Replication replication;
    DatabaseHelper dbh;
    ArrayList<GoodGroup> menugrp;
    Toolbar toolbar;
    NavigationView navigationView;
    TextView tv_versionname;
    TextView tv_dbname;
    TextView tv_brokercode;
    Button btn_changedb;
    TextView customer;
    TextView sumfac;
    TextView customer_code;
    Button create_factor;
    Button good_search;
    Button open_factor;
    Button all_factor;

    Button btn_test;
    TextView tv_test;

    private static final int REQUEST_LOCATION = 1;

    LocationManager locationManager;
    String latitude, longitude;
    WorkManager workManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);

        Config();
        try {
            Handler handler = new Handler();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                handler.postDelayed(this::init, 100);
            }
        }catch (Exception e){
            callMethod.ErrorLog(e.getMessage());
        }

    }

    //************************************************************


    public void Gpslocation() {

        int LOCATION_REFRESH_TIME =2000; // 15 seconds to update
        int LOCATION_REFRESH_DISTANCE = 3; // 500 meters to update

        LocationListener mLocationListener = location -> {

            tv_test.setText("lati="+location.getLatitude()+"\nlong="+location.getLongitude());
            Call<RetrofitResponse> call1 = apiInterface.Location(
                    "Location",
                    String.valueOf(location.getLongitude()),
                    String.valueOf(location.getLatitude())
            );
            call1.enqueue(new Callback<RetrofitResponse>() {
                @Override
                public void onResponse(@NotNull Call<RetrofitResponse> call, @NotNull Response<RetrofitResponse> response) {

                }

                @Override
                public void onFailure(@NotNull Call<RetrofitResponse> call, @NotNull Throwable t) {
                   // callMethod.ErrorLog(t.getMessage());
                }
            });

        };


        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        }else {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, mLocationListener);
        }
    }

    public void Config() {


        action = new Action(this);
        callMethod = new CallMethod(this);

        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);
        dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.NavActivity_toolbar);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.NavActivity_nav);
        navigationView.setNavigationItemSelectedListener(this);
        View hView = navigationView.getHeaderView(0);
        tv_versionname = hView.findViewById(R.id.header_versionname);
        tv_dbname = hView.findViewById(R.id.header_dbname);
        tv_brokercode = hView.findViewById(R.id.header_brokercode);
        btn_changedb = hView.findViewById(R.id.header_changedb);
        btn_changedb = hView.findViewById(R.id.header_changedb);

        customer= findViewById(R.id.MainActivity_customer);
        sumfac= findViewById(R.id.MainActivity_sum_factor);
        customer_code= findViewById(R.id.MainActivity_customer_code);
        create_factor= findViewById(R.id.mainactivity_create_factor);
        good_search= findViewById(R.id.mainactivity_good_search);
        open_factor= findViewById(R.id.mainactivity_open_factor);
        all_factor= findViewById(R.id.mainactivity_all_factor);
        btn_test= findViewById(R.id.mainactivity_test_btn);
        tv_test= findViewById(R.id.mainactivity_test_tv);


    }



    public void test_fun(View v) {

        replication.BrokerStack();
        replication.MenuBroker();
        replication.GoodTypeReplication();




    }

    @SuppressLint("SetTextI18n")
    public void CheckConfig() {

        UserInfo auser = dbh.LoadPersonalInfo();



            if (Integer.parseInt(auser.getBrokerCode())!=0) {

                tv_brokercode.setText(" کد بازاریاب : " + NumberFunctions.PerisanNumber(auser.getBrokerCode()));
                if (dbh.ReadConfig("BrokerStack").equals("0")) {
                    if(callMethod.ReadBoolan("AutoReplication")) {
                        workManager.cancelAllWork();
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("انباری تعریف نشده")
                            .setMessage("آیا مایل به تغییر کد بازاریاب می باشید ؟")
                            .setPositiveButton("بله", (dialogInterface, i) -> {
                                intent = new Intent(this, ConfigActivity.class);
                                callMethod.showToast("کد بازاریاب را وارد کنید");
                                startActivity(intent);
                            })
                            .setNegativeButton("خیر", (dialogInterface, i) -> {
                            })
                            .show();
                }
            }else {

                tv_brokercode.setText("کد بازاریاب ندارد");
                new AlertDialog.Builder(this)
                        .setTitle("عدم وجود کد بازاریاب")
                        .setMessage("آیا مایل به تعریف کد بازاریاب می باشید ؟")
                        .setPositiveButton("بله", (dialogInterface, i) -> {
                            intent = new Intent(this, ConfigActivity.class);
                            callMethod.showToast( "کد بازاریاب را وارد کنید");
                            startActivity(intent);
                        })
                        .setNegativeButton("خیر", (dialogInterface, i) -> {
                            callMethod.showToast("برای ادامه کار به کد بازاریاب نیازمندیم");
                        })
                        .show();
            }



    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"SetTextI18n", "MissingPermission"})
    public void init() {
        noti();
        CheckConfig();

        if(callMethod.ReadBoolan("AutoReplication")) {

            Constraints conster = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(WManager.class, 15, TimeUnit.MINUTES)
                    .setConstraints(conster)
                    .build();
            workManager = WorkManager.getInstance(NavActivity.this);
            workManager.enqueue(req);
        }


        tv_versionname.setText(BuildConfig.VERSION_NAME);
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));
        menugrp = dbh.getmenuGroups();


        navigationView.getMenu().clear();

        for (GoodGroup goodGroup : menugrp) {
            navigationView.getMenu().add(goodGroup.getGoodGroupFieldValue("Name")).setIcon(R.drawable.grpmenu).setOnMenuItemClickListener(item -> {
                intent = new Intent(this, SearchActivity.class);
                intent.putExtra("scan", "");
                intent.putExtra("id", goodGroup.getGoodGroupFieldValue("GroupCode"));
                intent.putExtra("title", goodGroup.getGoodGroupFieldValue("Name"));
                startActivity(intent);
                return false;
            });
        }

        navigationView.inflateMenu(R.menu.activity_navigation_drawer);


        if (callMethod.ReadString("PreFactorCode").equals("0")) {
            customer.setText("فاکتوری انتخاب نشده");
            sumfac.setText("0");
        } else {
            customer.setText(dbh.getFactorCustomer(callMethod.ReadString("PreFactorCode")));
            sumfac.setText(NumberFunctions.PerisanNumber(decimalFormat.format(Integer.parseInt(dbh.getFactorSum(callMethod.ReadString("PreFactorCode"))))));
            customer_code.setText(NumberFunctions.PerisanNumber(callMethod.ReadString("PreFactorCode")));
        }

        if (callMethod.ReadString("PersianCompanyNameUse").equals("اصلی")) {
            btn_test.setVisibility(View.VISIBLE);
            tv_test.setVisibility(View.VISIBLE);
            //dbh.SaveConfig("BrokerStack","1");
        }


        create_factor.setOnClickListener(view -> {
            intent = new Intent(this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("id", "0");
            intent.putExtra("factor_code", "0");
            startActivity(intent);
        });


        good_search.setOnClickListener(view -> {
            intent = new Intent(this, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        });

        open_factor.setOnClickListener(view -> {
            intent = new Intent(this, PrefactoropenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        });

        all_factor.setOnClickListener(view -> {
            intent = new Intent(this, PrefactorActivity.class);
            startActivity(intent);
        });
        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            //callMethod.EditString("DatabaseName", "");
            callMethod.EditString("DatabaseName", "");
            intent = new Intent(this, SplashActivity.class);
            finish();
            startActivity(intent);
        });

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
            workManager.cancelAllWork();
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "برای خروج مجددا کلیک کنید", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        final int id = item.getItemId();


        if (id == R.id.nav_search) {
            intent = new Intent(this, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        } else if (id == R.id.aboutus) {
            intent = new Intent(this, AboutusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_buy_history) {
            intent = new Intent(this, PrefactorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_open_fac) {
            intent = new Intent(this, PrefactoropenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        } else if (id == R.id.nav_rep) {

            replication.BrokerStack();
            action.app_info();
            try {
                workManager.cancelAllWork();
                replication.DoingReplicate();
            }catch (Exception e){
                replication.DoingReplicate();

            }

        } else if (id == R.id.nav_buy) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) > 0) {
                intent = new Intent(this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);
            } else {
                Toast.makeText(this, "سبد خرید خالی است.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_search_date) {
            intent = new Intent(this, Search_date_detailActivity.class);
            intent.putExtra("date", "7");
            startActivity(intent);
        } else if (id == R.id.nav_cfg) {
            intent = new Intent(this, ConfigActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.bag_shop) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) != 0) {
                intent = new Intent(this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);

            } else {
                Toast.makeText(this, "فاکتوری انتخاب نشده است", Toast.LENGTH_SHORT).show();

            }


            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void noti() {  }

    @Override
    protected void onStop() {
        if(callMethod.ReadBoolan("AutoReplication")) {
        workManager.cancelAllWork();
        }
        super.onStop();
    }



}

