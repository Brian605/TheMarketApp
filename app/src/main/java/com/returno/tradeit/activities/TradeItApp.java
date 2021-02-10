package com.returno.tradeit.activities;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.returno.tradeit.BuildConfig;

import timber.log.Timber;

//import android.support.v7.app.AppCompatActivity;

public class TradeItApp extends Application {

    //public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
        Stetho.initializeWithDefaults(this);
        AndroidNetworking.initialize(this);

      //TradeItApp.context=getApplicationContext();
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }
    }

}
