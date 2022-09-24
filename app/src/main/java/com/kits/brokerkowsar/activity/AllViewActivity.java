package com.kits.brokerkowsar.activity;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkManager;

import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.Good_ProSearch_Adapter;
import com.kits.brokerkowsar.application.Action;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.application.Category;
import com.kits.brokerkowsar.application.Product;
import com.kits.brokerkowsar.application.ProductAdapter;
import com.kits.brokerkowsar.application.Replication;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.GoodGroup;
import com.kits.brokerkowsar.webService.APIClient;
import com.kits.brokerkowsar.webService.APIInterface;


import java.text.DecimalFormat;
import java.util.ArrayList;



public class AllViewActivity extends AppCompatActivity {


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


    ArrayList<Category> companies=new ArrayList<>();
    RecyclerView rc;
    Category cm ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_view);

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



    public void Config() {


        action = new Action(this);
        callMethod = new CallMethod(this);

        dbh = new DatabaseHelper(this, callMethod.ReadString("DatabaseName"));
        replication = new Replication(this);
        dbh.ClearSearchColumn();

        toolbar = findViewById(R.id.allview_toolbar);
        rc = findViewById(R.id.allview_rc);
        apiInterface = APIClient.getCleint(callMethod.ReadString("ServerURLUse")).create(APIInterface.class);

        setSupportActionBar(toolbar);
    }



    public void init() {


        companies = new ArrayList<>();



        ArrayList<GoodGroup> res = dbh.getAllGroups("0");

        for (GoodGroup goodGroups_parent : res) {
            ArrayList<Product> Product_child = new ArrayList<>();
            ArrayList<GoodGroup> res1 = dbh.getAllGroups(goodGroups_parent.getGoodGroupFieldValue("groupcode"));
            for (GoodGroup goodGroups_parent1 : res1) {
                Product_child.add(new Product(
                        goodGroups_parent1.getGoodGroupFieldValue("Name"),
                        Integer.parseInt(goodGroups_parent1.getGoodGroupFieldValue("groupcode")),
                        Integer.parseInt(goodGroups_parent1.getGoodGroupFieldValue("ChildNo"))));
            }
            cm = new Category(
                    goodGroups_parent.getGoodGroupFieldValue("Name"),
                    Product_child,
                    Integer.parseInt(goodGroups_parent.getGoodGroupFieldValue("groupcode")),
                    Integer.parseInt(goodGroups_parent.getGoodGroupFieldValue("ChildNo")));
            companies.add(cm);
        }



    ProductAdapter adapter = new ProductAdapter(companies, App.getContext());
    rc.setAdapter(adapter);
    rc.setLayoutManager(new LinearLayoutManager(this));



    }




}