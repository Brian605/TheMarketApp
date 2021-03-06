package com.returno.tradeit.activities;

import android.app.Application;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.google.firebase.messaging.FirebaseMessaging;
import com.returno.tradeit.BuildConfig;
import com.returno.tradeit.utils.Constants;

import timber.log.Timber;


public class TradeItApp extends Application {

    //public static Context context;
    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
        Stetho.initializeWithDefaults(this);
        AndroidNetworking.initialize(this);
        FirebaseMessaging.getInstance().subscribeToTopic(Constants.PRODUCTS_CHANNEL);
      //TradeItApp.context=getApplicationContext();
        if (BuildConfig.DEBUG){
            Timber.plant(new Timber.DebugTree());
        }

    }

}
