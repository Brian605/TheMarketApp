package com.returno.tradeit.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.callbacks.CompleteCallBacks;
import com.returno.tradeit.models.Notification;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class FirebaseUtils {

    private ValueEventListener listener;

    public boolean isCurrentUser(String userId) {
        return FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId);
    }

    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void postAPushNotification(@NotNull Notification notification, String itemId){
        Thread thread=new Thread(() -> {

            JSONObject payLoad=new JSONObject();
            JSONObject notificationObject=new JSONObject();
            JSONObject dataPart=new JSONObject();
            try {
                payLoad.put("to","/topics/"+Constants.PRODUCTS_CHANNEL);
                notificationObject.put("title",notification.getTitle());
                notificationObject.put("body",notification.getBody());
                dataPart.put(Constants.ITEM_CATEGORY,notification.getCategory());
                dataPart.put(Constants.ITEM_ID,itemId);
                payLoad.put("notification",notificationObject);
                payLoad.put("data",dataPart);

                getApiKey(new CompleteCallBacks(){
                    @Override
                    public void onComplete(Object... objects) {

                    }

                    @Override
                    public void onStringData(String data) {
                        AndroidNetworking.post(Urls.FCM_URL)
                                .setContentType("application/json; charset=utf-8")
                                .addJSONObjectBody(payLoad)
                                .addHeaders("Authorization",data)
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsJSONObject(new JSONObjectRequestListener() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Timber.e(response.toString());
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                        Timber.e(anError.toString());
                                    }
                                });
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        thread.start();


    }

    private void getApiKey(CompleteCallBacks authorization) {
        AndroidNetworking.get(Urls.API_KEY_URL)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        authorization.onStringData(response);
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    public static boolean isInternetAvailable() {
        try {
            Process process = Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int result = process.waitFor();
            return (result == 0);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public float getDateDifference(String secondDate){
        Calendar today=Calendar.getInstance();

        Calendar auctionDay=Calendar.getInstance();

        SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date date= null;
        try {
            date = format.parse(secondDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        auctionDay.setTime(Objects.requireNonNull(date));

        long diffMillis=today.getTimeInMillis()-auctionDay.getTimeInMillis();

        float f= (float) diffMillis/(1000*60*60*24);
        return (int)f;
    }

}