package com.kits.brokerkowsar.application;

import android.app.Application;
import android.content.Context;

import com.kits.brokerkowsar.R;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class App extends Application {
    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/iransansmobile_medium.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public App() {
        instance = this;
    }

    public static Context getContext() {
        return instance;

    }


}
