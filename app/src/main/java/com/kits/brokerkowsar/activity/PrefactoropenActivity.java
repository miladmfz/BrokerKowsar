package com.kits.brokerkowsar.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.adapters.Prefactor_Header_Box_adapter;
import com.kits.brokerkowsar.adapters.Prefactor_Header_adapter;
import com.kits.brokerkowsar.application.CallMethod;
import com.kits.brokerkowsar.model.DatabaseHelper;
import com.kits.brokerkowsar.model.NumberFunctions;
import com.kits.brokerkowsar.model.PreFactor;

import java.util.ArrayList;
import java.util.Objects;


public class PrefactoropenActivity extends AppCompatActivity {


    DatabaseHelper dbh;
    private String fac;
    private Intent intent;
    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    CallMethod callMethod;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prefactoropen);


        final Dialog dialog1;
        dialog1 = new Dialog(this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog1.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog1.setContentView(R.layout.rep_prog);
        TextView repw = dialog1.findViewById(R.id.rep_prog_text);
        repw.setText("در حال خواندن اطلاعات");
        dialog1.show();
        intent();

        Handler handler = new Handler();
        handler.postDelayed(this::init, 100);

        handler.postDelayed(dialog1::dismiss, 1000);

    }

    //*********************************************

    public void init() {

        callMethod = new CallMethod(this);
        dbh = new DatabaseHelper(this, callMethod.ReadString("UseSQLiteURL"));
        Button addfactor = findViewById(R.id.PrefactoropenActivity_btn);
        Button refresh = findViewById(R.id.PrefactoropenActivity_refresh);
        Button dltempty = findViewById(R.id.PrefactoropenActivity_deleteempty);
        TextView tv = findViewById(R.id.PrefactoropenActivity_amount);
        recyclerView = findViewById(R.id.PrefactoropenActivity_recyclerView);


        ArrayList<PreFactor> preFactors;
        preFactors = dbh.getAllPrefactorHeaderopen();
        gridLayoutManager = new GridLayoutManager(PrefactoropenActivity.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        if (Integer.parseInt(fac) != 0) {
            Prefactor_Header_adapter adapter = new Prefactor_Header_adapter(preFactors, PrefactoropenActivity.this);
            recyclerView.setAdapter(adapter);
        } else {
            Prefactor_Header_Box_adapter adapter = new Prefactor_Header_Box_adapter(preFactors, PrefactoropenActivity.this);
            recyclerView.setAdapter(adapter);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        tv.setText((NumberFunctions.PerisanNumber("" + preFactors.size())));
        refresh.setOnClickListener(view -> {

            finish();
            startActivity(getIntent());

        });

        dltempty.setOnClickListener(view -> {
            dbh.DeleteEmptyPreFactor();
            finish();
            startActivity(getIntent());

        });


        addfactor.setOnClickListener(view -> {
            intent = new Intent(PrefactoropenActivity.this, CustomerActivity.class);
            intent.putExtra("edit", "0");
            intent.putExtra("factor_code", "0");
            intent.putExtra("id", "0");
            startActivity(intent);
        });


    }


    public void intent() {
        Bundle data = getIntent().getExtras();
        assert data != null;
        fac = data.getString("fac");
    }


}
