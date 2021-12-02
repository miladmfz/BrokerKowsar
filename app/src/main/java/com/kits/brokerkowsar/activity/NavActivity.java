package com.kits.brokerkowsar.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.kits.brokerkowsar.BuildConfig;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.model.NumberFunctions;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class NavActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private Action action;
    private boolean doubleBackToExitPressedOnce = false;
    private Intent intent;
    CallMethod callMethod;
    private final DecimalFormat decimalFormat = new DecimalFormat("0,000");
    private Replication replication;
    DatabaseHelper dbh;
    ArrayList<GoodGroup> menugrp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Handler handler = new Handler();
        handler.postDelayed(this::init, 100);
    }
//************************************************************

    public void init() {
        action = new Action(this);
        replication = new Replication(this);
        callMethod = new CallMethod(this);

        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        menugrp = dbh.getmenuGroups();

        Toolbar toolbar = findViewById(R.id.NavActivity_toolbar);

        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.NavActivity_nav);

        navigationView.setNavigationItemSelectedListener(this);
        TextView tv_versionname = findViewById(R.id.header_versionname);
        TextView tv_dbname = findViewById(R.id.header_dbname);
        Button btn_changedb = findViewById(R.id.header_changedb);
        tv_versionname.setText(BuildConfig.VERSION_NAME);
        tv_dbname.setText(callMethod.ReadString("PersianCompanyNameUse"));
        toolbar.setTitle(callMethod.ReadString("PersianCompanyNameUse"));

        btn_changedb.setOnClickListener(v -> {
            callMethod.EditString("PersianCompanyNameUse", "");
            callMethod.EditString("EnglishCompanyNameUse", "");
            callMethod.EditString("ServerURLUse", "");
            callMethod.EditString("UseSQLiteURL", "");
            intent = new Intent(this, SplashActivity.class);
            finish();
            startActivity(intent);
        });

        noti();

        TextView customer      = findViewById(R.id.MainActivity_customer);
        TextView sumfac        = findViewById(R.id.MainActivity_sum_factor);
        TextView customer_code = findViewById(R.id.MainActivity_customer_code);

        Button create_factor   = findViewById(R.id.mainactivity_create_factor);
        Button good_search     = findViewById(R.id.mainactivity_good_search);
        Button open_factor     = findViewById(R.id.mainactivity_open_factor);
        Button all_factor      = findViewById(R.id.mainactivity_all_factor);
        Button test            = findViewById(R.id.mainactivity_test);

        navigationView.getMenu().clear();

        for (GoodGroup goodGroup : menugrp) {
            navigationView.getMenu().add(goodGroup.getGoodGroupFieldValue("Name")).setIcon(R.drawable.grpmenu).setOnMenuItemClickListener(item -> {
                intent = new Intent(NavActivity.this, SearchActivity.class);
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
            test.setVisibility(View.VISIBLE);
            dbh.SaveConfig("BrokerStack","1");
        }


        create_factor.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("id", "0");
            intent.putExtra("factor_code", "0");
            startActivity(intent);
        });


        good_search.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        });

        open_factor.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, PrefactoropenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        });

        all_factor.setOnClickListener(view -> {
            intent = new Intent(NavActivity.this, PrefactorActivity.class);
            startActivity(intent);
        });


    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.NavActivity_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (doubleBackToExitPressedOnce) {
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
            intent = new Intent(NavActivity.this, SearchActivity.class);
            intent.putExtra("scan", "");
            intent.putExtra("id", "0");
            intent.putExtra("title", "جستجوی کالا");
            startActivity(intent);
        } else if (id == R.id.aboutus) {
            intent = new Intent(NavActivity.this, AboutusActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_buy_history) {
            intent = new Intent(NavActivity.this, PrefactorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_open_fac) {
            intent = new Intent(NavActivity.this, PrefactoropenActivity.class);
            intent.putExtra("fac", "1");
            startActivity(intent);
        } else if (id == R.id.nav_rep) {

            replication.BrokerStack();
            action.app_info();
            replication.replicate_all();

        } else if (id == R.id.nav_buy) {
            if (Integer.parseInt(callMethod.ReadString("PreFactorCode")) > 0) {
                intent = new Intent(NavActivity.this, BuyActivity.class);
                intent.putExtra("PreFac", callMethod.ReadString("PreFactorCode"));
                intent.putExtra("showflag", "2");
                startActivity(intent);
            } else {
                Toast.makeText(this, "سبد خرید خالی است.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_search_date) {
            intent = new Intent(NavActivity.this, Search_date_detailActivity.class);
            intent.putExtra("date", "7");
            startActivity(intent);
        } else if (id == R.id.nav_cfg) {
            intent = new Intent(NavActivity.this, ConfigActivity.class);
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
                intent = new Intent(NavActivity.this, BuyActivity.class);
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
    public void test_fun(View v) {    }

}

