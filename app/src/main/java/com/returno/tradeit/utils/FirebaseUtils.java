package com.returno.tradeit.utils;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.models.Notification;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class FirebaseUtils {

    private ValueEventListener listener;

    public boolean isCurrentUser(String userId) {
        return FirebaseAuth.getInstance().getCurrentUser().getUid().equals(userId);
    }

    public String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void postAPushNotification(Notification notification){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_MESSAGES).push();
        listener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notification.setNotification_id(snapshot.getKey());
                reference.setValue(notification);
                reference.removeEventListener(listener);
       }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        reference.addListenerForSingleValueEvent(listener);
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