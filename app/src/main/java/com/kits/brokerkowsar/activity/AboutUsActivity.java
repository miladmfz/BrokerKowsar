package com.kits.brokerkowsar.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.brokerkowsar.R;
import com.kits.brokerkowsar.application.App;
import com.kits.brokerkowsar.model.NumberFunctions;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);

        TextView tv1 = findViewById(R.id.tv1);
        TextView tv2 = findViewById(R.id.tv2);
        TextView tv3 = findViewById(R.id.tv3);

        tv1.setText(NumberFunctions.PerisanNumber(tv1.getText().toString()));
        tv2.setText(NumberFunctions.PerisanNumber(tv2.getText().toString()));
        tv3.setText(NumberFunctions.PerisanNumber(tv3.getText().toString()));

    }


}
