package com.returno.tradeit.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.returno.tradeit.R;
import com.returno.tradeit.adapters.NotificationsAdapter;
import com.returno.tradeit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentNotice extends Fragment {

private List<String> notificationList;
private NotificationsAdapter adapter;

    public FragmentNotice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notice, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        notificationList=new ArrayList<>();

       adapter=new NotificationsAdapter(notificationList);
       recyclerView.setAdapter(adapter);
       fetchNotifications();

        return view;
    }

    public void fetchNotifications(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_NOTIFICATIONS);
        Query query=reference.orderByKey();
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (dataSnapshot.hasChild(Constants.KEY_MESSAGE)){
                        notificationList.add(dataSnapshot.child(Constants.KEY_MESSAGE).getValue().toString());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }
}
