package com.returno.tradeit.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.CategoryViewActivity;
import com.returno.tradeit.callbacks.FetchCallBacks;
import com.returno.tradeit.models.Item;
import com.returno.tradeit.utils.Commons;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.ItemUtils;
import com.returno.tradeit.utils.UploadUtils;

import java.util.List;
import java.util.Map;
import java.util.Random;

import timber.log.Timber;

public class FirebaseMessenger extends FirebaseMessagingService {
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData()==null) {
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
        }else{
            sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle(),remoteMessage.getData());
         if (!remoteMessage.getData().get(Constants.ITEM_CATEGORY).equals("requests")){
             fetchItem(remoteMessage.getData().get(Constants.ITEM_ID));
         }
        }

    }

    private void fetchItem(String itemId) {
new UploadUtils().fetchItemsById(itemId, new FetchCallBacks() {
    @Override
    public void fetchComplete(List<Item> itemList) {
        ItemUtils.storeFileFromOnlineUrl(getApplicationContext(), itemList, dirs -> {

        });
    }

    @Override
    public void fetchError(String message) {
        Timber.e(message);
    }
});
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

    private void sendNotification(String notificationBody, String notificationTitle, Map<String,String> data) {
        Intent intent=new Intent(getApplicationContext(), CategoryViewActivity.class);
        intent.putExtra(Constants.ITEM_CATEGORY,data.get(Constants.ITEM_CATEGORY));

        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(), Commons.getInstance().generateRequestCode(),intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap icon= BitmapFactory.decodeResource(getResources(),R.drawable.logo1);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.drawable.ic_notification)
                .setColor(getResources().getColor(R.color.login_header))
                .setLargeIcon(icon)
                .setContentText(notificationBody)
                .setContentInfo("The Market Goods. Click to view")
                .setContentIntent(pendingIntent);

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(new Random().nextInt(),builder.build());
        }
    }
}


