package com.returno.tradeit.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.callbacks.CompleteCallBacks;

public class UserUtils {
    private static UserUtils userUtils;

    private ValueEventListener imageListener,locationListener;

    public static UserUtils getInstance() {
        if (userUtils==null)userUtils=new UserUtils();

        return userUtils;
    }


    public void fetchUserImage(String userId, CompleteCallBacks completeCallBacks) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(userId);
        imageListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(Constants.USER_IMAGE)){
                   completeCallBacks.onComplete(dataSnapshot.child(Constants.USER_IMAGE).getValue().toString());
                   reference.removeEventListener(imageListener);
                   return;
                }
                completeCallBacks.onFailure("This user has no profile Image yet");
                reference.removeEventListener(imageListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
completeCallBacks.onFailure(error.getMessage());
            }
        };
        reference.addListenerForSingleValueEvent(imageListener);
    }

    public void fetchLocation(String userId, CompleteCallBacks completeCallBacks) {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_USERS_DIR).child(userId);
        locationListener=new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (dataSnapshot.hasChild(Constants.USER_LOCATION)){
                    completeCallBacks.onComplete(dataSnapshot.child(Constants.USER_LOCATION).getValue().toString());
                    reference.removeEventListener(locationListener);
                    return;

                }
completeCallBacks.onFailure("This user has no location set yet");
                reference.removeEventListener(locationListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
completeCallBacks.onFailure(databaseError.getMessage());
            }
        };
        reference.addListenerForSingleValueEvent(locationListener);
    }

}


