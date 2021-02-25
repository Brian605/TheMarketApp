package com.returno.tradeit.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        Intent intent1=new Intent(context,NotificationService.class);
        context.startService(intent1);
       // PendingIntent pendingIntent=PendingIntent.getService(context,2222,intent1,PendingIntent.FLAG_CANCEL_CURRENT);

        // an Intent broadcast.
    }
}