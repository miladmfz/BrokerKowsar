package com.kits.brokerkowsar.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.Result;
import com.kits.brokerkowsar.application.App;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    Intent intent;
    ZXingScannerView scannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scannerView = new ZXingScannerView(this);
        setContentView(scannerView);
    }

    @Override
    public void handleResult(Result result) {
        String scan_result = result.getText();

        intent = new Intent(this, SearchActivity.class);
        intent.putExtra("scan", scan_result);
        intent.putExtra("title", "جستجوی کالا");
        intent.putExtra("id", "0");


        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        scannerView.startCamera();


    }
}
