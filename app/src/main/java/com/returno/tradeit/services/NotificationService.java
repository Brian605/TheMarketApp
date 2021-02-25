package com.returno.tradeit.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.returno.tradeit.R;
import com.returno.tradeit.activities.CategoryViewActivity;
import com.returno.tradeit.models.Notification;
import com.returno.tradeit.utils.Constants;
import com.returno.tradeit.utils.Urls;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends Service {
    @Override
    public void onCreate() {

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                AndroidNetworking.get(Urls.NOTIFICATION_URL)
                        .build()
                        .getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response1) {
                                try {
                                    for (int i=0;i<response1.length();i++) {
                                        JSONObject response = response1.getJSONObject(i);
                                        String itemId = response.getString(Constants.ITEM_ID);
                                        String price = response.getString(Constants.ITEM_PRICE);
                                        String branch = response.getString(Constants.ITEM_CATEGORY);
                                        String title = response.getString(Constants.ITEM_TITLE);
                                        String id = response.getString(Constants.NOTIFICATION_ID);

                                        Notification notification = new Notification(itemId, price, branch, title, itemId);
                                        onMessageReceived(notification);
                                        deleteNotification(itemId);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
            }
        });
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    private void deleteNotification(String itemId) {
        AndroidNetworking.post(Urls.NOTIFICATION_DELETE_URL)
                .addBodyParameter(Constants.ITEM_ID,itemId)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        if (response.equals("success")){

                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public void onMessageReceived(Notification remoteMessage) {
        String id=remoteMessage.getNotification_id();
        String body=remoteMessage.getTitle();
        String price=remoteMessage.getPrice();
        String category=remoteMessage.getBranch();
        String itemId=remoteMessage.getItemid();

        Intent intent=new Intent(this, CategoryViewActivity.class);
        intent.putExtra(Constants.ITEM_CATEGORY,category);
        intent.putExtra(Constants.ITEM_ID,itemId);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSound= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        RemoteViews remoteViews=new RemoteViews(getPackageName(),R.layout.push_notification_collapsed);
        remoteViews.setTextViewText(R.id.body,body);
        remoteViews.setImageViewUri(R.id.icon,Uri.parse(body.split("__")[1]));

        NotificationCompat.Builder builder= new NotificationCompat.Builder(this, getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.logo1)
                .setContentIntent(pendingIntent)
                .setContentInfo("The Market Goods. Click to view")
                .setColorized(true)
                .setChannelId("The Market")
                .setCustomContentView(remoteViews)
                .setCustomBigContentView(remoteViews)
                .setSound(defaultSound);

        NotificationManager manager=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.notify(0,builder.build());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
