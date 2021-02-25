package com.returno.tradeit.activities;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;
import com.returno.tradeit.BuildConfig;
import com.returno.tradeit.services.NotificationReceiver;

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

        Intent intent=new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,2222,intent,0);
        AlarmManager manager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
        manager.setRepeating(AlarmManager.RTC,0, 30000,pendingIntent);
    }

}
