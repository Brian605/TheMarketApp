package com.returno.tradeit.local;

import android.content.Context;
import android.content.SharedPreferences;

import com.returno.tradeit.utils.Constants;

public class PreferenceManager {
    private static PreferenceManager manager;
    private Context context;

    public static PreferenceManager getInstance(Context context){

        if(manager==null){
            manager=new PreferenceManager();
        }
        manager.context=context;
        return manager;
    }

    public boolean isBoleanValueTrue(String key){
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCE,Context.MODE_PRIVATE);
        return preferences.getBoolean(key,true);
    }

    public void storeBooleanValue(String key,boolean value){
        SharedPreferences preferences =context.getSharedPreferences(Constants.SHARED_PREFERENCE,Context.MODE_PRIVATE);
SharedPreferences.Editor editor=preferences.edit();
editor.putBoolean(key,value);
editor.apply();
    }

}
