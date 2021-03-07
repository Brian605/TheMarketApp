package com.returno.tradeit.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.returno.tradeit.utils.Constants;

public class PreferenceManager {
    private static PreferenceManager manager;
    public static PreferenceManager getInstance(){

        if(manager==null){
            manager=new PreferenceManager();
        }
        return manager;
    }

    public PreferenceManager(){

    }
    public boolean isBooleanValueTrue(String key, Context context){
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCE,Context.MODE_PRIVATE);
        return preferences.getBoolean(key,true);
    }

    public void storeBooleanValue(String key,boolean value,Context context){
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCE,Context.MODE_PRIVATE);
SharedPreferences.Editor editor=preferences.edit();
editor.putBoolean(key,value);
editor.apply();
    }

    public boolean isFirstTimeLaunch(Context context){
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCE,Context.MODE_PRIVATE);
        return preferences.getBoolean(Constants.FIRST_TIME_LAUNCH,true);
    }

}
