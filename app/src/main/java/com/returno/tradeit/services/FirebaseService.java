package com.returno.tradeit.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.CategoryViewActivity;

import timber.log.Timber;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onCreate() {
         super.onCreate();
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notiTitle,notiBody;

        if (remoteMessage.getNotification()!=null){

            Timber.e(remoteMessage.getNotification().getBody());
            notiBody=remoteMessage.getNotification().getBody();
            notiTitle=remoteMessage.getNotification().getTitle();

            //if ("products".equals(notiTitle)){
            assert notiBody != null;
            sendNotification(notiBody,notiTitle);
           // }else
               // if ("chats".equals(notiTitle)){
                   // insertMessage(notiBody,notiTitle);
               // }
        }


    }

    /*
    private void insertMessage(String notiBody, String notiTitle) {
        DatabaseManager manager=new DatabaseManager(getApplicationContext()).open();
        String senderId=notiBody.split("___")[0];
        String date=notiBody.split("___")[1];
        String message=notiBody.split("___")[2];
        String receiverId=notiBody.split("___")[3];
        String messageId=notiBody.split("___")[4];

        if (new FirebaseUtils().isCurrentUser(senderId)||new FirebaseUtils().isCurrentUser(receiverId)){
        Message message1=new Message(messageId,senderId,receiverId,message,date);
        manager.insertMessage(message1);
        manager.close();
        listener.onReceived();}
    }
    */

//83175737246
    //794491
    private void sendNotification(String notiBody, String notiTitle) {
        String category=notiBody.split("___")[0];
        if (notiBody.split("___")[1].equals("undefined") || notiBody.split("___")[2].equals("undefined")){
            notiBody="New Item Posted. Click to see";
        }
        else {
            notiBody = notiBody.split("___")[1] + " " + notiBody.split("___")[2];
        }
        Intent intent=new Intent(this, CategoryViewActivity.class);
        intent.putExtra("category",category);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.push_notification);
        remoteViews.setTextViewText(R.id.body,notiBody);

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(pendingIntent)
                .setContentTitle(notiTitle)
                .setContentInfo("TradeIt Goods. Click to view")
                .setColorized(true)
                .setChannelId("TradeIt")
                .setContent(remoteViews)
                .setSound(defaultSound);

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0,builder.build());
        }
    }
}
