package com.returno.tradeit.utils;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.models.Notification;

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

    public void postAPushNotification(Notification notification){
        String body=notification.getTitle()+" Ksh."+notification.getPrice();
        AndroidNetworking.post(Urls.NOTIFICATION_URL)
                .addBodyParameter(Constants.NOTIFICATION_BRANCH,Constants.PRODUCTS_CHANNEL)
                .addBodyParameter(Constants.ITEM_DESCRIPTION,body)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Timber.e(response);
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