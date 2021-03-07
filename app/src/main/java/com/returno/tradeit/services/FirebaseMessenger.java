package com.returno.tradeit.services;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returno.tradeit.R;

import java.util.Random;

public class FirebaseMessenger extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        sendNotification(remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());

        //TODO: Fetch the last notification data
    }
    private void sendNotification(String notificationBody, String notificationTitle) {

        Bitmap icon= BitmapFactory.decodeResource(getResources(),R.drawable.logo1);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.login_header))
                .setLargeIcon(icon)
                .setContentText(notificationBody)
                .setContentInfo("The Market Goods. Click to view");

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(new Random().nextInt(),builder.build());
        }
    }
}


