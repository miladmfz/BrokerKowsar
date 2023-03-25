package com.kits.brokerkowsar.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.kits.brokerkowsar.databinding.ActivityAboutusBinding;
import com.kits.brokerkowsar.model.NumberFunctions;


public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityAboutusBinding binding = ActivityAboutusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setPersianText(binding.tv1);
        setPersianText(binding.tv2);
        setPersianText(binding.tv3);
    }

    private void setPersianText(TextView textView) {
        textView.setText(NumberFunctions.PerisanNumber(textView.getText().toString()));
    }


}
